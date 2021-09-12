package com.procrastinate.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.procrastinate.activity.LongTermDetailsActivity;
import com.procrastinate.adapter.OngoingLongTermActivityAdapter;
import com.procrastinate.database.entity.LongTermActivityList;
import com.procrastinate.database.repository.LongTermActivityRepository;
import com.procrastinate.database.repository.OneTimeActivityRepository;
import com.procrastinate.utils.ColorUtils;
import com.procrastinate.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class FinishedLongTermActivityFragment extends Fragment implements OngoingLongTermActivityAdapter.OnItemClickListener {

    private FinishedLongTermActivityViewModel mViewModel;

    private RecyclerView mFinishedRecyclerView;
    private OngoingLongTermActivityAdapter mFinishedAdapter;

    private RecyclerView mUnfinishedRecyclerView;
    private OngoingLongTermActivityAdapter mUnfinishedAdapter;

    private PieChart mPieChart;

    private TextView mFinishedTitleTextView;
    private TextView mUnfinishedTitleTextView;
    private TextView mNoDataTextView;

    public static FinishedLongTermActivityFragment newInstance() {
        return new FinishedLongTermActivityFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_finished_activity, container, false);
        mFinishedRecyclerView = rootView.findViewById(R.id.finished_recyclerView);
        mUnfinishedRecyclerView = rootView.findViewById(R.id.unfinished_recyclerView);
        mPieChart = rootView.findViewById(R.id.mPieChart);
        mNoDataTextView = rootView.findViewById(R.id.prompt_no_data);
        mFinishedTitleTextView = rootView.findViewById(R.id.title_finished_activities);
        mUnfinishedTitleTextView = rootView.findViewById(R.id.title_unfinished_activities);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FinishedLongTermActivityViewModel.class);
        // finished recyclerView
        mFinishedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mFinishedAdapter = new OngoingLongTermActivityAdapter(mViewModel.getFinishedData().getValue(), true);
        mFinishedAdapter.setOnItemClickListener(this);
        mFinishedRecyclerView.setAdapter(mFinishedAdapter);
        // unfinished recyclerView
        mUnfinishedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUnfinishedAdapter = new OngoingLongTermActivityAdapter(mViewModel.getUnfinishedData().getValue(), true);
        mUnfinishedAdapter.setOnItemClickListener(this);
        mUnfinishedRecyclerView.setAdapter(mUnfinishedAdapter);
        // pic chart
        Description description = new Description();
        description.setText("The completion rate");
        description.setTextSize(12f);
        mPieChart.setDescription(description);
        mPieChart.setHoleRadius(36f);
        mPieChart.setTransparentCircleRadius(0f);  //去掉半透明
        mPieChart.setRotationEnabled(false);  // 设置pieChart图表是否可以手动旋转
        mPieChart.setUsePercentValues(true);
        mPieChart.animateY(500, Easing.EaseInOutQuad);
        // init date
        LongTermActivityRepository.getInstance(getContext()).loadUserLongTermActivityList(
                getActivity().getIntent().getStringExtra(Constants.USER_NAME))
                .observe(getViewLifecycleOwner(), oneTimeActivityEntities -> {
                            mViewModel.setFinishedData(oneTimeActivityEntities);
                            mViewModel.setUnfinishedData(oneTimeActivityEntities);
                        }
                );
        mViewModel.getFinishedData().observe(getViewLifecycleOwner(), list -> {
            mFinishedAdapter.notifyDataSetChanged();
            initPicChartData();
            if (list.size() == 0) {
                mFinishedTitleTextView.setVisibility(View.GONE);
            } else {
                mFinishedTitleTextView.setVisibility(View.VISIBLE);
            }
        });
        mViewModel.getUnfinishedData().observe(getViewLifecycleOwner(),list -> {
            mUnfinishedAdapter.notifyDataSetChanged();
            initPicChartData();
            if (list.size() == 0) {
                mUnfinishedTitleTextView.setVisibility(View.GONE);
            } else {
                mUnfinishedTitleTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 初始化图表数据
     */
    private void initPicChartData() {
        // set pic chart
        int finishedCount = mViewModel.getFinishedData().getValue().size();
        int unfinishedCount = mViewModel.getUnfinishedData().getValue().size();
        List<PieEntry> entries = new ArrayList<>();
        if (finishedCount != 0) {
            entries.add(new PieEntry(finishedCount, getString(R.string.action_finished)));
        }
        if (unfinishedCount != 0) {
            entries.add(new PieEntry(unfinishedCount, getString(R.string.action_unfinished)));
        }
        if (entries.size() == 0){  // 没有数据，隐藏图标
            mPieChart.setVisibility(View.GONE);
            mNoDataTextView.setVisibility(View.VISIBLE);
        }else {
            mPieChart.setVisibility(View.VISIBLE);
            mNoDataTextView.setVisibility(View.GONE);
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorUtils.getColorList(entries.size()));
        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);//显示字体
        data.setValueTextColor(0xFFFFFFFF);
        data.setValueFormatter(new PercentFormatter(mPieChart)); //显示百分比
        mPieChart.setData(data);
        mPieChart.invalidate();
    }

    /**
     * item click delete , 在这里是删除按钮
     */
    @Override
    public void onDetailsClickListener(LongTermActivityList entity) {
        new AlertDialog.Builder(getContext())
                .setTitle(String.format("Are you sure to delete [ %s ] ?", entity.getActivity().getActivityName()))
                .setPositiveButton("Ok", (dialog, which) -> {
                    new Thread(() -> {  // start insert
                         boolean isSuccessful= LongTermActivityRepository.getInstance(getContext()).deleteLongTermActivityList(entity);
                        getActivity().runOnUiThread(() -> {    // Switch to the UI thread
                            if (isSuccessful) {
                                Toast.makeText(getContext(), getText(R.string.common_succeeded), Toast.LENGTH_SHORT).show();
                            } else { //
                                Toast.makeText(getContext(), getText(R.string.common_error), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * item view click
     */
    @Override
    public void onItemClickListener(LongTermActivityList entity) {
        LongTermDetailsActivity.startAction(getContext(),
                entity.getActivity().getActivityName(),entity.getActivity().getActivityId());
    }

}