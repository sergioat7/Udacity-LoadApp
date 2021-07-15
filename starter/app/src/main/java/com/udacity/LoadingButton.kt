package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
    private var text: String = ""
    private var progress = 0.0F

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

    private var valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                text = textLoading ?: ""
                valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
                    duration = 3000L
                    repeatCount = ValueAnimator.INFINITE
                    addUpdateListener {
                        progress = it.animatedValue as Float
                        invalidate()
                    }
                    start()
                }
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                valueAnimator = ValueAnimator.ofFloat(progress, 1F).apply {
                    duration = 1000L
                    addUpdateListener {
                        progress = it.animatedValue as Float
                        invalidate()
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            text = textCompleted ?: ""
                            progress = 0.0F
                            invalidate()
                        }
                    })
                    start()
                }
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
        text = textCompleted ?: ""
        progress = 0.0F
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {

            canvas.drawColor(colorCompleted)

            colorPaint.color = colorLoading
            canvas.drawRect(
                0F,
                0F,
                widthSize.toFloat() * progress,
                heightSize.toFloat(),
                colorPaint
            )

            val textHeight = textPaint.descent() - textPaint.ascent()
            val textOffset = textHeight / 2 - textPaint.descent()
            canvas.drawText(
                text,
                widthSize.toFloat() / 2,
                heightSize.toFloat() / 2 + textOffset,
                textPaint
            )

            val arcX = 3 * widthSize.toFloat() / 4
            val arcY = heightSize.toFloat() / 2
            canvas.drawArc(
                arcX,
                arcY - circleRadius,
                circleRadius * 2 + arcX,
                circleRadius + arcY,
                0F,
                360F * progress,
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