

package com.production.advangenote.models.holders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.neopixl.pixlui.components.textview.TextView;
import com.production.advangenote.databinding.NoteLayoutBinding;
import com.production.advangenote.databinding.NoteLayoutExpandedBinding;
import com.production.advangenote.models.views.SquareImageView;


public class NoteViewHolder extends ViewHolder {

  public View root;
  public View cardLayout;
  public View categoryMarker;

  public TextView title;
  public TextView content;
  public TextView date;

  public ImageView archiveIcon;
  public ImageView locationIcon;
  public ImageView alarmIcon;
  public ImageView lockedIcon;
  @Nullable
  public ImageView attachmentIcon;
  @Nullable
  public SquareImageView attachmentThumbnail;

  public NoteViewHolder(View view, boolean expandedView) {
    super(view);

    if (expandedView) {
      NoteLayoutExpandedBinding binding = NoteLayoutExpandedBinding.bind(view);
      root = binding.root;
      cardLayout = binding.cardLayout;
      categoryMarker = binding.categoryMarker;
      title = binding.noteTitle;
      content = binding.noteContent;
      date = binding.noteDate;
      archiveIcon = binding.archivedIcon;
      locationIcon = binding.locationIcon;
      alarmIcon = binding.alarmIcon;
      lockedIcon = binding.lockedIcon;
      attachmentThumbnail = binding.attachmentThumbnail;
      lockedIcon = binding.lockedIcon;
      lockedIcon = binding.lockedIcon;
    } else {
      NoteLayoutBinding binding = NoteLayoutBinding.bind(view);
      root = binding.root;
      cardLayout = binding.cardLayout;
      categoryMarker = binding.categoryMarker;
      title = binding.noteTitle;
      content = binding.noteContent;
      date = binding.noteDate;
      archiveIcon = binding.archivedIcon;
      locationIcon = binding.locationIcon;
      alarmIcon = binding.alarmIcon;
      lockedIcon = binding.lockedIcon;
      attachmentIcon = binding.attachmentIcon;
      lockedIcon = binding.lockedIcon;
      lockedIcon = binding.lockedIcon;
    }

  }

}
