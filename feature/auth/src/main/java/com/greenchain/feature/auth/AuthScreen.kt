package com.greenchain.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.greenchain.feature.auth.R

// culorile din app, clonate local
private val GreenPrimary = Color(0xFFDDE5B6)
private val Background = Color(0xFFF0EAD2)
private val GreenSecondary = Color(0xFFADC178)
private val BrownLight = Color(0xFFA98467)
private val BrownDark = Color(0xFF6C584C)

// un bej foarte deschis pentru card (mai premium)
private val CardBackground = Color(0xFFFEFBF2)

@Composable
fun AuthScreen(
    onSuccess: (isNewUser: Boolean) -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()

    if (ui.success) {
        LaunchedEffect(ui.success) {
            onSuccess(ui.isNewUser)
            vm.onNavigated()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(id = R.drawable.auth_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // LOGO GreenChain
            Image(
                painter = painterResource(id = R.drawable.greenchain_logo),
                contentDescription = "GreenChain Logo",
                modifier = Modifier
                    .height(190.dp)
                    .padding(bottom = 100.dp)
            )

            // Card cu formularul
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 22.dp)
                ) {

                    // EMAIL
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.titleMedium,
                        color = BrownDark
                    )

                    TextField(
                        value = ui.email,
                        onValueChange = vm::onEmail,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = GreenSecondary,
                            unfocusedIndicatorColor = GreenSecondary.copy(alpha = 0.6f),
                            cursorColor = GreenSecondary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    // PASSWORD
                    Text(
                        text = "Password",
                        style = MaterialTheme.typography.titleMedium,
                        color = BrownDark
                    )

                    TextField(
                        value = ui.password,
                        onValueChange = vm::onPassword,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = GreenSecondary,
                            unfocusedIndicatorColor = GreenSecondary.copy(alpha = 0.6f),
                            cursorColor = GreenSecondary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent
                        )
                    )

                    Spacer(Modifier.height(24.dp))

                    // SIGN IN buton – accent clar, contrast mare
                    Button(
                        onClick = { vm.login() },
                        enabled = !ui.loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenSecondary,  // mai închis, iese în evidență
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (ui.loading) "Signing in..." else "Sign In",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Create account – tot BrownDark, font mai mare
            TextButton(
                onClick = { vm.register() },
                enabled = !ui.loading
            ) {
                Text(
                    text = "Create account",
                    style = MaterialTheme.typography.titleMedium,
                    color = BrownDark
                )
            }

            ui.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
