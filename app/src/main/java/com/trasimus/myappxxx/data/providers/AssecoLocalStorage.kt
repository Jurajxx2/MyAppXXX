package com.trasim.myapp.data.providers

import androidx.room.*
import com.trasim.myapp.Session
import com.trasim.myapp.data.entities.internal.local.InternalQueries
import com.trasim.myapp.data.entities.issue.IssueLocal
import com.trasim.myapp.data.entities.issue.local.IssueQueries
import com.trasim.myapp.data.entities.issue.models.checkList.CheckList
import com.trasim.myapp.data.entities.issue.models.issueMeta.IssueMeta
import com.trasim.myapp.data.entities.issue.models.permission.Permission
import com.trasim.myapp.data.entities.issueType.IssueTypeLocal
import com.trasim.myapp.data.entities.issueType.local.IssueTypeQueries
import com.trasim.myapp.data.entities.user.UserLocal
import com.trasim.myapp.data.entities.user.local.UserQueries
import com.trasim.base.data.local.LocalStorage
import com.trasim.base.helpers.jsonToObject
import com.trasim.base.helpers.objectToJson


class MyAppLocalStorage : LocalStorage() {

    val userQueries: UserQueries = (this.applicationDatabase as RoomApplicationDatabase).userQueries()
    val issueQueries: IssueQueries = (this.applicationDatabase as RoomApplicationDatabase).issueQueries()
    val issueTypeQueries: IssueTypeQueries = (this.applicationDatabase as RoomApplicationDatabase).issueTypeQueries()
    val internalQueries: InternalQueries = InternalQueries(this.sharedPreferencesManager)

    override fun createDatabase(): RoomDatabase = Room.inMemoryDatabaseBuilder(Session.application, RoomApplicationDatabase::class.java).build()

    @Database(entities = [UserLocal::class, IssueLocal::class, IssueTypeLocal::class], version = 1)
    @TypeConverters(IssueMetaConverter::class, UserConverter::class, CheckListConverter::class, PermissionsConverter::class, RoomConverter::class)
    abstract class RoomApplicationDatabase : RoomDatabase() {
        abstract fun userQueries(): UserQueries
        abstract fun issueQueries(): IssueQueries
        abstract fun issueTypeQueries(): IssueTypeQueries
    }
}

class IssueMetaConverter {
    @TypeConverter
    fun toIssueMeta(value: String?) = if (value == null) null else jsonToObject(value, IssueMeta::class.java)

    @TypeConverter
    fun toIssueMetaString(value: IssueMeta?) = if (value == null) null else objectToJson(value)
}

class RoomConverter {
    @TypeConverter
    fun toRoom(value: String?) =
        if (value == null) null else jsonToObject(value, com.trasim.myapp.data.entities.issue.models.room.Room::class.java)

    @TypeConverter
    fun toRoomString(value: com.trasim.myapp.data.entities.issue.models.room.Room?) =
        if (value == null) null else objectToJson(value)
}

class UserConverter {
    @TypeConverter
    fun toUser(value: String?) = if (value == null) null else jsonToObject(value, UserLocal::class.java)

    @TypeConverter
    fun toUserString(value: UserLocal?) = if (value == null) null else objectToJson(value)
}

class CheckListConverter {
    @TypeConverter
    fun toCheckList(value: String?) = if (value == null) null else jsonToObject(value, Array<CheckList>::class.java)

    @TypeConverter
    fun toCheckListString(value: Array<CheckList>?) = if (value == null) null else objectToJson(value)
}

class PermissionsConverter {
    @TypeConverter
    fun toPermissions(value: String?) = if (value == null) null else jsonToObject(value, Array<Permission>::class.java)

    @TypeConverter
    fun toPermissionsString(value: Array<Permission>?) = if (value == null) null else objectToJson(value)
}