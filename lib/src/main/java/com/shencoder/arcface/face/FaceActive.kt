package com.shencoder.arcface.face

import android.content.Context
import com.arcsoft.face.ActiveFileInfo
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.FaceEngine
import com.arcsoft.face.model.ActiveDeviceInfo
import com.shencoder.arcface.callback.OnActiveCallback
import com.shencoder.arcface.callback.OnActiveDeviceInfoCallback
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.*

/**
 * 虹软人脸激活工具
 *
 * @author ShenBen
 * @date 2020/12/15 15:19
 * @email 714081644@qq.com
 */
object FaceActive {
    private val sExecutor: ExecutorService

    init {
        sExecutor = ThreadPoolExecutor(
            1, 1,
            0L, TimeUnit.MILLISECONDS,
            LinkedBlockingQueue()
        ) { r ->
            val t = Thread(r)
            t.name = "face-active-thread-" + t.id
            t
        }
    }

    /**
     * 在线激活
     * 子线程中运行
     *
     * @param context   上下文
     * @param activeKey activeKey
     * @param appId     appId
     * @param sdkKey    sdkKey
     * @return
     */
    @JvmStatic
    fun activeOnline(
        context: Context,
        activeKey: String,
        appId: String,
        sdkKey: String,
        callback: OnActiveCallback?
    ) {
        sExecutor.execute {
            val code = FaceEngine.activeOnline(context, activeKey, appId, sdkKey)
            val isSuccess = code == ErrorInfo.MOK || code == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED
            callback?.activeCallback(isSuccess, code)
        }
    }

    /**
     * 离线激活
     * 子线程中运行
     *
     * @param context  上下文
     * @param filePath 离线激活文件地址
     * @return
     */
    @JvmStatic
    fun activeOffline(context: Context, filePath: String, callback: OnActiveCallback?) {
        sExecutor.execute {
            val code = FaceEngine.activeOffline(context, filePath)
            val isSuccess = code == ErrorInfo.MOK || code == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED
            callback?.activeCallback(isSuccess, code)
        }
    }

    /**
     * 判断是否已经激活人脸
     *
     * @param context 上下文
     * @return 是否已经激活人脸
     */
    @JvmStatic
    fun isActivated(context: Context): Boolean {
        return FaceEngine.getActiveFileInfo(context, ActiveFileInfo()) == ErrorInfo.MOK
    }

    /**
     * 生成设备指纹信息，用于离线激活
     * 子线程运行
     *
     *
     * 请自行获取内存卡读写权限
     *
     * @param context 上下文
     * @param saveFilePath 保存设备指纹文件的文件地址
     * @param callback 结果回调
     */
    @JvmStatic
    fun generateActiveDeviceInfo(
        context: Context,
        saveFilePath: String,
        callback: OnActiveDeviceInfoCallback?
    ) {
        sExecutor.execute {
            val deviceInfo = ActiveDeviceInfo()
            val code = FaceEngine.getActiveDeviceInfo(context, deviceInfo)
            if (code == ErrorInfo.MOK) {
                val deviceInfoStr = deviceInfo.deviceInfo
                val file = File(saveFilePath)
                val parentFile = file.parentFile
                if (parentFile != null) {
                    if (!parentFile.exists()) {
                        parentFile.mkdirs()
                    }
                    if (file.exists()) {
                        file.delete()
                    }
                    var isSuccess: Boolean
                    try {
                        FileOutputStream(file).use { fos ->
                            fos.write(deviceInfoStr.toByteArray())
                            isSuccess = true
                        }
                    } catch (e: IOException) {
                        isSuccess = false
                    }
                    callback?.deviceInfoCallback(
                        isSuccess,
                        if (isSuccess) code else -1,
                        deviceInfoStr,
                        file
                    )
                } else {
                    callback?.deviceInfoCallback(false, -1, null, null)
                }
            } else {
                callback?.deviceInfoCallback(false, code, null, null)
            }
        }
    }
}