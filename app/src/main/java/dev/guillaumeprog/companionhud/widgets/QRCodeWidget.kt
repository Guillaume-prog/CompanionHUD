package dev.guillaumeprog.companionhud.widgets

import androidx.compose.runtime.Composable
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import net.glxn.qrgen.android.QRCode

@Composable
fun QRCodeWidget(data: String, size: Int = 300, bgColor: Int = Color.WHITE, fgColor: Int = Color.BLACK) {
    val qrCode = QRCode
        .from(data)
        .withSize(size, size)
        .withColor(fgColor, bgColor)
        .bitmap()

    Image(
        bitmap = qrCode.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
@Preview
fun QRCodePreview() {
    QRCodeWidget(data = "Hello world")
}
