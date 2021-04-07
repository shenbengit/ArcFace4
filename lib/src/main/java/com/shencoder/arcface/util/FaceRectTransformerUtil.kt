package com.shencoder.arcface.util

import android.graphics.Rect

/**
 * 将检测回传的人脸框（基于NV21数据）转换为View绘制（基于View）所需的人脸框
 * @author  ShenBen
 * @date    2021/02/26 13:38
 * @email   714081644@qq.com
 */
object FaceRectTransformerUtil {
    /**
     * 调整人脸框用来绘制
     *
     * @param previewWidth 相机预览宽度
     * @param previewHeight 相机预览高度
     * @param canvasWidth 绘制控件的宽度
     * @param canvasHeight 绘制控件的高度
     * @param isMirror 是否水平镜像显示（若相机是镜像显示的，设为true，用于纠正）
     * @param detectRect 人脸检测Rect
     *
     * @return 调整后的需要被绘制到View上的rect
     */
    @JvmStatic
    fun adjustRect(
        previewWidth: Int,
        previewHeight: Int,
        canvasWidth: Int,
        canvasHeight: Int,
        isMirror: Boolean,
        detectRect: Rect,
        offsetX: Int = 0,
        offsetY: Int = 0
    ): Rect {
        //当前宽高比
        val current = AspectRatio.of(canvasWidth, canvasHeight)
        //目标宽高比
        val target = AspectRatio.of(previewWidth, previewHeight)

        //转换后的Rect
        val rect = Rect()
        //偏移量
        val offset: Int
        //缩放比
        val ratio: Float
        //缩放后的left
        val left: Int
        //缩放后的top
        val top: Int
        //缩放后的right
        val right: Int
        //缩放后的bottom
        val bottom: Int

        if (current.toFloat() >= target.toFloat()) {//当前宽高比大于目标宽高比
            val scaleY = current.toFloat() / target.toFloat()
            //缩放后的高度
            val ratioHeight = (canvasHeight * scaleY).toInt()
            //偏移量
            offset = (ratioHeight shr 1) - (canvasHeight shr 1)
            //缩放比
            ratio = canvasWidth.toFloat() / previewWidth.toFloat()
            left = (detectRect.left * ratio).toInt()
            top = (detectRect.top * ratio).toInt()
            right = (detectRect.right * ratio).toInt()
            bottom = (detectRect.bottom * ratio).toInt()
            rect.left = if (isMirror) {
                canvasWidth - right
            } else {
                left
            }
            rect.top = top - offset
            rect.right = if (isMirror) {
                canvasWidth - left
            } else {
                right
            }
            rect.bottom = bottom - offset
        } else {//当前宽高比小于目标宽高比
            val scaleX = target.toFloat() / current.toFloat()
            //缩放后的宽度
            val ratioWidth = (canvasWidth * scaleX).toInt()
            //偏移量
            offset = (ratioWidth shr 1) - (canvasWidth shr 1)
            //缩放比
            ratio = ratioWidth.toFloat() / previewWidth.toFloat()

            left = (detectRect.left * ratio).toInt()
            top = (detectRect.top * ratio).toInt()
            right = (detectRect.right * ratio).toInt()
            bottom = (detectRect.bottom * ratio).toInt()

            rect.left = if (isMirror) {
                ratioWidth - right - offset
            } else {
                left - offset
            }
            rect.top = top
            rect.right = if (isMirror) {
                ratioWidth - left - offset
            } else {
                right - offset
            }
            rect.bottom = bottom
        }
        rect.offset(offsetX,offsetY)
        return rect
    }

    /**
     * RGB人脸框转换成IR人脸框
     * @param rgbRect RGB人脸框
     * @param zoomRatio 缩放比
     * @param offsetX 偏移量X
     * @param offsetY 偏移量Y
     * @return IR人脸框
     */
    @JvmStatic
    fun rgbRectToIrRect(rgbRect: Rect, zoomRatio: Float, offsetX: Int = 0, offsetY: Int = 0): Rect {
        val rect = Rect(
            (rgbRect.left * zoomRatio).toInt(),
            (rgbRect.top * zoomRatio).toInt(),
            (rgbRect.right * zoomRatio).toInt(),
            (rgbRect.bottom * zoomRatio).toInt()
        )
        rect.offset(offsetX,offsetY)
        return rect
    }
}