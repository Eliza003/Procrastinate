package com.procrastinate.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "User")
public class UserEntity {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "user_name")
    private String userName;

    @NonNull
    @ColumnInfo(name = "password")
    private String password;

    public UserEntity() {
    }

    @Ignore
    public UserEntity(@NonNull String userName, @NonNull String password) {
        this.userName = userName;
        this.password = password;
    }

    @NonNull
    public String getUserName() {
        return userName;
    }

    public void setUserName(@NonNull String userName) {
        this.userName = userName;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
