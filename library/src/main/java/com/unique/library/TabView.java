package com.unique.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;

public class TabView extends RecyclerView {
    public static final int ACTION_DISTANCE_AUTO = Integer.MAX_VALUE;
    private float reflectionRatio = 0.4f;
    private int reflectionGap = 20;
    private boolean reflectionEnabled = false;
    private float unselectedAlpha;
    private Camera transformationCamera;
    private int maxRotation = 75;
    private float unselectedScale;
    private float scaleDownGravity = 0.5f;
    private int actionDistance;
    private float unselectedSaturation;

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.setLayoutManager(layoutManager);
        transformationCamera = new Camera();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (!(adapter instanceof TabViewAdapter)) {
            throw new ClassCastException(TabView.class.getSimpleName() + " only works in conjunction with a " + TabViewAdapter.class.getSimpleName());
        }
        super.setAdapter(adapter);
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        TabViewItemWrapper item = (TabViewItemWrapper) child;
        if (android.os.Build.VERSION.SDK_INT >= 16) item.invalidate();
        final int tabViewWidth = this.getWidth();
        final int tabViewCenter = tabViewWidth / 2;
        final int childWidth = item.getWidth();
        final int childHeight = item.getHeight();
        final int childCenter = item.getLeft() + childWidth / 2;
        final int actionDistance = (this.actionDistance == ACTION_DISTANCE_AUTO) ? (int) ((tabViewWidth + childWidth) / 2.0f) : this.actionDistance;
        final float effectsAmount = Math.min(1.0f, Math.max(-1.0f, (1.0f / actionDistance) * (childCenter - tabViewCenter)));
        t.clear();
        t.setTransformationType(Transformation.TYPE_BOTH);

        if (this.unselectedAlpha != 1) {
            final float alphaAmount = (this.unselectedAlpha - 1) * Math.abs(effectsAmount) + 1;
            t.setAlpha(alphaAmount);
        }
        if (this.unselectedSaturation != 1) {
            final float saturationAmount = (this.unselectedSaturation - 1) * Math.abs(effectsAmount) + 1;
            item.setSaturation(saturationAmount);
        }

        final Matrix imageMatrix = t.getMatrix();
        if (this.maxRotation != 0) {
            final int rotationAngle = (int) (-effectsAmount * this.maxRotation);
            this.transformationCamera.save();
            this.transformationCamera.rotateY(rotationAngle);
            this.transformationCamera.getMatrix(imageMatrix);
            this.transformationCamera.restore();
        }

        if (this.unselectedScale != 1) {
            final float zoomAmount = (this.unselectedScale - 1) * Math.abs(effectsAmount) + 1;
            final float translateX = childWidth / 2.0f;
            final float translateY = childHeight * this.scaleDownGravity;
            imageMatrix.preTranslate(-translateX, -translateY);
            imageMatrix.postScale(zoomAmount, zoomAmount);
            imageMatrix.postTranslate(translateX, translateY);
        }
        return super.getChildStaticTransformation(child, t);
    }

    private void setAttributes(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.TabView);
        try {
            actionDistance = array.getInteger(R.styleable.TabView_actionDistance, ACTION_DISTANCE_AUTO);
            scaleDownGravity = array.getFloat(R.styleable.TabView_scaleDownGravity, 1.0f);
            maxRotation = array.getInteger(R.styleable.TabView_maxRotation, 45);
            unselectedAlpha = array.getFloat(R.styleable.TabView_unselectedAlpha, 0.3f);
            unselectedSaturation = array.getFloat(R.styleable.TabView_unselectedSaturation, 0.0f);
            unselectedScale = array.getFloat(R.styleable.TabView_unselectedScale, 0.75f);
        } finally {
            array.recycle();
        }
    }

    public float getReflectionRatio() {
        return reflectionRatio;
    }

    public void setReflectionRatio(float reflectionRatio) {
        if (reflectionRatio <= 0 || reflectionRatio > 0.5f) {
            throw new IllegalArgumentException("reflectionRatio may only be in the interval (0, 0.5]");
        }

        this.reflectionRatio = reflectionRatio;

        if (this.getAdapter() != null) {
            this.getAdapter().notifyDataSetChanged();
        }
    }

    public int getReflectionGap() {
        return reflectionGap;
    }

    public void setReflectionGap(int reflectionGap) {
        this.reflectionGap = reflectionGap;

        if (this.getAdapter() != null) {
            this.getAdapter().notifyDataSetChanged();
        }
    }

    public boolean isReflectionEnabled() {
        return reflectionEnabled;
    }

    public void setReflectionEnabled(boolean reflectionEnabled) {
        this.reflectionEnabled = reflectionEnabled;

        if (this.getAdapter() != null) {
            this.getAdapter().notifyDataSetChanged();
        }
    }


    public void setUnselectedAlpha(float unselectedAlpha) {
        this.unselectedAlpha = unselectedAlpha;
    }

    public int getMaxRotation() {
        return maxRotation;
    }

    public void setMaxRotation(int maxRotation) {
        this.maxRotation = maxRotation;
    }

    public float getUnselectedAlpha() {
        return this.unselectedAlpha;
    }

    public float getUnselectedScale() {
        return unselectedScale;
    }

    public void setUnselectedScale(float unselectedScale) {
        this.unselectedScale = unselectedScale;
    }

    public float getScaleDownGravity() {
        return scaleDownGravity;
    }

    public void setScaleDownGravity(float scaleDownGravity) {
        this.scaleDownGravity = scaleDownGravity;
    }

    public int getActionDistance() {
        return actionDistance;
    }

    public void setActionDistance(int actionDistance) {
        this.actionDistance = actionDistance;
    }

    public float getUnselectedSaturation() {
        return unselectedSaturation;
    }

    public void setUnselectedSaturation(float unselectedSaturation) {
        this.unselectedSaturation = unselectedSaturation;
    }

    public static class LayoutParams extends RecyclerView.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
