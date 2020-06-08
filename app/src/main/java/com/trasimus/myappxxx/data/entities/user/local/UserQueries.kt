package com.trasim.myapp.data.entities.user.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trasim.myapp.data.entities.user.UserLocal

@Dao
interface UserQueries {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(userLocal: UserLocal): Long

    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): UserLocal?

    @Query("DELETE FROM user")
    fun deleteAllUsers()
}