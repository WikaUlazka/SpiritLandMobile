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
import com.example.spritland.data.model.*
import kotlinx.coroutines.launch

@Composable
fun GameDetailsScreen(
    gameId: Int,
    api: ApiService,
    lifecycleScope: LifecycleCoroutineScope,
    onBack: () -> Unit,
    onDeleted: () -> Unit
) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    var game by remember { mutableStateOf<GameDetailsDto?>(null) }

    // dodatkowe dane do nazw
    var users by remember { mutableStateOf<List<UserListItemDto>>(emptyList()) }
    var spirits by remember { mutableStateOf<List<SpiritListItemDto>>(emptyList()) }
    var spiritDetailsCache by remember { mutableStateOf<Map<Int, SpiritDetailsDto>>(emptyMap()) }

    var scenario by remember { mutableStateOf<ScenarioDto?>(null) }
    var adversary by remember { mutableStateOf<AdversaryDto?>(null) }

    var deleting by remember { mutableStateOf(false) }

    val backendBaseUrl = "http://192.168.0.222:5288"

    fun makeAbsoluteUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        return when {
            url.startsWith("http") -> url
            url.startsWith("/") -> backendBaseUrl + url
            else -> "$backendBaseUrl/$url"
        }
    }

    fun load() {
        lifecycleScope.launch {
            try {
                loading = true
                error = null

                // 1) gra
                val g = api.getGame(gameId)
                game = g

                // 2) listy do mapowania ID->name
                users = api.getUsers(null)
                spirits = api.getSpirits()

                // 3) scenariusz/przeciwnik (jeśli istnieją)
                scenario = g.scenarioId?.let { api.getScenarioById(it) }
                adversary = g.adversaryId?.let { api.getAdversaryById(it) }

                // 4) aspekty (pobierz szczegóły duchów użytych w grze)
                val spiritIdsInGame = g.players.mapNotNull { it.spiritId }.distinct()
                val map = mutableMapOf<Int, SpiritDetailsDto>()

                for (sid in spiritIdsInGame) {
                    try {
                        map[sid] = api.getSpiritById(sid)
                    } catch (_: Exception) { }
                }

                spiritDetailsCache = map

            } catch (e: Exception) {
                error = e.message
                e.printStackTrace()
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { load() }

    Column(modifier = Modifier.fillMaxSize()) {

        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "📄 Szczegóły gry",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text("← Wróć", modifier = Modifier.clickable { onBack() })
        }

        when {
            loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            error != null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("❌ Błąd: $error")
            }

            game == null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Brak danych")
            }

            else -> {
                val g = game!!

                fun username(userId: Int): String =
                    users.firstOrNull { it.id == userId }?.username ?: "User#$userId"

                fun spiritName(spiritId: Int?): String =
                    spiritId?.let { spirits.firstOrNull { s -> s.id == it }?.name } ?: "Brak"

                fun aspectName(spiritId: Int?, aspectId: Int?): String {
                    if (spiritId == null || aspectId == null) return "Brak"
                    val details = spiritDetailsCache[spiritId] ?: return "Aspekt#$aspectId"
                    return details.aspects.firstOrNull { it.id == aspectId }?.name ?: "Aspekt#$aspectId"
                }

                // ✅ SCROLL DLA CAŁEGO WIDOKU
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 28.dp)
                ) {

                    // GŁÓWNE INFO
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("📅 ${g.datePlayed.take(10)}", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Wynik: ${g.result}")
                                Text("Trudność: ${g.difficulty ?: "-"}")
                                Text("Tury: ${g.turns ?: "-"}")
                                Text("Blighted Island: ${if (g.blightedIsland) "TAK" else "NIE"}")

                                if (!g.comment.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Komentarz:", fontWeight = FontWeight.Bold)
                                    Text(g.comment!!)
                                }
                            }
                        }
                    }

                    // PRZECIWNIK
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("⚔️ Przeciwnik", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                if (adversary == null) {
                                    Text("Brak")
                                } else {
                                    Text(adversary!!.name, style = MaterialTheme.typography.titleLarge)

                                    val img = makeAbsoluteUrl(adversary!!.imageUrl)
                                    if (!img.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        AsyncImage(
                                            model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                                                .data(img)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Adversary image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(220.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // SCENARIUSZ
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("📜 Scenariusz", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                if (scenario == null) {
                                    Text("Brak")
                                } else {
                                    Text(scenario!!.name, style = MaterialTheme.typography.titleLarge)

                                    val img = makeAbsoluteUrl(scenario!!.imageUrl)
                                    if (!img.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        AsyncImage(
                                            model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                                                .data(img)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Scenario image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(220.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // GRACZE
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("👥 Gracze", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))

                                g.players.forEachIndexed { idx, p ->
                                    val u = username(p.userId)
                                    val sp = spiritName(p.spiritId)
                                    val asp = aspectName(p.spiritId, p.aspectId)

                                    Text("• ${idx + 1}. $u", fontWeight = FontWeight.Bold)
                                    Text("   Duch: $sp")
                                    Text("   Aspekt: $asp")

                                    if (!p.notes.isNullOrBlank()) {
                                        Text("   Notatki: ${p.notes}")
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }

                    // DELETE
                    item {
                        Button(
                            enabled = !deleting,
                            onClick = {
                                lifecycleScope.launch {
                                    try {
                                        deleting = true
                                        api.deleteGame(gameId)
                                        onDeleted()
                                    } catch (e: Exception) {
                                        error = e.message
                                        e.printStackTrace()
                                    } finally {
                                        deleting = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text(if (deleting) "Usuwanie..." else "🗑️ Usuń grę")
                        }
                    }
                }
            }
        }
    }
}
