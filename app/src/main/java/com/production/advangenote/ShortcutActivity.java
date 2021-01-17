 
package com.production.advangenote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import static com.production.advangenote.utils.ConstantsBase.ACTION_SHORTCUT_WIDGET;


public class ShortcutActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent shortcutIntent = new Intent(this, MainActivity.class);
    shortcutIntent.setAction(ACTION_SHORTCUT_WIDGET);
    Intent.ShortcutIconResource iconResource = Intent.ShortcutIconResource
        .fromContext(this, R.drawable
            .shortcut_icon);

    Intent intent = new Intent();
    intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
    intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.add_note));
    intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
    setResult(RESULT_OK, intent);

    finish();
  }
}
