package com.shinetech.mjpeglib

import android.content.pm.ActivityInfo
import android.graphics.*
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_ipcam_surface.*
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class IpCamSurfaceActivity : AppCompatActivity(), Runnable {


    private var holder: SurfaceHolder? = null
    private var mThread: Thread? = null
    private var canvas: Canvas? = null
    var videoUrl: URL? = null
    private var url: String? = null
    private var w = 0
    private var h = 0
    var conn: HttpURLConnection? = null
    var bmp: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        setContentView(R.layout.activity_ipcam_surface)

        init()
    }

    private fun init() {

        url = intent.getStringExtra("baseURL")

        w = windowManager.defaultDisplay.width;
        h = windowManager.defaultDisplay.height;

        surface_view.keepScreenOn  = true
        mThread = Thread(this);

        holder = surface_view.holder
        holder!!.addCallback(mSurfaceCallback)
    }

    private fun draw() {
        // TODO Auto-generated method stub
        try {
            var inputstream: InputStream? = null
            videoUrl = URL(url)
            //利用HttpURLConnection对象从网络中获取网页数据
            conn = videoUrl!!.openConnection() as HttpURLConnection
            //设置输入流
            conn!!.doInput = true
            //连接
            conn!!.connect()
            //得到网络返回的输入流
            inputstream = conn!!.inputStream
            //创建出一个bitmap
            bmp = BitmapFactory.decodeStream(inputstream)
            canvas = holder!!.lockCanvas()
            canvas!!.drawColor(Color.WHITE)
            val rectf = RectF(0F, 0F, w.toFloat(), h.toFloat())
            canvas!!.drawBitmap(bmp!!, null, rectf, null)
            holder!!.unlockCanvasAndPost(canvas)

            //关闭HttpURLConnection连接
            conn!!.disconnect()
        } catch (ex: Exception) {
        } finally {
        }
    }

    private val mSurfaceCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceDestroyed(holder: SurfaceHolder) {

        }

        override fun surfaceCreated(holder: SurfaceHolder) {
           mThread!!.start()
        }

        override fun surfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {

        }
    }

    override fun run() {

        while (true) {
            draw()
        }
    }
}


