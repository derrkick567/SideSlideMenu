package com.derrick.user.sideslidemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by user on 2015/9/1.
 */
public class MyHorizontalScrollView extends HorizontalScrollView {

    private LinearLayout mWrapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mScreenWidth;

    private boolean once = false;

    private int mMenuWidth;
    //dp;
    private int mMenuRightPadding = 50;
    private boolean isOpen;

    public MyHorizontalScrollView(Context context) {
        this(context, null);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    //當有使用自訂屬性時，會調用此constructor
    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //get our custom attr
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyHorizontalScrollView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.MyHorizontalScrollView_rightPadding:
                    mMenuRightPadding = a.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension
                                    (TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()));
            }
        }
        a.recycle();

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mScreenWidth = outMetrics.widthPixels;

        //convert dp into px
       // mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once) {
            //set width of child view
            mWrapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWrapper.getChildAt(0);
            mContent = (ViewGroup) mWrapper.getChildAt(1);

            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;

            once = true;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    /**
     * by setting offset , hide the menu
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(mMenuWidth, 0);
        }
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float scale  =l*1.0f/mMenuWidth;  //1~0

        float rightScale =0.7f+0.3f*scale;
        float leftScale = 1.0f-scale*0.3f;
        float leftAlpha = 0.6f+0.4f*(1-scale);
        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale*0.8f);
        ViewHelper.setScaleX(mMenu, leftScale);
        ViewHelper.setScaleY(mMenu, leftAlpha);
        ViewHelper.setAlpha(mMenu,leftAlpha);
        //set content縮放的中心點
        ViewHelper.setPivotX(mContent, 0);
        ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
        ViewHelper.setScaleX(mContent, rightScale);
        ViewHelper.setScaleY(mContent, rightScale);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                if (scrollX > mMenuWidth / 2) {
                    this.smoothScrollTo(mMenuWidth, 0);
                    isOpen=false;
                } else {
                    this.smoothScrollTo(0, 0);
                    isOpen=true;
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }


    //開啟menu
    public void openMenu(){
        if(isOpen) return ;
        this.smoothScrollTo(0, 0);
        isOpen=true;
    }

    public void closeMenu(){
        if(!isOpen) return;
        this.smoothScrollTo(mMenuWidth,0);
        isOpen=false;
    }
    public void toggle(){
        if(isOpen)
            closeMenu();
        else
            openMenu();
    }
}
