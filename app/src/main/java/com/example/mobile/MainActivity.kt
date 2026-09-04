package com.example.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobile.data.WasteData
import com.example.mobile.model.WasteCategory
import com.example.mobile.ui.screens.HomeScreen
import com.example.mobile.ui.screens.LoginScreen
import com.example.mobile.ui.screens.ProfileScreen
import com.example.mobile.ui.screens.RecycleMapScreen
import com.example.mobile.ui.screens.RewardsScreen
import com.example.mobile.ui.screens.SignUpScreen
import com.example.mobile.ui.screens.TrackerScreen
import com.example.mobile.ui.screens.WasteDetailScreen
import com.example.mobile.ui.screens.WasteGuideScreen
import com.example.mobile.ui.theme.EcoGreen
import com.example.mobile.ui.theme.MobileTheme
import com.example.mobile.ui.viewmodel.AuthViewModel
import com.example.mobile.ui.viewmodel.UserViewModel
import com.example.mobile.ui.viewmodel.WasteGuideViewModel
import com.example.mobile.data.SupabaseProvider
import com.example.mobile.ui.screens.ResetPasswordScreen
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val openedFromPasswordReset =
            intent.data?.scheme == "ecopulse" &&
                    intent.data?.host == "reset-password"

        SupabaseProvider.client.handleDeeplinks(
            intent = intent,
            onError = { error ->
                error.printStackTrace()
            }
        )

        enableEdgeToEdge()

        setContent {
            MobileTheme(
                darkTheme = false,
                dynamicColor = false
            ) {
                EcoPulseApp(
                    startDestination =
                        if (openedFromPasswordReset) {
                            Routes.RESET_PASSWORD
                        } else {
                            Routes.LOGIN
                        }
                )
            }
        }
    }

    @Composable
    fun EcoPulseApp(
        startDestination: String = Routes.LOGIN,
        authViewModel: AuthViewModel = viewModel(),
        userViewModel: UserViewModel = viewModel(),
        wasteViewModel: WasteGuideViewModel = viewModel()
    ) {
        val navController = rememberNavController()
        var mapCategory by remember { mutableStateOf(WasteCategory.ALL) }
        val authState by authViewModel.auState.collectAsStateWithLifecycle()
        val user by userViewModel.userState.collectAsStateWithLifecycle()
        val wasteState by wasteViewModel.uiState.collectAsStateWithLifecycle()
        val ecoLogs by userViewModel.ecoLogs.collectAsStateWithLifecycle()
        val dailyChallengeCompleted by userViewModel.dailyChallengeCompleted.collectAsStateWithLifecycle()
        val currentEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentEntry?.destination?.route
        val bottomRoutes = Screen.entries.map { it.route }.toSet()
        val showBottomBar = currentRoute in bottomRoutes || currentRoute == Routes.MAP

        LaunchedEffect(authState.logoutSucceeded) {
            if (authState.logoutSucceeded) {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
                authViewModel.consumeLogoutSuccess()
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Routes.LOGIN) {
                    LaunchedEffect(authState.loginSucceeded) {
                        if (authState.loginSucceeded) {
                            userViewModel.updateName(authState.signedInName)
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                                launchSingleTop = true
                            }
                            authViewModel.consumeLoginSuccess()
                        }
                    }

                    LoginScreen(
                        uiState = authState,
                        isFormValid = authViewModel.isLoginFormValid(),
                        onEmailChange = authViewModel::onEmailChange,
                        onPasswordChange = authViewModel::onPasswordChange,
                        onTogglePasswordVisibility = authViewModel::togglePasswordVisibility,
                        onToggleRememberMe = authViewModel::toggleRememberMe,
                        onSignIn = authViewModel::signIn,
                        onForgotPassword = authViewModel::resetPassword,
                        onSignUpClick = { navController.navigate(Routes.SIGN_UP) }
                    )
                }

                composable(Routes.SIGN_UP) {
                    LaunchedEffect(authState.signUpSucceeded) {
                        if (authState.signUpSucceeded) {
                            authViewModel.consumeSignUpSuccess()
                            navController.popBackStack()
                        }
                    }

                    SignUpScreen(
                        uiState = authState,
                        isFormValid = authViewModel.isSignUpFormValid(),
                        onFullNameChange = authViewModel::onFullNameChange,
                        onEmailChange = authViewModel::onEmailChange,
                        onPasswordChange = authViewModel::onPasswordChange,
                        onConfirmPasswordChange = authViewModel::onConfirmPasswordChange,
                        onTogglePasswordVisibility = authViewModel::togglePasswordVisibility,
                        onSignUp = authViewModel::signUp,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.RESET_PASSWORD) {

                    LaunchedEffect(Unit) {
                        authViewModel.preparePasswordReset()
                    }

                    LaunchedEffect(authState.passwordResetSucceeded) {

                        if (authState.passwordResetSucceeded) {

                            authViewModel.consumePasswordResetSuccess()

                            navController.navigate(Routes.LOGIN) {

                                popUpTo(Routes.RESET_PASSWORD) {
                                    inclusive = true
                                }

                                launchSingleTop = true
                            }
                        }
                    }

                    ResetPasswordScreen(
                        uiState = authState,
                        isFormValid =
                            authViewModel.isPasswordResetFormValid(),
                        onPasswordChange =
                            authViewModel::onPasswordChange,
                        onConfirmPasswordChange =
                            authViewModel::onConfirmPasswordChange,
                        onTogglePasswordVisibility =
                            authViewModel::togglePasswordVisibility,
                        onUpdatePassword =
                            authViewModel::updatePassword,
                        onBackToLogin = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.RESET_PASSWORD) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        userName = user?.name ?: authState.signedInName,
                        points = user?.points ?: 1250,
                        challengeCompleted = dailyChallengeCompleted,
                        onCompleteChallenge = userViewModel::completeDailyChallenge,
                        onNavigateToWaste = { navController.navigate(Screen.Waste.route) },
                        onNavigateToMap = {
                            mapCategory = WasteCategory.ALL
                            navController.navigate(Routes.MAP)
                        },
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                        onNavigateToTracker = { navController.navigate(Screen.Tracker.route) },
                        onNavigateToRewards = { navController.navigate(Screen.Rewards.route) }
                    )
                }

                composable(Screen.Waste.route) {
                    WasteGuideScreen(
                        uiState = wasteState,
                        onQueryChange = wasteViewModel::onQueryChange,
                        onCategorySelected = wasteViewModel::onCategorySelected,
                        onClearFilters = wasteViewModel::clearFilters,
                        onWasteItemClick = { id -> navController.navigate(Routes.wasteDetail(id)) },
                        onLogDisposal = { item -> userViewModel.logDisposal(item.name) },
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                        onNavigateToMap = {
                            mapCategory = WasteCategory.ALL
                            navController.navigate(Routes.MAP)
                        },
                        onNavigateToMapForCategory = { category ->
                            mapCategory = category
                            navController.navigate(Routes.MAP)
                        }
                    )
                }

                composable(
                    route = Routes.WASTE_DETAIL,
                    arguments = listOf(
                        navArgument(Routes.WASTE_ID) { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val wasteId = backStackEntry.arguments?.getInt(Routes.WASTE_ID) ?: -1
                    WasteDetailScreen(
                        item = WasteData.getById(wasteId),
                        onLogDisposal = { item -> userViewModel.logDisposal(item.name) },
                        onNavigateToMap = { category ->
                            mapCategory = category
                            navController.navigate(Routes.MAP)
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Tracker.route) {
                    TrackerScreen(
                        logs = ecoLogs,
                        onLogActivity = userViewModel::logActivity,
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                    )
                }

                composable(Screen.Rewards.route) {
                    RewardsScreen(
                        points = user?.points ?: 1250,
                        onRedeemReward = userViewModel::redeemReward,
                        onNavigateToProfile = {
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(Screen.Rewards.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        userName = user?.name ?: authState.signedInName,
                        points = user?.points ?: 1250,
                        onUserNameChange = userViewModel::updateName,
                        onNavigateToRewards = { navController.navigate(Screen.Rewards.route) },
                        onLogout = authViewModel::signOut
                    )
                }

                composable(Routes.MAP) {
                    RecycleMapScreen(
                        initialCategory = mapCategory,
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                        onNavigateToWasteGuide = {
                            navController.navigate(Screen.Waste.route) {
                                popUpTo(Screen.Home.route)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavHostController) {
        val currentEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentEntry?.destination?.route

        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EcoGreen
        ) {
            Screen.entries.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                    label = { Text(screen.title) },
                    selected = currentRoute == screen.route ||
                            (currentRoute == Routes.MAP && screen == Screen.Waste),
                    onClick = {
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = screen != Screen.Home
                                }
                                launchSingleTop = true
                                restoreState = screen != Screen.Home
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EcoGreen,
                        selectedTextColor = EcoGreen,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = EcoGreen.copy(alpha = 0.1f)
                    )
                )
            }
        }
    }

    enum class Screen(val route: String, val title: String, val icon: ImageVector) {
        Home("home", "Home", Icons.Default.Home),
        Waste("waste", "Waste", Icons.Default.Delete),
        Tracker("tracker", "Tracker", Icons.Default.BarChart),
        Rewards("rewards", "Rewards", Icons.Default.Stars),
        Profile("profile", "Profile", Icons.Default.Person)
    }

    private object Routes {
        const val LOGIN = "login"
        const val SIGN_UP = "signUp"
        const val MAP = "map"
        const val WASTE_ID = "wasteId"
        const val WASTE_DETAIL = "wasteDetail/{$WASTE_ID}"

        const val RESET_PASSWORD = "resetPassword"

        fun wasteDetail(wasteId: Int): String = "wasteDetail/$wasteId"
    }
}
