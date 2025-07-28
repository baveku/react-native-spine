require "json"
require "./nitro_pod_utils"
package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "NitroSpine"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported, :visionos => 1.0 }
  s.source       = { :git => "https://github.com/mrousavy/nitro.git", :tag => "#{s.version}" }

  s.source_files = [
    # Implementation (Swift)
    "ios/**/*.{swift}",
    # Autolinking/Registration (Objective-C++)
    "ios/**/*.{m,mm}",
    # Implementation (C++ objects)
    "cpp/**/*.{hpp,cpp}",
  ]

	xcconfig = {
    # Use C++ 20
    "CLANG_CXX_LANGUAGE_STANDARD" => "c++20",
    # Enables C++ <-> Swift interop (by default it's only C)
    "SWIFT_OBJC_INTEROP_MODE" => "objcxx",
    # Enables stricter modular headers
    "DEFINES_MODULE" => "YES",
		'HEADER_SEARCH_PATHS' => '$(inherited) "$(PODS_ROOT)/SpineCppLite/spine-cpp/spine-cpp/include" "$(PODS_ROOT)/SpineCppLite/spine-cpp/spine-cpp-lite"',
    'MTL_HEADER_SEARCH_PATHS' => '"$(PODS_ROOT)/SpineShadersStructs"'
  }

  if has_react_native()
    react_native_version = get_react_native_version()
    if (react_native_version < 80)
      # C++ compiler flags, for folly when building as static framework:
      xcconfig["GCC_PREPROCESSOR_DEFINITIONS"] = "$(inherited) FOLLY_NO_CONFIG FOLLY_CFG_NO_COROUTINES"
    end
  end

  s.pod_target_xcconfig = xcconfig


  load 'nitrogen/generated/ios/NitroSpine+autolinking.rb'
  add_nitrogen_files(s)

  s.dependency 'React-jsi'
  s.dependency 'React-callinvoker'
  s.dependency 'Spine'
  s.dependency 'SpineCppLite'
  s.dependency 'SpineShadersStructs'
  install_modules_dependencies(s)
end
