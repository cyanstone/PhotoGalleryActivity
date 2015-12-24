package com.example.photogalleryactivity;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class PhotoGalleryFragment extends Fragment {
	private static final String TAG = "PhotoGalleryFragment";
	GridView mGridView;
	ArrayList<GalleryItem> mItems;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		new FetchItemsTask().execute();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_photo_gallery,container, false);
		mGridView = (GridView) v.findViewById(R.id.gridView);
		
		setupAdapter();
		return v;
	}
	
	void setupAdapter() {
		if(getActivity() == null || mGridView == null) return;
		
		if(mItems != null) {
			mGridView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_gallery_item,mItems));
		} else {
			mGridView.setAdapter(null);
		}
	}
	
	private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {

		@Override
		protected ArrayList<GalleryItem> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				return new FlickrFetchr().fetchItems();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(ArrayList<GalleryItem> items) {
			// TODO Auto-generated method stub
			mItems = items;
			setupAdapter();
		}
		
	}
}
