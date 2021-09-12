package com.procrastinate.database.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.procrastinate.database.dao.ActivityStageDAO;
import com.procrastinate.database.dao.LongTermActivityDAO;
import com.procrastinate.database.dao.OneTimeActivityDAO;
import com.procrastinate.database.dao.UserDAO;
import com.procrastinate.database.entity.ActivityStageEntity;
import com.procrastinate.database.entity.LongTermActivityEntity;
import com.procrastinate.database.entity.OneTimeActivityEntity;
import com.procrastinate.database.entity.UserEntity;

@Database(entities = {UserEntity.class, OneTimeActivityEntity.class,
        LongTermActivityEntity.class, ActivityStageEntity.class}, version = 4, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "procrastinate_db";

    private volatile static UserDatabase databaseInstance;

    public static synchronized UserDatabase getInstance(Context context) {
        if (databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(context.getApplicationContext(), UserDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration() // 数据库更新时删除数据重新创建，只能测试时打开
                    .build();
        }
        return databaseInstance;
    }

    public abstract UserDAO mUserDAO();

    public abstract OneTimeActivityDAO mOneTimeActivityDAO();

    public abstract LongTermActivityDAO mLongTermActivityDAO();

    public abstract ActivityStageDAO mActivityStageDAO();

}