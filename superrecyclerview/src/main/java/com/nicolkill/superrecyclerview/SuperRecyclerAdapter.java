package com.nicolkill.superrecyclerview;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.AnimRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicolkill.superrecyclerview.annotations.BindField;
import com.nicolkill.superrecyclerview.annotations.LayoutResource;
import com.nicolkill.superrecyclerview.listeners.ClickListener;
import com.nicolkill.superrecyclerview.listeners.FilterApplicator;
import com.nicolkill.superrecyclerview.listeners.ItemsAnimationListener;
import com.nicolkill.superrecyclerview.listeners.LongClickListener;
import com.nicolkill.superrecyclerview.listeners.OnDataChangeListener;
import com.nicolkill.superrecyclerview.listeners.ViewClickListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nicol Acosta on 10/27/16.
 * nicol@parkiller.com
 */
public class SuperRecyclerAdapter<T> extends RecyclerView.Adapter<SuperRecyclerAdapter.SuperViewHolder> implements ClickEvents {

    private static final String TAG = SuperRecyclerAdapter.class.getSimpleName();

    public static final int NO_ANIMATION = -1;

    private static final int NO_INDEX = -1;

    private int mViewLayout = 0;
    private int mDefaultImageRes = R.drawable.icon;

    private int lastPosition = -1;
    private int mLayoutAnimation = NO_ANIMATION;

    private List<T> mObjects;
    private ClickListener<T> mClickListener;
    private LongClickListener<T> mLongClickListener;

    private ArrayList<Integer> mHiddenViews = new ArrayList<>();
    private HashMap<Integer, ViewClickListener<T>> mViewListeners = new HashMap<>();
    private HashMap<Integer, Integer> mImageFilters = new HashMap<>();
    private ArrayList<FilterApplicator> mFilterApplicators = new ArrayList<>();
    private Integer mDefaultFilter = R.color.colorPrimary;
    private ArrayList<OnDataChangeListener<T>> mOnChangeListeners = new ArrayList<>();

    private ItemsAnimationListener mItemsAnimationListener;
    private AnimationWatcher mAnimationWatcher;

    public SuperRecyclerAdapter(RecyclerView recyclerView, List<T> objects) {
        this(recyclerView);
        setElements(objects);
    }

    public SuperRecyclerAdapter(RecyclerView recyclerView) {
        this();
        recyclerView.setAdapter(this);
    }

    public SuperRecyclerAdapter() {
        this(new LinkedList<T>());
    }

    public SuperRecyclerAdapter(List<T> objects) {
        setElements(objects);
    }


    /**
     * Cuando el RecyclerView tiene que crear un elemento de la lista, llama a este metodo
     * @param viewGroup grupo de vistas
     * @param viewType tipo de vista
     * @return vista preparada para mostrar un elemento
     */
    @Override
    public SuperRecyclerAdapter.SuperViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(mViewLayout, viewGroup, false);
        return new SuperViewHolder(this, itemView);
    }

    /**
     * Cuando el RecyclerView necesita mostrar los datos en un elemento de la lista, llama a este metodo
     * @param viewHolder contenedor de la vista
     * @param position posicion del elemento de la vista
     */
    @Override
    public void onBindViewHolder(SuperRecyclerAdapter.SuperViewHolder viewHolder, int position) {
        viewHolder.bindViewHolder(mObjects.get(position));
        viewHolder.addClickListenerOnViews(mViewListeners);
        viewHolder.hideViews(mHiddenViews);
        viewHolder.applyFilterToImages(mImageFilters, mFilterApplicators);
        if (mLayoutAnimation != -1 && position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewHolder.itemView.getContext(), mLayoutAnimation);
            if (mItemsAnimationListener != null) {
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        if (mAnimationWatcher == null) {
                            mAnimationWatcher = new AnimationWatcher(mItemsAnimationListener);
                        } else {
                            mAnimationWatcher.add();
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mAnimationWatcher.remove();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                mItemsAnimationListener.onAnimationConfigure(animation, position);
            }
            viewHolder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    /**
     * Cuando el RecyclerView necesita saber cuantos elementos tiene la lista, llama a este metodo
     * @return cantidad de elementos de la lista
     */
    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    /**
     * Pone el listener en la clase el cual ayuda a saber cuando un elemento de la lista ha sido seleccionado
     * @param listener listener de seleccion
     * @see ClickListener
     */
    public void setOnClickListener(ClickListener<T> listener) {
        mClickListener = listener;
    }

    public void setOnLongClickListener(LongClickListener<T> listener) {
        mLongClickListener = listener;
    }

    /**
     * Cambia la lista de elementos por una nueva
     * @param objects lista de elementos
     */
    public void setElements(List<T> objects) {
        if (objects != null) {
            if (mObjects != null) {
                mObjects.clear();
            }
            addElements(objects);
        }
    }

    public void addElements(List<T> objects) {
        if (objects != null) {
            if (mObjects != null) {
                mObjects.addAll(objects);
            } else {
                mObjects = objects;
            }
            bindElementView();
            notifyDataSetChanged();
            for (OnDataChangeListener<T> listener: mOnChangeListeners) {
                listener.onDataAdded(mObjects);
            }
        }
    }

    public void addElement(T object) {
        if (object != null) {
            mObjects.add(0, object);
            bindElementView();
            notifyDataSetChanged();
            for (OnDataChangeListener<T> listener: mOnChangeListeners) {
                listener.onSimpleDataAdded(object);
            }
        }
    }

    public void replaceElement(int index, T object) {
        if (object != null && index != NO_INDEX && index <= mObjects.size()) {
            T last = mObjects.get(index);
            mObjects.set(index, object);
            notifyDataSetChanged();
            for (OnDataChangeListener<T> listener: mOnChangeListeners) {
                listener.onSimpleDataReplaced(object, last);
            }
        }
    }

    public void removeElement(T object) {
        if (mObjects != null && object != null) {
            if (mObjects.contains(object)) {
                if (mObjects.remove(object)) {
                    notifyDataSetChanged();
                    for (OnDataChangeListener<T> listener: mOnChangeListeners) {
                        listener.onSimpleDataRemoved(object);
                    }
                }
            }
        }
    }

    public void clearData() {
        if (mObjects != null) {
            mObjects.clear();
            notifyDataSetChanged();
            for (OnDataChangeListener<T> listener: mOnChangeListeners) {
                listener.onDataRemoved(mObjects);
            }
        }
    }

    public void addOnDataChangeListener(OnDataChangeListener<T> listener) {
        if (listener != null) {
            mOnChangeListeners.add(listener);
        }
    }

    private void bindElementView() {
        if (mObjects.size() > 0 && mViewLayout == 0) {
            Class clazz = mObjects.get(0).getClass();
            if (clazz.isAnnotationPresent(LayoutResource.class)) {
                mViewLayout = ((LayoutResource) clazz.getAnnotation(LayoutResource.class)).value();
            } else {
                throw new IllegalStateException("The class need the annotation LayoutResource");
            }
        }
    }

    public List<T> getDataList() {
        return mObjects;
    }

    public int getDataSize() {
        if (mObjects != null) {
            return mObjects.size();
        }
        return 0;
    }

    public void addClickOnViewListener(Integer viewId, ViewClickListener<T> viewClickListener) {
        if (viewId != null && viewClickListener != null) {
            mViewListeners.put(viewId, viewClickListener);
        }
    }

    public void hideView(Integer viewId) {
        if (viewId != null) {
            mHiddenViews.add(viewId);
        }
    }

    public void setDefaultImageRes(@DrawableRes int res) {
        mDefaultImageRes = res;
    }

    public void addFilterToView(Integer viewId, @ColorRes Integer colorRes) {
        if (viewId != null && colorRes != null) {
            mImageFilters.put(viewId, colorRes);
        }
    }

    public void setDefaultFilter(@ColorRes Integer colorRes) {
        if (colorRes != null) {
            mDefaultFilter = colorRes;
        }
    }

    public void addFilterApplicator(FilterApplicator filterApplicator) {
        if (filterApplicator != null) {
            mFilterApplicators.add(filterApplicator);
        }
    }

    public void setItemLayoutAnimation(@AnimRes int layoutAnimation) {
        mLayoutAnimation = layoutAnimation;
    }

    public void setItemsAnimationListener(ItemsAnimationListener itemsAnimationListener) {
        mItemsAnimationListener = itemsAnimationListener;
    }

    public void disableAnimationOnEnd() {
        setItemsAnimationListener(new ItemsAnimationListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
                setItemLayoutAnimation(NO_ANIMATION);
            }

            @Override
            public void onAnimationConfigure(Animation animation, int position) {
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        if (mClickListener != null) {
            mClickListener.onItemSelected(view, position, mObjects.get(position));
        }
    }

    @Override
    public void onLongClick(View view, int position) {
        if (mLongClickListener != null) {
            mLongClickListener.onLongClickItemSelected(view, position, mObjects.get(position));
        }
    }

    class SuperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ClickEvents mClickEvents;
        private T mObject;

        public SuperViewHolder(ClickEvents clickEvents, View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mClickEvents = clickEvents;
        }

        private void applyFilterToImages(HashMap<Integer, Integer> views, ArrayList<FilterApplicator> filterApplicators) {
            for (final HashMap.Entry<Integer, Integer> entry: views.entrySet()) {
                boolean canApplyFilter = true;
                if (filterApplicators.size() > 0) {
                    for (int i = 0;i < filterApplicators.size() && canApplyFilter;i++) {
                        canApplyFilter = filterApplicators.get(i).canApplyFilter(getLayoutPosition());
                    }
                }
                View view = itemView.findViewById(entry.getKey());
                int color = itemView.getResources().getColor(mDefaultFilter);
                if (canApplyFilter) {
                    color = itemView.getResources().getColor(entry.getValue());
                }
                if (view instanceof ImageView) {
                    ((ImageView) view)
                            .setColorFilter(
                                    color,
                                    PorterDuff.Mode.SRC_IN
                            );
                } else if (view instanceof TextView) {
                    ((TextView) view)
                            .setTextColor(color);
                }
            }
        }

        private void hideViews(ArrayList<Integer> views) {
            for (Integer id: views) {
                itemView.findViewById(id).setVisibility(View.GONE);
            }
        }

        private void addClickListenerOnViews(HashMap<Integer, ViewClickListener<T>> views) {
            for (final HashMap.Entry<Integer, ViewClickListener<T>> entry: views.entrySet()) {
                itemView.findViewById(entry.getKey()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        entry.getValue().onViewClick(view, getLayoutPosition(), mObject);
                    }
                });
            }
        }

        private void bindViewHolder(T object) {
            mObject = object;
            Class clazz = mObject.getClass();
            for(Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(BindField.class)) {
                    BindField bindField = method.getAnnotation(BindField.class);
                    Class parameters[] = method.getParameterTypes();
                    boolean hasContext = false;
                    if (parameters.length == 1) {
                        hasContext = parameters[0].equals(Context.class);
                        if (!hasContext) {
                            throw new IllegalStateException(method.getName() + " method can only have Context parameter");
                        }
                    }
                    if (parameters.length > 1) {
                        throw new IllegalStateException(method.getName() + " method can not have more that 1 parameter");
                    }
                    if (method.getReturnType().equals(Void.TYPE)) {
                        throw new IllegalStateException(method.getName() + " method need return a type");
                    }
                    View view = itemView.findViewById(bindField.id());
                    Object objInvoked = null;
                    try {
                        if (hasContext) {
                            objInvoked = method.invoke(mObject, itemView.getContext());
                        } else {
                            objInvoked = method.invoke(mObject);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed method call: " + method.toString());
                        Log.e(TAG, e.getMessage(), e);
                    }
                    if (objInvoked != null && !objInvoked.toString().isEmpty()) {
                        try {
                            switch (bindField.type()) {
                                case TEXT:
                                    text(view, objInvoked);
                                    break;
                                case IMAGE:
                                    image(method, view, objInvoked);
                                    break;
                                case VISIBLE:
                                    visible(method, view, objInvoked);
                                    break;
                                case GONE:
                                    gone(method, view, objInvoked);
                                    break;
                                case CHECKED:
                                    checked(method, view, objInvoked);
                                    break;
                                case ENABLED:
                                    enabled(method, view, objInvoked);
                                    break;
                                case BACKGROUND:
                                    background(method, view, objInvoked);
                                    break;
                            }
                        } catch (Exception e) {
                            view.setVisibility(View.GONE);
                        }
                    } else {
                        view.setVisibility(View.GONE);
                    }
                }
            }
        }

        private void text(View view, Object object) {
            ((TextView) view).setText(object.toString());
        }

        private void image(Method method, View view, Object object) {
            ImageView imageView = (ImageView) view;
            if (method.getReturnType().equals(Integer.class)) {
                imageView.setImageResource(Integer.parseInt(object.toString()));
            } else if (method.getReturnType().equals(String.class)) {
                if (!object.toString().isEmpty()) {
                    Picasso.with(itemView.getContext()).cancelRequest(imageView);
                    Picasso.with(itemView.getContext())
                            .load(object.toString())
                            .placeholder(mDefaultImageRes)
                            .error(mDefaultImageRes)
                            .into(imageView);
                }
            }
        }

        private void visible(Method method, View view, Object object) {
            if (transformToBoolean(method, object)) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.INVISIBLE);
            }
        }

        private void gone(Method method, View view, Object object) {
            if (transformToBoolean(method, object)) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }

        private void checked(Method method, View view, Object object) {
            CompoundButton checkable = (CompoundButton) view;
            checkable.setChecked(transformToBoolean(method, object));
        }

        private void enabled(Method method, View view, Object object) {
            view.setEnabled(transformToBoolean(method, object));
        }

        private Boolean transformToBoolean(Method method, Object object) {
            if (method.getReturnType().equals(Boolean.class)) {
                return ((Boolean) object);
            } else if (method.getReturnType().equals(Integer.class)) {
                return ((Integer) object) >= 1;
            } else {
                throw new IllegalStateException("The parameter method need be Boolean, Integer");
            }
        }

        private void background(Method method, View view, Object object) {
            if (method.getReturnType().equals(Integer.class)) {
                view.setBackgroundResource(Integer.parseInt(object.toString()));
            } else if (method.getReturnType().equals(Drawable.class)) {
                view.setBackgroundDrawable((Drawable) object);
            } else {
                throw new IllegalStateException("The parameter method need be Integer, Drawable");
            }
        }

        @Override
        public void onClick(View view) {
            mClickEvents.onClick(view, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            mClickEvents.onLongClick(view, getLayoutPosition());
            return true;
        }
    }

    private class AnimationWatcher extends Thread {

        private ItemsAnimationListener mAnimationListener;
        private int mIgnoreCount = 0;

        public AnimationWatcher(ItemsAnimationListener listener) {
            mAnimationListener = listener;
            start();
        }

        @Override
        public synchronized void start() {
            add();
            super.start();
        }

        @Override
        public void run() {
            mAnimationListener.onAnimationStart();
            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            mAnimationListener.onAnimationEnd();
            mAnimationWatcher = null;
        }

        public void add() {
            mIgnoreCount++;
        }

        public void remove() {
            mIgnoreCount--;
            if (mIgnoreCount == 0) {
                synchronized (this) {
                    notify();
                }
            }
        }
    }

}