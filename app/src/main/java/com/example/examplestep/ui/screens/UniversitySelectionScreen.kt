package com.example.examplestep.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.examplestep.UniversityViewModel

@Composable
fun UniversitySelectionScreen(
    navController: NavController,
    universityViewModel: UniversityViewModel,
    onUniversitySelected: (String) -> Unit
) {
    val context = LocalContext.current
    var universityName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp),
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
