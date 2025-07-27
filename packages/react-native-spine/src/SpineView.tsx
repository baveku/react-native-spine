import React from 'react'
import { Platform, View, type StyleProp, type ViewStyle } from 'react-native'
import { getHostComponent } from 'react-native-nitro-modules'
import ViewConfig from '../nitrogen/generated/shared/json/SpineViewConfig.json'
import type {
  NitroSpineViewMethods,
  NitroSpineViewProps,
} from './specs/Spine.nitro'
export const SpineViewRenderer = getHostComponent<
  NitroSpineViewProps,
  NitroSpineViewMethods
>('SpineView', () => ViewConfig)

export interface SpineViewProps {
  style?: StyleProp<ViewStyle>
  onAnimationStart?: (animation: string) => void
  onAnimationEnd?: (animation: string) => void
  onAnimationComplete?: (animation: string) => void
}

const SpineView: React.FC<SpineViewProps> = ({ style }) => {
  if (Platform.OS === 'ios' || Platform.OS === 'android') {
    // For now, since Nitro Views don't yet have direct React Native view integration,
    // we render a placeholder that represents the native view
    return (
      <View
        style={[
          {
            backgroundColor: 'rgba(200, 200, 200, 0.3)',
            borderWidth: 1,
            borderColor: 'transparent',
            justifyContent: 'center',
            alignItems: 'center',
          },
          style,
        ]}
      >
        <View style={{ alignItems: 'center' }}>
          <SpineViewRenderer
            atlasName="Tutor.atlas"
            skeletonName="Tutor.skel"
            style={{ flex: 1 }}
          />
        </View>
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
