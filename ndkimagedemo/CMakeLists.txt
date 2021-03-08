cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             nativeimage

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
        src/main/jni/native_image_lib.cpp
        src/main/jni/jni_helper.h
        src/main/jni/jni_helper.cpp
        )

#add_library(
#        jni_helper
#        SHARED
#        src/main/jni/jni_helper.h)

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

target_link_libraries( # Specifies the target library.
        nativeimage
        android
        mediandk

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )
