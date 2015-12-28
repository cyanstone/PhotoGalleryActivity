package com.example.photogalleryactivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PhotoPageFragment extends VisibleFragment {
	private String mUrl;
	private WebView mWebView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		mUrl = getActivity().getIntent().getData().toString();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_photo_page,container, false);
		
		final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		progressBar.setMax(100);
		
		final TextView titleTextView = (TextView)v.findViewById(R.id.titleTextView);
		
		mWebView = (WebView) v.findViewById(R.id.webView);
		mWebView.getSettings().setJavaScriptEnabled(true);
		
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				if(newProgress == 100) {
					progressBar.setVisibility(View.INVISIBLE);
				} else {
					progressBar.setVisibility(View.VISIBLE);
					progressBar.setProgress(newProgress);
				}
			}
			
			@Override
			public void onReceivedTitle(WebView view, String title) {
				// TODO Auto-generated method stub
				titleTextView.setText(title);
			}
		});
		mWebView.loadUrl(mUrl);
		return v;
	}
}
