package com.trasim.myapp.data.entities.issue.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.trasim.myapp.data.entities.issue.IssueLocal

@Dao
interface IssueQueries {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveIssues(issues: List<IssueLocal>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveIssue(issue: IssueLocal)

    @Query("SELECT * FROM issue")
    fun getIssues(): LiveData<List<IssueLocal>?>

    @Query("SELECT * FROM issue WHERE id=:id")
    fun getIssue(id: String): LiveData<IssueLocal?>

    @Query("DELETE FROM issue")
    fun deleteAllIssues()

    @Transaction
    fun saveIssuesTransaction(issues: List<IssueLocal>) {
        this.deleteAllIssues()
        this.saveIssues(issues)
    }
}