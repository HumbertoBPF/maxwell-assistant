package com.example.maxwell.utils

import android.content.Context
import android.util.Log
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AdminInitiateAuthRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AuthFlowType
import com.example.maxwell.BuildConfig
import com.example.maxwell.data_store.Settings
import java.util.Calendar

fun getStaticCredentialsProvider() : StaticCredentialsProvider = StaticCredentialsProvider {
    accessKeyId = BuildConfig.AWS_ACCESS_KEY_ID
    secretAccessKey = BuildConfig.AWS_SECRET_ACCESS_KEY
}

suspend fun getIdToken(context: Context, onPost: (idToken: String?) -> Unit) {
    val settings = Settings(context)

    settings.getIdTokenExpiration().collect { idTokenExpiration ->
        val currentTimeMillis = Calendar.getInstance().timeInMillis

        if (idTokenExpiration == null || currentTimeMillis >= idTokenExpiration) {
            val idToken = authenticateOnUserPool()

            if (idToken != null) {
                settings.setIdToken(idToken)
            }

            onPost(idToken)
            return@collect
        }

        settings.getIdToken().collect {idToken ->
            onPost(idToken)
        }
    }
}

suspend fun authenticateOnUserPool(): String? {
    val authParams = mutableMapOf<String, String>()

    authParams["USERNAME"] = BuildConfig.AUTH_PARAMETER_USERNAME
    authParams["PASSWORD"] = BuildConfig.AUTH_PARAMETER_PASSWORD

    val authRequest = AdminInitiateAuthRequest {
        clientId = BuildConfig.CLIENT_ID
        userPoolId = BuildConfig.USER_POOL_ID
        authParameters = authParams
        authFlow = AuthFlowType.AdminNoSrpAuth
    }

    CognitoIdentityProviderClient {
        region = "us-east-1"
        credentialsProvider = getStaticCredentialsProvider()
    }.use { identityProviderClient ->
        val response = identityProviderClient.adminInitiateAuth(authRequest)
        return response.authenticationResult?.idToken
    }
}
