package com.smartattendnance.feature.auth.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
//    private val loginRepository: LoginRepository
) : ViewModel() {

    fun login(username: String, password: String) {

    }
}
