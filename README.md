# Android Material Intro Screen
 [ ![Download](https://api.bintray.com/packages/nicolkill/android/SuperRecyclerView/images/download.svg) ](https://bintray.com/nicolkill/android/SuperRecyclerView/_latestVersion)
 [![Build Status](https://travis-ci.org/nicolkill/SuperRecyclerView.svg?branch=master)](https://travis-ci.org/TangoAgency/material-intro-screen)

**Super RecyclerView** is a library that helps to use RecyclerView adapter without code, just using annotations on your class, creating Collection of that class and creating Adapter that parametrize that class, and its all.

## Features
  - No adapter code required
  - Click and Long Click events
  - View element filter/hide
  - Single view click events
  - Data events (new, remove and replace single and all data)
  - Animations, single animation listener

## You can know how it works if you see the code, all it's on [SuperRecyclerAdapter](https://github.com/nicolkill/SuperRecyclerView/blob/master/superrecyclerview/src/main/java/com/nicolkill/superrecyclerview/SuperRecyclerAdapter.java) file

### ***For more functions or bugs, create a [issue](https://github.com/nicolkill/SuperRecyclerView/issues).***

## **Usage**
### Step 1:
#### Add gradle dependecy
```
dependencies {
  compile 'com.nicolkill:superrecyclerview:{latest_release}'
}
```
### Step 2:
#### Use the LayoutResource annotation in your class and BindField of the method that you want bind:
```java
@LayoutResource(R.layout.row)
public class Option {

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
```
### Step 3:
#### Create the adapter:
```java
ArrayList<Option> options = new ArrayList<>();
SuperRecyclerAdapter<Option> adapter = new SuperRecyclerAdapter<>(options);

```
### Step 4:
#### Customize the adapter with the functions and listeners that you want
```java
// click
adapter.setOnClickListener(new ClickListener<Option>() {
    @Override
    public void onItemSelected(View view, int position, Option element) {
        Snackbar.make(view, "Click option selected: " + element.getOptionName(), Snackbar.LENGTH_SHORT).show();
    }
});

// long click
adapter.setOnLongClickListener(new LongClickListener<Option>() {
    @Override
    public void onLongClickItemSelected(View view, int position, Option element) {
        Snackbar.make(view, "Long Click ppt
#### [Add slides:][Intro Activity]ion selected: " + element.getOptionName(), Snackbar.LENGTH_SHORT).show();
    }
});

// data listener
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

// view functions
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

// view animations
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
        // You can configure animation of any element
    }
});
adapter.disableAnimationOnEnd();
```
