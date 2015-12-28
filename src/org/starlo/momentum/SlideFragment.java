package org.starlo.momentum;

import android.os.*;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;

public class SlideFragment extends android.support.v4.app.Fragment
{
    private Drawable mDrawable = null;

    public SlideFragment(Drawable drawable)
    {
        mDrawable = drawable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.slide, container, false);
        ImageView image = (ImageView)view.findViewById(R.id.image);
        image.setImageDrawable(mDrawable);

        return view;
    }
}
