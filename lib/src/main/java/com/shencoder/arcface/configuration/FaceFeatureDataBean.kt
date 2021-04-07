package com.shencoder.arcface.configuration

/**
 * 人脸比对转换bean
 *
 * @author  ShenBen
 * @date    2021/03/02 9:35
 * @email   714081644@qq.com
 */

data class FaceFeatureDataBean(
    /**
     * 泛型数据
     */
    private val data: Any,
    /**
     * 人脸特征码二进制数据
     */
    val feature: ByteArray
) {
    val dataClass = data::javaClass.get()

    @Suppress("UNCHECKED_CAST")
    fun <T> getData(): T {
        return data as T
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FaceFeatureDataBean

        if (data != other.data) return false
        if (!feature.contentEquals(other.feature)) return false
        if (dataClass != other.dataClass) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + feature.contentHashCode()
        result = 31 * result + dataClass.hashCode()
        return result
    }

}