package com.shencoder.arcface.util

/**
 * 人脸特征码转换工具
 *
 * @author  ShenBen
 * @date    2021/03/03 9:18
 * @email   714081644@qq.com
 */
object FeatureCovertUtil {
    /**
     * 人脸特征码的长度
     */
    private const val FEATURE_BYTE_ARRAY_LENGTH = 1032
    private const val FEATURE_HEX_STRING_LENGTH = FEATURE_BYTE_ARRAY_LENGTH * 2

    /**
     * ByteArray特征码数据转为16进制字符串
     */
    @JvmStatic
    fun byteArrayToHexString(feature: ByteArray): String {
        if (feature.size != FEATURE_BYTE_ARRAY_LENGTH) {
            return AppUtil.encodeHexString(ByteArray(FEATURE_BYTE_ARRAY_LENGTH), false)
        }
        return AppUtil.encodeHexString(feature, false)
    }

    /**
     * 16进制字符串转为ByteArray特征码数据
     */
    @JvmStatic
    fun hexStringToByteArray(hexStr: String): ByteArray {
        if (hexStr.length != FEATURE_HEX_STRING_LENGTH) {
            return ByteArray(FEATURE_BYTE_ARRAY_LENGTH)
        }
        return AppUtil.decodeHex(hexStr)
    }

    /**
     * 判断两个特征码是否相等
     */
    @JvmStatic
    fun isSameFeature(feature1: ByteArray, feature2: ByteArray) = feature1.contentEquals(feature2)
}