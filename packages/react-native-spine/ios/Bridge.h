//
//  Bridge.h
//  NitroSpine
//
//  Created by Marc Rousavy on 22.07.24.
//

#pragma once

#import <Foundation/Foundation.h>

// C++ forward declarations
namespace margelo::nitro::spine {
  class SpineFactory;
  class SpineSkeleton;
}

// Swift forward declarations
@class HybridSpineFactory;
@class HybridSpineSkeleton;

// Bridge functions
std::shared_ptr<margelo::nitro::spine::SpineFactory> createHybridSpineFactory();
std::shared_ptr<margelo::nitro::spine::SpineSkeleton> createHybridSpineSkeleton();
