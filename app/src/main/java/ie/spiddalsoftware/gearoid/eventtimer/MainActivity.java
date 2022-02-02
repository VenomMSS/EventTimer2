package ie.spiddalsoftware.gearoid.eventtimer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    CompHelper timesDB = new CompHelper(this);
    String sizeofDB ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        sizeofDB = String.valueOf(DatabaseUtils.queryNumEntries(timesDB.getWritableDatabase(), "timing"));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
                Snackbar.make(view, "Entries recorded = " + sizeofDB, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sizeofDB = String.valueOf(DatabaseUtils.queryNumEntries(timesDB.getWritableDatabase(),"timing"));
        Context context = getApplicationContext();
        TextView tview = (TextView) findViewById(R.id.textview1);
        tview.setText(R.string.current_setting);
        tview.append(getString(R.string.currentlocation) + SettingsActivity.getCheckpoint(context) + '\n');
        tview.append(getString(R.string.currenttimekeeper) + SettingsActivity.getTimekeeper(context)+'\n');
        tview.append(getString(R.string.currenteventtype) + SettingsActivity.getEntrantType(context)+'\n');
        boolean checked = SettingsActivity.isManaged(context);
        tview.append("  "+'\n');
        if (checked) {
            tview.append(getString(R.string.current_ismanaged_true)+'\n');
            tview.append(getString(R.string.current_highest) + SettingsActivity.getMaxEntry(context));
            if (SettingsActivity.isAcceptingOORNumbers(context)) {
                tview.append(getString(R.string.current_OOR_accept));
            } else {
                tview.append(getString(R.string.current_OOR_reject));
            }
            if (SettingsActivity.isAcceptingDuplicates(context)) {
                tview.append(getString(R.string.current_DUP_accept));
            } else {
                tview.append(getString(R.string.current_DUP_reject));
            }
        } else {
            tview.append(getString(R.string.current_ismanaged_false)+'\n');
        }
        checked = SettingsActivity.isClassified(context);
        tview.append(" "  +'\n');
        if(checked) {
            tview.append(getString(R.string.current_isclassified_true)+'\n'+"The current settings are\n");
            tview.append(getString(R.string.current_class) + SettingsActivity.getVehicleClass(context)+'\n');
            tview.append(getString(R.string.current_size) + SettingsActivity.getVehicleSize(context)+'\n');
        } else {
            tview.append(getString(R.string.current_isclassified_false)+'\n');
        }
        tview.append(getString(R.string.settings_can_be_changed));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_enter) {
            // Handle the menu enter action
            Intent intent = new Intent(this, EntryActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_dbase) {
            // Intent intent = new Intent(this, ListActivity.class);
            Intent intent = new Intent(this, list2Activity.class);
            startActivity(intent);

        } else if (id == R.id.menu_save) {
            if (Integer.parseInt(sizeofDB) >0){
                Intent intent = new Intent(this, FileSaveActivity.class);
                 startActivity(intent);}
            else {
                alertuser("EMPTY DATABASE", "There is nothing ot save");
            }

        } else if (id == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_instruction) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);

        } else if ( id == R.id.menu_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void alertuser(String title, String message) {
        Context context = this;
        AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.show();
    }
}
