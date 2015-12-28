package org.starlo.momentum;

import android.content.Context;
import android.view.MotionEvent;
import android.util.AttributeSet;
import android.support.v4.view.ViewPager;

public class StopMotionViewPager extends ViewPager
{
    private boolean mAllowTouches = false;

    public StopMotionViewPager(Context context)
    {
        super(context);
    }

    public StopMotionViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        boolean result = false;
        if(mAllowTouches)
        {
            result = super.onInterceptTouchEvent(event);
        }

        return result;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean result = false;
        if(mAllowTouches)
        {
            result = super.onTouchEvent(event);
        }

        return result;

    }

    public void setAllowTouches(boolean allow)
    {
        mAllowTouches = allow;
    }

}
