package com.shencoder.arcface.callback

import java.io.File

/**
 * @author ShenBen
 * @date 2020/12/16 17:41
 * @email 714081644@qq.com
 */
interface OnActiveDeviceInfoCallback {
    /**
     * 生成设备信息回调
     *
     * @param isSuccess      是否成功
     * @param code           code
     * @param deviceInfo     设备信息，
     * @param deviceInfoPath 设备信息写入的文件，仅成功时有值
     */
    fun deviceInfoCallback(
        isSuccess: Boolean,
        code: Int,
        deviceInfo: String?,
        deviceInfoPath: File?
    )
}