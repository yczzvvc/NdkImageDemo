package com.me.ndkimage.demo.camera

import android.util.Size

open class BaseCameraProvider {

    val previewSize: Size = Size(720, 1280)
    var ScreenSize: Size? = null
    var TextureViewSize: Size? = null
}