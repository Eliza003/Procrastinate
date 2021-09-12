package com.procrastinate.database.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.procrastinate.database.dao.OneTimeActivityDAO;
import com.procrastinate.database.database.UserDatabase;
import com.procrastinate.database.entity.OneTimeActivityEntity;

import java.util.List;

public class OneTimeActivityRepository {

    private volatile static OneTimeActivityRepository sRepository;

    private OneTimeActivityDAO mOneTimeActivityDAO;

    private OneTimeActivityRepository() {
    }

    private OneTimeActivityRepository(Context context) {
        mOneTimeActivityDAO = UserDatabase.getInstance(context).mOneTimeActivityDAO();
    }

    public static synchronized OneTimeActivityRepository getInstance(Context context) {
        if (sRepository == null) {
            sRepository = new OneTimeActivityRepository(context);
        }
        return sRepository;
    }

//    /**
//     * 插入OneTimeActivity数据
//     *
//     * @return 插入是否成功
//     */
//    public boolean insertActivity(OneTimeActivityEntity entity) {
//        return mOneTimeActivityDAO.insertActivity(entity) != -1;
//    }

    /**
     * 插入OneTimeActivity数据
     *
     * @return id
     */
    public long insertActivity(OneTimeActivityEntity entity) {
        return mOneTimeActivityDAO.insertActivity(entity);
    }

    /**
     * 查询用户所有OneTimeActivity
     *
     * @param userName 待查询的用户
     * @return 返回所有数据
     */
    public LiveData<List<OneTimeActivityEntity>> loadUserOneTimeActivityList(String userName) {
        return mOneTimeActivityDAO.loadUserOneTimeActivityList(userName);
    }

    /**
     * 根据id查询OneTimeActivity
     *
     * @param activityId activity id
     * @return 返回数据
     */
    public List<OneTimeActivityEntity> loadOneTimeActivityForId(long activityId) {
        return mOneTimeActivityDAO.loadOneTimeActivityForId(activityId);
    }

    /**
     * 查询用户所有OneTimeActivity
     *
     * @param userName 待查询的用户
     * @return 返回所有数据
     */
    public LiveData<List<OneTimeActivityEntity>> loadUserOneTimeActivityListForType(String userName, int type) {
        return mOneTimeActivityDAO.loadUserOneTimeActivityListForType(userName, type);
    }

    /**
     * 更新OneTimeActivity
     *
     * @param entity 待更新的项
     * @return  更新是否成功
     */
    public boolean updateActivity(OneTimeActivityEntity entity) {
        return mOneTimeActivityDAO.updateActivity(entity) > 0;
    }

    /**
     * 删除OneTimeActivity
     *
     * @param id 待删除的项id
     * @return  删除是否成功
     */
    public boolean deleteActivityForId(long id) {
        OneTimeActivityEntity entity = new OneTimeActivityEntity();
        entity.setActivityId(id);
        return mOneTimeActivityDAO.deleteActivity(entity) > 0;
    }

}