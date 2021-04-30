package com.shencoder.arcface.view

import android.bluetooth.BluetoothClass
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.arcsoft.face.LivenessInfo
import com.shencoder.arcface.R
import com.shencoder.arcface.callback.OnCameraListener
import com.shencoder.arcface.callback.OnPreviewCallback
import com.shencoder.arcface.configuration.CameraFacing
import com.shencoder.arcface.configuration.FaceConfiguration
import com.shencoder.arcface.configuration.LivenessType
import com.shencoder.arcface.configuration.PreviewSize
import com.shencoder.arcface.face.FaceHelper
import com.shencoder.arcface.face.model.FacePreviewInfo
import com.shencoder.arcface.constant.RecognizeStatus
import com.shencoder.arcface.face.FaceActive
import com.shencoder.arcface.util.LogUtil
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.Resolution
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.preview.Frame
import io.fotoapparat.preview.FrameProcessor
import io.fotoapparat.selector.*
import io.fotoapparat.view.CameraView
import com.shencoder.arcface.constant.FaceConstant
import com.shencoder.arcface.face.FaceDetect

/**
 * 人脸识别CameraView，支持预览RGB、IR摄像头预览画面
 * RGB摄像头铺满父布局
 * IR摄像头等比缩放[FaceConstant.DEFAULT_ZOOM_RATIO]排放在父布局左下角
 *
 * @author  ShenBen
 * @date    2021/02/05 15:16
 * @email   714081644@qq.com
 */
class FaceCameraView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), LifecycleObserver, OnPreviewCallback {

    companion object {
        private const val TAG = "FaceCameraView->"
    }

    private val rgbCameraView: CameraView
    private lateinit var rgbCameraConfiguration: CameraConfiguration
    private lateinit var rgbFotoapparat: Fotoapparat

    private lateinit var irCameraConfiguration: CameraConfiguration
    private val irCameraView: CameraView
    private lateinit var irFotoapparat: Fotoapparat
    private var isIrCameraStarted: Boolean = false

    private val rgbFaceRectView: FaceRectView
    private val irFaceRectView: FaceRectView

    private val viewfinderView: ViewfinderView

    private val flIrCamera: FrameLayout

    private lateinit var mFaceConfiguration: FaceConfiguration
    private lateinit var faceHelper: FaceHelper

    private var mLifecycle: Lifecycle? = null

    /**
     * 是否启用人脸
     */
    @Volatile
    private var enableFace: Boolean = false

    /**
     * 启用黑白摄像头检测人脸，用于夜间模式
     */
    @Volatile
    private var enableIrDetectFaces: Boolean = false

    private var cameraListener: OnCameraListener? = null

    /**
     * 人脸检测
     */
    private val faceDetect = FaceDetect()

    init {
        LayoutInflater.from(context).inflate(R.layout.camera_preview, this)
        rgbFaceRectView = findViewById(R.id.rgbFaceRectView)
        irFaceRectView = findViewById(R.id.irFaceRectView)
        viewfinderView = findViewById(R.id.viewfinderView)
        rgbCameraView = findViewById(R.id.rgbCameraView)
        irCameraView = findViewById(R.id.irCameraView)
        flIrCamera = findViewById(R.id.flIrCamera)
        rgbCameraView.setScaleType(ScaleType.CenterCrop)
        irCameraView.setScaleType(ScaleType.CenterCrop)
    }

    fun setOnCameraListener(listener: OnCameraListener) {
        cameraListener = listener
    }

    /**
     * 必须要设置配置信息参数
     *
     * @param configuration 参数配置
     * @param autoInitFace 是否自动初始化[FaceHelper]，如果sdk未激活则不初始化[FaceHelper]
     * @param initIrDetectFace 初始化人脸检测，用于红外摄像头
     */
    fun setConfiguration(
        configuration: FaceConfiguration,
        autoInitFace: Boolean = false,
        initIrDetectFace: Boolean = false
    ) {
        rgbCameraView.isMirror(configuration.isRgbMirror)
        rgbCameraConfiguration = rgbCameraConfiguration(configuration.previewSize)
        rgbFotoapparat = Fotoapparat(
            context,
            rgbCameraView,
            lensPosition = getCameraFacing(configuration.rgbCameraFcing),
            cameraConfiguration = rgbCameraConfiguration,
            cameraErrorCallback = {
                LogUtil.e("RGB摄像头开启出错：${it.message}")
                cameraListener?.onRgbCameraError(it)
            }
        )
        irCameraConfiguration = irCameraConfiguration(configuration.previewSize)
        initIrCamera(configuration)

        rgbFaceRectView.visibility = if (configuration.drawFaceRect.isDraw) VISIBLE else INVISIBLE

        viewfinderView.visibility =
            if (configuration.enableRecognizeAreaLimited) VISIBLE else INVISIBLE
        viewfinderView.setFrameRatio(configuration.recognizeAreaLimitedRatio)
        viewfinderView.setLabelText(configuration.viewfinderText)
        viewfinderView.setLabelTextLocation(configuration.viewfinderTextGravity)
        mFaceConfiguration = configuration

        if (initIrDetectFace) {
            faceDetect.init(
                context,
                configuration.enableImageQuality,
                1,
                configuration.detectFaceOrient
            )
            faceDetect.setFaceDetectCallback(
                someone = {
                    mFaceConfiguration.recognizeCallback?.someone()
                },
                nobody = {
                    mFaceConfiguration.recognizeCallback?.nobody()
                },
                detectFaceNum = { num, faceIds ->
                    mFaceConfiguration.recognizeCallback?.detectFaceNum(num, faceIds)
                }
            )
        }
        if (autoInitFace) {
            enableFace = initFaceHelper()
        }
    }

    /**
     * 启用红外摄像头进行人脸检测
     * 调用在[setConfiguration]之后
     */
    fun enableIrDetectFaces(enable: Boolean) {
        if (enableIrDetectFaces == enable) {
            return
        }
        enableIrDetectFaces = enable
        initIrCamera(mFaceConfiguration)
        if (enable) {
            post { startIrCamera() }
        } else {
            if (mFaceConfiguration.livenessType != LivenessType.IR) {
                post { stopIrCamera() }
            }
        }
    }

    /**
     * 是否启用人脸，传入摄像头预览数据
     * 调用在[setConfiguration]之后
     */
    fun enableFace(enableFace: Boolean) {
        if (this.enableFace == enableFace) {
            return
        }
        if (enableFace) {
            if (this::faceHelper.isInitialized.not()) {
                val result = initFaceHelper()
                if (result.not()) {
                    return
                }
            }
        }
        this.enableFace = enableFace
    }

    fun setLifecycleOwner(owner: LifecycleOwner?) {
        clearLifecycleObserver()
        owner?.let {
            mLifecycle = owner.lifecycle.apply { addObserver(this@FaceCameraView) }
        }
    }

    /**
     * 手动重新识别
     * @param faceId 人脸id
     */
    fun retryRecognizeDelayed(faceId: Int) {
        if (this::faceHelper.isInitialized) {
            faceHelper.retryRecognizeDelayed(faceId)
        }
    }

    /**
     * 手动重新活体检测
     * @param faceId 人脸id
     */
    fun retryLivenessDetectDelayed(faceId: Int) {
        if (this::faceHelper.isInitialized) {
            faceHelper.retryLivenessDetectDelayed(faceId)
        }
    }

    private fun initFaceHelper(): Boolean {
        return if (FaceActive.isActivated(context)) {
            destroyFace()
            faceHelper = FaceHelper(mFaceConfiguration, this)
            true
        } else {
            LogUtil.e("${TAG}initFaceHelper-人脸识别未激活")
            false
        }
    }

    private fun initIrCamera(configuration: FaceConfiguration) {
        if (enableIrDetectFaces || configuration.livenessType == LivenessType.IR) {
            //启用红外检测人脸或者活体检测为IR活体检测
            irCameraView.isMirror(configuration.isIrMirror)
            flIrCamera.visibility = VISIBLE
            irFaceRectView.visibility =
                if (configuration.drawFaceRect.isDraw) VISIBLE else INVISIBLE

            if (this::irFotoapparat.isInitialized.not()) {
                //启用活体检测
                irFotoapparat = Fotoapparat(
                    context,
                    irCameraView,
                    lensPosition = getCameraFacing(configuration.irCameraFcing),
                    cameraConfiguration = irCameraConfiguration,
                    cameraErrorCallback = {
                        LogUtil.e("IR摄像头开启出错：${it.message}")
                        isIrCameraStarted = false
                        cameraListener?.onIrCameraError(it)
                    }
                )
            }
        } else {
            irFaceRectView.clearFaceInfo()
            flIrCamera.visibility = INVISIBLE
        }
    }


    private fun clearLifecycleObserver() {
        mLifecycle?.removeObserver(this)
        mLifecycle = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start() {
        if (this::rgbFotoapparat.isInitialized) {
            rgbFotoapparat.start()
        }
        startIrCamera()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stop() {
        if (this::rgbFotoapparat.isInitialized) {
            rgbFotoapparat.stop()
        }
        stopIrCamera()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        cameraListener = null
        faceDetect.destroy()
        destroyFace()
        rgbFaceRectView.clearFaceInfo()
        irFaceRectView.clearFaceInfo()
        clearLifecycleObserver()
    }

    @Synchronized
    private fun startIrCamera() {
        if (this::irFotoapparat.isInitialized) {
            if (isIrCameraStarted.not()) {
                irFotoapparat.start()
                isIrCameraStarted = true
            }
        }
    }

    @Synchronized
    private fun stopIrCamera() {
        if (this::irFotoapparat.isInitialized) {
            if (isIrCameraStarted) {
                irFotoapparat.stop()
                isIrCameraStarted = false
            }
        }
    }

    private fun destroyFace() {
        if (this::faceHelper.isInitialized) {
            faceHelper.destroy()
        }
    }

    private fun getCameraFacing(facing: CameraFacing): LensPositionSelector {
        return when (facing) {
            CameraFacing.BACK -> {
                back()
            }
            CameraFacing.FRONT -> {
                front()
            }
        }
    }

    /**
     * 获取识别限制区域
     */
    override fun getRecognizeAreaRect(): Rect {
        return if (mFaceConfiguration.enableRecognizeAreaLimited) {
            viewfinderView.getFrameRect()
        } else {
            Rect(
                0,
                0,
                if (width == 0) Int.MAX_VALUE else width,
                if (height == 0) Int.MAX_VALUE else height
            )
        }
    }

    override fun onPreviewFaceInfo(previewInfoList: List<FacePreviewInfo>) {
        if (mFaceConfiguration.drawFaceRect.isDraw) {
            val rgbList = mutableListOf<FaceRectView.DrawInfo>()
            val irList = mutableListOf<FaceRectView.DrawInfo>()
            for (previewInfo in previewInfoList) {
                val recognizeInfo = faceHelper.getRecognizeInfo(previewInfo.faceId)
                var color: Int = mFaceConfiguration.drawFaceRect.unknownColor
                when {
                    recognizeInfo.recognizeStatus == RecognizeStatus.SUCCEED -> {
                        color = mFaceConfiguration.drawFaceRect.successColor
                    }
                    recognizeInfo.recognizeStatus == RecognizeStatus.FAILED
                            || recognizeInfo.liveness == LivenessInfo.NOT_ALIVE -> {
                        color = mFaceConfiguration.drawFaceRect.failedColor
                    }

                }
                val msg = recognizeInfo.msg ?: previewInfo.faceId.toString()
                rgbList.add(
                    FaceRectView.DrawInfo(
                        previewInfo.rgbTransformedRect,
                        recognizeInfo.gender,
                        recognizeInfo.age,
                        recognizeInfo.liveness,
                        msg,
                        color
                    )
                )
                if (mFaceConfiguration.livenessType == LivenessType.IR) {
                    irList.add(
                        FaceRectView.DrawInfo(
                            previewInfo.irTransformedRect,
                            recognizeInfo.gender,
                            recognizeInfo.age,
                            recognizeInfo.liveness,
                            null,
                            color
                        )
                    )
                }
            }
            rgbFaceRectView.drawRealtimeFaceInfo(rgbList)
            if (mFaceConfiguration.livenessType == LivenessType.IR) {
                irFaceRectView.drawRealtimeFaceInfo(irList)
            }
        }
    }

    override fun someone() {
        if (enableIrDetectFaces.not()) {
            mFaceConfiguration.recognizeCallback?.someone()
        }
    }

    override fun nobody() {
        if (enableIrDetectFaces.not()) {
            mFaceConfiguration.recognizeCallback?.nobody()
        }
    }

    override fun detectFaceNum(num: Int, faceIds: List<Int>) {
        if (enableIrDetectFaces.not()) {
            mFaceConfiguration.recognizeCallback?.detectFaceNum(num, faceIds)
        }
    }

    private fun rgbCameraConfiguration(previewSize: PreviewSize?): CameraConfiguration {
        val previewResolution: ResolutionSelector = if (previewSize != null) {
            firstAvailable(
                { Resolution(previewSize.width, previewSize.height) },
                highestResolution()
            )
        } else {
            highestResolution()
        }
        return CameraConfiguration.builder()
            .previewResolution(previewResolution)
            .frameProcessor(object : FrameProcessor {
                override fun process(frame: Frame) {
                    if (enableFace.not()) {
                        return
                    }
                    faceHelper.onPreviewFrame(
                        frame.image,
                        frame.size.width,
                        frame.size.height,
                        rgbCameraView.width,
                        rgbCameraView.height
                    )
                }
            })
            .build()
    }

    private fun irCameraConfiguration(previewSize: PreviewSize?): CameraConfiguration {
        val previewResolution: ResolutionSelector = if (previewSize != null) {
            firstAvailable(
                { Resolution(previewSize.width, previewSize.height) },
                highestResolution()
            )
        } else {
            highestResolution()
        }
        return CameraConfiguration.builder()
            .previewResolution(previewResolution)
            .frameProcessor(object : FrameProcessor {
                override fun process(frame: Frame) {
                    if (enableFace.not()) {
                        return
                    }
                    if (enableIrDetectFaces) {
                        faceDetect.onPreviewFrame(frame.image, frame.size.width, frame.size.height)
                    }
                    if (mFaceConfiguration.livenessType == LivenessType.IR) {
                        faceHelper.refreshIrPreviewData(frame.image)
                    }
                }
            })
            .build()
    }
}

