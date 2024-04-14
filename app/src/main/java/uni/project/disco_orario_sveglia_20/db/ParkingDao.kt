package uni.project.disco_orario_sveglia_20.db

import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

interface ParkingDao {

    @Upsert
    suspend fun upsertParkingSession(parkingSession : Parking)

    @Delete
    suspend fun deleteParkingSession(parkingSession : Parking)

    @Query("SELECT * FROM parking_session")
    suspend fun getParking()
}