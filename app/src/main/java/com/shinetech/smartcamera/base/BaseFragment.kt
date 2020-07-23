package com.shinetech.smartcamera.base


import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.fragment.app.Fragment


/**
 * A simple [Fragment] subclass.
 */
abstract class BaseFragment : Fragment() {

//    var mBus: AndroidBus? = null
//    lateinit var mSession: Session
//    var loadingDialog: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        mBus = MyApplication.get(activity!!).mBus
//        mBus!!.register(this)
//        mSession = Session(activity!!)
    }

    override fun onResume() {
        super.onResume()
    }

    fun showToast(message: String) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * get version name
     */
    fun getVersionName(context: Context): String {

        val pm = context.getPackageManager()
        try {
            val packageInfo = pm.getPackageInfo(context.getPackageName(), 0)
            return packageInfo.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""

    }

//    fun showLoadingDialog() {
//        if (this.loadingDialog == null || !this.loadingDialog!!.isShowing) {
//            loadingDialog = KProgressHUD.create(activity)
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

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //mBus!!.unregister(this)
    }


}
