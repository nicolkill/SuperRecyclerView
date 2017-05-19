package com.nicolkill.superrecyclerview;

import android.view.View;

/**
 * Created by Nicol Acosta on 10/27/16.
 * nicol@parkiller.com
 */
public interface ClickEvents {

    void onClick(View view, int position);

    void onLongClick(View view, int position);

}
