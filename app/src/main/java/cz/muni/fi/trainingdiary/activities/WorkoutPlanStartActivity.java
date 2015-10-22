package cz.muni.fi.trainingdiary.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import cz.muni.fi.trainingdiary.R;
import cz.muni.fi.trainingdiary.database.DatabaseHelper;
import cz.muni.fi.trainingdiary.entities.ExeciseSet;
import cz.muni.fi.trainingdiary.entities.Exercise;
import cz.muni.fi.trainingdiary.entities.WorkoutPlan;

public class WorkoutPlanStartActivity extends ActionBarActivity {
    DatabaseHelper dbHelper;
    Intent parentActivity;
    Chronometer stopWatch;
    Button stopWatchButton;
    boolean stopWatchRunning = false;

    WorkoutPlan workoutPlan;

    List<Exercise> exercises;
    ArrayAdapter<ExeciseSet> setsAdapter;

    int workoutPlanID,
        currentExerciseIndex = 0,
        exercisesCount;

    TextView txt_exerciseName,
             txt_exerciseCurrent;
    ListView listView_sets;
    ImageButton btn_exerciseNext,
            btn_exercisePrev;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_plan_start);

        dbHelper = new DatabaseHelper(this);

        parentActivity = getIntent();
        workoutPlanID = parentActivity.getIntExtra(WorkoutPlanAddActivity.EXTRA_MESSAGE_WORKOUT_PLAN, -1);
        workoutPlan = dbHelper.getWorkoutPlan(workoutPlanID);
        exercisesCount = workoutPlan.getExerciseIds().size();

        // exercises and sets
        exercises = new ArrayList<>();
        for(int i = 0; i < workoutPlan.getExerciseIds().size(); i++) {
            Exercise exercise = dbHelper.getExerciseDetail(workoutPlan.getExerciseIds().get(i));

            List<ExeciseSet> sets = new ArrayList<>();

            for(int j = 0; j < workoutPlan.getExerciseCounts().get(i); j++) {
                sets.add(new ExeciseSet());
            }

            exercise.setAllSets(sets);

            exercises.add(exercise);
        }


        stopWatch = (Chronometer) findViewById(R.id.chronometer);
        stopWatchButton = (Button) findViewById(R.id.btn_chronometer);

        txt_exerciseName = (TextView) findViewById(R.id.txt_exercise_name);
        txt_exerciseCurrent = (TextView) findViewById(R.id.txt_exercise_current);
        ((TextView) findViewById(R.id.txt_exercise_count)).setText(String.valueOf(exercisesCount));

        listView_sets = (ListView) findViewById(R.id.listView_sets);

        btn_exercisePrev = (ImageButton) findViewById(R.id.btn_exercise_prev);
        btn_exerciseNext = (ImageButton) findViewById(R.id.btn_exercise_next);

        // init ui
        redrawUI();
    }

    private void redrawUI() {

        // buttons
        if(currentExerciseIndex == 0) {
            btn_exercisePrev.setEnabled(false);
            btn_exercisePrev.setBackgroundResource(R.drawable.icon_backward_disabled);
        }
        else {
            btn_exercisePrev.setEnabled(true);
            btn_exercisePrev.setBackgroundResource(R.drawable.icon_backward);
        }

        if(currentExerciseIndex == exercisesCount-1)
            btn_exerciseNext.setBackgroundResource(R.drawable.icon_ok);
        else
            btn_exerciseNext.setBackgroundResource(R.drawable.icon_forward);

        // exercise name
        txt_exerciseName.setText(exercises.get(currentExerciseIndex).getName());

        // list
        setsAdapter = new SetAdapter();
        listView_sets.setAdapter(setsAdapter);

        // paging
        txt_exerciseCurrent.setText(String.valueOf(currentExerciseIndex + 1));
    }

    private class SetAdapter extends ArrayAdapter<ExeciseSet> {
        public SetAdapter() {
            super(WorkoutPlanStartActivity.this, R.layout.listview_set, exercises.get(currentExerciseIndex).getAllSets());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.listview_set, parent, false);

            ExeciseSet execiseSet = exercises.get(currentExerciseIndex).getAllSets().get(position);
            ExeciseSet oldExeciseSet = dbHelper.getExerciseSetSet(exercises.get(currentExerciseIndex).getId(), position);


            ((TextView) convertView.findViewById(R.id.txt_set_number)).setText(String.valueOf(position + 1));

            EditText edit_weight = (EditText) convertView.findViewById(R.id.edit_weight);
            if(oldExeciseSet != null)
                edit_weight.setHint(String.valueOf(oldExeciseSet.getWeight()));
            else
                edit_weight.setHint("0");
            int weight = execiseSet.getWeight();
            if(weight != 0)
                edit_weight.setText(String.valueOf(weight));

            EditText edit_repetition = (EditText) convertView.findViewById(R.id.edit_repetition);
            if(oldExeciseSet != null)
                edit_repetition.setHint(String.valueOf(oldExeciseSet.getRepetition()));
            else
                edit_repetition.setHint("0");
            int repetition = execiseSet.getRepetition();
            if(repetition != 0)
                edit_repetition.setText(String.valueOf(repetition));

            return convertView;
        }
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

    public void onClickStopWatch(View view) {
        if(!stopWatchRunning) {
            stopWatch.setBase(SystemClock.elapsedRealtime());
            stopWatch.start();
            stopWatchButton.setText(R.string.btn_stopwatch_stop);
        }
        else {
            stopWatch.stop();
            stopWatchButton.setText(R.string.btn_stopwatch_start);
        }

        stopWatchRunning = !stopWatchRunning;
    }

    private void saveExerciseSets() {

        List<ExeciseSet> execiseSets = exercises.get(currentExerciseIndex).getAllSets();
        ExeciseSet exerciseSet;


        // update sets
        for (int position = 0; position < listView_sets.getChildCount(); position++) {
            View v = listView_sets.getChildAt(position);
            EditText edit_weight = (EditText) v.findViewById(R.id.edit_weight);
            EditText edit_repetition = (EditText) v.findViewById(R.id.edit_repetition);
            int weightValue = 0,
                repetitionValue = 0;
            try {
                weightValue = Integer.parseInt(edit_weight.getText().toString());
            }
            catch(Exception e){}

            try {
                repetitionValue = Integer.parseInt(edit_repetition.getText().toString());
            }
            catch(Exception e){}

            exerciseSet = execiseSets.get(position);
            exerciseSet.setWeight(weightValue);
            exerciseSet.setRepetition(repetitionValue);
        }

    }

    private boolean allExerciseSetsAreFilled() {

        // update sets
        for (int position = 0; position < listView_sets.getChildCount(); position++) {
            View v = listView_sets.getChildAt(position);
            EditText edit_weight = (EditText) v.findViewById(R.id.edit_weight);
            EditText edit_repetition = (EditText) v.findViewById(R.id.edit_repetition);
            try {
                Integer.parseInt(edit_weight.getText().toString());
                Integer.parseInt(edit_repetition.getText().toString());
            }
            catch(Exception e){
                return false;
            }
        }

        return true;
    }

    public void onClickExercisePrev(View view) {

            saveExerciseSets();

            if (currentExerciseIndex > 0) {
                currentExerciseIndex--;
                redrawUI();
            }
    }

    public void onClickExerciseNext(View view) {

        if(allExerciseSetsAreFilled()) {
            saveExerciseSets();

            if (currentExerciseIndex < exercisesCount - 1) {
                currentExerciseIndex++;
                redrawUI();
            }
            // finish
            else {
                dbHelper.saveExerciseSet(exercises);

                Intent intent = new Intent(WorkoutPlanStartActivity.this, WorkoutPlanFinishedActivity.class);
                intent.putExtra(WorkoutPlanAddActivity.EXTRA_MESSAGE_WORKOUT_PLAN, workoutPlanID);
                startActivity(intent);
            }
        }
        else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_exercise_sets_not_filled), Toast.LENGTH_SHORT).show();
        }
    }
}
