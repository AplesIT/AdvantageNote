package com.production.advangenote.helpers;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.R;
import com.production.advangenote.async.DataBackupIntentService;
import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.exception.checked.BackupAttachmentException;
import com.production.advangenote.helpers.notifications.NotificationsHelper;
import com.production.advangenote.models.Attachment;
import com.production.advangenote.models.Note;
import com.production.advangenote.utils.StorageHelper;
import com.production.advangenote.utils.TextHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;

import static com.production.advangenote.utils.ConstantsBase.DATABASE_NAME;

/**
 * @author vietnh
 * @name BackupHelper
 * @date 10/1/20
 **/
public  class BackupHelper {
    private static Logger logger = LoggerFactory.getLogger("BackupHelper.class");
    private BackupHelper() {
        // hides public constructor
    }

    public static void exportNotes(File backupDir) {
        for (Note note : DAOSQL.getInstance(true).getAllNotes(false)) {
            exportNote(backupDir, note);
        }
    }

    public static void exportNote(File backupDir, Note note) {
        File noteFile = getBackupNoteFile(backupDir, note);
        try {
            FileUtils.write(noteFile, note.toJSON());
        } catch (IOException e) {
            logger.info(String.format("Error on note %s backup: %s",  note.get_id(), e.getMessage()));
        }
    }

    @NonNull
    public static File getBackupNoteFile(File backupDir, Note note) {
        return new File(backupDir, note.get_id() + ".json");
    }

    public static boolean exportAttachments(File backupDir) {
        return exportAttachments(backupDir, null);
    }

    /**
     * Export attachments to backup folder notifying for each attachment copied
     *
     * @return True if success, false otherwise
     */
    public static boolean exportAttachments(File backupDir, NotificationsHelper notificationsHelper) {
        File destinationattachmentsDir = new File(backupDir,
                StorageHelper.getAttachmentDir().getName());
        ArrayList<Attachment> list = DAOSQL.getInstance().getAllAttachments();
        exportAttachments(notificationsHelper, destinationattachmentsDir, list, null);
        return true;
    }

    public static boolean exportAttachments(NotificationsHelper notificationsHelper,
                                            File destinationattachmentsDir, List<Attachment> list, List<Attachment> listOld) {
        boolean result = true;
        listOld = listOld == null ? Collections.emptyList() : listOld;
        int exported = 0;
        int failed = 0;
        String failedString = "";

        for (Attachment attachment : list) {
            try {
                exportAttachment(destinationattachmentsDir, attachment);
                ++exported;
            } catch (BackupAttachmentException e) {
                ++failed;
                result = false;
                failedString = " (" + failed + " " + AdvantageNotes.getAppContext().getString(R.string.failed) + ")";
            }

            notifyAttachmentBackup(notificationsHelper, list, exported, failedString);
        }

        Observable.from(listOld)
                .filter(attachment -> !list.contains(attachment))
                .forEach(attachment -> StorageHelper.delete(AdvantageNotes.getAppContext(), new File
                        (destinationattachmentsDir.getAbsolutePath(),
                                attachment.getUri().getLastPathSegment()).getAbsolutePath()));

        return result;
    }

    private static void notifyAttachmentBackup(NotificationsHelper notificationsHelper,
                                               List<Attachment> list, int exported, String failedString) {
        if (notificationsHelper != null) {
            String notificationMessage =
                    TextHelper.capitalize(AdvantageNotes.getAppContext().getString(R.string.attachment)) + " "
                            + exported + "/" + list.size() + failedString;
            notificationsHelper.updateMessage(notificationMessage);
        }
    }

    private static void exportAttachment(File attachmentsDestination, Attachment attachment)
            throws BackupAttachmentException {
        try {
            StorageHelper.copyToBackupDir(attachmentsDestination,
                    FilenameUtils.getName(attachment.getUriPath()),
                    AdvantageNotes.getAppContext().getContentResolver().openInputStream(attachment.getUri()));
        } catch (Exception e) {
            logger.error("Error during attachment backup: " + attachment.getUriPath(), e);
            throw new BackupAttachmentException(e);
        }
    }

    public static List<Note> importNotes(File backupDir) {
        List<Note> notes = new ArrayList<>();
        for (File file : FileUtils
                .listFiles(backupDir, new RegexFileFilter("\\d{13}.json"), TrueFileFilter.INSTANCE)) {
            notes.add(importNote(file));
        }
        return notes;
    }

    public static Note importNote(File file) {
        Note note = getImportNote(file);
        if (note.getCategory() != null) {
            DAOSQL.getInstance().updateCategory(note.getCategory());
        }
        DAOSQL.getInstance().updateNote(note, false);
        return note;
    }

    public static Note getImportNote(File file) {
        try {
            Note note = new Note();
            String jsonString = FileUtils.readFileToString(file);
            if (!TextUtils.isEmpty(jsonString)) {
                note.buildFromJson(jsonString);
                note.setAttachmentsListOld(DAOSQL.getInstance().getNoteAttachments(note));
            }
            return note;
        } catch (IOException e) {
            logger.error("Error parsing note json");
            return new Note();
        }
    }

    /**
     * Import attachments from backup folder notifying for each imported item
     */
    public static boolean importAttachments(File backupDir, NotificationsHelper notificationsHelper) {
        AtomicBoolean result = new AtomicBoolean(true);
        File attachmentsDir = StorageHelper.getAttachmentDir();
        File backupAttachmentsDir = new File(backupDir, attachmentsDir.getName());
        if (!backupAttachmentsDir.exists()) {
            return false;
        }

        AtomicInteger imported = new AtomicInteger();
        ArrayList<Attachment> attachments = DAOSQL.getInstance().getAllAttachments();
        rx.Observable.from(attachments)
                .forEach(attachment -> {
                    try {
                        importAttachment(backupAttachmentsDir, attachmentsDir, attachment);
                        if (notificationsHelper != null) {
                            notificationsHelper.updateMessage(TextHelper.capitalize(AdvantageNotes.getAppContext().getString(R.string.attachment)) + " "
                                    + imported.incrementAndGet() + "/" + attachments.size());
                        }
                    } catch (BackupAttachmentException e) {
                        result.set(false);
                    }
                });
        return result.get();
    }

    static void importAttachment(File backupAttachmentsDir, File attachmentsDir,
                                 Attachment attachment)
            throws BackupAttachmentException {
        try {
            File attachmentFile = new File(backupAttachmentsDir.getAbsolutePath(),
                    attachment.getUri().getLastPathSegment());
            FileUtils.copyFileToDirectory(attachmentFile, attachmentsDir, true);
        } catch (Exception e) {
            logger.error("Error importing the attachment " + attachment.getUri().getPath(), e);
            throw new BackupAttachmentException(e);
        }
    }

    /**
     * Starts backup service
     *
     * @param backupFolderName subfolder of the app's external sd folder where notes will be stored
     */
    public static void startBackupService(String backupFolderName) {
        Intent service = new Intent(AdvantageNotes.getAppContext(), DataBackupIntentService.class);
        service.setAction(DataBackupIntentService.ACTION_DATA_EXPORT);
        service.putExtra(DataBackupIntentService.INTENT_BACKUP_NAME, backupFolderName);
        AdvantageNotes.getAppContext().startService(service);
    }

    /**
     * Exports settings if required
     */
    public static boolean exportSettings(File backupDir) {
        File preferences = StorageHelper.getSharedPreferencesFile(AdvantageNotes.getAppContext());
        return (StorageHelper.copyFile(preferences, new File(backupDir, preferences.getName())));
    }

    /**
     * Imports settings
     */
    public static boolean importSettings(File backupDir) {
        File preferences = StorageHelper.getSharedPreferencesFile(AdvantageNotes.getAppContext());
        File preferenceBackup = new File(backupDir, preferences.getName());
        return (StorageHelper.copyFile(preferenceBackup, preferences));
    }

    public static boolean deleteNoteBackup(File backupDir, Note note) {
        File noteFile = getBackupNoteFile(backupDir, note);
        boolean result = noteFile.delete();
        File attachmentBackup = new File(backupDir, StorageHelper.getAttachmentDir().getName());
        for (Attachment attachment : note.getAttachmentsList()) {
            result =
                    result && new File(attachmentBackup, FilenameUtils.getName(attachment.getUri().getPath()))
                            .delete();
        }
        return result;
    }


    public static void deleteNote(File file) {
        try {
            Note note = new Note();
            note.buildFromJson(FileUtils.readFileToString(file));
            DAOSQL.getInstance().deleteNote(note);
        } catch (IOException e) {
            logger.error("Error parsing note json");
        }
    }

    /**
     * Import database from backup folder. Used ONLY to restore legacy backup
     *
     * @deprecated {@link BackupHelper#importNotes(File)}
     */
    @Deprecated
    public static boolean importDB(Context context, File backupDir) {
        File database = context.getDatabasePath(DATABASE_NAME);
        if (database.exists() && database.delete()) {
            return (StorageHelper.copyFile(new File(backupDir, DATABASE_NAME), database));
        }
        return false;
    }

    public static List<LinkedList<DiffMatchPatch.Diff>> integrityCheck(File backupDir) {
        List<LinkedList<DiffMatchPatch.Diff>> errors = new ArrayList<>();
        for (Note note : DAOSQL.getInstance(true).getAllNotes(false)) {
            File noteFile = getBackupNoteFile(backupDir, note);
            try {
                String noteString = note.toJSON();
                String noteFileString = FileUtils.readFileToString(noteFile);
                if (noteString.equals(noteFileString)) {
                    File backupAttachmentsDir = new File(backupDir,
                            StorageHelper.getAttachmentDir().getName());
                    for (Attachment attachment : note.getAttachmentsList()) {
                        if (!new File(backupAttachmentsDir, FilenameUtils.getName(attachment.getUriPath()))
                                .exists()) {
                            addIntegrityCheckError(errors, new FileNotFoundException("Attachment " + attachment
                                    .getUriPath() + " missing"));
                        }
                    }
                } else {
                    errors.add(new DiffMatchPatch().diffMain(noteString, noteFileString));
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                addIntegrityCheckError(errors, e);
            }
        }
        return errors;
    }

    private static void addIntegrityCheckError(List<LinkedList<DiffMatchPatch.Diff>> errors,
                                               IOException e) {
        LinkedList<DiffMatchPatch.Diff> l = new LinkedList<>();
        l.add(new DiffMatchPatch.Diff(DiffMatchPatch.Operation.DELETE, e.getMessage()));
        errors.add(l);
    }

}

