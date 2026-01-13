package com.example.spritland

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.spritland.data.model.AdversaryDto
import kotlinx.coroutines.launch

@Composable
fun AdversaryDetailsScreen(
    adversaryId: Int,
    api: ApiService,
    lifecycleScope: LifecycleCoroutineScope,
    onBack: () -> Unit
) {
    var adversary by remember { mutableStateOf<AdversaryDto?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val backendBaseUrl = "http://192.168.0.222:5288"

    LaunchedEffect(adversaryId) {
        lifecycleScope.launch {
            try {
                loading = true
                error = null
                adversary = api.getAdversaryById(adversaryId)
            } catch (e: Exception) {
                error = e.message
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
                text = "⚔️ Przeciwnik",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text("← Wróć", modifier = Modifier.clickable { onBack() })
        }

        when {
            loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Text("❌ Błąd: $error", modifier = Modifier.padding(16.dp))
            }

            adversary == null -> {
                Text("Nie znaleziono przeciwnika", modifier = Modifier.padding(16.dp))
            }

            else -> {
                val a = adversary!!

                val imageUrl = a.imageUrl?.let { url ->
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
                            text = a.name,
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
                                    contentDescription = a.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(360.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
