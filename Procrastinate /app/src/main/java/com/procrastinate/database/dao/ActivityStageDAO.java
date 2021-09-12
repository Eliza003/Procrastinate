package com.procrastinate.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.procrastinate.database.entity.ActivityStageEntity;
import com.procrastinate.database.entity.LongTermActivityEntity;
import com.procrastinate.database.entity.OneTimeActivityEntity;

import java.util.List;

@Dao
public interface ActivityStageDAO {

    /**
     * 查询单个活动所有step
     */
    @Query("SELECT * FROM activity_stage WHERE activity_id = :activityId ORDER BY _index")
    LiveData<List<ActivityStageEntity>> loadActivityStageList(long activityId);

    /**
     * 根据id查询step
     */
    @Query("SELECT * FROM activity_stage WHERE stage_id = :stage_id ORDER BY _index")
    List<ActivityStageEntity> loadActivityStageListForId(long stage_id);

    /**
     * 插入数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertActivityStage(ActivityStageEntity entity);

    /**
     * 更新数据
     */
    @Update
    int updateActivityStage(ActivityStageEntity entity);

    /**
     * 删除数据
     */
    @Update
    int deleteActivityStage(ActivityStageEntity entity);

}
