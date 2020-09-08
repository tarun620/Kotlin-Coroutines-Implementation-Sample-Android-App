package com.tarun.example.coroutines.data.local

import com.tarun.example.coroutines.data.local.entity.User

interface DatabaseHelper {

    suspend fun getUsers(): List<User>

    suspend fun insertAll(users: List<User>)

}