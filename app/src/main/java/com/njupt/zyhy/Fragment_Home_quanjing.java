package com.njupt.zyhy;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.List;


public class Fragment_Home_quanjing extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private ImageView back;
    private ViewPager mViewPager;
    private TextView mTvone, mTvtwo;

    //定义一个list来存放Fragment，建议这里的Fragment使用v4包下面的
    private List<Fragment> mFragmentLists;

    //自定义一个适配器
    private ViewPagerAdapter mAdater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_home_quanjing);
        back = (ImageView)findViewById(R.id.quanjinback);
        back.setOnClickListener(this);
        initView();
        initEvent();

        mAdater = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdater);
    }

    //初始化控件
    private void initView() {
        mViewPager = findViewById(R.id.viewPager);
        mTvone = findViewById(R.id.videoLayout);
        mTvtwo = findViewById(R.id.musicLayout);
        mFragmentLists = new ArrayList<>();

        mFragmentLists.add(new Fragment_Home_quanjing_1());
        mFragmentLists.add(new Fragment_Home_quanjing_2());

        //默认进入应用第一个被选中
        resetTextViewColor();
        mTvone.setTextColor(Color.parseColor("#78201f"));
    }

    //设置监听
    private void initEvent() {

        mViewPager.addOnPageChangeListener(this);
        mTvone.setOnClickListener(this);
        mTvtwo.setOnClickListener(this);

    }


    @Override
    public void onPageSelected(int position) {

        resetTextViewColor();
        switch (position) {
            case 0:
                mTvone.setTextColor(Color.parseColor("#78201f"));
                break;
            case 1:
                mTvtwo.setTextColor(Color.parseColor("#78201f"));
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    //页面滑动状态监听
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //点击事件，当点击标题的时候，通过传入当前点击的页面的position，调用mViewPager.setCurrentItem()来实现页面的变化
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quanjinback:
                finish();
                break;
            case R.id.videoLayout:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.musicLayout:
                mViewPager.setCurrentItem(1);
                break;
        }
    }

    //颜色初始化
    private void resetTextViewColor() {
        mTvone.setTextColor(Color.BLACK);
        mTvtwo.setTextColor(Color.BLACK);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //适配器
    class ViewPagerAdapter extends FragmentPagerAdapter {
        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //获取Fragment
        @Override
        public Fragment getItem(int position) {
            return mFragmentLists.get(position);
        }
        //总共有mFragmentLists.size()个需要显示
        @Override
        public int getCount() {
            return mFragmentLists.size();
        }
    }
}
