import type { HybridObject } from 'react-native-nitro-modules'

// Spine Event interface
export interface SpineEvent {
  readonly name: string
  readonly intValue: number
  readonly floatValue: number
  readonly stringValue: string
}

// Spine Animation State interface
export interface SpineAnimationState {
  readonly name: string
  readonly isLooping: boolean
  readonly timeScale: number
  readonly duration: number
}

// Spine Track Entry interface
export interface SpineTrackEntry {
  readonly trackIndex: number
  readonly animation: string
  readonly isLooping: boolean
  readonly mixDuration: number
  readonly timeScale: number
}

// Spine Animation State Listener interface
export interface SpineAnimationStateListener {
  onAnimationStart?(trackEntry: SpineTrackEntry): void
  onAnimationInterrupt?(trackEntry: SpineTrackEntry): void
  onAnimationEnd?(trackEntry: SpineTrackEntry): void
  onAnimationComplete?(trackEntry: SpineTrackEntry): void
  onAnimationDispose?(trackEntry: SpineTrackEntry): void
  onAnimationEvent?(trackEntry: SpineTrackEntry, event: SpineEvent): void
}

// Spine Skeleton interface
export interface SpineSkeleton
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  // Constructor parameters
  readonly atlasFilePath: string
  readonly skeletonFilePath: string

  // Properties
  readonly width: number
  readonly height: number
  readonly defaultSkin: string
  readonly skins: string[]
  readonly animations: string[]
  readonly bones: string[]
  readonly slots: string[]

  // Methods
  setAnimation(
    trackIndex: number,
    animationName: string,
    loop: boolean
  ): SpineTrackEntry
  addAnimation(
    trackIndex: number,
    animationName: string,
    loop: boolean,
    delay: number
  ): SpineTrackEntry
  setSkin(skinName: string): void
  setTimeScale(timeScale: number): void
  clearTrack(trackIndex: number): void
  clearTracks(): void
  update(deltaTime: number): void

  // Events
  setAnimationStateListener(listener: SpineAnimationStateListener | null): void
}

// Spine Factory interface
export interface SpineFactory
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  loadFromFiles(atlasFile: string, skeletonFile: string): Promise<SpineSkeleton>
  loadFromBundle(atlasPath: string, skeletonPath: string): SpineSkeleton
  loadFromAssets(atlasAsset: string, skeletonAsset: string): SpineSkeleton
}

// Spine View interface - Nitro View
export interface SpineView
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  // View properties
  skeleton: SpineSkeleton | null
  premultipliedAlpha: boolean
  debug: boolean

  // View methods
  invalidate(): void
}
