package com.nicolkill.superrecyclerview.listeners;

import java.util.List;

/**
 * Created by nicolkill on 1/9/17.
 */

public abstract class OnDataChangeListener<T> {
    public void onDataAdded(List<T> objects) {}
    public void onDataRemoved(List<T> last) {}
    public void onSimpleDataAdded(T object) {}
    public void onSimpleDataRemoved(T object) {}
    public void onSimpleDataReplaced(T object, T last) {}
}
