/*
 * (C) Copyright 2010-2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 */

package org.nuxeo.android.simpleclient;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.android.simpleclient.listing.ClipboardDocumentsActivity;
import org.nuxeo.android.simpleclient.listing.DomainListingActivity;
import org.nuxeo.android.simpleclient.listing.MyDocumentsActivity;
import org.nuxeo.android.simpleclient.listing.SavedSearchesDocumentsActivity;
import org.nuxeo.android.simpleclient.listing.TaskListActivity;
import org.nuxeo.android.simpleclient.menus.AboutActivity;
import org.nuxeo.android.simpleclient.menus.OfflineSettingsActivity;
import org.nuxeo.android.simpleclient.menus.SettingsActivity;
import org.nuxeo.android.simpleclient.ui.TitleBarAggregate;
import org.nuxeo.android.simpleclient.ui.TitleBarShowSearchFeature;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import com.smartnsoft.droid4me.app.SmartActivity;
import com.smartnsoft.droid4me.framework.Commands;
import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.menu.StaticMenuCommand;

/**
 * The starting screen of the application.
 * 
 * @author Nuxeo & Smart&Soft
 * @since 2011.02.17
 */
public final class HomeActivity extends SmartActivity<TitleBarAggregate>
        implements View.OnClickListener, TitleBarShowSearchFeature {

    private ImageButton searchDocuments;

    private ImageButton myDocuments;

    private ImageButton clipboard;

    private ImageButton savedSearches;

    private ImageButton browse;

    private ImageButton tasks;

    public void onRetrieveDisplayObjects() {
        setContentView(R.layout.home);
        searchDocuments = (ImageButton) findViewById(R.id.searchDocuments);
        myDocuments = (ImageButton) findViewById(R.id.myDocuments);
        clipboard = (ImageButton) findViewById(R.id.clipboard);
        savedSearches = (ImageButton) findViewById(R.id.savedSearches);
        browse = (ImageButton) findViewById(R.id.explorer);
        tasks = (ImageButton) findViewById(R.id.tasks);
    }

    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {
    }

    public void onFulfillDisplayObjects() {
        searchDocuments.setOnClickListener(this);
        myDocuments.setOnClickListener(this);
        clipboard.setOnClickListener(this);
        savedSearches.setOnClickListener(this);
        browse.setOnClickListener(this);
        tasks.setOnClickListener(this);
    }

    public void onSynchronizeDisplayObjects() {
    }

    @Override
    public List<StaticMenuCommand> getMenuCommands() {
        final List<StaticMenuCommand> commands = new ArrayList<StaticMenuCommand>();
        commands.add(new StaticMenuCommand(R.string.Home_menu_settings, '1',
                's', android.R.drawable.ic_menu_preferences,
                new Commands.StaticEnabledExecutable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(),
                                SettingsActivity.class));
                    }
                }));
        commands.add(new StaticMenuCommand(R.string.Home_menu_offline, '2',
                'a', android.R.drawable.ic_menu_manage,
                new Commands.StaticEnabledExecutable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(),
                                OfflineSettingsActivity.class));
                    }
                }));
        commands.add(new StaticMenuCommand(R.string.Home_menu_about, '2', 'a',
                android.R.drawable.ic_menu_info_details,
                new Commands.StaticEnabledExecutable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(),
                                AboutActivity.class));
                    }
                }));

        return commands;
    }

    @Override
    public void onClick(View view) {
        if (view == searchDocuments) {
            onTitleBarSearch();
        } else if (view == myDocuments) {
            startActivity(new Intent(getApplicationContext(),
                    MyDocumentsActivity.class));
        } else if (view == clipboard) {
            startActivity(new Intent(getApplicationContext(),
                    ClipboardDocumentsActivity.class));
        } else if (view == savedSearches) {
            startActivity(new Intent(getApplicationContext(),
                    SavedSearchesDocumentsActivity.class));
        } else if (view == browse) {
            startActivity(new Intent(getApplicationContext(),
                    DomainListingActivity.class));
        } else if (view == tasks) {
            startActivity(new Intent(getApplicationContext(),
                    TaskListActivity.class));
        }

    }

    @Override
    public void onTitleBarSearch() {
        startSearch(null, true, null, false);
    }

}
