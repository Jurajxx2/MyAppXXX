package com.trasim.myapp.data.entities.issue.models.checkList

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CheckList(
    @SerializedName("id") var id: String,
    @SerializedName("item type") var type: String,
    @SerializedName("name") var name: String,
    @SerializedName("value") var value: Int,
    @SerializedName("description") var description: String,
    @SerializedName("meta") var meta: CheckListMeta
) : Serializable

class CheckListMeta(@SerializedName("list") val list: Array<BarItem>,
                    @SerializedName("default value") val defaultValue: Int,
                    @SerializedName("title") val title: String,
                    @SerializedName("subtitle") val subtitle: String
)

class BarItem(@SerializedName("name") val name: String, @SerializedName("description") val description: String?, @SerializedName("value") val value: Int, @SerializedName("maxQuantity") val maxQuantity: Int, @SerializedName("id") val id: String) : Serializable