package com.procrastinate.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.procrastinate.database.entity.LongTermActivityEntity;
import com.procrastinate.database.entity.LongTermActivityList;

import java.util.List;

@Dao
public interface LongTermActivityDAO {

    /**
     * 查询用户所有LongTermActivity，一对多查询。
     */
    @Transaction
    @Query("SELECT * FROM long_term_activity WHERE user_name = :userName")
    LiveData<List<LongTermActivityList>> loadUserLongTermActivityList(String userName);

    /**
     * 查询用户所有LongTermActivity，一对多查询。 : 返回list
     */
    @Transaction
    @Query("SELECT * FROM long_term_activity WHERE user_name = :userName")
    List<LongTermActivityList> loadUserLongTermActivityListForUserName(String userName);

    /**
     * 插入数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertLongTermActivity(LongTermActivityEntity entity);

    /**
     * 删除数据
     */
    @Delete
    int deleteLongTermActivity(LongTermActivityEntity entity);

}
