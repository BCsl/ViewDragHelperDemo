package com.android.hellocsl.viewdraghelperdemo;

import android.content.Context;
import android.nfc.Tag;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by HelloCsl(cslgogogo@gmail.com) on 2015/4/26 0026.
 */
public class CoverLayout extends RelativeLayout {
    private final String TAG = this.getClass().getSimpleName();

    private ViewDragHelper mViewDragHelper;
    private View mCoverView;
    private ImageView mCoverImage;
    private View mButtomBar;
    private boolean isOpen = true;

    private int mButtonBarHeight;
    private int mViewHeigh = 0;
    private int mTotleDetal;
    private int mCoverLastX = 0;
    private int mCoverLastY = 0;
    private int mCoverInitX = 0;
    private int mCoverInitY = 0;
    private int mBarInitY = 0;
    private int mBarInitX = 0;

    public CoverLayout(Context context) {
        this(context, null);
    }

    public CoverLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoverLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "onFinishInflate:first child:" + getChildAt(0).getClass().getSimpleName());
        mCoverView = LayoutInflater.from(getContext()).inflate(R.layout.layout_cover, this, false);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mCoverView, layoutParams);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChange");
        super.onSizeChanged(w, h, oldw, oldh);
        mViewHeigh = h;
        init();
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
//            postInvalidateOnAnimation();
            postInvalidate();
        }
    }

    private void init() {
        mCoverImage = (ImageView) findViewById(R.id.image_cover);
        mButtomBar = findViewById(R.id.buttom_bar);
        mButtonBarHeight = getContext().getResources().getDimensionPixelOffset(R.dimen.buttom_bar_height);
        mCoverLastX = getContext().getResources().getDimensionPixelOffset(R.dimen.cover_last_x);
        mCoverLastY = getContext().getResources().getDimensionPixelOffset(R.dimen.cover_last_y);
        mTotleDetal = getHeight() - mButtonBarHeight;
        //指定好需要处理拖动的ViewGroup和回调 就可以开始使用了
        mViewDragHelper = ViewDragHelper.create(this, new DefaultDragHelper());
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL);
    }

    private class DefaultDragHelper extends ViewDragHelper.Callback {
        private boolean isFirst = true;

        @Override
        public boolean tryCaptureView(View view, int i) {
            Log.d(TAG, "tryCaptureView()-->pointerid:" + i + ",view:" + view.getClass().getSimpleName());
            if (isFirst) {
                mCoverInitX = mCoverImage.getLeft();
                mCoverInitY = mCoverImage.getTop();
                mBarInitY = mButtomBar.getTop();
                mBarInitX = mButtomBar.getLeft();
                Log.d(TAG, "init,x:" + mCoverInitX + ",y:" + mCoverLastY);
                isFirst = false;
            }
            return view == mCoverView;
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
            float rate = top / (mTotleDetal * 1.0f);
            ViewHelper.setTranslationX(mCoverImage, rate * (0 - mCoverInitX));
            ViewHelper.setTranslationY(mCoverImage, rate * (0 - mCoverInitY));
            ViewHelper.setPivotX(mCoverImage, mCoverLastX);
            ViewHelper.setPivotY(mCoverImage, mCoverLastY);
            ViewHelper.setScaleX(mCoverImage, 1 - rate * 3 / 4);
            ViewHelper.setScaleY(mCoverImage, 1 - rate * 3 / 4);

//            ViewHelper.setPivotX(mButtomBar, mBarInitX + mButtomBar.getWidth());
            ViewHelper.setTranslationY(mButtomBar, -top);
            ViewHelper.setPivotX(mButtomBar, getWidth());
            ViewHelper.setScaleX(mButtomBar, 1 - rate * 2 / 5);
            ViewHelper.setScaleY(mButtomBar, 1 - rate * 2 / 5);
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
//            if (xvel != 0 || yvel != 0) {
//                mViewDragHelper.flingCapturedView(0, 0, 0, mTotleDetal);
//            }
            //上拉
            if (yvel < 0) {
                if (mCoverView.getTop() > mTotleDetal / 3.0f) {
                    openCover();
                } else {
                    closeCover();
                }
            } else {
                //下拉
                if (mCoverView.getTop() > mTotleDetal / 3.0f) {
                    closeCover();
                } else {
                    openCover();
                }
            }
            invalidate();
        }

        private void openCover() {
            mViewDragHelper.smoothSlideViewTo(mCoverView, 0, 0);
            isOpen = true;
        }

        private void closeCover() {
            mViewDragHelper.smoothSlideViewTo(mCoverView, 0, mTotleDetal);
            isOpen = false;
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
            Log.d(TAG, "getOrderedChildIndex()：" + index);
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
            Log.d(TAG, "getViewHorizontalDragRange():" + child.getClass().getSimpleName());
            if (child == mCoverView) {
                return 0;
            }
            return super.getViewHorizontalDragRange(child);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            Log.d(TAG, "getViewVerticalDragRange():" + child.getClass().getSimpleName());
            if (child == mCoverView) {
                return Math.max(0, getHeight() - mButtonBarHeight);
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
            return super.clampViewPositionHorizontal(child, left, dx);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            Log.d(TAG, "clampViewPositionVertical()--top:" + top + ",dy:" + dy);
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - mButtonBarHeight;
            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;
        }

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.d(TAG, "onInterceptTouchEvent");
        final int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mViewDragHelper.cancel();
            return false;
        }
        boolean flag=mViewDragHelper.shouldInterceptTouchEvent(event);
        Log.d(TAG,"onInterceptTouchEvent:"+flag);
        return flag;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent");
        mViewDragHelper.processTouchEvent(event);
        return true;
    }
}
