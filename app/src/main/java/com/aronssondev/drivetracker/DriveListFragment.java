package com.aronssondev.drivetracker;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class DriveListFragment extends ListFragment {

    private static final int REQUEST_NEW_DRIVE = 0;

    private static final int LOAD_DRIVES = 1;

    private ActionMode mActionMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

//        getLoaderManager().initLoader(LOAD_DRIVES, null, mLoaderCallbacks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        ListView listView = (ListView) v.findViewById(android.R.id.list);

        /*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    return false;
                }

                getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                getListView().setItemChecked(position, true);

                ((ActionBarActivity) getActivity()).startSupportActionMode(new ActionMode.Callback() {

                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        MenuInflater inflater = actionMode.getMenuInflater();
                        inflater.inflate(R.menu.menu_drive_delete_context, menu);
                        mActionMode = actionMode;
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_item_delete_drive:
                                DriveCursorAdapter adapter = (DriveCursorAdapter) getListAdapter();
                                DriveManager driveManager = DriveManager.getInstance(getActivity());
                                long currentDriveId = getActivity().getSharedPreferences(
                                        DriveManager.PREFS_FILE, Context.MODE_PRIVATE).getLong(
                                        DriveManager.PREF_CURRENT_DRIVE_ID, 0);
                                for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                    if (getListView().isItemChecked(i)) {
                                        DriveDatabaseHelper.DriveCursor driveCursor = (DriveDatabaseHelper.DriveCursor) adapter.getItem(i);
                                        long driveId = driveCursor.getDrive().getId();
                                        if (driveId != currentDriveId) {
                                            driveManager.removeDrive(driveId);
                                        }
                                    }
                                }
                                actionMode.finish();
                                getLoaderManager().restartLoader(LOAD_DRIVES, null, mLoaderCallbacks);
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {
                        getListView().clearChoices();

                        for (int i = 0; i < getListView().getChildCount(); i++) {
                            getListView().getChildAt(i).getBackground().setState(new int[]{0});
                        }

                        getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
                        mActionMode = null;
                    }
                });

                return true;
            }
        });*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    if (getListView().isItemChecked(position)) {
                        getListView().setItemChecked(position, false);
                    } else {
                        getListView().setItemChecked(position, true);
                    }
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().initLoader(LOAD_DRIVES, null, mLoaderCallbacks);

        long currentDriveId = getActivity().getSharedPreferences(
                DriveManager.PREFS_FILE, Context.MODE_PRIVATE).getLong(
                DriveManager.PREF_CURRENT_DRIVE_ID, 0);

        if (currentDriveId == 0) {
            return;
        }

        Intent i = new Intent(getActivity(), DriveActivity.class);
        i.putExtra(DriveFragment.EXTRA_DRIVE_ID, currentDriveId);

        PendingIntent pi = PendingIntent.getActivity(getActivity(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources resources = getResources();

        Notification notification = new NotificationCompat.Builder(getActivity())
                .setTicker(resources.getString(R.string.current_drive_id))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.current_drive_id))
                .setContentText("Current Drive ID is " + currentDriveId)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager)
                getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private static class DriveCursorAdapter extends CursorAdapter {

        private DriveDatabaseHelper.DriveCursor mDriveCursor;

        public DriveCursorAdapter(Context context, DriveDatabaseHelper.DriveCursor driveCursor) {
            super(context, driveCursor, 0);
            mDriveCursor = driveCursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Drive drive = mDriveCursor.getDrive();

            TextView startDateTextView = (TextView) view;
            String cellText = context.getString(R.string.cell_text, drive.getFormattedDate());
            startDateTextView.setText(cellText);

            if (DriveManager.getInstance(context).isTrackingDrive(drive)) {
                startDateTextView.setBackgroundColor(Color.GREEN);
            } else {
                startDateTextView.setBackgroundResource(R.drawable.background_activated);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_drive_list_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_drive:
                Intent i = new Intent(getActivity(), DriveActivity.class);
                startActivityForResult(i, REQUEST_NEW_DRIVE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_NEW_DRIVE == requestCode) {
            getLoaderManager().restartLoader(LOAD_DRIVES, null, mLoaderCallbacks);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mActionMode == null) {
            Intent i = new Intent(getActivity(), DriveActivity.class);
            i.putExtra(DriveFragment.EXTRA_DRIVE_ID, id);
            startActivity(i);
        }
    }

    private static class DriveListCursorLoader extends SQLiteCursorLoader {

        public DriveListCursorLoader(Context context) {
            super(context);
        }

        @Override
        protected Cursor loadCursor() {
            return DriveManager.getInstance(getContext()).queryDrives();
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new DriveListCursorLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            DriveCursorAdapter adapter = new DriveCursorAdapter(getActivity(), (DriveDatabaseHelper.DriveCursor) data);
            setListAdapter(adapter);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            setListAdapter(null);
        }
    };
}