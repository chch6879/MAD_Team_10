package com.example.examplestep.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.examplestep.AppViewModel
import com.example.examplestep.R
import com.example.examplestep.components.GoogleSignInButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.sign

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // GoogleSignInClient 설정
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)


    fun checkIfFirstLogin(userId: String, onResult: (Boolean) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // 사용자 정보가 존재하면 최초 로그인이 아님
                    onResult(false)
                } else {
                    // 사용자 정보가 존재하지 않으면 최초 로그인
                    onResult(true)
                }
            }
            .addOnFailureListener {
                onResult(false) // 오류 발생 시 최초 로그인 아님으로 간주
            }
    }

    // 로그인 결과 처리
    fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    // 로그인 성공 후 최초 로그인 여부 확인
                    checkIfFirstLogin(auth.currentUser?.uid ?: "") { isFirstLogin ->
                        if (isFirstLogin) {
                            navController.navigate("university_selection") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "로그인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Google 로그인 결과를 처리하는 Launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task) // 로그인 결과 처리
        }
    }

    // Google Sign-In 프로세스를 시작하는 함수
    val launchGoogleSignIn = {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }



    // 최초 로그인 여부 확인
    if (currentUser != null) {
        LaunchedEffect(currentUser) {
            checkIfFirstLogin(currentUser.uid) { isFirstLogin ->
                if (isFirstLogin) {
                    navController.navigate("university_selection") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { launchGoogleSignIn() }) { // 로그인 버튼 클릭 시 Google Sign-In 시작
            Text("Google로 로그인")
        }
    }
}
//
//@Composable
//fun LoginScreen(navController: NavController) {
//    val context = LocalContext.current
//    val auth = FirebaseAuth.getInstance()
//    val currentUser = auth.currentUser
//
//    // GoogleSignInClient 설정
//    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//        .requestIdToken(context.getString(R.string.default_web_client_id)) // Firebase 콘솔에서 생성한 웹 클라이언트 ID 사용
//        .requestEmail()
//        .build()
//
//    val googleSignInClient = GoogleSignIn.getClient(context, gso)
//
//
//
//    // 로그인 결과 처리
//    fun handleSignInResult(task: Task<GoogleSignInAccount>, navController: NavController) {
//        try {
//            val account = task.getResult(ApiException::class.java) // 로그인 성공
//            val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
//            auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
//                if (authTask.isSuccessful) {
//                    navController.navigate("home")  // 로그인 성공 시 홈으로 이동
//                } else {
//                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
//                }
//            }
//        } catch (e: ApiException) {
//            Toast.makeText(context, "로그인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // Google 로그인 결과를 처리하는 Launcher
//    val googleSignInLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//            handleSignInResult(task, navController) // 로그인 결과 처리
//        }
//    }
//
//    // Google Sign-In 프로세스를 시작하는 함수
//    val launchGoogleSignIn = {
//        val signInIntent = googleSignInClient.signInIntent
//        // 로그인 결과를 처리할 Launcher에 signInIntent를 전달
//        googleSignInLauncher.launch(signInIntent)
//    }
//
//    // 로그아웃 함수 그냥 사용 안함
//    fun signOut() {
//        auth.signOut() // Firebase 로그아웃
//        googleSignInClient.signOut() // Google Sign-In 로그아웃
//    }
//
//    fun checkIfFirstLogin(userId: String, onResult: (Boolean) -> Unit) {
//        val firestore = FirebaseFirestore.getInstance()
//
//        firestore.collection("users").document(userId)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    // 사용자 정보가 존재하면 최초 로그인이 아님
//                    onResult(false)
//                } else {
//                    // 사용자 정보가 존재하지 않으면 최초 로그인
//                    onResult(true)
//                }
//            }
//            .addOnFailureListener {
//                onResult(false) // 오류 발생 시 최초 로그인 아님으로 간주
//            }
//    }
//
//    // 최초 로그인 여부 확인
//    if (currentUser != null) {
//        // 이미 로그인한 경우 대학 소속 입력 화면으로 이동
//        checkIfFirstLogin(currentUser.uid) { isFirstLogin ->
//            if (isFirstLogin) {
//                // 최초 로그인 시 대학 소속 입력 화면으로 이동
//                navController.navigate("university_selection")
//            } else {
//                // 홈 화면으로 이동
//                navController.navigate("home")
//            }
//        }
//    }
//
//
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Button(onClick = { launchGoogleSignIn() }) { // 로그인 버튼 클릭 시 Google Sign-In 시작
//            Text("Google로 로그인")
//        }
//    }
//
//}


