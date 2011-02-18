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
public final class HomeActivity extends SmartActivity<NuxeoAndroidApplication.TitleBarAggregate> implements
        View.OnClickListener {

    private ImageButton search;

    private ImageButton myDocuments;

    private ImageButton clipboard;

    private ImageButton savedSearches;

    public void onRetrieveDisplayObjects() {
        setContentView(R.layout.home);
        search = (ImageButton) findViewById(R.id.search);
        myDocuments = (ImageButton) findViewById(R.id.myDocuments);
        clipboard = (ImageButton) findViewById(R.id.clipboard);
        savedSearches = (ImageButton) findViewById(R.id.savedSearches);
    }

    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {
    }

    public void onFulfillDisplayObjects() {
        search.setOnClickListener(this);
        myDocuments.setOnClickListener(this);
        clipboard.setOnClickListener(this);
        savedSearches.setOnClickListener(this);
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
        if (view == search) {
            startSearch(null, true, null, false);
        } else if (view == myDocuments) {
            startActivity(new Intent(getApplicationContext(),
                    MyDocumentsActivity.class));
        } else if (view == clipboard) {
            startActivity(new Intent(getApplicationContext(),
                    MyDocumentsActivity.class));
        } else if (view == savedSearches) {
            startActivity(new Intent(getApplicationContext(),
                    MyDocumentsActivity.class));
        }
    }

}
