package com.shencoder.arcface.configuration

/**
 * 活体检测类型
 *
 * @author  ShenBen
 * @date    2021/02/24 14:17
 * @email   714081644@qq.com
 */
enum class LivenessType {
    /**
     * 不启用活体检测
     */
    NONE,

    /**
     * RGB活体检测，可见光活体检测
     */
    RGB,

    /**
     * IR活体检测，红外活体检测
     */
    IR
}