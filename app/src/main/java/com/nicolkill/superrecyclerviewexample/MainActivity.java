package com.nicolkill.superrecyclerviewexample;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import com.nicolkill.superrecyclerview.SuperRecyclerAdapter;
import com.nicolkill.superrecyclerview.listeners.ClickListener;
import com.nicolkill.superrecyclerview.listeners.FilterApplicator;
import com.nicolkill.superrecyclerview.listeners.ItemsAnimationListener;
import com.nicolkill.superrecyclerview.listeners.LongClickListener;
import com.nicolkill.superrecyclerview.listeners.OnDataChangeListener;
import com.nicolkill.superrecyclerview.listeners.ViewClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<Option> options = new ArrayList<>();
        for (int i = 0;i < 20;i++) {
            options.add(new Option(i));
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setNestedScrollingEnabled(false);
        SuperRecyclerAdapter<Option> adapter = new SuperRecyclerAdapter<>(recyclerView, options);

//        Basic click listeners
        adapter.setOnClickListener(new ClickListener<Option>() {
            @Override
            public void onItemSelected(View view, int position, Option element) {
                Snackbar.make(view, "Click option selected: " + element.getOptionName(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
        adapter.setOnLongClickListener(new LongClickListener<Option>() {
            @Override
            public void onLongClickItemSelected(View view, int position, Option element) {
                Snackbar.make(view, "Long Click pption selected: " + element.getOptionName(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

//        Data listener
        adapter.addOnDataChangeListener(new OnDataChangeListener<Option>() {
            @Override
            public void onDataAdded(List<Option> objects) {
                Log.e(TAG, "New data is added: " + objects.toString());
            }

            @Override
            public void onDataRemoved(List<Option> last) {
                Log.e(TAG, "Data is removed: " + last.toString());
            }

            @Override
            public void onSimpleDataAdded(Option object) {
                Log.e(TAG, "Individual data is added: " + object.getOptionName());
            }

            @Override
            public void onSimpleDataRemoved(Option object) {
                Log.e(TAG, "Individual data is removed: " + object.getOptionName());
            }

            @Override
            public void onSimpleDataReplaced(Option object, Option last) {
                Log.e(TAG, "Data replaced new: " + object.getOptionName() + ", last: " + last.getOptionName());
            }
        });

//        View functions
        adapter.addClickOnViewListener(R.id.option_logo, new ViewClickListener<Option>() {
            @Override
            public void onViewClick(View view, int position, Option element) {
                Snackbar.make(view, "Click on logo of option selected: " + element.getOptionName(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
        adapter.addFilterApplicator(new FilterApplicator() {
            @Override
            public boolean canApplyFilter(int position) {
                if (position % 2 == 0) {
                    return true;
                }
                return false;
            }
        });
        adapter.addFilterToView(R.id.option_name, R.color.colorAccent);

//        View animations
        adapter.setItemLayoutAnimation(R.anim.slide_in_right);
        adapter.setItemsAnimationListener(new ItemsAnimationListener() {
            @Override
            public void onAnimationStart() {
                Log.e(TAG, "Animation start!");
            }

            @Override
            public void onAnimationEnd() {
                Log.e(TAG, "Animation end!");
            }

            @Override
            public void onAnimationConfigure(Animation animation, int position) {
//                You can configure animation of any element
            }
        });
        adapter.disableAnimationOnEnd();
    }

}
