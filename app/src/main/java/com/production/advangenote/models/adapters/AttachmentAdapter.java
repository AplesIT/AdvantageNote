package com.production.advangenote.models.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.production.advangenote.R;
import com.production.advangenote.helpers.date.DateHelper;
import com.production.advangenote.models.Attachment;
import com.production.advangenote.models.views.ExpandableHeightGridView;
import com.production.advangenote.models.views.SquareImageView;
import com.production.advangenote.utils.BitmapHelper;
import com.production.advangenote.utils.date.DateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import static com.production.advangenote.utils.ConstantsBase.DATE_FORMAT_SORTABLE;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_AUDIO;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_FILES;

/**
 * @author vietnh
 * @name AttachmentAdapter
 * @date 10/25/20
 **/
public class AttachmentAdapter extends BaseAdapter {

    private Activity mActivity;
    private List<Attachment> attachmentsList;
    private LayoutInflater inflater;
    private final Logger logger= LoggerFactory.getLogger(this.getClass().getName());


    public AttachmentAdapter(Activity mActivity, List<Attachment> attachmentsList,
                             ExpandableHeightGridView mGridView) {
        this.mActivity = mActivity;
        if (attachmentsList == null) {
            attachmentsList = Collections.emptyList();
        }
        this.attachmentsList = attachmentsList;
        inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public int getCount() {
        return attachmentsList.size();
    }


    public Attachment getItem(int position) {
        return attachmentsList.get(position);
    }


    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        logger.info("GridView called for position " + position);

        Attachment mAttachment = attachmentsList.get(position);

        AttachmentHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gridview_item, parent, false);

            holder = new AttachmentHolder();
            holder.image = convertView.findViewById(R.id.gridview_item_picture);
            holder.text = convertView.findViewById(R.id.gridview_item_text);
            convertView.setTag(holder);
        } else {
            holder = (AttachmentHolder) convertView.getTag();
        }

        // Draw name in case the type is an audio recording
        if (mAttachment.getMime_type() != null && mAttachment.getMime_type().equals(MIME_TYPE_AUDIO)) {
            String text;

            if (mAttachment.getLength() > 0) {
                // Recording duration
                text = DateHelper.formatShortTime(mActivity, mAttachment.getLength());
            } else {
                // Recording date otherwise
                text = DateUtils.getLocalizedDateTime(mActivity, mAttachment
                        .getUri().getLastPathSegment().split("\\.")[0], DATE_FORMAT_SORTABLE);
            }

            if (text == null) {
                text = mActivity.getString(R.string.attachment);
            }
            holder.text.setText(text);
            holder.text.setVisibility(View.VISIBLE);
        } else {
            holder.text.setVisibility(View.GONE);
        }

        // Draw name in case the type is an audio recording (or file in the future)
        if (mAttachment.getMime_type() != null && mAttachment.getMime_type().equals(MIME_TYPE_FILES)) {
            holder.text.setText(mAttachment.getName());
            holder.text.setVisibility(View.VISIBLE);
        }

        // Starts the AsyncTask to draw bitmap into ImageView
        Uri thumbnailUri = BitmapHelper.getThumbnailUri(mActivity, mAttachment);
        Glide.with(mActivity.getApplicationContext())
                .load(thumbnailUri)
                .into(holder.image);

        return convertView;
    }


    public List<Attachment> getAttachmentsList() {
        return attachmentsList;
    }


    public class AttachmentHolder {

        TextView text;
        SquareImageView image;
    }


}

