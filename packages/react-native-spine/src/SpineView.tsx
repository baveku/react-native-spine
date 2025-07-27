import React, { useEffect, useState } from 'react'
import {
  Platform,
  Text,
  View,
  type StyleProp,
  type ViewStyle,
} from 'react-native'
import { NitroModules } from 'react-native-nitro-modules'
import type {
  SpineAnimationStateListener,
  SpineSkeleton,
  SpineView as SpineViewInterface,
} from './specs/Spine.nitro'

export interface SpineViewProps {
  skeleton?: SpineSkeleton
  premultipliedAlpha?: boolean
  debug?: boolean
  style?: StyleProp<ViewStyle>
  onAnimationStart?: (animation: string) => void
  onAnimationEnd?: (animation: string) => void
  onAnimationComplete?: (animation: string) => void
}

const SpineView: React.FC<SpineViewProps> = ({
  skeleton,
  premultipliedAlpha = true,
  debug = false,
  style,
  onAnimationStart,
  onAnimationEnd,
  onAnimationComplete,
}) => {
  const [spineView, setSpineView] = useState<SpineViewInterface | null>(null)
  const [isViewReady, setIsViewReady] = useState(false)

  // Create Nitro SpineView instance
  useEffect(() => {
    try {
      const view =
        NitroModules.createHybridObject<SpineViewInterface>('SpineView')
      setSpineView(view)
      setIsViewReady(true)
    } catch (error) {
      console.error('Failed to create SpineView:', error)
    }
  }, [])

  // Update view properties when they change
  useEffect(() => {
    if (!spineView) return

    spineView.skeleton = skeleton || null
    spineView.premultipliedAlpha = premultipliedAlpha
    spineView.debug = debug

    // Trigger redraw
    try {
      spineView.invalidate()
    } catch (error) {
      console.error('Failed to invalidate SpineView:', error)
    }
  }, [spineView, skeleton, premultipliedAlpha, debug])

  // Setup animation listeners
  useEffect(() => {
    if (!skeleton || !isViewReady) return

    const listener: SpineAnimationStateListener = {
      onAnimationStart: (trackEntry) => {
        onAnimationStart?.(trackEntry.animation)
      },
      onAnimationEnd: (trackEntry) => {
        onAnimationEnd?.(trackEntry.animation)
      },
      onAnimationComplete: (trackEntry) => {
        onAnimationComplete?.(trackEntry.animation)
      },
    }

    skeleton.setAnimationStateListener(listener)

    return () => {
      skeleton.setAnimationStateListener(null)
    }
  }, [
    skeleton,
    isViewReady,
    onAnimationStart,
    onAnimationEnd,
    onAnimationComplete,
  ])

  if (Platform.OS === 'ios' || Platform.OS === 'android') {
    // For now, since Nitro Views don't yet have direct React Native view integration,
    // we render a placeholder that represents the native view
    return (
      <View
        style={[
          {
            backgroundColor: skeleton
              ? 'rgba(74, 144, 226, 0.3)'
              : 'rgba(200, 200, 200, 0.3)',
            borderWidth: 1,
            borderColor: debug ? 'red' : 'transparent',
            justifyContent: 'center',
            alignItems: 'center',
          },
          style,
        ]}
      >
        {skeleton ? (
          <View style={{ alignItems: 'center' }}>
            <View
              style={{
                padding: 10,
                backgroundColor: 'rgba(255, 255, 255, 0.9)',
                borderRadius: 5,
                marginBottom: 10,
              }}
            >
              <Text
                style={{ fontSize: 16, fontWeight: 'bold', marginBottom: 5 }}
              >
                Spine Skeleton
              </Text>
              <Text style={{ fontSize: 12 }}>
                Animations: {skeleton.animations.join(', ')}
              </Text>
              <Text style={{ fontSize: 12 }}>
                Size: {skeleton.width}x{skeleton.height}
              </Text>
              <Text style={{ fontSize: 12 }}>Skin: {skeleton.defaultSkin}</Text>
            </View>
            {debug && (
              <View
                style={{
                  position: 'absolute',
                  top: 5,
                  right: 5,
                  backgroundColor: 'red',
                  padding: 2,
                }}
              >
                <Text style={{ color: 'white', fontSize: 10 }}>DEBUG</Text>
              </View>
            )}
          </View>
        ) : (
          <View
            style={{
              padding: 20,
              backgroundColor: 'rgba(200, 200, 200, 0.5)',
              borderRadius: 5,
            }}
          >
            <Text>No Skeleton</Text>
          </View>
        )}
      </View>
    )
  }

  // Fallback for unsupported platforms
  return (
    <View
      style={[
        {
          backgroundColor: 'rgba(255, 0, 0, 0.1)',
          justifyContent: 'center',
          alignItems: 'center',
        },
        style,
      ]}
    >
      {/* Placeholder for web or other platforms */}
    </View>
  )
}

export default SpineView
