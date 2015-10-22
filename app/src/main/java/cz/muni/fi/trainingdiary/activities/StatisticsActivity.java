package cz.muni.fi.trainingdiary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import cz.muni.fi.trainingdiary.R;
import cz.muni.fi.trainingdiary.database.DatabaseHelper;
import cz.muni.fi.trainingdiary.entities.Exercise;
import cz.muni.fi.trainingdiary.entities.WorkoutPlan;

public class StatisticsActivity extends ActionBarActivity {
    DatabaseHelper dbHelper;
    Spinner spinnerWorkoutPlans;
    List<WorkoutPlan> workoutPlans;
    ArrayAdapter<WorkoutPlan> adapter;
    DataPoint dataPoint;
    GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        dbHelper = new DatabaseHelper(this);

        workoutPlans = dbHelper.getAllWorkoutPlans();

        graph = (GraphView) findViewById(R.id.graph);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext(),
                new SimpleDateFormat("d.M.yyyy")));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 3 because of the space

        // set manual X, Y bounds
        /*
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        */




        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, workoutPlans);

        spinnerWorkoutPlans = (Spinner) findViewById(R.id.spinner_workout_plans);
        spinnerWorkoutPlans.setAdapter(adapter);
        spinnerWorkoutPlans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateGraph();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    private void updateGraph() {

        graph.removeAllSeries();

        // TODO
        Map<Date, Integer> datePowerMap = dbHelper.getStats(((WorkoutPlan) spinnerWorkoutPlans.getSelectedItem()).getId());



        /*
        Map<Date, Integer> datePowerMap = new TreeMap<>();
        */
        Calendar calendar = Calendar.getInstance();
        /*
        for(int i = 0; i < 10; i++) {
            Date date = calendar.getTime();
            calendar.add(Calendar.DATE, randInt(1, 10));
            datePowerMap.put(date, randInt(5000,20000));
        }
        */

        if(!datePowerMap.isEmpty()) {

            DataPoint[] dataPoint = new DataPoint[datePowerMap.size()];
            int i = 0;
            Date date; //, minX = null, maxX = null;
            Integer value; //, minY = null, maxY = null;


            for (Map.Entry<Date, Integer> entry : datePowerMap.entrySet()) {

                date = entry.getKey();
                value = entry.getValue();
                /*
                Log.i("statistika", "("+i+") datum="+date+" hodnota="+value);

                calendar.add(Calendar.SECOND, 10);
                date = calendar.getTime();

                Log.i("statistika", "("+i+") datum="+date+" hodnota="+value);
                */
                //Log.i("Statistika", "Datum=+"+date+" Value="+value);

                /*
                // init min max
                if(i == 0) {
                    minX = maxX = date;
                    minY = maxY = value;
                }

                if(minX.before(date)) minX = date;
                if(maxX.after(date)) maxX = date;
                if(minY.intValue() < value.intValue()) minY = value;
                if(maxY.intValue() > value.intValue()) maxY = value;
                */

                dataPoint[i] = new DataPoint(date, value);
                i++;
            }

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoint);
            /*
            // set manual Y bounds
            graph.getViewport().setMinX((double) minX.getTime());
            graph.getViewport().setMaxX((double) maxX.getTime());

            // set manual Y bounds
            graph.getViewport().setMinY((double) minY.intValue());
            graph.getViewport().setMaxY((double) maxY.intValue());
            */
            graph.addSeries(series);
        }

    }
}
