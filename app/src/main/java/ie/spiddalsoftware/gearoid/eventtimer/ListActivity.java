package ie.spiddalsoftware.gearoid.eventtimer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ListActivity extends AppCompatActivity {
    public static List<String> records;
    CompHelper timesDB = new CompHelper(this);
    ArrayAdapter<String> listAdapter;
    ListView listing ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // link data
        listing = (ListView) findViewById(R.id.listings);
        timesDB.getWritableDatabase();
        records = timesDB.getAllForDisplay();
        listAdapter = new ArrayAdapter<String>(this, R.layout.row_layout, R.id.listtext, records);
        listing.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

    }
}
