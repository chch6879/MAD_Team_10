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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.examplestep.R
import com.example.examplestep.ui.theme.Blue
import com.example.examplestep.ui.theme.Blue40
import com.example.examplestep.ui.theme.BottomColor
import com.example.examplestep.ui.theme.LightGray
import org.checkerframework.common.subtyping.qual.Bottom

@Composable
fun BottomAppBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.MyData,
        BottomNavItem.Ranking,
        BottomNavItem.Setting
    )

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = 20.dp, topEnd = 20.dp,
                    bottomEnd = 0.dp, bottomStart = 0.dp)
            )
            .height(130.dp)
            .border(
                width = 1.dp,
                color = LightGray,
                shape = RoundedCornerShape(
                    topStart = 20.dp, topEnd = 20.dp,
                    bottomEnd = 0.dp, bottomStart = 0.dp)
            ),
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
                            .size(32.dp)
                    )
                },
                label = { Text(stringResource(id = item.title), fontSize = 15.sp) },
                selected = currentRoute == item.screenRoute,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black, // 선택된 아이콘 색상
                    unselectedIconColor = BottomColor, // 선택되지 않은 아이콘 색상
                    selectedTextColor = Blue, // 선택된 텍스트 색상
                    unselectedTextColor = BottomColor, // 선택되지 않은 텍스트 색상
                    indicatorColor = Blue40
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
    object MyData : BottomNavItem(R.string.mydata, Icons.Filled.Person, "mydata")
    object Ranking : BottomNavItem(R.string.ranking, Icons.Filled.Stars, "ranking")
    object Setting : BottomNavItem(R.string.setting, Icons.Filled.Settings, "setting")
}
