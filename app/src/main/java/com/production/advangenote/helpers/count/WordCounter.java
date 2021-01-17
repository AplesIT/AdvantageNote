package com.production.advangenote.helpers.count;

import com.production.advangenote.models.Note;

import java.util.regex.Pattern;

/**
 * @author vietnh
 * @name WordCounter
 * @date 10/1/20
 **/
public interface WordCounter {

    int countWords(Note note);

    int countChars(Note note);

    default String sanitizeTextForWordsAndCharsCount(Note note, String field) {
        if (note.isChecklist()) {
            String regex =
                    "(" + Pattern.quote(it.feio.android.checklistview.interfaces.Constants.CHECKED_SYM) + "|"
                            + Pattern.quote(it.feio.android.checklistview.interfaces.Constants.UNCHECKED_SYM)
                            + ")";
            field = field.replaceAll(regex, "");
        }
        return field;
    }
}
