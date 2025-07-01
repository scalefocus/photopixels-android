package io.photopixels.data.storage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.photopixels.data.storage.database.entities.DeviceMediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceMediaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMediaData(deviceMediaEntities: List<DeviceMediaEntity>)

    @Update
    suspend fun updateMediaData(deviceMediaEntities: List<DeviceMediaEntity>)

    @Query("DELETE FROM device_media WHERE id=:photoId")
    suspend fun removeMediaData(photoId: Int)

    @Query("DELETE FROM device_media WHERE id in (:photoIds)")
    suspend fun removeMediaData(photoIds: List<Int>)

    @Query("SELECT * FROM device_media")
    fun getMediaData(): Flow<DeviceMediaEntity>

    @Query(
        "SELECT * FROM device_media WHERE serverItemHashId IS NULL AND isDeleted IS NULL AND " +
            "isAlreadyUploaded IS NULL AND hash IS NOT NULL ORDER BY dateCreated DESC"
    )
    fun getMediaForUpload(): Flow<List<DeviceMediaEntity>>

    @Query(
        "SELECT * FROM device_media WHERE hash IS NULL ORDER BY dateCreated DESC"
    )
    suspend fun getMediaWithMissingHashes(): List<DeviceMediaEntity>

    @Query("SELECT * FROM device_media WHERE hash IN (:hashes)")
    suspend fun getMediaByHashes(hashes: List<String>): List<DeviceMediaEntity>

    @Update
    fun updateMediaData(deviceMediaEntity: DeviceMediaEntity)

    @Query("SELECT id FROM device_media")
    suspend fun getMediaIds(): List<Int>

    @Query("DELETE FROM device_media")
    suspend fun clearMediaTable()
}
