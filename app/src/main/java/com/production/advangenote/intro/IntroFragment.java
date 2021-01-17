
package com.production.advangenote.intro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.production.advangenote.databinding.IntroSlideBinding;


public class IntroFragment extends Fragment {

  IntroSlideBinding binding;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = IntroSlideBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

}
