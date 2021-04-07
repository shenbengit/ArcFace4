package com.shencoder.arcface.callback


/**
 * 摄像头相关操作
 *
 * @author  ShenBen
 * @date    2021/03/05 10:57
 * @email   714081644@qq.com
 */
interface OnCameraListener {
    /**
     * 摄像头开启异常
     */
    fun onRgbCameraError(exception: Exception) {

    }

    /**
     * 摄像头开启异常
     */
    fun onIrCameraError(exception: Exception) {

    }
}