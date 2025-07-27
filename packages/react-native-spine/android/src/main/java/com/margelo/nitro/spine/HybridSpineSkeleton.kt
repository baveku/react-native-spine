package com.margelo.nitro.spine

import android.util.Log
import com.esotericsoftware.spine.*

class HybridSpineSkeleton(
  private val spineSkeletonData: SkeletonData,
  private val spineAtlas: TextureAtlas
) : HybridSpineSkeletonSpec() {
  
  companion object {
    const val TAG = "HybridSpineSkeleton"
  }

  // MARK: - Spine Objects
  
  private val spineSkeleton: Skeleton = Skeleton(spineSkeletonData)
  private val spineAnimationStateData: AnimationStateData = AnimationStateData(spineSkeletonData)
  private val spineAnimationState: AnimationState = AnimationState(spineAnimationStateData)
  
  private var animationListener: SpineAnimationStateListener? = null

  init {
    // Setup default mix duration
    spineAnimationStateData.defaultMix = 0.25f
  }

  // MARK: - Properties

  override val width: Double
    get() = spineSkeletonData.width.toDouble()

  override val height: Double
    get() = spineSkeletonData.height.toDouble()

  override val defaultSkin: String
    get() = spineSkeletonData.defaultSkin?.name ?: "default"

  override val skins: Array<String>
    get() {
      val skinNames = mutableListOf<String>()
      for (skin in spineSkeletonData.skins) {
        skinNames.add(skin.name)
      }
      return skinNames.toTypedArray()
    }

  override val animations: Array<String>
    get() {
      val animationNames = mutableListOf<String>()
      for (animation in spineSkeletonData.animations) {
        animationNames.add(animation.name)
      }
      return animationNames.toTypedArray()
    }

  override val bones: Array<String>
    get() {
      val boneNames = mutableListOf<String>()
      for (bone in spineSkeletonData.bones) {
        boneNames.add(bone.name)
      }
      return boneNames.toTypedArray()
    }

  override val slots: Array<String>
    get() {
      val slotNames = mutableListOf<String>()
      for (slot in spineSkeletonData.slots) {
        slotNames.add(slot.name)
      }
      return slotNames.toTypedArray()
    }

  // MARK: - Methods

  override fun setAnimation(trackIndex: Double, animationName: String, loop: Boolean): SpineTrackEntry {
    Log.d(TAG, "setAnimation: track=$trackIndex, animation=$animationName, loop=$loop")
    
    val trackEntry = spineAnimationState.setAnimation(trackIndex.toInt(), animationName, loop)
    return createTrackEntryInterface(trackEntry)
  }

  override fun addAnimation(trackIndex: Double, animationName: String, loop: Boolean, delay: Double): SpineTrackEntry {
    Log.d(TAG, "addAnimation: track=$trackIndex, animation=$animationName, loop=$loop, delay=$delay")
    
    val trackEntry = spineAnimationState.addAnimation(trackIndex.toInt(), animationName, loop, delay.toFloat())
    return createTrackEntryInterface(trackEntry)
  }

  override fun setSkin(skinName: String) {
    Log.d(TAG, "setSkin: skin=$skinName")
    
    val skin = spineSkeletonData.findSkin(skinName)
    if (skin != null) {
      spineSkeleton.skin = skin
      spineSkeleton.setSlotsToSetupPose()
    } else {
      throw Exception("Skin '$skinName' not found")
    }
  }

  override fun setTimeScale(timeScale: Double) {
    Log.d(TAG, "setTimeScale: timeScale=$timeScale")
    spineAnimationState.timeScale = timeScale.toFloat()
  }

  override fun clearTrack(trackIndex: Double) {
    Log.d(TAG, "clearTrack: track=$trackIndex")
    spineAnimationState.clearTrack(trackIndex.toInt())
  }

  override fun clearTracks() {
    Log.d(TAG, "clearTracks")
    spineAnimationState.clearTracks()
  }

  override fun update(deltaTime: Double) {
    Log.d(TAG, "update: deltaTime=$deltaTime")
    
    spineAnimationState.update(deltaTime.toFloat())
    spineAnimationState.apply(spineSkeleton)
    spineSkeleton.updateWorldTransform()
  }

  override fun setAnimationStateListener(listener: SpineAnimationStateListener?) {
    Log.d(TAG, "setAnimationStateListener: listener=$listener")
    
    this.animationListener = listener
    
    if (listener != null) {
      // Set up Spine libGDX animation state listener
      spineAnimationState.addListener(object : AnimationState.AnimationStateListener {
        override fun start(entry: TrackEntry) {
          val trackEntry = createTrackEntryInterface(entry)
          listener.onAnimationStart?.invoke(trackEntry)
        }

        override fun interrupt(entry: TrackEntry) {
          val trackEntry = createTrackEntryInterface(entry)
          listener.onAnimationInterrupt?.invoke(trackEntry)
        }

        override fun end(entry: TrackEntry) {
          val trackEntry = createTrackEntryInterface(entry)
          listener.onAnimationEnd?.invoke(trackEntry)
        }

        override fun complete(entry: TrackEntry) {
          val trackEntry = createTrackEntryInterface(entry)
          listener.onAnimationComplete?.invoke(trackEntry)
        }

        override fun dispose(entry: TrackEntry) {
          val trackEntry = createTrackEntryInterface(entry)
          listener.onAnimationDispose?.invoke(trackEntry)
        }

        override fun event(entry: TrackEntry, event: Event) {
          val trackEntry = createTrackEntryInterface(entry)
          val spineEvent = SpineEvent(
            name = event.data.name,
            intValue = event.intValue.toDouble(),
            floatValue = event.floatValue.toDouble(),
            stringValue = event.stringValue ?: ""
          )
          listener.onAnimationEvent?.invoke(trackEntry, spineEvent)
        }
      })
    } else {
      spineAnimationState.clearListeners()
    }
  }

  // MARK: - Internal Methods

  internal fun getSkeleton(): Skeleton {
    return spineSkeleton
  }

  internal fun getAnimationState(): AnimationState {
    return spineAnimationState
  }

  internal fun getSkeletonData(): SkeletonData {
    return spineSkeletonData
  }

  internal fun getAtlas(): TextureAtlas {
    return spineAtlas
  }

  private fun createTrackEntryInterface(trackEntry: TrackEntry): SpineTrackEntry {
    return SpineTrackEntry(
      trackIndex = trackEntry.trackIndex.toDouble(),
      animation = trackEntry.animation?.name ?: "",
      isLooping = trackEntry.loop,
      mixDuration = trackEntry.mixDuration.toDouble(),
      timeScale = trackEntry.timeScale.toDouble()
    )
  }
} 