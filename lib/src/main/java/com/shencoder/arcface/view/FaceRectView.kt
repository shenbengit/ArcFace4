package com.shencoder.arcface.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import java.util.concurrent.CopyOnWriteArrayList

/**
 *
 * @author  ShenBen
 * @date    2021/02/26 9:00
 * @email   714081644@qq.com
 */
internal class FaceRectView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val drawInfoList: MutableList<DrawInfo> = CopyOnWriteArrayList()

    /**
     * 画笔
     */
    private val paint = Paint()

    init {
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) {
            return
        }
        for (drawInfo in drawInfoList) {
            drawFaceRect(canvas, drawInfo, paint)
        }
    }

    /**
     * 清除所有人脸框信息
     */
    fun clearFaceInfo() {
        drawInfoList.clear()
        postInvalidate()
    }

    fun drawRealtimeFaceInfo(list: List<DrawInfo>?) {
        drawInfoList.clear()
        list?.let {
            drawInfoList.addAll(it)
        }
        postInvalidate()
    }

    private fun drawFaceRect(canvas: Canvas, drawInfo: DrawInfo, paint: Paint) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        paint.color = drawInfo.color

        val path = Path()
        // 左上
        val rect: Rect = drawInfo.rect
        path.moveTo(rect.left.toFloat(), rect.top + (rect.height() shr 2).toFloat())
        path.lineTo(rect.left.toFloat(), rect.top.toFloat())
        path.lineTo(rect.left + (rect.width() shr 2).toFloat(), rect.top.toFloat())
        // 右上
        path.moveTo(rect.right - (rect.width() shr 2).toFloat(), rect.top.toFloat())
        path.lineTo(rect.right.toFloat(), rect.top.toFloat())
        path.lineTo(rect.right.toFloat(), rect.top + (rect.height() shr 2).toFloat())
        // 右下
        path.moveTo(rect.right.toFloat(), rect.bottom - (rect.height() shr 2).toFloat())
        path.lineTo(rect.right.toFloat(), rect.bottom.toFloat())
        path.lineTo(rect.right - (rect.width() shr 2).toFloat(), rect.bottom.toFloat())
        // 左下
        path.moveTo(rect.left + (rect.width() shr 2).toFloat(), rect.bottom.toFloat())
        path.lineTo(rect.left.toFloat(), rect.bottom.toFloat())
        path.lineTo(rect.left.toFloat(), rect.bottom - (rect.height() shr 2).toFloat())
        canvas.drawPath(path, paint)

        drawInfo.name?.let {
            // 绘制文字，用最细的即可，避免在某些低像素设备上文字模糊
            paint.strokeWidth = 1f
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.textSize = (rect.width() shr 3.toFloat().toInt()).toFloat()

            canvas.drawText(it, rect.left.toFloat(), rect.top - 10.toFloat(), paint)
        }
    }

    internal data class DrawInfo(
        val rect: Rect,
        val sex: Int,
        val age: Int,
        val liveness: Int,
        val name: String?,
        val color: Int
    )
}