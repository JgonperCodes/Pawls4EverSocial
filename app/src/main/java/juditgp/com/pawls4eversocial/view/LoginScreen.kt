package juditgp.com.pawls4eversocial.view

import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pawls4ever.navigation.Screens
import juditgp.com.pawls4eversocial.R
import juditgp.com.pawls4eversocial.model.resources.functions.GoogleLogin
import juditgp.com.pawls4eversocial.model.resources.ui.BoldTextRoboto
import juditgp.com.pawls4eversocial.model.resources.ui.ButtonRobotoRegularWithBackground
import juditgp.com.pawls4eversocial.model.resources.ui.CustomToast
import juditgp.com.pawls4eversocial.model.resources.ui.ImageLoadingCoilApp
import juditgp.com.pawls4eversocial.model.resources.ui.PasswordTextField
import juditgp.com.pawls4eversocial.model.resources.ui.RegularTextRoboto
import juditgp.com.pawls4eversocial.model.resources.ui.WhiteTextField
import juditgp.com.pawls4eversocial.ui.theme.BackgroundColorLight
import juditgp.com.pawls4eversocial.ui.theme.DarkGreen
import juditgp.com.pawls4eversocial.ui.theme.DarkRed
import juditgp.com.pawls4eversocial.ui.theme.Red
import juditgp.com.pawls4eversocial.ui.theme.White
import juditgp.com.pawls4eversocial.viewmodel.LoginViewModel


//Constantes de la aplicación para el LoginScreen
const val TEXT_LOGIN = "Login"
const val TEXT_SIGN_UP = " Sign Up"
const val TEXT_LOGIN_BUTTON = "Iniciar Sesión"
const val TEXT_FORGOT_PASSWORD = "¿No recuerdas tu contraseña? "
const val TEXT_CLICK_HERE = "Pulsa aquí"
const val TEXT_WELCOME_BACK = "¡Bienvenido de nuevo!"
const val TEXT_NO_ACCOUNT = "¿No tienes cuenta?"

const val TEXT_EMAIL = "Email"
const val TEXT_EMAIL_PLACEHOLDER = "Gmail@gmail.com"
const val TEXT_PASSWORD = "Contraseña"

const val TEXT_DESCRIPTION_IMAGE_SIGN_UP = "Imagen de una chica barazando su labrador retriever" +
        "dentro de un marco de foto rojo con una pegatina de corazón rojo"
const val TEXT_DESCRIPTION_LOGO = "Imagen del logo de Pawls4Ever, las letras son de color negro y contiene unas patitas de color verde" +
        "a los lados del texto del logo, dentro de esté contiene iconografia de mascotas y dueños"

const val SIZE_TITLE_TEXTFIELD = 19
const val SIZE_TITLE_WELCOME = 24
const val SIZE_TITLE_LOGIN = 34
const val SIZE_SIGNUP_TEXTS = 17
const val SIZE_REMEMBER_PASSWORD = 15
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = LoginViewModel()) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColorLight)
            .padding(top = 40.dp),
        color = BackgroundColorLight
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Encabezado
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RegularTextRoboto (
                    text = TEXT_WELCOME_BACK,
                    fontSize = SIZE_TITLE_WELCOME,
                    color = DarkRed
                )
                Spacer(modifier = Modifier.height(8.dp))
                ImageLoadingCoilApp(
                    model = R.drawable.logoappnegro,
                    contentDescription = TEXT_DESCRIPTION_LOGO
                )
            }

            // Fondo verde
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(DarkGreen, shape = RoundedCornerShape(20.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título + foto grande
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Texto Bold Roboto
                    BoldTextRoboto(TEXT_LOGIN, SIZE_TITLE_LOGIN, White)

                    ImageLoadingCoilApp(
                        model = R.drawable.chicaabrazandoperro,
                        contentDescription = TEXT_DESCRIPTION_IMAGE_SIGN_UP,
                        modifier = Modifier.size(230.dp)
                    )
                }

                // Campos de texto de la pantalla de inicio de sesión.
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    //EMAIL
                    RegularTextRoboto(
                        text = TEXT_EMAIL,
                        fontSize = SIZE_TITLE_TEXTFIELD,
                        color = White
                    )
                    WhiteTextField(
                        valueInserted = viewModel.email.value,
                        placeHolderText = TEXT_EMAIL_PLACEHOLDER,
                        onValueChanged = { viewModel.email.value = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    //CONTRASEÑA
                    RegularTextRoboto(
                        text = TEXT_PASSWORD,
                        fontSize = SIZE_TITLE_TEXTFIELD,
                        color = White
                    )
                    PasswordTextField(
                        password = viewModel.password.value,
                        onPasswordChange = { viewModel.password.value = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    RegularTextRoboto(TEXT_FORGOT_PASSWORD, SIZE_REMEMBER_PASSWORD, White)
                    BoldTextRoboto(TEXT_CLICK_HERE, SIZE_REMEMBER_PASSWORD, Red,
                        modifier = Modifier.clickable {
                            viewModel.verifyUser {}
                        })
                }

                // Google Sign-In
                GoogleLogin(context = context, navController = navController)

                // Botón Iniciar Sesión
                ButtonRobotoRegularWithBackground(
                    text = TEXT_LOGIN_BUTTON,
                    fontSize = 17,
                    color = DarkRed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    onClick = {
                        viewModel.loginUser {
                            navController.navigate(Screens.HomeScreen.name) {
                                popUpTo(Screens.LoginScreen.name) { inclusive = true }
                            }
                        }
                    }
                )

                // Sección Sign Up
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    RegularTextRoboto(TEXT_NO_ACCOUNT, SIZE_SIGNUP_TEXTS, White)
                    BoldTextRoboto(TEXT_SIGN_UP, SIZE_SIGNUP_TEXTS, White,
                        modifier = Modifier.clickable {
                        navController.navigate(Screens.RegisterScreen.name)
                    })
                }
            }
        }

        // CustomToast para que se muestre en el Login Screen
        CustomToast(
            message = viewModel.toastMessage.value,
            isVisible = viewModel.showToast.value,
            positivo = viewModel.isPositiveToast.value,
            onDismiss = { viewModel.dismissToast() }
        )
    }
}