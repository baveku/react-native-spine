import UIKit
import Foundation
import NitroModules
import Spine

class SpineViewWrapper: UIView {
    override var frame: CGRect {
        didSet {
            subviews.forEach { $0.frame = bounds }
        }
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        subviews.forEach({$0.frame = bounds})
    }
}

class HybridSpineView: HybridSpineViewSpec {
    weak var spineView: SpineUIView?
    let mainView = SpineViewWrapper()
    
    var view: UIView {
        mainView
    }
    
    var atlasName: String = "" {
        didSet {
            onUpdate()
        }
    }
    
    var skeletonName: String = "" {
        didSet {
            onUpdate()
        }
    }
    
    func onUpdate() {
        if let spineView {
            spineView.removeFromSuperview()
        }
        
        if atlasName.isEmpty || skeletonName.isEmpty {
            return
        }
        
        let resource = SpineViewSource.bundle(atlasFileName: atlasName, skeletonFileName: skeletonName, bundle: .main)
        let spineView = SpineUIView.init(from: resource, controller: SpineController(onInitialized: { controller in
            print(controller.skeletonData.animations.map({$0.name}))
            controller.animationState.setAnimationByName(trackIndex: 0, animationName: "Idle_Listening", loop: true)
        }))
        self.spineView = spineView
        mainView.addSubview(spineView)
    }
    
    func beforeUpdate() {
        
    }
    
    func afterUpdate() {
        
    }
    
}
