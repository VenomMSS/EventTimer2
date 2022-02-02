package ie.spiddalsoftware.gearoid.eventtimer;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String versionName, packageName, name;
        int versioncode;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView display = (TextView) findViewById(R.id.about_textview);

        // set defaults
        versioncode = 999;
        versionName = "none";
        packageName = "none";
        name = "none";

        PackageManager packageManager = getApplicationContext().getPackageManager();
        packageName = getApplicationContext().getPackageName();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versioncode = packageInfo.versionCode;
            versionName = packageInfo.versionName;



        } catch (Exception e) {
            e.printStackTrace();
        }

        display.append("Package:     "+packageName+'\n');
        // display.append("Information: "+name+'\n');
        display.append("Version Name "+versionName+'\n');
        display.append("Build   code "+versioncode+'\n');
        display.append("Export File version "+getString(R.string.file_format)+"\n\n");
        display.append(getString(R.string.app_name)+'\n');
        display.append("Created by \n");
        display.append(getString(R.string.company)+'\n');
        display.append(getString(R.string.emailaddress)+'\n');
        display.append("Copyright 2022 \n");
       


    }
}
