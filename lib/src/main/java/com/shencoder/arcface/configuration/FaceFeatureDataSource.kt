package com.shencoder.arcface.configuration

/**
 * 人脸比对特征码数据源
 *
 * @author  ShenBen
 * @date    2021/03/02 9:28
 * @email   714081644@qq.com
 */
interface FaceFeatureDataSource {
    /**
     * 识别相似度阈值，有效值范围(0.0f,1.0f)，推荐值0.8
     */
    fun similarThreshold(): Float = 0.8f

    /**
     * 待比较人脸数据集合，需要自己封装传入
     */
    fun faceFeatureList(): List<FaceFeatureDataBean> = emptyList()
}