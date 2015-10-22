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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.muni.fi.trainingdiary.R;
import cz.muni.fi.trainingdiary.database.DatabaseHelper;
import cz.muni.fi.trainingdiary.entities.WorkoutPlan;


public class WorkoutPlanViewAll extends ActionBarActivity {

    DatabaseHelper dbHelper;

    List<WorkoutPlan> workoutPlans;
    ListView workoutPlansListView;
    ArrayAdapter<WorkoutPlan> workoutPlansAdapter;
    int longClickedWorkoutPlanIndex;

    private static final int contextMenu_DELETE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_plan_view_all);
        dbHelper = new DatabaseHelper(this);


        workoutPlansListView = (ListView) findViewById(R.id.listView_workoutPlan);
        registerForContextMenu(workoutPlansListView);
        workoutPlansListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickedWorkoutPlanIndex = position;
                return false;
            }
        });
        workoutPlansListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int workoutPlanID =  workoutPlans.get(position).getId();
                Intent intent = new Intent(WorkoutPlanViewAll.this, WorkoutPlanDetailActivity.class);
                intent.putExtra(WorkoutPlanAddActivity.EXTRA_MESSAGE_WORKOUT_PLAN, workoutPlanID);
                startActivity(intent);
            }
        });

        populateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workout_plan_view_all, menu);
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
        workoutPlans = dbHelper.getAllWorkoutPlans();
        workoutPlansAdapter = new WorkoutPlansListAdapter();
        workoutPlansListView.setAdapter(workoutPlansAdapter);
    }

    private class WorkoutPlansListAdapter extends ArrayAdapter<WorkoutPlan> {
        public WorkoutPlansListAdapter() {
            super(WorkoutPlanViewAll.this, R.layout.listview_workout_plan, workoutPlans);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.listview_workout_plan, parent, false);

            WorkoutPlan currentWorkoutPlan = dbHelper.getWorkoutPlan(workoutPlans.get(position).getId());

            TextView name = (TextView) convertView.findViewById(R.id.txtWorkoutPlanName);
            name.setText(currentWorkoutPlan.getName());

            TextView count = (TextView) convertView.findViewById(R.id.txtWorkoutPlanExerciseCount);
            count.setText(String.valueOf(currentWorkoutPlan.getExerciseIds().size()));

            return convertView;
        }
    }

    public void addWorkoutPlan(View view) {
        Intent intent = new Intent(this, WorkoutPlanAddActivity.class);
        final int result = 1;
        startActivityForResult(intent, result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            populateList();
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

                dbHelper.deleteWorkoutPlan(workoutPlans.get(longClickedWorkoutPlanIndex).getId());
                Toast.makeText(getApplicationContext(), R.string.toast_workout_plan_deleted, Toast.LENGTH_SHORT).show();
                workoutPlans.remove(longClickedWorkoutPlanIndex);
                workoutPlansAdapter.notifyDataSetChanged();
                break;
        }

        return super.onContextItemSelected(item);
    }

}
