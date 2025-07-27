package com.margelo.nitro.spine

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.spine.SkeletonRenderer
import com.margelo.nitro.NitroModules

class HybridSpineView : HybridSpineViewSpec() {
  companion object {
    const val TAG = "HybridSpineView"
  }

  // MARK: - SpineView Properties

  override var skeleton: HybridSpineSkeletonSpec? = null
    set(value) {
      field = value
      spineView?.skeleton = value as? HybridSpineSkeleton
    }

  override var premultipliedAlpha: Boolean = true
    set(value) {
      field = value
      spineView?.premultipliedAlpha = value
    }

  override var debug: Boolean = false
    set(value) {
      field = value
      spineView?.debug = value
    }

  // MARK: - Native View

  private var spineView: SpineRenderView? = null

  init {
    setupSpineView()
  }

  private fun setupSpineView() {
    val context = NitroModules.applicationContext?.currentActivity
    if (context != null) {
      spineView = SpineRenderView(context).apply {
        this.skeleton = this@HybridSpineView.skeleton as? HybridSpineSkeleton
        this.premultipliedAlpha = this@HybridSpineView.premultipliedAlpha
        this.debug = this@HybridSpineView.debug
      }
    }
  }

  // MARK: - SpineView Methods

  override fun invalidate() {
    spineView?.invalidate()
  }
}

// MARK: - Spine Rendering View

private class SpineRenderView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    var skeleton: HybridSpineSkeleton? = null
        set(value) {
            field = value
            setupSpineRenderer()
        }

    var premultipliedAlpha: Boolean = true
        set(value) {
            field = value
            // Update renderer settings if needed
        }

    var debug: Boolean = false
        set(value) {
            field = value
            // Update debug rendering
        }

    private var spriteBatch: SpriteBatch? = null
    private var skeletonRenderer: SkeletonRenderer? = null
    private var camera: OrthographicCamera? = null
    private var isRendering = false
    private var lastTime = 0L

    init {
        holder.addCallback(this)
        setWillNotDraw(false)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d("SpineRenderView", "Surface created")
        setupLibGDX()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d("SpineRenderView", "Surface changed: ${width}x${height}")
        setupCamera(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d("SpineRenderView", "Surface destroyed")
        isRendering = false
        cleanup()
    }

    private fun setupLibGDX() {
        // Initialize libGDX components for Android
        try {
            spriteBatch = SpriteBatch()
            skeletonRenderer = SkeletonRenderer().apply {
                premultipliedAlpha = this@SpineRenderView.premultipliedAlpha
            }
            isRendering = true
            startRenderLoop()
        } catch (e: Exception) {
            Log.e("SpineRenderView", "Failed to setup libGDX", e)
        }
    }

    private fun setupCamera(width: Int, height: Int) {
        camera = OrthographicCamera().apply {
            setToOrtho(false, width.toFloat(), height.toFloat())
            update()
        }
    }

    private fun setupSpineRenderer() {
        // This will be called when skeleton is set
        // Additional setup can be done here
    }

    private fun startRenderLoop() {
        Thread {
            lastTime = System.currentTimeMillis()
            while (isRendering) {
                try {
                    val canvas = holder.lockCanvas()
                    if (canvas != null) {
                        render(canvas)
                        holder.unlockCanvasAndPost(canvas)
                    }
                    Thread.sleep(16) // ~60fps
                } catch (e: Exception) {
                    Log.e("SpineRenderView", "Render error", e)
                }
            }
        }.start()
    }

    private fun render(canvas: Canvas) {
        val currentTime = System.currentTimeMillis()
        val deltaTime = (currentTime - lastTime) / 1000f
        lastTime = currentTime

        // Clear canvas
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        skeleton?.let { hybridSkeleton ->
            try {
                // Update skeleton animation
                hybridSkeleton.update(deltaTime.toDouble())

                // Draw skeleton using canvas (simplified rendering)
                drawSkeletonToCanvas(canvas, hybridSkeleton)

                if (debug) {
                    drawDebugInfo(canvas, hybridSkeleton)
                }
            } catch (e: Exception) {
                Log.e("SpineRenderView", "Failed to render skeleton", e)
                drawErrorMessage(canvas, "Failed to render skeleton: ${e.message}")
            }
        } ?: run {
            drawPlaceholder(canvas)
        }
    }

    private fun drawSkeletonToCanvas(canvas: Canvas, hybridSkeleton: HybridSpineSkeleton) {
        // This is a simplified rendering approach using Android Canvas
        // For full Spine rendering, you would need to integrate with libGDX properly
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLUE
            alpha = 150
        }

        // Draw skeleton bounds
        val centerX = width / 2f
        val centerY = height / 2f
        val skeletonWidth = hybridSkeleton.width.toFloat()
        val skeletonHeight = hybridSkeleton.height.toFloat()

        canvas.drawRect(
            centerX - skeletonWidth / 2,
            centerY - skeletonHeight / 2,
            centerX + skeletonWidth / 2,
            centerY + skeletonHeight / 2,
            paint
        )

        // Draw skeleton info
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 32f
            textAlign = Paint.Align.CENTER
        }

        val animations = hybridSkeleton.animations.joinToString(", ")
        canvas.drawText("Spine Skeleton", centerX, centerY - 20, textPaint)
        canvas.drawText("Animations: $animations", centerX, centerY + 20, textPaint)
    }

    private fun drawDebugInfo(canvas: Canvas, hybridSkeleton: HybridSpineSkeleton) {
        val debugPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }

        // Draw debug border
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), debugPaint)

        // Draw debug text
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            textSize = 24f
        }
        canvas.drawText("DEBUG MODE", 10f, 30f, textPaint)
        canvas.drawText("Size: ${hybridSkeleton.width}x${hybridSkeleton.height}", 10f, 60f, textPaint)
    }

    private fun drawPlaceholder(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }

        canvas.drawRect(20f, 20f, width - 20f, height - 20f, paint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.LTGRAY
            textSize = 32f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText("No Skeleton", width / 2f, height / 2f, textPaint)
    }

    private fun drawErrorMessage(canvas: Canvas, message: String) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            textSize = 24f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText("Error: $message", width / 2f, height / 2f, paint)
    }

    private fun cleanup() {
        spriteBatch?.dispose()
        skeletonRenderer = null
        camera = null
    }
} 