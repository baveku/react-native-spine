import { useState } from 'react'
import { Alert, Button, Image, StyleSheet, View } from 'react-native'

import { HelloWave } from '@/components/HelloWave'
import ParallaxScrollView from '@/components/ParallaxScrollView'
import { ThemedText } from '@/components/ThemedText'
import { ThemedView } from '@/components/ThemedView'

// Import Spine components
import { SpineView } from '@bitu/react-native-spine'

export default function HomeScreen() {
  const [isLoading, setIsLoading] = useState(false)
  const [currentAnimation, setCurrentAnimation] = useState<string>('')

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
          <SpineView style={styles.spineView} />
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
    width: 300,
    height: 300,
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
