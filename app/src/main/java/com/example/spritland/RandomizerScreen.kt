package com.example.spritland

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.spritland.data.api.ApiService
import com.example.spritland.data.model.*
import com.example.spritland.sensor.ShakeEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class RandomType {
    SPIRIT, SCENARIO, ADVERSARY
}

data class RandomHistoryItem(
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomizerScreen(
    api: ApiService,
    lifecycleScope: LifecycleCoroutineScope,
    onBack: () -> Unit
) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    var randomType by remember { mutableStateOf(RandomType.SPIRIT) }

    var resultTitle by remember { mutableStateOf("Potrząśnij telefonem 🎲") }
    var resultImageUrl by remember { mutableStateOf<String?>(null) }

    var isRolling by remember { mutableStateOf(false) }

    var spirits by remember { mutableStateOf<List<SpiritListItemDto>>(emptyList()) }
    var scenarios by remember { mutableStateOf<List<ScenarioDto>>(emptyList()) }
    var adversaries by remember { mutableStateOf<List<AdversaryDto>>(emptyList()) }

    // historia
    val history = remember { mutableStateListOf<RandomHistoryItem>() }

    val backendBaseUrl = "http://192.168.0.222:5288"

    // animacja obracania kości podczas losowania
    val infiniteTransition = rememberInfiniteTransition(label = "dice")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dice_rotation"
    )

    fun makeAbsoluteUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        return when {
            url.startsWith("http") -> url
            url.startsWith("/") -> backendBaseUrl + url
            else -> "$backendBaseUrl/$url"
        }
    }

    fun addToHistory(title: String) {
        history.add(0, RandomHistoryItem(title))
        if (history.size > 10) history.removeLast()
    }

    fun performRoll() {
        if (isRolling) return

        lifecycleScope.launch {
            isRolling = true
            resultTitle = "🎲 Losowanie..."
            resultImageUrl = null

            // małe opóźnienie aby animacja miała sens
            delay(700)

            when (randomType) {
                RandomType.SPIRIT -> {
                    val chosen = spirits.randomOrNull()
                    if (chosen == null) {
                        resultTitle = "Brak duchów w bazie"
                        resultImageUrl = null
                    } else {
                        resultTitle = "👻 Duch: ${chosen.name}"
                        resultImageUrl = makeAbsoluteUrl(chosen.imageUrl)
                        addToHistory("👻 ${chosen.name}")
                    }
                }

                RandomType.SCENARIO -> {
                    val chosen = scenarios.randomOrNull()
                    if (chosen == null) {
                        resultTitle = "Brak scenariuszy w bazie"
                        resultImageUrl = null
                    } else {
                        resultTitle = "📜 Scenariusz: ${chosen.name}"
                        resultImageUrl = makeAbsoluteUrl(chosen.imageUrl)
                        addToHistory("📜 ${chosen.name}")
                    }
                }

                RandomType.ADVERSARY -> {
                    val chosen = adversaries.randomOrNull()
                    if (chosen == null) {
                        resultTitle = "Brak przeciwników w bazie"
                        resultImageUrl = null
                    } else {
                        resultTitle = "⚔️ Przeciwnik: ${chosen.name}"
                        resultImageUrl = makeAbsoluteUrl(chosen.imageUrl)
                        addToHistory("⚔️ ${chosen.name}")
                    }
                }
            }

            isRolling = false
        }
    }

    // pobranie list
    LaunchedEffect(Unit) {
        lifecycleScope.launch {
            try {
                loading = true
                error = null

                spirits = api.getSpirits()
                scenarios = api.getScenarios()
                adversaries = api.getAdversaries()

            } catch (e: Exception) {
                error = e.message
                e.printStackTrace()
            } finally {
                loading = false
            }
        }
    }

    // SHAKE
    ShakeEffect(
        enabled = !loading && error == null,
        onShake = { performRoll() }
    )

    Column(modifier = Modifier.fillMaxSize()) {

        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎲 Losowanie",
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

        if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("❌ Błąd: $error")
            }
            return
        }

        // ✅ JEDEN wspólny scroll dla wszystkiego
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(6.dp))
            }

            // wybór typu
            item {
                Text(
                    text = "Co chcesz losować?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = randomType == RandomType.SPIRIT,
                                onClick = { randomType = RandomType.SPIRIT }
                            )
                            Text("Duch")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = randomType == RandomType.SCENARIO,
                                onClick = { randomType = RandomType.SCENARIO }
                            )
                            Text("Scenariusz")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = randomType == RandomType.ADVERSARY,
                                onClick = { randomType = RandomType.ADVERSARY }
                            )
                            Text("Przeciwnik")
                        }
                    }
                }
            }

            // wynik + animacja
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎲",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.rotate(if (isRolling) rotation else 0f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = resultTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        if (!resultImageUrl.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            AsyncImage(
                                model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                                    .data(resultImageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Random image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            // przycisk ręczny
            item {
                Button(
                    onClick = { performRoll() },
                    enabled = !isRolling,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(if (isRolling) "Losowanie..." else "Losuj")
                }
            }

            item {
                Text(
                    text = "📳 Potrząśnij telefonem aby losować!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            item { Divider() }

            // historia
            item {
                Text(
                    text = "Historia losowań",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (history.isEmpty()) {
                item { Text("Brak losowań jeszcze 🙂") }
            } else {
                items(history) { h ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = h.title,
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
