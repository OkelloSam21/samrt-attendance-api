package com.smartattendnance.feature.auth.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartattendance.modulesui.design.ui.theme.SmartAttendanceTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
//    userType: UserType = UserType.STUDENT,
    onSignUpClicked: (userData: Map<String, String>) -> Unit = { },
    onBackClicked: () -> Unit = {},
    onLoginClicked: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var timezone by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("") }
    var yearsOfExperience by remember { mutableStateOf("") }
    var linkedInUrl by remember { mutableStateOf("") }
    var fieldsOfInterest by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Text(
            text = "Smart Attendance",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
//            color = Purple40,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        )


        Text(
            text = "Welcome to Smart Attendance",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 4.dp)
        )

        Text(
            text = "Create an account to continue",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 24.dp)
        )

        // Phone number field
        Text(
            text = "Name",
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Enter your name") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
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
            value = email,
            onValueChange = { email = it },
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
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            placeholder = { Text("Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
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

        // Terms and conditions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = acceptTerms,
                onCheckedChange = { acceptTerms = it },
                colors = CheckboxDefaults.colors(
//                    checkedColor = Purple40
                ),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "I AGREE TO TERMS & CONDITIONS",
                fontSize = 12.sp,
                color = Color.DarkGray
            )
        }

        // Sign up button
        Button(
            onClick = {
                val userData = mapOf(
                    "phone" to name,
                    "email" to email,
                    "password" to password,
                    "fullName" to fullName,
                    "gender" to gender,
                    "dateOfBirth" to dateOfBirth,
                    "nationality" to nationality,
                    "timezone" to timezone,
                    "jobTitle" to jobTitle,
                    "yearsOfExperience" to yearsOfExperience,
                    "linkedInUrl" to linkedInUrl,
                    "fieldsOfInterest" to fieldsOfInterest
                )
                onSignUpClicked(userData)
            },
            enabled = email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && acceptTerms,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
//                containerColor = Purple40,
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("SIGN UP")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Social login
        Text(
            text = "OR",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Social login icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
//            SocialButton(icon = R.drawable.ic_twitter)
//            SocialButton(icon = R.drawable.ic_google)
//            SocialButton(icon = R.drawable.ic_apple)
//            SocialButton(icon = R.drawable.ic_linkedin)
        }

        // Already have an account
        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account?",
                fontSize = 14.sp,
                color = Color.Gray
            )

            TextButton(
                onClick = onLoginClicked
            ) {
                Text(
                    text = "LOG IN",
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
private fun SignUpPreview(){
    SmartAttendanceTheme {
        SignUpScreen(
            onLoginClicked = {},
            onSignUpClicked = {},
            onBackClicked = {}
        )
    }
}