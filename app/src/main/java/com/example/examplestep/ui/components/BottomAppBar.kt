package com.example.examplestep.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun BottomAppBar(navController: NavController) {
    BottomAppBar(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround

        ) {
            IconButton(onClick = {
                navController.navigate("home") // 'Home' 화면으로 이동
            }) {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home, contentDescription = "Home"
                    )
                    Text(text = "홈", fontSize = 14.sp)
                }
            }

            IconButton(onClick = {
                navController.navigate("ranking") // 'Ranking' 화면으로 이동
            }) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Ranking"
                    )
                    Text(text = "랭킹", fontSize = 14.sp)
                }
            }

            IconButton(onClick = {
                navController.navigate("setting") // 'Ranking' 화면으로 이동
            }) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Setting"
                    )
                    Text(text = "설정", fontSize = 14.sp)
                }
            }

        }
    }
}
