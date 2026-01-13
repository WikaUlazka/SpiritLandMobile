package com.example.spritland

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spritland.data.api.ApiClient
import com.example.spritland.data.auth.TokenManager
import com.example.spritland.data.model.LoginRequest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = ApiClient.create(applicationContext)
        val tokenManager = TokenManager(applicationContext)

        setContent {
            val navController = rememberNavController()

            val startDestination =
                if (!tokenManager.getToken().isNullOrBlank()) "home" else "login"

            NavHost(navController = navController, startDestination = startDestination) {

                // ✅ LOGIN
                composable("login") {
                    LoginScreen(
                        onLogin = { email, password, setStatus ->
                            lifecycleScope.launch {
                                try {
                                    setStatus("Logowanie...")

                                    val login = api.login(LoginRequest(email, password))
                                    tokenManager.saveToken(login.token)

                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } catch (e: Exception) {
                                    setStatus("❌ Błąd: ${e.message}")
                                    e.printStackTrace()
                                }
                            }
                        },
                        onOpenRegister = {
                            navController.navigate("register")
                        }
                    )
                }

                // ✅ REGISTER
                composable("register") {
                    RegisterScreen(
                        api = api,
                        lifecycleScope = lifecycleScope,
                        onBackToLogin = { navController.popBackStack() }
                    )
                }

                // ✅ HOME
                composable("home") {
                    HomeScreen(
                        tokenManager = tokenManager,
                        onLogout = {
                            tokenManager.clear()
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onOpenSpirits = {
                            navController.navigate("spirits")
                        },
                        onOpenScenarios = {
                            navController.navigate("scenarios")
                        },
                        onOpenAdversaries = {
                            navController.navigate("adversaries")
                        },
                        onOpenProfile = {
                            navController.navigate("profile")
                        },
                        onOpenCreateGame = {
                            navController.navigate("createGame")
                        },
                        onOpenRandomizer = {
                            navController.navigate("randomizer")
                        },
                        onOpenMyGames = {
                            navController.navigate("myGames")
                        }
                    )
                }

                // ✅ SPIRITS LIST
                composable("spirits") {
                    SpiritsScreen(
                        api = api,
                        lifecycleScope = lifecycleScope,
                        onBack = { navController.popBackStack() },
                        onOpenSpirit = { id ->
                            navController.navigate("spiritDetails/$id")
                        }
                    )
                }

                // ✅ SPIRIT DETAILS
                composable("spiritDetails/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0

                    SpiritDetailsScreen(
                        spiritId = id,
                        api = api,
                        lifecycleScope = lifecycleScope,
                        onBack = { navController.popBackStack() }
                    )
                }

                // ✅ SCENARIOS LIST
                composable("scenarios") {
                    ScenariosScreen(
                        api = api,
                        lifecycleScope = lifecycleScope,
                        onBack = { navController.popBackStack() },
                        onOpenScenario = { id ->
                            navController.navigate("scenarioDetails/$id")
                        }
                    )
                }

                // ✅ SCENARIO DETAILS
                composable("scenarioDetails/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0

                    ScenarioDetailsScreen(
                        scenarioId = id,
                        api = api,
                        lifecycleScope = lifecycleScope,
                        onBack = { navController.popBackStack() }
                    )
                }

                // ✅ ADVERSARIES LIST
                composable("adversaries") {
                    AdversariesScreen(
                        api = api,
                        lifecycleScope = lifecycleScope,
                        onBack = { navController.popBackStack() },
                        onOpenAdversary = { id ->
                            navController.navigate("adversaryDetails/$id")
                        }
                    )
                }

                // ✅ ADVERSARY DETAILS
                composable("adversaryDetails/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0

                    AdversaryDetailsScreen(
                        adversaryId = id,
                        api = api,
                        lifecycleScope = lifecycleScope,
                        onBack = { navController.popBackStack() }
                    )
                }

                // ✅ PROFILE
                composable("profile") {
                    ProfileScreen(
                        api = api,
                        lifecycleScope = lifecycleScope,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("createGame") {
                    CreateGameScreen(
                        api = api,
                        lifecycleScope = lifecycleScope,
                        onBack = { navController.popBackStack() },
                        onGameCreated = {
                            navController.navigate("home") {
                                popUpTo("createGame") { inclusive = true }
                            }
                        }
                    )
                }

                composable("randomizer") {
                    RandomizerScreen(
                        api = api,
                        lifecycleScope = lifecycleScope,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("myGames") {
                    MyGamesScreen(
                        api = api,
                        lifecycleScope = lifecycleScope,
                        onBack = { navController.popBackStack() },
                        onOpenGame = { id ->
                            navController.navigate("gameDetails/$id")
                        }
                    )
                }

                composable("gameDetails/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0

                    GameDetailsScreen(
                        gameId = id,
                        api = api,
                        lifecycleScope = lifecycleScope,
                        onBack = { navController.popBackStack() },
                        onDeleted = {
                            navController.popBackStack() // wróć do listy
                        }
                    )
                }
            }
        }
    }
}
