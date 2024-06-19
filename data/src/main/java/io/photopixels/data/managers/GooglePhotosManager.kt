package io.photopixels.data.managers

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.Credentials
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.UserCredentials
import com.google.photos.library.v1.PhotosLibraryClient
import com.google.photos.library.v1.PhotosLibrarySettings
import com.google.photos.types.proto.MediaItem
import io.photopixels.data.BuildConfig
import io.photopixels.data.mappers.toEntity
import io.photopixels.data.storage.database.GooglePhotosDao
import io.photopixels.data.storage.datastore.AuthDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GooglePhotosManager @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val googlePhotosDao: GooglePhotosDao
) {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var photosLibraryClient: PhotosLibraryClient

    init {
        coroutineScope.launch {
            initPhotosLibrary()
        }
    }

    fun fetchGooglePhotos() {
        fetchPhotos()
    }

    private suspend fun initPhotosLibrary() {
        Timber.tag(GOOGLE_PHOTOS_TAG).d("init google photos library")
        val googleAuthToken = authDataStore.getGoogleAuthToken()
        googleAuthToken?.let {
            val settings = PhotosLibrarySettings
                .newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(getUserCredentials(it)))
                .build()

            photosLibraryClient = PhotosLibraryClient.initialize(settings)
            Timber.tag(GOOGLE_PHOTOS_TAG).d("Initialization successful")
        }
    }

    private fun getUserCredentials(googleAuthToken: String): Credentials {
        Timber.tag(GOOGLE_PHOTOS_TAG).d("init google photos library with google auth token:$googleAuthToken")
        val accessToken = AccessToken(googleAuthToken, null)
        return UserCredentials
            .newBuilder()
            .setClientId(BuildConfig.GOOGLE_OAUTH_WEB_CLIENT_ID)
            .setClientSecret(BuildConfig.GOOGLE_OAUTH_WEB_CLIENT_SECRET)
            .setAccessToken(accessToken)
            .build()
    }

    private fun fetchPhotos() {
        coroutineScope.launch(Dispatchers.IO) {
            Timber.tag(GOOGLE_PHOTOS_TAG).d("in fetchPhotos()")
            if (!::photosLibraryClient.isInitialized) {
                Timber.tag(GOOGLE_PHOTOS_TAG).d("library is not initialized")
                initPhotosLibrary()
                Timber.tag(GOOGLE_PHOTOS_TAG).d("library initialized")
            } else {
                Timber.tag(GOOGLE_PHOTOS_TAG).d("library initialized already")
            }

            try {
                Timber.tag(GOOGLE_PHOTOS_TAG).d("Try List media Items")
                val response = photosLibraryClient.listMediaItems()
                // response contains list of photo objects
                val mediaItems = response.iterateAll().toList()
                for (mediaItem in mediaItems) {
                    // Access media item properties
                    val id = mediaItem.id
                    val baseUrl = mediaItem.baseUrl
                    Timber.tag(GOOGLE_PHOTOS_TAG).d("fetchPhotos: each item : $mediaItem")
                }

                savePhotosDataToDB(mediaItems)
            } catch (e: Exception) {
                Timber.tag(GOOGLE_PHOTOS_TAG).e("fetchPhotos: exception handled ====$e")
            }
        }
    }

    private suspend fun savePhotosDataToDB(mediaItemsList: List<MediaItem>) {
        val googlePhotosList = mediaItemsList.map { it.toEntity() }
        googlePhotosDao.insertPhotosData(googlePhotosList)
    }

    companion object {
        private const val GOOGLE_PHOTOS_TAG = "GOOGLE_PHOTOS"
    }
}
