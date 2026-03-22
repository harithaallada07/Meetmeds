package uk.ac.tees.mad.meetmeds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import uk.ac.tees.mad.meetmeds.presentation.auth.AuthViewModel
import uk.ac.tees.mad.meetmeds.presentation.navigation.NavGraph
import uk.ac.tees.mad.meetmeds.presentation.theme.MeetMedsTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AuthViewModel by viewModels {
        val container = (application as MeetMedsApplication).container
        AuthViewModel.Factory(container.authRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
        setContent {
            MeetMedsTheme(dynamicColor = false) {
                NavGraph(viewModel.startDestination.value)
            }
        }
    }
}