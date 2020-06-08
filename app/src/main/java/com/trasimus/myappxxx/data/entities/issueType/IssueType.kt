package com.trasim.myapp.data.entities.issueType

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.trasim.base.data.RepositoryType
import kotlinx.android.parcel.Parcelize

class IssueType(val id: String, val code: String, val name: String) {

    constructor (issueTypeLocal: IssueTypeLocal) : this(issueTypeLocal.id, issueTypeLocal.code, issueTypeLocal.name)

    constructor (issueTypeRemote: IssueTypeRemote) : this(issueTypeRemote.id, issueTypeRemote.code, issueTypeRemote.name)
}

@Entity(tableName = "issueType")
@Parcelize
class IssueTypeLocal(@PrimaryKey(autoGenerate = false) var id: String, var code: String, var name: String) : RepositoryType<IssueType>, Parcelable {
    override fun convertToMasterType(): IssueType = IssueType(this)

    constructor(issueTypeRemote: IssueTypeRemote) : this(issueTypeRemote.id, issueTypeRemote.code, issueTypeRemote.name)
}

open class IssueTypeRemote(@SerializedName("id") val id: String, @SerializedName("code") val code: String, @SerializedName("name") val name: String) : RepositoryType<IssueType> {
    override fun convertToMasterType(): IssueType = IssueType(this)

    constructor(issue: IssueType) : this(issue.id, issue.code, issue.name)
}
