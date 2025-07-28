package com.margelo.nitro.spine

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.esotericsoftware.spine.android.*
import com.esotericsoftware.spine.android.bounds.Alignment
import com.esotericsoftware.spine.android.bounds.ContentMode

class HybridSpineView(context: Context) : HybridSpineViewSpec() {
  companion object {
    const val TAG = "HybridSpineView"
  }
  private val ctx = context
  private var _skelName = ""
  private var _atlasName = ""
  private var currentSpineView: SpineView? = null
  
  // Background view - your main view
  private val mainView = View(context)
  
  // Container that holds both mainView and spineView
  private val containerView = FrameLayout(context)

  override var skeletonName: String
    get() = _skelName
    set(value) {
      if (value != _skelName) {
        _skelName = value
        onUpdate()
      }
    }

  override var atlasName: String
    get() = _atlasName
    set(value) {
      if (value != _atlasName) {
        _atlasName = value
        onUpdate()
      }
    }

  override val view: View
    get() = containerView

  private fun onUpdate() {
    // Remove existing spine view if any
    currentSpineView?.let { 
      containerView.removeView(it)
      currentSpineView = null
    }
    
    // Only create new spine view if both atlas and skeleton names are provided
    if (_atlasName.isNotEmpty() && _skelName.isNotEmpty()) {
      val spineView = SpineView.loadFromAssets(_atlasName, _skelName, containerView.context, SpineController() {
        print(it.skeleton.data.animations.map { it.name })
        it.animationState.setAnimation(0, "Idle_Listening", true)
      })
      spineView.setContentMode(ContentMode.FILL)
      spineView.setAlignment(Alignment.CENTER)
      
      // Add spine view on top of mainView
      val layoutParams = RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.MATCH_PARENT,
        RelativeLayout.LayoutParams.MATCH_PARENT
      )
      containerView.addView(spineView, layoutParams)
      currentSpineView = spineView
    }
  }
} 