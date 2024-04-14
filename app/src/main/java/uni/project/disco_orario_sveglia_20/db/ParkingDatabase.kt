package uni.project.disco_orario_sveglia_20.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Parking::class],
    version = 1
)
abstract class ParkingDatabase: RoomDatabase() {
    abstract val dao: ParkingDao
}