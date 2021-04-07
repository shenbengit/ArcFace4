package com.shencoder.arcface.callback

import android.graphics.Rect
import com.shencoder.arcface.face.model.FacePreviewInfo

/**
 *
 * @author  ShenBen
 * @date    2021/03/01 14:14
 * @email   714081644@qq.com
 */
internal interface OnPreviewCallback : FaceDetectCallback {
    /**
     * 获取识别限制区域
     */
    fun getRecognizeAreaRect(): Rect

    /**
     * 预览人脸位置信息
     */
    fun onPreviewFaceInfo(previewInfoList: List<FacePreviewInfo>)
}