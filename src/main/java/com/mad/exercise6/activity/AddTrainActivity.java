package com.mad.exercise6.activity;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mad.exercise6.R;
import com.mad.exercise6.db.TrainDatabaseHelper;
import com.mad.exercise6.model.Train;

import java.util.ArrayList;
import java.util.List;

public class AddTrainActivity extends AppCompatActivity {

    private EditText mPlatformEditText;
    private EditText mArrivalTimeEditText;
    private Spinner mStatusSpinner;
    private EditText mDestinationEditText;
    private EditText mDestinationTimeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_train);

        mPlatformEditText = (EditText) findViewById(R.id.add_train_platform_et);
        mArrivalTimeEditText = (EditText) findViewById(R.id.add_train_arrival_time_et);
        mStatusSpinner = (Spinner) findViewById(R.id.add_train_status_spinner);
        mDestinationEditText = (EditText) findViewById(R.id.add_train_destination_et);
        mDestinationTimeEditText = (EditText) findViewById(R.id.add_train_destination_time_et);

        Resources res = getResources();
        String[] statusOptions = res.getStringArray(R.array.arrival_status_labels);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, statusOptions);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStatusSpinner.setAdapter(adapter);

    }

    public void onClickAddTrainCancel(View view) {
        this.finish();
    }

    public void onClickAddTrain(View view) {
        try {
            Train train = new Train(mPlatformEditText.getText().toString(),
                Integer.parseInt(mArrivalTimeEditText.getText().toString()),
                mStatusSpinner.getSelectedItem().toString(),
                mDestinationEditText.getText().toString(),
                mDestinationTimeEditText.getText().toString());
            TrainDatabaseHelper.getInstance(getApplicationContext()).addTrain(train);
        } catch (IllegalStateException e) {
            Toast.makeText(AddTrainActivity.this, "Invalid Train", Toast.LENGTH_LONG).show();
        }
        this.finish();
    }
}
