package com.production.advangenote.models.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.production.advangenote.MainActivity;
import com.production.advangenote.R;
import com.production.advangenote.databinding.DrawerListItemBinding;
import com.production.advangenote.models.Category;
import com.production.advangenote.models.adapters.category.CategoryViewHolder;

import java.util.List;

import static com.production.advangenote.utils.Constants.PREFS_NAME;
import static com.production.advangenote.utils.ConstantsBase.PREF_NAVIGATION;

/**
 * @author vietnh
 * @name CategoryRecyclerViewAdapter
 * @date 10/1/20
 **/
public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private Activity mActivity;
    private List<Category> categories;
    private String navigationTmp;


    public CategoryRecyclerViewAdapter(Activity mActivity, List<Category> categories) {
        this(mActivity, categories, null);
    }

    public CategoryRecyclerViewAdapter(Activity mActivity, List<Category> categories,
                                       String navigationTmp) {
        this.mActivity = mActivity;
        this.categories = categories;
        this.navigationTmp = navigationTmp;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(
                DrawerListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.txtTitle.setText(category.getName());

        if (isSelected(position)) {
            holder.txtTitle.setTypeface(null, Typeface.BOLD);
            holder.txtTitle.setTextColor(Integer.parseInt(category.getColor()));
        } else {
            holder.txtTitle.setTypeface(null, Typeface.NORMAL);
            holder.txtTitle.setTextColor(mActivity.getResources().getColor(R.color.drawer_text));
        }

        // Set the results into ImageView checking if an icon is present before
        if (category.getColor() != null && category.getColor().length() > 0) {
            Drawable img = mActivity.getResources().getDrawable(R.drawable.ic_folder_special_black_24dp);
            ColorFilter cf = new LightingColorFilter(Color.parseColor("#000000"),
                    Integer.parseInt(category.getColor()));
            img.mutate().setColorFilter(cf);
            holder.imgIcon.setImageDrawable(img);
            int padding = 4;
            holder.imgIcon.setPadding(padding, padding, padding, padding);
        }
        showCategoryCounter(holder, category);
    }

    private void showCategoryCounter(@NonNull CategoryViewHolder holder, Category category) {
        if (mActivity.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS).getBoolean(
                "settings_show_category_count", true)) {
            holder.count.setText(String.valueOf(category.getCount()));
            holder.count.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean isSelected(int position) {
        String[] navigationListCodes = mActivity.getResources()
                .getStringArray(R.array.navigation_list_codes);

        // Managing temporary navigation indicator when coming from a widget
        String navigationTmpLocal =
                MainActivity.class.isAssignableFrom(mActivity.getClass()) ? ((MainActivity)
                        mActivity).getNavigationTmp() : null;
        navigationTmpLocal = this.navigationTmp != null ? this.navigationTmp : navigationTmpLocal;

        String navigation = navigationTmp != null ? navigationTmpLocal
                : mActivity.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS)
                .getString(PREF_NAVIGATION, navigationListCodes[0]);

        return navigation.equals(String.valueOf(categories.get(position).getId()));
    }

}