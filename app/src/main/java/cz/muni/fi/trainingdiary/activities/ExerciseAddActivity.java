package cz.muni.fi.trainingdiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import cz.muni.fi.trainingdiary.R;
import cz.muni.fi.trainingdiary.database.DatabaseHelper;
import cz.muni.fi.trainingdiary.entities.Category;
import cz.muni.fi.trainingdiary.entities.Exercise;


public class ExerciseAddActivity extends ActionBarActivity {
    public static final String EXTRA_MESSAGE_EXERCISE = "cz.muni.fi.trainingdiary.activities.MESSAGE_EXERCISE";
    private DatabaseHelper dbHelper;
    private Set<Category> pickedCategories = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_add);

        dbHelper = new DatabaseHelper(this);
        setupKeyboardDissapearOutsideEditText(findViewById(R.id.activity_add_exercise));
    }

    private void setupKeyboardDissapearOutsideEditText(View view) {
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Activity activity = ExerciseAddActivity.this;
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
        getMenuInflater().inflate(R.menu.menu_exercise_add, menu);
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

    public void addExercise(View view){

        EditText exerciseNameForm = (EditText) findViewById(R.id.exercise_name_value);
        EditText exerciseDescriptionForm = (EditText) findViewById(R.id.exercise_description_value);
//        Category exerciseCategory = Category.getStringResourceCategory(this).get(exerciseCategorySpinner.getSelectedItem());

        String exerciseName = exerciseNameForm.getText().toString();
        String exerciseDescription = exerciseDescriptionForm.getText().toString();

        if(exerciseName.trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.toast_exercise_choose_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if(pickedCategories.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.toast_exercise_choose_categories, Toast.LENGTH_SHORT).show();
            return;
        }


        Exercise exercise = new Exercise(exerciseName,exerciseDescription);
//        exercise.getCategories().add(exerciseCategory);
        exercise.setCategories(pickedCategories);


        dbHelper.createExerciseDetail(exercise);

        Toast.makeText(getApplicationContext(), R.string.toast_exercise_added, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    public void chooseCategories(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.check_box_WHOLE_BODY :
                if (checked)
                    pickedCategories.add(Category.WHOLE_BODY);
                else
                    pickedCategories.remove(Category.WHOLE_BODY);
                break;
            case R.id.check_box_BACK :
                if(checked)
                    pickedCategories.add(Category.BACK);
                else
                    pickedCategories.remove(Category.BACK);
                break;
            case R.id.check_box_ABDOMEN :
                if(checked)
                    pickedCategories.add(Category.ABDOMEN);
                else
                    pickedCategories.remove(Category.ABDOMEN);
                break;
            case R.id.check_box_HANDS :
                if(checked)
                    pickedCategories.add(Category.HANDS);
                else
                    pickedCategories.remove(Category.HANDS);
                break;
            case R.id.check_box_LEGS :
                if(checked)
                    pickedCategories.add(Category.LEGS);
                else
                    pickedCategories.remove(Category.LEGS);
                break;
        }
    }
}
