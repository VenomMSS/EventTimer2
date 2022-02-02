package ie.spiddalsoftware.gearoid.eventtimer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EntryActivity extends AppCompatActivity {
    private ArrayAdapter<CharSequence> vclassAdapter;
    private ArrayAdapter<CharSequence> vsizeAdapter;
    static String entrie, entrytype, entryclass, entrysize, settype, setclass, setsize;
    String feedbackString, location, timekeeper;
    boolean ismanaged, acceptOOR, acceptDup;
    int highestnumber = 100;
    CompHelper timesDB = new CompHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        // set up vehicle class spinner
        final Spinner VehicleClass =(Spinner) findViewById(R.id.vehicleclassSpinner);
        vclassAdapter = ArrayAdapter.createFromResource(this,
                R.array.pref_classify_age_values, android.R.layout.simple_spinner_item);
        vclassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        VehicleClass.setAdapter(vclassAdapter);

        // set up vehicle size spinner
        final Spinner VehicleSize = (Spinner) findViewById(R.id.vehiclesizeSpinner);
        vsizeAdapter = ArrayAdapter.createFromResource(this,
                R.array.pref_classify_size_values, android.R.layout.simple_spinner_item);
        vsizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        VehicleSize.setAdapter(vsizeAdapter);

        final EditText NumbersEntry = (EditText) findViewById(R.id.entryNumbers);

        // read current vehicle classifcation from preferences
        // only enable these spinners if 'isClasified' is true
        if (SettingsActivity.isClassified(this)) {
            entryclass = SettingsActivity.getVehicleClass(this);
            entrysize = SettingsActivity.getVehicleSize(this);
            VehicleClass.setSelection(getPosition(VehicleClass, entryclass));
            VehicleSize.setSelection(getPosition(VehicleSize, entrysize));
            VehicleClass.setEnabled(true);
            VehicleSize.setEnabled(true);
        } else {
            entryclass = "notclassified";
            entrysize = "nosize";
            VehicleClass.setEnabled(false);
            VehicleSize.setEnabled(false);
        }

        NumbersEntry.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    if ((keyCode == KeyEvent.KEYCODE_ENTER)
                            || (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
                        // get current time
                        Date now = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat(
                                "dd/MM/yyyy HH:mm:ss", Locale.UK);
                        String nowString = sdf.format(now);
                        // if vehicles are classified then get most recent values from spinners
                        if (SettingsActivity.isClassified(getApplicationContext())) {
                            entryclass = (String) VehicleClass.getSelectedItem();
                            entrysize = (String) VehicleSize.getSelectedItem();
                        } else {
                            entryclass = "notclassified";
                            entrysize = "nosize";
                        }
                        // get other settings from preferences
                        location = SettingsActivity.getCheckpoint(getApplicationContext());
                        //timekeeper = SettingsActivity.getTimekeeper(getApplicationContext());
                        entrytype = SettingsActivity.getEntrantType(getApplicationContext());
                        ismanaged = SettingsActivity.isManaged(getApplicationContext());
                        // get range and checks if 'ismanaged'
                        if (ismanaged) {
                            highestnumber = SettingsActivity.getMaxEntry(getApplicationContext());
                            acceptOOR = SettingsActivity.isAcceptingOORNumbers(getApplicationContext());
                            acceptDup = SettingsActivity.isAcceptingDuplicates(getApplicationContext());
                        }

                        feedbackString = "recorded ";
                        String a="";
                        int entry_number;
                        boolean numberalreadyentered = false;


                        // get string array of entered numbers from the EditText
                        String[] entries = NumbersEntry.getText().toString().split("[-.+*/#]");

                        // then save each number = test for acceptance
                        for (String s : entries) {
                            try {
                                a = s.trim();
                                // if not managed then always save - no need to text what number entered
                                if (!ismanaged) {
                                    timesDB.addEntry(entrytype,a, location,nowString,entryclass,entrysize);
                                    feedbackString = feedbackString+a+" not managed , ";
                                } else {
                                    // need to check if number is ok
                                    // see if OORange
                                    entry_number = Integer.parseInt(a);
                                    if ((entry_number >highestnumber)&&(!acceptOOR)) {
                                        // feedback
                                        //TODO change this to an alert dialog
                                        alertuser("OOR ERROR",
                                          "This number [ " + a + " ] is out of range and settings do not allow out of range values");
                                        feedback("Number " + a + " OORange. Not processed");
                                    } else {
                                        // check is already enterd and duplicates not allowed
                                        // numberalreadyentered is created because it is needed after the item is added.
                                        // Ttesting after the item is added will always give true to isAlreadyEntered()
                                        numberalreadyentered = timesDB.isAlreadyEntered(a);
                                        if ((timesDB.isAlreadyEntered(a))&&(!acceptDup)) {
                                            // feedback
                                            //TODO change to an Alert Dialog
                                            alertuser("DUPLICATE ERROR",
                                             "This number [ " + a + " ] is already entered and settings do not allow duplicates");

                                            feedback("Number "+a+" Already entered. Not processed");
                                        } else {
                                            // number is acceptable to process
                                            // save to database
                                            timesDB.addEntry(entrytype, a,location,
                                                    sdf.format(now),entryclass,entrysize);
                                            feedbackString = feedbackString+a+" , ";
                                            // if oor ( even if accepted) give warning to user
                                            if (entry_number >highestnumber) {
                                                feedback("Number " + a + " recorded but is out of range");
                                            }
                                            // numberalradyentered was created before adding item
                                            if (numberalreadyentered) {
                                                feedback("Number " + a + " recorded but is a duplicate entry");
                                            }
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                CharSequence text = e.toString() + " cannot be resolved to a number";
                                feedback(text);
                            }
                        }
                        NumbersEntry.setText("");
                        feedback(feedbackString);
                        return true;
                    }


                return false;
            }


        });
    }

    private int getPosition(Spinner sp, String ent) {
        int found = 0;
        for (int i =0; i<sp.getCount();i++) {
            if (ent.equals((String) sp.getItemAtPosition(i))) found =i;
        }
        return found;
    }

    private void feedback(CharSequence message) {
        Context context = getApplicationContext();
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    private void alertuser(String title, String message) {
        Context context = this;
        AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setButton(AlertDialog.BUTTON_NEUTRAL,"OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.show();
    }
}
