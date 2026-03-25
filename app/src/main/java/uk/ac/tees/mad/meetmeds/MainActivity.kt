package uk.ac.tees.mad.meetmeds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.meetmeds.presentation.auth.AuthViewModel
import uk.ac.tees.mad.meetmeds.presentation.navigation.NavGraph
import uk.ac.tees.mad.meetmeds.presentation.theme.MeetMedsTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Inject the ViewModel to access the splash loading state
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen().apply {
//            setOnExitAnimationListener { screen->
//                screen.iconView.animate()
//                    .alpha(0f)
//                    .setDuration(1500)
//                    .withEndAction {
//                        screen.remove()
//                    }.start()
//            }
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