package cz.muni.fi.trainingdiary.entities;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by martina on 27.5.15.
 */
public class Exercise {
    private int id;
    private String name;
    private String description;
    private Set<Category> categories = new HashSet<>();
    private List<ExeciseSet> sets;

    public Exercise(String name, String description) {
        this.name = name;
        this.description = description;
        sets = new ArrayList<>();
    }

    public Exercise() {}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public boolean searchFor(String subString) {
        return (name.contains(subString) || description.contains(subString));
    }

    public String getCategoriesString(Context context) {

        String categoriesString = "";
        Map<Category, String> categoriesMap = Category.getCategoryStringResource(context);


        for (Iterator<Category> it = categories.iterator(); it.hasNext(); ) {
            Category category = it.next();
            categoriesString += categoriesMap.get(category);
            if(it.hasNext())
                categoriesString += ", ";
        }


        return  categoriesString;
    }

    public void setAllSets(List<ExeciseSet> sets) {
        if(sets == null)
            throw new NullPointerException("sets cannot be null");

        this.sets = sets;
    }

    public List<ExeciseSet> getAllSets() {
        return sets;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) {
        this.categories.add(category);
    }

    public boolean isInCategories(List<Category> exerciseCategories) {
        for(Category category : exerciseCategories) {
            if(categories.contains(category))
                return true;
        }

        return false;
    }

    public static Exercise getById(List<Exercise> exercises, long id) {
        for (Exercise e : exercises) {
            if (e.getId() == id)
                return e;
        }
        return null;
    }

    public static Exercise getExerciseByName(List<Exercise> exercises, String name) {
        for (Exercise e : exercises) {
            if (e.getName() == name)
                return e;
        }
        return null;
    }
}
