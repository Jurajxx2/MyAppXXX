package com.trasim.myapp.screens.issues.fragments.goodsRefillInfoDialog.handler

import com.trasim.myapp.data.entities.issue.models.checkList.CheckList

interface GoodsRefillInfoHandler {
    fun onOkDoneClick(checkList: CheckList)
}