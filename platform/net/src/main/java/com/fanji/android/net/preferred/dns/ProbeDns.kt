package com.fanji.android.net.preferred.dns

import com.fanji.android.net.preferred.model.IpModel
import okhttp3.Dns
import java.net.InetAddress
import java.util.*

/**
 * @author 蒋世德
 * @email 18311271399@163.com
 * @since 21-4-28 下午12:59
 */
internal class ProbeDns(private val ipModel: IpModel) : Dns {
    override fun lookup(hostname: String): MutableList<InetAddress> {
        val inetAddressList = ArrayList<InetAddress>()
        inetAddressList.add(InetAddress.getByAddress(
                ipModel.host, InetAddress.getByName(ipModel.ip).address
        ))
        return inetAddressList
    }
}