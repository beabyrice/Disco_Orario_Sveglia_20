package uni.project.disco_orario_sveglia_20.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import uni.project.disco_orario_sveglia_20.model.Parking

@Dao
interface ParkingDao {
    @Upsert
    suspend fun upsertParkingSession(parkingSession : Parking)

    @Delete
    suspend fun deleteParkingSession(parkingSession : Parking)

    //TODO: delete id
    @Query("SELECT * FROM parking_session WHERE id = :id")
    suspend fun getParking(id: Int = 0) : Parking

}