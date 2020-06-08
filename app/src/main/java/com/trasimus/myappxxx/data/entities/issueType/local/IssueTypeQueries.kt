package com.trasim.myapp.data.entities.issueType.local

import androidx.room.*
import com.trasim.myapp.data.entities.issueType.IssueTypeLocal

@Dao
interface IssueTypeQueries {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveIssueTypes(issues: List<IssueTypeLocal>)

    @Query("DELETE  FROM issueType")
    fun deleteAllIssueTypes()

    @Query("SELECT * FROM issueType")
    fun getIssueTypes(): List<IssueTypeLocal>

    @Transaction
    fun saveIssueTypesTransaction(issueTypes: List<IssueTypeLocal>) {
        this.deleteAllIssueTypes()
        this.saveIssueTypes(issueTypes)
    }
}