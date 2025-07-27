# React Native Spine

React Native Spine sử dụng Nitro Modules để tích hợp Spine Runtime vào React Native applications.

## Cài đặt

### Cài đặt package

```bash
npm install @bitu/react-native-spine
# hoặc
yarn add @bitu/react-native-spine
```

### Cài đặt dependencies

```bash
npm install react-native-nitro-modules
cd ios && pod install
```

### Cài đặt Spine Runtime

#### iOS

Vì podspec không hỗ trợ git dependencies, bạn cần thêm vào `Podfile` của project:

```ruby
target 'YourApp' do
  # ... existing pods

  # Spine Runtime for iOS
  pod 'spine-ios', :git => 'https://github.com/EsotericSoftware/spine-runtimes.git', :branch => 'spine-4.1'

  # ... other pods
end
```

#### Android

Thêm dependency vào `android/app/build.gradle`:

```gradle
dependencies {
    // ... existing dependencies

    implementation 'com.esotericsoftware.spine:spine-libgdx:4.1.+'

    // ... other dependencies
}
```

## Sử dụng

### Cơ bản

```typescript
import React, { useEffect, useState } from 'react'
import { View } from 'react-native'
import { SpineView, SpineFactory } from '@bitu/react-native-spine'
import type { SpineSkeleton } from '@bitu/react-native-spine'

export default function App() {
  const [skeleton, setSkeleton] = useState<SpineSkeleton | null>(null)

  useEffect(() => {
    const loadSkeleton = async () => {
      try {
        // Load từ bundle
        const loadedSkeleton = SpineFactory.loadFromBundle(
          'spineboy.atlas',
          'spineboy.skel'
        )
        setSkeleton(loadedSkeleton)

        // Set animation
        loadedSkeleton.setAnimation(0, 'walk', true)
      } catch (error) {
        console.error('Failed to load skeleton:', error)
      }
    }

    loadSkeleton()
  }, [])

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <SpineView
        skeleton={skeleton}
        style={{ width: 300, height: 300 }}
        onAnimationStart={(animation) => {
          console.log('Animation started:', animation)
        }}
        onAnimationEnd={(animation) => {
          console.log('Animation ended:', animation)
        }}
      />
    </View>
  )
}
```

### Loading từ Files

```typescript
const skeleton = await SpineFactory.loadFromFiles(
  '/path/to/skeleton.atlas',
  '/path/to/skeleton.skel'
)
```

### Loading từ Assets

```typescript
const skeleton = SpineFactory.loadFromAssets('skeleton.atlas', 'skeleton.skel')
```

### Animation Control

```typescript
// Set animation
const trackEntry = skeleton.setAnimation(0, 'walk', true)

// Add animation
skeleton.addAnimation(0, 'jump', false, 0.5)

// Clear tracks
skeleton.clearTrack(0)
skeleton.clearTracks()

// Set time scale
skeleton.setTimeScale(1.5)

// Set skin
skeleton.setSkin('goblin')
```

### Animation Events

```typescript
skeleton.setAnimationStateListener({
  onAnimationStart: (trackEntry) => {
    console.log('Animation started:', trackEntry.animation)
  },
  onAnimationEnd: (trackEntry) => {
    console.log('Animation ended:', trackEntry.animation)
  },
  onAnimationComplete: (trackEntry) => {
    console.log('Animation completed:', trackEntry.animation)
  },
  onAnimationEvent: (trackEntry, event) => {
    console.log('Animation event:', event)
  },
})
```

## API

### SpineFactory

#### Phương thức

- `loadFromFiles(atlasFile: string, skeletonFile: string): Promise<SpineSkeleton>`
- `loadFromBundle(atlasPath: string, skeletonPath: string): SpineSkeleton`
- `loadFromAssets(atlasAsset: string, skeletonAsset: string): SpineSkeleton`

### SpineSkeleton

#### Properties

- `width: number` - Chiều rộng skeleton
- `height: number` - Chiều cao skeleton
- `defaultSkin: string` - Tên skin mặc định
- `skins: string[]` - Danh sách tên skins
- `animations: string[]` - Danh sách tên animations
- `bones: string[]` - Danh sách tên bones
- `slots: string[]` - Danh sách tên slots

#### Phương thức

- `setAnimation(trackIndex: number, animationName: string, loop: boolean): SpineTrackEntry`
- `addAnimation(trackIndex: number, animationName: string, loop: boolean, delay: number): SpineTrackEntry`
- `setSkin(skinName: string): void`
- `setTimeScale(timeScale: number): void`
- `clearTrack(trackIndex: number): void`
- `clearTracks(): void`
- `update(deltaTime: number): void`
- `setAnimationStateListener(listener: SpineAnimationStateListener | null): void`

### SpineView

#### Props

- `skeleton?: SpineSkeleton` - Spine skeleton để render
- `premultipliedAlpha?: boolean` - Sử dụng premultiplied alpha (mặc định: true)
- `debug?: boolean` - Hiển thị debug info (mặc định: false)
- `style?: StyleProp<ViewStyle>` - Style cho view
- `onAnimationStart?: (animation: string) => void` - Callback khi animation bắt đầu
- `onAnimationEnd?: (animation: string) => void` - Callback khi animation kết thúc
- `onAnimationComplete?: (animation: string) => void` - Callback khi animation hoàn thành

## Requirements

- React Native 0.68+
- iOS 11.0+
- Android API level 21+
- New Architecture enabled

## License

MIT

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
