package com.me.ndkimage.demo

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager


object CommonUtils {

    val SIZE_1 = 640
    val SIZE_2 = 480

    /**
     * dip转为PX
     */
    fun dp2px(context: Context, dipValue: Float): Int {
        val fontScale: Float = context.resources.displayMetrics.density
        return (dipValue * fontScale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dp(context: Context, pxValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 获取屏幕的宽度px
     *
     * @param context 上下文
     * @return 屏幕宽px
     */
    fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics() // 创建了一张白纸
        windowManager.defaultDisplay.getMetrics(outMetrics) // 给白纸设置宽高
        return outMetrics.widthPixels
    }

    /**
     * 获取屏幕的高度px
     *
     * @param context 上下文
     * @return 屏幕高px
     */
    fun getScreenHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics() // 创建了一张白纸
        windowManager.defaultDisplay.getMetrics(outMetrics) // 给白纸设置宽高
        return outMetrics.heightPixels
    }

    /**
     * @brief：设置View的大小
     * @param：View 所要设置的View，width 宽度，height 高度
     */
    fun setViewSize(view: View, width: Int, height: Int) {
        // 推断这个ViewGroup的LayoutParams是获取View尺寸的方法
        val layoutParams: ViewGroup.LayoutParams = view.layoutParams ?: return
        layoutParams.width = width
        layoutParams.height = height
        view.layoutParams = layoutParams
    }
}