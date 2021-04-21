package com.shencoder.arcface.callback

import com.shencoder.arcface.configuration.FaceFeatureDataBean
import com.shencoder.arcface.configuration.FaceFeatureDataSource
import com.shencoder.arcface.face.model.RecognizeInfo
import com.shencoder.arcface.configuration.FaceConfiguration


/**
 * 人脸识别结果回调
 *
 * @author ShenBen
 * @date 2020/12/17 9:17
 * @email 714081644@qq.com
 */
interface OnRecognizeCallback : FaceFeatureDataSource, FaceDetectCallback {

    /**
     * 如果不想自动比对的话，可以通过此接口返回识别到的人脸特征码，仅在[FaceConfiguration.enableCompareFace] 为false时才会回调
     * <p>运行在子线程</p>
     *
     * @param faceId 人脸Id
     * @param feature 人脸特征码
     * @param recognizeInfo 识别到的其他信息，包含活体值、年龄、性别、人脸角度等信息
     * @param nv21 camera预览数据
     * @param width 预览数据宽度
     * @param height 预览数据高度
     */
    fun onGetFaceFeature(
        faceId: Int,
        feature: ByteArray,
        recognizeInfo: RecognizeInfo,
        nv21: ByteArray,
        width: Int,
        height: Int
    ) {

    }

    /**
     * 识别成功后结果回调，仅回调一次，直到人脸离开画面
     * <p>运行在子线程</p>
     *
     * @param bean 识别的数据 [faceFeatureList] 的子项
     * @param similar 识别通过的相似度
     * @param recognizeInfo 识别到的其他信息，包含活体值、年龄、性别、人脸角度等信息
     * @param nv21 camera预览数据
     * @param width 预览数据宽度
     * @param height 预览数据高度
     *
     * @return 人脸绘制框上成功时绘制的文字
     */
    fun onRecognized(
        bean: FaceFeatureDataBean,
        similar: Float,
        recognizeInfo: RecognizeInfo,
        nv21: ByteArray,
        width: Int,
        height: Int
    ): String? {
        return null
    }
}