package com.example.examplestep.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.examplestep.UniversityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversitySelectionScreen(
    navController: NavController,
    universityViewModel: UniversityViewModel,
    onUniversitySelected: (String) -> Unit
) {
    val context = LocalContext.current
    var universityName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(vertical = 16.dp), // 원하는 패딩 추가
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("setting") {
                            popUpTo("modify") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                title = {
                    Text(
                        text = "대학 입력",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("대학 소속을 입력하세요")

            Spacer(modifier = Modifier.height(8.dp))

            // 대학 소속 입력 필드
            TextField(
                value = universityName,
                onValueChange = { universityName = it },
                label = { Text("대학 이름(ex. OO대학교 정자로 기입해주세요 ") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // 선택한 대학 이름을 Firebase에 저장
                    universityViewModel.saveUniversityNameToFirebase(universityName,
                        onSuccess = {
                            // 대학 선택 후 홈 화면으로 이동
                            navController.navigate("status")
                        },
                        onFailure = { exception ->
                            errorMessage = "대학 이름 저장 실패: ${exception.message}"
                        }
                    )
                },
                enabled = universityName.isNotEmpty()
            ) {
                Text("확인")
            }
        }
    }
}