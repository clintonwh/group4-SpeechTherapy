package com.example.sweng04.speachtherapyapp;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;

public class Recordings extends AppCompatActivity implements OnItemClickListener {
    ArrayList<DatabaseOperations.Record>recordings = new ArrayList<DatabaseOperations.Record>();
    final DatabaseOperations db = new DatabaseOperations(this);
    String prevActivity="";
    boolean unassignedRecs;
    int catID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        db.getWritableDatabase();
        catID = getIntent().getIntExtra("catID", 0);
        unassignedRecs = getIntent().getBooleanExtra("unassigned", false);
        Log.d("Unassigned boolean", unassignedRecs + "");
        if (unassignedRecs) {
            recordings = unassignedRecordings();
        } else {
            recordings = db.getRecCat(catID); // This gets the recordings from a specific category.
            //recordings=db.getAllRecords(); // This gets all recordings
        }

        //Log.d("rec array size", recordings.size()+"");
        prevActivity = getIntent().getStringExtra("key");
        //TODO Get Recordings saving and displaying properly
        ListView list = (ListView) findViewById(R.id.rec_list);
        recAdapter adapter = new recAdapter();
        list.setAdapter(adapter);
        TextView emptyText = (TextView)findViewById(android.R.id.empty);
        list.setEmptyView(emptyText);
        list.setOnItemClickListener(this);

    }

    @Override // Goes to parent activity - Back button
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class recAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return recordings.size();
        }

        @Override
        public Object getItem(int position) {
            return recordings.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null) {
                convertView = getLayoutInflater().inflate(R.layout.recordings_row, parent, false);
            }
            TextView recording = (TextView) convertView.findViewById(R.id.rec_text);
            String recordingName = recordings.get(position).getRecName();
            Log.d("Recording name", recordings.get(position).getRecName() + "  end of name and it's ID = " + recordings.get(position).getId());
            recording.setText(recordingName);
            return convertView;
        }
    }

    public ArrayList<DatabaseOperations.Record> unassignedRecordings(){
        ArrayList<DatabaseOperations.Record> allRecs =db.getAllRecords();
        ArrayList<DatabaseOperations.Record> allAssignedRecs =db.getAssignedRec();
        ArrayList<DatabaseOperations.Record> allUnassignedRecs = new ArrayList<>();
        for (int i=0; i<allRecs.size();i++){
            boolean assigned=false;
            for (int j=0;j<allAssignedRecs.size();j++){
                if (allAssignedRecs.get(j)==allRecs.get(i)){
                    assigned=true;
                    break;
                }
            }
            if (assigned==false){
                allUnassignedRecs.add(allRecs.get(i)); // The recording is unassigned
            }
        }
        Log.d("unassigned size", allUnassignedRecs.size()+"");
        return allUnassignedRecs;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // Either play or edit the recordings
        if (prevActivity.equals("Edit")){
            Intent intent = new Intent(Recordings.this, EditRecording.class);
            intent.putExtra("recID", recordings.get(position).getId()); // Passes the recording ID that needs to be edited.
            intent.putExtra("Key","Edit");
            intent.putExtra("catID",catID);
            if (unassignedRecs) {
                intent.putExtra("unassigned", true);
            }
            startActivity(intent);
        }else if (prevActivity.equals("Categories")){
            //Play the recording
            Log.d("rec array size", recordings.size()+"");
        }
    }
}
