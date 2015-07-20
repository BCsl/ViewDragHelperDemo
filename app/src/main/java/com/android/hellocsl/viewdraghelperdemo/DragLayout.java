package com.android.hellocsl.viewdraghelperdemo;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by HelloCsl(cslgogogo@gmail.com) on 2015/4/26 0026.
 */
public class DragLayout extends FrameLayout {
    private final String TAG = this.getClass().getSimpleName();
    private ViewDragHelper mViewDragHelper;
    private ImageView mImageView;
    private int mImageLeft;
    private int mImageTop;
    private TextView mTextView;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
//    <TextView
//    android:layout_gravity="center"
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:text="Alpha"
//    android:textSize="18sp"
//    android:gravity="center"
//    android:textStyle="bold"/>
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        mTextView = (TextView) getChildAt(1);
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            postInvalidateOnAnimation();
        }
    }

    private void init() {
        mImageView = new ImageView(getContext());
        mImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.cover_expand));
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        mImageView.setClickable(true);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(mImageView, layoutParams);
        //指定好需要处理拖动的ViewGroup和回调 就可以开始使用了
        mViewDragHelper = ViewDragHelper.create(this, new DefaultDragHelper());
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL);
    }

    private class DefaultDragHelper extends ViewDragHelper.Callback {

        private boolean isFirst=true;

        @Override
        public boolean tryCaptureView(View view, int i) {
            Log.d(TAG, "tryCaptureView()-->pointerid:" + i + ",flag:" + (boolean) (view == mImageView));

            if (isFirst) {
                mImageLeft = mImageView.getLeft();
                mImageTop = mImageView.getTop();
                isFirst = false;
            }
            return view == mImageView;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            Log.d(TAG, "onViewDragStateChanged():" + state);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            Log.d(TAG, "onViewPositionChanged()--" + "left:" + left + ",top:" + top + ",dx:" + dx + ",dy:" + dy);
            int horizhonRange = getWidth() - mImageView.getWidth();
            int verticalRange = getHeight() - mImageView.getHeight();
            float horizontalFraction = left * 1.0f / horizhonRange;
            float verticalFraction = top * 1.0f / verticalRange;
            float fraction = (horizontalFraction + verticalFraction) / 2f;
//            mTextView.setText("alpha:" + fraction);
            invalidate();
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            Log.d(TAG, "onViewCaptured()--:");
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            Log.d(TAG, "onViewReleased()--xv:" + xvel + ",yv:" + yvel);
            if (xvel != 0 || yvel != 0) {
                mViewDragHelper.flingCapturedView(mImageLeft, mImageTop, mImageLeft, mImageTop);
            }
            invalidate();
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
            Log.d(TAG, "onEdgeTouched()");
        }

        @Override
        public boolean onEdgeLock(int edgeFlags) {
            Log.d(TAG, "onEdgeLock()");
            return super.onEdgeLock(edgeFlags);
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);
            Log.d(TAG, "onEdgeDragStarted()");
        }

        @Override
        public int getOrderedChildIndex(int index) {
            Log.d(TAG, "getOrderedChildIndex()");
            return super.getOrderedChildIndex(index);
        }

        /**
         * Return the magnitude of a draggable child view's horizontal range of motion in pixels.
         * 似乎作用不大，其他情况只用于判断是否可以拖动
         * 具体返回值真正起作用在于{@link ViewDragHelper#smoothSlideViewTo(View, int, int)}
         *
         * @param child Child view to check
         * @return range of horizontal motion in pixels
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            Log.d(TAG, "getViewHorizontalDragRange()");
            if (child == mImageView) {
                return getWidth();
            }
            return super.getViewHorizontalDragRange(child);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            Log.d(TAG, "getViewVerticalDragRange()");
            if (child == mImageView) {
                return getHeight();
            }
            return super.getViewVerticalDragRange(child);
        }

        /**
         * 限制子View水平拖拉操作。默认不支持水平操作，重写该方法提供新的水平坐标（根据提供的渴望的水平坐标）
         *
         * @param child Child view being dragged
         * @param left  Attempted motion along the X axis
         * @param dx    Proposed change in position for left
         * @return The new clamped position for left
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Log.d(TAG, "clampViewPositionHorizontal()--left:" + left + ",dx:" + dx);
            final int leftBound = getPaddingLeft();
            final int rightBound = getWidth() - mImageView.getWidth();
            final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
            return newLeft;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            Log.d(TAG, "clampViewPositionVertical()--top:" + top + ",dy:" + dy);
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - mImageView.getHeight();
            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.d(TAG, "onInterceptHoverEvent");
        final int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mViewDragHelper.cancel();
            return false;
        }
        return mViewDragHelper.shouldInterceptTouchEvent(event);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "onTouchEvent");
        mViewDragHelper.processTouchEvent(event);
        return true;
    }
}
