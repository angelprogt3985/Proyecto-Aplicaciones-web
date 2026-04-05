package com.mindguardians.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguardians.ui.theme.*

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: com.mindguardians.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val rotateAnim = rememberInfiniteTransition(label = "star")
    val rotation by rotateAnim.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "rotation"
    )

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onRegisterSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SpaceDark, SpaceDeep, SpaceMid)))
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .rotate(rotation)
                    .clip(CircleShape)
                    .background(GoldNeon)
                    .border(2.dp, GoldDark.copy(.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🌟", fontSize = 32.sp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Únete al cosmos", color = GoldNeon, fontWeight = FontWeight.Black, fontSize = 26.sp)
                Text(
                    "Crea tu cuenta para empezar tu aventura galáctica",
                    color = Color.White.copy(.5f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(.05f))
                    .border(1.dp, GoldNeon.copy(.3f), RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    RegisterTextField(
                        value = uiState.displayName,
                        onValueChange = viewModel::onDisplayNameChange,
                        label = "Nombre de héroe",
                        placeholder = "Guerrero Estelar"
                    )

                    RegisterTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = "Correo electrónico",
                        placeholder = "guerrero@cosmos.com",
                        keyboardType = KeyboardType.Email
                    )

                    RegisterTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = "Contraseña",
                        placeholder = "Mínimo 6 caracteres",
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )

                    RegisterTextField(
                        value = uiState.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = "Confirmar contraseña",
                        placeholder = "Repite tu contraseña",
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = Color(0xFFFF6B6B),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(
                        onClick = viewModel::register,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldNeon,
                            contentColor = SpaceDark
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = SpaceDark,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("🌟  Crear mi héroe", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿Ya tienes cuenta? ", color = Color.White.copy(.5f), fontSize = 13.sp)
                Text(
                    "Iniciar sesión",
                    color = CyanNeon,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Composable
private fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = Color.White.copy(.7f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.White.copy(.25f), fontSize = 14.sp) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = GoldNeon,
                unfocusedBorderColor = GoldNeon.copy(.3f),
                cursorColor = GoldNeon,
            )
        )
    }
}