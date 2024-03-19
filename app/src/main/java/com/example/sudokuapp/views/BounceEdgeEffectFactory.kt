package com.example.sudokuapp.views

import android.content.Context
import android.graphics.Canvas
import android.util.TypedValue
import android.widget.EdgeEffect
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView

private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 1.0f

class BounceEdgeEffectFactory(context: Context): RecyclerView.EdgeEffectFactory() {
    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
        return object : EdgeEffect(view.context){

            var anim: SpringAnimation? = null

            override fun onPull(deltaDistance: Float) {
                super.onPull(deltaDistance)
                handlePull(deltaDistance)
            }

            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)
                handlePull(deltaDistance)
            }

            override fun onRelease() {
                super.onRelease()

                if (view.translationY != 0f) {
                    anim = createAnim()?.also { it.start() }
                }
            }

            override fun onAbsorb(velocity: Int) {
                super.onAbsorb(velocity)

                val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                val translationVelocity = sign * velocity * OVERSCROLL_TRANSLATION_MAGNITUDE
                anim?.cancel()
                anim = createAnim().setStartVelocity(translationVelocity)?.also { it.start() }
            }

            override fun draw(canvas: Canvas?): Boolean {
                return false
            }

            override fun isFinished(): Boolean {
                return anim?.isRunning?.not() ?: true
            }

            private fun handlePull(deltaDistance: Float){
                val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                val translationYDelta = sign * view.width * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE

                /* MaxTranslation is 150dp*/
                val maxTranslation = 150.dpToPx(view.context)
                if ((view.translationY + translationYDelta) * sign > maxTranslation) {
                    view.translationY = maxTranslation.toFloat() * sign
                } else {
                    view.translationY += translationYDelta
                }

                anim?.cancel()

            }

            private fun createAnim() = SpringAnimation(view, SpringAnimation.TRANSLATION_Y)
                .setSpring(SpringForce()
                    .setFinalPosition(0f)
                    .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                    .setStiffness(SpringForce.STIFFNESS_LOW)
                )

            fun Int.dpToPx(context: Context): Int {
                return TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
                ).toInt()
            }

        }
    }
}