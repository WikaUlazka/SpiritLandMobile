package com.example.spritland

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
import com.example.spritland.data.model.ScenarioDto
import kotlinx.coroutines.launch

@Composable
fun ScenariosScreen(
    api: ApiService,
    lifecycleScope: LifecycleCoroutineScope,
    onBack: () -> Unit,
    onOpenScenario: (Int) -> Unit
) {
    var scenarios by remember { mutableStateOf<List<ScenarioDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val backendBaseUrl = "http://192.168.0.222:5288"

    LaunchedEffect(Unit) {
        lifecycleScope.launch {
            try {
                loading = true
                error = null
                scenarios = api.getScenarios()
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
                text = "📜 Scenariusze",
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

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(scenarios) { s ->

                        val imageUrl = s.imageUrl.let { url ->
                            when {
                                url.startsWith("http") -> url
                                url.startsWith("/") -> backendBaseUrl + url
                                else -> "$backendBaseUrl/$url"
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenScenario(s.id) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Text(
                                    text = s.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Trudność: ${s.difficulty}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                if (s.imageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                                            .data(imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = s.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
