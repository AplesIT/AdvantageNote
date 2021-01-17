
package com.production.advangenote.intro;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.production.advangenote.R;


public class IntroSlide1 extends IntroFragment {

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    binding.introBackground.setBackgroundColor(Color.parseColor("#222222"));
    binding.introTitle.setText(R.string.tour_listactivity_intro_title);
    binding.introImage.setVisibility(View.GONE);
    binding.introImageSmall.setImageResource(R.drawable.logo);
    binding.introImageSmall.setVisibility(View.VISIBLE);
    binding.introDescription.setText(R.string.tour_listactivity_final_detail);
  }
}
