package com.example.examplestep.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.examplestep.AppViewModel
import com.example.examplestep.R
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


    LaunchedEffect(currentUser) {
        // currentUser가 null일 때만 로그인 화면을 보여줘야 하므로, null이 아닐 경우에만 처리하도록 변경
        currentUser?.let {
            checkIfFirstLogin(it.uid) { isFirstLogin ->
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
        Button(
            onClick = { launchGoogleSignIn() },
            contentPadding = PaddingValues(0.dp) // 여백을 없앰
        ){
            Image(
                painter = painterResource(id = R.drawable.loginimg),
                contentDescription = "Google Logo",
            )
        }
    }
}


