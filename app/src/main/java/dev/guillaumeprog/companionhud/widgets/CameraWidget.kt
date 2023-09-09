package dev.guillaumeprog.companionhud.widgets

import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture

@Composable
fun CameraPreview(cameraProviderFuture: ListenableFuture<ProcessCameraProvider>) {
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    val backgroundColor = MaterialTheme.colorScheme.background.hashCode()

    Log.d("Lifecycle", "state camera: " + lifecycleOwner.lifecycle.currentState)

    AndroidView(factory = { context ->
        PreviewView(context).apply {
            setBackgroundColor(backgroundColor)
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            
            scaleType = PreviewView.ScaleType.FILL_START
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE

            post {
                Log.d("Lifecycle", "state post: " + lifecycleOwner.lifecycle.currentState)
                if(
                    lifecycleOwner.lifecycle.currentState == Lifecycle.State.CREATED ||
                    lifecycleOwner.lifecycle.currentState == Lifecycle.State.STARTED ||
                    lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED
                    ) {
                    cameraProviderFuture.addListener({
                        Log.d("Lifecycle", "state listener: " + lifecycleOwner.lifecycle.currentState)
                        val cameraProvider = cameraProviderFuture.get()
                        bindPreview(cameraProvider, lifecycleOwner, this)
                    }, ContextCompat.getMainExecutor(context))
                }
            }
        }
    })
}

fun bindPreview(cameraProvider: ProcessCameraProvider, lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
    val preview: Preview = Preview.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
        .build()

    val cameraSelector: CameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    preview.setSurfaceProvider(previewView.surfaceProvider)
    Log.d("Lifecycle", "Check: " + lifecycleOwner.lifecycle.currentState)

    // use camera variable to control zoom and shit
    val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
    Log.d("Lifecycle", "Check passed")
}