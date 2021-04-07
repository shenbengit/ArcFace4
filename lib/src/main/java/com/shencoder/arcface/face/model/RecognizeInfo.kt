package com.shencoder.arcface.face.model

import com.arcsoft.face.*
import com.shencoder.arcface.constant.RecognizeStatus

/**
 * 单个人脸（faceId）识别过程中的信息
 * @author  ShenBen
 * @date    2021/03/01 14:41
 * @email   714081644@qq.com
 */
class RecognizeInfo(val faceId: Int) {
    /**
     * 活体值
     */
    var liveness = LivenessInfo.UNKNOWN

    /**
     * 年龄
     */
    var age = AgeInfo.UNKNOWN_AGE

    /**
     * 性别
     */
    var gender = GenderInfo.UNKNOWN

    /**
     * 3D角度信息
     */
    var angle = Face3DAngle()

    /**
     * 是否佩戴口罩
     */
    var mask = MaskInfo.UNKNOWN

    /**
     * 用于记录人脸特征提取出错重试次数
     */
    internal var extractFeatureErrorRetryCount = 0

    /**
     * 用于存储活体检测出错重试次数
     */
    internal var livenessErrorRetryCount = 0

    /**
     * 人脸识别相关状态
     */
    internal var recognizeStatus = RecognizeStatus.TO_RETRY

    /**
     *  人脸识别框上绘制的文字
     */
    internal var msg: String? = null

    /**
     * 特征等活体的lock
     */
    internal val lock = Object()

    /**
     * 重置次数
     */
    fun resetExtractFeatureErrorRetryCount() {
        extractFeatureErrorRetryCount = 0
    }

    fun increaseAndGetExtractFeatureErrorRetryCount(): Int {
        return ++extractFeatureErrorRetryCount
    }

    /**
     * 重置次数
     */
    fun resetLivenessErrorRetryCount() {
        livenessErrorRetryCount = 0
    }

    fun increaseAndGetLivenessErrorRetryCount(): Int {
        return ++livenessErrorRetryCount
    }

    override fun toString(): String {
        return "RecognizeInfo(faceId=$faceId, liveness=$liveness, age=$age, gender=$gender, angle=$angle)"
    }


}