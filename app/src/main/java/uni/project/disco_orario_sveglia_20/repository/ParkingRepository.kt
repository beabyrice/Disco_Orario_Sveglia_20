package uni.project.disco_orario_sveglia_20.repository

import uni.project.disco_orario_sveglia_20.db.ParkingDatabase
import uni.project.disco_orario_sveglia_20.model.Parking

class ParkingRepository(private val db:ParkingDatabase) {
    suspend fun upsertParking(parking: Parking) = db.dao.upsertParkingSession(parking)
    suspend fun deleteParking(parking: Parking) = db.dao.deleteParkingSession(parking)
}