package juditgp.com.pawls4eversocial.model.resources.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import juditgp.com.pawls4eversocial.R
import coil.compose.AsyncImage
import coil.imageLoader
import juditgp.com.pawls4eversocial.ui.theme.DarkGreen
import juditgp.com.pawls4eversocial.ui.theme.DarkRed

/**
 * Composable que muestra un mensaje emergente (toast) personalizado.
 *
 * @param message El mensaje a mostrar en el toast.
 * @param isVisible Bandera que indica si el toast es visible o no.
 * @param positivo Bandera que indica si el mensaje es positivo o negativo.
 * @param onDismiss Función que se llama cuando se oculta el toast.
 */
@Composable
fun CustomToast(message: String, isVisible: Boolean, positivo: Boolean, onDismiss: () -> Unit) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = if (positivo) DarkGreen else DarkRed,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = if (positivo) R.drawable.happydog else R.drawable.saddog,
                        contentDescription = "GIF de acompañamiento",
                        modifier = Modifier.size(65.dp),
                        imageLoader = LocalContext.current.imageLoader.newBuilder()
                            .components {
                                add(coil.decode.GifDecoder.Factory())
                            }
                            .build()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message,
                        style = TextStyle(color = Color.White, fontSize = 20.sp)
                    )
                }
            }
        }
    }

    // Ocultar automáticamente después de 2 segundos
    LaunchedEffect(key1 = isVisible) {
        if (isVisible) {
            delay(2000)
            onDismiss() // Cambia el estado de isVisible a false
        }
    }
}