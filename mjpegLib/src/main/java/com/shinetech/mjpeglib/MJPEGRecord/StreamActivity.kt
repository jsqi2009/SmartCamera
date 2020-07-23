package com.shinetech.mjpeglib.MJPEGRecord

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.shinetech.mjpeglib.R
import kotlinx.android.synthetic.main.activity_stream.*
import java.io.IOException


class StreamActivity : AppCompatActivity() {

    private val TAG = "StreamActivity"
    private val REQUEST_WRITE_PERMISSIONS = 1
    private var url: String? = null
    private val mImageView: ImageView? = null
    private var mMjpegWriter: MjpegWriter? = null
    private val running = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream)

        mMjpegWriter = MjpegWriter()
        url = intent.getStringExtra("baseURL")

        startSession()
    }

    private fun startSession() {
        MjpegInputStream.read(
            url, this
        ) { stream ->
            val mjpegThread = Thread(Runnable {
                while (running) {
                    try {
                        val mjpegContainer = stream.readMjpegFrame()
                        if (mMjpegWriter != null) mMjpegWriter!!.saveByteArrayToFile(
                            mjpegContainer.data
                        )

                        // this is mainly for testing if we are really capturing anything
                        runOnUiThread {
                            val bitmap = mjpegContainer.bitmap
                            outputImageView!!.setImageBitmap(bitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            })
            mjpegThread.start()
        }

        // Test recording videos:

        val t = Thread(Runnable {
            try {
                Thread.sleep(1000)
                runOnUiThread { mMjpegWriter!!.recordMjpeg() }
                Thread.sleep(8000)
                runOnUiThread { mMjpegWriter!!.stopRecording() }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        })
        t.start()
    }

}