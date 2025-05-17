package com.example.diplomapplication.ui.enter_screen.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.diplomapplication.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginForm(
    toStartForm: () -> Unit,
    loginClick: (String, String) -> Unit,
    isLoginError: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ){
        val context = LocalContext.current
        IconButton(
            onClick = toStartForm,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp),
        ) {
            Icon(
                Icons.Filled.KeyboardArrowLeft,
                "to start form",
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp),
                )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = context.resources.getString(R.string.entrance),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(modifier = Modifier.height(52.dp))

            Text(
                text = context.resources.getString(R.string.login_enter_form_hint),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(6.dp))
            var login by remember { mutableStateOf("") }
            OutlinedTextField(
                modifier = Modifier
                    .width(306.dp),
                value = login,
                onValueChange = { newLogin ->
                    login = newLogin
                },
                placeholder = { Text(context.resources.getString(R.string.login_enter_form), color = MaterialTheme.colorScheme.secondary) },
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(29.dp))

            Text(
                text = context.resources.getString(R.string.password_enter_form_hint),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(6.dp))
            var password by remember { mutableStateOf("") }
            OutlinedTextField(
                modifier = Modifier
                    .width(306.dp),
                value = password,
                onValueChange = { newPassword ->
                    password = newPassword
                },
                placeholder = { Text(context.resources.getString(R.string.password_enter_form), color = MaterialTheme.colorScheme.secondary) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )


            Spacer(modifier = Modifier.height(52.dp))
            TextButton(
                onClick = { loginClick(login, password) },
                modifier = Modifier
                    .width(188.dp)
                    .height(49.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(size = 8.dp)
                    )
            ) {
                Text(
                    text = context.resources.getString(R.string.enter),
                    color = MaterialTheme.colorScheme.background,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

