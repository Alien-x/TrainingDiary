package cz.muni.fi.trainingdiary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cz.muni.fi.trainingdiary.entities.Category;
import cz.muni.fi.trainingdiary.entities.ExeciseSet;
import cz.muni.fi.trainingdiary.entities.Exercise;
import cz.muni.fi.trainingdiary.entities.WorkoutPlan;

/**
 * Created by martina on 27.5.15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "trainingDiaryManager";

    private static final String TABLE_EXERCISE_DETAIL = "exerciseDetail";
    private static final String TABLE_EXERCISE_CATEGORY = "category";
    private static final String TABLE_EXERCISE_DETAIL_CATEGORY = "exerciseDetailCategory";
    private static final String TABLE_EXERCISE_SET_REPETITIONS = "exerciseSetRepetitions";
    private static final String TABLE_WORKOUT_PLAN = "workoutPlan";
    private static final String TABLE_WORKOUT_EXERCISE_ORDER = "workoutExerciseOrder";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_EXERCISE_CURRENT = "exerciseCurrent";
    private static final String KEY_EXERCISE_PREVOUS = "exercisePrevious";
    private static final String KEY_EXERCISE_NEXT = "exerciseNext";
    private static final String KEY_COUNT = "count";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_REPETITIONS = "repetitions";
    private static final String KEY_DATE = "date";

    public static final String CREATE_TABLE_EXERCISE_DETAIL = "CREATE TABLE " + TABLE_EXERCISE_DETAIL + "(" + KEY_ID +
            " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT, " + KEY_DESCRIPTION + " TEXT)";
    public static final String CREATE_TABLE_EXERCISE_CATEGORY = "CREATE TABLE " + TABLE_EXERCISE_CATEGORY + "(" + KEY_NAME +
            " TEXT PRIMARY KEY)";
    public static final String CREATE_TABLE_EXERCISE_DETAIL_CATEGORY = "CREATE TABLE " + TABLE_EXERCISE_DETAIL_CATEGORY +
            "(" + TABLE_EXERCISE_DETAIL + KEY_ID + " INTEGER, " + TABLE_EXERCISE_CATEGORY + KEY_NAME + " TEXT REFERENCES " + TABLE_EXERCISE_CATEGORY +
            ", PRIMARY KEY (" + TABLE_EXERCISE_DETAIL + KEY_ID  + "," + TABLE_EXERCISE_CATEGORY + KEY_NAME + "))";
    public static final String CREATE_TABLE_EXERCISE_SET_REPETITIONS = "CREATE TABLE " + TABLE_EXERCISE_SET_REPETITIONS + "(" + KEY_ID +
            " INTEGER PRIMARY KEY, " + TABLE_EXERCISE_DETAIL + KEY_ID + " INTEGER REFERENCES " + TABLE_EXERCISE_DETAIL +  ", " +
            KEY_WEIGHT + " INTEGER, " + KEY_REPETITIONS + " INTEGER, " + KEY_DATE + " DATETIME)";
    public static final String CREATE_TABLE_WORKOUT_PLAN = "CREATE TABLE " + TABLE_WORKOUT_PLAN + "(" + KEY_ID +
            " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT)";
    public static final String CREATE_TABLE_WORKOUT_EXERCISE_ORDER = "CREATE TABLE " + TABLE_WORKOUT_EXERCISE_ORDER + "(" + KEY_ID +
            " INTEGER PRIMARY KEY, " + TABLE_WORKOUT_PLAN + KEY_ID + " INTEGER REFERENCES " + TABLE_WORKOUT_PLAN + ", " + KEY_EXERCISE_CURRENT +
            " INTEGER REFERENCES " + TABLE_EXERCISE_DETAIL + ", " + KEY_EXERCISE_PREVOUS + " INTEGER REFERENCES " + TABLE_EXERCISE_DETAIL +
            ", " + KEY_EXERCISE_NEXT + " INTEGER REFERENCES " + TABLE_EXERCISE_DETAIL + "," + KEY_COUNT + " INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_EXERCISE_DETAIL);
        sqLiteDatabase.execSQL(CREATE_TABLE_EXERCISE_CATEGORY);
        populateExerciseCategory(sqLiteDatabase);
        sqLiteDatabase.execSQL(CREATE_TABLE_EXERCISE_DETAIL_CATEGORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_EXERCISE_SET_REPETITIONS);
        sqLiteDatabase.execSQL(CREATE_TABLE_WORKOUT_PLAN);
        sqLiteDatabase.execSQL(CREATE_TABLE_WORKOUT_EXERCISE_ORDER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_DETAIL);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_CATEGORY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_DETAIL_CATEGORY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_SET_REPETITIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT_PLAN);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT_EXERCISE_ORDER);
        onCreate(sqLiteDatabase);
    }

    public void closeDB(SQLiteDatabase db){
        if (db != null && db.isOpen())
            db.close();
    }

    public void refreshDatabase() {
        onUpgrade(this.getWritableDatabase(), 0, 0);
    }

    public void createExerciseDetail(Exercise exercise){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, exercise.getName());
        values.put(KEY_DESCRIPTION, exercise.getDescription());

        exercise.setId((int) db.insert(TABLE_EXERCISE_DETAIL, null, values));

        for (Category category : exercise.getCategories()) {
            createExerciseDetailCategory(exercise.getId(), category);
        }
    }

    public Exercise getExerciseDetail(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_EXERCISE_DETAIL + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();
        Exercise exercise = new Exercise();
        exercise.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        exercise.setName(c.getString(c.getColumnIndex(KEY_NAME)));
        exercise.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));

        selectQuery = "SELECT * FROM " + TABLE_EXERCISE_DETAIL_CATEGORY + " WHERE " + TABLE_EXERCISE_DETAIL + KEY_ID + " = " + exercise.getId();
        c.close();
        c = db.rawQuery(selectQuery, null);
        while (c.moveToNext()) {
            exercise.addCategory(Category.valueOf(c.getString(c.getColumnIndex(TABLE_EXERCISE_CATEGORY + KEY_NAME))));
        }
        c.close();
        return exercise;
    }

    public List<Exercise> getAllExerciseDetails(){
        List<Exercise> exercises = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_ID + ", "
                                       + KEY_NAME + ", "
                                       + KEY_DESCRIPTION + ", "
                                       + TABLE_EXERCISE_CATEGORY + KEY_NAME
                           + " FROM " + TABLE_EXERCISE_DETAIL + " INNER JOIN " + TABLE_EXERCISE_DETAIL_CATEGORY
                           + " ON " + KEY_ID + " = " + TABLE_EXERCISE_DETAIL + KEY_ID;

        SQLiteDatabase db = this.getWritableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);

        int id;
        Exercise exercise;
        if(c.moveToFirst()) {
            do {
                id = c.getInt(c.getColumnIndex(KEY_ID));
                exercise = Exercise.getById(exercises, id);
                if (exercise == null) {
                    exercise = new Exercise();
                    exercise.setId(id);
                    exercise.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                    exercise.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
                    exercise.getCategories().add(Category.valueOf(c.getString(c.getColumnIndex(TABLE_EXERCISE_CATEGORY + KEY_NAME))));

                    exercises.add(exercise);
                } else {
                    exercise.getCategories().add(Category.valueOf(c.getString(c.getColumnIndex(TABLE_EXERCISE_CATEGORY + KEY_NAME))));
                }
            } while (c.moveToNext());
        }
        c.close();
        return exercises;
    }

    public void updateExerciseDetail(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION,exercise.getDescription());

        db.update(TABLE_EXERCISE_DETAIL, values, KEY_NAME + " = ?", new String[]{exercise.getName()});

        deleteExerciseDetailCategory(exercise.getId());
        for (Category category : exercise.getCategories()) {
            createExerciseDetailCategory(exercise.getId(),category);
        }
    }

    public void deleteExerciseDetail(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXERCISE_DETAIL, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        deleteExerciseDetailCategory(id);
    }

    public void populateExerciseCategory(SQLiteDatabase db){
        ContentValues values;

        for (Category category : Category.values()) {
            values = new ContentValues();
            values.put(KEY_NAME,category.name());
            db.insert(TABLE_EXERCISE_CATEGORY,null,values);
        }
    }

    public void createExerciseDetailCategory(long exerciseDetailId, Category category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_EXERCISE_DETAIL + KEY_ID, exerciseDetailId);
        values.put(TABLE_EXERCISE_CATEGORY + KEY_NAME, category.name());
        db.insert(TABLE_EXERCISE_DETAIL_CATEGORY, null, values);
    }

    private void deleteExerciseDetailCategory(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXERCISE_DETAIL_CATEGORY, TABLE_EXERCISE_DETAIL + KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void createWorkoutPlan(WorkoutPlan workoutPlan){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, workoutPlan.getName());
        workoutPlan.setId((int) db.insert(TABLE_WORKOUT_PLAN, null, values));

        String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_WORKOUT_EXERCISE_ORDER + " ORDER BY " + KEY_ID + " DESC LIMIT 1";
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();

        int previousId = -1;
        int nextId;
        if (workoutPlan.getExerciseIds().size() == 1) {
            nextId = -1;
        } else if (c.getCount() == 0) {
            nextId = 2;
        } else {
            nextId = c.getInt(c.getColumnIndex(KEY_ID)) + 2;
        }

        for (int i = 0; i < workoutPlan.getExerciseIds().size(); i++) {
            values = new ContentValues();
            values.put(TABLE_WORKOUT_PLAN + KEY_ID, workoutPlan.getId());
            values.put(KEY_EXERCISE_CURRENT, workoutPlan.getExerciseIds().get(i));
            values.put(KEY_EXERCISE_PREVOUS, previousId);
            values.put(KEY_EXERCISE_NEXT, nextId);
            values.put(KEY_COUNT, workoutPlan.getExerciseCounts().get(i));
            previousId = ((int) db.insert(TABLE_WORKOUT_EXERCISE_ORDER, null, values));
            nextId = i == (workoutPlan.getExerciseIds().size() - 2) ? -1 : nextId + 1;
        }
        c.close();
    }

    public void deleteWorkoutPlan(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = {String.valueOf(id)};
        db.delete(TABLE_WORKOUT_PLAN, KEY_ID + " = ?", args);
        db.delete(TABLE_WORKOUT_EXERCISE_ORDER, TABLE_WORKOUT_PLAN + KEY_ID + " = ?", args);
    }


    public WorkoutPlan getWorkoutPlan(int id){
        WorkoutPlan workoutPlan = new WorkoutPlan();
        workoutPlan.setId(id);

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_PLAN + " WHERE " + KEY_ID  + " = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();

        workoutPlan.setName(c.getString(c.getColumnIndex(KEY_NAME)));

        selectQuery = "SELECT * FROM " + TABLE_WORKOUT_EXERCISE_ORDER + " WHERE " + TABLE_WORKOUT_PLAN + KEY_ID + " = " + id;
        c.close();
        c = db.rawQuery(selectQuery, null);

        List<Integer> exerciseList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        List<Integer> currentList = new ArrayList<>();
        List<Integer> previousList = new ArrayList<>();
        List<Integer> nextList = new ArrayList<>();

        while (c.moveToNext()) {
            exerciseList.add(c.getInt(c.getColumnIndex(KEY_EXERCISE_CURRENT)));
            countList.add(c.getInt(c.getColumnIndex(KEY_COUNT)));
            currentList.add(c.getInt(c.getColumnIndex(KEY_ID)));
            previousList.add(c.getInt(c.getColumnIndex(KEY_EXERCISE_PREVOUS)));
            nextList.add(c.getInt(c.getColumnIndex(KEY_EXERCISE_NEXT)));
        }

        int index = previousList.indexOf(-1);
        while (index != nextList.indexOf(-1)){
            workoutPlan.getExerciseIds().add(exerciseList.get(index));
            workoutPlan.getExerciseCounts().add(countList.get(index));
            index = previousList.indexOf(currentList.get(index));
        }
        workoutPlan.getExerciseIds().add(exerciseList.get(index));
        workoutPlan.getExerciseCounts().add(countList.get(index));
        c.close();
        return workoutPlan;
    }

    public List<WorkoutPlan> getAllWorkoutPlans() {
        WorkoutPlan workoutPlan;
        List<WorkoutPlan> workoutPlans= new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_WORKOUT_PLAN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();

        if (c.getCount() != 0) {
            do {
                workoutPlan = new WorkoutPlan();
                workoutPlan.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                workoutPlan.setName(c.getString(c.getColumnIndex(KEY_NAME)));

                workoutPlans.add(workoutPlan);
            } while (c.moveToNext());

        }
        c.close();
        return workoutPlans;
    }

    public void saveExerciseSet(List<Exercise> exrcises) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values;
        for (Exercise exercise : exrcises) {

            for (ExeciseSet exerciseSet : exercise.getAllSets()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                values = new ContentValues();

                values.put(TABLE_EXERCISE_DETAIL + KEY_ID, exercise.getId());
                values.put(KEY_WEIGHT, exerciseSet.getWeight());
                values.put(KEY_REPETITIONS, exerciseSet.getRepetition());
                values.put(KEY_DATE,  dateFormat.format(date));
                db.insert(TABLE_EXERCISE_SET_REPETITIONS, null, values);
            }
        }
    }

    public ExeciseSet getExerciseSetSet(int exerciseId, int order) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_EXERCISE_SET_REPETITIONS +
                " WHERE " + TABLE_EXERCISE_DETAIL + KEY_ID + " = " + exerciseId +
                " ORDER BY " + KEY_DATE + " DESC";

        Cursor c = db.rawQuery(query, null);
        ExeciseSet exerciseSet = new ExeciseSet();

        int i = 0;
        while (c.moveToNext()) {
            if (i == order) {
                exerciseSet.setWeight(c.getInt(c.getColumnIndex(KEY_WEIGHT)));
                exerciseSet.setRepetition(c.getInt(c.getColumnIndex(KEY_REPETITIONS)));
                c.close();
                return exerciseSet;
            } else {
                i++;
            }
        }
        c.close();
        return null;
    }

    public Map<Date, Integer> getStats(int workoutPlanId){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_EXERCISE_SET_REPETITIONS + " WHERE " + TABLE_EXERCISE_DETAIL + KEY_ID  + " IN " +
        "(SELECT DISTINCT " + KEY_EXERCISE_CURRENT + " FROM " + TABLE_WORKOUT_EXERCISE_ORDER +
                " WHERE " + TABLE_WORKOUT_PLAN + KEY_ID + " = " + workoutPlanId + ") ORDER BY " + KEY_DATE;

        Map<Date, Integer> stats = new TreeMap<>();

        Cursor c = db.rawQuery(query, null);
        while (c.moveToNext()) {
            String dateTime = c.getString(c.getColumnIndex(KEY_DATE));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = dateFormat.parse(dateTime);
                int weight = c.getInt(c.getColumnIndex(KEY_WEIGHT));
                int repetitions = c.getInt(c.getColumnIndex(KEY_REPETITIONS));
                if (stats.containsKey(date)) {
                    stats.put(date, stats.get(date) + (weight * repetitions));
                } else {
                    stats.put(date, weight * repetitions);
                }
            } catch (ParseException e) {
            }
        }
        return stats;
    }

    public int getStatDifference(int workoutPlanId){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_EXERCISE_SET_REPETITIONS + " WHERE " + TABLE_EXERCISE_DETAIL + KEY_ID  + " IN " +
                "(SELECT DISTINCT " + KEY_EXERCISE_CURRENT + " FROM " + TABLE_WORKOUT_EXERCISE_ORDER +
                " WHERE " + TABLE_WORKOUT_PLAN + KEY_ID + " = " + workoutPlanId + ") ORDER BY " + KEY_DATE + " DESC";

        Cursor c = db.rawQuery(query, null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        Date date2 = null;
        int sum1 = 0;
        int sum2 = 0;
        int weight = 0;
        int repetitions = 0;
        while (c.moveToNext()) {
            try {
                String dataString = c.getString(c.getColumnIndex((KEY_DATE)));
                Date date = dateFormat.parse(dataString);
                if (date1 == null) {
                    date1 = date;
                    weight = c.getInt(c.getColumnIndex(KEY_WEIGHT));
                    repetitions = c.getInt(c.getColumnIndex(KEY_REPETITIONS));
                    sum1 = weight * repetitions;
                } else if (date2 == null && date1.equals(date)) {
                    weight = c.getInt(c.getColumnIndex(KEY_WEIGHT));
                    repetitions = c.getInt(c.getColumnIndex(KEY_REPETITIONS));
                    sum1 = sum1 + (weight * repetitions);
                } else if (date2 == null && !date1.equals(date)){
                    date2 = date;
                    weight = c.getInt(c.getColumnIndex(KEY_WEIGHT));
                    repetitions = c.getInt(c.getColumnIndex(KEY_REPETITIONS));
                    sum2 = weight * repetitions;
                } else if (date2.equals(date)) {
                    weight = c.getInt(c.getColumnIndex(KEY_WEIGHT));
                    repetitions = c.getInt(c.getColumnIndex(KEY_REPETITIONS));
                    sum2 = sum2 + (weight * repetitions);
                } else {
                    return date2 == null ? 100 : Math.round(sum1 * 100.0f / sum2);
                }
            } catch (ParseException e) {

            }

        }
        return 100;
    }
}
