package com.peekr.data.account.util

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
import com.peekr.data.BuildConfig
import com.peekr.domain.account.model.ProviderId
import com.peekr.domain.account.util.AuthManager
import com.peekr.domain.common.util.ErrorType
import com.peekr.domain.common.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class GoogleAuthManager(private val context: Context) : AuthManager {
    private val auth = Firebase.auth
    private val credentialManager = CredentialManager.create(context)

    override fun signIn(): Flow<Result<ProviderId, ErrorType>> = flow {
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
            Timber.e(e, "Cannot parsing google id token.")
            emit(Result.Error(ErrorType.Auth.IdTokenParsing, e.message))
        } catch (e: GetCredentialCancellationException) {
            emit(Result.Error(ErrorType.Auth.Cancellation, e.message))
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error during Google sign-in.")
            emit(Result.Error(ErrorType.Unexpected(e), e.message))
        }
    }

    override fun signOut(): Flow<Result<Unit, ErrorType>> = flow {
        try {
            auth.signOut()
            credentialManager.clearCredentialState(
                ClearCredentialStateRequest(),
            )
            Timber.i("Google sign-out Succeeded.")
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            Timber.e(e, "Failed to Google sign-out")
            emit(Result.Error(ErrorType.Unexpected(e), e.message))
        }
    }

    override fun deleteAccount(): Flow<Result<Unit, ErrorType>> = flow {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            emit(Result.Error(ErrorType.Auth.UserNotFound))
        } else {
            try {
                val task = currentUser.delete()

                if (task.isComplete && task.isSuccessful) {
                    credentialManager.clearCredentialState(
                        ClearCredentialStateRequest(),
                    )
                    Timber.i("Google account deleted.")
                    emit(Result.Success(Unit))
                } else {
                    emit(Result.Error(ErrorType.Auth.DeleteAccountFailed))
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete Google account.")
                emit(Result.Error(ErrorType.Auth.DeleteAccountFailed, e.message))
            }
        }
    }

    // CredentialResponse를 통해 로그인을 진행하고 결과 값(사용자 uid)을 반환한다.
    private suspend fun signInWithCredentialResponse(credentialResponse: GetCredentialResponse): Result<ProviderId, ErrorType> =
        when (val credential = credentialResponse.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val firebaseUser = getFirebaseUser(credential)
                    firebaseUser?.let {
                        Timber.i("Firebase user fetched successfully.")
                        val providerId = ProviderId(firebaseUser.uid)
                        Result.Success(providerId)
                    } ?: Result.Error(ErrorType.Auth.UserNotFound)
                } else {
                    // 올바르지 않은 형태의 토큰
                    Timber.w("Google token type invalid.")
                    Result.Error(ErrorType.Auth.TokenTypeInvalid)
                }
            }

            else -> {
                // 올바르지 않은 형태의 토큰
                Timber.w("Google token type invalid.")
                Result.Error(ErrorType.Auth.TokenTypeInvalid)
            }
        }

    // credential을 통해 Firebase 사용자를 가져온다.
    private suspend fun getFirebaseUser(credential: Credential): FirebaseUser? {
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val googleIdToken = googleIdTokenCredential.idToken
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        val firebaseUser = auth.signInWithCredential(firebaseCredential).await().user
        return firebaseUser
    }
}
