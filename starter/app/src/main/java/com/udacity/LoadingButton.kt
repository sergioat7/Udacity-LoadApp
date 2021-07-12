package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val circleRadius = context.resources.getDimension(R.dimen.circleRadius)
    private var widthSize = 0
    private var heightSize = 0
    private var colorLoading: Int = 0
    private var colorCompleted: Int = 0
    private var textLoading: String? = null
    private var textCompleted: String? = null
    private var color: Int = 0
    private var text: String = ""
    private var sweepAngle = 0F

    private val colorPaint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.white)
        textAlign = Paint.Align.CENTER
        textSize = context.resources.getDimension(R.dimen.default_text_size)
    }
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.colorAccent)
    }

    private val valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                color = colorLoading
                text = textLoading ?: ""
                sweepAngle = 90F
            }
            ButtonState.Completed -> {
                color = colorCompleted
                text = textCompleted ?: ""
                sweepAngle = 0F
            }
            else -> Unit
        }
        invalidate()
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0).apply {
            colorLoading = getColor(R.styleable.LoadingButton_colorLoading, 0)
            colorCompleted = getColor(R.styleable.LoadingButton_colorCompleted, 0)
            textLoading = getString(R.styleable.LoadingButton_textLoading)
            textCompleted = getString(R.styleable.LoadingButton_textCompleted)
        }
        color = colorCompleted
        text = textCompleted ?: ""
        sweepAngle = 0F
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {

            colorPaint.color = color
            canvas.drawRect(0F, 0F, widthSize.toFloat(), heightSize.toFloat(), colorPaint)
            canvas.drawColor(color)

            val textHeight = textPaint.descent() - textPaint.ascent()
            val textOffset = textHeight / 2 - textPaint.descent()
            canvas.drawText(
                text,
                widthSize.toFloat() / 2,
                heightSize.toFloat() / 2 + textOffset,
                textPaint
            )

            canvas.drawArc(
                2 * widthSize.toFloat() / 3,
                heightSize.toFloat() / 2 - circleRadius,
                circleRadius * 2 + 2 * widthSize.toFloat() / 3,
                circleRadius + heightSize.toFloat() / 2,
                0F,
                sweepAngle,
                true,
                circlePaint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}