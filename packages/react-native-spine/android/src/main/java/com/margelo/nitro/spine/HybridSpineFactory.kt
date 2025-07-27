package com.margelo.nitro.spine

import android.content.Context
import android.util.Log
import com.badlogic.gdx.backends.android.AndroidFiles
import com.badlogic.gdx.files.FileHandle
import com.esotericsoftware.spine.*
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise

class HybridSpineFactory : HybridSpineFactorySpec() {
  companion object {
    const val TAG = "HybridSpineFactory"
  }

  override fun loadFromFiles(atlasFile: String, skeletonFile: String): Promise<HybridSpineSkeletonSpec> {
    Log.d(TAG, "loadFromFiles: atlas=$atlasFile, skeleton=$skeletonFile")
    
    return Promise.async {
      try {
        val context = NitroModules.applicationContext?.currentActivity ?: throw Exception("No context available")
        val files = AndroidFiles(context.assets)
        
        // Load atlas
        val atlasFileHandle = files.internal(atlasFile)
        val atlas = TextureAtlas(atlasFileHandle)
        
        // Load skeleton data
        val skeletonFileHandle = files.internal(skeletonFile)
        val skeletonData = if (skeletonFile.endsWith(".json")) {
          val json = SkeletonJson(atlas)
          json.readSkeletonData(skeletonFileHandle)
        } else {
          val binary = SkeletonBinary(atlas)
          binary.readSkeletonData(skeletonFileHandle)
        }
        
        HybridSpineSkeleton(skeletonData, atlas)
      } catch (e: Exception) {
        Log.e(TAG, "Failed to load from files", e)
        throw Exception("Failed to load from files: ${e.message}")
      }
    }
  }

  override fun loadFromBundle(atlasPath: String, skeletonPath: String): HybridSpineSkeletonSpec {
    Log.d(TAG, "loadFromBundle: atlas=$atlasPath, skeleton=$skeletonPath")
    
    try {
      val context = NitroModules.applicationContext?.currentActivity ?: throw Exception("No context available")
      val files = AndroidFiles(context.assets)
      
      // Load atlas from assets
      val atlasFileHandle = files.internal(atlasPath)
      val atlas = TextureAtlas(atlasFileHandle)
      
      // Load skeleton data from assets
      val skeletonFileHandle = files.internal(skeletonPath)
      val skeletonData = if (skeletonPath.endsWith(".json")) {
        val json = SkeletonJson(atlas)
        json.readSkeletonData(skeletonFileHandle)
      } else {
        val binary = SkeletonBinary(atlas)
        binary.readSkeletonData(skeletonFileHandle)
      }
      
      return HybridSpineSkeleton(skeletonData, atlas)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to load from bundle", e)
      throw Exception("Failed to load from bundle: ${e.message}")
    }
  }

  override fun loadFromAssets(atlasAsset: String, skeletonAsset: String): HybridSpineSkeletonSpec {
    Log.d(TAG, "loadFromAssets: atlas=$atlasAsset, skeleton=$skeletonAsset")
    
    // For Android, assets and bundle are the same
    return loadFromBundle(atlasAsset, skeletonAsset)
  }
} 