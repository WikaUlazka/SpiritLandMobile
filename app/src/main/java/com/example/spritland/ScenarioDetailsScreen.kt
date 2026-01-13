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
import com.example.spritland.data.model.ScenarioDto
import kotlinx.coroutines.launch

@Composable
fun ScenarioDetailsScreen(
    scenarioId: Int,
    api: ApiService,
    lifecycleScope: LifecycleCoroutineScope,
    onBack: () -> Unit
) {
    var scenario by remember { mutableStateOf<ScenarioDto?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val backendBaseUrl = "http://192.168.0.222:5288"

    LaunchedEffect(scenarioId) {
        lifecycleScope.launch {
            try {
                loading = true
                error = null
                scenario = api.getScenarioById(scenarioId)
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
                text = "📜 Szczegóły scenariusza",
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
                Text("❌ Błąd: $error", modifier = Modifier.padding(16.dp))
            }

            scenario == null -> {
                Text("Nie znaleziono scenariusza", modifier = Modifier.padding(16.dp))
            }

            else -> {
                val s = scenario!!

                val imageUrl = s.imageUrl.let { url ->
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

                    item {
                        Text(
                            text = "Trudność: ${s.difficulty}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (s.imageUrl.isNotBlank()) {
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
                                        .height(260.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = s.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
