package com.nicolkill.superrecyclerview.listeners;

import android.view.animation.Animation;

/**
 * Created by nicolkill on 3/13/17.
 */

public interface ItemsAnimationListener {

    void onAnimationStart();

    void onAnimationEnd();

    void onAnimationConfigure(Animation animation, int position);

}
