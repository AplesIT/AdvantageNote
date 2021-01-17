/*
 * Copyright (C) 2013-2020 Federico Iosue (federico@iosue.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.production.advangenote;

import android.animation.ValueAnimator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.production.advangenote.async.CategoryMenuTask;
import com.production.advangenote.async.MainMenuTask;
import com.production.advangenote.async.bus.CategoriesUpdatedEvent;
import com.production.advangenote.async.bus.DynamicNavigationReadyEvent;
import com.production.advangenote.async.bus.NavigationUpdatedEvent;
import com.production.advangenote.async.bus.NavigationUpdatedNavDrawerClosedEvent;
import com.production.advangenote.async.bus.NotesLoadedEvent;
import com.production.advangenote.async.bus.NotesUpdatedEvent;
import com.production.advangenote.async.bus.SwitchFragmentEvent;

import com.production.advangenote.models.Category;
import com.production.advangenote.models.NavigationItem;
import com.production.advangenote.utils.Display;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;

import static com.production.advangenote.async.bus.SwitchFragmentEvent.Direction.CHILDREN;


public class NavigationDrawerFragment extends Fragment {
  private final static Logger logger = LoggerFactory.getLogger("NavigationDrawerFragment.class");

  static final int BURGER = 0;
  static final int ARROW = 1;

  ActionBarDrawerToggle mDrawerToggle;
  DrawerLayout mDrawerLayout;
  private MainActivity mActivity;
  private boolean alreadyInitialized;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }


  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }


  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
  }


  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mActivity = (MainActivity) getActivity();
    init();
  }


  private MainActivity getMainActivity() {
    return (MainActivity) getActivity();
  }


  public void onEventMainThread(DynamicNavigationReadyEvent event) {
    if (alreadyInitialized) {
      alreadyInitialized = false;
    } else {
      refreshMenus();
    }
  }


  public void onEvent(CategoriesUpdatedEvent event) {
    refreshMenus();
  }


  public void onEventAsync(NotesUpdatedEvent event) {
    alreadyInitialized = false;
  }


  public void onEvent(NotesLoadedEvent event) {
    if (mDrawerLayout != null) {
      if (!isDoublePanelActive()) {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
      }
    }
    if (getMainActivity().getSupportFragmentManager().getBackStackEntryCount() == 0) {
      init();
    }
    refreshMenus();
    alreadyInitialized = true;
  }


  public void onEvent(SwitchFragmentEvent event) {
    if (CHILDREN.equals(event.getDirection())) {
      animateBurger(ARROW);
    } else {
      animateBurger(BURGER);
    }
  }


  public void onEvent(NavigationUpdatedEvent navigationUpdatedEvent) {
    if (navigationUpdatedEvent.navigationItem.getClass().isAssignableFrom(NavigationItem.class)) {
      mActivity.getSupportActionBar()
          .setTitle(((NavigationItem) navigationUpdatedEvent.navigationItem).getText());
    } else {
      mActivity.getSupportActionBar()
          .setTitle(((Category) navigationUpdatedEvent.navigationItem).getName());
    }
    if (mDrawerLayout != null) {
      if (!isDoublePanelActive()) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
      }
      new Handler()
          .postDelayed(() -> EventBus.getDefault().post(new NavigationUpdatedNavDrawerClosedEvent
              (navigationUpdatedEvent.navigationItem)), 400);
    }
  }


  public void init() {
    logger.info("Started navigation drawer initialization");

    mDrawerLayout = mActivity.findViewById(R.id.drawer_layout);
    mDrawerLayout.setFocusableInTouchMode(false);

    View leftDrawer = getView().findViewById(R.id.left_drawer);
    int leftDrawerBottomPadding = Display.getNavigationBarHeightKitkat(getActivity());
    leftDrawer.setPadding(leftDrawer.getPaddingLeft(), leftDrawer.getPaddingTop(),
        leftDrawer.getPaddingRight(),
        leftDrawerBottomPadding);

    // ActionBarDrawerToggle± ties together the the proper interactions
    // between the sliding drawer and the action bar app icon
    mDrawerToggle = new ActionBarDrawerToggle(mActivity,
        mDrawerLayout,
        getMainActivity().getToolbar(),
        R.string.drawer_open,
        R.string.drawer_close
    ) {
      public void onDrawerClosed(View view) {
        mActivity.supportInvalidateOptionsMenu();
      }


      public void onDrawerOpened(View drawerView) {
        mActivity.commitPending();
        mActivity.finishActionMode();
      }
    };

    if (isDoublePanelActive()) {
      mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
    }

    // Styling options
    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    mDrawerLayout.addDrawerListener(mDrawerToggle);
    mDrawerToggle.setDrawerIndicatorEnabled(true);

    logger.info("Finished navigation drawer initialization");
  }


  private void refreshMenus() {
    buildMainMenu();
    logger.info("Finished main menu initialization");
    buildCategoriesMenu();
    logger.info("Finished categories menu initialization");
    mDrawerToggle.syncState();
  }


  private void buildCategoriesMenu() {
    CategoryMenuTask task = new CategoryMenuTask(this);
    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }


  private void buildMainMenu() {
    MainMenuTask task = new MainMenuTask(this);
    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }


  void animateBurger(int targetShape) {
    if (mDrawerToggle != null) {
      if (targetShape != BURGER && targetShape != ARROW) {
        return;
      }
      ValueAnimator anim = ValueAnimator.ofFloat((targetShape + 1) % 2, targetShape);
      anim.addUpdateListener(valueAnimator -> {
        float slideOffset = (Float) valueAnimator.getAnimatedValue();
        mDrawerToggle.onDrawerSlide(mDrawerLayout, slideOffset);
      });
      anim.setInterpolator(new DecelerateInterpolator());
      anim.setDuration(500);
      anim.start();
    }
  }


  public static boolean isDoublePanelActive() {

    return false;
  }

}
