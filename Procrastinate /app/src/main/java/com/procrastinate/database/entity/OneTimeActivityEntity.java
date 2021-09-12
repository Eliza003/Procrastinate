package com.procrastinate.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "One_Time_Activity")
public class OneTimeActivityEntity implements Serializable {

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

    @NonNull
    @ColumnInfo(name = "location")
    private String location;

    @NonNull
    @ColumnInfo(name = "description")
    private String description;

    /**
     * 一对多关系，保存用户名
     */
    @NonNull
    @ColumnInfo(name = "user_name")
    private String userName;

    /**
     * 类型
     *  0 ：正在进行
     *  1 ：已完成
     *  2 ：未完成
     */
    private int type = 0;

    public OneTimeActivityEntity() {
    }

    @Ignore
    public OneTimeActivityEntity(@NonNull String activityName, @NonNull String startTime, @NonNull String location, @NonNull String description, String userName) {
        this(activityName,startTime, location,description,userName,0);
    }

    @Ignore
    public OneTimeActivityEntity(@NonNull String activityName, @NonNull String startTime, @NonNull String location, @NonNull String description, String userName, int type) {
        this.activityName = activityName;
        this.startTime = startTime;
        this.location = location;
        this.description = description;
        this.userName = userName;
        this.type = type;
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

    @NonNull
    public String getLocation() {
        return location;
    }

    public void setLocation(@NonNull String location) {
        this.location = location;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "OneTimeActivityEntity{" +
                "activityId=" + activityId +
                ", activityName='" + activityName + '\'' +
                ", startTime='" + startTime + '\'' +
                ", startLocation='" + location + '\'' +
                ", description='" + description + '\'' +
                ", userName='" + userName + '\'' +
                ", type=" + type +
                '}';
    }

}
