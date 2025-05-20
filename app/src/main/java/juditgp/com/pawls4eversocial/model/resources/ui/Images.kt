package juditgp.com.pawls4eversocial.model.resources.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage


//Carga los recursos almacenadas de la App
@Composable
fun ImageLoadingCoilApp(
    model: Any,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier
    )
}

