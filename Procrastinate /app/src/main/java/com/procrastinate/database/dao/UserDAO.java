package com.procrastinate.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.procrastinate.database.entity.UserEntity;

import java.util.List;

@Dao
public interface UserDAO {

    /**
     * 插入用户
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(UserEntity user);

    /**
     * 更新用户
     */
    @Update
    int updateUser(UserEntity user);

    /**
     * 按照用户名查询用户
     */
    @Query("SELECT * FROM User WHERE user_name = :userName")
    List<UserEntity> loadUserForName(String userName);

    /**
     *
     */
    @Query("SELECT * FROM User WHERE user_name = :userName AND password = :password")
    List<UserEntity> loadUserForPassword(String userName, String password);

}
