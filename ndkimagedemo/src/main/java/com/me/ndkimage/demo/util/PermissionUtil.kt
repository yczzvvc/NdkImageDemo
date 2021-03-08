package com.me.ndkimage.demo.util

import android.app.Activity
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions


object PermissionUtil {

    fun checkPermission(context: Context, perms: Array<String>): Boolean {
        return EasyPermissions.hasPermissions(context, *perms)
    }

    fun requestPermission(
        context: Activity,
        tip: String,
        requestCode: Int,
        perms: Array<String>
    ) {
        EasyPermissions.requestPermissions(context, tip, requestCode, *perms)
    }
}