package com.example.spritland

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spritland.data.auth.TokenManager

@Composable
fun HomeScreen(
    tokenManager: TokenManager,
    onLogout: () -> Unit,
    onOpenSpirits: () -> Unit,
    onOpenScenarios: () -> Unit,
    onOpenAdversaries: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenCreateGame: () -> Unit,
    onOpenRandomizer: () -> Unit,
    onOpenMyGames: () -> Unit
) {
    val username = "Wiktoria" // później podepniemy z /me

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7FB))
    ) {
        TopBar(onLogout = onLogout)

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "👋 Witaj, $username!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3D2C8D)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Gotowy na kolejną przygodę?",
                fontSize = 16.sp,
                color = Color(0xFF555555)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ✅ RZĄD 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickCard(title = "🎮 Moje gry", modifier = Modifier.weight(1f)) { onOpenMyGames() }
                QuickCard(title = "➕ Nowa gra", modifier = Modifier.weight(1f)) { onOpenCreateGame() }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ RZĄD 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickCard(title = "👻 Duchy", modifier = Modifier.weight(1f)) { onOpenSpirits() }
                QuickCard(title = "📜 Scenariusze", modifier = Modifier.weight(1f)) { onOpenScenarios() }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ RZĄD 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickCard(title = "⚔️ Przeciwnicy", modifier = Modifier.weight(1f)) { onOpenAdversaries() }
                QuickCard(title = "👤 Profil", modifier = Modifier.weight(1f)) { onOpenProfile() }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ RZĄD 4 (Losowanie + Wyloguj) <- TO JEST POPRAWIONE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickCard(
                    title = "🎲 Losowanie",
                    modifier = Modifier.weight(1f)
                ) { onOpenRandomizer() }

                QuickCard(
                    title = "📄 Wyloguj",
                    modifier = Modifier.weight(1f)
                ) { onLogout() }
            }
        }
    }
}

@Composable
private fun TopBar(onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4E39D4))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Spiritland 🎲",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Wyloguj",
            color = Color.White,
            modifier = Modifier.clickable { onLogout() }
        )
    }
}

@Composable
private fun QuickCard(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(70.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3D2C8D)
            )
        }
    }
}
