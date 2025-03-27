package com.smartattendnance.feature.auth.selectusertype

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartattendance.modulesui.design.ui.theme.SmartAttendanceTheme
import com.smartattendnance.modulesui.resources.R

@Composable
fun SelectUserTypeScreen(
    viewModel: SelectUserTypeViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    // Collect navigation events from the ViewModel
    LaunchedEffect(key1 = viewModel.navigationEvents) {
        viewModel.navigationEvents.collect { event ->
            // Navigation will be handled by the parent composable via collectAsStateWithLifecycle
        }
    }

    SelectUserTypeScreenContent(
        state = state.value,
        event = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectUserTypeScreenContent(
    state: SelectUserTypeUiState,
    event: (SelectUserTypeEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        // Logo/App Name
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
            text = "What type of user are you?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // User type options in a row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Student option
            UserTypeCard(
                userType = UserType.STUDENT,
                icon = com.smartattendnance.modulesui.resources.R.drawable.ic_student,
                title = "Student",
                description = "Mark your attendance",
                isSelected = state.selectedUserType == UserType.STUDENT,
                onClick = { event(SelectUserTypeEvent.UserTypeSelected(UserType.STUDENT)) }
            )
            
            // Lecturer option
            UserTypeCard(
                userType = UserType.LECTURER,
                icon = R.drawable.ic_lecturer,
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
            modifier = Modifier.fillMaxWidth(0.5f).align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Next button
        Button(
            onClick = { event(SelectUserTypeEvent.NextClicked) },
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
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "Next"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            .let {
                if (userType != UserType.ADMIN) {
                    it.weight(1f).aspectRatio(0.85f)
                } else {
                    it
                }
            },
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
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
            event = {}
        )
    }
}