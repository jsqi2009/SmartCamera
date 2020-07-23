package com.shinetech.smartcamera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.datwyler.smartrack.base.BaseActivity
import com.shinetech.mjpeglib.IpCamDefaultActivity
import com.shinetech.mjpeglib.IpCamSurfaceActivity
import com.shinetech.mjpeglib.LiveStreamActivity
import com.shinetech.mjpeglib.MJPEGRecord.StreamActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener {

    private val baseURL = "http://10.10.10.1:8080/?action=stream"
    private val filePath = "/WiFiCamera/photos/"

    private val NEEDED_PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )
    private val PERMISSION_REQUEST_CODE = 1
    private var mHasPermission: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        requestPermission(NEEDED_PERMISSIONS, PERMISSION_REQUEST_CODE)
    }

    override fun onResume() {
        super.onResume()

    }

    private fun init() {

        tv_preview.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_preview -> {

                /*var intent = Intent(this, IpCamDefaultActivity::class.java)
                intent.putExtra("baseURL", baseURL)
                intent.putExtra("filePath", filePath)
                startActivity(intent)*/

                /*var intent = Intent(this, IpCamSurfaceActivity::class.java)
                intent.putExtra("baseURL", baseURL)
                intent.putExtra("filePath", filePath)
                startActivity(intent)*/

                /*var intent = Intent(this, LiveStreamActivity::class.java)
                intent.putExtra("baseURL", baseURL)
                intent.putExtra("filePath", filePath)
                startActivity(intent)*/

                var intent = Intent(this, StreamActivity::class.java)
                intent.putExtra("baseURL", baseURL)
                intent.putExtra("filePath", filePath)
                startActivity(intent)
            }
            else -> {

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var hasAllPermission = true
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (i in grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    hasAllPermission = false
                    break
                }
            }
            mHasPermission = hasAllPermission
        }
    }

}