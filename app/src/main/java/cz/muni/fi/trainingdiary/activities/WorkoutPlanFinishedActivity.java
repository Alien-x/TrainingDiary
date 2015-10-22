package cz.muni.fi.trainingdiary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.muni.fi.trainingdiary.R;
import cz.muni.fi.trainingdiary.database.DatabaseHelper;
import cz.muni.fi.trainingdiary.entities.ExeciseSet;
import cz.muni.fi.trainingdiary.entities.Exercise;
import cz.muni.fi.trainingdiary.entities.WorkoutPlan;

public class WorkoutPlanFinishedActivity extends ActionBarActivity {
    DatabaseHelper dbHelper;
    Intent parentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_plan_finished);

        dbHelper = new DatabaseHelper(this);

        parentActivity = getIntent();
        int workoutPlanID = parentActivity.getIntExtra(WorkoutPlanAddActivity.EXTRA_MESSAGE_WORKOUT_PLAN, -1);
        ((TextView) findViewById(R.id.txt_percentage)).setText(dbHelper.getStatDifference(workoutPlanID) + "%");

    }


    public void onClickReturnMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
