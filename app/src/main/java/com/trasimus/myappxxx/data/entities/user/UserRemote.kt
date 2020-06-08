package com.trasim.myapp.data.entities.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.trasim.base.data.RepositoryType
import java.io.Serializable
import java.util.*

class User(var id: String?, var role: String?, var name: String = "", var token: String?) : Serializable {

    constructor(userLocal: UserLocal) : this(userLocal.id, userLocal.role, userLocal.name, userLocal.token)
    constructor(userRemote: UserRemote) : this(userRemote.id, userRemote.role, userRemote.name, userRemote.token)
}

class UserRemote(@SerializedName("id") val id: String?, @SerializedName("role") val role: String?, @SerializedName("name") val name: String, @SerializedName("token") var token: String? = "") :
    RepositoryType<User> {
    override fun convertToMasterType() = User(this)

    constructor(user: User) : this(user.id, user.role, user.name, user.token)
}

@Entity(tableName = "user")
class UserLocal(@PrimaryKey(autoGenerate = false) var id: String, var role: String?, var name: String = "", var token: String?) : RepositoryType<User>, Serializable {
    override fun convertToMasterType() = User(this)

    constructor(userRemote: UserRemote) : this(
        userRemote.id ?: UUID.randomUUID().toString(), userRemote.role, userRemote.name, userRemote.token
    )
}

enum class Role(val code: String) {
    CHAIRMAID("CHYZNA"),
    HANDYMAN("UDRZBAR")
}