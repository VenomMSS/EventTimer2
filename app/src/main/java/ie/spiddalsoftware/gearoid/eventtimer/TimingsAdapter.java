package ie.spiddalsoftware.gearoid.eventtimer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by sarah on 29/12/2015.
 */
public class TimingsAdapter extends CursorAdapter {
    public  TimingsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_itemlayout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView numTV = (TextView)view.findViewById(R.id.entrynumTV);
        TextView locTV = (TextView)view.findViewById(R.id.locationTV);
        TextView whenTV = (TextView)view.findViewById(R.id.whenTV);
        TextView typeTV = (TextView)view.findViewById(R.id.entrytypeTV);
        TextView classTV = (TextView)view.findViewById(R.id.entryclassTV);
        TextView sizeTV = (TextView)view.findViewById(R.id.entrysizeTV);
        TextView idTV = (TextView)view.findViewById(R.id.IDTV);

        String datetime = cursor.getString(cursor.getColumnIndexOrThrow("time"));
        String timeolnly = datetime.substring(11);
        String dateonly = datetime.substring(0,11);
        whenTV.setText(timeolnly);

        numTV.setText(cursor.getString(cursor.getColumnIndexOrThrow("entry")));
        locTV.setText(cursor.getString(cursor.getColumnIndexOrThrow("location")));
        typeTV.setText(cursor.getString(cursor.getColumnIndexOrThrow("type")));
        classTV.setText(cursor.getString(cursor.getColumnIndexOrThrow("class")));
        sizeTV.setText(cursor.getString(cursor.getColumnIndexOrThrow("size")));
        idTV.setText(dateonly);
        // idTV.setText(cursor.getString(cursor.getColumnIndexOrThrow("_id")));



    }
}
