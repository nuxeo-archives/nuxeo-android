/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.android.layout;

import java.util.UUID;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

public class LayoutContext {

    protected final Activity activity;

    protected final ViewGroup rootView;

    protected String layoutId;
    
    protected final Fragment fragment;

    public LayoutContext(Activity act, ViewGroup rootView, Fragment frag) {
    	this.activity = act;
    	this.rootView = rootView;
    	this.fragment = frag;
    }
    
    public LayoutContext(Activity activity, ViewGroup rootView) {
        this.activity = activity;
        this.rootView = rootView;
        this.fragment = null;
    }

    public String getLayoutId() {
        if (layoutId == null) {
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
    
    public Fragment getFragment() {
    	return fragment;
    }

}
