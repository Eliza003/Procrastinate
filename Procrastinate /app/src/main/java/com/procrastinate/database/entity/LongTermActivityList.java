package com.procrastinate.database.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

/**
 * 一对多关系
 */
public class LongTermActivityList implements Serializable {

    @Embedded
    public LongTermActivityEntity activity;

    @Relation(
            parentColumn = "activity_id",
            entityColumn = "activity_id"
    )

    public List<ActivityStageEntity> mActivityStageList;

    public LongTermActivityList() {

    }

    public LongTermActivityEntity getActivity() {
        return activity;
    }

    public void setActivity(LongTermActivityEntity activity) {
        this.activity = activity;
    }

    public List<ActivityStageEntity> getActivityStageList() {
        return mActivityStageList;
    }

    public void setActivityStageList(List<ActivityStageEntity> activityStageList) {
        mActivityStageList = activityStageList;
    }

    @Override
    public String toString() {
        return "LongTermActivityList{" +
                "activity=" + activity +
                ", mActivityStageList=" + mActivityStageList +
                '}';
    }
}
