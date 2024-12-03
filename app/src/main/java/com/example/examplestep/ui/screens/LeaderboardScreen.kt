package com.example.examplestep.ui.screens
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.examplestep.LeaderboardViewModel
import com.example.examplestep.R
import com.example.examplestep.StepData
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    navController: NavController,
    leaderboardViewModel: LeaderboardViewModel = viewModel()
) {
    val currentYearMonth = remember {
        val calendar = Calendar.getInstance()
        "${calendar.get(Calendar.YEAR)}-${String.format("%02d", calendar.get(Calendar.MONTH) + 1)}"
    }
    var selectedMonth by remember { mutableStateOf(currentYearMonth) }
//    var selectedMonth by remember { mutableStateOf("2024-11") }
    val leaderboardState = remember { mutableStateOf<List<StepData>>(emptyList()) }
    val loadingState = remember { mutableStateOf(true) }
    val errorState = remember { mutableStateOf<String?>(null) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // 데이터 가져오기 작업
    LaunchedEffect(selectedMonth) {
        leaderboardViewModel.getMonthlyTotalSteps(
            month = selectedMonth,
            onSuccess = {
                leaderboardState.value = it
                loadingState.value = false
            },
            onFailure = { e ->
                errorState.value = e.message
                loadingState.value = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Leaderboard",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            )
        },
        bottomBar = {
            com.example.examplestep.ui.components.BottomAppBar(navController)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 선택된 날짜 표시
                Text(
                    text = selectedMonth,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold // 굵게 설정
                    )
                )

                // 날짜 선택 아이콘
                IconButton(onClick = { showDatePickerDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select Month",
                        tint = MaterialTheme.colorScheme.primary
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

            Text(text = selectedMonth)

            // 로딩 상태 및 오류 처리
            if (loadingState.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (errorState.value != null) {
                Text(
                    text = "Error: ${errorState.value}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                // 1~3등 상단 표시
                TopThreeRanking(
                    stepDataList = leaderboardState.value
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4등 이후 LazyColumn으로 나열
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(leaderboardState.value.drop(3)) { stepData ->
                        RankingItem(stepData)
                    }
                }
            }
        }
    }
}

@Composable
fun RankingItem(stepData: StepData) {
    // 리더보드 항목 UI 구성
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(
                Color(0xFFF5F5F5),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stepData.universityName,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "${stepData.totalSteps} steps",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}



@Composable
fun TopThreeRanking(stepDataList: List<StepData>) {
    // 1, 2, 3등 데이터를 개별적으로 추출
    val first = stepDataList.getOrNull(0)
    val second = stepDataList.getOrNull(1)
    val third = stepDataList.getOrNull(2)

    val medalIcons = listOf(
        R.drawable.gold,   // 금메달
        R.drawable.silver, // 은메달
        R.drawable.bronze  // 동메달
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2등 자리
        RankingMedalItem(
            stepData = second,
            medalIcon = medalIcons.getOrNull(1),
            description = "Silver Medal"
        )

        // 1등 자리
        RankingMedalItem(
            stepData = first,
            medalIcon = medalIcons.getOrNull(0),
            description = "Gold Medal"
        )

        // 3등 자리
        RankingMedalItem(
            stepData = third,
            medalIcon = medalIcons.getOrNull(2),
            description = "Bronze Medal"
        )
    }
}

@Composable
fun RankingMedalItem(
    stepData: StepData?,
    medalIcon: Int?,
    description: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp) // 고정된 넓이로 자리 차지
    ) {
        if (stepData != null && medalIcon != null) {
            // 메달 이미지와 텍스트가 있을 때만 렌더링
            Image(
                painter = painterResource(id = medalIcon),
                contentDescription = description,
                modifier = Modifier.size(60.dp)
            )
            Text(text = stepData.universityName, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            Text(text = "${stepData.totalSteps} steps", style = MaterialTheme.typography.bodyMedium)
        } else {
            // 데이터가 없으면 빈 상태로 유지
            Spacer(modifier = Modifier.height(60.dp)) // 메달 자리
            Text(text = "", style = MaterialTheme.typography.bodyLarge)
            Text(text = "", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
