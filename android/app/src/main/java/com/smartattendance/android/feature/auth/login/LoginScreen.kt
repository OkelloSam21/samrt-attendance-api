package com.smartattendance.android.feature.auth.login

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartattendance.modulesui.design.ui.theme.SmartAttendanceTheme


@Composable
fun LoginScreenContent(
    state: LoginUiState,
    event: (LoginUiEvents) -> Unit,
) {
    var showPassword by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Text(
            text = "Smart Attendance",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
//            color = Purple40,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
        )

        // Welcome text
        Text(
            text = "Welcome back",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 4.dp)
        )

        Text(
            text = "Log in to your account to continue",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 24.dp)
        )

        // Email field
        Text(
            text = "Email address",
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = state.email,
            onValueChange = { event(LoginUiEvents.OnEmailChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Enter email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        // Password field
        Text(
            text = "Password",
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = state.password,
            onValueChange = { event(LoginUiEvents.OnPasswordChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            placeholder = { Text("Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
//                IconButton(onClick = { showPassword = !showPassword }) {
//                    Icon(
//                        painter = painterResource(
//                            id = if (showPassword) R.drawable.ic_visibility_on
//                            else R.drawable.ic_visibility_off
//                        ),
//                        contentDescription = if (showPassword) "Hide password" else "Show password"
//                    )
//                }
            }
        )

        // Remember me & Forgot password row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
//                        checkedColor = Purple40
                    ),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Remember me",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            TextButton(
                onClick = { event(LoginUiEvents.OnForgotPasswordClicked) }
            ) {
                Text(
                    text = "FORGOT PASSWORD?",
                    fontSize = 12.sp,
//                    color = Purple40
                )
            }
        }

        // Login button
        Button(
            onClick = { event(LoginUiEvents.OnLoginClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
//                containerColor = Purple40
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("LOGIN")
        }

        Spacer(modifier = Modifier.weight(1f))



        // Don't have an account
        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account?",
                fontSize = 14.sp,
                color = Color.Gray
            )

            TextButton(
                onClick = {event(LoginUiEvents.OnSignUpClicked)}
            ) {
                Text(
                    text = "SIGN UP",
                    fontSize = 14.sp,
//                    color = Purple40,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SmartAttendanceTheme {
        LoginScreen()
    }
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SmartAttendanceTheme {
        LoginScreenContent(
            state = state,
            event = viewModel::onEvent
        )
    }
}
