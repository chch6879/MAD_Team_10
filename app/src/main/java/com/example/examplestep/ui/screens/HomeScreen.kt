package com.example.examplestep.ui.screens


import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.examplestep.R
import com.example.examplestep.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.example.examplestep.ui.components.BottomAppBar

@Composable
fun HomeScreen(
    navController:NavController,
    userViewModel: UserViewModel= viewModel()
) {
    val stepCount by userViewModel.stepCount.collectAsState() // ViewModel에서 상태 구독
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

    // Step detector event listener
//    DisposableEffect(sensorManager) {
//        val sensorEventListener = object : SensorEventListener {
//            override fun onSensorChanged(event: SensorEvent?) {
//                if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
//                    val newStepCount = stepCount + event.values[0].toInt()
//                    userViewModel.setStepCount(newStepCount)
//                    userViewModel.updateDailySteps(
//                        newStepCount,
//                        onSuccess = {},
//                        onFailure = { it.printStackTrace() }
//                    )
//                }
//            }
//
//            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
//        }
//
//        sensorManager?.registerListener(
//            sensorEventListener,
//            stepDetectorSensor,
//            SensorManager.SENSOR_DELAY_NORMAL
//        )
//
//        onDispose {
//            sensorManager?.unregisterListener(sensorEventListener)
//        }
//    }


Scaffold(
    modifier = Modifier.fillMaxSize(),
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
                text = "Today Step",
                style = MaterialTheme.typography.displayLarge,
                color = Color.Black
            )
            Text(
                text = "$stepCount",
                style = MaterialTheme.typography.displayLarge,
                color = Color.Black
            )
            Text(text = "Height: $height cm")
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Weight: $weight kg")
            Spacer(modifier = Modifier.height(16.dp))

            Text("Distance Walked: %.2f m".format(distanceInMeters))
            Text("Calories Burned: %.2f kcal".format(caloriesBurned))
            Spacer(modifier = Modifier.height(16.dp))
        }


        Spacer(modifier = Modifier.height(16.dp))

        // 걸음 수 증가 버튼
        Button(onClick = {
//            stepCount += 1  // 걸음 수 증가
//            userViewModel.updateDailySteps(
//                stepCount,
//                onSuccess = { /* 성공 처리 (필요 시 메시지 표시) */ },
//                onFailure = { /* 예외 처리 */ }
//            )
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

        Spacer(modifier = Modifier.height(32.dp))


        // 로그아웃 버튼 추가
        Button(
//            onClick = {
//                userViewModel.updateDailySteps(
//                    stepCount,
//                    onSuccess = {
//                        auth.signOut() // Firebase에서 로그아웃
//                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                            .requestIdToken(context.getString(R.string.default_web_client_id))
//                            .requestEmail()
//                            .build()
//                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
//                        googleSignInClient.signOut() // GoogleSignInClient 초기화
//                        navController.navigate("login") // 로그인 화면으로 이동
//                    },
//                    onFailure = { /* 예외 처리 */ }
//                )
//            },
            onClick = {
                // Firebase 로그아웃
                auth.signOut()

                // Google Sign-In 로그아웃
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)

                // Google Sign-In 로그아웃
                googleSignInClient.signOut().addOnCompleteListener {
                    // 로그아웃 완료 후 로그인 화면으로 이동

                    navController.navigate("login") {
                        // 기존 스택을 없애고 로그인 화면으로 이동
                        popUpTo("home") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.padding(top = 80.dp)
        ) {
            Text("로그아웃")
        }
    }
}

}

