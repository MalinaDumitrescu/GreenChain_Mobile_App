package com.greenchain.feature.setup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun SetupScreen(
    onSuccess: () -> Unit,
    vm: SetupViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { vm.onPhotoUriSelected(it) } }
    )

    if (ui.success) {
        LaunchedEffect(ui.success) {
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
        Text("Setup your GreenChain Profile", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        val defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/greenchain-1134d.firebasestorage.app/o/avatar_greenchain.jpg?alt=media&token=eb85f1c9-1db7-4d7b-9e3e-491f09cf5ddb"
        val imageUri = ui.photoUri ?: defaultImageUrl

        Image(
            painter = rememberAsyncImagePainter(model = imageUri),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(150

                    .dp)
                .clip(CircleShape)
                .border(1.dp, Color.Gray, CircleShape)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = ui.name, onValueChange = vm::onName,
            label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = ui.username, onValueChange = vm::onUsername,
            label = { Text("Username") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = ui.description, onValueChange = vm::onDescription,
            label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(120.dp)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { vm.save() },
            enabled = !ui.loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (ui.loading) "Saving..." else "Save Profile") }

        ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
