package com.lanshifu.lib.ext.permission

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.lanshifu.lib.Ktx

/**
 * @author lanxiaobin
 * @date 2019-12-19.
 */
class PermissionUtil {

    companion object {

        val TAG = "requestUnknowAppPermission"

        /**
         * 请求未知应用权限，Android O 安装apk用
         */
        @SuppressLint("NewApi")
        fun requestUnknowAppPermission(activity: FragmentActivity, callbacks: PermissionsCallbackDSL.() -> Unit) {

            val permissionsCallback = PermissionsCallbackDSL().apply { callbacks() }
            val requestCode = PermissionsMap.put(permissionsCallback)

            if (checkUnknowAppAndroidO()) {
                permissionsCallback.onGranted()
                PermissionsMap.get(requestCode)
            } else {
                getKtxPermissionFragment(activity).requestUnknowAppPermissionsByFragment(requestCode)
            }
        }


        /**
         * 动态权限
         */
        fun requestPermission(activity: FragmentActivity, vararg permissions: String, callbacks: PermissionsCallbackDSL.() -> Unit) {

            val permissionsCallback = PermissionsCallbackDSL().apply { callbacks() }
            //获取一个新的请求码，会缓存 permissionsCallback
            val requestCode = PermissionsMap.put(permissionsCallback)

            //过滤已经申请的权限
            val needRequestPermissions = permissions.filter { !isGranted(activity, it) }

            if (needRequestPermissions.isEmpty()) {
                permissionsCallback.onGranted()
            } else {
                val shouldShowRationalePermissions = mutableListOf<String>()
                val shouldNotShowRationalePermissions = mutableListOf<String>()
                for (permission in needRequestPermissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                        shouldShowRationalePermissions.add(permission)
                    else
                        shouldNotShowRationalePermissions.add(permission)
                }

                //这个应该是拒绝过的权限，
                if (shouldShowRationalePermissions.isNotEmpty()) {
                    permissionsCallback.onShowRationale(
                            PermissionRequest(
                                    getKtxPermissionFragment(activity),
                                    shouldShowRationalePermissions,
                                    requestCode
                            )
                    )
                }

                //一般的权限请求
                if (shouldNotShowRationalePermissions.isNotEmpty()) {
                    getKtxPermissionFragment(activity).requestPermissionsByFragment(
                            shouldNotShowRationalePermissions.toTypedArray(),
                            requestCode
                    )
                }
            }
        }


        private fun getKtxPermissionFragment(activity: FragmentActivity): KtxPermissionFragment {


            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = KtxPermissionFragment()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    activity.supportFragmentManager.beginTransaction().add(fragment, TAG).commitNow()
                }else{
                    activity.supportFragmentManager.beginTransaction().add(fragment, TAG).commit()
                    activity.supportFragmentManager.executePendingTransactions()
                }
            }
            return fragment as KtxPermissionFragment
        }


        fun isGranted(activity: Activity, permission: String): Boolean {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                    ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }


        fun checkUnknowAppAndroidO(): Boolean {
            return if (Build.VERSION.SDK_INT >= 26) {
                Ktx.app.packageManager.canRequestPackageInstalls()
            } else {
                true
            }
        }

    }
}