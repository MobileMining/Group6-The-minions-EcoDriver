package com.aronssondev.andreas.drivetracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.aronssondev.andreas.drivetracker.DriveDatabaseHelper.DriveCursor;

public class DriveListFragment extends ListFragment implements LoaderCallbacks<Cursor> {
    private static final int REQUEST_NEW_RUN = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // initialize the loader to load the list of drives
        getLoaderManager().initLoader(0, null, this);
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // we only ever load the drives, so assume this is the case
        return new DriveListCursorLoader(getActivity());
    }
    
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // create an adapter to point at this cursor
        DriveCursorAdapter adapter = new DriveCursorAdapter(getActivity(), (DriveCursor)cursor);
        setListAdapter(adapter);
    }
    
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // stop using the cursor (via the adapter)
        setListAdapter(null);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.drive_list_options, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_item_new_drive:
            Intent i = new Intent(getActivity(), DriveActivity.class);
            startActivityForResult(i, REQUEST_NEW_RUN);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_NEW_RUN == requestCode) {
            // restart the loader to get any new drive available
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // the id argument will be the Drive ID; CursorAdapter gives us this for free
        Intent i = new Intent(getActivity(), DriveActivity.class);
        i.putExtra(DriveActivity.EXTRA_RUN_ID, id);
        startActivity(i);
    }

    private static class DriveListCursorLoader extends SQLiteCursorLoader {

        public DriveListCursorLoader(Context context) {
            super(context);
        }

        @Override
        protected Cursor loadCursor() {
            // query the list of drives
            return DriveManager.get(getContext()).queryDrives();
        }
        
    }
    
    private static class DriveCursorAdapter extends CursorAdapter {
        
        private DriveCursor mDriveCursor;
        
        public DriveCursorAdapter(Context context, DriveCursor cursor) {
            super(context, cursor, 0);
            mDriveCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // use a layout inflater to get a row view
            LayoutInflater inflater = 
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // get the drive for the current row
            Drive drive = mDriveCursor.getDrive();
            
            // set up the start date text view
            TextView startDateTextView = (TextView)view;
            startDateTextView.setText("Drive at " + drive.getStartDate());
        }
        
    }
}
