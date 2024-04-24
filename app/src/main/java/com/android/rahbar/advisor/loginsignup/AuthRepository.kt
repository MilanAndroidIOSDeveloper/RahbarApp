package com.android.rahbar.advisor.loginsignup

import com.android.rahbar.advisor.R
import com.android.rahbar.advisor.Rahbarapplication
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor() {

    private val auth = Firebase.auth

    suspend fun login(email: String, password: String): Resource<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: Rahbarapplication.appContext.getString(R.string.login_failed))
        }
    }

    suspend fun signUp(email: String, password: String): Resource<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: Rahbarapplication.appContext.getString(R.string.sign_up_failed))
        }
    }
}