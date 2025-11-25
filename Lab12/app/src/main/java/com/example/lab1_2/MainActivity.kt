package com.example.lab1_2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab1_2.ui.theme.Lab12Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab12Theme {
                AppNav()
            }
        }
    }
}

@Composable
fun AppNav() {
    val navController = rememberNavController()

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("NAV", "Открыт экран: ${destination.route}")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "A",
            modifier = Modifier.padding(padding)
        ) {
            composable("A") {
                ScreenA(onNext = { navController.navigate("B") })
            }
            composable("B") {
                ScreenB(onNext = { navController.navigate("C") })
            }
            composable("C") {
                ScreenC(
                    onNext = {
                        navController.navigate("A") {
                            popUpTo("A") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ScreenA(onNext: () -> Unit) {
    Log.d("ScreenA", "compose: recomposed")
    ScreenTemplate(text = "Спроси меня о птицах...", onNext = onNext)
}

@Composable
fun ScreenB(onNext: () -> Unit) {
    Log.d("ScreenB", "compose: recomposed")
    ScreenTemplate(text = "Что ты знаешь...", onNext = onNext)
}

@Composable
fun ScreenC(onNext: () -> Unit) {
    Log.d("ScreenC", "compose: recomposed")
    ScreenTemplate(text = "О белолобом попугае?", onNext = onNext)
}

@Composable
fun ScreenTemplate(text: String, onNext: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.sticker),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(200.dp),
            contentScale = ContentScale.Fit
        )

        Text(text = text,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center))

        Button(
            onClick = onNext,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            Text("ПРЫГНУТЬ")
        }
    }
}