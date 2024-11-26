package com.example.examplestep.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.examplestep.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDataScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    // 선택된 날짜 변수
    var selectedMonth by remember { mutableStateOf("2024-11") }
    val leaderboardState = userViewModel.leaderboardState.value
    val loadingState = userViewModel.loadingState.value
    val errorState = userViewModel.errorState.value
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // Firebase UserId
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // 날짜 변경 시 데이터 새로 고침
    LaunchedEffect(selectedMonth) {
        userViewModel.getMonthlyStepsData(selectedMonth, { steps ->
            // 성공적으로 데이터를 가져오면 leaderboardState 업데이트
            userViewModel.leaderboardState.value = steps
        }, { error ->
            // 실패하면 errorState에 에러 메시지가 설정됨
            userViewModel.errorState.value = error.message
        })
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "나의 월별 데이터", fontSize = 24.sp)
                }
            )
        },
        bottomBar = {
            // BottomAppBar를 추가할 수 있습니다
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = selectedMonth, style = MaterialTheme.typography.headlineSmall)

                IconButton(onClick = { showDatePickerDialog = true }) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Select Month")
                }
            }



// DatePickerModalInput Dialog 표시
                    if (showDatePickerDialog) {
                        DatePickerModalInput(
                            onDateSelected = { selectedDate ->
                                selectedMonth = selectedDate?.let {
                                    // 밀리초를 기준으로 날짜를 년-월 형식으로 변환
                                    val calendar = Calendar.getInstance()
                                    calendar.timeInMillis = it
                                    val year = calendar.get(Calendar.YEAR)
                                    val month = calendar.get(Calendar.MONTH) + 1 // 0부터 시작하므로 +1 해줘야 함
                                    // "YYYY-MM" 형식으로 변환
                                    String.format("%04d-%02d", year, month)
                                } ?: selectedMonth
                                showDatePickerDialog = false
                            },
                            onDismiss = { showDatePickerDialog = false }
                        )
                    }

            Spacer(modifier = Modifier.height(16.dp))

            if (loadingState) {
                CircularProgressIndicator()
            } else if (errorState != null) {
                Text("Error: $errorState")
            } else {
                LazyColumn {
                    items(leaderboardState) { stepData ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = stepData.date)
                            Text(text = "${stepData.stepCount} 걸음")
                        }
                    }
                }
            }
        }
    }
}