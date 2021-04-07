package com.shencoder.arcface.constant

/**
 * 人脸识别相关错误
 *
 * @author  ShenBen
 * @date    2021/03/03 14:01
 * @email   714081644@qq.com
 */
enum class FaceErrorType {
    /**
     * 检测人脸错误
     */
    DETECT_FACES,

    /**
     * 图片质量检测错误
     */
    IMAGE_QUALITY,

    /**
     * 检测年龄错误
     */
    DETECT_AGE,

    /**
     * 检测性别错误
     */
    DETECT_GENDER,

    /**
     * 检测人脸角度错误
     */
    DETECT_ANGLE,

    /**
     * 检测活体错误
     */
    DETECT_LIVENESS,

    /**
     * 提取特征码错误
     */
    EXTRACT_FEATURE,
}