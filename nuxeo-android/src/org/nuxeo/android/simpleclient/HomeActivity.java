package org.nuxeo.android.simpleclient;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;

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
public final class HomeActivity extends SmartActivity {

    public void onRetrieveDisplayObjects() {
        // TODO Auto-generated method stub

    }

    public void onRetrieveBusinessObjects()
            throws BusinessObjectUnavailableException {
        // TODO Auto-generated method stub

    }

    public void onFulfillDisplayObjects() {
        // TODO Auto-generated method stub

    }

    public void onSynchronizeDisplayObjects() {
        // TODO Auto-generated method stub

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
        commands.add(new StaticMenuCommand("My Documents", '2', 'a',
                android.R.drawable.ic_menu_info_details,
                new Commands.StaticEnabledExecutable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(),
                                MyDocumentsActivity.class));
                    }
                }));
        return commands;
    }

}
