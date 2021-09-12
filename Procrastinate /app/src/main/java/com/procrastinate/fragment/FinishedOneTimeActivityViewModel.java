package com.procrastinate.fragment;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.procrastinate.database.entity.OneTimeActivityEntity;

import java.util.ArrayList;
import java.util.List;

public class FinishedOneTimeActivityViewModel extends ViewModel {

    private MutableLiveData<List<Pair<Long, String>>> mFinishedPairs;
    private MutableLiveData<List<Pair<Long, String>>> mUnfinishedPairs;

    public FinishedOneTimeActivityViewModel() {
        mFinishedPairs = new MutableLiveData<>();
        mFinishedPairs.setValue(new ArrayList<>());
        mUnfinishedPairs = new MutableLiveData<>();
        mUnfinishedPairs.setValue(new ArrayList<>());
    }

    public LiveData<List<Pair<Long, String>>> getFinishedPairs() {
        return mFinishedPairs;
    }

    public void setFinishedPairs(List<OneTimeActivityEntity> oneTimeActivityEntities) {
        mFinishedPairs.getValue().clear();
        for (OneTimeActivityEntity entity : oneTimeActivityEntities) {
            mFinishedPairs.getValue().add(new Pair<>(entity.getActivityId(),entity.getActivityName()));
        }
        mFinishedPairs.setValue(mFinishedPairs.getValue());
    }

    public  LiveData<List<Pair<Long, String>>> getUnfinishedPairs() {
        return mUnfinishedPairs;
    }

    public void setUnfinishedPairs(List<OneTimeActivityEntity> oneTimeActivityEntities) {
        mUnfinishedPairs.getValue().clear();
        for (OneTimeActivityEntity entity : oneTimeActivityEntities) {
            mUnfinishedPairs.getValue().add(new Pair<>(entity.getActivityId(),entity.getActivityName()));
        }
        mUnfinishedPairs.setValue(mUnfinishedPairs.getValue());
    }

}