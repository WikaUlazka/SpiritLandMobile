package com.example.spritland

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.spritland.data.api.ApiService
import com.example.spritland.data.model.SpiritListItemDto
import com.example.spritland.data.model.UpdateProfileRequest
import com.example.spritland.data.model.UserDto
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    api: ApiService,
    lifecycleScope: LifecycleCoroutineScope,
    onBack: () -> Unit
) {
    var me by remember { mutableStateOf<UserDto?>(null) }
    var spirits by remember { mutableStateOf<List<SpiritListItemDto>>(emptyList()) }

    var username by remember { mutableStateOf("") }
    var favoriteSpiritId by remember { mutableStateOf<Int?>(null) }
    var favoriteAspectId by remember { mutableStateOf<Int?>(null) }

    var loading by remember { mutableStateOf(true) }
    var status by remember { mutableStateOf<String?>(null) }

    // dropdown states
    var spiritDropdownOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        lifecycleScope.launch {
            try {
                loading = true

                // 1) user
                val user = api.me()
                me = user
                username = user.username
                favoriteSpiritId = user.favoriteSpiritId
                favoriteAspectId = user.favoriteAspectId

                // 2) duchy do dropdown
                spirits = api.getSpirits()

            } catch (e: Exception) {
                status = "❌ Błąd: ${e.message}"
                e.printStackTrace()
            } finally {
                loading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "👤 Profil",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "← Wróć",
                modifier = Modifier.clickable { onBack() }
            )
        }

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // INFO
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Dane konta",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Email: ${me?.email ?: "-"}")
                        Text("Utworzono: ${me?.createdAt ?: "-"}")
                    }
                }
            }

            // USERNAME
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ustawienia profilu",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Nazwa użytkownika") }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // FAVORITE SPIRIT
                        ExposedDropdownMenuBox(
                            expanded = spiritDropdownOpen,
                            onExpandedChange = { spiritDropdownOpen = !spiritDropdownOpen }
                        ) {
                            val selectedSpiritName =
                                spirits.firstOrNull { it.id == favoriteSpiritId }?.name ?: "Brak"

                            OutlinedTextField(
                                readOnly = true,
                                value = selectedSpiritName,
                                onValueChange = {},
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                label = { Text("Ulubiony duch") }
                            )

                            ExposedDropdownMenu(
                                expanded = spiritDropdownOpen,
                                onDismissRequest = { spiritDropdownOpen = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Brak") },
                                    onClick = {
                                        favoriteSpiritId = null
                                        favoriteAspectId = null
                                        spiritDropdownOpen = false
                                    }
                                )

                                spirits.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text(s.name) },
                                        onClick = {
                                            favoriteSpiritId = s.id
                                            favoriteAspectId = null // reset aspektu
                                            spiritDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                lifecycleScope.launch {
                                    try {
                                        status = "Zapisywanie..."

                                        api.updateProfile(
                                            UpdateProfileRequest(
                                                username = username,
                                                favoriteSpiritId = favoriteSpiritId,
                                                favoriteAspectId = favoriteAspectId
                                            )
                                        )

                                        status = "✅ Zapisano profil!"
                                    } catch (e: Exception) {
                                        status = "❌ Błąd zapisu: ${e.message}"
                                        e.printStackTrace()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Zapisz zmiany")
                        }

                        if (status != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(status!!)
                        }
                    }
                }
            }
        }
    }
}
