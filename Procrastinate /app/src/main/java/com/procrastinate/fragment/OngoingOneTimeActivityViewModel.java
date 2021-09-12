package com.procrastinate.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.procrastinate.database.entity.OneTimeActivityEntity;

import java.util.ArrayList;
import java.util.List;

public class OngoingOneTimeActivityViewModel extends ViewModel {

    private MutableLiveData<List<OneTimeActivityEntity>> mListData;

    public OngoingOneTimeActivityViewModel() {
        mListData = new MutableLiveData<>();
        mListData.setValue(new ArrayList<>());
    }

    public LiveData<List<OneTimeActivityEntity>> getListData() {
        return mListData;
    }

    public void setListData(List<OneTimeActivityEntity> list) {
        mListData.getValue().clear();
        mListData.getValue().addAll(list);
        mListData.setValue(mListData.getValue());
    }

}