package com.lanshifu.lib.ext.permission

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.lanshifu.lib.ext.logd


class KtxPermissionFragment : Fragment() {

    var mRequestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun requestPermissionsByFragment(permissions: Array<String>, requestCode: Int) {
        requestPermissions(permissions, requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val neverAskPermissions = mutableListOf<String>()
        val deniedPermissions = mutableListOf<String>()
        val grantedPermissions = mutableListOf<String>()
        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    deniedPermissions.add(permission)
                } else {
                    neverAskPermissions.add(permission)
                }
            } else {
                grantedPermissions.add(permission)
            }
        }

        "onRequestPermissionsResult".logd(TAG)
        val permissionsCallback = PermissionsMap.get(requestCode)
        if (deniedPermissions.isNotEmpty()) {
            "onDenied".logd(TAG)
            // denied
            permissionsCallback?.onDenied(deniedPermissions)
        }

        if (neverAskPermissions.isNotEmpty()) {
            "never ask".logd(TAG)
            // never ask
            permissionsCallback?.onNeverAskAgain(neverAskPermissions)
        }

        if (deniedPermissions.isEmpty() && neverAskPermissions.isEmpty()) {
            "granted".logd(TAG)
            // granted
            permissionsCallback?.onGranted()
        }
    }

    /**
     * 请求打开未知权限，Android O 安装apk 需要
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun requestUnknowAppPermissionsByFragment(requestCode: Int) {
        val selfPackageUri = Uri.parse("package:" + context?.packageName)
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, selfPackageUri)

        "startActivityForResult,requestCode=$requestCode".logd(TAG)
        mRequestCode = requestCode
        startActivityForResult(intent, requestCode)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        "onActivityResult,requestCode=$requestCode,resultCode=$resultCode".logd(TAG)
        if ((requestCode == mRequestCode) and (resultCode == -1)) {
            val permissionsCallback = PermissionsMap.get(requestCode)
            permissionsCallback?.onGranted()
        }

    }
}