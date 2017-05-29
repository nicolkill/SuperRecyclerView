# SuperRecyclerView
 [ ![Download](https://api.bintray.com/packages/nicolkill/android/SuperRecyclerView/images/download.svg) ](https://bintray.com/nicolkill/android/SuperRecyclerView/_latestVersion)

Super RecyclerView is a fearless RecyclerView Adapter that helps to focus only on your view, binding the data that you want to show in the components of your design.

## Usage
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
adapter.setOnClickListener(new ClickListener<Option>() {
    @Override
    public void onItemSelected(View view, int position, Option element) {
        Snackbar.make(view, "Click option selected: " + element.getOptionName(), Snackbar.LENGTH_SHORT).show();
    }
});
adapter.setOnLongClickListener(new LongClickListener<Option>() {
    @Override
    public void onLongClickItemSelected(View view, int position, Option element) {
        Snackbar.make(view, "Long Click pption selected: " + element.getOptionName(), Snackbar.LENGTH_SHORT).show();
    }
});
```
