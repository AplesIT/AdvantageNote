 
package com.production.advangenote.async;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import com.production.advangenote.MainActivity;
import com.production.advangenote.R;
import com.production.advangenote.async.bus.NavigationUpdatedEvent;
import com.production.advangenote.models.NavigationItem;
import com.production.advangenote.models.adapters.NavDrawerAdapter;
import com.production.advangenote.models.mics.DynamicNavigationLookupTable;
import com.production.advangenote.models.views.NonScrollableListView;
import com.production.advangenote.utils.Navigation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static com.production.advangenote.utils.Constants.PREFS_NAME;
import static com.production.advangenote.utils.ConstantsBase.PREF_DYNAMIC_MENU;
import static com.production.advangenote.utils.ConstantsBase.PREF_SHOW_UNCATEGORIZED;


public class MainMenuTask extends AsyncTask<Void, Void, List<NavigationItem>> {

  private final WeakReference<Fragment> mFragmentWeakReference;
  private final MainActivity mainActivity;
  @BindView(R.id.drawer_nav_list)
  NonScrollableListView mDrawerList;
  @BindView(R.id.drawer_tag_list)
  NonScrollableListView mDrawerCategoriesList;


  public MainMenuTask(Fragment mFragment) {
    mFragmentWeakReference = new WeakReference<>(mFragment);
    this.mainActivity = (MainActivity) mFragment.getActivity();
    ButterKnife.bind(this, mFragment.getView());
  }

  @Override
  protected List<NavigationItem> doInBackground(Void... params) {
    return buildMainMenu();
  }

  @Override
  protected void onPostExecute(final List<NavigationItem> items) {
    if (isAlive()) {
      mDrawerList.setAdapter(new NavDrawerAdapter(mainActivity, items));
      mDrawerList.setOnItemClickListener((arg0, arg1, position, arg3) -> {
        String navigation = mFragmentWeakReference.get().getResources().getStringArray(R.array
            .navigation_list_codes)[items.get(position).getArrayIndex()];
        updateNavigation(position, navigation);
      });
      mDrawerList.justifyListViewHeightBasedOnChildren();
    }
  }

  private void updateNavigation(int position, String navigation) {
    if (mainActivity.updateNavigation(navigation)) {
      mDrawerList.setItemChecked(position, true);
      if (mDrawerCategoriesList != null) {
        mDrawerCategoriesList.setItemChecked(0, false); // Called to force redraw
      }
      mainActivity.getIntent().setAction(Intent.ACTION_MAIN);
      EventBus.getDefault()
          .post(new NavigationUpdatedEvent(mDrawerList.getItemAtPosition(position)));
    }
  }

  private boolean isAlive() {
    return mFragmentWeakReference.get() != null
        && mFragmentWeakReference.get().isAdded()
        && mFragmentWeakReference.get().getActivity() != null
        && !mFragmentWeakReference.get().getActivity().isFinishing();
  }

  private List<NavigationItem> buildMainMenu() {
    if (!isAlive()) {
      return new ArrayList<>();
    }

    String[] mNavigationArray = mainActivity.getResources().getStringArray(R.array.navigation_list);
    TypedArray mNavigationIconsArray = mainActivity.getResources()
        .obtainTypedArray(R.array.navigation_list_icons);
    TypedArray mNavigationIconsSelectedArray = mainActivity.getResources().obtainTypedArray(R.array
        .navigation_list_icons_selected);

    final List<NavigationItem> items = new ArrayList<>();
    for (int i = 0; i < mNavigationArray.length; i++) {
      if (!checkSkippableItem(i)) {
        NavigationItem item = new NavigationItem(i, mNavigationArray[i],
            mNavigationIconsArray.getResourceId(i,
                0), mNavigationIconsSelectedArray.getResourceId(i, 0));
        items.add(item);
      }
    }
    return items;
  }

  private boolean checkSkippableItem(int i) {
    boolean skippable = false;
    SharedPreferences prefs = mainActivity
        .getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS);
    boolean dynamicMenu = prefs.getBoolean(PREF_DYNAMIC_MENU, true);
    DynamicNavigationLookupTable dynamicNavigationLookupTable = null;
    if (dynamicMenu) {
      dynamicNavigationLookupTable = DynamicNavigationLookupTable.getInstance();
    }
    switch (i) {
      case Navigation.REMINDERS:
        if (dynamicMenu && dynamicNavigationLookupTable.getReminders() == 0) {
          skippable = true;
        }
        break;
      case Navigation.UNCATEGORIZED:
        boolean showUncategorized = prefs.getBoolean(PREF_SHOW_UNCATEGORIZED, false);
        if (!showUncategorized || (dynamicMenu
            && dynamicNavigationLookupTable.getUncategorized() == 0)) {
          skippable = true;
        }
        break;
      case Navigation.ARCHIVE:
        if (dynamicMenu && dynamicNavigationLookupTable.getArchived() == 0) {
          skippable = true;
        }
        break;
      case Navigation.TRASH:
        if (dynamicMenu && dynamicNavigationLookupTable.getTrashed() == 0) {
          skippable = true;
        }
        break;
    }
    return skippable;
  }

}
