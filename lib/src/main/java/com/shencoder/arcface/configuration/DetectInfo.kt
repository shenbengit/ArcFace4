package com.shencoder.arcface.configuration

import com.shencoder.arcface.callback.OnRecognizeCallback

/**
 * 针对算法功能会有常量值与之一一对应，可根据业务需求进行自由选择，不需要的属性可以不用
 * 初始化，否则会占用多余内存。
 *
 * 使用这个功能需要开启活体检测 [FaceConfiguration.livenessType]为[LivenessType.RGB]
 * 具体结果回调在[OnRecognizeCallback.onRecognized]
 *
 * @author  ShenBen
 * @date    2021/02/24 16:46
 * @email   714081644@qq.com
 */
data class DetectInfo(
    /**
     * 检测年龄
     */
    val age: Boolean = false,
    /**
     * 检测性别
     */
    val gender: Boolean = false,
    /**
     * 检测3d角度
     */
    val angle: Boolean = false
)