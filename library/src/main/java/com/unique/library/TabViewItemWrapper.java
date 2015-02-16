package com.unique.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class TabViewItemWrapper extends ViewGroup {
    private Paint mPaint;
    private ColorMatrix mColorMatrix;
    private float mSaturation;
    private boolean isReflectionEnabled = false;
    private float originalScaleFactor;
    private float imageReflectionRatio;
    private int reflectionGap;
    private Bitmap wrappedViewBitmap;
    private Canvas wrappedViewDrawingCanvas;

    public TabViewItemWrapper(Context context) {
        super(context);
        init();
    }

    public TabViewItemWrapper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TabViewItemWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int measuredWidth = this.getMeasuredWidth();
            int measuredHeight = this.getMeasuredHeight();
            if (wrappedViewBitmap == null || wrappedViewBitmap.getWidth() != measuredWidth || wrappedViewBitmap.getHeight() != measuredHeight) {
                wrappedViewBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
                wrappedViewDrawingCanvas = new Canvas(wrappedViewBitmap);
            }
            View child = getChildAt(0);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            int childLeft = (measuredWidth - childWidth) / 2;
            int childRight = measuredWidth - childLeft;
            child.layout(childLeft, 0, childRight, childHeight);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        remeasureChildren();
        if (isReflectionEnabled)
            this.setMeasuredDimension((int) (this.getMeasuredWidth() * this.originalScaleFactor), this.getMeasuredHeight());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        View childView = getChildAt(0);
        if (childView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (childView.isDirty()) {
                    childView.draw(wrappedViewDrawingCanvas);
                    if (isReflectionEnabled) createReflectedImages();
                }
            } else childView.draw(wrappedViewDrawingCanvas);
            canvas.drawBitmap(wrappedViewBitmap, (this.getWidth() - childView.getWidth()) / 2, 0, mPaint);
        }
    }

    private void createReflectedImages() {
        final int width = wrappedViewBitmap.getWidth();
        final int height = wrappedViewBitmap.getHeight();
        final Matrix matrix = new Matrix();
        matrix.postScale(1, -1);
        final int scaledHeight = (int) (height * originalScaleFactor);
        final int invertedHeight = height - scaledHeight - reflectionGap;
        final int invertedBitmapSourceTop = scaledHeight - invertedHeight;
        final Bitmap invertedBitmap = Bitmap.createBitmap(wrappedViewBitmap, 0, invertedBitmapSourceTop, width, invertedHeight, matrix, true);
        wrappedViewDrawingCanvas.drawBitmap(invertedBitmap, 0, scaledHeight + reflectionGap, null);
        final Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        wrappedViewDrawingCanvas.drawRect(0, height * (1 - imageReflectionRatio), width, height, paint);
    }

    private void init() {
        mPaint = new Paint();
        mColorMatrix = new ColorMatrix();
        setSaturation(1);
    }

    public void setReflectionEnabled(boolean b) {
        if (b != isReflectionEnabled) {
            isReflectionEnabled = b;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                this.setLayerType(b ? View.LAYER_TYPE_SOFTWARE : View.LAYER_TYPE_HARDWARE, null);
            }
            remeasureChildren();
        }
    }

    public void setReflectionRatio(float imageReflectionRatio) {
        if (imageReflectionRatio != this.imageReflectionRatio) {
            this.imageReflectionRatio = imageReflectionRatio;
            remeasureChildren();
        }
    }

    public void setReflectionGap(int reflectionGap) {
        if (reflectionGap != this.reflectionGap) {
            this.reflectionGap = reflectionGap;
            remeasureChildren();
        }
    }

    private void remeasureChildren() {
        View child = this.getChildAt(0);
        if (null != child) {
            final int originalChildHeight = this.getMeasuredHeight();
            originalScaleFactor = isReflectionEnabled ? (originalChildHeight * (1 - this.imageReflectionRatio) - reflectionGap) / originalChildHeight : 1.0f;
            final int childHeight = (int) (originalScaleFactor * originalChildHeight);
            final int childWidth = (int) (originalScaleFactor * getMeasuredWidth());
            int heightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST);
            int widthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST);
            this.getChildAt(0).measure(widthSpec, heightSpec);
        }
    }

    public void setSaturation(float saturation) {
        if (saturation != mSaturation) {
            mSaturation = saturation;
            mColorMatrix.setSaturation(saturation);
            mPaint.setColorFilter(new ColorMatrixColorFilter(mColorMatrix));
        }
    }


}
