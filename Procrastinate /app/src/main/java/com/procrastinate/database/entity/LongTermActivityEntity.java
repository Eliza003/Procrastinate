package com.procrastinate.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Long_Term_Activity")
public class LongTermActivityEntity implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "activity_id")
    private long activityId;

    @NonNull
    @ColumnInfo(name = "activity_name")
    private String activityName;

    @NonNull
    @ColumnInfo(name = "start_time")
    private String startTime;

    /**
     * 一对多关系，保存用户名
     */
    @NonNull
    @ColumnInfo(name = "user_name")
    private String userName;

    public LongTermActivityEntity() {
    }

    @Ignore
    public LongTermActivityEntity(@NonNull String activityName, @NonNull String startTime, String userName) {
        this.activityName = activityName;
        this.startTime = startTime;
        this.userName = userName;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    @NonNull
    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(@NonNull String activityName) {
        this.activityName = activityName;
    }

    @NonNull
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(@NonNull String startTime) {
        this.startTime = startTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "LongTermActivityEntity{" +
                "activityId=" + activityId +
                ", activityName='" + activityName + '\'' +
                ", startTime='" + startTime + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
