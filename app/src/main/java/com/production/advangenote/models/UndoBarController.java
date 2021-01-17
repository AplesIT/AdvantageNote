package com.production.advangenote.models;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;

import com.production.advangenote.R;

import java.util.Locale;

import it.feio.android.checklistview.utils.AlphaManager;

import static androidx.core.view.ViewCompat.animate;

/**
 * @author vietnh
 * @name UndoBarController
 * @date 10/10/20
 **/
public class  UndoBarController {

    private View mBarView;
    private TextView mMessageView;
    private ViewPropertyAnimatorCompat mBarAnimator;

    private UndoListener mUndoListener;

    private Parcelable mUndoToken;
    private CharSequence mUndoMessage;
    private Button mButtonView;
    private boolean isVisible;


    public interface UndoListener {

        void onUndo(Parcelable token);
    }


    public UndoBarController(View undoBarView, UndoListener undoListener) {
        mBarView = undoBarView;
        mBarAnimator = animate(mBarView);
        mUndoListener = undoListener;

        mMessageView = mBarView.findViewById(R.id.undobar_message);

        mButtonView = mBarView.findViewById(R.id.undobar_button);
        mButtonView.setText(mButtonView.getText().toString().toUpperCase(Locale.getDefault()));
        mButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideUndoBar(false);
                mUndoListener.onUndo(mUndoToken);
            }
        });

        hideUndoBar(false);
    }


    public void showUndoBar(boolean immediate, CharSequence message, Parcelable undoToken) {
        mUndoToken = undoToken;
        mUndoMessage = message;
        mMessageView.setText(mUndoMessage);
        mBarView.setVisibility(View.VISIBLE);
        if (immediate) {
            AlphaManager.setAlpha(mBarView, 1);
        } else {
            mBarAnimator.cancel();
            mBarAnimator
                    .alpha(1)
                    .setDuration(
                            mBarView.getResources()
                                    .getInteger(android.R.integer.config_shortAnimTime))
                    .setListener(null);
        }
        isVisible = true;
    }


    public void hideUndoBar(boolean immediate) {
//        mHideHandler.removeCallbacks(mHideRunnable);
        if (immediate) {
            mBarView.setVisibility(View.GONE);
            AlphaManager.setAlpha(mBarView, 0);
            mUndoMessage = null;
            mUndoToken = null;

        } else {
            mBarAnimator.cancel();
            mBarAnimator
                    .alpha(0)
                    .setDuration(mBarView.getResources()
                            .getInteger(android.R.integer.config_shortAnimTime))
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(View view) {
                            super.onAnimationEnd(view);
                            mBarView.setVisibility(View.GONE);
                            mUndoMessage = null;
                            mUndoToken = null;
                        }
                    });
        }
        isVisible = false;
    }


    public void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence("undo_message", mUndoMessage);
        outState.putParcelable("undo_token", mUndoToken);
    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mUndoMessage = savedInstanceState.getCharSequence("undo_message");
            mUndoToken = savedInstanceState.getParcelable("undo_token");

            if (mUndoToken != null || !TextUtils.isEmpty(mUndoMessage)) {
                showUndoBar(true, mUndoMessage, mUndoToken);
            }
        }
    }


    public boolean isVisible() {
        return isVisible;
    }


}
