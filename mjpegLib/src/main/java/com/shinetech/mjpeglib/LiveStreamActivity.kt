package com.shinetech.mjpeglib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_live_stream.*
import me.lake.librestreaming.core.listener.RESConnectionListener
import me.lake.librestreaming.ws.StreamAVOption

class LiveStreamActivity : AppCompatActivity() {

    private var streamAVOption: StreamAVOption? = null
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_stream)

        init()
    }

    private fun init() {

        //参数配置 start
        streamAVOption = StreamAVOption()
        streamAVOption!!.streamUrl = url
        //参数配置 end

        stream_previewView.init(this, streamAVOption)
        stream_previewView.addStreamStateListener(object : RESConnectionListener {
            override fun onOpenConnectionResult(p0: Int) {
                TODO("Not yet implemented")
            }

            override fun onWriteError(p0: Int) {
                TODO("Not yet implemented")
            }

            override fun onCloseConnectionResult(p0: Int) {
                TODO("Not yet implemented")
            }
        })

        startStreaming()

    }

    private fun startStreaming() {
        if (!stream_previewView.isStreaming) {
            stream_previewView.startStreaming(url)
        }
    }
}