package cz.muni.fi.trainingdiary.entities;

import android.content.Context;
import android.content.res.Resources;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by martina on 27.5.15.
 */
public enum Category {
    WHOLE_BODY,
    BACK,
    ABDOMEN,
    HANDS,
    LEGS;

//    public static Map<String, Category> getStringResourceCategory(Context context){
//        Resources resources = context.getResources();
//        Map<String,Category> values = new HashMap<>();
//        for (Category category : Category.values()) {
//            int id = resources.getIdentifier(category.name(), "string", context.getPackageName());
//            values.put(resources.getString(id), category);
//        }
//        return values;
//    }

    public static Map<Category, String> getCategoryStringResource(Context context) {
        Resources resources = context.getResources();
        Map<Category, String> values = new HashMap<>();
        for (Category category : Category.values()) {
            int id = resources.getIdentifier(category.name(), "string", context.getPackageName());
            values.put(category, resources.getString(id));
        }
        return values;
    }
}
