package dev.guillaumeprog.companionhud

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.guillaumeprog.companionhud.ui.theme.BTCompanionTheme
import dev.guillaumeprog.companionhud.widgets.CameraPreview
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HUDActivity : ComponentActivity() {
    private val tag = "HUD-ACTIVITY"
    private val cameraPermission = PermissionHelper(this, PermissionHelper.CameraPerms)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set screen orientation horizontal
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Set full screen
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        setContent {
            BTCompanionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GetPermsWrapper(cameraPermission) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            //Text("Camera goes here")
                            Log.d("Lifecycle", "state: " + LocalLifecycleOwner.current.lifecycle.currentState)
                            CameraPreview(cameraProviderFuture = cameraProviderFuture)
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 24.dp, vertical = 8.dp), contentAlignment = Alignment.BottomEnd) {
                               ClockWidget()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClockWidget() {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.FRANCE)
    var currentTime by remember { mutableStateOf(sdf.format(Date())) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = sdf.format(Date())
            delay(1000) // Update every 1 second
        }
    }

    Text(currentTime)
}

@Composable
fun GetPermsWrapper(cameraPermission: PermissionHelper, content: @Composable () -> Unit) {
    var hasCameraPermissions by remember { mutableStateOf(
        cameraPermission.hasPermission()
    ) }

    when (hasCameraPermissions) {
        true -> content()
        false -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("This app won't work without access to the camera")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { cameraPermission.requestPermission { hasCameraPermissions = it } }
                    ) {
                        Text("Allow Camera")
                    }
                }
            }
        }
    }
}