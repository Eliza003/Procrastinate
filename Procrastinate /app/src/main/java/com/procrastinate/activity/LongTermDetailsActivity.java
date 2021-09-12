package com.procrastinate.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.procrastinate.R;
import com.procrastinate.adapter.LongTermDetailsActivityAdapter;
import com.procrastinate.database.entity.ActivityStageEntity;
import com.procrastinate.database.repository.ActivityStageRepository;
import com.procrastinate.utils.ColorUtils;
import com.procrastinate.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class LongTermDetailsActivity extends BaseActivity implements LongTermDetailsActivityAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private LongTermDetailsActivityAdapter mAdapter;
    private List<ActivityStageEntity> mStageEntityList;

    private PieChart mPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_term_details);
        Log.e("TAGGGG", "传输后 ：  addNotification: " +  getIntent().getLongExtra(Constants.ACTIVITY_ID, 0) );
        // init title
        setTitle(getIntent().getStringExtra(Constants.TITLE));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // init view
        mRecyclerView = findViewById(R.id.mRecyclerView);
        mStageEntityList = new ArrayList<>();
        mAdapter = new LongTermDetailsActivityAdapter(mStageEntityList);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        // chart
        mPieChart = findViewById(R.id.mPieChart);
        Description description = new Description();
        description.setText("Event progress");
        description.setTextSize(12f);
        mPieChart.setDescription(description);
        mPieChart.setHoleRadius(36f);
        mPieChart.setTransparentCircleRadius(0f);  //去掉半透明
        mPieChart.setRotationEnabled(false);  // 设置pieChart图表是否可以手动旋转
        mPieChart.setUsePercentValues(true);
        mPieChart.animateY(500, Easing.EaseInOutQuad);
        // init observe
        ActivityStageRepository.getInstance(this)
                .loadActivityStageList(getIntent().getLongExtra(Constants.ACTIVITY_ID, 0))
                .observe(this, activityStageEntities -> {
                    // set list
                    mStageEntityList.clear();
                    mStageEntityList.addAll(activityStageEntities);
                    mAdapter.notifyDataSetChanged();
                    // set pic chart
                    int ongoingCount = 0;
                    int finishedCount = 0;
                    int unfinishedCount = 0;
                    for (ActivityStageEntity stageEntity : activityStageEntities) {
                        switch (stageEntity.getType()) {
                            case 0:
                                ongoingCount++;
                                break;
                            case 1:
                                finishedCount++;
                                break;
                            case 2:
                                unfinishedCount++;
                                break;
                        }
                    }
                    List<PieEntry> entries = new ArrayList<>();
                    if (ongoingCount != 0) {
                        entries.add(new PieEntry(ongoingCount, getString(R.string.action_ongoing)));
                    }
                    if (finishedCount != 0) {
                        entries.add(new PieEntry(finishedCount, getString(R.string.action_finished)));
                    }
                    if (unfinishedCount != 0) {
                        entries.add(new PieEntry(unfinishedCount, getString(R.string.action_unfinished)));
                    }
                    PieDataSet dataSet = new PieDataSet(entries, "");
                    dataSet.setColors(ColorUtils.getColorList(entries.size()));
                    PieData data = new PieData(dataSet);
                    data.setValueTextSize(14f);//显示字体
                    data.setValueTextColor(0xFFFFFFFF);
                    data.setValueFormatter(new PercentFormatter(mPieChart)); //显示百分比
                    mPieChart.setData(data);
                    mPieChart.invalidate();
                });
    }

    public static void startAction(Context context, String title, long activityId) {
        Intent intent = new Intent(context, LongTermDetailsActivity.class);
        intent.putExtra(Constants.ACTIVITY_ID, activityId);
        intent.putExtra(Constants.TITLE, title);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    /**
     * item click finished
     */
    @Override
    public void onFinishedClickListener(ActivityStageEntity entity) {
        entity.setType(1);
        updateDatabase(entity);
    }

    /**
     * item click unfinished
     */
    @Override
    public void onUnfinishedClickListener(ActivityStageEntity entity) {
        entity.setType(2);
        updateDatabase(entity);
    }

    /**
     * update database
     */
    private void updateDatabase(ActivityStageEntity entity) {
        new Thread(() -> {
            boolean isSuccessful = ActivityStageRepository.getInstance(this).updateActivityStage(entity);
            runOnUiThread(() -> {    // Switch to the UI thread
                if (isSuccessful) {
                    Toast.makeText(this, getText(R.string.common_succeeded), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getText(R.string.common_error), Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    /**
     * item click reset
     */
    @Override
    public void onResetClickListener(ActivityStageEntity entity) {
        StageActivity.startAction(this,entity,getIntent().getStringExtra(Constants.TITLE));
    }

}