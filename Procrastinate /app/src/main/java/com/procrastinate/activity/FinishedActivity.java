package com.procrastinate.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.procrastinate.R;
import com.procrastinate.fragment.FinishedLongTermActivityFragment;
import com.procrastinate.fragment.FinishedOneTimeActivityFragment;
import com.procrastinate.utils.Constants;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FinishedActivity extends BaseActivity {

    private MagicIndicator mMagicIndicator;
    private ViewPager2 mViewPager2;
    private Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing);
        // init title
        setTitle(getString(R.string.title_activity_finished));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // initView
        mMagicIndicator = findViewById(R.id.mMagicIndicator);
        mViewPager2 = findViewById(R.id.mViewPager2);
        // 初始化子页面
        fragments = new Fragment[]{
                FinishedOneTimeActivityFragment.newInstance(),
                FinishedLongTermActivityFragment.newInstance()
        };
        // 把刚刚创建的子页面丢给ViewPager2处理
        mViewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments[position];
            }

            @Override
            public int getItemCount() {
                return fragments.length;
            }
        });
        // view page indicator
        List<String> mTitleDataList = new ArrayList<>();
        Collections.addAll(mTitleDataList, getString(R.string.indicator_one_time_activity), getString(R.string.indicator_long_term_activity));
        /**
         * 指示器的适配器，其中getTitleView是获取textView,要在里面设置选中文字样式，和没有选中文字的样式
         *              getIndicator是指示器的图标，这里只是一个小横块
         */
        CommonNavigatorAdapter commonNavigatorAdapter = new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mTitleDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleView.setNormalColor(Color.parseColor("#AAFFFFFF"));
                titleView.setSelectedColor(Color.parseColor("#FFFFFFFF"));
                titleView.setText(mTitleDataList.get(index));
                titleView.setTextSize(15f);
                titleView.setOnClickListener(view -> mViewPager2.setCurrentItem(index));
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setColors(Color.parseColor("#FFFFFF"));
                indicator.setRoundRadius(180);
                return indicator;
            }
        };
        /**
         * 把刚刚设置好的样式，给到指示器
         */
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(commonNavigatorAdapter);
        mMagicIndicator.setNavigator(commonNavigator);
        /**
         * 绑定指示器和viewPager ： 把两个的生命周期设置为同步，滑动的时候就一起滑动了
         */
        // bind indicator , view page
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                mMagicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mMagicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                mMagicIndicator.onPageScrollStateChanged(state);
            }
        });
    }

    public static void startAction(Context context, String userName) {
        Intent intent = new Intent(context, FinishedActivity.class);
        intent.putExtra(Constants.USER_NAME, userName);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}