package com.example.photogalleryactivity;

import android.support.v4.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity {

	@Override
	public Fragment createFragment() {
		// TODO Auto-generated method stub
		return new PhotoPageFragment();
	}

}
