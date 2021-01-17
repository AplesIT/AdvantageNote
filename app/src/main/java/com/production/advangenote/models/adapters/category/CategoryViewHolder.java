package com.production.advangenote.models.adapters.category;

import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.neopixl.pixlui.components.textview.TextView;
import com.production.advangenote.databinding.DrawerListItemBinding;
/**
 * @author vietnh
 * @name CategoryViewHolder
 * @date 1/10/20
 **/
public class CategoryViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgIcon;
    public TextView txtTitle;
    public android.widget.TextView count;

    public CategoryViewHolder(DrawerListItemBinding binding) {
        super(binding.getRoot());
        imgIcon = binding.icon;
        txtTitle = binding.title;
        count = binding.count;
    }
}