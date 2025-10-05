package com.example.musep50.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musep50.data.AppDatabase
import com.example.musep50.data.Repository
import com.example.musep50.data.entities.User
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository = Repository(AppDatabase.getDatabase(application))

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    fun login(email: String, pin: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            if (user != null && user.pin == pin) {
                _loginResult.value = LoginResult.Success(user)
            } else {
                _loginResult.value = LoginResult.Error("Email ou PIN incorrect")
            }
        }
    }

    fun register(nom: String, email: String, telephone: String?, organisation: String?, pin: String) {
        viewModelScope.launch {
            try {
                val existingUser = repository.getUserByEmail(email)
                if (existingUser != null) {
                    _registerResult.value = RegisterResult.Error("Un compte avec cet email existe déjà")
                    return@launch
                }

                val user = User(
                    nom = nom,
                    email = email,
                    telephone = telephone,
                    organisation = organisation,
                    pin = pin
                )
                val userId = repository.insertUser(user)
                val createdUser = repository.getUserById(userId)
                _registerResult.value = RegisterResult.Success(createdUser!!)
            } catch (e: Exception) {
                _registerResult.value = RegisterResult.Error("Erreur lors de la création du compte: ${e.message}")
            }
        }
    }

    fun getUserById(userId: Long): LiveData<User?> {
        val result = MutableLiveData<User?>()
        viewModelScope.launch {
            result.value = repository.getUserById(userId)
        }
        return result
    }
}

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

sealed class RegisterResult {
    data class Success(val user: User) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}