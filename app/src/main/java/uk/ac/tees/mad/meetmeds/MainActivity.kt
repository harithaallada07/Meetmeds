package uk.ac.tees.mad.meetmeds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import uk.ac.tees.mad.meetmeds.presentation.navigation.NavGraph
import uk.ac.tees.mad.meetmeds.presentation.theme.MeetMedsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen().apply {
            setOnExitAnimationListener { screen->
                screen.iconView.animate()
                    .alpha(0f)
                    .setDuration(3000)
                    .withEndAction {
                        screen.remove()
                    }.start()
            }
//            setKeepOnScreenCondition {
//
//            }
        }
        setContent {
            MeetMedsTheme(dynamicColor = false) {
                NavGraph()
            }
        }
    }
}