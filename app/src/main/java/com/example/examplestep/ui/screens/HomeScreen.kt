package com.example.examplestep.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.examplestep.R
import com.example.examplestep.StepcountViewModel
import com.example.examplestep.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.example.examplestep.ui.components.BottomAppBar

@Composable
fun HomeScreen(
    navController:NavController,
    userViewModel: UserViewModel= viewModel(),
    stepcountViewModel: StepcountViewModel = viewModel()
) {
    val stepCount by userViewModel.stepCount.collectAsState() // ViewModel에서 상태 구독
    val liveStepCount by stepcountViewModel.steps           // 실시간 걸음수
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(true) }

    // HomeScreen이 처음 로드될 때 오늘 걸음 수 불러오기
    LaunchedEffect(Unit) {
        // Firebase 인증 상태가 올바르게 설정된 후 데이터 가져오기
        if (auth.currentUser != null) {
            userViewModel.getTodaySteps(
                onSuccess = { steps ->
                    isLoading.value = false
                    userViewModel.setStepCount(steps)  },
                onFailure = { /* 예외 처리 */ }
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

    LaunchedEffect(Unit) {
        stepcountViewModel.startListening()
    }


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
            Text(
                text = "실시간 걸음 수: $liveStepCount",
                style = MaterialTheme.typography.displayMedium,
                color = Color.Black
            )
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