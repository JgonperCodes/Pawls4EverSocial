package juditgp.com.pawls4eversocial.ui.theme

import androidx.compose.ui.graphics.Color
import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import juditgp.com.pawls4eversocial.model.resources.ui.ImageLoadingCoilApp
import juditgp.com.pawls4eversocial.model.resources.ui.RegularTextRoboto

@Composable
fun placeHolderNewNote(
    text: String,
    modifierText: Modifier,
    color: Color,
    image: Any,
    contentDescriptionImage: String,
    modifierImage: Modifier,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = { onClick?.invoke() })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Texto del placeholder
            RegularTextRoboto(
                text = text,
                fontSize = 16,
                color = Color.White,
                modifier = modifierText
            )

            // Imagen debajo del texto
            ImageLoadingCoilApp(
                model = image,
                contentDescription = contentDescriptionImage,
                modifier = modifierImage
                    .padding(top = 8.dp)
                    .size(150.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}