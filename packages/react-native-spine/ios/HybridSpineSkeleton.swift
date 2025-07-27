import Foundation
import NitroModules
import Spine

public class HybridSpineSkeleton: HybridSpineSkeletonSpec {
    
    
  
  // MARK: - Spine Objects
  
  private let _atlasFilePath: String
  private let _skeletonFilePath: String
  
  // Note: In Spine 4.2, Skeleton is internal and can only be obtained from SpineUIView
  private weak var spineView: SpineUIView?
  private weak var controller: SpineController?
  private var animationListener: SpineAnimationStateListener?
  
  // MARK: - Initialization
  
  public init(atlasFilePath: String, skeletonFilePath: String) {
    self._atlasFilePath = atlasFilePath
    self._skeletonFilePath = skeletonFilePath
    
    super.init()
  }
    
    override init() {
        _atlasFilePath = ""
        _skeletonFilePath = ""
        super.init()
    }
  
  deinit {
    // Cleanup
    spineView = nil
    controller = nil
  }
  
  // MARK: - Properties (required by Nitro spec and runtime access)
  
  // Constructor properties
  public var atlasFilePath: String {
    return _atlasFilePath
  }
  
  public var skeletonFilePath: String {
    return _skeletonFilePath
  }
  
  // Runtime properties (access via controller after SpineUIView is created)
  public var width: Double {
      guard let controller = controller, let width = controller.skeleton.data?.width else { return 0.0 }
      return Double(width)
  }
  
  public var height: Double {
      guard let controller = controller, let height = controller.skeleton.data?.height else { return 0.0 }
    return Double(height)
  }
  
  public var defaultSkin: String {
    guard let controller = controller else { return "default" }
      return controller.skeleton.data?.defaultSkin?.name ?? "default"
  }
  
  public var skins: [String] {
    guard let controller = controller else { return [] }
    var skinNames: [String] = []
      if let skeletonData = controller.skeleton.data {
          for index in 0..<skeletonData.skins.count {
          skinNames.append(skeletonData.skins[index].name ?? "")
        }
      }
      
    return skinNames
  }
  
  public var animations: [String] {
    guard let controller = controller else { return [] }
    var animationNames: [String] = []
      if let skeletonData = controller.skeleton.data {
          for index in 0..<skeletonData.animations.count {
            animationNames.append(skeletonData.animations[index].name ?? "")
          }
      }
    
    return animationNames
  }
  
  public var bones: [String] {
    guard let controller = controller else { return [] }
    var boneNames: [String] = []
      if let skeletonData = controller.skeleton.data {
          for index in 0..<skeletonData.bones.count {
            boneNames.append(skeletonData.bones[index].name ?? "")
          }
      }
    
    return boneNames
  }
  
  public var slots: [String] {
    guard let controller = controller else { return [] }
    var slotNames: [String] = []
      if let skeletonData = controller.skeleton.data {
          for index in 0..<skeletonData.slots.count {
            slotNames.append(skeletonData.slots[index].name ?? "")
          }
      }
    
    return slotNames
  }
  
  // MARK: - Methods (access via controller)
  
  public func setAnimation(trackIndex: Double, animationName: String, loop: Bool) throws -> SpineTrackEntry {
    guard let controller = controller else {
      throw NSError(domain: "HybridSpineSkeleton", code: 1, userInfo: [NSLocalizedDescriptionKey: "SpineView not attached"])
    }
    
      let trackEntry = controller.animationState.setAnimationByName(trackIndex: Int32(Int(trackIndex)), animationName: animationName, loop: loop)
    return createTrackEntryInterface(from: trackEntry)
  }
    
    public func addAnimation(trackIndex: Double, animationName: String, loop: Bool, delay: Double) throws -> SpineTrackEntry {
        guard let controller = controller else {
          throw NSError(domain: "HybridSpineSkeleton", code: 1, userInfo: [NSLocalizedDescriptionKey: "SpineView not attached"])
        }
        
          let trackEntry = controller.animationState.addAnimationByName(trackIndex: Int32(trackIndex), animationName: animationName, loop: loop, delay: Float(delay))
        return createTrackEntryInterface(from: trackEntry)
    }
  
  public func setSkin(skinName: String) throws {
    guard let controller = controller else {
      throw NSError(domain: "HybridSpineSkeleton", code: 1, userInfo: [NSLocalizedDescriptionKey: "SpineView not attached"])
    }
    
      if let skin = controller.skeleton.data?.findSkin(name: skinName) {
      controller.skeleton.skin = skin
      controller.skeleton.setSlotsToSetupPose()
    } else {
      throw NSError(domain: "HybridSpineSkeleton", code: 1, userInfo: [NSLocalizedDescriptionKey: "Skin '\(skinName)' not found"])
    }
  }
  
  public func setTimeScale(timeScale: Double) throws {
    guard let controller = controller else {
      throw NSError(domain: "HybridSpineSkeleton", code: 1, userInfo: [NSLocalizedDescriptionKey: "SpineView not attached"])
    }
    
    controller.animationState.timeScale = Float(timeScale)
  }
  
  public func clearTrack(trackIndex: Double) throws {
    guard let controller = controller else {
      throw NSError(domain: "HybridSpineSkeleton", code: 1, userInfo: [NSLocalizedDescriptionKey: "SpineView not attached"])
    }
    
      controller.animationState.clearTrack(trackIndex: Int32(Int(trackIndex)))
  }
  
  public func clearTracks() throws {
    guard let controller = controller else {
      throw NSError(domain: "HybridSpineSkeleton", code: 1, userInfo: [NSLocalizedDescriptionKey: "SpineView not attached"])
    }
    
    controller.animationState.clearTracks()
  }
  
  public func update(deltaTime: Double) throws {
    guard let controller = controller else {
      throw NSError(domain: "HybridSpineSkeleton", code: 1, userInfo: [NSLocalizedDescriptionKey: "SpineView not attached"])
    }
    
    controller.animationState.update(delta: Float(deltaTime))
    controller.animationState.apply(skeleton: controller.skeleton)
    controller.skeleton.updateWorldTransform(physics: .init(2))
  }
  
  public func setAnimationStateListener(listener: SpineAnimationStateListener?) throws {
    self.animationListener = listener
    
    guard let controller = controller else {
      // Store listener for later when SpineView is attached
      return
    }
    
    if let listener = listener {
      // Set up Spine iOS animation state listener for Spine 4.2
        
        controller.animationStateWrapper.setStateListener({ [weak self] type, entry, event in
            guard let self = self, let listener = self.animationListener else { return }
            
            let trackEntry = self.createTrackEntryInterface(from: entry)
            
            switch type {
            case .init(1):
              listener.onAnimationStart?(trackEntry)
            case .init(0):
              listener.onAnimationInterrupt?(trackEntry)
            case .init(2):
              listener.onAnimationEnd?(trackEntry)
            case .init(3):
              listener.onAnimationComplete?(trackEntry)
            case .init(4):
              listener.onAnimationDispose?(trackEntry)
            case .init(5):
                if let eventData = event?.data {
                let spineEvent = SpineEvent(
                    name: eventData.name ?? "",
                  intValue: Double(eventData.intValue),
                  floatValue: Double(eventData.floatValue),
                  stringValue: eventData.stringValue ?? ""
                )
                listener.onAnimationEvent?(trackEntry, spineEvent)
              }
            default:
              break
            }
        })
    } else {
        
    }
  }
  
  // MARK: - Internal Methods
  
  internal func getAtlasFilePath() -> String {
    return _atlasFilePath
  }
  
  internal func getSkeletonFilePath() -> String {
    return _skeletonFilePath
  }
  
  internal func attachToSpineView(_ spineView: SpineUIView, controller: SpineController) {
    self.spineView = spineView
    self.controller = controller
    
//    // Setup animation listener if it was set before attachment
//    if animationListener != nil {
//      try? setAnimationStateListener(animationListener)
//    }
  }
  
  internal func detachFromSpineView() {
    self.spineView = nil
    self.controller = nil
  }
  
  private func createTrackEntryInterface(from trackEntry: TrackEntry) -> SpineTrackEntry {
    return SpineTrackEntry(
      trackIndex: Double(trackEntry.trackIndex),
      animation: trackEntry.animation.name ?? "",
      isLooping: trackEntry.loop,
      mixDuration: Double(trackEntry.mixDuration),
      timeScale: Double(trackEntry.timeScale)
    )
  }
} 
