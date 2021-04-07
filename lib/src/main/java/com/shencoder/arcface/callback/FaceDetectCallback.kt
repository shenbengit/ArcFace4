package com.shencoder.arcface.callback

/**
 * 人脸检测Callback
 * @author  ShenBen
 * @date    2021/03/26 9:39
 * @email   714081644@qq.com
 */
interface FaceDetectCallback {

    /**
     * 有人，仅在有变化时调用一次
     * <p>运行在子线程</p>
     */
    fun someone() {

    }

    /**
     * 无人，仅在有变化时调用一次
     * <p>运行在子线程</p>
     */
    fun nobody() {

    }

    /**
     * 检测到的人脸数量
     * <p>运行在子线程</p>
     *
     * @param num 人脸数量
     * @param faceIds faceId
     */
    fun detectFaceNum(num: Int, faceIds: List<Int>) {

    }
}