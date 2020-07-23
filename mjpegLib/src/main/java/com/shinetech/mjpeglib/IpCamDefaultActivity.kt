package com.shinetech.mjpeglib

import android.content.res.Configuration
import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.niqdev.mjpeg.*
import com.shinetech.mjpeglib.utils.FileUtil
import kotlinx.android.synthetic.main.activity_ipcam_default.*
import java.io.File


class IpCamDefaultActivity : AppCompatActivity(), View.OnClickListener,
    OnFrameCapturedListener {


    private var mjpegView: MjpegSurfaceView? = null
    private val tvTakePhoto: TextView? = null
    private var imageView: ImageView? = null
    private var lastPreview: Bitmap? = null
    private val player: MediaPlayer? = null
    private var url: String? = null
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mIsRecording = false
    private var mIsSufaceCreated = false

    private var filePath: String? = null

    private var mRecorder: MediaRecorder? = null
    private val mHandler: Handler = Handler()
    private val mMjpeg: MjpegView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ipcam_default)

        init()
    }

    private fun init() {
        url = intent.getStringExtra("baseURL")
        filePath = intent.getStringExtra("filePath")

        mjpegView = findViewById(R.id.mjpegViewDefault)
        imageView = findViewById(R.id.imageView)
        //        tvTakePhoto = findViewById(R.id.tv_take_photo);
        mjpegView!!.setOnFrameCapturedListener(this)

        mSurfaceHolder = mjpegView!!.surfaceView.holder
        mSurfaceHolder!!.addCallback(mSurfaceCallback)
        mSurfaceHolder!!.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        imageView!!.setOnClickListener(this)
    }

    private fun calculateDisplayMode(): DisplayMode {
        val orientation = resources.configuration.orientation
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE) DisplayMode.FULLSCREEN else DisplayMode.BEST_FIT
    }

    fun loadIpCam(url: String?) {
        Mjpeg.newInstance()
            .open(url, TIMEOUT)
            .subscribe(
                { inputStream: MjpegInputStream? ->
                    mjpegView!!.setSource(inputStream)
                    mjpegView!!.setDisplayMode(calculateDisplayMode())
                    mjpegView!!.flipHorizontal(true)
                    mjpegView!!.flipVertical(true)
                    mjpegView!!.setRotate(180f)
                    mjpegView!!.showFps(true)
                }
            ) { throwable: Throwable? ->
                Log.e(javaClass.simpleName, "mjpeg error", throwable)
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
            }
    }

    override fun onResume() {
        super.onResume()
        loadIpCam(url)
    }

    override fun onPause() {
        super.onPause()
        mjpegView!!.stopPlayback()

        if (mIsRecording) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.imageView -> {
                if (mIsRecording) {
                    stopRecording();
                } else {
                    initMediaRecorder();
                    startRecording();

                    //开始录像后，每隔1s去更新录像的时间戳
                    mHandler.postDelayed(mTimestampRunnable, 1000);
                }
            }
            else -> {
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initMediaRecorder() {
        mRecorder = MediaRecorder() //实例化
        mRecorder!!.setOrientationHint(90) //改变保存后的视频文件播放时是否横屏(不加这句，视频文件播放的时候角度是反的)
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC) // 设置从麦克风采集声音
        //mRecorder!!.setVideoSource(mjpegView!!.holder.surface) // 设置从摄像头采集图像

        mRecorder!!.setInputSurface(mjpegView!!.holder.surface)

       // mRecorder!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)


        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) // 设置视频的输出格式 为MP4
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT) // 设置音频的编码格式
        mRecorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264) // 设置视频的编码格式
        mRecorder!!.setVideoSize(176, 144) // 设置视频大小
        mRecorder!!.setVideoFrameRate(20) // 设置帧率
        //        mRecorder.setMaxDuration(10000); //设置最大录像时间为10s
        mRecorder!!.setPreviewDisplay(mjpegView!!.holder.surface)


        val sdCardDir = Environment.getExternalStorageDirectory().toString() + filePath
        val appDir = File(sdCardDir)

        if (!appDir.exists()) { //not exist
            appDir.mkdirs()
        }

        mRecorder!!.setOutputFile(
            appDir.path + File.separator.toString() + "VID_" + System.currentTimeMillis()
                .toString() + ".mp4"
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_capture) {
            runOnUiThread {
                if (lastPreview != null) {
                    imageView!!.setImageBitmap(lastPreview)

                    FileUtil.saveBitmap2SDCard(this, filePath, lastPreview)
                }
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_capture, menu)
        return true
    }

    override fun onFrameCaptured(bitmap: Bitmap) {
        lastPreview = bitmap
    }

    private val mSurfaceCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceDestroyed(holder: SurfaceHolder) {
            mIsSufaceCreated = false
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            mIsSufaceCreated = true
        }

        override fun surfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
//            startPreview();
        }
    }

    private fun startRecording() {
        if (mRecorder != null) {
            try {
                mRecorder!!.prepare()
                mRecorder!!.start()
            } catch (e: Exception) {
                mIsRecording = false
            }
        }
        //mShutter.setImageDrawable(resources.getDrawable(R.drawable.recording_shutter_hl))
        mIsRecording = true
    }

    private fun stopRecording() {

//        if (mRecorder != null) {
//            mRecorder!!.stop()
//            mRecorder!!.release()
//            mRecorder = null
//        }

        if (mRecorder != null) {
            try {
                mRecorder!!.setOnErrorListener(null)
                mRecorder!!.setOnInfoListener(null)
                mRecorder!!.setPreviewDisplay(null)
                mRecorder!!.stop();
            } catch (e: IllegalStateException) {
                // TODO 如果当前java状态和jni里面的状态不一致，
                //e.printStackTrace();
                mRecorder = null;
                mRecorder = MediaRecorder()


            }catch (e: RuntimeException) {
                e.printStackTrace();
            } catch (e: Exception) {
                e.printStackTrace();
            }
            mRecorder!!.release();
            mRecorder = null;
        }

        //mShutter.setImageDrawable(resources.getDrawable(R.drawable.recording_shutter))

        mIsRecording = false
        mHandler.removeCallbacks(mTimestampRunnable)

        //将录像时间还原
        mMinutePrefix.setVisibility(View.VISIBLE)
        mMinuteText.setText("0")
        mSecondPrefix.setVisibility(View.VISIBLE)
        mSecondText.setText("0")

        //重启预览
        //startPreview()
    }

    private val mTimestampRunnable: Runnable = object : Runnable {
        override fun run() {
            updateTimestamp()
            mHandler.postDelayed(this, 1000)
        }
    }

    private fun updateTimestamp() {
        var second: Int = mSecondText.getText().toString().toInt()
        var minute: Int = mMinuteText.getText().toString().toInt()
        second++
//        Log.d(FragmentActivity.TAG, "second: $second")
        if (second < 10) {
            mSecondText.setText(second.toString())
        } else if (second >= 10 && second < 60) {
            mSecondPrefix.setVisibility(View.GONE)
            mSecondText.setText(second.toString())
        } else if (second >= 60) {
            mSecondPrefix.setVisibility(View.VISIBLE)
            mSecondText.setText("0")
            minute++
            mMinuteText.setText(minute.toString())
        } else if (minute >= 60) {
            mMinutePrefix.setVisibility(View.GONE)
        }
    }

    companion object {
        private const val TIMEOUT = 5
    }
}

