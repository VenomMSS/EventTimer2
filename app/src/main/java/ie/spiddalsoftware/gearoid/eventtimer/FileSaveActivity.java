package ie.spiddalsoftware.gearoid.eventtimer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FileSaveActivity extends AppCompatActivity {
    public String FileName;
    public CompHelper timesDB = new CompHelper(this);
    public static List<String> records;
    int STORAGE_PERMISSION_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_save);

        timesDB.getWritableDatabase();

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddHHmmss", Locale.UK);
        FileName = SettingsActivity.getCheckpoint(this) + sdf.format(now) +".txt";
        EditText FileNameView = (EditText)findViewById(R.id.filename_edit);
        FileNameView.setText(FileName);

        // make delete buttons and additional text invisible until needed

        // check for permission and request if necessary
        // check for permission if the build version is equal or greater than M ( 23)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasStoragePermission()) {
                //n request storage permission if NOT has permission
                requestStoragePermission();
            } }

    }



    public void writeToFile(View view) {
        boolean datasaved = false;
        TextView whilesave = (TextView) findViewById(R.id.whilesaving);
        whilesave.setText(R.string.whilefilesave);
        EditText FileNameView = (EditText)findViewById(R.id.filename_edit);
        FileName = FileNameView.getText().toString();

        datasaved = writetocard(FileName);

        // if data is successfully saved then ask user if to delete
        if (datasaved) {
            String message = getString(R.string.save_success);
            whilesave.setText(message +" to file "+ FileName);
            TextView deleteInst = (TextView) findViewById(R.id.deleteinstructions);
            deleteInst.setText(R.string.delete_question);
            // set to bold   deleteInst.setTextAppearance(2);

            Button delbutton = (Button) findViewById(R.id.delete_btn);
            delbutton.setEnabled(true);
            delbutton.setClickable(true);
            delbutton.setBackgroundColor(Color.RED);
            Button keepbutton = (Button) findViewById(R.id.notdelete_btn);
            keepbutton.setEnabled(true);
            keepbutton.setClickable(true);
        }
    }

    public void exitActivity(View view) {
        this.finish();
    }

    private boolean writetocard(String filename) {
        boolean success = false;
        String fileformat = getString(R.string.file_format);
        String TimeKeeper = SettingsActivity.getTimekeeper(this);
        String writeline ="";
        if (isExternalStorageWritable()) {
            records = timesDB.getAllRecords();
            // create directory
            try {
                File folder = new File(Environment.getExternalStorageDirectory() + "/Timing_Files");
                if (!folder.exists()) {
                   boolean dirsuccess = folder.mkdir();
                }
                // d  = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                File writefile = new File(folder, FileName);
                try {
                    FileOutputStream fos = new FileOutputStream(writefile);
                    for (String s: records) {
                    writeline = fileformat+","+s+","+TimeKeeper;
                    fos.write(writeline.getBytes());
                     fos.write('\n');
                   }
                    //writeline = "this is a test";
                   // fos.write(writeline.getBytes());
                   // fos.write('\n');
                    fos.close();
                    success = true;

                } catch (Exception e) {
                   feedback("error writing to file " + e.getMessage());
                    success = false;
                }

            } catch (Exception e) {
            feedback("cannot create file " + e.getMessage());
            success = false;
            }

        } else {
            // no external storage available
            feedback("No external storage available");
            success = false;

        }
        return success;
    }

    private boolean hasStoragePermission() {
        return (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED);
    }

    // mothod called to request permission if needed


    private void requestStoragePermission() {
        // give explanation if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale( Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed TO SAVE DATA IN A FILE")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(FileSaveActivity.this,
                                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        }
    }

    // method onRequestPermissionsResult)_ is automoatically called after requestPermissions is called

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }




    // check if external media is available
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteDatabase(View view) {

        timesDB.onUpgrade(timesDB.getWritableDatabase(),1,2);
        alertuser("DATA DELETE", "All records have been deleted");
        // this.finish();
    }

    private void feedback(CharSequence message) {
        Context context = getApplicationContext();
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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
