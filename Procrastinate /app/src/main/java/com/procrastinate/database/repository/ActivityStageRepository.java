package com.procrastinate.database.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.procrastinate.database.dao.ActivityStageDAO;
import com.procrastinate.database.database.UserDatabase;
import com.procrastinate.database.entity.ActivityStageEntity;

import java.util.List;

public class ActivityStageRepository {

    private volatile static ActivityStageRepository sRepository;

    private ActivityStageDAO mLongTermActivityDAO;

    private ActivityStageRepository() {
    }

    private ActivityStageRepository(Context context) {
        mLongTermActivityDAO = UserDatabase.getInstance(context).mActivityStageDAO();
    }

    public static synchronized ActivityStageRepository getInstance(Context context) {
        if (sRepository == null) {
            sRepository = new ActivityStageRepository(context);
        }
        return sRepository;
    }

    /**
     * 查询单个活动所有step
     *
     * @param activityId 活动id
     * @return step list
     */
    public LiveData<List<ActivityStageEntity>> loadActivityStageList(long activityId) {
        return mLongTermActivityDAO.loadActivityStageList(activityId);
    }

    /**
     * 根据id查询step
     *
     * @param stage_id stage id
     * @return step list
     */
    public List<ActivityStageEntity> loadActivityStageListForId(long stage_id) {
        return mLongTermActivityDAO.loadActivityStageListForId(stage_id);
    }


    /**
     * 更新step数据
     *
     * @return 更新是否成功
     */
    public boolean updateActivityStage(ActivityStageEntity entity) {
        return mLongTermActivityDAO.updateActivityStage(entity) > 0;
    }

}