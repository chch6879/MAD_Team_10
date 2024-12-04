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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.examplestep.R
import com.example.examplestep.UserViewModel
import com.example.examplestep.ui.components.CustomTopAppBar
import com.example.examplestep.ui.components.boldFontFamily
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDataScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    // 선택된 날짜 변수
    val currentYearMonth = remember {
        val calendar = Calendar.getInstance()
        "${calendar.get(Calendar.YEAR)}-${String.format("%02d", calendar.get(Calendar.MONTH) + 1)}"
    }
    var selectedMonth by remember { mutableStateOf(currentYearMonth) }
//    var selectedMonth by remember { mutableStateOf("2024-11") }
    val leaderboardState = userViewModel.leaderboardState.value
    val loadingState = userViewModel.loadingState.value
    val errorState = userViewModel.errorState.value
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // Firebase UserId
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // 날짜 변경 시 데이터 새로 고침
    LaunchedEffect(selectedMonth) {
        userViewModel.getMonthlyStepsData(
            selectedMonth,
            onSuccess = { steps ->
            // 성공적으로 데이터를 가져오면 leaderboardState 업데이트
            userViewModel.leaderboardState.value = steps
            },
            onFailure =  { error ->
            // 실패하면 errorState에 에러 메시지가 설정됨
            userViewModel.errorState.value = error.message
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            CustomTopAppBar("나의 월별 데이터")
        },
        bottomBar = {
            com.example.examplestep.ui.components.BottomAppBar(navController)
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
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedMonth,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = customFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                )
                IconButton(onClick = { showDatePickerDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Select Month",
                        tint = Color(0xFF3f88e8),
                        modifier = Modifier
                            .size(30.dp)
                    )
                }
            }

            // DatePickerModalInput Dialog 표시
            if (showDatePickerDialog) {
                DatePickerModalInput(
                    onDateSelected = { selectedDate ->
                        selectedMonth = selectedDate?.let {
                            // 날짜 선택 후, 년-월 형식으로 변환
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = it
                            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}"
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
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stepData.date,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = customFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp
                                ),
                                color = Color.DarkGray
                            )
                            Text(
                                text = "${stepData.stepCount} 걸음",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = customFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp
                                ),
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}
