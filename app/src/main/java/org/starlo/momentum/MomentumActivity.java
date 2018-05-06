package org.starlo.momentum;

import android.os.*;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.content.res.*;
import android.graphics.drawable.*;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MomentumActivity extends android.support.v4.app.FragmentActivity
{
    private int mContentWait = 5000;

    private TextView mText = null;
    private TextView mLabel = null;
    private StopMotionViewPager mPager = null;

    private int mCount = 0;
    private int mSlideIndex = 0;
    private int mContentIndex = 0;
    private boolean mKeepGoing = true;

    private View.OnClickListener mPaddleListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mPager = (StopMotionViewPager)findViewById(R.id.pager);
        mPager.setAdapter(new SlideshowAdapter(getSupportFragmentManager()));

        mLabel = (TextView)findViewById(R.id.label);
        final String[] labels = getResources().getStringArray(R.array.labels);
        mLabel.setText(labels[0]);

        mText = (TextView)findViewById(R.id.text);
        final String[] content = getResources().getStringArray(R.array.content);
        mText.setText(content[0]);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE);

        mPaddleListener = new PaddleListener(labels, content);

        int[] mediaButtons = {R.id.play, R.id.pause};
        for(int i = 0; i < mediaButtons.length; i++)
        {
            findViewById(mediaButtons[i]).setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    int invisibleView = 0;
                    switch(view.getId())
                    {
                        case R.id.play:
                            mKeepGoing = true;
                            invisibleView = R.id.pause;
                            String[] labels = getResources().getStringArray(R.array.labels);
                            String[] content = getResources().getStringArray(R.array.content);
                            new PlayShowTask(labels, content).execute(mSlideIndex);
                            break;
                        case R.id.pause:
                            mKeepGoing = false;
                            invisibleView = R.id.play;
                            break;
                    }
                    int[] pageButtons = {R.id.left_paddle, R.id.right_paddle};
                    for(int i = 0; i < pageButtons.length; i++)
                    {
                        findViewById(pageButtons[i]).setOnClickListener(mKeepGoing ? null: mPaddleListener);
                    }
                    mPager.setAllowTouches(!mKeepGoing);
                    findViewById(view.getId()).setVisibility(View.GONE);
                    findViewById(invisibleView).setVisibility(View.VISIBLE);
                }

            });
        }

        new PlayShowTask(labels, content).execute(0);
    }

    private class SlideshowAdapter extends FragmentPagerAdapter
    {
        private String[] mSlides = null;
        private AssetManager mManager = null;

        public SlideshowAdapter(FragmentManager fm)
        {
            super(fm);
            mManager = getAssets();
            try 
            {
                mSlides = mManager.list("slides");
            }
            catch(Exception e) 
            {
                mSlides = new String[0];
            }
        }

        @Override
        public int getCount()
        {
            return mSlides.length;
        }

        @Override
        public Fragment getItem(int position)
        {
            Drawable drawable = null;
            try 
            {
                drawable = Drawable.createFromStream(mManager.open("slides/"+mSlides[position]), null);
            }
            catch(Exception e) 
            {
            }

            return new SlideFragment(drawable);
        }
    }

    private void pageLeft(String[] labels, String[] content)
    {
        mContentIndex--;
        mContentIndex = mContentIndex == -1 ? content.length-1: mContentIndex;
        mLabel.setText(labels[mContentIndex]);
        mText.setText(content[mContentIndex]);
    }

    private void pageRight(String[] labels, String[] content)
    {
        mContentIndex++;
        mContentIndex = mContentIndex == content.length ? 0: mContentIndex;
        mLabel.setText(labels[mContentIndex]);
        mText.setText(content[mContentIndex]);
    }

    private class PlayShowTask extends AsyncTask<Integer, Void, Integer>
    {
        private String[] mLabels = null;
        private String[] mContent = null;

        public PlayShowTask(String[] labels, String[] content)
        {
            mLabels = labels;
            mContent = content;
        }

        @Override
        protected Integer doInBackground(Integer... param)
        {
            Integer index = param[0];
            int iterations = Math.round(mContentWait/200.0f);
            do{
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE);
                try{ Thread.sleep(200); }catch(Exception e){}
            }while(--iterations > 0 && mKeepGoing);

            return index;
        }

        @Override
        protected void onPostExecute(Integer param)
        {
            mPager.setCurrentItem(param);
            mCount++;
            if(mCount%3 == 0)
            {
                pageRight(mLabels, mContent);
            }
            param++;
            mSlideIndex = param == mPager.getAdapter().getCount() ? 0: param;
            if(mKeepGoing)
            {
                new PlayShowTask(mLabels, mContent).execute(mSlideIndex);
            }
        }
    }

    private class PaddleListener implements View.OnClickListener
    {
        private String[] mLabels = null;
        private String[] mContent = null;

        public PaddleListener(String[] labels, String[] content)
        {
            mLabels = labels;
            mContent = content;
        }

        public void onClick(View view)
        {
            switch(view.getId())
            {
               case R.id.left_paddle:
                   pageLeft(mLabels, mContent);
                   break;
               case R.id.right_paddle:
                   pageRight(mLabels, mContent);
                   break;
            }
        }

    }

}
