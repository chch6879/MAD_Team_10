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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.examplestep.UserViewModel

@Composable
fun StatusScreen(
    navController: NavController,
    userViewModel: UserViewModel
){
    var height by remember { mutableStateOf(TextFieldValue("")) }
    var weight by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                val heightValue = height.text.toIntOrNull()
                val weightValue = weight.text.toIntOrNull()

                if (heightValue != null && weightValue != null) {
                    isLoading = true
                    userViewModel.saveUserHeightAndWeight(
                        heightValue,
                        weightValue,
                        onSuccess = {
                            isLoading = false
                            successMessage = "Data saved successfully!"
                            errorMessage = ""
                            navController.navigate("home")
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
            modifier = Modifier.fillMaxWidth()
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
