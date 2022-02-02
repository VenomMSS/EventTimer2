package ie.spiddalsoftware.gearoid.eventtimer;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

public class list2Activity extends AppCompatActivity {
    CompHelper timesDB = new CompHelper(this);
    TimingsAdapter myadapter;
    Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);
        // get reference to the listview
        ListView currentTimings = (ListView)findViewById(R.id.currentTimings);
        View header = (View)getLayoutInflater().inflate(R.layout.listheaderlayout,null);
        currentTimings.addHeaderView(header);

        // get database and get all records
        timesDB.getWritableDatabase();
        cursor = timesDB.getAllinCursor();

        // link cursor to listview using adapter
        myadapter = new TimingsAdapter(this, cursor, 0);
        currentTimings.setAdapter(myadapter);
    }
}
