package io.photopixels.presentation.login

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.photopixels.domain.base.GoogleAuth

@Module
@InstallIn(SingletonComponent::class)
interface GoogleAuthModule {

    @Binds
    fun bindGoogleAuth(googleAuthorization: GoogleAuthorization): GoogleAuth
}
