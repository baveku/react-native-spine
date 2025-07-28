import { useEffect, useState } from 'react'
import { Button, Image, StyleSheet, View } from 'react-native'

import { HelloWave } from '@/components/HelloWave'
import ParallaxScrollView from '@/components/ParallaxScrollView'
import { ThemedText } from '@/components/ThemedText'
import { ThemedView } from '@/components/ThemedView'

// Import Spine components
import { SpineView } from '@bitu/react-native-spine'
import Animated, { interpolate, useAnimatedStyle, useSharedValue, withRepeat, withTiming } from 'react-native-reanimated'

const SpineAnimationView = Animated.createAnimatedComponent(SpineView)

export default function HomeScreen() {
  const progress = useSharedValue(0)
  const animatedStyle = useAnimatedStyle(() => {
    const scale = interpolate(progress.value, [0, 1], [0.5, 1])
    return {
      transform: [{ scale }],
    }
  })

  useEffect(() => {
    progress.value = withRepeat(withTiming(1, { duration: 1000 }), -1, true)
  }, [])

  const [isLoading, setIsLoading] = useState(false)

  return (
    <ParallaxScrollView
      headerBackgroundColor={{ light: '#A1CEDC', dark: '#1D3D47' }}
      headerImage={
        <Image
          source={require('@/assets/images/partial-react-logo.png')}
          style={styles.reactLogo}
        />
      }
    >
      <ThemedView style={styles.titleContainer}>
        <ThemedText type="title">React Native Spine Demo</ThemedText>
        <HelloWave />
      </ThemedView>

      <ThemedView style={styles.stepContainer}>
        <ThemedText type="subtitle">Spine Animation với Nitro Modules</ThemedText>
        <ThemedText>Đây là demo của React Native Spine sử dụng Nitro Modules để tích hợp Spine Runtime.</ThemedText>
      </ThemedView>

      <ThemedView style={styles.stepContainer}>
        <View style={styles.spineContainer}>
          <SpineAnimationView
            atlasName="Tutor.atlas"
            skeletonName="Tutor.skel"
            style={[styles.spineView, animatedStyle]}
          />
        </View>
      </ThemedView>

      <ThemedView style={styles.stepContainer}>
        <Button
          title={isLoading ? 'Loading...' : 'Load Spine Skeleton'}
          disabled={isLoading}
        />
      </ThemedView>

      <ThemedView style={styles.stepContainer}>
        <ThemedText type="subtitle">Controls</ThemedText>
      </ThemedView>
    </ParallaxScrollView>
  )
}

const styles = StyleSheet.create({
  titleContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  stepContainer: {
    gap: 8,
    marginBottom: 8,
  },
  reactLogo: {
    height: 178,
    width: 290,
    bottom: 0,
    left: 0,
    position: 'absolute',
  },
  spineContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  spineView: {
    width: 200,
    height: 200,
    backgroundColor: 'rgba(0, 0, 0, 0.1)',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#ccc',
  },
  placeholder: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  controlsContainer: {
    gap: 8,
  },
})
