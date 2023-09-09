package dev.guillaumeprog.companionhud

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class PermissionHelper(private val activity: ComponentActivity, private val permission: String) {

    private val tag = "PermissionHelper"

    companion object {
        const val CameraPerms = android.Manifest.permission.CAMERA
        val BluetoothPerms = when(Build.VERSION.SDK_INT < 31) {
            true -> android.Manifest.permission.BLUETOOTH
            false -> android.Manifest.permission.BLUETOOTH_CONNECT
        }
    }

    private var callback: ((Boolean) -> Unit)? = null

    private var requestPermissionContract = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
        Log.d(tag, "Was $permission accepted ? $isGranted")
        callback?.invoke(isGranted)
    }

    fun hasPermission(): Boolean {
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(callback: (Boolean) -> Unit) {
        if(hasPermission()) {
            Log.d(tag, "$permission already granted")
            callback(true)
            return
        }

        Log.d(tag, "$permission not granted, requesting access")
        this.callback = callback
        requestPermissionContract.launch(permission)
    }
}