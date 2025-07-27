# @bitu/react-native-spine

🚧 **Work In Progress** - A React Native bridge for Spine Native iOS & Android using Nitro Modules.

## Overview

`@bitu/react-native-spine` provides a high-performance bridge to integrate [Spine Runtime](https://esotericsoftware.com/spine-runtimes) animations natively in React Native applications. Built with [Nitro Modules](https://github.com/mrousavy/nitro), this library offers:

- ⚡ **High Performance**: Direct JSI bridge without React Native bridge bottlenecks
- 🎯 **Native Spine**: Uses official Spine iOS (4.2) and Android (libGDX) runtimes
- 🏗️ **Modern Architecture**: Built on Nitro Modules with TypeScript support
- 📱 **Cross Platform**: Unified API for iOS and Android
- 🎮 **Full Control**: Animation states, skins, events, and timeline control

## Installation

```bash
npm install @bitu/react-native-spine
# or
yarn add @bitu/react-native-spine
# or
bun add @bitu/react-native-spine
```

## Setup

### iOS Setup

#### Option 1: Expo (Recommended)

Add to your `app.json`:

```json
{
  "expo": {
    "plugins": [
      [
        "expo-build-properties",
        {
          "ios": {
            "useFrameworks": "static",
            "extraPods": [
              {
                "name": "Spine",
                "podspec": "https://raw.githubusercontent.com/EsotericSoftware/spine-runtimes/4.2/Spine.podspec"
              },
              {
                "name": "SpineCppLite",
                "podspec": "https://raw.githubusercontent.com/EsotericSoftware/spine-runtimes/4.2/SpineCppLite.podspec"
              },
              {
                "name": "SpineShadersStructs",
                "podspec": "https://raw.githubusercontent.com/EsotericSoftware/spine-runtimes/4.2/SpineShadersStructs.podspec"
              }
            ]
          }
        }
      ]
    ]
  }
}
```

#### Option 2: React Native CLI

Add to your `ios/Podfile`:

```ruby
target 'YourApp' do
  # ... existing config

  # Spine iOS Runtime 4.2
  pod 'Spine', :podspec => 'https://raw.githubusercontent.com/EsotericSoftware/spine-runtimes/4.2/Spine.podspec'
  pod 'SpineCppLite', :podspec => 'https://raw.githubusercontent.com/EsotericSoftware/spine-runtimes/4.2/SpineCppLite.podspec'
  pod 'SpineShadersStructs', :podspec => 'https://raw.githubusercontent.com/EsotericSoftware/spine-runtimes/4.2/SpineShadersStructs.podspec'

  use_frameworks! :linkage => :static
end
```

Then run:

```bash
cd ios && pod install
```

### Android Setup

Add to your `android/app/build.gradle`:

```gradle
dependencies {
    // Spine libGDX Runtime
    implementation 'com.esotericsoftware.spine:spine-libgdx:4.2.+'
    implementation 'com.badlogicgames.gdx:gdx:1.12.1'
    implementation 'com.badlogicgames.gdx:gdx-backend-android:1.12.1'
}
```

### New Architecture Required

This library requires React Native's **New Architecture** to be enabled:

#### Expo

```json
{
  "expo": {
    "plugins": [
      [
        "expo-build-properties",
        {
          "android": {
            "newArchEnabled": true
          },
          "ios": {
            "newArchEnabled": true
          }
        }
      ]
    ]
  }
}
```

#### React Native CLI

Set in `android/gradle.properties`:

```properties
newArchEnabled=true
```

Set in `ios/Podfile`:

```ruby
:fabric_enabled => true
```

## Usage

### Basic Example

```typescript
import React, { useEffect, useState } from 'react'
import { View, Button } from 'react-native'
import { SpineView, SpineFactory, type SpineSkeleton } from '@bitu/react-native-spine'

export default function SpineExample() {
  const [skeleton, setSkeleton] = useState<SpineSkeleton | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    loadSkeleton()
  }, [])

  const loadSkeleton = async () => {
    try {
      // Load skeleton from app bundle
      const loadedSkeleton = SpineFactory.loadFromBundle('spineboy.atlas', 'spineboy.skel')
      setSkeleton(loadedSkeleton)
      setIsLoading(false)
    } catch (error) {
      console.error('Failed to load skeleton:', error)
    }
  }

  const playAnimation = (animationName: string) => {
    if (skeleton) {
      skeleton.setAnimation(0, animationName, true)
    }
  }

  if (isLoading) {
    return <View><Text>Loading...</Text></View>
  }

  return (
    <View style={{ flex: 1 }}>
      <SpineView
        skeleton={skeleton}
        style={{ width: 300, height: 300 }}
        debug={false}
        premultipliedAlpha={true}
        onAnimationStart={(entry) => console.log('Animation started:', entry.animation)}
        onAnimationComplete={(entry) => console.log('Animation completed:', entry.animation)}
      />

      <View style={{ flexDirection: 'row', justifyContent: 'space-around', padding: 20 }}>
        <Button title="Idle" onPress={() => playAnimation('idle')} />
        <Button title="Walk" onPress={() => playAnimation('walk')} />
        <Button title="Run" onPress={() => playAnimation('run')} />
      </View>
    </View>
  )
}
```

### Loading Skeletons

```typescript
import { SpineFactory } from '@bitu/react-native-spine'

// Load from app bundle (iOS/Android assets)
const skeleton = SpineFactory.loadFromBundle(
  'character.atlas',
  'character.skel'
)

// Load from file paths (absolute paths)
const skeleton = await SpineFactory.loadFromFiles(
  '/path/to/atlas.atlas',
  '/path/to/skeleton.skel'
)

// Load from assets (alias for loadFromBundle)
const skeleton = SpineFactory.loadFromAssets(
  'character.atlas',
  'character.json'
)
```

### Animation Control

```typescript
// Set animation on track 0, looping
skeleton.setAnimation(0, 'walk', true)

// Queue animation after current one
skeleton.addAnimation(0, 'jump', false, 2.0) // 2 second delay

// Change time scale
skeleton.setTimeScale(1.5) // 1.5x speed

// Clear specific track
skeleton.clearTrack(0)

// Clear all tracks
skeleton.clearTracks()

// Manual update (usually handled automatically)
skeleton.update(deltaTime)
```

### Skins

```typescript
// Get available skins
console.log(skeleton.skins) // ['default', 'goblin', 'girl']

// Change skin
skeleton.setSkin('goblin')
```

### Animation Events

```typescript
const skeleton = SpineFactory.loadFromBundle(
  'character.atlas',
  'character.skel'
)

skeleton.setAnimationStateListener({
  onAnimationStart: (entry) => {
    console.log(
      `Animation ${entry.animation} started on track ${entry.trackIndex}`
    )
  },
  onAnimationComplete: (entry) => {
    console.log(`Animation ${entry.animation} completed`)
  },
  onAnimationEnd: (entry) => {
    console.log(`Animation ${entry.animation} ended`)
  },
  onAnimationEvent: (entry, event) => {
    console.log(`Custom event ${event.name}: ${event.stringValue}`)
  },
})
```

## API Reference

### SpineView Props

| Prop                  | Type                               | Default | Description                         |
| --------------------- | ---------------------------------- | ------- | ----------------------------------- |
| `skeleton`            | `SpineSkeleton \| null`            | `null`  | The skeleton to render              |
| `premultipliedAlpha`  | `boolean`                          | `true`  | Enable premultiplied alpha blending |
| `debug`               | `boolean`                          | `false` | Show debug bones and bounding boxes |
| `style`               | `ViewStyle`                        | -       | Standard React Native view styling  |
| `onAnimationStart`    | `(entry: SpineTrackEntry) => void` | -       | Called when animation starts        |
| `onAnimationEnd`      | `(entry: SpineTrackEntry) => void` | -       | Called when animation ends          |
| `onAnimationComplete` | `(entry: SpineTrackEntry) => void` | -       | Called when animation completes     |

### SpineFactory Methods

| Method           | Parameters                                  | Returns                  | Description          |
| ---------------- | ------------------------------------------- | ------------------------ | -------------------- |
| `loadFromBundle` | `atlasPath: string, skeletonPath: string`   | `SpineSkeleton`          | Load from app bundle |
| `loadFromFiles`  | `atlasFile: string, skeletonFile: string`   | `Promise<SpineSkeleton>` | Load from file paths |
| `loadFromAssets` | `atlasAsset: string, skeletonAsset: string` | `SpineSkeleton`          | Load from assets     |

### SpineSkeleton Properties

| Property      | Type       | Description               |
| ------------- | ---------- | ------------------------- |
| `width`       | `number`   | Skeleton width            |
| `height`      | `number`   | Skeleton height           |
| `defaultSkin` | `string`   | Default skin name         |
| `skins`       | `string[]` | Available skin names      |
| `animations`  | `string[]` | Available animation names |
| `bones`       | `string[]` | Bone names                |
| `slots`       | `string[]` | Slot names                |

## Requirements

- React Native 0.74+
- New Architecture enabled
- iOS 13.0+
- Android API 21+
- Spine Runtime 4.2

## Limitations & Known Issues

- 🚧 **WIP**: This library is under active development
- iOS requires static frameworks (`use_frameworks! :linkage => :static`)
- New Architecture is mandatory (no bridge mode support)
- Large skeleton files may impact app bundle size
- Some advanced Spine features may not be implemented yet

## Contributing

This project is in early development. Contributions are welcome!

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

### Development Setup

```bash
# Clone repo
git clone https://github.com/your-org/react-native-spine.git
cd react-native-spine

# Install dependencies
bun install

# Build TypeScript
bun run typescript

# Generate Nitro specs
bun run specs

# Run example app
cd apps/mobile-example
bun install
bun run ios # or bun run android
```

## License

MIT License - see LICENSE file for details.

## Credits

- Built with [Nitro Modules](https://github.com/mrousavy/nitro) by [@mrousavy](https://github.com/mrousavy)
- Uses [Spine Runtime](https://esotericsoftware.com/spine-runtimes) by Esoteric Software
- Inspired by the React Native community's need for high-performance animations

---

**⚠️ Note**: This library is currently in development. APIs may change before stable release.
