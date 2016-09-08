package com.mad.exercise6.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.mad.exercise6.R;
import com.mad.exercise6.adapter.TrainAdapter;
import com.mad.exercise6.db.TrainDatabaseHelper;
import com.mad.exercise6.model.Train;

import java.util.ArrayList;
import java.util.Random;

/**
 * The main activity class which will display a list of trains and their current status
 * as outlined
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXCEPTION = "Exception";

    private TrainAdapter mTrainAdapter;
    private RecyclerView mTrainRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * onCreate method called on creation of the main activity
     * Sets up all of the layouts, populates the train data and sets
     * up the recycler view using that train data
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TrainDatabaseHelper.getInstance(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTrainIntent = new Intent(MainActivity.this, AddTrainActivity.class);
                MainActivity.this.startActivity(addTrainIntent);

            }
        });

        populateTrainList();
        mTrainRecyclerView = (RecyclerView) findViewById(R.id.main_train_info_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mTrainRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<Train> trainList = TrainDatabaseHelper.getInstance(getApplicationContext())
                .readAllTrains();
        mTrainAdapter = new TrainAdapter(this, trainList);
        mTrainRecyclerView.setAdapter(mTrainAdapter);

    }

    /**
     * Called when the activity is resumed and will update the UI of any changes that
     * happened to the database when away from this main activity
     */
    @Override
    protected void onResume() {
        super.onResume();

        mTrainAdapter.setNewTrainList(TrainDatabaseHelper.
                getInstance(getApplicationContext()).readAllTrains());
        mTrainAdapter.notifyDataSetChanged();

    }

    /**
     * Method that creates the options menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Checks to see if a menu item has been clicked
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_delete_all) {
            //Delete all of the trains from the database then read in the new list
            //to the adapter then notify of data change
            TrainDatabaseHelper.getInstance(getApplicationContext()).deleteAll();
            mTrainAdapter.setNewTrainList(TrainDatabaseHelper.
                    getInstance(getApplicationContext()).readAllTrains());
            mTrainAdapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.menu_refresh) {
            ProgressBar progressBar =
                    (ProgressBar) findViewById(R.id.main_train_info_loading_progress_bar);

            ArrayList<Train> trainList = TrainDatabaseHelper.getInstance(getApplicationContext())
                    .readAllTrains();

            new RefreshTrainsAsyncTask(progressBar).execute(trainList);

            return true;
        }

        //If the selection made was one of the menu items then return true
        if (id == R.id.menu_quit) {
            System.exit(0);
        }

        //If no menu options were selected then pass the item on to the parent
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to populate the data in the list of trains
     */
    private void populateTrainList() {
        TrainDatabaseHelper.getInstance(getApplicationContext()).addTrain(
                new Train("Albion Park Platform 1", 3,
                        getString(R.string.train_status_on_time), "Allawah", "14:11")
        );
        TrainDatabaseHelper.getInstance(getApplicationContext()).addTrain(
                new Train("Arncliffe Platform 2", 4,
                        getString(R.string.train_status_late), "Central", "14:34")
        );
        TrainDatabaseHelper.getInstance(getApplicationContext()).addTrain(
                new Train("Artarmon Platform 3", 7,
                        getString(R.string.train_status_on_time), "Ashfield", "15:01")
        );
        TrainDatabaseHelper.getInstance(getApplicationContext()).addTrain(
                new Train("Berowra Platform 4", 12,
                        getString(R.string.train_status_late), "Beverly", "15:18")
        );
    }

    /**
     * An async task that refreshes all of the train arrival times
     * An indeterminate progress bar is displayed on the screen
     */
    private class RefreshTrainsAsyncTask
            extends AsyncTask<ArrayList<Train>, Void, ArrayList<Train>> {

        private ProgressBar mProgressBar;

        /**
         * Constructor method that stores a reference to the progress bar view
         * @param progressBar
         */
        protected RefreshTrainsAsyncTask(ProgressBar progressBar) {
            mProgressBar = progressBar;
        }

        /**
         * Will set up and display the progress bar in the UI
         */
        @Override
        protected void onPreExecute() {
            mTrainRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Will change the train data to have the new arrival times
         * @param arrayLists
         * @return
         */
        @Override
        protected ArrayList<Train> doInBackground(ArrayList<Train>... arrayLists) {
            Random rand = new Random();
            int arrivalTime;
            ArrayList<Train> allTrains = new ArrayList<Train>();
            for (ArrayList<Train> trains : arrayLists) {
                for (Train train : trains) {
                    arrivalTime = rand.nextInt(20) + 1;
                    train.setArrivalTime(arrivalTime);
                    TrainDatabaseHelper.getInstance(getApplicationContext()).updateTrain(train);
                    allTrains.add(train);
                }
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.d(EXCEPTION, "Interrupted Exception thrown when thread was sleeping");
            }
            return allTrains;
        }

        /**
         * Make the progress bar invisible again and the recycler view visible
         */
        @Override
        protected void onPostExecute(ArrayList<Train> trains) {
            mTrainAdapter.setNewTrainList(trains);
            mTrainAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
            mTrainRecyclerView.setVisibility(View.VISIBLE);
        }


    }


}
