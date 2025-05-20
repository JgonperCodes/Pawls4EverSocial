package juditgp.com.pawls4eversocial.view

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pawls4ever.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import juditgp.com.pawls4eversocial.R
import kotlinx.coroutines.delay
import juditgp.com.pawls4eversocial.ui.theme.Roboto
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


//Constantes para las animaciónes en SplashScreen
private const val DELAY_BEFORE_NAVIGATION = 1000L
private const val LOGO_DESCRIPTION = "Logo de Pawls4Ever, el fondo es de color rojo " +
        "y en el centro hay una silueta de un gato, una persona y perro de color blanco," +
        " estás están encima de una forma en comentario de color verde para " +
        "dar la sensación de que están dentro de un comentario"
private const val PAW_DESCRIPTION_RED = "Pata de color rojo sobre un fondo de color rojo"
private const val PAW_DESCRIPTION_GREEN = "Pata de color verde sobre un fondo de color rojo"
private const val SPLASH_BACKGROUND_COLOR = 0xFFFFF4EC


@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val textOffset = remember { Animatable(-340f) }
    val textAlpha = remember { Animatable(0f) }
    val pawOffsetTopLeft = remember { Animatable(-200f) }
    val pawOffsetBottomRight = remember { Animatable(200f) }
    val pawRotationTopLeft = remember { Animatable(0f) }
    val pawRotationTopRight = remember { Animatable(0f) }
    val pawRotationBottomLeft = remember { Animatable(0f) }
    val pawRotationBottomRight = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        runAnimations(
            scale, alpha, textOffset, textAlpha,
            pawOffsetTopLeft, pawOffsetBottomRight,
            pawRotationTopLeft, pawRotationTopRight,
            pawRotationBottomLeft, pawRotationBottomRight
        )
        navigateToNextScreen(navController)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(SPLASH_BACKGROUND_COLOR)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            PawImage(
                rotation = pawRotationTopLeft.value,
                alignment = Alignment.TopStart,
                painter = painterResource(id = R.drawable.iconopatarojo),
                offsetX = pawOffsetTopLeft.value
            )
            PawImage(
                rotation = pawRotationTopRight.value,
                alignment = Alignment.TopEnd,
                painter = painterResource(id = R.drawable.iconopataverde),
                offsetX = -pawOffsetTopLeft.value
            )
            PawImage(
                rotation = pawRotationBottomLeft.value,
                alignment = Alignment.BottomStart,
                painter = painterResource(id = R.drawable.iconopataverde),
                offsetX = pawOffsetBottomRight.value
            )
            PawImage(
                rotation = pawRotationBottomRight.value,
                alignment = Alignment.BottomEnd,
                painter = painterResource(id = R.drawable.iconopatarojo),
                offsetX = -pawOffsetBottomRight.value
            )
            LogoAndText(scale, alpha, textOffset, textAlpha)
        }
    }
}


@Composable
fun PawImage(rotation: Float, alignment: Alignment, painter: Painter, offsetX: Float = 0f, offsetY: Float = 0f) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = alignment
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .offset(x = offsetX.dp, y = offsetY.dp)
                .rotate(rotation)
        )
    }
}
@Composable
fun LogoAndText(
    scale: Animatable<Float, AnimationVector1D>,
    alpha: Animatable<Float, AnimationVector1D>,
    textOffset: Animatable<Float, AnimationVector1D>,
    textAlpha: Animatable<Float, AnimationVector1D>
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        LogoImage()
        Spacer(modifier = Modifier.height(12.dp))
        IconoImage(scale, alpha, modifier = Modifier.size(300.dp))
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedText(textOffset, textAlpha)
    }
}

@Composable
fun LogoImage() {
    Image(
        painter = painterResource(id = R.drawable.logoappnegro),
        contentDescription = LOGO_DESCRIPTION
    )
}

@Composable
fun IconoImage(
    scale: Animatable<Float, AnimationVector1D>,
    alpha: Animatable<Float, AnimationVector1D>,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.iconoapp),
        contentDescription = LOGO_DESCRIPTION,
        modifier = modifier
            .scale(scale.value)
            .alpha(alpha.value)
            .clip(RoundedCornerShape(16.dp))
    )
}

@Composable
fun AnimatedText(textOffset: Animatable<Float, AnimationVector1D>, textAlpha: Animatable<Float, AnimationVector1D>) {
    Box(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .offset(x = textOffset.value.dp, y = 4.dp)
            .alpha(textAlpha.value)
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier.offset(y = -4.dp)
        ) {
            Text(
                text = "Registra y comparte las memorias de tu compañero con tus amigos",
                style = TextStyle(
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                ),
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

suspend fun runAnimations(
    scale: Animatable<Float, AnimationVector1D>,
    alpha: Animatable<Float, AnimationVector1D>,
    textOffset: Animatable<Float, AnimationVector1D>,
    textAlpha: Animatable<Float, AnimationVector1D>,
    pawOffsetTopLeft: Animatable<Float, AnimationVector1D>,
    pawOffsetBottomRight: Animatable<Float, AnimationVector1D>,
    pawRotationTopLeft: Animatable<Float, AnimationVector1D>,
    pawRotationTopRight: Animatable<Float, AnimationVector1D>,
    pawRotationBottomLeft: Animatable<Float, AnimationVector1D>,
    pawRotationBottomRight: Animatable<Float, AnimationVector1D>
) {
    coroutineScope {
        // Escalar el icono
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = { OvershootInterpolator(4f).getInterpolation(it) }
                )
            )
        }

        // Hacer visible el icono
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }

        // Animar las patas: mover y rotar
        launch {
            pawOffsetTopLeft.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
        launch {
            pawOffsetBottomRight.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
        launch {
            pawRotationTopLeft.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
        launch {
            pawRotationTopRight.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
        launch {
            pawRotationBottomLeft.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
        launch {
            pawRotationBottomRight.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
    }
    delay(100)
    coroutineScope {
        launch {
            textOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 800, easing = { OvershootInterpolator(2f).getInterpolation(it) })
            )
        }
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }
    }
    delay(DELAY_BEFORE_NAVIGATION)
}


fun navigateToNextScreen(navController: NavController) {
    if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) {
        navController.navigate(Screens.LoginScreen.name) {
            popUpTo(Screens.SplashScreen.name) { inclusive = true }
        }
    } else {
        navController.navigate(Screens.HomeScreen.name) {
            popUpTo(Screens.SplashScreen.name) { inclusive = true }
        }
    }
}