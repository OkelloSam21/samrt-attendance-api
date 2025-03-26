//package com.smartattendnance.feature.auth.signup
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Button
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PersonalDetailsScreen(
//    onBackClicked: () -> Unit = {},
//    onContinueClicked: (personalData: Map<String, String>) -> Unit = { }
//) {
//    val scrollState = rememberScrollState()
//
//    var fullName by remember { mutableStateOf("") }
//    var gender by remember { mutableStateOf("") }
//    var dateOfBirth by remember { mutableStateOf("") }
//    var nationality by remember { mutableStateOf("") }
//    var timezone by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(scrollState)
//            .padding(horizontal = 24.dp, vertical = 16.dp)
//    ) {
//        // Progress indicator
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 16.dp),
//            horizontalArrangement = Arrangement.Center
//        ) {
////            ProgressStep(step = 1, completed = true)
////            ProgressLine(completed = false)
////            ProgressStep(step = 2, completed = true)
////            ProgressLine(completed = false)
////            ProgressStep(step = 3, completed = false)
//        }
//
//        // Full name field
//        Text(
//            text = "Full name",
//            fontSize = 14.sp,
//            modifier = Modifier
//                .align(Alignment.Start)
//                .padding(bottom = 8.dp)
//        )
//
//        OutlinedTextField(
//            value = fullName,
//            onValueChange = { fullName = it },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp),
//            placeholder = { Text("Enter full name") },
//            singleLine = true,
//            shape = RoundedCornerShape(8.dp)
//        )
//
//        // Gender field
//        Text(
//            text = "Gender",
//            fontSize = 14.sp,
//            modifier = Modifier
//                .align(Alignment.Start)
//                .padding(bottom = 8.dp)
//        )
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
////            RadioButtonWithLabel(
////                selected = gender == "Male",
////                onClick = { gender = "Male" },
////                label = "Male"
////            )
////
////            Spacer(modifier = Modifier.width(24.dp))
////
////            RadioButtonWithLabel(
////                selected = gender == "Female",
////                onClick = { gender = "Female" },
////                label = "Female"
////            )
//        }
//
//        // Date of birth field
//        Text(
//            text = "Date of birth",
//            fontSize = 14.sp,
//            modifier = Modifier
//                .align(Alignment.Start)
//                .padding(bottom = 8.dp)
//        )
//
//        OutlinedTextField(
//            value = dateOfBirth,
//            onValueChange = { dateOfBirth = it },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp),
//            placeholder = { Text("DD/MM/YYYY") },
//            trailingIcon = {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_calendar),
//                    contentDescription = "Select date"
//                )
//            },
//            singleLine = true,
//            shape = RoundedCornerShape(8.dp)
//        )
//
//        // Nationality field
//        Text(
//            text = "Nationality",
//            fontSize = 14.sp,
//            modifier = Modifier
//                .align(Alignment.Start)
//                .padding(bottom = 8.dp)
//        )
//
//        OutlinedTextField(
//            value = nationality,
//            onValueChange = { nationality = it },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp),
//            placeholder = { Text("Select country") },
//            readOnly = true,
//            trailingIcon = {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_dropdown),
//                    contentDescription = "Select country"
//                )
//            },
//            singleLine = true,
//            shape = RoundedCornerShape(8.dp)
//        )
//
//        // Timezone field
//        Text(
//            text = "Timezone",
//            fontSize = 14.sp,
//            modifier = Modifier
//                .align(Alignment.Start)
//                .padding(bottom = 8.dp)
//        )
//
//        OutlinedTextField(
//            value = timezone,
//            onValueChange = { timezone = it },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 24.dp),
//            placeholder = { Text("Select timezone") },
//            readOnly = true,
//            trailingIcon = {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_dropdown),
//                    contentDescription = "Select timezone"
//                )
//            },
//            singleLine = true,
//            shape = RoundedCornerShape(8.dp)
//        )
//
//        // Navigation buttons
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            OutlinedButton(
//                onClick = onBackClicked,
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(end = 8.dp),
//                border = BorderStroke(1.dp, Purple40),
//                shape = RoundedCornerShape(8.dp)
//            ) {
//                Text(
//                    text = "BACK",
//                    color = Purple40
//                )
//            }
//
//            Button(
//                onClick = {
//                    val personalData = map