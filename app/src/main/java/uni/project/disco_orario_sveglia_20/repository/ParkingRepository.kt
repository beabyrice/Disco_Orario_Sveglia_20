package uni.project.disco_orario_sveglia_20.repository

import android.content.Context
import uni.project.disco_orario_sveglia_20.db.ParkingDatabase
import uni.project.disco_orario_sveglia_20.model.Parking

class ParkingRepository(context: Context) {
    private val db = ParkingDatabase.invoke(context)
    private val parkingDao = db.dao()
    suspend fun upsertParking(parking: Parking) = parkingDao.upsertParkingSession(parking)
    suspend fun deleteParking(parking: Parking) = parkingDao.deleteParkingSession(parking)
    suspend fun getParking() = parkingDao.getParking()
}