package com.shencoder.arcface.face.model

import android.graphics.Rect
import com.arcsoft.face.FaceInfo
import com.arcsoft.face.MaskInfo
import com.shencoder.arcface.view.FaceRectView

/**
 * 人脸追踪时的信息
 *
 * @author  ShenBen
 * @date    2021/02/26 10:14
 * @email   714081644@qq.com
 */
class FacePreviewInfo constructor(
    val faceId: Int,
    /**
     * RGB人脸信息，包括人脸框和人脸角度
     */
    val faceInfoRgb: FaceInfo,
    /**
     * IR人脸信息，包括人脸框和人脸角度
     */
    val faceInfoIr: FaceInfo
) {

    /**
     * 可见光成像对应的用于[FaceRectView]绘制的Rect
     */
    var rgbTransformedRect: Rect = Rect()

    /**
     * 红外成像对应的用于[FaceRectView]绘制的Rect
     */
    var irTransformedRect: Rect = Rect()

    var imageQuality = 0f

    /**
     * 识别区域是否合法
     */
    var recognizeAreaValid = false

    /**
     * 是否戴口罩
     * [MaskInfo.WORN] 佩戴口罩
     * [MaskInfo.NOT_WORN] 未佩戴口罩
     * [MaskInfo.UNKNOWN] 未知
     */
    var mask: Int = MaskInfo.NOT_WORN
}