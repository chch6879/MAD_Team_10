package com.example.examplestep.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.examplestep.MainActivity
import com.example.examplestep.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyStatusScreen(
    navController: NavController,
    userViewModel: UserViewModel
){
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
Scaffold(
    topBar = {
        CenterAlignedTopAppBar(
            modifier = Modifier.padding(vertical = 16.dp), // 원하는 패딩 추가
            navigationIcon = {
                IconButton(onClick = {  navController.navigate("setting"){
                    popUpTo("modify"){ inclusive= true }
                } }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            },
            title = {
                Text(
                    text = "정보 수정",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        )
    },
) {innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Enter Height (cm)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Enter Weight (kg)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val heightValue = height.toIntOrNull()
                val weightValue = weight.toIntOrNull()

                if (heightValue != null && weightValue != null) {
                    isLoading = true
                    userViewModel.saveUserHeightAndWeight(
                        heightValue,
                        weightValue,
                        onSuccess = {
                            isLoading = false
                            successMessage = "Data saved successfully!"
                            errorMessage = ""
                            navController.navigate("setting"){
                                popUpTo("modify"){ inclusive= true }
                            }
                        },
                        onFailure = { e ->
                            isLoading = false
                            successMessage = ""
                            errorMessage = "Failed to save data: ${e.message}"
                        }
                    )
                } else {
                    errorMessage = "Please enter valid numeric values for height and weight."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = height.isNotEmpty() && height.isNotEmpty()
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Save")
            }
        }
        if (successMessage.isNotEmpty()) {
            Text(successMessage)
        }
        if (errorMessage.isNotEmpty()) {
            Text(errorMessage)
        }
    }
}
}