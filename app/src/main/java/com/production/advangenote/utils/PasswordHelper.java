
package com.production.advangenote.utils;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.R;
import com.production.advangenote.async.bus.PasswordRemovedEvent;
import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.models.PasswordValidator;



import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.MODE_MULTI_PROCESS;
import static com.production.advangenote.utils.Constants.PREFS_NAME;
import static com.production.advangenote.utils.ConstantsBase.PREF_PASSWORD;
import static com.production.advangenote.utils.ConstantsBase.PREF_PASSWORD_ANSWER;
import static com.production.advangenote.utils.ConstantsBase.PREF_PASSWORD_QUESTION;


public class PasswordHelper {


  public static void requestPassword(final Activity mActivity,
      final PasswordValidator mPasswordValidator) {
    LayoutInflater inflater = mActivity.getLayoutInflater();
    final View v = inflater.inflate(R.layout.password_request_dialog_layout, null);
    final EditText passwordEditText = v.findViewById(R.id.password_request);

    MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
        .autoDismiss(false)
        .title(R.string.insert_security_password)
        .customView(v, false)
        .positiveText(R.string.ok)
        .positiveColorRes(R.color.colorPrimary)
        .onPositive((dialog12, which) -> {
          // When positive button is pressed password correctness is checked
          String oldPassword = mActivity.getSharedPreferences(PREFS_NAME, MODE_MULTI_PROCESS)
              .getString(PREF_PASSWORD, "");
          String password = passwordEditText.getText().toString();
          // The check is done on password's hash stored in preferences
          boolean result = Security.md5(password).equals(oldPassword);

          // In case password is ok dialog is dismissed and result sent to callback
          if (result) {
            KeyboardUtils.hideKeyboard(passwordEditText);
            dialog12.dismiss();
            mPasswordValidator.onPasswordValidated(PasswordValidator.Result.SUCCEED);
            // If password is wrong the auth flow is not interrupted and simply a message is shown
          } else {
            passwordEditText.setError(mActivity.getString(R.string.wrong_password));
          }
        })
        .neutralText(mActivity.getResources().getString(R.string.password_forgot))
        .onNeutral((dialog13, which) -> {
          PasswordHelper.resetPassword(mActivity);
          mPasswordValidator.onPasswordValidated(PasswordValidator.Result.RESTORE);
          dialog13.dismiss();
        })
        .build();

    dialog.setOnCancelListener(dialog1 -> {
      KeyboardUtils.hideKeyboard(passwordEditText);
      dialog1.dismiss();
      mPasswordValidator.onPasswordValidated(PasswordValidator.Result.FAIL);
    });

    passwordEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
      if (actionId == EditorInfo.IME_ACTION_DONE) {
        dialog.getActionButton(DialogAction.POSITIVE).callOnClick();
        return true;
      }
      return false;
    });

    dialog.show();

    new Handler().postDelayed(() -> KeyboardUtils.showKeyboard(passwordEditText), 100);
  }


  public static void resetPassword(final Activity mActivity) {
    View layout = mActivity.getLayoutInflater()
        .inflate(R.layout.password_reset_dialog_layout, null);
    final EditText answerEditText = layout.findViewById(R.id.reset_password_answer);

    MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
        .title(AdvantageNotes.getSharedPreferences().getString(PREF_PASSWORD_QUESTION, ""))
        .customView(layout, false)
        .autoDismiss(false)
        .contentColorRes(R.color.text_color)
        .positiveText(R.string.ok)
        .onPositive((dialogElement, which) -> {
          // When positive button is pressed answer correctness is checked
          String oldAnswer = AdvantageNotes.getSharedPreferences().getString(PREF_PASSWORD_ANSWER, "");
          String answer1 = answerEditText.getText().toString();
          // The check is done on password's hash stored in preferences
          boolean result = Security.md5(answer1).equals(oldAnswer);
          if (result) {
            dialogElement.dismiss();
            removePassword();
          } else {
            answerEditText.setError(mActivity.getString(R.string.wrong_answer));
          }
        }).build();
    dialog.show();

    answerEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
      if (actionId == EditorInfo.IME_ACTION_DONE) {
        dialog.getActionButton(DialogAction.POSITIVE).callOnClick();
        return true;
      }
      return false;
    });

    new Handler().postDelayed(() -> KeyboardUtils.showKeyboard(answerEditText), 100);
  }


  public static void removePassword() {
    Observable
        .from(DAOSQL.getInstance().getNotesWithLock(true))
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(note -> {
          note.setLocked(false);
          DAOSQL.getInstance().updateNote(note, false);
        })
        .doOnCompleted(() -> {
          AdvantageNotes.getSharedPreferences().edit()
              .remove(PREF_PASSWORD)
              .remove(PREF_PASSWORD_QUESTION)
              .remove(PREF_PASSWORD_ANSWER)
              .remove("settings_password_access")
              .apply();
          EventBus.getDefault().post(new PasswordRemovedEvent());
        })
        .subscribe();
  }
}
