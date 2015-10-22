package cz.muni.fi.trainingdiary.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.muni.fi.trainingdiary.R;
import cz.muni.fi.trainingdiary.androidcomponent.NumberPickerDialog;
import cz.muni.fi.trainingdiary.androidcomponent.SpinnerAdapterWithHint;
import cz.muni.fi.trainingdiary.database.DatabaseHelper;
import cz.muni.fi.trainingdiary.entities.ExeciseSet;
import cz.muni.fi.trainingdiary.entities.Exercise;
import cz.muni.fi.trainingdiary.entities.WorkoutPlan;


public class WorkoutPlanAddActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener, NumberPickerDialog.NoticeDialogListener{
    public static final int ID_PREFIX_COUNT_EXERCISE = 100;
    public static final String EXTRA_MESSAGE_WORKOUT_PLAN = "cz.muni.fi.trainingdiary.MESSAGE_WORKOUT_PLAN";
    private DatabaseHelper dbHelper;
    private WorkoutPlan workoutPlan;
    private int indexOfChangedExercise = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_plan_add);
        dbHelper = new DatabaseHelper(this);



        workoutPlan = new WorkoutPlan();
        setupKeyboardDissapearOutsideEditText(findViewById(R.id.activity_add_workout_plan));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    private void setupKeyboardDissapearOutsideEditText(View view) {
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Activity activity = WorkoutPlanAddActivity.this;
                    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                    return false;
                }
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupKeyboardDissapearOutsideEditText(innerView);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workout_plan_add, menu);
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

    public void addExercise(View view) {
        /*TableLayout layout = (TableLayout) findViewById(R.id.activity_add_workout_plan);
        Button button = (Button) findViewById(R.id.add_exercise_button);
        TableRow row = (TableRow) button.getParent();
        int index = ((ViewGroup) row.getParent()).indexOfChild(row);
        button.setVisibility(View.GONE);
        TableRow newRow = new TableRow(this);

        newRow.addView(exerciseSpinner);
        layout.addView(newRow, index);*/



        Intent intent = new Intent(this, ExerciseViewAll.class);
        final int result = 1;
        startActivityForResult(intent, result);



        //addTextViewForExercise(name);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {

            int exerciseID = data.getIntExtra("exerciseID", 0);
            String exerciseName = data.getStringExtra("exerciseName");

            //Toast.makeText(getApplicationContext(), "id = "+String.valueOf(exerciseID), Toast.LENGTH_SHORT).show();

            addTextViewForExercise(exerciseID, exerciseName);
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void addTextViewForExercise(int exerciseID, String exerciseName){
        TableLayout layout = (TableLayout) findViewById(R.id.activity_add_workout_plan);
        Button btnAddExercise = (Button) findViewById(R.id.add_exercise_button);
        ViewGroup tableRow = (ViewGroup) btnAddExercise.getParent();
        int index = ((ViewGroup) tableRow.getParent()).indexOfChild(tableRow);
        ViewGroup.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);

        workoutPlan.getExerciseIds().add(exerciseID); //Exercise.getExerciseByName(availableExercises, name).getId());
        workoutPlan.getExerciseCounts().add(3);

        final TableRow row = new TableRow(this);

        TextView textView = new TextView(this);
        textView.setText(exerciseName);
        textView.setLayoutParams(params);
        row.addView(textView);


        Button numberButton = new Button(this, null, R.style.Button);
        numberButton.setTag(workoutPlan.getExerciseIds().size() - 1);
        numberButton.setId(ID_PREFIX_COUNT_EXERCISE + workoutPlan.getExerciseCounts().size() - 1);
        numberButton.setText(ExeciseSet.getLabelForSet(getApplicationContext().getResources(), 3));

        numberButton.setLayoutParams(params);
        numberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indexOfChangedExercise = (int) view.getTag();
                showNumberPicker();
            }
        });
        row.addView(numberButton);

        Button deleteButton = new Button(this, null, R.style.Button);
        deleteButton.setPadding(0, 10, 0, 10);
        deleteButton.setText(getResources().getText(R.string.delete_button));
        deleteButton.setLayoutParams(params);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup rowToDelete = (ViewGroup) view.getParent();
                ViewGroup parent = (ViewGroup) rowToDelete.getParent();
                parent.removeView(rowToDelete);
                parent.invalidate();
            }
        });
        row.addView(deleteButton);


        layout.addView(row, index);
    }

    public void showNumberPicker() {
        DialogFragment dialog = new NumberPickerDialog();
        dialog.show(getFragmentManager(), "");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int number) {
        workoutPlan.getExerciseCounts().set(indexOfChangedExercise, number);
        Button btn = (Button) findViewById(ID_PREFIX_COUNT_EXERCISE + indexOfChangedExercise);
        btn.setText(ExeciseSet.getLabelForSet(getApplicationContext().getResources(), number));
    }

    public void saveWorkoutPlan(View view){
        String workoutName = ((EditText) findViewById(R.id.workout_plan_name)).getText().toString();

        if(workoutName.trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.toast_workout_plan_choose_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if(workoutPlan.getExerciseIds().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.toast_workout_plan_add_exercise, Toast.LENGTH_SHORT).show();
            return;
        }

        workoutPlan.setName(workoutName);
        dbHelper.createWorkoutPlan(workoutPlan);
        Toast.makeText(getApplicationContext(), R.string.toast_workout_plan_added, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
