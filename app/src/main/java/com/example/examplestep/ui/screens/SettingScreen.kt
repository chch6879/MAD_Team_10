package com.example.examplestep.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.examplestep.R
import com.example.examplestep.UserViewModel
import com.example.examplestep.ui.components.BottomAppBar
import com.example.examplestep.ui.components.CustomTopAppBar
import com.example.examplestep.ui.components.boldFontFamily
import com.example.examplestep.ui.theme.Blue
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    var stepCount by remember { mutableStateOf(0) }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CustomTopAppBar("설정")
        },
        bottomBar = {
            BottomAppBar(navController)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick ={ navController.navigate("modify") },
                modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(start = 20.dp, end = 20.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    Color.White
                )
            ) {
                Text(
                    text = "정보 수정",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = boldFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 로그아웃 버튼 추가
            Button(
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(start = 20.dp, end = 20.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    Blue
                )
            ) {
                Text(
                    text = "로그아웃",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = boldFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                )
            }
        }
    }
}