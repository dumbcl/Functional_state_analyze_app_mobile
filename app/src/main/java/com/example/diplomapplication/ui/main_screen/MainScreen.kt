package com.example.diplomapplication.ui.main_screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.diplomapplication.R

@Composable
fun MainScreen(
    openProfile: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(imageVector = Icons.Outlined.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.main)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = openProfile,
                    icon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = null) },
                    label = { Text(stringResource(R.string.profile)) }
                )
            }
        }

    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            item {
                Text(
                    text = stringResource(R.string.greeting),
                )
            }
        }
    }
}
