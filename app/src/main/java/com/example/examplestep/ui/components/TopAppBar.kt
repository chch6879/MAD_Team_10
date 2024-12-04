package com.example.examplestep.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examplestep.R
import com.example.examplestep.ui.screens.customFontFamily

val boldFontFamily = FontFamily(Font(R.font.nanum_barun_gothic_bold))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(currentPageTitle: String) {
    CenterAlignedTopAppBar(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 0.dp),
        title = {
            Text(
                text = currentPageTitle,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = boldFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 25.sp
                )
            )
        }
    )
}