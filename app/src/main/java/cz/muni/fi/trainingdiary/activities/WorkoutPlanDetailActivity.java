package cz.muni.fi.trainingdiary.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cz.muni.fi.trainingdiary.R;
import cz.muni.fi.trainingdiary.database.DatabaseHelper;
import cz.muni.fi.trainingdiary.entities.ExeciseSet;
import cz.muni.fi.trainingdiary.entities.Exercise;
import cz.muni.fi.trainingdiary.entities.WorkoutPlan;

public class WorkoutPlanDetailActivity extends ActionBarActivity {
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_workout_plan_detail);

        final int id = intent.getIntExtra(WorkoutPlanAddActivity.EXTRA_MESSAGE_WORKOUT_PLAN, -1);

        dbHelper = new DatabaseHelper(this);

        final List<Exercise> exercises = dbHelper.getAllExerciseDetails();
        TableLayout layout = (TableLayout) findViewById(R.id.activity_detail_workout_plan);
        ViewGroup.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);

        final WorkoutPlan workoutPlan = dbHelper.getWorkoutPlan(id);

        ((TextView) findViewById(R.id.txt_workout_plan_name)).setText(workoutPlan.getName());

        for (int i = 0; i < workoutPlan.getExerciseIds().size(); i++) {
            Exercise exercise = Exercise.getById(exercises, workoutPlan.getExerciseIds().get(i));
            TableRow row = new TableRow(this);

            TextView viewName = new TextView(this);
            viewName.setText(exercise.getName());
            viewName.setLayoutParams(params);
            row.addView(viewName);

            TextView viewCount = new TextView(this);
            int exerciseCounts = workoutPlan.getExerciseCounts().get(i);
            String setsLabel = ExeciseSet.getLabelForSet(getApplicationContext().getResources(), exerciseCounts);

            viewCount.setText("     " + setsLabel);
            viewCount.setLayoutParams(params);
            row.addView(viewCount);

            layout.addView(row);
        }


        Button startButton = (Button) findViewById(R.id.btn_start_workout_plan);
        final Context context = this;
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WorkoutPlanStartActivity.class);
                intent.putExtra(WorkoutPlanAddActivity.EXTRA_MESSAGE_WORKOUT_PLAN, id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workout_plan_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
