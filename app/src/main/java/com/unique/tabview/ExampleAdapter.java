package com.unique.tabview;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.unique.library.TabView;
import com.unique.library.TabViewAdapter;


public class ExampleAdapter extends TabViewAdapter {
    private Context context;


    public ExampleAdapter(Integer[] images, Context context) {
        super(images);
        this.context = context;
    }

    @Override
    public View getTabItem(int position, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (null != view)
            imageView = (ImageView) view;
        else {
            imageView = new ImageView(viewGroup.getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new TabView.LayoutParams(dpToPx(30, context.getResources()), dpToPx(30, context.getResources())));
        }
        return imageView;
    }

    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

}
