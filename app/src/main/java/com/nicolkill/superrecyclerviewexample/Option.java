package com.nicolkill.superrecyclerviewexample;

import android.content.Context;

import com.nicolkill.superrecyclerview.annotations.BindField;
import com.nicolkill.superrecyclerview.annotations.LayoutResource;

/**
 * Created by nicolkill on 5/18/17.
 */

@LayoutResource(R.layout.row)
public class Option {

    private static final String TAG = Option.class.getSimpleName();

    private int mOptionNumber;

    public Option(int number) {
        mOptionNumber = number;
    }

    @BindField(id = R.id.option_name)
    public String getOptionName() {
        return "Option " + (mOptionNumber + 1);
    }

    @BindField(type = BindField.Type.IMAGE, id = R.id.option_logo)
    public String getOptionImage(Context context) {
        return "https://dummyimage.com/200x200/" +
                Integer.toHexString(context.getResources().getColor(R.color.colorPrimary)) +
                "/ffffff.png&text=" + (mOptionNumber + 1);
    }

}
