package com.procrastinate.database.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.procrastinate.database.dao.LongTermActivityDAO;
import com.procrastinate.database.database.UserDatabase;
import com.procrastinate.database.entity.ActivityStageEntity;
import com.procrastinate.database.entity.LongTermActivityList;

import java.util.List;

public class LongTermActivityRepository {

    private volatile static LongTermActivityRepository sRepository;

    private LongTermActivityDAO mLongTermActivityDAO;
    private UserDatabase database;

    private LongTermActivityRepository() {
    }

    private LongTermActivityRepository(Context context) {
        database = UserDatabase.getInstance(context);
        mLongTermActivityDAO = database.mLongTermActivityDAO();
    }

    public static synchronized LongTermActivityRepository getInstance(Context context) {
        if (sRepository == null) {
            sRepository = new LongTermActivityRepository(context);
        }
        return sRepository;
    }

    /**
     * 查询用户所有LongTermActivity，一对多查询。
     *
     * @param userName 用户名
     * @return LongTermActivity列表 ： LiveData
     */
    public LiveData<List<LongTermActivityList>> loadUserLongTermActivityList(String userName) {
        return mLongTermActivityDAO.loadUserLongTermActivityList(userName);
    }

    /**
     * 查询用户所有LongTermActivity，一对多查询。
     *
     * @param userName 用户名
     * @return LongTermActivity列表 ： list
     */
    public List<LongTermActivityList> loadUserLongTermActivityListForUserName(String userName) {
        return mLongTermActivityDAO.loadUserLongTermActivityListForUserName(userName);
    }

    /**
     * 插入LongTermActivity数据，同步插入stage  - 事务处理
     *
     * @param list       待插入的数据
     * @param onCallback 回调接口
     */
    public void insertLongTermActivityList(LongTermActivityList list, OnCallback onCallback) {
        database.runInTransaction(() -> {
            try {
                long activityId = mLongTermActivityDAO.insertLongTermActivity(list.getActivity());
                if (activityId == -1) throw new Exception("activity insert error");
                list.getActivity().setActivityId(activityId);
                for (ActivityStageEntity activityStageEntity : list.getActivityStageList()) {
                    activityStageEntity.setActivityId(activityId);
                    long stageId = database.mActivityStageDAO().insertActivityStage(activityStageEntity);
                    if (stageId == -1) throw new Exception("stage insert error");
                    activityStageEntity.setStageId(stageId);
                }
                onCallback.succeeded();
            } catch (Exception e) {
                onCallback.failure(e.getMessage());
            }
        });
    }

    /**
     * 删除LongTermActivity数据，同步删除stage - 事务处理
     *
     * @param list       待删除的数据
     * @param onCallback 回调接口
     */
    public void deleteLongTermActivityList(LongTermActivityList list, OnCallback onCallback) {
        database.runInTransaction(() -> {
            try {
                int activityDelete = mLongTermActivityDAO.deleteLongTermActivity(list.getActivity());
                Log.e("TAG", "activityDelete : " + activityDelete);
                if (activityDelete == 0) throw new Exception("activity delete error");
                for (ActivityStageEntity activityStageEntity : list.getActivityStageList()) {
                    int stageDelete = database.mActivityStageDAO().deleteActivityStage(activityStageEntity);
                    if (stageDelete == 0) throw new Exception("stage delete error");
                    Log.e("stageDelete", "stageDelete : " + stageDelete);
                }
                onCallback.succeeded();
            } catch (Exception e) {
                onCallback.failure(e.getMessage());
            }
        });
    }

    /**
     * 删除LongTermActivity数据，不删除stage
     *
     * @param list 待删除的数据
     * @return 删除是否成功
     */
    public boolean deleteLongTermActivityList(LongTermActivityList list) {
        int activityDelete = mLongTermActivityDAO.deleteLongTermActivity(list.getActivity());
        return activityDelete > 0;
    }

    public interface OnCallback {
        void succeeded();

        void failure(String error);
    }

}