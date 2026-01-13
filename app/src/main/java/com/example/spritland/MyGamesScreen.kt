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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.spritland.data.api.ApiService
import com.example.spritland.data.model.MyGameListItemDto
import kotlinx.coroutines.launch

@Composable
fun MyGamesScreen(
    api: ApiService,
    lifecycleScope: LifecycleCoroutineScope,
    onBack: () -> Unit,
    onOpenGame: (Int) -> Unit
) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var games by remember { mutableStateOf<List<MyGameListItemDto>>(emptyList()) }

    fun load() {
        lifecycleScope.launch {
            try {
                loading = true
                error = null
                games = api.getMyGames()
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

        // top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "🎮 Moje gry",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text("← Wróć", modifier = Modifier.clickable { onBack() })
        }

        when {
            loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            error != null -> Column(modifier = Modifier.padding(16.dp)) {
                Text("❌ Błąd: $error")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { load() }) { Text("Spróbuj ponownie") }
            }

            games.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Brak zapisanych gier 🙂")
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(games) { g ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenGame(g.id) },
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📅 ${g.datePlayed.take(10)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Wynik: ${g.result}")
                        }
                    }
                }
            }
        }
    }
}
