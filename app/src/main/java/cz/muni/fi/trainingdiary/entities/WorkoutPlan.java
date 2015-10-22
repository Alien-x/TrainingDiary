package cz.muni.fi.trainingdiary.entities;

import java.util.ArrayList;

/**
 * Created by martina on 1.6.15.
 */
public class WorkoutPlan {
    private int id;
    private String name;
    private ArrayList<Integer> exerciseIds = new ArrayList<>();
    private ArrayList<Integer> exerciseCounts = new ArrayList<>();

    public WorkoutPlan() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getExerciseIds() {
        return exerciseIds;
    }

    public void setExerciseIds(ArrayList<Integer> exerciseNames) {
        this.exerciseIds = exerciseNames;
    }

    public ArrayList<Integer> getExerciseCounts() {
        return exerciseCounts;
    }

    public void setExerciseCounts(ArrayList<Integer> exerciseCounts) {
        this.exerciseCounts = exerciseCounts;
    }

    @Override
    public String toString() {
        return getName();
    }
}
