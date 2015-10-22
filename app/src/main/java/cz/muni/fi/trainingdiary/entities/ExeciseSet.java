package cz.muni.fi.trainingdiary.entities;

import android.content.Context;
import android.R.string;
import android.content.res.Resources;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cz.muni.fi.trainingdiary.R;

/**
 * Created by martina on 27.5.15.
 */
public class ExeciseSet {
    private int weight;
    private int repetition;

    private int old_weight;
    private int old_repetition;

    public ExeciseSet() {
        this.weight = 0;
        this.repetition = 0;
        old_weight = 0;
        old_repetition = 0;
    }


    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getRepetition() {
        return repetition;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }


    public static final String getLabelForSet(Resources resources, int numberOfSets) {
        String setsLabel;
        if(numberOfSets == 1)
            setsLabel = resources.getString(R.string.label_sets_1);
        else if(numberOfSets < 5)
            setsLabel = resources.getString(R.string.label_sets_2_4);
        else
            setsLabel = resources.getString(R.string.label_sets_5_more);

        return numberOfSets + " " + setsLabel;
    }
}
