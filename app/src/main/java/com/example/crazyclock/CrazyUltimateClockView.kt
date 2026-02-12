package com.example.crazyclock

import android.animation.ValueAnimator
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.*
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.OvershootInterpolator
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CrazyUltimateClockView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    enum class Theme(val id: Int) { 
        PLAYFUL(0), NEON(1), PASTEL(2);
        companion object { fun fromId(id: Int) = values().find { it.id == id } ?: PLAYFUL }
    }

    private val prefs = context.getSharedPreferences("clock_prefs", MODE_PRIVATE)
    private var currentTheme = Theme.fromId(prefs.getInt("theme_id", 0))
    private var crazyOrder = loadOrder()

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
        textAlign = Paint.Align.CENTER 
        typeface = Typeface.DEFAULT_BOLD
    }
    private val handPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeCap = Paint.Cap.ROUND }
    
    private var animatedHourIndex = 0f
    private var isClockVisible = true

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        if (!isClockVisible) return

        val cx = width / 2f
        val cy = height / 2f
        val radius = min(width, height) / 2f * 0.9f

        drawBackground(canvas, cx, cy, radius)
        drawNumbers(canvas, cx, cy, radius * 0.8f)

        val cal = Calendar.getInstance()
        val h = cal.get(Calendar.HOUR).let { if (it == 0) 12 else it }
        val m = cal.get(Calendar.MINUTE)
        val s = cal.get(Calendar.SECOND)
        val ms = cal.get(Calendar.MILLISECOND)

        val targetH = crazyOrder.indexOf(h).toFloat()
        animatedHourIndex = lerp(animatedHourIndex, targetH, 0.1f)

        // Рисуем стрелки (Часовая, Минутная, Секундная)
        drawHand(canvas, cx, cy, angleForIndex(animatedHourIndex), radius * 0.5f, 16f, true)
        drawHand(canvas, cx, cy, Math.toRadians((m * 6 - 90).toDouble()), radius * 0.7f, 10f, true)
        drawHand(canvas, cx, cy, Math.toRadians(((s + ms/1000f) * 6 - 90).toDouble()), radius * 0.85f, 4f, false)

        canvas.drawCircle(cx, cy, 15f, handPaint.apply { shader = null; color = Color.GRAY })
        postInvalidateOnAnimation()
    }

    private fun drawBackground(canvas: Canvas, cx: Float, cy: Float, r: Float) {
        val colors = when(currentTheme) {
            Theme.PLAYFUL -> intArrayOf(Color.parseColor("#434343"), Color.BLACK)
            Theme.NEON -> intArrayOf(Color.parseColor("#004e92"), Color.parseColor("#000428"))
            Theme.PASTEL -> intArrayOf(Color.parseColor("#FADADD"), Color.parseColor("#FFF1E6"))
        }
        bgPaint.shader = RadialGradient(cx, cy, r, colors, null, Shader.TileMode.CLAMP)
        canvas.drawCircle(cx, cy, r, bgPaint)
    }

    private fun drawNumbers(canvas: Canvas, cx: Float, cy: Float, r: Float) {
        textPaint.textSize = r * 0.25f
        for (i in 0 until 12) {
            val angle = Math.toRadians((i * 30 - 90).toDouble())
            val x = cx + cos(angle) * r
            val y = cy + sin(angle) * r + (textPaint.textSize / 3)
            textPaint.color = if (currentTheme == Theme.PASTEL) Color.GRAY else Color.WHITE
            canvas.drawText(crazyOrder[i].toString(), x.toFloat(), y.toFloat(), textPaint)
        }
    }

    private fun drawHand(canvas: Canvas, cx: Float, cy: Float, angle: Double, len: Float, width: Float, isGold: Boolean) {
        handPaint.strokeWidth = width
        if (isGold) {
            handPaint.shader = LinearGradient(0f, 0f, width, len, Color.parseColor("#FFD700"), Color.parseColor("#B8860B"), Shader.TileMode.MIRROR)
        } else {
            handPaint.shader = null
            handPaint.color = Color.parseColor("#FF5252")
        }
        val x = cx + cos(angle) * len
        val y = cy + sin(angle) * len
        canvas.drawLine(cx, cy, x.toFloat(), y.toFloat(), handPaint)
    }

    private fun angleForIndex(index: Float) = Math.toRadians((index * 30 - 90).toDouble())
    private fun lerp(start: Float, stop: Float, fraction: Float) = start + (stop - start) * fraction

    override fun performClick(): Boolean {
        currentTheme = Theme.fromId((currentTheme.id + 1) % 3)
        prefs.edit().putInt("theme_id", currentTheme.id).apply()
        performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        return super.performClick()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        isClockVisible = visibility == VISIBLE
    }

    private fun loadOrder(): List<Int> {
        val s = prefs.getString("order", "")
        if (s.isNullOrEmpty()) {
            val newOrder = (1..12).shuffled().toMutableList()
            val idx = newOrder.indexOf(12); Collections.swap(newOrder, idx, 0)
            prefs.edit().putString("order", newOrder.joinToString(",")).apply()
            return newOrder
        }
        return s.split(",").map { it.toInt() }
    }
}
