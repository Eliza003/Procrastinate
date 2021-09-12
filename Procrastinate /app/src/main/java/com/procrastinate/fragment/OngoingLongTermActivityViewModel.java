package com.procrastinate.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.procrastinate.database.entity.ActivityStageEntity;
import com.procrastinate.database.entity.LongTermActivityList;

import java.util.ArrayList;
import java.util.List;

public class OngoingLongTermActivityViewModel extends ViewModel {

    private MutableLiveData<List<LongTermActivityList>> mListData;

    public OngoingLongTermActivityViewModel() {
        mListData = new MutableLiveData<>();
        mListData.setValue(new ArrayList<>());
    }

    public LiveData<List<LongTermActivityList>> getListData() {
        return mListData;
    }

    public void setListData(List<LongTermActivityList> list) {
        mListData.getValue().clear();
        for (LongTermActivityList activityList : list) {
            for (ActivityStageEntity stageEntity : activityList.getActivityStageList()) {
                if (stageEntity.getType() == 0) {
                    mListData.getValue().add(activityList);
                    break;
                }
            }
        }
        mListData.setValue(mListData.getValue());
    }

}