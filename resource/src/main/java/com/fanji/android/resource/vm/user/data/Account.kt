package com.fanji.android.resource.vm.user.data

import android.content.Context

/**
 * created by jiangshide on 2020/5/2.
 * email:18311271399@163.com
 */
class Account {
    var id: Long = 0
    var name: String = ""

    var resId: Int = 0
    var selected: Boolean = false

}

class RechargeQuota {
    var id: Int = 0
    var name: String = ""
    var coin: Int = 0
    var money: Int = 0
    var increase: Int = 0
    var selected: Boolean = false

    fun getArr(context: Context): MutableList<RechargeQuota> {
        val arr = ArrayList<RechargeQuota>()
        val coins = listOf(50, 100, 300, 500, 1000, 2000, 5000)
        listOf(5, 10, 30, 50, 100, 200, 500).forEachIndexed { index, s ->
            val rechargeQuota = RechargeQuota()
            rechargeQuota.selected = index == 0
            rechargeQuota.coin = coins[index]
            rechargeQuota.money = s
            arr.add(rechargeQuota)
        }
        return arr
    }
}


