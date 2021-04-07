package com.shencoder.arcface.view

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.shencoder.arcface.R
import android.graphics.LinearGradient
import kotlin.math.min

/**
 * @author ShenBen
 * @date 2021/02/24 9:18
 * @email 714081644@qq.com
 */
class ViewfinderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    /**
     * 画笔
     */
    private lateinit var paint: Paint

    /**
     * 文本画笔
     */
    private lateinit var textPaint: TextPaint

    /**
     * 扫码框外面遮罩颜色
     */
    private var maskColor = 0

    /**
     * 扫描区域边框颜色
     */
    private var frameColor = 0

    /**
     * 扫描线颜色
     */
    private var laserColor = 0

    /**
     * 扫码框四角颜色
     */
    private var cornerColor = 0

    /**
     * 提示文本与扫码框的边距
     */
    private var labelTextPadding = 0f

    /**
     * 提示文本的宽度
     */
    private var labelTextWidth = 0

    /**
     * 提示文本的位置
     */
    private lateinit var labelTextLocation: TextLocation

    /**
     * 扫描区域提示文本
     */
    private var labelText: String? = null

    /**
     * 扫描区域提示文本颜色
     */
    private var labelTextColor = 0

    /**
     * 提示文本字体大小
     */
    private var labelTextSize = 0f

    /**
     * 扫描线开始位置
     */
    var scannerStart = 0

    /**
     * 扫描线结束位置
     */
    var scannerEnd = 0

    /**
     * 扫码框宽
     */
    private var frameWidth = 0

    /**
     * 扫码框高
     */
    private var frameHeight = 0

    /**
     * 扫描激光线风格
     */
    private lateinit var laserStyle: LaserStyle

    /**
     * 网格列数
     */
    private var gridColumn = 0

    /**
     * 网格高度
     */
    private var gridHeight = 0

    /**
     * 扫码框
     */
    private lateinit var frame: Rect

    /**
     * 扫描区边角的宽
     */
    private var cornerRectWidth = 0

    /**
     * 扫描区边角的高
     */
    private var cornerRectHeight = 0

    /**
     * 扫描线每次移动距离
     */
    private var scannerLineMoveDistance = 0

    /**
     * 扫描线高度
     */
    private var scannerLineHeight = 0

    /**
     * 边框线宽度
     */
    private var frameLineWidth = 0

    /**
     * 扫描动画延迟间隔时间 默认20毫秒
     */
    private var scannerAnimationDelay = 0

    /**
     * 扫码框占比
     */
    private var frameRatio = 0f

    /**
     * 扫码框内间距
     */
    private var framePaddingLeft = 0f
    private var framePaddingTop = 0f
    private var framePaddingRight = 0f
    private var framePaddingBottom = 0f

    /**
     * 扫码框对齐方式
     */
    private lateinit var frameGravity: FrameGravity
    private var pointColor = 0
    private var pointStrokeColor = 0
    private var pointRadius = 0f
    private var needUpdateFrame = false

    enum class LaserStyle(val mValue: Int) {
        /**
         * 无
         */
        NONE(0),

        /**
         * 线
         */
        LINE(1),

        /**
         * 网格
         */
        GRID(2);

        companion object {
            fun getFromInt(value: Int): LaserStyle {
                for (style in values()) {
                    if (style.mValue == value) {
                        return style
                    }
                }
                return LINE
            }
        }
    }

    enum class TextLocation(private val mValue: Int) {
        TOP(0), BOTTOM(1);

        companion object {
            fun getFromInt(value: Int): TextLocation {
                for (location in values()) {
                    if (location.mValue == value) {
                        return location
                    }
                }
                return TOP
            }
        }
    }

    enum class FrameGravity(val mValue: Int) {
        CENTER(0), LEFT(1), TOP(2), RIGHT(3), BOTTOM(4);

        companion object {
            fun getFromInt(value: Int): FrameGravity {
                for (gravity in values()) {
                    if (gravity.mValue == value) {
                        return gravity
                    }
                }
                return CENTER
            }
        }
    }

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        //初始化自定义属性信息
        val array = context.obtainStyledAttributes(attrs, R.styleable.ViewfinderView)
        maskColor = array.getColor(
            R.styleable.ViewfinderView_maskColor,
            ContextCompat.getColor(context, R.color.viewfinder_mask)
        )
        frameColor = array.getColor(
            R.styleable.ViewfinderView_frameColor,
            ContextCompat.getColor(context, R.color.viewfinder_frame)
        )
        cornerColor = array.getColor(
            R.styleable.ViewfinderView_cornerColor,
            ContextCompat.getColor(context, R.color.viewfinder_corner)
        )
        laserColor = array.getColor(
            R.styleable.ViewfinderView_laserColor,
            ContextCompat.getColor(context, R.color.viewfinder_laser)
        )
        //        resultPointColor = array.getColor(R.styleable.ViewfinderView_resultPointColor, ContextCompat.getColor(context,R.color.viewfinder_result_point_color));
        labelText = array.getString(R.styleable.ViewfinderView_labelText)
        labelTextColor = array.getColor(
            R.styleable.ViewfinderView_labelTextColor,
            ContextCompat.getColor(context, R.color.viewfinder_text_color)
        )
        labelTextSize = array.getDimension(
            R.styleable.ViewfinderView_labelTextSize,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, resources.displayMetrics)
        )
        labelTextPadding = array.getDimension(
            R.styleable.ViewfinderView_labelTextPadding,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics)
        )
        labelTextWidth = array.getDimensionPixelSize(R.styleable.ViewfinderView_labelTextWidth, 0)
        labelTextLocation =
            TextLocation.getFromInt(array.getInt(R.styleable.ViewfinderView_labelTextLocation, 0))

//        isShowResultPoint = array.getBoolean(R.styleable.ViewfinderView_showResultPoint,false);
        frameWidth = array.getDimensionPixelSize(R.styleable.ViewfinderView_frameWidth, 0)
        frameHeight = array.getDimensionPixelSize(R.styleable.ViewfinderView_frameHeight, 0)
        laserStyle = LaserStyle.getFromInt(
            array.getInt(
                R.styleable.ViewfinderView_laserStyle,
                LaserStyle.LINE.mValue
            )
        )
        gridColumn = array.getInt(R.styleable.ViewfinderView_gridColumn, 20)
        gridHeight = array.getDimension(
            R.styleable.ViewfinderView_gridHeight,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                40f,
                resources.displayMetrics
            )
        ).toInt()
        cornerRectWidth = array.getDimension(
            R.styleable.ViewfinderView_cornerRectWidth,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                4f,
                resources.displayMetrics
            )
        ).toInt()
        cornerRectHeight = array.getDimension(
            R.styleable.ViewfinderView_cornerRectHeight,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16f,
                resources.displayMetrics
            )
        ).toInt()
        scannerLineMoveDistance = array.getDimension(
            R.styleable.ViewfinderView_scannerLineMoveDistance,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                2f,
                resources.displayMetrics
            )
        ).toInt()
        scannerLineHeight = array.getDimension(
            R.styleable.ViewfinderView_scannerLineHeight,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5f,
                resources.displayMetrics
            )
        ).toInt()
        frameLineWidth = array.getDimension(
            R.styleable.ViewfinderView_frameLineWidth,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                1f,
                resources.displayMetrics
            )
        ).toInt()
        scannerAnimationDelay =
            array.getInteger(R.styleable.ViewfinderView_scannerAnimationDelay, 20)
        frameRatio = array.getFloat(R.styleable.ViewfinderView_frameRatio, 0.625f)
        framePaddingLeft = array.getDimension(R.styleable.ViewfinderView_framePaddingLeft, 0f)
        framePaddingTop = array.getDimension(R.styleable.ViewfinderView_framePaddingTop, 0f)
        framePaddingRight = array.getDimension(R.styleable.ViewfinderView_framePaddingRight, 0f)
        framePaddingBottom = array.getDimension(R.styleable.ViewfinderView_framePaddingBottom, 0f)
        frameGravity = FrameGravity.getFromInt(
            array.getInt(
                R.styleable.ViewfinderView_frameGravity,
                FrameGravity.CENTER.mValue
            )
        )
        array.recycle()
        pointColor = laserColor
        pointStrokeColor = Color.WHITE
        pointRadius =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    }

    private val displayMetrics: DisplayMetrics
        get() = resources.displayMetrics

    fun setLabelText(labelText: String?) {
        this.labelText = labelText
    }

    fun setLabelTextLocation(labelTextLocation: TextLocation) {
        this.labelTextLocation = labelTextLocation
    }

    fun setLabelTextColor(@ColorInt color: Int) {
        labelTextColor = color
    }

    fun setLabelTextColorResource(@ColorRes id: Int) {
        labelTextColor = ContextCompat.getColor(context, id)
    }

    fun setLabelTextSize(textSize: Float) {
        labelTextSize = textSize
    }

    fun setFrameRatio(frameRatio: Float) {
        if (frameRatio == this.frameRatio) {
            return
        }
        this.frameRatio = frameRatio
        needUpdateFrame = true
    }

    private fun initFrame() {
        val width = width
        val height = height
        val size = (min(width, height) * frameRatio).toInt()
        if (frameWidth <= 0 || frameWidth > width) {
            frameWidth = size
        }
        if (frameHeight <= 0 || frameHeight > height) {
            frameHeight = size
        }
        if (labelTextWidth <= 0) {
            labelTextWidth = width - paddingLeft - paddingRight
        }
        var leftOffsets = (width - frameWidth shr 1) + framePaddingLeft - framePaddingRight
        var topOffsets = (height - frameHeight shr 1) + framePaddingTop - framePaddingBottom
        when (frameGravity) {
            FrameGravity.LEFT -> leftOffsets = framePaddingLeft
            FrameGravity.TOP -> topOffsets = framePaddingTop
            FrameGravity.RIGHT -> leftOffsets = width - frameWidth + framePaddingRight
            FrameGravity.BOTTOM -> topOffsets = height - frameHeight + framePaddingBottom
            else -> {
            }
        }
        frame = Rect(
            leftOffsets.toInt(),
            topOffsets.toInt(),
            leftOffsets.toInt() + frameWidth,
            topOffsets.toInt() + frameHeight
        )
    }

    public override fun onDraw(canvas: Canvas) {
        val width = width
        val height = height
        if (this::frame.isInitialized.not() || needUpdateFrame) {
            initFrame()
            scannerStart = frame.top
            scannerEnd = frame.bottom - scannerLineHeight
            needUpdateFrame = false
        }

        // 绘制模糊区域
        drawExterior(canvas, frame, width, height)
        // 绘制扫描动画
        drawLaserScanner(canvas, frame)
        // 绘制取景区域框
        drawFrame(canvas, frame)
        // 绘制取景区域边角
        drawCorner(canvas, frame)
        //绘制提示信息
        drawTextInfo(canvas, frame)
        // 间隔更新取景区域
        postInvalidateDelayed(
            scannerAnimationDelay.toLong(),
            frame.left,
            frame.top,
            frame.right,
            frame.bottom
        )
    }

    /**
     * 绘制文本
     *
     * @param canvas
     * @param frame
     */
    private fun drawTextInfo(canvas: Canvas, frame: Rect) {
        if (!TextUtils.isEmpty(labelText)) {
            textPaint.color = labelTextColor
            textPaint.textSize = labelTextSize
            textPaint.textAlign = Paint.Align.CENTER
            val staticLayout = StaticLayout(
                labelText,
                textPaint,
                labelTextWidth,
                Layout.Alignment.ALIGN_NORMAL,
                1.2f,
                0.0f,
                true
            )
            if (labelTextLocation == TextLocation.BOTTOM) {
                canvas.translate(
                    frame.left + (frame.width() shr 1).toFloat(),
                    frame.bottom + labelTextPadding
                )
            } else {
                canvas.translate(
                    frame.left + (frame.width() shr 1).toFloat(),
                    frame.top - labelTextPadding - staticLayout.height
                )
            }
            staticLayout.draw(canvas)
        }
    }

    /**
     * 绘制边角
     *
     * @param canvas
     * @param frame
     */
    private fun drawCorner(canvas: Canvas, frame: Rect) {
        paint.color = cornerColor
        //左上
        canvas.drawRect(
            frame.left.toFloat(),
            frame.top.toFloat(),
            frame.left + cornerRectWidth.toFloat(),
            frame.top + cornerRectHeight.toFloat(),
            paint
        )
        canvas.drawRect(
            frame.left.toFloat(),
            frame.top.toFloat(),
            frame.left + cornerRectHeight.toFloat(),
            frame.top + cornerRectWidth.toFloat(),
            paint
        )
        //右上
        canvas.drawRect(
            frame.right - cornerRectWidth.toFloat(),
            frame.top.toFloat(),
            frame.right.toFloat(),
            frame.top + cornerRectHeight.toFloat(),
            paint
        )
        canvas.drawRect(
            frame.right - cornerRectHeight.toFloat(),
            frame.top.toFloat(),
            frame.right.toFloat(),
            frame.top + cornerRectWidth.toFloat(),
            paint
        )
        //左下
        canvas.drawRect(
            frame.left.toFloat(),
            frame.bottom - cornerRectWidth.toFloat(),
            frame.left + cornerRectHeight.toFloat(),
            frame.bottom.toFloat(),
            paint
        )
        canvas.drawRect(
            frame.left.toFloat(),
            frame.bottom - cornerRectHeight.toFloat(),
            frame.left + cornerRectWidth.toFloat(),
            frame.bottom.toFloat(),
            paint
        )
        //右下
        canvas.drawRect(
            frame.right - cornerRectWidth.toFloat(),
            frame.bottom - cornerRectHeight.toFloat(),
            frame.right.toFloat(),
            frame.bottom.toFloat(),
            paint
        )
        canvas.drawRect(
            frame.right - cornerRectHeight.toFloat(),
            frame.bottom - cornerRectWidth.toFloat(),
            frame.right.toFloat(),
            frame.bottom.toFloat(),
            paint
        )
    }

    /**
     * 绘制激光扫描线
     *
     * @param canvas
     * @param frame
     */
    private fun drawLaserScanner(canvas: Canvas, frame: Rect) {
        paint.color = laserColor
        when (laserStyle) {
            LaserStyle.LINE ->                     //线
                drawLineScanner(canvas, frame)
            LaserStyle.GRID ->                     //网格
                drawGridScanner(canvas, frame)
            LaserStyle.NONE -> {
            }
        }
        paint.shader = null
    }

    /**
     * 绘制线性式扫描
     *
     * @param canvas
     * @param frame
     */
    private fun drawLineScanner(canvas: Canvas, frame: Rect) {
        //线性渐变
        val linearGradient = LinearGradient(
            frame.left.toFloat(), scannerStart.toFloat(),
            frame.left.toFloat(), (scannerStart + scannerLineHeight).toFloat(),
            shadeColor(laserColor),
            laserColor,
            Shader.TileMode.MIRROR
        )
        paint.shader = linearGradient
        if (scannerStart <= scannerEnd) {
            //椭圆
            val rectF = RectF(
                (frame.left + 2 * scannerLineHeight).toFloat(),
                scannerStart.toFloat(),
                (frame.right - 2 * scannerLineHeight).toFloat(),
                (scannerStart + scannerLineHeight).toFloat()
            )
            canvas.drawOval(rectF, paint)
            scannerStart += scannerLineMoveDistance
        } else {
            scannerStart = frame.top
        }
    }

    /**
     * 绘制网格式扫描
     *
     * @param canvas
     * @param frame
     */
    private fun drawGridScanner(canvas: Canvas, frame: Rect?) {
        val stroke = 2
        paint.strokeWidth = stroke.toFloat()
        //计算Y轴开始位置
        val startY =
            if (gridHeight > 0 && scannerStart - frame!!.top > gridHeight) scannerStart - gridHeight else frame!!.top
        val linearGradient = LinearGradient(
            (frame.left + (frame.width() shr 1)).toFloat(),
            startY.toFloat(),
            (frame.left + frame.width() / 2).toFloat(),
            scannerStart.toFloat(),
            intArrayOf(shadeColor(laserColor), laserColor),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        //给画笔设置着色器
        paint.shader = linearGradient
        val wUnit = frame.width() * 1.0f / gridColumn
        //遍历绘制网格纵线
        for (i in 1 until gridColumn) {
            canvas.drawLine(
                frame.left + i * wUnit,
                startY.toFloat(),
                frame.left + i * wUnit,
                scannerStart.toFloat(),
                paint
            )
        }
        val height =
            if (gridHeight > 0 && scannerStart - frame.top > gridHeight) gridHeight else scannerStart - frame.top

        //遍历绘制网格横线
        var i = 0
        while (i <= height / wUnit) {
            canvas.drawLine(
                frame.left.toFloat(),
                scannerStart - i * wUnit,
                frame.right.toFloat(),
                scannerStart - i * wUnit,
                paint
            )
            i++
        }
        if (scannerStart < scannerEnd) {
            scannerStart += scannerLineMoveDistance
        } else {
            scannerStart = frame.top
        }
    }

    /**
     * 处理颜色模糊
     *
     * @param color
     * @return
     */
    fun shadeColor(color: Int): Int {
        val hax = Integer.toHexString(color)
        val result = "01" + hax.substring(2)
        return Integer.valueOf(result, 16)
    }

    /**
     * 绘制扫描区边框
     *
     * @param canvas
     * @param frame
     */
    private fun drawFrame(canvas: Canvas, frame: Rect) {
        paint.color = frameColor
        canvas.drawRect(
            frame.left.toFloat(),
            frame.top.toFloat(),
            frame.right.toFloat(),
            frame.top + frameLineWidth.toFloat(),
            paint
        )
        canvas.drawRect(
            frame.left.toFloat(),
            frame.top.toFloat(),
            frame.left + frameLineWidth.toFloat(),
            frame.bottom.toFloat(),
            paint
        )
        canvas.drawRect(
            frame.right - frameLineWidth.toFloat(),
            frame.top.toFloat(),
            frame.right.toFloat(),
            frame.bottom.toFloat(),
            paint
        )
        canvas.drawRect(
            frame.left.toFloat(),
            frame.bottom - frameLineWidth.toFloat(),
            frame.right.toFloat(),
            frame.bottom.toFloat(),
            paint
        )
    }

    /**
     * 绘制模糊区域
     *
     * @param canvas
     * @param frame
     * @param width
     * @param height
     */
    private fun drawExterior(canvas: Canvas, frame: Rect, width: Int, height: Int) {
        if (maskColor != 0) {
            paint.color = maskColor
            canvas.drawRect(0f, 0f, width.toFloat(), frame.top.toFloat(), paint)
            canvas.drawRect(
                0f,
                frame.top.toFloat(),
                frame.left.toFloat(),
                frame.bottom.toFloat(),
                paint
            )
            canvas.drawRect(
                frame.right.toFloat(),
                frame.top.toFloat(),
                width.toFloat(),
                frame.bottom.toFloat(),
                paint
            )
            canvas.drawRect(0f, frame.bottom.toFloat(), width.toFloat(), height.toFloat(), paint)
        }
    }

    fun drawViewfinder() {
        invalidate()
    }

    fun setLaserStyle(laserStyle: LaserStyle) {
        this.laserStyle = laserStyle
    }

    fun getFrameRect(): Rect {
        if (this::frame.isInitialized.not()) {
            initFrame()
        }
        return frame
    }
}