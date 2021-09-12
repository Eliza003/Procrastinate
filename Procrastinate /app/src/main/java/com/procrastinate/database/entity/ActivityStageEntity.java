package com.procrastinate.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Activity_Stage")
public class ActivityStageEntity implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "stage_id")
    private long stageId;

    /**
     * 一对多关系，保存活动名
     */
    @NonNull
    @ColumnInfo(name = "activity_id")
    private long activityId;

    @NonNull
    @ColumnInfo(name = "stage_name")
    private String stageName;

    @NonNull
    @ColumnInfo(name = "start_time")
    private String startTime;

    @NonNull
    @ColumnInfo(name = "end_time")
    private String endTime;

    /**
     * 第几步，用于显示排序
     */
    @NonNull
    @ColumnInfo(name = "_index")
    private int index;

    /**
     * 类型
     *  0 ：正在进行
     *  1 ：已完成
     *  2 ：未完成
     */
    private int type = 0;

    public ActivityStageEntity() {
    }

    @Ignore
    public ActivityStageEntity(long activityId, @NonNull String stageName, @NonNull String startTime, @NonNull String endTime,int index) {
        this(activityId,stageName,startTime,endTime,index,0);
    }

    @Ignore
    public ActivityStageEntity(long activityId, @NonNull String stageName, @NonNull String startTime, @NonNull String endTime,int index, int type) {
        this.activityId = activityId;
        this.stageName = stageName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.index = index;
        this.type = type;
    }

    public long getStageId() {
        return stageId;
    }

    public void setStageId(long stageId) {
        this.stageId = stageId;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    @NonNull
    public String getStageName() {
        return stageName;
    }

    public void setStageName(@NonNull String stageName) {
        this.stageName = stageName;
    }

    @NonNull
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(@NonNull String startTime) {
        this.startTime = startTime;
    }

    @NonNull
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(@NonNull String endTime) {
        this.endTime = endTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "ActivityStageEntity{" +
                "stageId=" + stageId +
                ", activityId=" + activityId +
                ", stageName='" + stageName + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", index=" + index +
                ", type=" + type +
                '}';
    }
}

