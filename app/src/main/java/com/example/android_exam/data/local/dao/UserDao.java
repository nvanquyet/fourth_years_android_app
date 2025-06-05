package com.example.android_exam.data.local.dao;

import androidx.room.*;
import com.example.android_exam.data.local.entity.User;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username AND hashedPassword = :hashedPassword LIMIT 1")
    User login(String username, String hashedPassword);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Insert
    long insert(User user);

    @Update
    int update(User user);

    @Delete
    int delete(User user);
}