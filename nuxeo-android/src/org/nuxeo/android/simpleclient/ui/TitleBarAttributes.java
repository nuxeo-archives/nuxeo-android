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
package org.nuxeo.android.simpleclient.ui;

import org.nuxeo.android.simpleclient.R;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smartnsoft.droid4me.app.ProgressHandler;

public class TitleBarAttributes extends ProgressHandler {

    private boolean enabledRefresh;

    final ImageButton home;

    // private final View separator1;

    final TextView headerTitle;

    private final View separator2;

    final ImageButton action;

    private final View separator3;

    final ImageButton refresh;

    private final ProgressBar refreshProgress;

    private final View separator4;

    final ImageButton search;

    public TitleBarAttributes(Activity activity, View view) {
        home = (ImageButton) view.findViewById(R.id.home);
        // separator1 = view.findViewById(R.id.separator1);
        headerTitle = (TextView) view.findViewById(R.id.headerTitle);
        setTitle(activity.getText(R.string.applicationTitle));
        separator2 = view.findViewById(R.id.separator2);
        action = (ImageButton) view.findViewById(R.id.action);
        separator3 = view.findViewById(R.id.separator3);
        refresh = (ImageButton) view.findViewById(R.id.refresh);
        refreshProgress = (ProgressBar) view.findViewById(R.id.refreshProgress);
        separator4 = view.findViewById(R.id.separator4);
        search = (ImageButton) view.findViewById(R.id.search);
        // home.setOnClickListener(this);
        setShowRefresh(null);
        setShowSearch(false, null);
        setShowAction(-1, null);
    }

    public void setTitle(CharSequence title) {
        // headerTitle.setText(title);
    }

    public void setShowHome(int iconResourceId,
            View.OnClickListener onClickListener) {
        if (iconResourceId != -1) {
            home.setImageResource(iconResourceId);
        } else {
            home.setImageDrawable(null);
        }
        home.setOnClickListener(onClickListener);
    }

    public void setShowAction(int iconResourceId,
            View.OnClickListener onClickListener) {
        if (iconResourceId != -1) {
            action.setImageResource(iconResourceId);
        } else {
            action.setImageDrawable(null);
        }
        action.setVisibility(onClickListener != null ? View.VISIBLE : View.GONE);
        separator2.setVisibility(onClickListener != null ? View.VISIBLE
                : View.GONE);
        action.setOnClickListener(onClickListener);
    }

    public void setShowRefresh(View.OnClickListener onClickListener) {
        refresh.setVisibility(onClickListener != null ? View.VISIBLE
                : View.INVISIBLE);
        separator3.setVisibility(onClickListener != null ? View.VISIBLE
                : View.INVISIBLE);
        refresh.setOnClickListener(onClickListener);
        enabledRefresh = onClickListener != null;
    }

    public void setShowSearch(boolean value,
            View.OnClickListener onClickListener) {
        search.setVisibility(value == true ? View.VISIBLE : View.GONE);
        separator4.setVisibility(value == true ? View.VISIBLE : View.GONE);
        search.setOnClickListener(onClickListener);
    }

    public void onProgress(boolean isLoading) {
        toggleRefresh(isLoading);
    }

    public void toggleRefresh(boolean isLoading) {
        if (enabledRefresh == true) {
            refresh.setVisibility(isLoading == true ? View.INVISIBLE
                    : View.VISIBLE);
        }
        refreshProgress.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void dismiss(Activity activity, Object progressExtra) {
        toggleRefresh(false);
    }

    @Override
    protected void show(Activity activity, Object progressExtra) {
        toggleRefresh(true);
    }

}
