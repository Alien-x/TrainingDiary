package cz.muni.fi.trainingdiary.androidcomponent;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by martina on 31.5.15.
 */
public class SpinnerAdapterWithHint extends ArrayAdapter<String> {
    public SpinnerAdapterWithHint(Context context, int resource) {
        super(context, resource);
    }

    public SpinnerAdapterWithHint(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public SpinnerAdapterWithHint(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    public SpinnerAdapterWithHint(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public SpinnerAdapterWithHint(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    public SpinnerAdapterWithHint(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (position == getCount()) {
            ((TextView) view.findViewById(android.R.id.text1)).setText("");
            ((TextView) view.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
        }
        return view;
    }

    @Override
    public int getCount() {
        return super.getCount() - 1;
    }
}
