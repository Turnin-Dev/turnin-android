package com.peekr.data.login.util

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.auth.error.AuthErrorType
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.ProviderId
import com.peekr.data.BuildConfig
import com.peekr.domain.login.util.AuthManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(private val context: Context) : AuthManager {
    private val tag = this::class.java.simpleName
    private val auth = Firebase.auth
    private val credentialManager = CredentialManager.Companion.create(context)

    override fun signIn(): Flow<Result<ProviderId, AuthErrorType>> = flow {
        try {
            val googleIdOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption
                .Builder(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .build()

            val credentialRequest: GetCredentialRequest = GetCredentialRequest
                .Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialResponse = credentialManager.getCredential(
                request = credentialRequest,
                context = context,
            )

            emit(signInWithCredentialResponse(credentialResponse))
        } catch (e: GoogleIdTokenParsingException) {
            AppLogger.e(tag, "Failed to parse Google ID token.")
            emit(Result.Error(AuthErrorType.IdTokenParsing, e.message))
        } catch (e: GetCredentialCancellationException) {
            emit(Result.Error(AuthErrorType.Cancellation, e.message))
        } catch (e: Exception) {
            AppLogger.e(tag, "Unexpected error during Google sign-in.")
            emit(Result.Error(AuthErrorType.Unexpected(e), e.message))
        }
    }

    override fun signOut(): Flow<Result<Unit, AuthErrorType>> = flow {
        try {
            auth.signOut()
            credentialManager.clearCredentialState(
                ClearCredentialStateRequest(),
            )
            AppLogger.i(tag, "Google sign-out Succeeded.")
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            AppLogger.e(tag, e, "Failed to Google sign-out")
            emit(Result.Error(AuthErrorType.Unexpected(e), e.message))
        }
    }

    override fun deleteAccount(): Flow<Result<Unit, AuthErrorType>> = flow {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            emit(Result.Error(AuthErrorType.UserNotFound))
        } else {
            try {
                val task = currentUser.delete()

                if (task.isComplete && task.isSuccessful) {
                    credentialManager.clearCredentialState(
                        ClearCredentialStateRequest(),
                    )
                    AppLogger.i(tag, "Google account deleted.")
                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error(AuthErrorType.DeleteAccountFailed))
                }
            } catch (e: Exception) {
                AppLogger.e(tag, e, "Failed to delete Google account.")
                emit(Result.Error(AuthErrorType.DeleteAccountFailed, e.message))
            }
        }
    }

    // CredentialResponse를 통해 로그인을 진행하고 결과 값(사용자 uid)을 반환한다.
    private suspend fun signInWithCredentialResponse(
        credentialResponse: GetCredentialResponse,
    ): Result<ProviderId, AuthErrorType> =
        when (val credential = credentialResponse.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val firebaseUser = getFirebaseUser(credential)
                    firebaseUser?.let {
                        AppLogger.i(tag, "Firebase user fetched successfully.")
                        val providerId = ProviderId(firebaseUser.uid)
                        Result.Success(providerId)
                    } ?: Result.Error(AuthErrorType.UserNotFound)
                } else {
                    // 올바르지 않은 형태의 토큰
                    AppLogger.w(tag, "Google token type invalid.")
                    Result.Error(AuthErrorType.TokenTypeInvalid)
                }
            }

            else -> {
                // 올바르지 않은 형태의 토큰
                AppLogger.w(tag, "Google token type invalid.")
                Result.Error(AuthErrorType.TokenTypeInvalid)
            }
        }

    // credential을 통해 Firebase 사용자를 가져온다.
    private suspend fun getFirebaseUser(credential: Credential): FirebaseUser? {
        val googleIdTokenCredential = GoogleIdTokenCredential.Companion.createFrom(credential.data)
        val googleIdToken = googleIdTokenCredential.idToken
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        val firebaseUser = auth.signInWithCredential(firebaseCredential).await().user
        return firebaseUser
    }
}
