package dev.guillaumeprog.companionhud

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

typealias StatusChangeCallback = (Boolean) -> Unit

class BtManager(context: Context): BroadcastReceiver() {

//    val manager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
//    val adapter: BluetoothAdapter? = manager.adapter
//
    private var callback: StatusChangeCallback? = null
//    val permission = Manifest.permission.BLUETOOTH_CONNECT
//
//    fun hasPermissions(context: Context): Boolean {
//        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
//    }
//
    fun onStatusChange(listener: StatusChangeCallback) {
        this.callback = listener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            when(intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                BluetoothAdapter.STATE_OFF -> callback?.invoke(false)
                BluetoothAdapter.STATE_ON -> callback?.invoke(true)
            }
        }
    }
}