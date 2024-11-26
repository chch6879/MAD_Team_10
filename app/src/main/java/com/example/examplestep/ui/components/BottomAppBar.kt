package com.example.examplestep.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun BottomAppBar(navController: NavController) {
    // 현재 선택된 버튼을 관리하는 상태
    var selectedButton by remember { mutableStateOf("home") }

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp) // 바깥 여백
            .background(
                color = Color.White,
                shape = RoundedCornerShape(24.dp) // 둥근 모양 설정
            ),
        containerColor = Color.White,
        contentColor = Color.Gray,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically

        ) {
            CustomIconButton(
                isSelected = selectedButton == "home",
                onClick = {
                    selectedButton = "home"
                    navController.navigate("home")
                },
                icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = "Home") },
                text = "홈"
            )

            CustomIconButton(
                isSelected = selectedButton == "ranking",
                onClick = { navController.navigate("mydata") },
                icon = { Icon(imageVector = Icons.Filled.Star, contentDescription = "Ranking") },
                text = "내 데이터"
            )

            CustomIconButton(
                isSelected = selectedButton == "ranking",
                onClick = { navController.navigate("ranking") },
                icon = { Icon(imageVector = Icons.Filled.Star, contentDescription = "Ranking") },
                text = "랭킹"
            )

            CustomIconButton(
                isSelected = selectedButton == "setting",
                onClick = { navController.navigate("setting") },
                icon = { Icon(imageVector = Icons.Filled.Settings, contentDescription = "Setting") },
                text = "설정"
            )
        }
    }
}

@Composable
fun CustomIconButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    text: String
) {
    val animatedFontSize by animateFloatAsState(
        targetValue = if (isSelected) 16f else 14f, // 클릭 시 16sp, 기본은 14sp
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 300) // 애니메이션 지속 시간
    )

    Box(
        modifier = Modifier
            .size(70.dp) // 버튼 크기
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = 300.dp, // 클릭 효과 반경
                    color = Color.Gray
                ),
                onClick = {onClick()}

            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Text(
                text = text,
                fontSize = animatedFontSize.sp
            )
        }
    }
}
