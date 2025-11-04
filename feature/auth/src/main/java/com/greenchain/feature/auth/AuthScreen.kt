package com.greenchain.feature.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun AuthScreen(
    onSuccess: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()

    if (ui.success) {
        // navigăm imediat în app
        LaunchedEffect(Unit) {
            Firebase.auth.currentUser?.let { user ->
                vm.onLoginSuccess(user)
            }
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign in to GreenChain", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = ui.email, onValueChange = vm::onEmail,
            label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = ui.password, onValueChange = vm::onPassword,
            label = { Text("Password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { vm.login() },
            enabled = !ui.loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (ui.loading) "Signing in..." else "Sign In") }

        TextButton(
            onClick = { vm.register() },
            enabled = !ui.loading
        ) { Text("Create account") }

        ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
