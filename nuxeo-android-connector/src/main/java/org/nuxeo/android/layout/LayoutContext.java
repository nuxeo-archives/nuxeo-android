package org.nuxeo.android.layout;

import java.util.UUID;

import android.app.Activity;
import android.view.ViewGroup;

public class LayoutContext {

	protected final Activity activity;

	protected final ViewGroup rootView;

	protected String layoutId;

	public LayoutContext(Activity activity, ViewGroup rootView) {
		this.activity=activity;
		this.rootView=rootView;
	}

	public String getLayoutId() {
		if (layoutId==null) {
			layoutId = UUID.randomUUID().toString();
		}
		return layoutId;
	}

	public ViewGroup getRootView() {
		return rootView;
	}

	public Activity getActivity() {
		return activity;
	}



}
