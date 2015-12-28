package com.example.photogalleryactivity;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;

public class PhotoGalleryFragment extends Fragment {
	private static final String TAG = "PhotoGalleryFragment";
	
	GridView mGridView;
	ArrayList<GalleryItem> mItems;
	ThumbnailDowanloader<ImageView> mThumbnailThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
			
		//new FetchItemsTask().execute();
		updateItems();
		
//		Intent i = new Intent(getActivity(),PollService.class);
//		getActivity().startService(i);
	//	PollService.setServiceAlarm(getActivity(), true);
		
		//mThumbnailThread = new ThumbnailDowanloader<ImageView>();
		mThumbnailThread = new ThumbnailDowanloader<ImageView>(new Handler());
		mThumbnailThread.setListener(new ThumbnailDowanloader.Listener<ImageView>() {

			@Override
			public void onThumbnailDownloaded(ImageView imageView, Bitmap thumnail) {
				// TODO Auto-generated method stub
				if(isVisible()) {
					imageView.setImageBitmap(thumnail);
				}
			}
		});
		mThumbnailThread.start();
		mThumbnailThread.getLooper();
		Log.i(TAG,"Background thread started");
	}
	
	public AsyncTask<Void, Void, ArrayList<GalleryItem>> updateItems(){
		return new FetchItemsTask().execute();
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
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		mThumbnailThread.clearQueue();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mThumbnailThread.quit();
		Log.i(TAG, "Background thread destroyed");
	}
	
	void setupAdapter() {
		if(getActivity() == null || mGridView == null) return;
		
		if(mItems != null) {
			mGridView.setAdapter(new GalleryItemAdapter(mItems));
			//mGridView.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(), android.R.layout.simple_gallery_item, mItems));
		} else {
			mGridView.setAdapter(null);
		}
	}
	
	private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {
		//String query = "anroid"; //Just For Test
		
		@Override
		protected ArrayList<GalleryItem> doInBackground(Void... params) {
			Activity activity = getActivity();
			if(activity == null)
				return new ArrayList<GalleryItem>();
			
			String query = PreferenceManager.getDefaultSharedPreferences(activity)
					.getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
			if(query != null) {
				return new FlickrFetchr().search(query);
			} else {
				return new FlickrFetchr().fetchItems();
			}
		}
		
		@Override
		protected void onPostExecute(ArrayList<GalleryItem> items) {
			// TODO Auto-generated method stub
			mItems = items;
			setupAdapter();
		}	
	}
	
	private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {
		public GalleryItemAdapter(ArrayList<GalleryItem> items) {
			super(getActivity(),0,items);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item,parent,false);
			} 
			
			ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
			imageView.setImageResource(R.drawable.brian_up_close);
			
			GalleryItem item = getItem(position);
			mThumbnailThread.queueThumbnail(imageView, item.getUrl());
			
			return convertView;
		}
		
	}
	@TargetApi(11)
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_photo_gallery, menu);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			MenuItem searchItem = menu.findItem(R.id.menu_item_search);
			SearchView searchView = (SearchView)searchItem.getActionView();
			
			SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
			ComponentName name = getActivity().getComponentName();
			SearchableInfo searchInfo = searchManager.getSearchableInfo(name);
			
			searchView.setSearchableInfo(searchInfo);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case R.id.menu_item_search :
			getActivity().onSearchRequested();
			return true;
		case R.id.menu_item_clear:
			PreferenceManager.getDefaultSharedPreferences(getActivity())
			.edit()
			.putString(FlickrFetchr.PREF_SEARCH_QUERY, null)
			.commit();
			return true;
		case R.id.menu_item_toggle_polling:
			boolean shouldStartAlarm = !PollService.isServiceAlaremOn(getActivity());
			PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
			
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				getActivity().invalidateOptionsMenu();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
		
		MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
		if(PollService.isServiceAlaremOn(getActivity())) {
			toggleItem.setTitle(R.string.stop_polling);
		} else {
			toggleItem.setTitle(R.string.start_polling);
		}
	}
}
