    package com.example.examplestep.ui.screens

    import android.content.Context
    import android.content.Intent
    import android.widget.Toast
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.text.BasicTextField
    import androidx.compose.foundation.text.KeyboardOptions
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.automirrored.filled.ArrowBack
    import androidx.compose.material3.Button
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.CenterAlignedTopAppBar
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.LocalTextStyle
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
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.input.KeyboardType
    import androidx.compose.ui.text.input.TextFieldValue
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation.NavController
    import com.example.examplestep.MainActivity
    import com.example.examplestep.UserViewModel
    import com.example.examplestep.ui.components.boldFontFamily
    import com.example.examplestep.ui.theme.Blue
    import com.example.examplestep.ui.theme.LightGray

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun StatusScreen(
        navController: NavController,
        userViewModel: UserViewModel,
        context: Context // Context를 받아오기
    ){
        var height by remember { mutableStateOf("") }
        var weight by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var successMessage by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier
                        .padding(
                            start = 16.dp, end = 16.dp,
                            top = 20.dp, bottom = 0.dp
                        ),
                    navigationIcon = {
                        IconButton(onClick = {  navController.navigate("university_selection"){
                            popUpTo("status"){ inclusive= true }
                        } }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "신체 정보 입력",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontFamily = boldFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 25.sp
                            )
                        )
                    }
                )
            },
        ) { innerPadding->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(start = 32.dp, top = 180.dp, end = 32.dp, bottom = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomNumberInputField2(
                    value = height,
                    onValueChange = { height = it },
                    scale = "cm",
                    placeholder = "height"
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomNumberInputField2(
                    value = weight,
                    onValueChange = { weight = it },
                    scale = "kg",
                    placeholder = "weight"
                )
                Spacer(modifier = Modifier.height(32.dp))
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
                                    //successMessage = "\nData saved successfully!"
                                    Toast.makeText(context, "Successfully saved!", Toast.LENGTH_SHORT).show()
                                    errorMessage = ""
                                    restartApp(context)  // 성공 시 앱 재시작
                                    navController.navigate("home"){
                                        popUpTo("status"){ inclusive= true }
                                    }
                                },
                                onFailure = { e ->
                                    isLoading = false
                                    successMessage = ""
                                    //errorMessage = "Failed to save data: ${e.message}"
                                    Toast.makeText(context, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            Toast.makeText(context, "키와 몸무게를 입력해주세요.", Toast.LENGTH_SHORT).show()
                            //errorMessage = "Please enter valid numeric values for height and weight."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        Blue
                    ),
                    enabled = height.isNotEmpty() && weight.isNotEmpty()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Blue,
                            modifier = Modifier.size(25.dp)
                        )
                    } else {
                        Text(
                            text = "저장",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = boldFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
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


    fun restartApp(context: Context) {
        // 앱을 종료하고 MainActivity를 다시 시작하는 코드
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)

        // 앱 종료
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }

    @Composable
    fun CustomNumberInputField2(
        value: String,
        onValueChange: (String) -> Unit,
        scale: String,
        placeholder: String,
        modifier: Modifier = Modifier
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier
                    .width(200.dp)
                    .height(80.dp)
                    .background(
                        color = LightGray,
                        shape = RoundedCornerShape(size = 12.dp)
                    )
                    .padding(start = 32.dp, end = 32.dp),
                textStyle = LocalTextStyle.current.copy(
                    color = Color.DarkGray,
                    fontSize = 50.sp,
                    fontFamily = boldFontFamily,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color.Gray,
                                fontSize = 30.sp,
                                fontFamily = boldFontFamily,
                                textAlign = TextAlign.Center,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = scale,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
