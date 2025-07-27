import Foundation
import NitroModules
import Spine

public class HybridSpineFactory: HybridSpineFactorySpec {
  
  public func loadFromFiles(atlasFile: String, skeletonFile: String) throws -> Promise<any HybridSpineSkeletonSpec> {
    return Promise.resolved(
        withResult: HybridSpineSkeleton(
        atlasFilePath: atlasFile,
        skeletonFilePath: skeletonFile
      )
    )
  }
  
  public func loadFromBundle(atlasPath: String, skeletonPath: String) throws -> any HybridSpineSkeletonSpec {
    // Get bundle file paths
    let atlasResourceName = atlasPath.replacingOccurrences(of: ".atlas", with: "")
    let skeletonResourceName = skeletonPath
      .replacingOccurrences(of: ".skel", with: "")
      .replacingOccurrences(of: ".json", with: "")
    let skeletonExtension = skeletonPath.hasSuffix(".json") ? "json" : "skel"
    
    guard let atlasUrl = Bundle.main.url(
      forResource: atlasResourceName, 
      withExtension: "atlas"
    ),
    let skeletonUrl = Bundle.main.url(
      forResource: skeletonResourceName, 
      withExtension: skeletonExtension
    ) else {
      throw NSError(domain: "HybridSpineFactory", code: 1, userInfo: [
        NSLocalizedDescriptionKey: "Atlas or skeleton file not found in bundle"
      ])
    }
    
    // Return HybridSpineSkeleton with file paths only
    return HybridSpineSkeleton(
      atlasFilePath: atlasUrl.path,
      skeletonFilePath: skeletonUrl.path
    )
  }
  
  public func loadFromAssets(atlasAsset: String, skeletonAsset: String) throws -> any HybridSpineSkeletonSpec {
    // For iOS, assets are typically in the bundle
    return try loadFromBundle(atlasPath: atlasAsset, skeletonPath: skeletonAsset)
  }
}
