package com.example.spritland

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.spritland.data.api.ApiService
import com.example.spritland.data.model.RegisterRequest
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    api: ApiService,
    lifecycleScope: LifecycleCoroutineScope,
    onBackToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var status by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Rejestracja",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nazwa użytkownika") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Hasło") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                lifecycleScope.launch {
                    try {
                        loading = true
                        status = null

                        api.register(
                            RegisterRequest(
                                username = username.trim(),
                                email = email.trim(),
                                password = password
                            )
                        )

                        status = "✅ Konto utworzone! Zaloguj się."
                        onBackToLogin()

                    } catch (e: Exception) {
                        status = "❌ Błąd: ${e.message}"
                        e.printStackTrace()
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(if (loading) "Tworzenie..." else "Zarejestruj")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Masz już konto? Zaloguj się",
            modifier = Modifier.clickable { onBackToLogin() }
        )

        if (status != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(status!!)
        }
    }
}
