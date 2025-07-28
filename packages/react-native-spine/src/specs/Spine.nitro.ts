import {
  type HybridView,
  type HybridViewMethods,
  type HybridViewProps,
} from 'react-native-nitro-modules'

export interface NitroSpineViewProps extends HybridViewProps {
  atlasName: string
  skeletonName: string
}

export interface NitroSpineViewMethods extends HybridViewMethods {}
export type SpineView = HybridView<NitroSpineViewProps, NitroSpineViewMethods>
