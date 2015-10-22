package cz.muni.fi.trainingdiary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.muni.fi.trainingdiary.R;
import cz.muni.fi.trainingdiary.database.DatabaseHelper;
import cz.muni.fi.trainingdiary.entities.Category;
import cz.muni.fi.trainingdiary.entities.Exercise;
import cz.muni.fi.trainingdiary.entities.WorkoutPlan;

public class ExerciseViewAll extends ActionBarActivity {

    DatabaseHelper dbHelper;

    List<Exercise> exercisesAll, exercisesFilter;
    ListView exercisesListView;
    int longClickedExerciseIndex;

    ArrayAdapter<Exercise> exerciseAdapter;
    private static final int contextMenu_DELETE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_view_all);

        dbHelper = new DatabaseHelper(this);

        exercisesListView = (ListView) findViewById(R.id.listView_exercises);
        registerForContextMenu(exercisesListView);
        exercisesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickedExerciseIndex = position;
                return false;
            }
        });
        exercisesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent parentActivity = getIntent();
                parentActivity.putExtra("exerciseID", exercisesFilter.get(position).getId());
                parentActivity.putExtra("exerciseName", exercisesFilter.get(position).getName());
                setResult(RESULT_OK, parentActivity);
                finish();

            }
        });


        exercisesAll = dbHelper.getAllExerciseDetails();
        exercisesFilter = new ArrayList<>(exercisesAll);
        populateList();

        // set search
        setSearchListener();
    }


    private void setSearchListener() {
        SearchView searchView = (SearchView) findViewById(R.id.searchView_filter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterExercises(null);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterExercises(null);
                return true;
            }

            public void callSearch(String query) {
                //Do searching
            }

        });
        searchView.clearFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exercise_view_all, menu);
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

    private void populateList() {
        exerciseAdapter = new ExercisesListAdapter();
        exercisesListView.setAdapter(exerciseAdapter);
    }

    private class ExercisesListAdapter extends ArrayAdapter<Exercise> {
        public ExercisesListAdapter() {
            super(ExerciseViewAll.this, R.layout.listview_exercise, exercisesFilter);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.listview_exercise, parent, false);

            Exercise currentExercise = exercisesFilter.get(position);

            TextView name = (TextView) convertView.findViewById(R.id.txtExerciseName);
            name.setText(currentExercise.getName());

            TextView description = (TextView) convertView.findViewById(R.id.txtExerciseDescription);
            description.setText(currentExercise.getDescription());

            TextView categories = (TextView) convertView.findViewById(R.id.txtExerciseCategories);
            categories.setText(currentExercise.getCategoriesString(this.getContext()));

            return convertView;
        }
    }

    public void filterExercises(View view) {

        List<Category> categories = new ArrayList<>();
        List<Exercise> exercisesTmp;

        if(((CheckBox) findViewById(R.id.check_box_WHOLE_BODY)).isChecked())
            categories.add(Category.WHOLE_BODY);
        if(((CheckBox) findViewById(R.id.check_box_BACK)).isChecked())
            categories.add(Category.BACK);
        if(((CheckBox) findViewById(R.id.check_box_ABDOMEN)).isChecked())
            categories.add(Category.ABDOMEN);
        if(((CheckBox) findViewById(R.id.check_box_HANDS)).isChecked())
            categories.add(Category.HANDS);
        if(((CheckBox) findViewById(R.id.check_box_LEGS)).isChecked())
            categories.add(Category.LEGS);

        // filter categories
        if(categories.size() > 0) {
            exercisesTmp = new ArrayList<>();

            for (Exercise exercise : exercisesAll) {
                if (exercise.isInCategories(categories))
                    exercisesTmp.add(exercise);
            }
        }
        // show all
        else {
            exercisesTmp = new ArrayList<>(exercisesAll);
        }

        // search filter
        String filter = String.valueOf(((SearchView) findViewById(R.id.searchView_filter)).getQuery());
        if(!filter.isEmpty()) {
            List<Exercise> exercisesSearchFilter = new ArrayList<>();

            for(Exercise exercise : exercisesTmp) {
                if(exercise.searchFor(filter))
                    exercisesSearchFilter.add(exercise);
            }

            exercisesFilter = exercisesSearchFilter;
        }
        else {
            exercisesFilter = exercisesTmp;
        }

        populateList();
    }

    public void addExercise(View view) {
        Intent intent = new Intent(this, ExerciseAddActivity.class);
        final int result = 1;
        startActivityForResult(intent, result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            exercisesAll = dbHelper.getAllExerciseDetails();
            populateList();
            filterExercises(null);
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.setHeaderIcon(R.drawable.icon_delete);
        menu.setHeaderTitle(R.string.menu_action);

        menu.add(Menu.NONE, contextMenu_DELETE, menu.NONE, R.string.menu_delete);
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case contextMenu_DELETE:
                dbHelper.deleteExerciseDetail(exercisesFilter.get(longClickedExerciseIndex).getId());
                Toast.makeText(getApplicationContext(), R.string.toast_exercise_deleted, Toast.LENGTH_SHORT).show();
                exercisesAll = dbHelper.getAllExerciseDetails();
                populateList();
                filterExercises(null);
                break;
        }

        return super.onContextItemSelected(item);
    }

}
