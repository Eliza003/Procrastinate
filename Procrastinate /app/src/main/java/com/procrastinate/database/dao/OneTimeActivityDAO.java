package com.procrastinate.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.procrastinate.database.entity.OneTimeActivityEntity;

import java.util.List;

@Dao
public interface OneTimeActivityDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertActivity(OneTimeActivityEntity entity);


    @Update
    int updateActivity(OneTimeActivityEntity entity);

    @Delete
    int deleteActivity(OneTimeActivityEntity entity);

    /**
     * 查询用户所有OneTimeActivity
     */
    @Query("SELECT * FROM one_time_activity WHERE user_name = :userName")
    LiveData<List<OneTimeActivityEntity>> loadUserOneTimeActivityList(String userName);

    /**
     * 根据id查询OneTimeActivity
     */
    @Query("SELECT * FROM one_time_activity WHERE activity_id = :activityId")
    List<OneTimeActivityEntity> loadOneTimeActivityForId(long activityId);

    /**
     * 查询用户所有OneTimeActivity : 根据type查询
     */
    @Query("SELECT * FROM one_time_activity WHERE user_name = :userName AND type = :type")
    LiveData<List<OneTimeActivityEntity>> loadUserOneTimeActivityListForType(String userName, int type);

}
