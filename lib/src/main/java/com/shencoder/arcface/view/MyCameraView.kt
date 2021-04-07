package com.shencoder.arcface.view

import io.fotoapparat.view.CameraView


/**
 *
 * @author  ShenBen
 * @date    2021/02/23 17:24
 * @email   714081644@qq.com
 */
fun CameraView.isMirror(isMirror: Boolean) {
    scaleX = if (isMirror) {
        -scaleX
    } else {
        scaleX
    }
}