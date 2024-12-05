package com.example.examplestep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.examplestep.UniversityViewModel
import com.example.examplestep.ui.components.boldFontFamily
import com.example.examplestep.ui.theme.Blue
import com.example.examplestep.ui.theme.LightGray

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
                        navController.navigate("login") {
                            popUpTo("university_selection") { inclusive = true }
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
            // 대학 소속 입력 필드
            TextField(
                value = universityName,
                onValueChange = { universityName = it },
                label = {
                    Text("대학 이름(ex. OO대학교 정자로 기입해주세요)", color = Color.Black)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = LightGray,
                        shape = RoundedCornerShape(size = 12.dp)
                    ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = LightGray, // 배경을 투명하게 설정
                    focusedIndicatorColor = Color.Transparent, // 포커스 시 밑줄 제거
                    unfocusedIndicatorColor = Color.Transparent // 비포커스 시 밑줄 제거
                )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    Blue
                ),
                enabled = universityName.isNotEmpty()
            ) {
                Text(
                    text = "확인",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = boldFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}