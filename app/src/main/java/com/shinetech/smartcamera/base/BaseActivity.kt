package com.datwyler.smartrack.base

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import com.shinetech.smartcamera.R


open class BaseActivity : FragmentActivity() {

//    lateinit var mBus: AndroidBus
//    lateinit var mSession: Session
    lateinit var mContext: BaseActivity
    //var loadingDialog: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
//            mContext = this
//            mBus = MyApplication.get(this).mBus
//            this.mBus!!.register(this)
//            mSession = Session(this)
//            AppManager.appManager.addActivity(this)

//            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
//
//            hideBottomMenu()

        } catch (e: Exception) {

        }

        //设置手机屏幕常亮
        //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    fun showToast(message: String) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

//    fun showLoadingDialog() {
//        if (this.loadingDialog == null || !this.loadingDialog!!.isShowing) {
//            loadingDialog = KProgressHUD.create(this)
//                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                    .setDimAmount(0.5f)
//            loadingDialog!!.show()
//        }
//    }
//
//    fun hideLoadingDialog() {
//        if (this.loadingDialog != null && this.loadingDialog!!.isShowing) {
//            this.loadingDialog!!.dismiss()
//            this.loadingDialog = null
//        }
//    }

    //设置Activity对应的顶部状态栏的颜色
    fun setStatusBarColor(activity: Activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = activity.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = activity.resources.getColor(R.color.colorPrimary)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun hideBottomMenu() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            var view = this.window.decorView
            view.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19){
            var decorView = window.decorView
            var uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN

            decorView.systemUiVisibility = uiOptions
        }
    }

    /**
     * 是否在后台
     *
     * @return
     */
    val isAppOnFreground: Boolean
        get() {
            val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val curPackageName = applicationContext.packageName
            val app = am.runningAppProcesses ?: return false
            for (a in app) {
                if (a.processName == curPackageName && a.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true
                }
            }
            return false
        }

    val dpi: Int
        get() {
            var dpi = 0
            val display = windowManager.defaultDisplay
            val dm = DisplayMetrics()
            val c: Class<*>
            try {
                c = Class.forName("android.view.Display")
                val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
                method.invoke(display, dm)
                dpi = dm.heightPixels
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return dpi
        }

    val statusBarHeight: Int
        get() {
            var result = 20
            try {
                val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
                if (resourceId > 0) {
                    result = resources.getDimensionPixelSize(resourceId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return result
        }


    override fun onDestroy() {
        super.onDestroy()
        try {
//            this.mBus!!.unregister(this)
//            AppManager.appManager.finishActivity(this)
//            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            val isOpen = imm.isActive
//            if (isOpen) {
//                closeKeyboard()
//            }
        } catch (e: Exception) {
        }

    }

    fun closeKeyboard() {
        val view = window.peekDecorView()
        if (view != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    fun getScreenSize(context: Context) {
        var manager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var displayMetrics = DisplayMetrics()
        manager.defaultDisplay.getMetrics(displayMetrics)
        var mWidth: Int = displayMetrics.widthPixels
        var mHight: Int = displayMetrics.heightPixels

        Log.e("screen size", "width:$mWidth,height:$mHight")

    }

    /**
     * EditText获取焦点并显示软键盘
     */
    fun showSoftInputFromWindow(activity: Activity, editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    /**
     * check Permission
     *
     * @return
     */
    fun checkPermission(NEEDED_PERMISSIONS: Array<String>): Boolean {
        for (permission in NEEDED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

                return false
            }
        }
        return true
    }

    /**
     * request permission
     */
    fun requestPermission(NEEDED_PERMISSIONS: Array<String>, PERMISSION_REQUEST_CODE: Int) {
        ActivityCompat.requestPermissions(this,
            NEEDED_PERMISSIONS, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }


}
