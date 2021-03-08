package com.me.ndkimage.demo

import android.Manifest
import android.os.Bundle
import android.view.TextureView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.me.ndkimage.demo.camera.Camera2Provider
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private val TAG = "MainActivity"
    private var surface: TextureView? = null
    private var cameraProvider: Camera2Provider? = null

    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val mPermissionList: MutableList<String> = ArrayList()
    private val PERMISSION_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        surface = findViewById(R.id.surface)

        cameraProvider = Camera2Provider(this)
        cameraProvider?.initTexture(surface)

        findViewById<Button>(R.id.button).setOnClickListener { _ ->
            cameraProvider?.capture()
        }
    }

    /**
     * 响应授权
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //从设置页面返回，判断权限是否申请。
            if (EasyPermissions.hasPermissions(this, *permissions)) {
                Toast.makeText(this, "权限申请成功!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "权限申请失败!", Toast.LENGTH_SHORT).show();

            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }


}