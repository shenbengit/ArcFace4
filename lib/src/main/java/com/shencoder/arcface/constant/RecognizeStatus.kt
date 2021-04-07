package com.shencoder.arcface.constant

/**
 *
 * @author  ShenBen
 * @date    2021/03/01 14:47
 * @email   714081644@qq.com
 */
enum class RecognizeStatus {
    /**
     * 待重试
     */
    TO_RETRY,
    /**
     * 比对中
     */
    SEARCHING,

    /**
     * 识别成功
     */
    SUCCEED,

    /**
     * 识别失败
     */
    FAILED,
}