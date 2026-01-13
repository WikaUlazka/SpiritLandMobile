package com.example.spritland

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.spritland.data.api.ApiService
import com.example.spritland.data.model.SpiritDetailsDto
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color

@Composable
fun SpiritDetailsScreen(
    spiritId: Int,
    api: ApiService,
    lifecycleScope: LifecycleCoroutineScope,
    onBack: () -> Unit
) {
    var spirit by remember { mutableStateOf<SpiritDetailsDto?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val backendBaseUrl = "http://192.168.0.222:5288"

    LaunchedEffect(spiritId) {
        lifecycleScope.launch {
            try {
                loading = true
                error = null
                spirit = api.getSpiritById(spiritId)
            } catch (e: Exception) {
                error = e.message
            } finally {
                loading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // TOP
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "👻 Szczegóły ducha",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "← Wróć",
                modifier = Modifier.clickable { onBack() }
            )
        }

        when {
            loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Text(
                    text = "❌ Błąd: $error",
                    modifier = Modifier.padding(16.dp)
                )
            }

            spirit == null -> {
                Text(
                    text = "Nie znaleziono ducha",
                    modifier = Modifier.padding(16.dp)
                )
            }

            else -> {
                val s = spirit!!

                val imageUrl = s.imageUrl?.let { url ->
                    when {
                        url.startsWith("http") -> url
                        url.startsWith("/") -> backendBaseUrl + url
                        else -> "$backendBaseUrl/$url"
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    item {
                        Text(
                            text = s.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!imageUrl.isNullOrBlank()) {
                        item {
                            Card(shape = RoundedCornerShape(18.dp)) {
                                AsyncImage(
                                    model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                                        .data(imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = s.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Złożoność: ${s.complexity}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (!s.description.isNullOrBlank()) {
                        item {
                            Text(
                                text = s.description ?: "",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Aspekty",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(s.aspects) { a ->

                        val aspectImageUrl = a.imageUrl?.let { url ->
                            when {
                                url.startsWith("http") -> url
                                url.startsWith("/") -> backendBaseUrl + url
                                else -> "$backendBaseUrl/$url"
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(22.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                // ✅ NAZWA NAD ZDJĘCIEM
                                Text(
                                    text = a.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp)
                                )

                                // ✅ DUŻY OBRAZEK
                                if (!aspectImageUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                                            .data(aspectImageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = a.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(320.dp), // 🔥 DUŻE I CZYTELNE
                                        contentScale = ContentScale.Fit // ✅ żeby było czytelne (nie ucina)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Brak obrazka")
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }


                }
            }
        }
    }
}
