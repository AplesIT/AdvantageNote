

package com.production.advangenote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.production.advangenote.async.DataBackupIntentService;
import com.production.advangenote.databinding.ActivitySettingsBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import it.feio.android.analitica.AnalyticsHelper;


public class SettingsActivity extends AppCompatActivity implements
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, FolderChooserDialog.FolderCallback {

  private List<Fragment> backStack = new ArrayList<>();

  private ActivitySettingsBinding binding;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivitySettingsBinding.inflate(getLayoutInflater());
    View view = binding.getRoot();
    setContentView(view);

    initUI();
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.content_frame, new SettingsFragment()).commit();
  }


  void initUI() {
    setSupportActionBar(binding.toolbar.toolbar);
    binding.toolbar.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
  }


  private void replaceFragment(Fragment sf) {
    getSupportFragmentManager().beginTransaction()
        .setCustomAnimations(R.animator.fade_in, R.animator.fade_out,
            R.animator.fade_in, R.animator.fade_out).replace(R.id.content_frame, sf).commit();
  }


  @Override
  public void onBackPressed() {
    if (!backStack.isEmpty()) {
      replaceFragment(backStack.remove(backStack.size() - 1));
    } else {
      super.onBackPressed();
    }
  }


  public void showMessage(int messageId, Style style) {
    showMessage(getString(messageId), style);
  }


  public void showMessage(String message, Style style) {
    // ViewGroup used to show Crouton keeping compatibility with the new Toolbar
    Crouton.makeText(this, message, style, binding.croutonHandle.croutonHandle).show();
  }


  @Override
  public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull File folder) {
    new MaterialDialog.Builder(this)
        .title(R.string.data_import_message_warning)
        .content(folder.getName())
        .positiveText(R.string.confirm)
        .onPositive((dialog1, which) -> {
          ((AdvantageNotes) getApplication()).getAnalyticsHelper()
              .trackEvent(AnalyticsHelper.CATEGORIES.SETTING,
                  "settings_import_data");
          Intent service = new Intent(getApplicationContext(), DataBackupIntentService.class);
          service.setAction(DataBackupIntentService.ACTION_DATA_IMPORT_LEGACY);
          service.putExtra(DataBackupIntentService.INTENT_BACKUP_NAME, folder.getAbsolutePath());
          startService(service);
        }).build().show();
  }

  @Override
  public void onFolderChooserDismissed(@NonNull FolderChooserDialog dialog) {
    // Nothing to do
  }

  @Override
  public void onPointerCaptureChanged(boolean hasCapture) {
    // Nothing to do
  }

  @Override
  public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
    Bundle b = new Bundle();
    b.putString(SettingsFragment.XML_NAME, pref.getKey());

    final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
        getClassLoader(),
        pref.getFragment());
    fragment.setArguments(b);
    fragment.setTargetFragment(caller, 0);

    getSupportFragmentManager().beginTransaction()
        .replace(R.id.content_frame, fragment)
        .addToBackStack(null)
        .commit();
    return true;
  }

}
