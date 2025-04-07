package com.smartattendance.android.feature.lecturer.createsession

import androidx.lifecycle.ViewModel
import com.smartattendance.android.data.repository.AttendanceRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateSessionViewModel @Inject constructor(
    private val sessionRepository: AttendanceRepositoryImpl
): ViewModel () {

}