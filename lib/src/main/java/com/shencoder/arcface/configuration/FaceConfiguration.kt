package com.shencoder.arcface.configuration

import android.content.Context
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.shencoder.arcface.callback.OnErrorCallback
import com.shencoder.arcface.callback.OnRecognizeCallback
import com.shencoder.arcface.view.ViewfinderView
import io.fotoapparat.configuration.CameraConfiguration

/**
 * 人脸识别相关配置
 *
 * @author  ShenBen
 * @date    2021/02/05 15:25
 * @email   714081644@qq.com
 */
class FaceConfiguration internal constructor(builder: Builder) {

    companion object {

        @JvmStatic
        fun builder(context: Context, recognizeCallback: OnRecognizeCallback?): Builder =
            Builder(context, recognizeCallback)

        /**
         * 默认配置
         */
        @JvmStatic
        fun default(context: Context, recognizeCallback: OnRecognizeCallback?): FaceConfiguration =
            builder(context, recognizeCallback).build()
    }

    val context: Context = builder.context

    val recognizeCallback: OnRecognizeCallback? = builder.recognizeCallback

    /**
     * 是否需要识别
     */
    val enableRecognize: Boolean = builder.enableRecognize

    /**
     * 人脸检测角度
     */
    val detectFaceOrient: DetectFaceOrient = builder.detectFaceOrient

    /**
     * 活体检测类型
     */
    val livenessType: LivenessType = builder.livenessType

    /**
     * 设置RGB可见光活体阈值，有效值范围(0.0f,1.0f)，推荐值为0.6f
     */
    val rgbLivenessThreshold: Float = builder.rgbLivenessThreshold

    /**
     * 设置IR红外活体阈值，有效值范围(0.0f,1.0f)，推荐值为0.7f
     */
    val irLivenessThreshold: Float = builder.irLivenessThreshold

    /**
     * 是否启用图像质量阈值
     */
    val enableImageQuality: Boolean = builder.enableImageQuality

    /**
     * 图像质量阈值，有效值范围(0.0f,1.0f)
     */
    val imageQualityThreshold: Float = builder.imageQualityThreshold

    /**
     * 最大需要检测的人脸个数，取值范围[1,50]
     */
    val detectFaceMaxNum: Int = builder.detectFaceMaxNum

    /**
     * 是否仅识别最大人脸
     */
    val recognizeKeepMaxFace: Boolean = builder.recognizeKeepMaxFace

    /**
     * 是否限制识别区域
     */
    val enableRecognizeAreaLimited: Boolean = builder.enableRecognizeAreaLimited

    /**
     * 识别区域屏占比，默认在摄像预览画面中间，有效值范围(0.0f,1.0f)
     */
    val recognizeAreaLimitedRatio: Float = builder.recognizeAreaLimitedRatio

    /**
     * 相关属性检测，年龄、性别、3d角度
     */
    val detectInfo: DetectInfo = builder.detectInfo

    /**
     * 彩色RGB摄像头类型
     */
    val rgbCameraFcing: CameraFacing = builder.rgbCameraFcing

    /**
     * 红外IR摄像头类型
     */
    val irCameraFcing: CameraFacing = builder.irCameraFcing

    /**
     * 摄像头预览分辨率，为null时自动计算
     * 预览分辨率是[rgbCameraFcing] [irCameraFcing] 都支持的预览分辨率
     */
    val previewSize: PreviewSize? = builder.previewSize

    /**
     * 人脸识别框绘制相关
     */
    val drawFaceRect: DrawFaceRect = builder.drawFaceRect

    /**
     * RGB是否镜像预览
     */
    val isRgbMirror: Boolean = builder.isRgbMirror

    /**
     * IR是否镜像预览
     */
    val isIrMirror: Boolean = builder.isIrMirror

    /**
     * 人脸特征提取出错重试次数
     */
    @IntRange(from = 1)
    val extractFeatureErrorRetryCount: Int = builder.extractFeatureErrorRetryCount

    /**
     * 人脸识别失败后，重试间隔，单位：毫秒
     */
    @IntRange(from = 1)
    val recognizeFailedRetryInterval: Long = builder.recognizeFailedRetryInterval

    /**
     * 体检测出错重试次数
     */
    @IntRange(from = 1)
    val livenessErrorRetryCount: Int = builder.livenessErrorRetryCount

    /**
     * 活体检测失败后，重试间隔，单位：毫秒
     */
    @IntRange(from = 1)
    val livenessFailedRetryInterval: Long = builder.livenessFailedRetryInterval

    /**
     * 是否启用人脸比对
     */
    val enableCompareFace: Boolean = builder.enableCompareFace

    /**
     * 扫描框提示文字
     */
    val viewfinderText: String? = builder.viewfinderText

    /**
     * 扫描框提示文字位置
     */
    val viewfinderTextGravity: ViewfinderView.TextLocation = builder.viewfinderTextGravity

    /**
     * 是否启用口罩识别
     */
    val enableMask: Boolean = builder.enableMask

    /**
     * 人脸识别时异常回调
     */
    val onErrorCallback: OnErrorCallback? = builder.onErrorCallback

    class Builder internal constructor(
        internal val context: Context,
        val recognizeCallback: OnRecognizeCallback?
    ) {
        /**
         * 人脸检测角度
         */
        internal var detectFaceOrient: DetectFaceOrient = DetectFaceOrient.ASF_OP_0_ONLY
        fun setDetectFaceOrient(detectFaceOrient: DetectFaceOrient) =
            apply { this.detectFaceOrient = detectFaceOrient }

        /**
         * 是否需要识别
         */
        internal var enableRecognize = true
        fun enableRecognize(enableRecognize: Boolean) =
            apply { this.enableRecognize = enableRecognize }

        /**
         * 活体检测类型
         */
        internal var livenessType: LivenessType = LivenessType.NONE
        fun setLivenessType(livenessType: LivenessType) =
            apply { this.livenessType = livenessType }

        /**
         * 设置RGB可见光活体阈值，有效值范围(0.0f,1.0f)，推荐值为0.6f
         */
        internal var rgbLivenessThreshold: Float = 0.6f
        fun setRgbLivenessThreshold(
            @FloatRange(
                from = 0.0,
                to = 1.0,
                fromInclusive = false,
                toInclusive = false
            ) rgbLivenessThreshold: Float
        ) =
            apply { this.rgbLivenessThreshold = rgbLivenessThreshold }

        /**
         * 设置IR红外活体阈值，有效值范围(0.0f,1.0f)，推荐值为0.7f
         */
        internal var irLivenessThreshold: Float = 0.7f
        fun setIrLivenessThreshold(
            @FloatRange(
                from = 0.0,
                to = 1.0,
                fromInclusive = false,
                toInclusive = false
            ) irLivenessThreshold: Float
        ) =
            apply { this.irLivenessThreshold = irLivenessThreshold }

        /**
         * 是否启用图像质量检测
         */
        internal var enableImageQuality = false
        fun enableImageQuality(
            enableImageQuality: Boolean
        ) =
            apply { this.enableImageQuality = enableImageQuality }

        /**
         * 图像质量阈值，有效值范围(0.0f,1.0f)
         */
        internal var imageQualityThreshold: Float = 0.35f
        fun setImageQualityThreshold(
            @FloatRange(
                from = 0.0,
                to = 1.0,
                fromInclusive = false,
                toInclusive = false
            ) imageQualityThreshold: Float
        ) =
            apply { this.imageQualityThreshold = imageQualityThreshold }

        /**
         * 最大需要检测的人脸个数，取值范围[1,50]
         */
        internal var detectFaceMaxNum = 1
        fun setDetectFaceMaxNum(@IntRange(from = 1, to = 50) detectFaceMaxNum: Int) =
            apply { this.detectFaceMaxNum = detectFaceMaxNum }

        /**
         * 是否仅识别最大人脸
         */
        internal var recognizeKeepMaxFace = true
        fun recognizeKeepMaxFace(recognizeKeepMaxFace: Boolean) =
            apply { this.recognizeKeepMaxFace = recognizeKeepMaxFace }

        /**
         * 是否限制识别区域
         */
        internal var enableRecognizeAreaLimited = false
        fun enableRecognizeAreaLimited(enableRecognizeAreaLimited: Boolean) =
            apply { this.enableRecognizeAreaLimited = enableRecognizeAreaLimited }

        /**
         * 识别区域屏占比，默认在摄像预览画面中间，有效值范围(0.0f,1.0f)
         * 限制区域为正方形
         */
        internal var recognizeAreaLimitedRatio = 0.625f
        fun setRecognizeAreaLimitedRatio(
            @FloatRange(
                from = 0.0,
                to = 1.0,
                fromInclusive = false,
                toInclusive = false
            ) recognizeAreaLimitedRatio: Float
        ) =
            apply { this.recognizeAreaLimitedRatio = recognizeAreaLimitedRatio }

        /**
         * 相关属性检测，年龄、性别、3d角度
         * 这个功能依附于 [livenessType]，需要为[LivenessType.RGB]
         */
        internal var detectInfo: DetectInfo = DetectInfo()
        fun setDetectInfo(detectInfo: DetectInfo) =
            apply {
                this.detectInfo = detectInfo
            }

        /**
         * 彩色RGB摄像头类型
         */
        internal var rgbCameraFcing = CameraFacing.BACK
        fun setRgbCameraFcing(rgbCameraFcing: CameraFacing) =
            apply { this.rgbCameraFcing = rgbCameraFcing }

        /**
         * 红外IR摄像头类型
         */
        internal var irCameraFcing = CameraFacing.FRONT
        fun setIrCameraFcing(irCameraFcing: CameraFacing) =
            apply { this.irCameraFcing = irCameraFcing }

        /**
         * 摄像头预览分辨率，为null时自动计算，
         * 可能[rgbCameraFcing] [irCameraFcing] 预览的分辨率不一致，导致IR活体检测异常
         * <p>最好手动设置</p>
         * 预览分辨率是[rgbCameraFcing] [irCameraFcing] 都支持的预览分辨率
         */
        internal var previewSize: PreviewSize? = null
        fun setPreviewSize(previewSize: PreviewSize) = apply { this.previewSize = previewSize }

        /**
         * 人脸识别框绘制相关
         */
        internal var drawFaceRect: DrawFaceRect = DrawFaceRect()
        fun setDrawFaceRect(drawFaceRect: DrawFaceRect) = apply {
            this.drawFaceRect = drawFaceRect
        }

        /**
         * RGB是否镜像预览
         */
        internal var isRgbMirror = false
        fun isRgbMirror(isRgbMirror: Boolean) = apply {
            this.isRgbMirror = isRgbMirror
        }

        /**
         * IR是否镜像预览
         */
        internal var isIrMirror = false
        fun isIrMirror(isIrMirror: Boolean) = apply {
            this.isIrMirror = isIrMirror
        }

        /**
         * 人脸特征提取出错重试次数
         */
        @IntRange(from = 1)
        internal var extractFeatureErrorRetryCount = 3
        fun setExtractFeatureErrorRetryCount(@IntRange(from = 1) extractFeatureErrorRetryCount: Int) =
            apply {
                this.extractFeatureErrorRetryCount = extractFeatureErrorRetryCount
            }

        /**
         * 人脸识别失败后，重试间隔，单位：毫秒
         */
        @IntRange(from = 1)
        internal var recognizeFailedRetryInterval: Long = 1000
        fun setRecognizeFailedRetryInterval(@IntRange(from = 1) recognizeFailedRetryInterval: Long) =
            apply {
                this.recognizeFailedRetryInterval = recognizeFailedRetryInterval
            }

        /**
         * 体检测出错重试次数
         */
        @IntRange(from = 1)
        internal var livenessErrorRetryCount = 3
        fun setLivenessErrorRetryCount(@IntRange(from = 1) livenessErrorRetryCount: Int) = apply {
            this.livenessErrorRetryCount = livenessErrorRetryCount
        }

        /**
         * 活体检测失败后，重试间隔，单位：毫秒
         */
        @IntRange(from = 1)
        internal var livenessFailedRetryInterval: Long = 1000
        fun setLivenessFailedRetryInterval(@IntRange(from = 1) livenessFailedRetryInterval: Long) =
            apply {
                this.livenessFailedRetryInterval = livenessFailedRetryInterval
            }

        /**
         * 是否启用人脸比对
         */
        internal var enableCompareFace = true
        fun enableCompareFace(enableCompareFace: Boolean) =
            apply {
                this.enableCompareFace = enableCompareFace
            }

        /**
         * 扫描框提示文字
         */
        internal var viewfinderText: String? = null
        fun setViewfinderText(viewfinderText: String?) =
            apply {
                this.viewfinderText = viewfinderText
            }

        /**
         * 扫描框提示文字位置
         */
        internal var viewfinderTextGravity = ViewfinderView.TextLocation.BOTTOM
        fun setViewfinderGravity(viewfinderTextGravity: ViewfinderView.TextLocation) =
            apply {
                this.viewfinderTextGravity = viewfinderTextGravity
            }

        /**
         * 是否启用口罩识别
         */
        internal var enableMask: Boolean = false
        fun enableMask(enableMask: Boolean) = apply { this.enableMask = enableMask }

        /**
         * 人脸识别时异常回调
         */
        internal var onErrorCallback: OnErrorCallback? = null
        fun setOnErrorCallback(onErrorCallback: OnErrorCallback?) =
            apply {
                this.onErrorCallback = onErrorCallback
            }

        fun build(): FaceConfiguration {
            if (recognizeCallback == null) {
                //如果识别结果回掉为空，则直接强制不启用识别操作
                enableRecognize = false
            }
            return FaceConfiguration(this)
        }
    }
}