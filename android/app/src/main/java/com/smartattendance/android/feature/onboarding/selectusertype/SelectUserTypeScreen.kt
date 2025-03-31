package com.smartattendance.android.feature.onboarding.selectusertype

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samuelokello.smartattendance.android.R
import com.smartattendance.android.feature.auth.signup.SignUpViewModel
import com.smartattendance.modulesui.design.ui.theme.SmartAttendanceTheme

@Composable
fun SelectUserTypeScreen(
    viewModel: SelectUserTypeViewModel = hiltViewModel(),
    onNextClicked: (UserType) -> Unit
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    SmartAttendanceTheme {
        SelectUserTypeScreenContent(
            state = state.value,
            event = viewModel::onEvent,
            onNextClicked = onNextClicked
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectUserTypeScreenContent(
    state: SelectUserTypeUiState,
    event: (SelectUserTypeEvent) -> Unit,
    onNextClicked: (UserType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo/App Name
        Box(
            modifier = Modifier
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Smart Attendance",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Question text
        Text(
            text = "Select a User type",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

//         User type options in a row
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Student option
                UserTypeCard(
                    userType = UserType.STUDENT,
                    icon = R.drawable.ic_student,
                    title = "Student",
                    description = "Mark your attendance",
                    isSelected = state.selectedUserType == UserType.STUDENT,
                    onClick = { event(SelectUserTypeEvent.UserTypeSelected(UserType.STUDENT)) }
                )

                // Lecturer option
                UserTypeCard(
                    userType = UserType.LECTURER,
                    icon = R.drawable.ic_teacher,
                    title = "Lecturer",
                    description = "Track student attendance",
                    isSelected = state.selectedUserType == UserType.LECTURER,
                    onClick = { event(SelectUserTypeEvent.UserTypeSelected(UserType.LECTURER)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Admin option - takes full width
            UserTypeCard(
                userType = UserType.ADMIN,
                icon = R.drawable.ic_admin,
                title = "Admin",
                description = "Manage the system",
                isSelected = state.selectedUserType == UserType.ADMIN,
                onClick = { event(SelectUserTypeEvent.UserTypeSelected(UserType.ADMIN)) },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.CenterHorizontally)
            )
        }


        Spacer(modifier = Modifier.height(32.dp))

        // Next button
        Button(
            onClick = {
                onNextClicked(state.selectedUserType!!)
            },
            enabled = state.selectedUserType != null,
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
            )
        ) {
            Text("NEXT")
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next"
            )
        }

    }
}

@Composable
fun UserTypeCard(
    userType: UserType,
    icon: Int,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .width(170.dp)
            .height(180.dp)
            .shadow(
                elevation = 12.dp,
                spotColor = MaterialTheme.colorScheme.background,
                ambientColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
//            .shadow(elevation = 4.dp, spotColor = MaterialTheme.colorScheme.primary, ambientColor = MaterialTheme.colorScheme.onPrimary),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectUserTypeScreenPreview() {
    SmartAttendanceTheme {
        SelectUserTypeScreenContent(
            state = SelectUserTypeUiState(),
            event = {},
            onNextClicked = {}
        )
    }
}