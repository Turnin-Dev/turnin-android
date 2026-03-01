package com.peekr.core.data.source.network.social

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
import com.peekr.core.data.BuildConfig
import com.peekr.core.domain.auth.social.SocialAuthManager
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.ProviderId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class GoogleSocialAuthManager(private val context: Context) : SocialAuthManager {
    private val tag = this::class.java.simpleName
    private val auth = Firebase.auth
    private val credentialManager = CredentialManager.Companion.create(context)

    override fun signIn(): Flow<Result<ProviderId, CommonErrorType>> = flow {
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
            emit(Result.Error(CommonErrorType.SocialAuth.IdTokenParsing, e.message))
        } catch (e: GetCredentialCancellationException) {
            emit(Result.Error(CommonErrorType.SocialAuth.Cancellation, e.message))
        } catch (e: Exception) {
            AppLogger.e(tag, "Unexpected error during Google sign-in.")
            emit(Result.Error(CommonErrorType.SocialAuth.Unexpected(e), e.message))
        }
    }

    override suspend fun signOut(): Result<Unit, CommonErrorType> = try {
        auth.signOut()
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest(),
        )
        AppLogger.i(tag, "Google sign-out Succeeded.")
        Result.Success(Unit)
    } catch (e: Exception) {
        AppLogger.e(tag, e, "Failed to Google sign-out")
        Result.Error(CommonErrorType.SocialAuth.Unexpected(e), e.message)
    }

    override suspend fun deleteAccount(): Result<Unit, CommonErrorType> {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            return Result.Error(CommonErrorType.SocialAuth.UserNotFound)
        } else {
            return try {
                val task = currentUser.delete()

                if (task.isComplete && task.isSuccessful) {
                    credentialManager.clearCredentialState(
                        ClearCredentialStateRequest(),
                    )
                    AppLogger.i(tag, "Google account deleted.")
                    Result.Success(Unit)
                } else {
                    Result.Error(CommonErrorType.SocialAuth.DeleteAccountFailed)
                }
            } catch (e: Exception) {
                AppLogger.e(tag, e, "Failed to delete Google account.")
                return Result.Error(CommonErrorType.SocialAuth.DeleteAccountFailed, e.message)
            }
        }
    }

    // CredentialResponse를 통해 로그인을 진행하고 결과 값(사용자 uid)을 반환한다.
    private suspend fun signInWithCredentialResponse(
        credentialResponse: GetCredentialResponse,
    ): Result<ProviderId, CommonErrorType> =
        when (val credential = credentialResponse.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val firebaseUser = getFirebaseUser(credential)
                    firebaseUser?.let {
                        AppLogger.i(tag, "Firebase user fetched successfully.")
                        val providerId = ProviderId(firebaseUser.uid)
                        Result.Success(providerId)
                    } ?: Result.Error(CommonErrorType.SocialAuth.UserNotFound)
                } else {
                    // 올바르지 않은 형태의 토큰
                    AppLogger.w(tag, "Google token type invalid.")
                    Result.Error(CommonErrorType.SocialAuth.TokenTypeInvalid)
                }
            }

            else -> {
                // 올바르지 않은 형태의 토큰
                AppLogger.w(tag, "Google token type invalid.")
                Result.Error(CommonErrorType.SocialAuth.TokenTypeInvalid)
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
