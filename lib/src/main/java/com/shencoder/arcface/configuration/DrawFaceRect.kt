package com.shencoder.arcface.configuration

import android.graphics.Color

/**
 * 人脸识别框绘制相关
 * @author  ShenBen
 * @date    2021/02/26 9:12
 * @email   714081644@qq.com
 */
data class DrawFaceRect(
    /**
     * 是否绘制人脸框
     */
    val isDraw: Boolean = true,
    /**
     * 未知情况的颜色
     */
    val unknownColor: Int = Color.YELLOW,
    /**
     * 失败的颜色
     */
    val failedColor: Int = Color.RED,
    /**
     * 成功的颜色
     */
    val successColor: Int = Color.GREEN,
    /**
     * RGB人脸框X偏移量
     */
    val rgbOffsetX: Int = 0,
    /**
     * RGB人脸框Y偏移量
     */
    val rgbOffsetY: Int = 0,
    /**
     * IR人脸框X偏移量
     */
    val irOffsetX: Int = 0,
    /**
     * IR人脸框Y偏移量
     */
    val irOffsetY: Int = 0,
)