package com.procrastinate.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.procrastinate.database.entity.ActivityStageEntity;
import com.procrastinate.database.entity.LongTermActivityList;

import java.util.ArrayList;
import java.util.List;

public class FinishedLongTermActivityViewModel extends ViewModel {

    private MutableLiveData<List<LongTermActivityList>> mFinishedData;
    private MutableLiveData<List<LongTermActivityList>> mUnfinishedData;

    public FinishedLongTermActivityViewModel() {
        mFinishedData = new MutableLiveData<>();
        mFinishedData.setValue(new ArrayList<>());
        mUnfinishedData = new MutableLiveData<>();
        mUnfinishedData.setValue(new ArrayList<>());
    }

    public LiveData<List<LongTermActivityList>> getFinishedData() {
        return mFinishedData;
    }

    public void setFinishedData(List<LongTermActivityList> list) {
        mFinishedData.getValue().clear();
        for (LongTermActivityList activityList : list) {
            int count = 0;
            for (ActivityStageEntity stageEntity : activityList.getActivityStageList()) {
                if (stageEntity.getType() == 1) {
                    count++;
                }
            }
            // 只显示全部完成的项
            if (count == activityList.getActivityStageList().size()) {
                mFinishedData.getValue().add(activityList);
            }
        }
        mFinishedData.setValue(mFinishedData.getValue());
    }

    public LiveData<List<LongTermActivityList>> getUnfinishedData() {
        return mUnfinishedData;
    }

    public void setUnfinishedData(List<LongTermActivityList> list) {
        mUnfinishedData.getValue().clear();
        for (LongTermActivityList activityList : list) {
            int ongoingCount = 0;
            int unfinishedCount = 0;
            for (ActivityStageEntity stageEntity : activityList.getActivityStageList()) {
                if (stageEntity.getType() == 0) {
                    ongoingCount++;
                } else if (stageEntity.getType() == 2) {
                    unfinishedCount++;
                }
            }
            // 没有正在进行的，并且有未完成的
            if (ongoingCount == 0 && unfinishedCount != 0) {
                mUnfinishedData.getValue().add(activityList);
            }
        }
        mUnfinishedData.setValue(mUnfinishedData.getValue());
    }

}