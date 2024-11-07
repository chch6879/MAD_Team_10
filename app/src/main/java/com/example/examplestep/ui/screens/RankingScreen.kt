package com.example.examplestep.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.examplestep.ui.components.BottomAppBar

@Composable
fun RankingScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomAppBar(navController)
        },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                // Ranking 화면 내용
                Text(text = "Ranking Screen Content")
                Spacer(modifier = Modifier.height(20.dp))
                // 기타 Ranking 화면에 필요한 UI 요소들
            }
        }
    )
}
