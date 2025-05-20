package juditgp.com.pawls4eversocial.view

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pawls4ever.navigation.Screens
import juditgp.com.pawls4eversocial.R
import juditgp.com.pawls4eversocial.model.resources.functions.GoogleRegisterAccount
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
import juditgp.com.pawls4eversocial.ui.theme.White
import juditgp.com.pawls4eversocial.viewmodel.SignUpViewModel


/**
 * Pantalla de registro de usuario.
 *
 * @param navController Controlador de navegación para navegar entre pantallas.
 * @param viewModel ViewModel para manejar la lógica de la pantalla de registro.
 */

/* Constantes privadas de la clase para manejar los
* mensajes de error y mantener facilmente la aplicación*/
private const val TEXT_WELCOME = "Unéte a la comunidad"
private const val DESCRIPTION_LOGO = "Logo Pawls4Ever"
private const val TEXT_TITLE_NAME = "Nombre de usuario"
private const val TEXT_TITLE_EMAIL = "Email"
private const val TEXT_TITLE_PASSWORD = "Contraseña"
private const val TEXT_PLACEHOLDER_NAME = "Nombre"
private const val TEXT_PLACEHOLDER_EMAIL = "Gmail@gmail.com"
private const val BUTTON_TEXT_SING_UP = "Crear cuenta"
private const val TEXT_ACCESS_LOGIN_FIRST = "¿Ya tienes cuenta?"
private const val TEXT_ACCESS_LOGIN_SECOND = " Login"

@Composable
fun SingUpScreen(navController: NavController,  viewModel: SignUpViewModel = SignUpViewModel()) {
    val context = LocalContext.current // Obtener el contexto actual
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColorLight)
            .padding(top = 40.dp),
        color = BackgroundColorLight
    ) {
        // Contenedor principal que almacena todos los elementos de la pantalla
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Encabezado exterior de la zona verde
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RegularTextRoboto (
                    text = TEXT_WELCOME,
                    fontSize = 24,
                    color = DarkRed
                )
                Spacer(modifier = Modifier.height(8.dp))
                ImageLoadingCoilApp(
                    model = R.drawable.logoappnegro,
                    contentDescription = DESCRIPTION_LOGO
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            // Column que contiene el formulario de registro
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(DarkGreen, shape = RoundedCornerShape(20.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BoldTextRoboto("Sign Up", 34, White, modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                )

                // Columna compuesta por varios textos y campos de texto.
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    //NOMBRE
                    RegularTextRoboto(
                        text = TEXT_TITLE_NAME,
                        fontSize = 17,
                        color = White
                    )
                    WhiteTextField(
                        valueInserted = viewModel.name.value,
                        placeHolderText = TEXT_PLACEHOLDER_NAME,
                        onValueChanged = { viewModel.name.value = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    //EMAIL
                    RegularTextRoboto(
                        text = TEXT_TITLE_EMAIL,
                        fontSize = 17,
                        color = White
                    )
                    WhiteTextField(
                        valueInserted = viewModel.email.value,
                        placeHolderText = TEXT_PLACEHOLDER_EMAIL,
                        onValueChanged = { viewModel.email.value = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    //CONTRASEÑA
                    RegularTextRoboto(
                        text = TEXT_TITLE_PASSWORD,
                        fontSize = 17,
                        color = White
                    )
                    PasswordTextField(
                        password = viewModel.password.value,
                        onPasswordChange = { viewModel.password.value = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }


                // Función de google para registrar la cuenta.
                GoogleRegisterAccount(context = context, navController = navController)

                // Botón para crear la cuenta.
                ButtonRobotoRegularWithBackground(
                    text = BUTTON_TEXT_SING_UP,
                    fontSize = 17,
                    color = DarkRed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    onClick = {
                        viewModel.singUpUser{
                        }
                    }
                )

                // Texto para acceder a la pantalla de inicio de sesión.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    RegularTextRoboto(TEXT_ACCESS_LOGIN_FIRST, 17, White)
                    BoldTextRoboto(TEXT_ACCESS_LOGIN_SECOND, 17, White,
                        modifier = Modifier.clickable {
                            navController.navigate(Screens.LoginScreen.name)
                        })
                }
            }
        }

        // CustomToast para la pantalla de SignUp.
        CustomToast(
            message = viewModel.toastMessage.value,
            isVisible = viewModel.showToast.value,
            positivo = viewModel.isPositiveToast.value,
            onDismiss = { viewModel.dismissToast() }
        )

        // Mostrar el AlertDialog justo después de pulsar el botón y tras enviar el correo
        if (viewModel.showVerifyDialog.value) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    androidx.compose.material3.Button(onClick = {
                        viewModel.checkEmailVerified {
                            navController.navigate(Screens.HomeScreen.name) {
                                popUpTo(Screens.LoginScreen.name) { inclusive = true }
                            }
                        }
                    }) {
                        androidx.compose.material3.Text("Ya verifiqué mi correo")
                    }
                },
                dismissButton = {
                    androidx.compose.material3.Button(onClick = {
                        // Reenviar correo de verificación
                        com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                    }) {
                        androidx.compose.material3.Text("Reenviar correo")
                    }
                },
                title = { androidx.compose.material3.Text("Verifica tu correo") },
                text = { androidx.compose.material3.Text("Te hemos enviado un correo de verificación. Por favor, verifica tu correo antes de continuar.") }
            )
        }
    }
}
