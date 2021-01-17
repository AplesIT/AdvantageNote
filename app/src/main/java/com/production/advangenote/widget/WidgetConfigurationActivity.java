
package com.production.advangenote.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.production.advangenote.R;
import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.models.Category;
import com.production.advangenote.models.adapters.CategoryBaseAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;




public class WidgetConfigurationActivity extends Activity {

  private Spinner categorySpinner;
  private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
  private String sqlCondition;
  private RadioGroup mRadioGroup;
  private static Logger logger = LoggerFactory.getLogger("WidgetConfigurationActivity.class");


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setResult(RESULT_CANCELED);

    setContentView(R.layout.activity_widget_configuration);

    mRadioGroup = findViewById(R.id.widget_config_radiogroup);
    mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
      switch (checkedId) {
        case R.id.widget_config_notes:
          categorySpinner.setEnabled(false);
          break;

        case R.id.widget_config_categories:
          categorySpinner.setEnabled(true);
          break;

        default:
          logger.error("Wrong element choosen: " + checkedId);
      }
    });

    categorySpinner = findViewById(R.id.widget_config_spinner);
    categorySpinner.setEnabled(false);
    DAOSQL db = DAOSQL.getInstance();
    ArrayList<Category> categories = db.getCategories();
    categorySpinner.setAdapter(new CategoryBaseAdapter(this, categories));

    Button configOkButton = findViewById(R.id.widget_config_confirm);
    configOkButton.setOnClickListener(v -> {

      if (mRadioGroup.getCheckedRadioButtonId() == R.id.widget_config_notes) {
        sqlCondition =
            " WHERE " + DAOSQL.KEY_ARCHIVED + " IS NOT 1 AND " + DAOSQL.KEY_TRASHED + " IS" +
                " NOT 1 ";

      } else {
        Category tag = (Category) categorySpinner.getSelectedItem();
        sqlCondition = " WHERE " + DAOSQL.TABLE_NOTES + "."
            + DAOSQL.KEY_CATEGORY + " = " + tag.getId()
            + " AND " + DAOSQL.KEY_ARCHIVED + " IS NOT 1"
            + " AND " + DAOSQL.KEY_TRASHED + " IS NOT 1";
      }

      CheckBox showThumbnailsCheckBox = findViewById(R.id.show_thumbnails);
      CheckBox showTimestampsCheckBox = findViewById(R.id.show_timestamps);

      // Updating the ListRemoteViewsFactory parameter to get the list
      // of notes
      ListRemoteViewsFactory.updateConfiguration(getApplicationContext(), mAppWidgetId,
          sqlCondition, showThumbnailsCheckBox.isChecked(), showTimestampsCheckBox.isChecked());

      Intent resultValue = new Intent();
      resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
          mAppWidgetId);
      setResult(RESULT_OK, resultValue);

      finish();
    });

    // Checks if no tags are available and then disable that option
    if (categories.size() == 0) {
      mRadioGroup.setVisibility(View.GONE);
      categorySpinner.setVisibility(View.GONE);
    }

    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
          AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    // If they gave us an intent without the widget ID, just bail.
    if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
      finish();
    }
  }

}
