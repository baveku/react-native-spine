import UIKit
import Foundation
import NitroModules
import Spine

public class HybridSpineView: HybridSpineViewSpec {
    weak var spineView: SpineUIView?
    let mainView = UIView()
    
    public var view: UIView {
        get {
            return mainView
        }
    }
    
    public var atlasName: String = "" {
        didSet {
         onUpdate()
        }
    }
    
    public var skeletonName: String = "" {
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
            
        }))
        self.spineView = spineView
        mainView.addSubview(spineView)
    }
    
    public func beforeUpdate() {
        
    }
    
    public func afterUpdate() {
        
    }
    
}
