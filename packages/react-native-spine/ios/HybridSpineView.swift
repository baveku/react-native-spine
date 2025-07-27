import Foundation
import NitroModules
import Spine

public class HybridSpineView: HybridSpineViewSpec {
  
  // MARK: - SpineView Properties
  
  public var skeleton: (any HybridSpineSkeletonSpec)? {
    didSet {
      DispatchQueue.main.async { [weak self] in
        self?.updateSkeleton()
      }
    }
  }
  
  public var premultipliedAlpha: Bool = true {
    didSet {
      DispatchQueue.main.async { [weak self] in
        self?.updateRenderingSettings()
      }
    }
  }
  
  public var debug: Bool = false {
    didSet {
      DispatchQueue.main.async { [weak self] in
        self?.updateDebugSettings()
      }
    }
  }
  
  // MARK: - Native View
  
  private var spineView: SpineUIView?
  private var displayLink: CADisplayLink?
  let controller = SpineController()
  
  public override init() {
    super.init()
    setupDisplayLink()
  }
  
  private func setupDisplayLink() {
    // Setup display link but don't start it yet
    displayLink = CADisplayLink(target: self, selector: #selector(update))
  }
  
  private func createSpineView(with atlasFile: String, skeletonFile: String) {
    do {
      // Convert string paths to URLs
      guard let atlasURL = URL(string: atlasFile),
            let skeletonURL = URL(string: skeletonFile) else {
        print("Failed to create URLs from file paths")
        return
      }
      
      // Create SpineViewSource from files - this is the correct Spine 4.2 way
      let source = SpineViewSource.file(atlasFile: atlasURL, skeletonFile: skeletonURL)
      
      // Create SpineUIView with SpineViewSource
        spineView = SpineUIView(from: source, controller: controller)
      spineView?.backgroundColor = UIColor.clear
      
      // Apply current settings
      updateRenderingSettings()
      updateDebugSettings()
      
      // Set skeleton to setup pose
      controller.skeleton.setToSetupPose()
      
      // Start animation loop
      startAnimationLoop()
      
    } catch {
      print("Failed to create SpineUIView with SpineViewSource: \(error)")
    }
  }
  
  private func startAnimationLoop() {
    displayLink?.add(to: .main, forMode: .common)
  }
  
  private func stopAnimationLoop() {
    displayLink?.remove(from: .main, forMode: .common)
  }
  
  @objc private func update() {
    guard let spineView = spineView else { return }
    
    // Update animation state (typically 60fps)
    let deltaTime = displayLink?.duration ?? 1.0/60.0
    
    // Access controller through SpineUIView
    controller.animationState.update(delta: Float(deltaTime))
    controller.animationState.apply(skeleton: controller.skeleton)
      controller.skeleton.updateWorldTransform(physics: .init(2))
    
    spineView.setNeedsLayout()
  }
  
  // MARK: - SpineView Methods
  
  public func invalidate() throws {
    DispatchQueue.main.async { [weak self] in
      self?.spineView?.setNeedsLayout()
    }
  }
  
  private func updateSkeleton() {
    // Detach from previous skeleton
    if let previousSkeleton = skeleton as? HybridSpineSkeleton {
      previousSkeleton.detachFromSpineView()
    }
    
    // Stop current animation loop
    stopAnimationLoop()
    
    // Remove current SpineUIView
    spineView = nil
    
    guard let hybridSkeleton = skeleton as? HybridSpineSkeleton else {
      return
    }
    
    // Get file paths from HybridSpineSkeleton
    let atlasFilePath = hybridSkeleton.getAtlasFilePath()
    let skeletonFilePath = hybridSkeleton.getSkeletonFilePath()
    
    // Create new SpineUIView with file paths
    createSpineView(with: atlasFilePath, skeletonFile: skeletonFilePath)
    
    // Attach HybridSpineSkeleton to SpineUIView
    if let spineView = spineView {
      hybridSkeleton.attachToSpineView(spineView, controller: controller)
    }
  }
  
  private func updateRenderingSettings() {
    guard let spineView = spineView else { return }
  }
  
  private func updateDebugSettings() {
    guard let spineView = spineView else { return }
  }
  
  // MARK: - View Lifecycle
  
  deinit {
    stopAnimationLoop()
    displayLink?.invalidate()
    
    // Detach skeleton
    if let hybridSkeleton = skeleton as? HybridSpineSkeleton {
      hybridSkeleton.detachFromSpineView()
    }
    
    spineView = nil
  }
  
  // MARK: - Internal Access
  
  internal func getSpineView() -> SpineUIView? {
    return spineView
  }
  
  internal func getFrame() -> CGRect {
    return spineView?.frame ?? .zero
  }
  
  internal func setFrame(_ frame: CGRect) {
    DispatchQueue.main.async { [weak self] in
      self?.spineView?.frame = frame
    }
  }
} 
