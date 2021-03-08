package com.me.ndkimage.demo.util

import android.content.Context
import android.content.res.AssetManager
import android.graphics.*
import java.io.*
import java.nio.ByteBuffer


object BitmapUtil {

    fun rotateBitmap(origin: Bitmap?, rotate: Float): Bitmap? {
        if (origin == null) {
            return null
        }
        val width = origin.width
        val height = origin.height
        val matrix = Matrix()
        matrix.setRotate(rotate)
        // 围绕原地进行旋转
        val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
        if (newBM == origin) {
            return newBM
        }
        origin.recycle()
        return newBM
    }

    fun rotateYUV420Degree90(data: ByteArray, imageWidth: Int, imageHeight: Int): ByteArray? {
        val yuv = ByteArray(imageWidth * imageHeight * 3 / 2)
        // Rotate the Y luma
        var i = 0
        for (x in 0 until imageWidth) {
            for (y in imageHeight - 1 downTo 0) {
                yuv[i] = data[y * imageWidth + x]
                i++
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1
        var x = imageWidth - 1
        while (x > 0) {
            for (y in 0 until imageHeight / 2) {
                yuv[i] = data[imageWidth * imageHeight + y * imageWidth + x]
                i--
                yuv[i] = data[imageWidth * imageHeight + y * imageWidth + (x - 1)]
                i--
            }
            x = x - 2
        }
        return yuv
    }

    fun getBitmapImageFromYUV(data: ByteArray?, width: Int, height: Int): Bitmap? {
        val yuvimage = YuvImage(data, ImageFormat.NV21, width, height, null)
        val baos = ByteArrayOutputStream()
        yuvimage.compressToJpeg(Rect(0, 0, width, height), 80, baos)
        val jdata: ByteArray = baos.toByteArray()
        val bitmapFatoryOptions = BitmapFactory.Options()
        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565
        return BitmapFactory.decodeByteArray(jdata, 0, jdata.size, bitmapFatoryOptions)
    }

    fun convertColorToByte(color: IntArray?): ByteArray? {
        if (color == null) {
            return null
        }
        val data = ByteArray(color.size * 3)
        for (i in color.indices) {
            data[i * 3] = (color[i] shr 16 and 0xff).toByte()
            data[i * 3 + 1] = (color[i] shr 8 and 0xff).toByte()
            data[i * 3 + 2] = (color[i] and 0xff).toByte()
        }
        return data
    }

    fun dumpFile(fileName: String, data: ByteArray?) {
        val outStream: FileOutputStream
        try {
            outStream = FileOutputStream(fileName)
        } catch (ioe: IOException) {
            throw RuntimeException("Unable to create output file $fileName", ioe)
        }
        try {
            outStream.write(data)
            outStream.close()
        } catch (ioe: IOException) {
            throw RuntimeException("failed writing data to file $fileName", ioe)
        }
    }

    fun I420Tonv21(data: ByteArray, width: Int, height: Int): ByteArray? {
        val ret = ByteArray(data.size)
        val total = width * height
        val bufferY: ByteBuffer = ByteBuffer.wrap(ret, 0, total)
        val bufferV: ByteBuffer = ByteBuffer.wrap(ret, total, total / 4)
        val bufferU: ByteBuffer = ByteBuffer.wrap(ret, total + total / 4, total / 4)
        bufferY.put(data, 0, total)
        var i = 0
        while (i < total / 4) {
            bufferV.put(data[total + i])
            bufferU.put(data[i + total + total / 4])
            i += 1
        }
        return ret
    }

    fun saveBitmap(path: String?, bitmap: Bitmap) {
        //获取文件
        val file = File(path)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (fos != null) {
                    fos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getBitmapFromAssets(context: Context, path: String?): Bitmap? {
        var bitmap: Bitmap? = null
        val am: AssetManager = context.getResources().getAssets()
        try {
            //读取assert 的文图
            val `is`: InputStream = am.open(path!!)
            bitmap = BitmapFactory.decodeStream(`is`)
        } catch (e: Exception) {
        }
        return bitmap
    }
}