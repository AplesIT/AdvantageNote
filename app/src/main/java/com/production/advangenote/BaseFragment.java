package com.production.advangenote;

import android.os.SystemClock;

import androidx.fragment.app.Fragment;

/**
 * @author vietnh
 * @name BaseFragment
 * @date 11/10/21
 **/
public class BaseFragment extends Fragment {


    private static final long OPTIONS_ITEM_CLICK_DELAY_TIME = 1000;
    private long mLastClickTime;

    @Override
    public void onStart() {
        super.onStart();
        ((AdvantageNotes) getActivity().getApplication()).getAnalyticsHelper()
                .trackScreenView(getClass().getName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected boolean isOptionsItemFastClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < OPTIONS_ITEM_CLICK_DELAY_TIME) {
            return true;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return false;
    }
}