package com.example.zigzag

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.min

class GridCellView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = 22f * resources.displayMetrics.density
        color = Color.WHITE
    }

    private val backgroundPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    var text: String = ""
        set(value) {
            field = value
            invalidate()
        }

    var isCellSelected: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    var selectionColor: Int = Color.RED
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isCellSelected) {
            backgroundPaint.color = selectionColor
            canvas.drawCircle(width / 2f, height / 2f, min(width, height) / 2f - 4f, backgroundPaint)
            
            val borderPaint = Paint().apply {
                color = Color.WHITE
                style = Paint.Style.STROKE
                strokeWidth = 2f
                isAntiAlias = true
            }
            canvas.drawCircle(width / 2f, height / 2f, min(width, height) / 2f - 2f, borderPaint)
        }
        canvas.drawText(text, width / 2f, height / 2f + paint.textSize / 3, paint)
    }
} 