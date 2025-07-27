import { NitroModules } from 'react-native-nitro-modules'
import type { SpineFactory as SpineFactoryInterface } from './specs/Spine.nitro'
import SpineViewComponent from './SpineView'

// Create SpineFactory instance
export const SpineFactory =
  NitroModules.createHybridObject<SpineFactoryInterface>('SpineFactory')

// Export types
export type {
  SpineAnimationState,
  SpineAnimationStateListener,
  SpineEvent,
  SpineFactory as SpineFactoryInterface,
  SpineSkeleton,
  SpineTrackEntry,
  SpineView as SpineViewInterface,
} from './specs/Spine.nitro'

export type { SpineViewProps } from './SpineView'

// Export components
export { SpineViewComponent as SpineView }
export default SpineViewComponent
