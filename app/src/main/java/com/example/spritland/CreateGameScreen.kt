package com.example.spritland

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.spritland.data.api.ApiService
import com.example.spritland.data.model.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class PlayerForm(
    var user: UserListItemDto? = null,
    var spirit: SpiritListItemDto? = null,
    var aspect: AspectDto? = null,
    var aspects: List<AspectDto> = emptyList(),
    var notes: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGameScreen(
    api: ApiService,
    lifecycleScope: LifecycleCoroutineScope,
    onBack: () -> Unit,
    onGameCreated: () -> Unit
) {
    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }

    // поля gry
    var datePlayed by remember { mutableStateOf(LocalDate.now().toString()) } // yyyy-MM-dd
    var result by remember { mutableStateOf("win") }
    var difficulty by remember { mutableStateOf("") }
    var turns by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var blightedIsland by remember { mutableStateOf(false) }

    // dane do list
    var me by remember { mutableStateOf<UserDto?>(null) }
    var users by remember { mutableStateOf<List<UserListItemDto>>(emptyList()) }
    var scenarios by remember { mutableStateOf<List<ScenarioDto>>(emptyList()) }
    var adversaries by remember { mutableStateOf<List<AdversaryDto>>(emptyList()) }
    var spirits by remember { mutableStateOf<List<SpiritListItemDto>>(emptyList()) }

    // dropdown selected
    var selectedScenario by remember { mutableStateOf<ScenarioDto?>(null) }
    var selectedAdversary by remember { mutableStateOf<AdversaryDto?>(null) }

    // dropdown open states
    var resultOpen by remember { mutableStateOf(false) }
    var scenarioOpen by remember { mutableStateOf(false) }
    var adversaryOpen by remember { mutableStateOf(false) }

    // gracze
    val players = remember { mutableStateListOf<PlayerForm>() }

    // LOAD
    LaunchedEffect(Unit) {
        lifecycleScope.launch {
            try {
                loading = true
                status = null

                me = api.me()
                users = api.getUsers(null)
                scenarios = api.getScenarios()
                adversaries = api.getAdversaries()
                spirits = api.getSpirits()

                // domyślnie 1 gracz = Ty
                val meUser = users.firstOrNull { it.id == me?.id }
                players.clear()
                players.add(PlayerForm(user = meUser))

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "➕ Nowa gra",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text("← Wróć", modifier = Modifier.clickable { onBack() })
        }

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- BASIC ---
            OutlinedTextField(
                value = datePlayed,
                onValueChange = { datePlayed = it },
                label = { Text("Data gry (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth()
            )

            // wynik win/lose
            ExposedDropdownMenuBox(
                expanded = resultOpen,
                onExpandedChange = { resultOpen = !resultOpen }
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    value = result,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Wynik") }
                )
                ExposedDropdownMenu(
                    expanded = resultOpen,
                    onDismissRequest = { resultOpen = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("win") },
                        onClick = { result = "win"; resultOpen = false }
                    )
                    DropdownMenuItem(
                        text = { Text("lose") },
                        onClick = { result = "lose"; resultOpen = false }
                    )
                }
            }

            // scenariusz
            ExposedDropdownMenuBox(
                expanded = scenarioOpen,
                onExpandedChange = { scenarioOpen = !scenarioOpen }
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    value = selectedScenario?.name ?: "Brak",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Scenariusz") }
                )
                ExposedDropdownMenu(
                    expanded = scenarioOpen,
                    onDismissRequest = { scenarioOpen = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Brak") },
                        onClick = {
                            selectedScenario = null
                            scenarioOpen = false
                        }
                    )
                    scenarios.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s.name) },
                            onClick = {
                                selectedScenario = s
                                scenarioOpen = false
                            }
                        )
                    }
                }
            }

            // przeciwnik
            ExposedDropdownMenuBox(
                expanded = adversaryOpen,
                onExpandedChange = { adversaryOpen = !adversaryOpen }
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    value = selectedAdversary?.name ?: "Brak",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Przeciwnik") }
                )
                ExposedDropdownMenu(
                    expanded = adversaryOpen,
                    onDismissRequest = { adversaryOpen = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Brak") },
                        onClick = {
                            selectedAdversary = null
                            adversaryOpen = false
                        }
                    )
                    adversaries.forEach { a ->
                        DropdownMenuItem(
                            text = { Text(a.name) },
                            onClick = {
                                selectedAdversary = a
                                adversaryOpen = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = difficulty,
                onValueChange = { difficulty = it },
                label = { Text("Poziom trudności (np. 1-10)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = turns,
                onValueChange = { turns = it },
                label = { Text("Tury (opcjonalnie)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Komentarz (opcjonalnie)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = blightedIsland, onCheckedChange = { blightedIsland = it })
                Text("Blighted Island")
            }

            Divider()

            // --- PLAYERS HEADER ---
            Text(
                text = "Gracze",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { players.add(PlayerForm()) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("➕ Dodaj gracza")
            }

            // --- PLAYERS LIST ---
            players.forEachIndexed { index, p ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Gracz ${index + 1}",
                                fontWeight = FontWeight.Bold
                            )

                            if (index != 0) {
                                Text(
                                    text = "Usuń",
                                    modifier = Modifier.clickable { players.removeAt(index) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // USER dropdown
                        var userOpen by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = userOpen,
                            onExpandedChange = { userOpen = !userOpen }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                value = p.user?.username ?: "Wybierz gracza",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Użytkownik") }
                            )
                            ExposedDropdownMenu(
                                expanded = userOpen,
                                onDismissRequest = { userOpen = false }
                            ) {
                                users.forEach { u ->
                                    DropdownMenuItem(
                                        text = { Text(u.username) },
                                        onClick = {
                                            p.user = u
                                            userOpen = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // SPIRIT dropdown
                        var spiritOpen by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = spiritOpen,
                            onExpandedChange = { spiritOpen = !spiritOpen }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                value = p.spirit?.name ?: "Wybierz ducha",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Duch") }
                            )
                            ExposedDropdownMenu(
                                expanded = spiritOpen,
                                onDismissRequest = { spiritOpen = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Brak") },
                                    onClick = {
                                        p.spirit = null
                                        p.aspect = null
                                        p.aspects = emptyList()
                                        spiritOpen = false
                                    }
                                )

                                spirits.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text(s.name) },
                                        onClick = {
                                            p.spirit = s
                                            p.aspect = null
                                            spiritOpen = false

                                            lifecycleScope.launch {
                                                try {
                                                    val details = api.getSpiritById(s.id)
                                                    p.aspects = details.aspects
                                                } catch (_: Exception) {
                                                    p.aspects = emptyList()
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // ASPECT dropdown
                        var aspectOpen by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = aspectOpen,
                            onExpandedChange = { aspectOpen = !aspectOpen }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                value = p.aspect?.name ?: "Wybierz aspekt",
                                onValueChange = {},
                                readOnly = true,
                                enabled = p.spirit != null,
                                label = { Text("Aspekt") }
                            )
                            ExposedDropdownMenu(
                                expanded = aspectOpen,
                                onDismissRequest = { aspectOpen = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Brak") },
                                    onClick = {
                                        p.aspect = null
                                        aspectOpen = false
                                    }
                                )

                                p.aspects.forEach { a ->
                                    DropdownMenuItem(
                                        text = { Text(a.name) },
                                        onClick = {
                                            p.aspect = a
                                            aspectOpen = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = p.notes,
                            onValueChange = { p.notes = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Notatki (opcjonalnie)") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // --- SAVE BUTTON ---
            Button(
                enabled = !saving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                onClick = {
                    lifecycleScope.launch {
                        try {
                            saving = true
                            status = "Zapisywanie..."

                            val user = me ?: throw Exception("Brak danych użytkownika")

                            val req = CreateGameRequest(
                                creatorUserId = user.id,
                                datePlayed = "${datePlayed}T00:00:00Z",
                                adversaryId = selectedAdversary?.id,
                                scenarioId = selectedScenario?.id,
                                difficulty = difficulty.toIntOrNull(),
                                boardSetup = null,
                                result = result,
                                endReason = null,
                                turns = turns.toIntOrNull(),
                                comment = comment.takeIf { it.isNotBlank() },
                                blightedIsland = blightedIsland,
                                players = players.mapNotNull { p ->
                                    val u = p.user ?: return@mapNotNull null
                                    CreateGamePlayerRequest(
                                        userId = u.id,
                                        spiritId = p.spirit?.id,
                                        aspectId = p.aspect?.id,
                                        notes = p.notes.takeIf { it.isNotBlank() }
                                    )
                                }
                            )

                            api.createGame(req)

                            status = "✅ Dodano grę!"
                            onGameCreated()

                        } catch (e: Exception) {
                            status = "❌ Błąd: ${e.message}"
                            e.printStackTrace()
                        } finally {
                            saving = false
                        }
                    }
                }
            ) {
                Text(if (saving) "Zapisywanie..." else "Zapisz grę")
            }

            if (status != null) {
                Text(status!!)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
