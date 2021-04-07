package com.shencoder.arcface.configuration

/**
 * SDK初始化引擎中可选择仅对0度、90度、180度、270度单角度进行人脸检测，对于VIDEO模式也
 * 可选择全角度进行检测；根据应用场景，推荐使用单角度进行人脸检测，因为选择全角度的情况下，算
 * 法会对每个角度检测一遍，导致性能相对于单角度较慢。IMAGE模式下为了提高识别率不支持全角度检
 * 测。
 *
 * @author ShenBen
 * @date 2021/02/24 14:26
 * @email 714081644@qq.com
 */
enum class DetectFaceOrient(val priority: Int) {
    /**
     * 仅0度
     */
    ASF_OP_0_ONLY(1),

    /**
     * 仅90度
     */
    ASF_OP_90_ONLY(2),

    /**
     * 仅180度
     */
    ASF_OP_180_ONLY(4),

    /**
     * 仅270度
     */
    ASF_OP_270_ONLY(3),

    /**
     * 全角度
     */
    ASF_OP_ALL_OUT(5);
}