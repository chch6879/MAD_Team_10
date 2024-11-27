package com.example.examplestep.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.examplestep.R

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun BottomAppBar_(navController: NavController) {
    // 현재 선택된 버튼을 관리하는 상태
    var selectedButton by remember { mutableStateOf("home") }

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(24.dp)
            ).border(width = 0.5.dp, color = Color.Gray, shape = RoundedCornerShape(24.dp)), // 검정색 테두리 추가
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
                onClick = { onClick() }

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

@Composable
fun BottomAppBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Ranking,
        BottomNavItem.Setting
    )

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(24.dp)
            ).border(width = 0.5.dp, color = Color.Gray, shape = RoundedCornerShape(24.dp)),
        containerColor = Color.White,
        contentColor = Color.Gray,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(id = item.title),
                        modifier = Modifier
                            .size(40.dp)
                    )
                },
                label = { Text(stringResource(id = item.title), fontSize = 15.sp) },
                selected = currentRoute == item.screenRoute,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black, // 선택된 아이콘 색상
                    unselectedIconColor = Color.Gray, // 선택되지 않은 아이콘 색상
                    selectedTextColor = MaterialTheme.colorScheme.primary, // 선택된 텍스트 색상
                    unselectedTextColor = Color.Gray // 선택되지 않은 텍스트 색상
                ),
                alwaysShowLabel = false,
                onClick = {
                    navController.navigate(item.screenRoute) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class BottomNavItem(
    val title: Int, val icon: ImageVector, val screenRoute: String
) {
    object Home : BottomNavItem(R.string.home, Icons.Filled.Home, "home")
    object Ranking : BottomNavItem(R.string.Ranking, Icons.Filled.Stars, "ranking")
    object Setting : BottomNavItem(R.string.Setting, Icons.Filled.Settings, "setting")
}