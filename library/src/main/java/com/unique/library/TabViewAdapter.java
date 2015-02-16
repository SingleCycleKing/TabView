package com.unique.library;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by WZQ on 15-2-8.
 */
public abstract class TabViewAdapter extends RecyclerView.Adapter<TabViewAdapter.ViewHolder> {

    private List<Integer> images;
    private OnItemSelectedListener mOnItemSelectedListener;

    public TabViewAdapter(Integer[] images) {
        this.images = new ArrayList<>(Arrays.asList(images));
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;

        public ViewHolder(TabViewItemWrapper view) {
            super(view);
            mImageView = (ImageView) view.getChildAt(0);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != mOnItemSelectedListener)
                mOnItemSelectedListener.onItemSelected(v, getPosition());
        }
    }

    public void setOnItemClickListener(OnItemSelectedListener mOnItemClickListener) {
        this.mOnItemSelectedListener = mOnItemClickListener;
    }

    public interface OnItemSelectedListener {
        public void onItemSelected(View view, int position);
    }

    @Override
    public TabViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        TabView tabView = (TabView) viewGroup;
        View wrappedView = null;
        TabViewItemWrapper mItem;
        if (0 != viewGroup.getChildCount()) {
            mItem = (TabViewItemWrapper) viewGroup.getChildAt(0);
            wrappedView = mItem.getChildAt(0);
            mItem.removeAllViews();
        } else {
            mItem = new TabViewItemWrapper(viewGroup.getContext());
        }
        wrappedView = getTabItem(i, wrappedView, viewGroup);
        if (null == wrappedView) {
            throw new NullPointerException("getTabItem() was expected to return a view, but null was returned.");
        }

        final boolean isReflectionEnabled = tabView.isReflectionEnabled();
        mItem.setReflectionEnabled(isReflectionEnabled);
        if (isReflectionEnabled) {
            mItem.setReflectionGap(tabView.getReflectionGap());
            mItem.setReflectionRatio(tabView.getReflectionRatio());
        }
        mItem.addView(wrappedView);
        mItem.setLayoutParams(wrappedView.getLayoutParams());

        return new ViewHolder(mItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.mImageView.setImageResource(images.get(i));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public abstract View getTabItem(int position, View view, ViewGroup viewGroup);

}
