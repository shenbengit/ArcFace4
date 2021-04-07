package com.shencoder.arcface.face

import android.content.Context
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.FaceEngine
import com.arcsoft.face.FaceInfo
import com.arcsoft.face.enums.DetectFaceOrientPriority
import com.arcsoft.face.enums.DetectMode
import com.shencoder.arcface.callback.FaceDetectCallback
import com.shencoder.arcface.constant.FaceConstant
import com.shencoder.arcface.util.LogUtil
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 *
 * @author  ShenBen
 * @date    2021/03/25 18:50
 * @email   714081644@qq.com
 */
class FaceDetect {
    companion object {
        private const val TAG = "FaceDetect-->"
    }

    /**
     * 虹软人脸检测信息
     */
    private val faceInfoList: MutableList<FaceInfo> = mutableListOf()

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
            t.name = "face-detect-thread-" + t.id
            t
        }
    }

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

    private var mFaceDetectCallback: FaceDetectCallback? = null

    fun init(
        context: Context,
        enableImageQuality: Boolean = false,
        detectFaceMaxNum: Int,
        detectFaceOrient: DetectFaceOrientPriority = DetectFaceOrientPriority.ASF_OP_0_ONLY
    ) {
        //初始化人脸检测引擎
        var mask = FaceEngine.ASF_FACE_DETECT
        if (enableImageQuality) {
            mask = mask or FaceEngine.ASF_IMAGEQUALITY
        }

        val result = detectFaceEngine.init(
            context,
            DetectMode.ASF_DETECT_MODE_VIDEO,
            DetectFaceOrientPriority.valueOf(detectFaceOrient.name),
            detectFaceMaxNum,
            mask
        )

        LogUtil.i("${TAG}人脸检测引擎初始化:$result")
    }

    fun setFaceDetectCallback(callback: FaceDetectCallback?) {
        this.mFaceDetectCallback = callback
    }

    /**
     * 预览数据
     */
    fun onPreviewFrame(
        rgbNV21: ByteArray,
        previewWidth: Int,
        previewHeight: Int
    ) {
        detectFaceExecutor.execute {
            faceInfoList.clear()
            //人脸检测
            val result = detectFaceEngine.detectFaces(
                rgbNV21,
                previewWidth,
                previewHeight,
                FaceEngine.CP_PAF_NV21,
                faceInfoList
            )
            if (result != ErrorInfo.MOK) {
                LogUtil.w(
                    "${TAG}destroy-onPreviewFrame.detectFaces:$result,msg:${
                        FaceConstant.getFaceErrorMsg(
                            result
                        )
                    }"
                )
                return@execute
            }
            anybody(faceInfoList)
        }
    }

    inline fun setFaceDetectCallback(
        crossinline someone: () -> Unit = {},
        crossinline nobody: () -> Unit = {},
        crossinline detectFaceNum: (num: Int, faceIds: List<Int>) -> Unit = { _, _ -> },
    ) {
        setFaceDetectCallback(object : FaceDetectCallback {
            override fun someone() = someone()

            override fun nobody() = nobody()

            override fun detectFaceNum(num: Int, faceIds: List<Int>) = detectFaceNum(num, faceIds)
        })
    }

    /**
     * 销毁资源
     */
    fun destroy() {
        if (detectFaceExecutor.isShutdown.not()) {
            detectFaceExecutor.shutdownNow()
        }
        synchronized(detectFaceEngine) {
            val result = detectFaceEngine.unInit()
            LogUtil.w("${TAG}destroy-detectFaceEngine.unInit:$result")
        }
        mFaceDetectCallback = null
    }

    /**
     * 判断是否有人
     */
    private fun anybody(faceInfoList: List<FaceInfo>) {
        //人脸信息列表不为空则是有人,为空则是无人
        val anybody = faceInfoList.isNotEmpty()
        if (isAnybody != anybody) {
            if (anybody) {
                mFaceDetectCallback?.someone()
            } else {
                mFaceDetectCallback?.nobody()
            }
            isAnybody = anybody
        }
        val num = faceInfoList.size
        if (detectFaceNum != num) {
            val faceIds: ArrayList<Int> = ArrayList(faceInfoList.size)
            faceInfoList.forEach { faceIds.add(it.faceId) }
            mFaceDetectCallback?.detectFaceNum(num, faceIds)
            detectFaceNum = num
        }
    }
}