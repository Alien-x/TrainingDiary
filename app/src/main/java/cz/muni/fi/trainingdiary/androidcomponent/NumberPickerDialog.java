package cz.muni.fi.trainingdiary.androidcomponent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import cz.muni.fi.trainingdiary.R;

/**
 * Created by martina on 31.5.15.
 */
public class NumberPickerDialog extends DialogFragment {
    public interface NoticeDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog, int number);
    }
    NoticeDialogListener listener;
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            listener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final NumberPicker numberPicker = new NumberPicker(getActivity());
        numberPicker.setMinValue(getResources().getInteger(R.integer.minimum_number_of_sets));
        numberPicker.setMaxValue(getResources().getInteger(R.integer.maximum_number_of_sets));
        numberPicker.setValue(3);

        FrameLayout parent = new FrameLayout(getActivity());
        parent.addView(numberPicker, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        builder.setView(parent)
                .setPositiveButton(R.string.add_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDialogPositiveClick(NumberPickerDialog.this, numberPicker.getValue());
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        return builder.create();
    }
}
