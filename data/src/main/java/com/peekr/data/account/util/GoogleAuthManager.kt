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
import com.peekr.domain.account.model.UserUID
import com.peekr.domain.account.util.AuthManager
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(private val context: Context) : AuthManager {
    private val auth = Firebase.auth
    private val credentialManager = CredentialManager.create(context)

    override suspend fun signIn(): Result<UserUID, ErrorType> = try {
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

        signInWithCredentialResponse(credentialResponse)
    } catch (e: GoogleIdTokenParsingException) {
        // Google ID Token 파싱 예외
        Result.Error(ErrorType.Auth.IdTokenParsing, e.message)
    } catch (e: GetCredentialCancellationException) {
        // 인증이 취소될 때 예외
        Result.Error(ErrorType.Auth.Cancellation, e.message)
    } catch (e: Exception) {
        // 이 외의 예외
        Result.Error(ErrorType.Auth.Unexpected, e.message)
    }

    override suspend fun signOut(): Result<Unit, ErrorType> = try {
        auth.signOut()
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest(),
        )
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(ErrorType.Auth.Unexpected, e.message)
    }

    override suspend fun deleteAccount(): Result<Unit, ErrorType> {
        val currentUser = auth.currentUser

        if (currentUser == null) return Result.Error(ErrorType.Auth.UserNotFound)

        return try {
            val task = currentUser.delete()

            if (task.isComplete && task.isSuccessful) {
                credentialManager.clearCredentialState(
                    ClearCredentialStateRequest(),
                )
                Result.Success(Unit)
            } else {
                Result.Error(ErrorType.Auth.DeleteAccountFailed)
            }
        } catch (e: Exception) {
            Result.Error(ErrorType.Auth.Unexpected, e.message)
        }
    }

    // CredentialResponse를 통해 로그인을 진행하고 결과 값(사용자 uid)을 반환한다.
    private suspend fun signInWithCredentialResponse(credentialResponse: GetCredentialResponse): Result<UserUID, ErrorType> =
        when (val credential = credentialResponse.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val firebaseUser = getFirebaseUser(credential)
                    firebaseUser?.let {
                        val userUID = UserUID(firebaseUser.uid)
                        Result.Success(userUID)
                    } ?: Result.Error(ErrorType.Auth.UserNotFound)
                } else {
                    // 올바르지 않은 형태의 토큰
                    Result.Error(ErrorType.Auth.TokenTypeInvalid)
                }
            }

            else -> {
                // 올바르지 않은 형태의 토큰
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
