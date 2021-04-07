package com.shencoder.arcface.callback

/**
 * 激活状态回调
 *
 * @author ShenBen
 * @date 2020/12/15 15:30
 * @email 714081644@qq.com
 */
interface OnActiveCallback {
    /**
     * 激活状态回调
     *
     * @param isSuccess    是否激活成功
     * @param code 激活返回的状态码
     * @see [com.arcsoft.face.ErrorInfo]
     */
    fun activeCallback(isSuccess: Boolean, code: Int)
}