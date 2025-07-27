#include <jni.h>
#include "NitroSpineOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::spine::initialize(vm);
}
