package com.me.ndkimage.demo;

import android.hardware.HardwareBuffer;
import android.view.Surface;

public class ImageReaderBridge {

    static {
        System.loadLibrary("nativeimage");
    }

    public interface OnSurfaceUpdateListener {
        void onSurfaceUpdate(HardwareBuffer hardwareBuffer);
    }

    private static OnSurfaceUpdateListener mListener;

    public static void setOnSurfaceUpdateListener(OnSurfaceUpdateListener listener) {
        mListener = listener;
    }

    public static void removeOnSurfaceUpdateListener() {
        mListener = null;
    }

    public static void onSurfaceUpdate(HardwareBuffer hardwareBuffer) {
        if (mListener != null) {
            mListener.onSurfaceUpdate(hardwareBuffer);
        }
    }
    public static native void nativeInitImageReader();
    public static native Surface nativeGetSurfaceFromImageReader();
}
