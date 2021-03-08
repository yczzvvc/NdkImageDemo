package com.me.ndkimage.demo.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.hardware.HardwareBuffer
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.TextureView
import com.me.ndkimage.demo.ImageReaderBridge
import com.me.ndkimage.demo.util.PermissionUtil


class Camera2Provider(var mContext: Context?) : BaseCameraProvider() {

    private var mCameraId: String? = null
    private var mCameraHandler: Handler? = null
    private var mCameraDevice: CameraDevice? = null
    private var mTextureView: TextureView? = null
    private var mPreviewBuilder: CaptureRequest.Builder? = null
    private var mImageReader: ImageReader? = null
    private var mHandlerThread: HandlerThread? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null

    private var surface: Surface? = null

    init {
        mHandlerThread = HandlerThread("camera")
        mHandlerThread?.start()
        mCameraHandler = Handler(mHandlerThread!!.looper)
    }

    private val mStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            val texture = mTextureView?.surfaceTexture
            texture?.setDefaultBufferSize(previewSize.width, previewSize.height)
            val previewSurface = Surface(texture)
            try {
                surface = ImageReaderBridge.nativeGetSurfaceFromImageReader()
                Log.i("nativeImage", "surface is valid is ${surface?.isValid}")
                mPreviewBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                mPreviewBuilder?.addTarget(previewSurface)
                mCameraDevice?.createCaptureSession(
                    arrayListOf(previewSurface, mImageReader!!.surface, surface),
//                    arrayListOf(previewSurface, mImageReader!!.surface),
                    mSessionStateCallback,
                    mCameraHandler
                )
            } catch (e: CameraAccessException) {
                e.printStackTrace();
            }
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
        }

    }

    private val mSessionStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            try {
                mPreviewBuilder?.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                )
                val request = mPreviewBuilder?.build()

                if (request != null) {
                    mCameraCaptureSession = session
                    session.setRepeatingRequest(request, null, mCameraHandler)
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            print("CameraDevice onConfigureFailed")
        }
    }

    private val mOnImageAvailableListener = object : ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(reader: ImageReader?) {
            val image = reader?.acquireLatestImage() ?: return

            val hardwareBuffer = HardwareBuffer.create(
                600,
                400,
                PixelFormat.RGBA_8888,
                1,
                HardwareBuffer.USAGE_GPU_DATA_BUFFER
            )

//            val w = image.width
//            val h = image.height
//            val i420Size = w * h * 3 / 2
//            val picel1 = ImageFormat.getBitsPerPixel(ImageFormat.NV21)
//            val picel2 = ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888)
//
//            val planes = image.planes
//            val remaing0 = planes[0].buffer.remaining()
//            val remaing1 = planes[1].buffer.remaining()
//            val remaing2 = planes[2].buffer.remaining()
//            val yRawSrcBytes = ByteArray(remaing0)
//            val uRawSrcBytes = ByteArray(remaing1)
//            val vRawSrcBytes = ByteArray(remaing2)
//
//            val pixelStride = planes[2].pixelStride
//            val rowStride = planes[2].rowStride
//
//            val nv21 = ByteArray(i420Size)
//
//            planes[0].buffer.get(yRawSrcBytes)
//            planes[1].buffer.get(uRawSrcBytes)
//            planes[2].buffer.get(vRawSrcBytes)
//
//            if (pixelStride == w) {
//                System.arraycopy(yRawSrcBytes, 0, nv21, 0, rowStride * h);
//                System.arraycopy(vRawSrcBytes, 0, nv21, rowStride * h, rowStride * h / 2 - 1);
//            } else {
//                val ySrcBytes = ByteArray(w * h)
//                val uSrcBytes = ByteArray(w * h / 2 - 1)
//                val vSrcBytes = ByteArray(w * h / 2 - 1)
//                for (row in 0 until h) {
//                    //源数组每隔 rowOffest 个bytes 拷贝 w 个bytes到目标数组
//                    System.arraycopy(yRawSrcBytes, rowStride * row, ySrcBytes, w * row, w)
//
//                    //y执行两次，uv执行一次
//                    if (row % 2 == 0) {
//                        //最后一行需要减一
//                        if (row == h - 2) {
//                            System.arraycopy(
//                                vRawSrcBytes,
//                                rowStride * row / 2,
//                                vSrcBytes,
//                                w * row / 2,
//                                w - 1
//                            )
//                        } else {
//                            System.arraycopy(
//                                vRawSrcBytes,
//                                rowStride * row / 2,
//                                vSrcBytes,
//                                w * row / 2,
//                                w
//                            )
//                        }
//                    }
//                }
//                System.arraycopy(ySrcBytes, 0, nv21, 0, w * h)
//                System.arraycopy(vSrcBytes, 0, nv21, w * h, w * h / 2 - 1)
//            }
//
//            val bm = BitmapUtil.getBitmapImageFromYUV(nv21, w, h)
//            val m = Matrix()
//            m.setRotate(90F, bm!!.width.toFloat() / 2, bm.height.toFloat() / 2)
//            val bitmap = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, m, true)

            image.close()
        }
    }

    fun initTexture(texture: TextureView?) {
        mTextureView = texture
        mTextureView?.surfaceTextureListener = object : TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                openCamera(width, height);
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                releaseCamera()
                releaseThread()
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }
        }
    }

    private fun openCamera(width: Int, height: Int) {
        val cameraManager = mContext?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in cameraManager.cameraIdList) {
                //描述相机设备的属性类
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                //获取是前置还是后置摄像头
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                //使用后置摄像头
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    if (map != null) {
                        val sizeMap = map.getOutputSizes(SurfaceTexture::class.java)
                        val sizes = StringBuilder()
                        for (size in sizeMap) {
                            sizes.append(size.width).append("|").append(size.height).append("      ")
                        }
                        Log.i("Camera2Provider", "size->" + sizes.toString())
                        Log.i("Camera2Provider", "preview->" + previewSize.toString())
                        mCameraId = cameraId;
                    }
                }
            }
            mImageReader = ImageReader.newInstance(
                previewSize.width,
                previewSize.height,
                ImageFormat.YUV_420_888,
                2
            )
            mImageReader?.setOnImageAvailableListener(mOnImageAvailableListener, mCameraHandler)

            ImageReaderBridge.nativeInitImageReader()
            ImageReaderBridge.setOnSurfaceUpdateListener { hardwareBuffer -> print(hardwareBuffer.hashCode()) }

            val params = arrayOf<String>(Manifest.permission.CAMERA)
            if (mContext!= null && !PermissionUtil.checkPermission(mContext!!, params)) {
                PermissionUtil.requestPermission(mContext as Activity, "", 1, params)
            }

            if (mCameraId != null) cameraManager.openCamera(
                mCameraId!!,
                mStateCallback,
                mCameraHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun capture() {
        try {

            // 创建一个拍照的CaptureRequest.Builder
            val captureBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
//            captureBuilder.addTarget(mImageReader!!.surface)
            captureBuilder.addTarget(surface!!)

            //先停止以前的预览状态
            mCameraCaptureSession?.stopRepeating()
            mCameraCaptureSession?.abortCaptures()

            //执行拍照动作
            mCameraCaptureSession?.capture(captureBuilder.build(), object : CameraCaptureSession.CaptureCallback() {

            }, mCameraHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun releaseCamera() {
        mCameraCaptureSession?.close()
        mCameraCaptureSession = null

        mCameraDevice?.close()
        mCameraDevice = null

        mImageReader?.close()
        mImageReader = null
    }

    fun releaseThread() {
        mHandlerThread?.quitSafely()
    }
}
