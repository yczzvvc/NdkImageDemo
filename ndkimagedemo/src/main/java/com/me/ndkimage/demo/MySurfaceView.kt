package com.me.ndkimage.demo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Region
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceView

class MySurfaceView(context: Context, attrs: AttributeSet?, defStyle: Int) : SurfaceView(
    context,
    attrs,
    defStyle
) {

//    private var paint: Paint? = null
    private var widthSize = 0
//    private var camera: Camera? = null
    private var viewHeight = 0

    init {
        initView()
    }

    constructor (context: Context) : this(context, null, 0)

    constructor (context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private fun initView() {
        setFocusable(true)
        setFocusableInTouchMode(true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        widthSize = MeasureSpec.getSize(widthMeasureSpec) //父类的宽度，

        //height = MeasureSpec.getSize(heightMeasureSpec);//父类的高度，
        //height = MeasureSpec.getSize(heightMeasureSpec);//父类的高度，
        val screenWidth: Int = CommonUtils.getScreenWidth(context) //屏幕的宽度

        val screenHeight: Int = CommonUtils.getScreenHeight(context) //屏幕的高度

        viewHeight = screenHeight / 2 + 20 //适配HUAWEIP20的高度

        //height=1000;
        //可以理解为红色的背景盖住了大部分的区域，我们只能看到圆框里面的，如果还是按照原来的比例绘制surfaceview
        //需要把手机拿的很远才可以显示出整张脸，故而我用了一个比较取巧的办法就是，按比例缩小，试验了很多数后，感觉0.55
        //是最合适的比例
        //height=1000;
        //可以理解为红色的背景盖住了大部分的区域，我们只能看到圆框里面的，如果还是按照原来的比例绘制surfaceview
        //需要把手机拿的很远才可以显示出整张脸，故而我用了一个比较取巧的办法就是，按比例缩小，试验了很多数后，感觉0.55
        //是最合适的比例
        val screenWidth1 = 0.55 * screenWidth //即屏幕的宽度*0.55，绘制的surfaceview的宽度

        val screenHeight1 = 0.55 * screenHeight //即屏幕的高度*0.55，绘制的surfaceView的高度

        Log.e("onMeasure", "widthSize=$widthSize")
        Log.e(
            "onMeasure",
            "draw: widthMeasureSpec = $screenWidth  heightMeasureSpec = $screenHeight"
        )
        //绘制的输入参数必须是整数型，做浮点型运算后为float型数据，故需要做取整操作
        //绘制的输入参数必须是整数型，做浮点型运算后为float型数据，故需要做取整操作
        setMeasuredDimension(screenWidth1.toInt(), screenHeight1.toInt())
    }

    //绘制一个圆形的框，并设置圆框的坐标和半径大小
    override fun draw(canvas: Canvas) {
        Log.e("onDraw", "draw: test")
        val path = Path()
        //path.addCircle(widthSize / 2, height / 2, height / 2, Path.Direction.CCW);
        path.addCircle(widthSize.toFloat() / 2, viewHeight.toFloat() / 2, widthSize.toFloat() / 2, Path.Direction.CCW)
        canvas.clipPath(path, Region.Op.REPLACE)
        super.draw(canvas)
    }

    override fun onDraw(canvas: Canvas?) {
        Log.e("onDraw", "onDraw")
        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        var w = w
        var h = h
        val screenWidth: Int = CommonUtils.getScreenWidth(context)
        val screenHeight: Int = CommonUtils.getScreenHeight(context)
        Log.d("screenWidth", Integer.toString(screenWidth))
        Log.d("screenHeight", Integer.toString(screenHeight))
        w = screenWidth
        h = screenHeight
        super.onSizeChanged(w, h, oldw, oldh)
    }
}