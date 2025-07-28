import {
  getHostComponent,
  type HybridView,
  type HybridViewMethods,
  type HybridViewProps,
} from 'react-native-nitro-modules'
import ViewConfig from '../../nitrogen/generated/shared/json/SpineViewConfig.json'

export interface NitroSpineViewProps extends HybridViewProps {
  atlasName: string
  skeletonName: string
}

export interface NitroSpineViewMethods extends HybridViewMethods {}
export type SpineView = HybridView<NitroSpineViewProps, NitroSpineViewMethods>
export const SpineViewRenderer = getHostComponent<
  NitroSpineViewProps,
  NitroSpineViewMethods
>('SpineView', () => ViewConfig)
