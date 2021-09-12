package com.procrastinate.database.repository;

import android.content.Context;

import com.procrastinate.database.dao.UserDAO;
import com.procrastinate.database.database.UserDatabase;
import com.procrastinate.database.entity.UserEntity;

import java.util.List;

public class UserRepository {

    private volatile static UserRepository sRepository;

    private UserDAO mUserDAO;

    private UserRepository() {
    }

    private UserRepository(Context context) {
        mUserDAO = UserDatabase.getInstance(context).mUserDAO();
    }

    public static synchronized UserRepository getInstance(Context context) {
        if (sRepository == null) {
            sRepository = new UserRepository(context);
        }
        return sRepository;
    }

    /**
     * 注册
     *
     * @param userName 待注册的帐号
     * @param password 待注册的密码
     * @return 注册是否成功
     */
    public boolean register(String userName, String password) {
        UserEntity user = new UserEntity(userName, password);
        List<UserEntity> userList = mUserDAO.loadUserForName(userName);
        if (userList != null && userList.size() != 0)
            return false; // Account already exists
        return mUserDAO.insertUser(user) != -1;
    }

    /**
     * 登录
     *
     * @param userName 待验证的帐号
     * @param password 待验证的密码
     * @return 登陆是否成功
     */
    public boolean login(String userName, String password) {
        List<UserEntity> list = mUserDAO.loadUserForPassword(userName, password);
        return list != null && list.size() != 0;
    }

    /**
     * 修改密码
     *
     * @param userName 待验证的帐号
     * @param password 待修改的密码
     * @return 密码是否修改成功
     */
    public boolean resetPassword(String userName, String password) {
        UserEntity user = new UserEntity(userName, password);
        List<UserEntity> userList = mUserDAO.loadUserForName(userName);
        if (userList == null || userList.size() == 0)
            return false; // Account does not exist
        return mUserDAO.updateUser(user) > 0 ;
    }

}