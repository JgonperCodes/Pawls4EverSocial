package juditgp.com.pawls4eversocial.model.resources.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import juditgp.com.pawls4eversocial.ui.theme.Green
import juditgp.com.pawls4eversocial.ui.theme.LightGreen
import juditgp.com.pawls4eversocial.ui.theme.Roboto


@Composable
fun BoldTextRoboto(
    text: String,
    fontSize: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontFamily = Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize.sp,
        color = color,
        modifier = modifier
    )
}

@Composable
fun RegularTextRoboto(
    text: String,
    fontSize: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontFamily = Roboto,
        fontSize = fontSize.sp,
        color = color,
        modifier = modifier
    )
}

@Composable
fun AnnotatedTextRoboto(
    text: AnnotatedString,
    fontSize: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null // Callback opcional para manejar clics
) {
    Text(
        text = text,
        fontFamily = Roboto,
        fontSize = fontSize.sp,
        modifier = if (onClick != null) {
            modifier.clickable { onClick() }
        } else {
            modifier
        }
    )
}

@Composable
fun TextTitleWithBackgroundRound(
    sectionTitle: String,
    onClick: (() -> Unit)? = null
) {
   Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Green, shape = RoundedCornerShape(30.dp))
            .clickable { onClick?.invoke() }
            .padding(8.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(
            text = sectionTitle,
            color = Color.White,
            fontFamily = Roboto,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MetricLabel(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            color = Color.Gray
        )
    }
}

@Composable
fun ClickableTextLabel(label: String, value: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = androidx.compose.ui.Modifier.clickable { onClick() }
    ) {
        Text(
            text = value.toString(),
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            color = Color(0xFF6B122B)
        )
    }
}
