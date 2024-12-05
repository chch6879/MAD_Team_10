package com.example.examplestep.ui.screens
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.examplestep.ui.components.CustomTopAppBar
import com.example.examplestep.ui.components.boldFontFamily
import com.example.examplestep.ui.theme.Blue
import com.example.examplestep.ui.theme.LightGray
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

    val context = LocalContext.current

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
            CustomTopAppBar("이달의 순위")
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
                // 선택된 날짜 표시
                Text(
                    text = selectedMonth,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold, // 굵게 설정
                        fontFamily = customFontFamily,
                        fontSize = 25.sp
                    )
                )
                // 날짜 선택 아이콘
                IconButton(onClick = { showDatePickerDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
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

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = LightGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Text(text = selectedMonth)

            // 로딩 상태 및 오류 처리
            if (loadingState.value) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator( color = Blue )
                }
            } else if (errorState.value != null) {
                /*
                Text(
                    text = "Error: ${errorState.value}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
                 */
                Toast.makeText(context, "Error: ${errorState.value}", Toast.LENGTH_SHORT).show()
            } else {
                // 1~3등 상단 표시
                TopThreeRanking(
                    stepDataList = leaderboardState.value
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4등 이후 LazyColumn으로 나열
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(leaderboardState.value.drop(3)) { index, stepData ->
                        RankingItem(stepData = stepData, rank = index + 4)
                    }
                }
            }
        }
    }
}

@Composable
fun RankingItem(stepData: StepData, rank: Int) {
    // 리더보드 항목 UI 구성
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .background(
                Color.White,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "${rank}위", // 순위 숫자와 점을 표시
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = customFontFamily,
                    fontSize = 16.sp,
                ),
                color = Color.Gray, // 순위 텍스트 색상
                modifier = Modifier.padding(end = 8.dp) // 순위와 대학명 간 간격
            )
            Text(
                text = stepData.universityName,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = customFontFamily,
                    fontSize = 20.sp,
                ),
                color = Color.DarkGray,
            )
        }
        Text(
            text = "${stepData.totalSteps} 걸음",
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = customFontFamily,
                fontSize = 16.sp,
            ),
            color = Color.Gray
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

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Blue,        // 버튼 색상
            onPrimary = Color.White,            // 버튼 텍스트 색상
            surface = Color.White,        // 다이얼로그 배경색
            onSurface = Blue            // 다이얼로그 텍스트 색상
        )
    ) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSelected(datePickerState.selectedDateMillis)
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = LightGray,
                titleContentColor = Blue,
                headlineContentColor = Color.DarkGray
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = LightGray,
                    titleContentColor = Blue,
                    weekdayContentColor = Blue
                )
            )
        }
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
            description = "Silver Medal",
            modifier = Modifier.padding(top = 16.dp)
        )

        // 1등 자리
        RankingMedalItem(
            stepData = first,
            medalIcon = medalIcons.getOrNull(0),
            description = "Gold Medal",
            modifier = Modifier.offset(y = (-16).dp)
        )

        // 3등 자리
        RankingMedalItem(
            stepData = third,
            medalIcon = medalIcons.getOrNull(2),
            description = "Bronze Medal",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun RankingMedalItem(
    stepData: StepData?,
    medalIcon: Int?,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(100.dp) // 고정된 넓이로 자리 차지
    ) {
        if (stepData != null && medalIcon != null) {
            // 메달 이미지와 텍스트가 있을 때만 렌더링
            Image(
                painter = painterResource(id = medalIcon),
                contentDescription = description,
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stepData.universityName,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = boldFontFamily,
                    fontSize = 20.sp,
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${stepData.totalSteps} 걸음",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = boldFontFamily,
                    fontSize = 16.sp,
                ),
                color = Color.Gray
            )
        } else {
            // 데이터가 없으면 빈 상태로 유지
            Spacer(modifier = Modifier.height(60.dp)) // 메달 자리
            Text(text = "", style = MaterialTheme.typography.bodyLarge)
            Text(text = "", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
