package com.mad.exercise6.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mad.exercise6.R;
import com.mad.exercise6.activity.MainActivity;
import com.mad.exercise6.db.TrainDatabaseHelper;
import com.mad.exercise6.model.Train;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Andrew on 7/09/2016.
 * Train Adapter class used to load data from the model into recycler view
 */
public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.ViewHolder> {

    private ArrayList<Train> mTrains;
    private Context mContext;

    /**
     * Constructor method that stores a reference to the data and the main activity context
     * @param context
     * @param trains
     */
    public TrainAdapter(Context context, ArrayList<Train> trains) {
        mTrains = trains;
        mContext = context;
    }

    /**
     * A view holder class to find and maintain the text views in a single train item
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView timeToArrivalTextView;
        public TextView platformNameTextView;
        public TextView platformArrivalTimeTextView;
        public TextView platformArrivalStatusTextView;
        public TextView destinationArrivalTimeTextView;
        public TextView destinationNameTextView;
        public ProgressBar arrivalTimeRefreshProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            timeToArrivalTextView = (TextView) itemView.findViewById(R.id.train_item_time_info_tv);
            platformNameTextView =
                    (TextView) itemView.findViewById(R.id.train_item_platform_name_tv);
            platformArrivalTimeTextView =
                    (TextView) itemView.findViewById(R.id.train_item_platform_arrival_time_tv);
            platformArrivalStatusTextView =
                    (TextView) itemView.findViewById(R.id.train_item_platform_arrival_status_tv);
            destinationArrivalTimeTextView =
                    (TextView) itemView.findViewById(R.id.train_item_destination_time_tv);
            destinationNameTextView =
                    (TextView) itemView.findViewById(R.id.train_item_destination_name_tv);
            arrivalTimeRefreshProgressBar =
                    (ProgressBar) itemView.findViewById(R.id.train_item_time_info_progress);

        }

    }

    /**
     * Will inflate a new view from the train item layout with the ViewHolder text view references
     * @param parent
     * @param viewType
     * @return A new ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.train_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * Will attach the information about the train to the train ViewHolder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Train currentTrain = mTrains.get(position);
        holder.timeToArrivalTextView.setText(currentTrain.getArrivalTime() + "\n" +
                mContext.getString(R.string.minute));
        holder.platformNameTextView.setText(currentTrain.getPlatform());
        holder.platformArrivalStatusTextView.setText(currentTrain.getStatus());
        if (currentTrain.getStatus().equals(mContext.getString(R.string.train_status_on_time))) {
            holder.platformArrivalStatusTextView.setTextColor
                    (ContextCompat.getColor(mContext, R.color.train_status_on_time_color));
        } else if (currentTrain.getStatus().
                equals(mContext.getString(R.string.train_status_late))) {
            holder.platformArrivalStatusTextView.setTextColor
                    (ContextCompat.getColor(mContext, R.color.train_status_late_color));
        }
        holder.destinationArrivalTimeTextView.setText(currentTrain.getDestinationTime());
        holder.destinationNameTextView.setText(currentTrain.getDestination());

        //Sets the on click listener for the arrival time box
        holder.timeToArrivalTextView.setOnClickListener(new RefreshOnClickListener(
                holder.arrivalTimeRefreshProgressBar, holder.timeToArrivalTextView,
                position, holder.destinationNameTextView));
    }

    /**
     * The number of trains in the array
     * @return
     */
    @Override
    public int getItemCount() {
        return mTrains.size();
    }

    /**
     * A Async Task class to refresh the arrival time of a single train
     */
    private class RefreshSingleTrainAsyncTask extends AsyncTask<Train, Void, Train> {

        private ProgressBar mProgressBar;
        private TextView mTrainTimeTextView;

        /**
         * A constructor to store a reference to a progress bar and the text view of the
         * arrival time of the train
         * @param progressBar
         * @param trainTime
         */
        protected RefreshSingleTrainAsyncTask(ProgressBar progressBar, TextView trainTime) {
            mProgressBar = progressBar;
            mTrainTimeTextView = trainTime;
        }

        /**
         * Makes the progress bar visible in the train arrival time box
         */
        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mTrainTimeTextView.setVisibility(View.GONE);
        }

        /**
         * Will change the individual trains arrival time
         * @param trains
         * @return
         */
        @Override
        protected Train doInBackground(Train... trains) {
            Random rand = new Random();
            Train updateTrain = null;
            for(Train train : trains) {
                train.setArrivalTime(rand.nextInt(20) + 1);
                updateTrain = train;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.d(MainActivity.EXCEPTION, "Interrupted Exception thrown when thread was sleeping");
            }
            return updateTrain;
        }

        /**
         * Will notify of change in model and set the arrival time to be visible again
         * @param updateTrain
         */
        @Override
        protected void onPostExecute(Train updateTrain) {
            notifyDataSetChanged();
            TrainDatabaseHelper.getInstance(mContext).updateTrain(updateTrain);
            mProgressBar.setVisibility(View.GONE);
            mTrainTimeTextView.setVisibility(View.VISIBLE);
        }


    }

    /**
     * A custom on click listener class that takes the progress bar and the text view of a
     * particular train that was clicked on and then calls the async refresh task
     */
    private class RefreshOnClickListener implements View.OnClickListener {

        private ProgressBar mArrivalTimeProgressBar;
        private TextView mArrivalTimeTextView;
        private TextView mDestinationNameTextView;
        private int mPosition;

        protected RefreshOnClickListener(ProgressBar progressBar, TextView textView,
                                         int position, TextView destinationNameTextView) {
            mArrivalTimeProgressBar = progressBar;
            mArrivalTimeTextView = textView;
            mPosition = position;
            mDestinationNameTextView = destinationNameTextView;
        }

        /**
         * An on click method that updates the train arrival time and displays a toast message
         * informing the user which train is being refreshed
         * @param view
         */
        @Override
        public void onClick(View view) {
            new RefreshSingleTrainAsyncTask(mArrivalTimeProgressBar, mArrivalTimeTextView)
                    .execute(mTrains.get(mPosition));
            Toast.makeText(mContext, mContext.getString(R.string.refreshing) +
                    " " + mDestinationNameTextView.getText().toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Replaces the current train list with a given list of trains
     * @param trains
     */
    public void setNewTrainList(ArrayList<Train> trains) {
        mTrains = trains;
    }

}
