package com.shencoder.arcface.constant

import android.util.SparseArray
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.LivenessInfo
import com.arcsoft.imageutil.ArcSoftImageUtilError

/**
 * @author ShenBen
 * @date 2020/12/15 15:36
 * @email 714081644@qq.com
 */
object FaceConstant {

    const val DEFAULT_ZOOM_RATIO = 0.25f

    /**
     * 未知错误
     */
    private const val UNKNOWN_ERROR = "unknown_error"

    /**
     * 人脸识别错误码名称Map
     */
    private val sFaceErrorMsgArray: SparseArray<String> = SparseArray()

    /**
     * Image错误码名称Map
     */
    private val sImageErrorMsgArray: SparseArray<String> = SparseArray()

    /**
     * 活体信息错误码名称Map
     */
    private val sLivenessErrorMsgArray: SparseArray<String> = SparseArray()

    init {
        sFaceErrorMsgArray.put(ErrorInfo.MOK, "成功")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_UNKNOWN, "错误原因不明")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_INVALID_PARAM, "无效的参数")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_UNSUPPORTED, "引擎不支持")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_NO_MEMORY, "内存不足")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_BAD_STATE, "状态错误")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_USER_CANCEL, "用户取消相关操作")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_EXPIRED, "操作时间过期")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_USER_PAUSE, "用户暂停操作")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_BUFFER_OVERFLOW, "缓冲上溢")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_BUFFER_UNDERFLOW, "缓冲下溢")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_NO_DISKSPACE, "存贮空间不足")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_COMPONENT_NOT_EXIST, "组件不存在")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_GLOBAL_DATA_NOT_EXIST, "全局数据不存在")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_FSDK_INVALID_APP_ID, "无效的AppId")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_FSDK_INVALID_SDK_ID, "无效的SDKKey")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_FSDK_INVALID_ID_PAIR, "AppId和SDKKey不匹配")
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_FSDK_MISMATCH_ID_AND_SDK,
            "SDKKey和使用的SDK不匹配（注意：调用初始化引擎接口时，请确认激活接口传入的参数，并重新激活）"
        )
        sFaceErrorMsgArray.put(ErrorInfo.MERR_FSDK_SYSTEM_VERSION_UNSUPPORTED, "系统版本不被当前SDK所支持")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_FSDK_FR_INVALID_MEMORY_INFO, "无效的输入内存")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_FSDK_FR_INVALID_IMAGE_INFO, "无效的输入图像参数")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_FSDK_FR_INVALID_FACE_INFO, "无效的脸部信息")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_FSDK_FR_NO_GPU_AVAILABLE, "当前设备无GPU可用")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_FSDK_FR_MISMATCHED_FEATURE_LEVEL, "待比较的两个人脸特征的版本不一致")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_FSDK_FACEFEATURE_UNKNOWN, "人脸特征检测错误未知")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_FSDK_FACEFEATURE_MEMORY, "人脸特征检测内存错误")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_FSDK_FACEFEATURE_INVALID_FORMAT, "人脸特征检测格式错误")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_FSDK_FACEFEATURE_INVALID_PARAM, "人脸特征检测参数错误")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_FSDK_FACEFEATURE_LOW_CONFIDENCE_LEVEL, "人脸特征检测结果置信度低")

        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_EX_FEATURE_UNSUPPORTED_ON_INIT, "Engine不支持的检测属性")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_EX_FEATURE_UNINITED, "需要检测的属性未初始化")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_EX_FEATURE_UNPROCESSED, "待获取的属性未在process中处理过")
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_EX_FEATURE_UNSUPPORTED_ON_PROCESS,
            "PROCESS不支持的检测属性，例如FR，有自己独立的处理函数"
        )
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_EX_INVALID_IMAGE_INFO, "无效的输入图像")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_EX_INVALID_FACE_INFO, "无效的脸部信息")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_ACTIVATION_FAIL, "SDK激活失败，请打开读写权限")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_ALREADY_ACTIVATED, "SDK已激活")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_NOT_ACTIVATED, "SDK未激活")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_SCALE_NOT_SUPPORT, "detectFaceScaleVal不支持")
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_ACTIVEFILE_SDKTYPE_MISMATCH,
            "激活文件与SDK类型不匹配，请确认使用的sdk"
        )
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_DEVICE_MISMATCH, "设备不匹配")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_UNIQUE_IDENTIFIER_ILLEGAL, "唯一标识不合法")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_PARAM_NULL, "参数为空")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_VERSION_NOT_SUPPORT, "版本不支持")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_SIGN_ERROR, "签名错误")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_DATABASE_ERROR, "激活信息保存异常")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_UNIQUE_CHECKOUT_FAIL, "唯一标识符校验失败")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_COLOR_SPACE_NOT_SUPPORT, "颜色空间不支持")
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_IMAGE_WIDTH_HEIGHT_NOT_SUPPORT,
            "图片宽高不支持，宽度需四字节对齐"
        )
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_READ_PHONE_STATE_DENIED,
            "android.permission.READ_PHONE_STATE权限被拒绝"
        )
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_ACTIVATION_DATA_DESTROYED,
            "激活数据被破坏,请删除激活文件，重新进行激活"
        )
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_SERVER_UNKNOWN_ERROR, "服务端未知错误")
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_INTERNET_DENIED,
            "android.permission.INTERNET权限被拒绝"
        )
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_ACTIVEFILE_SDK_MISMATCH, "激活文件与SDK版本不匹配,请重新激活")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_DEVICEINFO_LESS, "设备信息太少，不足以生成设备指纹")
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_LOCAL_TIME_NOT_CALIBRATED,
            "客户端时间与服务器时间（即北京时间）前后相差在30分钟以上"
        )
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_APPID_DATA_DECRYPT, "数据校验异常")
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_APPID_APPKEY_SDK_MISMATCH,
            "传入的AppId和AppKey与使用的SDK版本不一致"
        )
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_NO_REQUEST, "短时间大量请求会被禁止请求,30分钟之后解封")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_ACTIVE_FILE_NO_EXIST, "激活文件不存在")
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_CURRENT_DEVICE_TIME_INCORRECT,
            "当前设备时间不正确，请调整设备时间"
        )
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_DETECT_MODEL_UNSUPPORTED,
            "检测模型不支持，请查看对应接口说明，使用当前支持的检测模型"
        )
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_NETWORK_COULDNT_RESOLVE_HOST, "无法解析主机地址")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_NETWORK_COULDNT_CONNECT_SERVER, "无法连接服务器")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_NETWORK_CONNECT_TIMEOUT, "网络连接超时")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_NETWORK_UNKNOWN_ERROR, "网络未知错误")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_ACTIVEKEY_COULDNT_CONNECT_SERVER, "无法连接激活服务器")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_ACTIVEKEY_SERVER_SYSTEM_ERROR, "服务器系统错误")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_ACTIVEKEY_POST_PARM_ERROR, "请求参数错误")
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_ACTIVEKEY_PARM_MISMATCH,
            "ActiveKey与AppId、SDKKey不匹配"
        )
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_ACTIVEKEY_ACTIVEKEY_ACTIVATED, "ActiveKey已经被使用")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_ACTIVEKEY_ACTIVEKEY_FORMAT_ERROR, "ActiveKey信息异常")
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_ACTIVEKEY_APPID_PARM_MISMATCH,
            "ActiveKey与AppId不匹配"
        )
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_ACTIVEKEY_SDK_FILE_MISMATCH, "SDK与激活文件版本不匹配")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_ACTIVEKEY_EXPIRED, "ActiveKey已过期")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_LICENSE_FILE_NOT_EXIST, "离线授权文件不存在或无读写权限")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_LICENSE_FILE_DATA_DESTROYED, "离线授权文件已损坏")
        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_LICENSE_FILE_SDK_MISMATCH, "离线授权文件与SDK版本不匹配")
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_LICENSE_FILEINFO_SDKINFO_MISMATCH,
            "离线授权文件与SDK信息不匹配"
        )

        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_LICENSE_FILE_FINGERPRINT_MISMATCH,
            "离线授权文件与设备指纹不匹配"
        )

        sFaceErrorMsgArray.put(ErrorInfo.MERR_ASF_LICENSE_FILE_EXPIRED, "离线授权文件已过期")
        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_LOCAL_EXIST_USEFUL_ACTIVE_FILE,
            "离线授权文件不可用，本地原有激活文件可继续使用"
        )

        sFaceErrorMsgArray.put(
            ErrorInfo.MERR_ASF_LICENSE_FILE_VERSION_TOO_LOW,
            "离线授权文件版本过低，请使用新版本激活助手重新进行离线激活"
        )
    }

    init {
        sImageErrorMsgArray.put(ArcSoftImageUtilError.CODE_SUCCESS, "处理成功")
        sImageErrorMsgArray.put(ArcSoftImageUtilError.CODE_SIZE_MISMATCH, "指定格式的图像数据的长度和宽高不匹配")
        sImageErrorMsgArray.put(
            ArcSoftImageUtilError.CODE_UNSUPPORTED_BITMAP_FORMAT,
            "Bitmap对象的格式只支持Bitmap.Config.ARGB_8888和Bitmap.Config.RGB_565"
        )
        sImageErrorMsgArray.put(
            ArcSoftImageUtilError.CODE_GET_BITMAP_INFO_FAILED,
            "无法从Bitmap对象中获取信息"
        )
        sImageErrorMsgArray.put(ArcSoftImageUtilError.CODE_NULL_PARAMS, "传入了空参数")
        sImageErrorMsgArray.put(ArcSoftImageUtilError.CODE_SAME_OBJECT, "图像处理时的入参和出参是同一个对象")
        sImageErrorMsgArray.put(ArcSoftImageUtilError.CODE_INVALID_AREA, "指定的图像区域不合法")
        sImageErrorMsgArray.put(ArcSoftImageUtilError.CODE_WIDTH_HEIGHT_UNSUPPORTED, "不支持的宽高")
        sImageErrorMsgArray.put(ArcSoftImageUtilError.CODE_ROTATE_DEGREE_UNSUPPORTED, "不支持的旋转角度")
        sImageErrorMsgArray.put(ArcSoftImageUtilError.CODE_IMAGE_FORMAT_UNSUPPORTED, "不支持的图像格式")
    }

    init {
        sLivenessErrorMsgArray.put(LivenessInfo.NOT_ALIVE, "非真人")
        sLivenessErrorMsgArray.put(LivenessInfo.ALIVE, "真人")
        sLivenessErrorMsgArray.put(LivenessInfo.UNKNOWN, "不确定")
        sLivenessErrorMsgArray.put(LivenessInfo.FACE_NUM_MORE_THAN_ONE, "传入人脸数 > 1")
        sLivenessErrorMsgArray.put(LivenessInfo.FACE_TOO_SMALL, "人脸过小")
        sLivenessErrorMsgArray.put(LivenessInfo.FACE_ANGLE_TOO_LARGE, "角度过大")
        sLivenessErrorMsgArray.put(LivenessInfo.FACE_BEYOND_BOUNDARY, "人脸超出边界")
        sLivenessErrorMsgArray.put(LivenessInfo.ERROR_DEPTH, "深度图错误")
        sLivenessErrorMsgArray.put(LivenessInfo.TOO_BRIGHT_IR_IMAGE, "：红外图像太亮")
    }

    fun getFaceErrorMsg(status: Int): String {
        val msg = sFaceErrorMsgArray.get(status)
        return if (msg.isNullOrBlank()) UNKNOWN_ERROR else msg
    }

    fun getImageErrorMsg(status: Int): String {
        val msg = sImageErrorMsgArray.get(status)
        return if (msg.isNullOrBlank()) UNKNOWN_ERROR else msg
    }

    fun getLivenessErrorMsg(status: Int): String {
        val msg = sLivenessErrorMsgArray.get(status)
        return if (msg.isNullOrBlank()) UNKNOWN_ERROR else msg
    }

}