package com.example.mobile.data.repository

import com.example.mobile.data.local.User
import com.example.mobile.data.local.UserDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserRepository(private val userDao: UserDao) {

    val userFlow: Flow<User?> = userDao.getUser()

    suspend fun updatePoints(points: Int) {
        userDao.updatePoints(points)
    }

    suspend fun changePoints(change: Int) {
        userDao.changePoints(change)
    }

    suspend fun updateName(name: String) {
        userDao.updateName(name)
    }

    suspend fun initializeUserIfEmpty(defaultName: String, defaultPoints: Int) {
        if (userFlow.first() == null) {
            userDao.insertUser(User(name = defaultName, points = defaultPoints))
        }
    }
}
