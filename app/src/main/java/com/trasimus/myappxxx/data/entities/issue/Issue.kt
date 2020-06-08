package com.trasim.myapp.data.entities.issue

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.trasim.myapp.R
import com.trasim.myapp.data.entities.issue.models.checkList.CheckList
import com.trasim.myapp.data.entities.issue.models.issueMeta.IssueMeta
import com.trasim.myapp.data.entities.issue.models.permission.Permission
import com.trasim.myapp.data.entities.issue.models.room.Room
import com.trasim.myapp.data.entities.user.User
import com.trasim.myapp.data.entities.user.UserLocal
import com.trasim.myapp.data.entities.user.UserRemote
import com.trasim.base.data.RepositoryType

class Issue(
    val id: String,
    val name: String,
    val description: String,
    val note: String?,
    val priority: Priority,
    var state: IssueState, var room: Room?,
    val type: Type,
    val meta: IssueMeta?, val permissions: Array<Permission>?,
    var assignee: User,
    val owner: User?,
    var checkout: Boolean,
    var checklists: Array<CheckList>
) {

    constructor (issueLocal: IssueLocal) : this(
        issueLocal.id,
        issueLocal.name,
        issueLocal.description,
        issueLocal.note,
        Priority.findPriorityByName(issueLocal.priority),
        IssueState.findStateByName(issueLocal.state), issueLocal.room,
        Type.findStateByName(issueLocal.type),
        issueLocal.meta, issueLocal.permissions,
        issueLocal.assignee.convertToMasterType(),
        issueLocal.owner?.convertToMasterType(),
        issueLocal.checkout,
        issueLocal.checklists
    )

    constructor (remoteIssue: IssueRemote) : this(
        remoteIssue.id,
        remoteIssue.name,
        remoteIssue.description,
        remoteIssue.note,
        Priority.findPriorityByName(remoteIssue.priority),
        IssueState.findStateByName(remoteIssue.state), remoteIssue.room,
        Type.findStateByName(remoteIssue.type),
        remoteIssue.meta, remoteIssue.permissions,
        remoteIssue.assignee.convertToMasterType(),
        remoteIssue.owner?.convertToMasterType(),
        remoteIssue.checkout,
        remoteIssue.checklists
    )
}


@Entity(tableName = "issue")
class IssueLocal(
    @PrimaryKey(autoGenerate = false) var id: String,
    var name: String,
    var description: String,
    var note: String?,
    var priority: String,
    var state: String, var room: Room?,
    var type: String,
    var meta: IssueMeta?, var permissions: Array<Permission>?,
    var assignee: UserLocal,
    var owner: UserLocal?,
    var checkout: Boolean,
    var checklists: Array<CheckList>
) : RepositoryType<Issue> {
    override fun convertToMasterType(): Issue = Issue(this)

    constructor(issueRemote: IssueRemote) : this(
        issueRemote.id,
        issueRemote.name,
        issueRemote.description,
        issueRemote.note,
        issueRemote.priority,
        issueRemote.state, issueRemote.room,
        issueRemote.type,
        issueRemote.meta, issueRemote.permissions,
        UserLocal(issueRemote.assignee),
        if (issueRemote.owner != null) {
            UserLocal(issueRemote.owner)
        } else {
            null
        },
        issueRemote.checkout,
        issueRemote.checklists
    )
}

open class IssueRemote(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("note")
    val note: String?,
    @SerializedName("priority")
    val priority: String,
    @SerializedName("state")
    val state: String, @SerializedName("room") val room: Room?,
    @SerializedName("type")
    val type: String, @SerializedName("meta") val meta: IssueMeta?, @SerializedName("permissions") val permissions: Array<Permission>?,
    @SerializedName("assignee")
    val assignee: UserRemote,
    @SerializedName("owner")
    val owner: UserRemote?,
    @SerializedName("checkout")
    val checkout: Boolean,
    @SerializedName("checklists")
    val checklists: Array<CheckList>
) : RepositoryType<Issue> {
    override fun convertToMasterType(): Issue = Issue(this)

    constructor(issue: Issue) : this(
        issue.id,
        issue.name,
        issue.description,
        issue.note,
        issue.priority.serializedName,
        issue.state.serializedName, issue.room,
        issue.type.serializedName,
        issue.meta, issue.permissions,
        UserRemote(issue.assignee),
        if (issue.owner != null) {
            UserRemote(issue.owner)
        } else {
            null
        },
        issue.checkout,
        issue.checklists
    )
}

enum class Priority(val serializedName: String) {
    HIGH("high"),
    NORMAL("normal"),
    LOW("low");

    companion object {
        fun findPriorityByName(name: String): Priority = values().first { it.serializedName == name }
    }
}

enum class IssueState(val serializedName: String) {
    NEW("new"),
    IN_PROGRESS("in_progress"),
    DONE("done"),
    REJECTED("rejected");

    companion object {
        fun findStateByName(name: String): IssueState = values().first { it.serializedName == name }
    }
}

enum class IssueStateNames(val resId: Int) {
    WAITING(R.string.issue_detail__state_waiting),
    TAKEN(R.string.issue_detail__state_taken),
    PARTIALLY_DONE(R.string.issue_detail__state_partially_done),
    DONE(R.string.issue_detail__state_done),
    DONE_HANDYMAN(R.string.issue_detail__state_done_handyman),
    DO_NOT_DISTURB(R.string.issue_detail__state_do_not_disturb);
}

enum class Type(val serializedName: String) {
    ROOM("room"),
    TASK("task");

    companion object {
        fun findStateByName(name: String): Type = values().first { it.serializedName == name }
    }
}

enum class GalleryItemType(val serializedName: String) {
    IMAGE("image"),
    VIDEO("video");

    companion object {
        fun findStateByName(name: String): GalleryItemType = values().first { it.serializedName == name }
    }
}

enum class CheckListType(val serializedName: String) {
    ITEM("item"),
    LIST("list");

    companion object {
        fun findStateByName(name: String): CheckListType = values().first { it.serializedName == name }
    }
}

fun IssueState.getBackground(): Int = when (this) {
    IssueState.NEW -> {
        R.drawable.shape__rounded_transparent
    }
    IssueState.DONE -> {
        R.drawable.shape__rounded_green
    }
    IssueState.IN_PROGRESS -> {
        R.drawable.shape__rounded_orange
    }
    IssueState.REJECTED -> {
        R.drawable.shape__rounded_red
    }
}

fun IssueState.getTitle(isChairmade: Boolean = true): Int = when (this) {
    IssueState.NEW -> {
        if (isChairmade) {
            R.string.room_states__new
        } else {
            R.string.room_states__new_handyman
        }
    }
    IssueState.DONE -> {
        if (isChairmade) {
            R.string.room_states__done
        } else {
            R.string.room_states__done_handyman
        }
    }
    IssueState.IN_PROGRESS -> {
        R.string.room_states__in_progress
    }
    IssueState.REJECTED -> {
        R.string.room_states__rejected
    }
}

fun IssueState.getColor(): Int = when (this) {
    IssueState.NEW -> {
        R.color.even_more_dark_gray
    }
    IssueState.DONE -> {
        R.color.positive_green
    }
    IssueState.IN_PROGRESS -> {
        R.color.orange
    }
    IssueState.REJECTED -> {
        R.color.red
    }
}
