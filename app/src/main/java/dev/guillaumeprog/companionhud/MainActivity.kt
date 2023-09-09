package dev.guillaumeprog.companionhud

import android.bluetooth.BluetoothAdapter
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.guillaumeprog.companionhud.ui.theme.BTCompanionTheme
import dev.guillaumeprog.companionhud.widgets.QRCodeWidget

class MainActivity : ComponentActivity() {
    private val tag = "MY-BT-COMPANION"

    private val bt = BtManager(this)
    private val permissionHelper = PermissionHelper(this, PermissionHelper.BluetoothPerms)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionHelper.requestPermission { granted: Boolean ->
            when(granted) {
                true -> init()
                false -> Log.e(tag, "Permission denied")
            }
        }

        setContent {
            BTCompanionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Row {
                        BTText(bt = bt)
                        QRCodeWidget(
                            data = "Hello world",
                            size = 400,
                            bgColor = MaterialTheme.colorScheme.background.hashCode(),
                            fgColor = MaterialTheme.colorScheme.onSurface.hashCode()
                        )
                    }
                }
            }
        }
    }

    private fun init() {
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bt, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bt)
    }
}

@Composable
fun BTText(bt: BtManager) {
    var state by remember { mutableStateOf(false) }
    bt.onStatusChange { state = it }

    val text = when(state) {
        true -> "on"
        false -> "off"
    }

    Text("Bluetooth status: $text")
}