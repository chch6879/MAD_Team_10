package com.example.examplestep.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.Modifier

@Composable
fun BottomAppBar(navController: NavController) {
    BottomAppBar(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = {
            navController.navigate("home") // 'Home' 화면으로 이동
        }) {
            Icon(imageVector = Icons.Filled.Home, contentDescription = "Home")
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = {
            navController.navigate("ranking") // 'Ranking' 화면으로 이동
        }) {
            Icon(imageVector = Icons.Filled.Star, contentDescription = "Ranking")
        }
    }
}
