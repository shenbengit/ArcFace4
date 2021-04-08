package com.shencoder.arcface.face

import android.os.Handler
import android.os.Looper
import com.arcsoft.face.*
import com.arcsoft.face.enums.DetectFaceOrientPriority
import com.arcsoft.face.enums.DetectMode
import com.arcsoft.face.enums.ExtractType
import com.shencoder.arcface.callback.OnPreviewCallback
import com.shencoder.arcface.configuration.FaceConfiguration
import com.shencoder.arcface.configuration.LivenessType
import com.shencoder.arcface.constant.FaceConstant
import com.shencoder.arcface.constant.FaceErrorType
import com.shencoder.arcface.constant.RecognizeStatus
import com.shencoder.arcface.constant.RequestLivenessStatus
import com.shencoder.arcface.face.model.FacePreviewInfo
import com.shencoder.arcface.face.model.RecognizeInfo
import com.shencoder.arcface.util.FaceRectTransformerUtil
import com.shencoder.arcface.util.LogUtil
import java.util.concurrent.*

/**
 * 人脸操作辅助类
 *
 * @author  ShenBen
 * @date    2021/02/24 14:08
 * @email   714081644@qq.com
 */
internal class FaceHelper(
    private val configuration: FaceConfiguration,
    private val onPreviewCallback: OnPreviewCallback
) {

    companion object {
        private const val TAG = "FaceHelper->"
    }

    private val mHandler = Handler(Looper.getMainLooper())

    /**
     * VIDEO模式人脸检测引擎，用于预览帧人脸追踪及图像质量检测
     */
    private val detectFaceEngine: FaceEngine = FaceEngine()

    /**
     * 人脸检测引擎-线程池
     */
    private val detectFaceExecutor: ExecutorService by lazy {
        ThreadPoolExecutor(
            1, 1, 0, TimeUnit.MILLISECONDS, LinkedBlockingQueue()
        ) { r ->
            val t = Thread(r)
            t.name = "detect-thread-" + t.id
            t
        }
    }

    /**
     * 用于特征提取的引擎
     */
    private val extractFeatureEngine: FaceEngine = FaceEngine()

    /**
     * 特征提取-线程队列
     */
    private val extractFeatureThreadQueue =
        LinkedBlockingQueue<Runnable>(configuration.detectFaceMaxNum)

    /**
     * 特征提取-线程池
     */
    private val extractFeatureExecutor: ExecutorService by lazy {
        ThreadPoolExecutor(
            configuration.detectFaceMaxNum,
            configuration.detectFaceMaxNum,
            0,
            TimeUnit.MILLISECONDS,
            extractFeatureThreadQueue
        ) { r ->
            val t = Thread(r)
            t.name = "extract-feature-thread-" + t.id
            t
        }
    }

    /**
     * IMAGE模式活体检测引擎，用于预览帧人脸信息
     * 包括年龄、性别、3d角度、活体
     */
    private val detectInfoEngine: FaceEngine = FaceEngine()

    /**
     * 人脸信息检测-线程队列
     */
    private val detectInfoThreadQueue =
        LinkedBlockingQueue<Runnable>(configuration.detectFaceMaxNum)

    /**
     * 人脸信息检测-线程池
     */
    private val detectInfoExecutor: ExecutorService by lazy {
        ThreadPoolExecutor(
            configuration.detectFaceMaxNum,
            configuration.detectFaceMaxNum,
            0,
            TimeUnit.MILLISECONDS,
            detectInfoThreadQueue
        ) { r ->
            val t = Thread(r)
            t.name = "liveness-thread-" + t.id
            t
        }
    }
    private var detectInfoMask = 0

    /**
     * 人脸比对
     */
    private val faceServer = FaceServer()

    /**
     * 用于记录人脸识别过程信息
     * <faceId,RecognizeInfo>
     */
    private val recognizeInfoMap: MutableMap<Int, RecognizeInfo> =
        ConcurrentHashMap(configuration.detectFaceMaxNum)

    /**
     * 虹软人脸检测信息
     */
    private val faceInfoList: MutableList<FaceInfo> = mutableListOf()

    /**
     * 口罩信息
     */
    private val maskInfoList: MutableList<MaskInfo> = mutableListOf()

    /**
     * 人脸预览相关信息
     */
    private val facePreviewInfo: MutableList<FacePreviewInfo> = mutableListOf()

    /**
     * 当前是否有人
     *
     * true:有人
     * false:无人
     */
    private var isAnybody = false

    /**
     * 检测到的人脸数量
     */
    private var detectFaceNum = 0

    /**
     * 红外摄像头预览数据
     */
    @Volatile
    private var irNV21: ByteArray? = null

    init {
        //初始化人脸检测引擎
        var mask = FaceEngine.ASF_FACE_DETECT
        if (configuration.enableMask) {
            mask = mask or FaceEngine.ASF_MASK_DETECT
        }
        val result = detectFaceEngine.init(
            configuration.context,
            DetectMode.ASF_DETECT_MODE_VIDEO,
            configuration.detectFaceOrient,
            configuration.detectFaceMaxNum,
            mask
        )
        LogUtil.i("${TAG}人脸检测引擎初始化:$result")
    }

    init {
        if (configuration.enableRecognize) {
            //初始化特征提取引擎
            val orientPriority =
                if (configuration.detectFaceOrient == DetectFaceOrientPriority.ASF_OP_ALL_OUT) {
                    DetectFaceOrientPriority.ASF_OP_0_ONLY
                } else {
                    configuration.detectFaceOrient
                }
            var mask = FaceEngine.ASF_FACE_RECOGNITION
            if (configuration.enableImageQuality) {
                mask = mask or FaceEngine.ASF_IMAGEQUALITY
            }
            val result = extractFeatureEngine.init(
                configuration.context,
                DetectMode.ASF_DETECT_MODE_IMAGE,
                orientPriority,
                configuration.detectFaceMaxNum,
                mask
            )
            LogUtil.i("${TAG}特征提取引擎初始化:$result")
        }
    }

    init {
        val detectInfo = configuration.detectInfo
        if (configuration.livenessType != LivenessType.NONE) {
            val orientPriority =
                if (configuration.detectFaceOrient == DetectFaceOrientPriority.ASF_OP_ALL_OUT) {
                    DetectFaceOrientPriority.ASF_OP_0_ONLY
                } else {
                    configuration.detectFaceOrient
                }
            if (configuration.livenessType == LivenessType.RGB) {
                detectInfoMask = detectInfoMask or FaceEngine.ASF_LIVENESS
                if (detectInfo.age) {
                    detectInfoMask = detectInfoMask or FaceEngine.ASF_AGE
                }
                if (detectInfo.gender) {
                    detectInfoMask = detectInfoMask or FaceEngine.ASF_GENDER
                }
                if (detectInfo.angle) {
                    detectInfoMask = detectInfoMask or FaceEngine.ASF_FACE3DANGLE
                }
            } else {
                detectInfoMask = detectInfoMask or FaceEngine.ASF_IR_LIVENESS
            }
            val result = detectInfoEngine.init(
                configuration.context,
                DetectMode.ASF_DETECT_MODE_IMAGE,
                orientPriority,
                configuration.detectFaceMaxNum,
                detectInfoMask
            )
            detectInfoEngine.setLivenessParam(
                LivenessParam(
                    configuration.rgbLivenessThreshold,
                    configuration.irLivenessThreshold
                )
            )
            LogUtil.i("${TAG}活体检测初始化:$result")
        }
    }

    init {
        faceServer.init(configuration.context, configuration.detectFaceOrient)
    }

    fun refreshIrPreviewData(irNV21: ByteArray?) {
        this.irNV21 = irNV21
    }

    /**
     * 预览数据
     */
    fun onPreviewFrame(
        rgbNV21: ByteArray,
        previewWidth: Int,
        previewHeight: Int,
        canvasWidth: Int,
        canvasHeight: Int,
    ) {
        detectFaceExecutor.execute {
            faceInfoList.clear()
            maskInfoList.clear()
            facePreviewInfo.clear()
            //人脸检测
            val result = detectFaceEngine.detectFaces(
                rgbNV21,
                previewWidth,
                previewHeight,
                FaceEngine.CP_PAF_NV21,
                faceInfoList
            )
            if (result != ErrorInfo.MOK) {
                onError(
                    FaceErrorType.DETECT_FACES,
                    result,
                    FaceConstant.getFaceErrorMsg(result)
                )
                return@execute
            }
            //是否有人
            anybody(faceInfoList)

            if (configuration.recognizeKeepMaxFace) {
                //保留最大人脸
                keepMaxFace(faceInfoList)
            }
            //是否启用口罩识别
            if (configuration.enableMask) {
                val detectMaskResult = detectFaceEngine.process(
                    rgbNV21,
                    previewWidth,
                    previewHeight,
                    FaceEngine.CP_PAF_NV21,
                    faceInfoList,
                    FaceEngine.ASF_MASK_DETECT
                )
                if (detectMaskResult == ErrorInfo.MOK) {
                    val maskResult = detectFaceEngine.getMask(maskInfoList)
                    if (maskResult != ErrorInfo.MOK) {
                        onError(
                            FaceErrorType.DETECT_MASK,
                            detectMaskResult,
                            FaceConstant.getFaceErrorMsg(maskResult)
                        )
                    }
                } else {
                    onError(
                        FaceErrorType.DETECT_MASK,
                        detectMaskResult,
                        FaceConstant.getFaceErrorMsg(detectMaskResult)
                    )
                }
            }
            //转换人脸信息
            faceInfoList.forEachIndexed { index, faceInfo ->
                val rgbAdjustRect = FaceRectTransformerUtil.adjustRect(
                    previewWidth,
                    previewHeight,
                    canvasWidth,
                    canvasHeight,
                    configuration.isRgbMirror,
                    faceInfo.rect,
                    configuration.drawFaceRect.rgbOffsetX,
                    configuration.drawFaceRect.rgbOffsetY
                )

                val previewInfo =
                    FacePreviewInfo(faceInfo.faceId, faceInfo, FaceInfo(faceInfo))
                if (configuration.enableMask && maskInfoList.isNotEmpty() && maskInfoList.size == faceInfoList.size) {
                    previewInfo.mask = maskInfoList[index].mask
                }
                previewInfo.rgbTransformedRect = rgbAdjustRect
                previewInfo.irTransformedRect = FaceRectTransformerUtil.rgbRectToIrRect(
                    rgbAdjustRect,
                    FaceConstant.DEFAULT_ZOOM_RATIO,
                    configuration.drawFaceRect.irOffsetX,
                    configuration.drawFaceRect.irOffsetY
                )
                previewInfo.recognizeAreaValid =
                    onPreviewCallback.getRecognizeAreaRect().contains(rgbAdjustRect)
                facePreviewInfo.add(previewInfo)
            }

            if (configuration.enableRecognize && faceInfoList.isNotEmpty()) {
                //开始识别
                doRecognize(rgbNV21, previewWidth, previewHeight, facePreviewInfo)
            }
            //清除离开人脸
            clearLeftFace(faceInfoList)
            //回调预览人脸数据
            onPreviewCallback.onPreviewFaceInfo(facePreviewInfo)
        }
    }

    fun getRecognizeInfo(faceId: Int): RecognizeInfo {
        var recognizeInfo = recognizeInfoMap[faceId]
        if (recognizeInfo == null) {
            recognizeInfo = RecognizeInfo(faceId)
            recognizeInfoMap[faceId] = recognizeInfo
        }
        return recognizeInfo
    }

    fun destroy() {
        mHandler.removeCallbacksAndMessages(null)
        if (detectFaceExecutor.isShutdown.not()) {
            detectFaceExecutor.shutdownNow()
        }
        synchronized(detectFaceEngine) {
            val result = detectFaceEngine.unInit()
            LogUtil.w("${TAG}destroy-detectFaceEngine.unInit:$result")
        }

        if (extractFeatureExecutor.isShutdown.not()) {
            extractFeatureExecutor.shutdownNow()
        }
        extractFeatureThreadQueue.clear()
        synchronized(extractFeatureEngine) {
            val result = extractFeatureEngine.unInit()
            LogUtil.w("${TAG}destroy-extractFeatureEngine.unInit:$result")
        }

        if (detectInfoExecutor.isShutdown.not()) {
            detectInfoExecutor.shutdownNow()
        }
        detectInfoThreadQueue.clear()
        synchronized(detectInfoEngine) {
            val result = detectInfoEngine.unInit()
            LogUtil.w("${TAG}destroy-detectInfoEngine.unInit:$result")
        }

        faceServer.destroy()

        recognizeInfoMap.clear()
        faceInfoList.clear()
        maskInfoList.clear()
        facePreviewInfo.clear()
        irNV21 = null
    }


    /**
     * 判断是否有人
     */
    private fun anybody(faceInfoList: List<FaceInfo>) {
        //人脸信息列表不为空则是有人,为空则是无人
        val anybody = faceInfoList.isNotEmpty()
        if (isAnybody != anybody) {
            if (anybody) {
                onPreviewCallback.someone()
            } else {
                onPreviewCallback.nobody()
            }
            isAnybody = anybody
        }
        val num = faceInfoList.size
        if (detectFaceNum != num) {
            val faceIds: ArrayList<Int> = ArrayList(faceInfoList.size)
            faceInfoList.forEach { faceIds.add(it.faceId) }
            onPreviewCallback.detectFaceNum(num, faceIds)
            detectFaceNum = num
        }
    }

    /**
     * 保留最大的人脸
     * @param list 人脸追踪时，一帧数据的人脸信息
     */
    private fun keepMaxFace(list: MutableList<FaceInfo>) {
        if (list.size <= 1) {
            return
        }
        var maxFaceInfo = list[0]
        for (info in list) {
            if (info.rect.width() * info.rect.height() > maxFaceInfo.rect.width() * maxFaceInfo.rect.height()) {
                maxFaceInfo = info
            }
        }
        list.clear()
        list.add(maxFaceInfo)
    }

    /**
     * 删除已经离开的人脸
     */
    private fun clearLeftFace(faceInfoList: List<FaceInfo>) {
        val iterator = recognizeInfoMap.entries.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            var contained = false
            for (faceInfo in faceInfoList) {
                if (next.key == faceInfo.faceId) {
                    contained = true
                    break
                }
            }
            if (contained.not()) {
                val value = next.value
                //先删除
                iterator.remove()
                //再通知
                synchronized(value.lock) {
                    value.lock.notifyAll()
                }
            }
        }
    }

    /**
     * 开始识别
     * @param rgbNV21 相机预览数据
     * @param width 相机预览大小-宽
     * @param height 相机预览大小-高
     */
    private fun doRecognize(
        rgbNV21: ByteArray,
        width: Int,
        height: Int,
        faceInfoList: List<FacePreviewInfo>
    ) {
        for (previewInfo in faceInfoList) {
            if (judgeFaceSize(previewInfo).not()) {
                continue
            }
            if (previewInfo.recognizeAreaValid.not()) {
                //不在识别区域，跳过
                continue
            }
            if (configuration.enableMask && previewInfo.mask == MaskInfo.UNKNOWN) {
                //跳过mask值为MaskInfo.UNKNOWN的人脸
                continue
            }
            val recognizeInfo = getRecognizeInfo(previewInfo.faceId)

            if (configuration.livenessType != LivenessType.NONE
                && recognizeInfo.recognizeStatus != RecognizeStatus.SUCCEED
            ) {
                val liveness = recognizeInfo.liveness
                if (liveness != LivenessInfo.ALIVE
                    && liveness != LivenessInfo.NOT_ALIVE
                    && liveness != RequestLivenessStatus.ANALYZING
                    && liveness != RequestLivenessStatus.FAILED
                ) {
                    //开启活体检测线程
                    changeLiveness(previewInfo.faceId, RequestLivenessStatus.ANALYZING)
                    requestFaceDetectInfo(rgbNV21, irNV21, width, height, previewInfo)
                }
            }
            if (recognizeInfo.recognizeStatus == RecognizeStatus.TO_RETRY) {
                //开启人脸特征码提取线程
                changeRecognizeStatus(previewInfo.faceId, RecognizeStatus.SEARCHING)
                requestFaceFeature(
                    rgbNV21,
                    width,
                    height,
                    previewInfo
                )
            }
        }
    }

    /**
     * 判断人脸大小是否超过可识别限制
     * @return true:超过 继续识别；false:小于阈值 忽略
     */
    private fun judgeFaceSize(previewInfo: FacePreviewInfo): Boolean {
        val rect = previewInfo.faceInfoRgb.rect
        // 由于目前人脸框的宽高接近一致
        return rect.width() >= configuration.faceSizeLimit && rect.height() >= configuration.faceSizeLimit
    }

    /**
     * 异常情况
     */
    private fun onError(type: FaceErrorType, errorCode: Int, errorMessage: String) {
        LogUtil.e("${TAG}onError: FaceErrorType:${type},errorCode:${errorCode},errorMessage:${errorMessage}")
        configuration.onErrorCallback?.onError(type, errorCode, errorMessage)
    }

    private fun requestFaceDetectInfo(
        rgbNV21: ByteArray,
        irNV21: ByteArray?,
        width: Int,
        height: Int,
        info: FacePreviewInfo
    ) {
        if (detectInfoThreadQueue.remainingCapacity() <= 0) {
            LogUtil.w("requestFaceDetectInfo 线程池满了")
            changeMsg(info.faceId, "DetectInfoThread is full.")
            return
        }
        detectInfoExecutor.execute(
            FaceDetectInfoRunnable(
                rgbNV21,
                irNV21,
                width,
                height,
                info.faceId,
                info.faceInfoRgb,
                info.faceInfoIr
            )
        )
    }

    private fun requestFaceFeature(
        rgbNV21: ByteArray,
        width: Int,
        height: Int,
        info: FacePreviewInfo
    ) {
        if (extractFeatureThreadQueue.remainingCapacity() <= 0) {
            LogUtil.w("requestFaceFeature 线程池满了")
            changeMsg(info.faceId, "FeatureThread is full.")
            return
        }
        extractFeatureExecutor.execute(
            ExtractFeatureRunnable(
                rgbNV21,
                width,
                height,
                info.faceId,
                info.faceInfoRgb,
                info.mask
            )
        )
    }

    /**
     * 请求活体检测后的操作
     */
    private fun onFaceLivenessInfoGet(livenessInfo: LivenessInfo?, faceId: Int, errorCode: Int) {
        if (recognizeInfoMap.containsKey(faceId).not()) {
            //人脸已离开，不用处理
            LogUtil.w("${TAG}onFaceLivenessInfoGet-faceId:${faceId}已离开")
            return
        }
        val recognizeInfo = getRecognizeInfo(faceId)

        if (livenessInfo != null) {
            changeLiveness(faceId, livenessInfo.liveness)
            when (livenessInfo.liveness) {
                LivenessInfo.ALIVE -> {
                    synchronized(recognizeInfo.lock) {
                        recognizeInfo.lock.notifyAll()
                    }
                }
                LivenessInfo.NOT_ALIVE -> {
                    changeMsg(faceId, "NOT_ALIVE")
                    retryLivenessDetectDelayed(faceId)
                }
                else -> {
                    changeMsg(faceId, "LIVENESS FAILED:${livenessInfo.liveness}")
                    //继续活体检测
                    LogUtil.w(
                        "${TAG}onFaceLivenessInfoGet-liveness:${livenessInfo.liveness},faceId:$faceId,${
                            FaceConstant.getLivenessErrorMsg(
                                livenessInfo.liveness
                            )
                        }"
                    )
                }
            }
        } else {
            changeMsg(faceId, "ProcessFailed:${errorCode},faceId:${faceId}")
            if (recognizeInfo.increaseAndGetLivenessErrorRetryCount() > configuration.livenessErrorRetryCount) {
                //错误码不为检测置信度低或者在尝试最大次数后，活体检测仍然失败，则认为失败
                recognizeInfo.resetLivenessErrorRetryCount()
                changeLiveness(faceId, RequestLivenessStatus.FAILED)
                synchronized(recognizeInfo.lock) {
                    recognizeInfo.lock.notifyAll()
                }
                retryLivenessDetectDelayed(faceId)
            } else {
                changeLiveness(faceId, LivenessInfo.UNKNOWN)
            }
        }
    }

    /**
     * 请求人脸特征后的操作
     * @param faceFeature 人脸特征码
     * @param faceId faceId
     */
    private fun onFaceFeatureInfoGet(
        faceFeature: FaceFeature?,
        faceId: Int,
        errorCode: Int,
        nv21: ByteArray,
        mask: Int,
        isImageQualityDetect: Boolean
    ) {
        if (recognizeInfoMap.containsKey(faceId).not()) {
            //人脸已离开，不用处理
            LogUtil.w("${TAG}onFaceFeatureInfoGet-faceId:${faceId}已离开")
            return
        }
        val recognizeInfo = getRecognizeInfo(faceId)
        when {
            faceFeature == null -> {
                if (isImageQualityDetect) {
                    if (errorCode == ErrorInfo.MOK) {
                        changeMsg(faceId, "ImageQuality too low,faceId:${faceId}")
                    } else {
                        changeMsg(faceId, "ImageDetectFailed:${errorCode},faceId:${faceId}")
                    }
                } else {
                    changeMsg(faceId, "ExtractFailed:${errorCode},faceId:${faceId}")
                }
                if (recognizeInfo.increaseAndGetExtractFeatureErrorRetryCount() > configuration.extractFeatureErrorRetryCount) {
                    //错误码不为检测置信度低或者在尝试最大次数后，特征提取仍然失败，则认为识别未通过
                    recognizeInfo.resetExtractFeatureErrorRetryCount()
                    changeRecognizeStatus(faceId, RecognizeStatus.FAILED)
                    retryRecognizeDelayed(faceId)
                } else {
                    changeRecognizeStatus(faceId, RecognizeStatus.TO_RETRY)
                }
            }
            configuration.livenessType == LivenessType.NONE || recognizeInfo.liveness == LivenessInfo.ALIVE -> {
                recognizeInfo.mask = mask
                searchFace(faceFeature, faceId, nv21)
            }
            recognizeInfo.liveness == RequestLivenessStatus.FAILED -> {
                //活体检测失败
                changeRecognizeStatus(faceId, RecognizeStatus.FAILED)
            }
            else -> {
                synchronized(recognizeInfo.lock) {
                    try {
                        recognizeInfo.lock.wait()
                        //避免比对中途，人脸离开了
                        onFaceFeatureInfoGet(
                            faceFeature,
                            faceId,
                            errorCode,
                            nv21,
                            mask,
                            isImageQualityDetect
                        )
                    } catch (e: InterruptedException) {
                        LogUtil.w("${TAG}onFaceFeatureInfoGet: 等待活体结果时退出界面会执行，正常现象，可注释异常代码块")
                    }
                }
            }
        }
    }

    /**
     * 搜索人脸
     */
    private fun searchFace(faceFeature: FaceFeature, faceId: Int, nv21: ByteArray) {
        val callback = configuration.recognizeCallback ?: let {
            changeRecognizeStatus(faceId, RecognizeStatus.FAILED)
            changeMsg(faceId, "Visitor,$faceId")
            return
        }
        if (configuration.enableCompareFace.not()) {
            callback.onGetFaceFeature(
                faceId,
                faceFeature.featureData,
                getRecognizeInfo(faceId),
                nv21
            )
            changeRecognizeStatus(faceId, RecognizeStatus.SUCCEED)
            changeMsg(faceId, "Visitor,$faceId")
            return
        }
        val compareResult =
            faceServer.compareFaceFeature(faceFeature, callback.faceFeatureList()) ?: let {
                changeRecognizeStatus(faceId, RecognizeStatus.FAILED)
                changeMsg(faceId, "Visitor,$faceId")
                return
            }
        if (compareResult.similar >= callback.similarThreshold()) {
            changeRecognizeStatus(faceId, RecognizeStatus.SUCCEED)
            val msg =
                callback.onRecognized(
                    compareResult.bean,
                    compareResult.similar,
                    getRecognizeInfo(faceId)
                )
            changeMsg(faceId, msg)
        } else {
            retryRecognizeDelayed(faceId)
            changeMsg(faceId, "未通过：NOT_REGISTERED")
        }
    }

    private fun changeRecognizeStatus(faceId: Int, recognizeStatus: RecognizeStatus) {
        if (recognizeInfoMap.containsKey(faceId)) {
            getRecognizeInfo(faceId).recognizeStatus = recognizeStatus
        }
    }

    private fun changeLiveness(faceId: Int, liveness: Int) {
        if (recognizeInfoMap.containsKey(faceId)) {
            getRecognizeInfo(faceId).liveness = liveness
        }
    }

    private fun changeMsg(faceId: Int, msg: String?) {
        if (recognizeInfoMap.containsKey(faceId)) {
            getRecognizeInfo(faceId).msg = msg
        }
    }

    private fun retryLivenessDetectDelayed(faceId: Int) {
        mHandler.postDelayed({
            changeMsg(faceId, "$faceId")
            changeLiveness(faceId, LivenessInfo.UNKNOWN)
        }, configuration.livenessFailedRetryInterval)
    }

    private fun retryRecognizeDelayed(faceId: Int) {
        changeRecognizeStatus(faceId, RecognizeStatus.FAILED)
        mHandler.postDelayed({
            changeMsg(faceId, "$faceId")
            changeRecognizeStatus(faceId, RecognizeStatus.TO_RETRY)
        }, configuration.recognizeFailedRetryInterval)
    }


    /**
     * 人脸信息检测线程
     * 包括年龄、性别、3d角度、活体
     */
    inner class FaceDetectInfoRunnable(
        private val rgbNV21: ByteArray,
        private val irNV21: ByteArray?,
        private val width: Int,
        private val height: Int,
        private val faceId: Int,
        private val rgbFaceInfo: FaceInfo,
        private val irFaceInfo: FaceInfo
    ) : Runnable {

        override fun run() {
            //活体检测结果
            var livenessResult: Int
            //活体检测
            val livenessList: MutableList<LivenessInfo> = mutableListOf()
            synchronized(detectInfoEngine) {
                if (configuration.livenessType == LivenessType.RGB) {
                    //仅在RGB活体检测时生效
                    //人脸属性检测（年龄/性别/人脸3D角度），最多支持4张人脸信息检测，超过部分返回未知
                    val processResult = detectInfoEngine.process(
                        rgbNV21,
                        width,
                        height,
                        FaceEngine.CP_PAF_NV21,
                        listOf(rgbFaceInfo),
                        detectInfoMask
                    )
                    if (processResult == ErrorInfo.MOK) {
                        val recognizeInfo = getRecognizeInfo(faceId)
                        if (configuration.detectInfo.age) {
                            //获取年龄
                            val list: MutableList<AgeInfo> = mutableListOf()
                            val ageResult = detectInfoEngine.getAge(list)
                            if (ageResult == ErrorInfo.MOK && list.isNotEmpty()) {
                                recognizeInfo.age = list[0].age
                            } else {
                                onError(
                                    FaceErrorType.DETECT_AGE,
                                    ageResult,
                                    FaceConstant.getFaceErrorMsg(ageResult)
                                )
                            }
                        }
                        if (configuration.detectInfo.gender) {
                            //获取性别
                            val list: MutableList<GenderInfo> = mutableListOf()
                            val genderResult = detectInfoEngine.getGender(list)
                            if (genderResult == ErrorInfo.MOK && list.isNotEmpty()) {
                                recognizeInfo.gender = list[0].gender
                            } else {
                                onError(
                                    FaceErrorType.DETECT_GENDER,
                                    genderResult,
                                    FaceConstant.getFaceErrorMsg(genderResult)
                                )
                            }
                        }
                        if (configuration.detectInfo.angle) {
                            //获取人脸角度
                            val list: MutableList<Face3DAngle> = mutableListOf()
                            val angleResult = detectInfoEngine.getFace3DAngle(list)
                            if (angleResult == ErrorInfo.MOK && list.isNotEmpty()) {
                                recognizeInfo.angle = list[0]
                            } else {
                                onError(
                                    FaceErrorType.DETECT_ANGLE,
                                    angleResult,
                                    FaceConstant.getFaceErrorMsg(angleResult)
                                )
                            }
                        }
                        livenessResult = detectInfoEngine.getLiveness(livenessList)
                    } else {
                        livenessResult = processResult
                    }
                } else {
                    //该接口仅支持单人脸 IR 活体检测，超出返回未知。
                    val processResult = detectInfoEngine.processIr(
                        irNV21,
                        width,
                        height,
                        FaceEngine.CP_PAF_NV21,
                        listOf(irFaceInfo),
                        detectInfoMask
                    )
                    livenessResult = if (processResult == ErrorInfo.MOK) {
                        detectInfoEngine.getIrLiveness(livenessList)
                    } else {
                        processResult
                    }
                }
            }
            if (livenessResult == ErrorInfo.MOK && livenessList.isNotEmpty()) {
                onFaceLivenessInfoGet(livenessList[0], faceId, livenessResult)
            } else {
                onFaceLivenessInfoGet(null, faceId, livenessResult)
                onError(
                    FaceErrorType.DETECT_LIVENESS,
                    livenessResult,
                    FaceConstant.getFaceErrorMsg(livenessResult)
                )
            }
        }

    }

    /**
     * 人脸特征提取线程
     */
    inner class ExtractFeatureRunnable(
        private val rgbNV21: ByteArray,
        private val width: Int,
        private val height: Int,
        private val faceId: Int,
        private val faceInfo: FaceInfo,
        private val mask: Int
    ) : Runnable {

        override fun run() {
            if (configuration.enableImageQuality) {
                //启用图片质量检测
                val qualitySimilar = ImageQualitySimilar()
                val result: Int
                synchronized(extractFeatureEngine) {
                    result = extractFeatureEngine.imageQualityDetect(
                        rgbNV21,
                        width,
                        height,
                        FaceEngine.CP_PAF_NV21,
                        faceInfo,
                        mask,
                        qualitySimilar
                    )
                }
                if (result == ErrorInfo.MOK) {
                    val score = qualitySimilar.score
                    val threshold = if (mask == MaskInfo.WORN) {
                        configuration.imageQualityMaskRecognizeThreshold
                    } else {
                        configuration.imageQualityNoMaskRecognizeThreshold
                    }
                    if (score >= threshold) {
                        extractFaceFeature()
                    } else {
                        onFaceFeatureInfoGet(null, faceId, result, rgbNV21, mask, true)
                        onError(
                            FaceErrorType.IMAGE_QUALITY,
                            result,
                            "imageQuality score too low"
                        )
                    }
                } else {
                    onFaceFeatureInfoGet(null, faceId, result, rgbNV21, mask, true)
                    onError(
                        FaceErrorType.IMAGE_QUALITY,
                        result,
                        FaceConstant.getFaceErrorMsg(result)
                    )
                }
            } else {
                //直接人脸特征码检测
                extractFaceFeature()
            }
        }

        /**
         * 提取人脸特征码
         */
        private fun extractFaceFeature() {
            val faceFeature = FaceFeature()
            val result: Int
            synchronized(extractFeatureEngine) {
                result = extractFeatureEngine.extractFaceFeature(
                    rgbNV21,
                    width,
                    height,
                    FaceEngine.CP_PAF_NV21,
                    faceInfo,
                    if (configuration.enableCompareFace) {
                        ExtractType.RECOGNIZE
                    } else {
                        ExtractType.REGISTER
                    },
                    mask,
                    faceFeature
                )
            }
            if (result == ErrorInfo.MOK) {
                onFaceFeatureInfoGet(faceFeature, faceId, result, rgbNV21, mask, false)
            } else {
                onFaceFeatureInfoGet(null, faceId, result, rgbNV21, mask, false)
                onError(
                    FaceErrorType.EXTRACT_FEATURE,
                    result,
                    FaceConstant.getFaceErrorMsg(result)
                )
            }
        }
    }
}