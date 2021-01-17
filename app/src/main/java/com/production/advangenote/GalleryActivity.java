

package com.production.advangenote;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.production.advangenote.databinding.ActivityGalleryBinding;
import com.production.advangenote.models.Attachment;
import com.production.advangenote.models.listeners.OnViewTouchedListener;
import com.production.advangenote.utils.FileProviderHelper;
import com.production.advangenote.utils.StorageHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import it.feio.android.simplegallery.models.GalleryPagerAdapter;

import static com.production.advangenote.utils.ConstantsBase.GALLERY_CLICKED_IMAGE;
import static com.production.advangenote.utils.ConstantsBase.GALLERY_IMAGES;
import static com.production.advangenote.utils.ConstantsBase.GALLERY_TITLE;
import static com.production.advangenote.utils.ConstantsBase.MIME_TYPE_VIDEO;


/**
 * An example full-screen activity that shows and hides the system UI (i.e. status bar and
 * navigation/system bar) * with user interaction.
 */
public class GalleryActivity extends AppCompatActivity {

  private ActivityGalleryBinding binding;
  private final static Logger logger = LoggerFactory.getLogger("GalleryActivity.class");
  private List<Attachment> images;
  OnViewTouchedListener screenTouches = new OnViewTouchedListener() {
    private final int MOVING_THRESHOLD = 30;
    float x;
    float y;
    private boolean status_pressed = false;


    @Override
    public void onViewTouchOccurred(MotionEvent ev) {
      if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
        x = ev.getX();
        y = ev.getY();
        status_pressed = true;
      }
      if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
        float dx = Math.abs(x - ev.getX());
        float dy = Math.abs(y - ev.getY());
        double dxy = Math.sqrt(dx * dx + dy * dy);
        logger.info("Moved of " + dxy);
        if (dxy >= MOVING_THRESHOLD) {
          status_pressed = false;
        }
      }
      if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
        if (status_pressed) {
          click();
          status_pressed = false;
        }
      }
    }


    private void click() {
      Attachment attachment = images.get(binding.fullscreenContent.getCurrentItem());
      if (attachment.getMime_type().equals(MIME_TYPE_VIDEO)) {
        viewMedia();
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivityGalleryBinding.inflate(getLayoutInflater());
    View view = binding.getRoot();
    setContentView(view);

    initViews();
    initData();
  }

  @Override
  public void onStart() {
    ((AdvantageNotes) getApplication()).getAnalyticsHelper().trackScreenView(getClass().getName());
    super.onStart();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_gallery, menu);
    return true;
  }

  private void initViews() {
    // Show the Up button in the action bar.
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayShowTitleEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    binding.galleryRoot.setOnViewTouchedListener(screenTouches);

    binding.fullscreenContent.addOnPageChangeListener(new OnPageChangeListener() {
      @Override
      public void onPageSelected(int arg0) {
        getSupportActionBar().setSubtitle("(" + (arg0 + 1) + "/" + images.size() + ")");
      }


      @Override
      public void onPageScrolled(int arg0, float arg1, int arg2) {
      }


      @Override
      public void onPageScrollStateChanged(int arg0) {
      }
    });
  }

  /**
   * Initializes data received from note detail screen
   */
  private void initData() {
    String title = getIntent().getStringExtra(GALLERY_TITLE);
    images = getIntent().getParcelableArrayListExtra(GALLERY_IMAGES);
    int clickedImage = getIntent().getIntExtra(GALLERY_CLICKED_IMAGE, 0);

    ArrayList<Uri> imageUris = new ArrayList<>();
    for (Attachment mAttachment : images) {
      imageUris.add(mAttachment.getUri());
    }

    GalleryPagerAdapter pagerAdapter = new GalleryPagerAdapter(this, imageUris);
    binding.fullscreenContent.setOffscreenPageLimit(3);
    binding.fullscreenContent.setAdapter(pagerAdapter);
    binding.fullscreenContent.setCurrentItem(clickedImage);

    getSupportActionBar().setTitle(title);
    getSupportActionBar().setSubtitle("(" + (clickedImage + 1) + "/" + images.size() + ")");

    // If selected attachment is a video it will be immediately played
    if (images.get(clickedImage).getMime_type().equals(MIME_TYPE_VIDEO)) {
      viewMedia();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        break;
      case R.id.menu_gallery_share:
        shareMedia();
        break;
      case R.id.menu_gallery:
        viewMedia();
        break;
      default:
        logger.error("Wrong element choosen: " + item.getItemId());
    }
    return super.onOptionsItemSelected(item);
  }

  private void viewMedia() {
    Attachment attachment = images.get(binding.fullscreenContent.getCurrentItem());
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    intent.setDataAndType(FileProviderHelper.getShareableUri(attachment),
        StorageHelper.getMimeType(this, attachment.getUri()));
    startActivity(intent);
  }

  private void shareMedia() {
    Attachment attachment = images.get(binding.fullscreenContent.getCurrentItem());
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType(StorageHelper.getMimeType(this, attachment.getUri()));
    intent.putExtra(Intent.EXTRA_STREAM, FileProviderHelper.getShareableUri(attachment));
    startActivity(intent);
  }

}
