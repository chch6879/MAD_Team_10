package com.example.examplestep.ui.screens


import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.examplestep.R
import com.example.examplestep.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.example.examplestep.ui.components.BottomAppBar
import com.example.examplestep.ui.components.StepCircleProgress
import com.example.examplestep.ui.components.boldFontFamily
import com.example.examplestep.ui.theme.LightGray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

val customFontFamily = FontFamily(Font(R.font.nanum_barun_gothic))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController:NavController,
    userViewModel: UserViewModel= viewModel()
) {
    val stepCount by userViewModel.stepCount.collectAsState() // ViewModel에서 상태 구독
    // 목표 걸음 수
    val goalSteps by userViewModel.goalSteps.collectAsState()

    val height by userViewModel.height.collectAsState()
    val weight by userViewModel.weight.collectAsState()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(true) }

    // SensorManager and StepDetector setup
//    val sensorManager = context.getSystemService(SensorManager::class.java)
//    val stepDetectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    // 거리 및 칼로리 소모 계산
    val strideLength = 0.415 * height // 보폭 계산 (cm)
    val distanceInMeters = stepCount * strideLength / 100 // 걸은 거리 (m)
    val caloriesBurned = (distanceInMeters / 1000) * weight // 칼로리 계산

    // 현재 날짜 및 날짜 형식
    var currentDate by remember { mutableStateOf(Date()) }
    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 (E)")
    val dateText = dateFormat.format(currentDate)
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val currentYearMonth = remember {
        val calendar = Calendar.getInstance()
        "${calendar.get(Calendar.YEAR)}-${String.format("%02d", calendar.get(Calendar.MONTH) + 1)}"
    }
    var selectedMonth by remember { mutableStateOf(currentYearMonth) }
//    var selectedMonth by remember { mutableStateOf("2024-11") }

    // HomeScreen이 처음 로드될 때 오늘 걸음 수 불러오기
    LaunchedEffect(Unit) {
        // Firebase 인증 상태가 올바르게 설정된 후 데이터 가져오기
        if (auth.currentUser != null) {
            userViewModel.getTodayStepsAndUserData(
                onSuccess = { steps, userHeight, userWeight ->
                    userViewModel.setStepCount(steps)
                    userViewModel.setHeightAndWeight(userHeight, userWeight)

                    // LocalContext에서 Activity 가져오기
                    val activity = context as? Activity
                    if (activity != null) {
                        userViewModel.setupSensor(context, activity) // Activity를 전달
                    } else {
                        // Activity가 null인 경우 적절한 처리
                        println("Activity is null")
                    }
                },
                onFailure = { e -> e.printStackTrace() }
            )
        } else {
            // 인증되지 않은 경우 로그인 화면으로 리다이렉트
            navController.navigate("login")
        }

    }
    if (isLoading.value) {
        // 로딩 중 표시
        CircularProgressIndicator()
    } else {
        // 데이터가 로드되었을 때 표시
        Text("Today's Step Count: $stepCount")
    }

Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
        CenterAlignedTopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    // 날짜 표시
                    Text (
                        text = dateText,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = customFontFamily,
                            fontSize = 20.sp
                        ),
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )

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
            }
        )
    },
    bottomBar = {
        BottomAppBar(navController)
    },
) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize().padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.todaystep),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = boldFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 목표 수 대비 걸음 수 게이지 표시
            StepCircleProgress(currentSteps = stepCount, goalSteps = goalSteps)
        }

        Spacer(modifier = Modifier.height(32.dp))
/*
        // 걸음 수 증가 버튼
        Button(onClick = {
            val updatedStepCount = stepCount + 1  // 걸음 수 증가
            userViewModel.setStepCount(updatedStepCount)
            userViewModel.updateDailySteps(
                updatedStepCount,
                onSuccess = { /* 성공 처리 (필요 시 메시지 표시) */ },
                onFailure = { /* 예외 처리 */ }
            )
        }) {
            Text("걸음 수 증가")
        }
*/
        //Spacer(modifier = Modifier.height(32.dp))

        // 이동 거리 및 소모 칼로리 표시
        StatsSection(distanceKm = distanceInMeters, caloriesBurned = caloriesBurned)
    }
  }
}

@Composable
fun InfoCard(icon: @Composable () -> Unit, title: String, value: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .border(
                width = 1.dp,
                color = LightGray,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = boldFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                    ),
                    color = Color.Gray
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = boldFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                ),
                color = Color.Black
            )
        }
    }
}

@Composable
fun StatsSection(distanceKm: Double, caloriesBurned: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // 이동 거리 표시
        InfoCard(
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Distance Icon",
                    tint = Color(0xFF3f88e8)
                )
            },
            title = "이동거리",
            value = "%.2f m".format(distanceKm)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 소모 칼로리 표시
        InfoCard(
            icon = {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Calories Icon",
                    tint = Color(0xFFe89f3f)
                )
            },
            title = "칼로리",
            value = "%.2f kcal".format(caloriesBurned)
        )
    }
}