package uni.project.disco_orario_sveglia_20.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uni.project.disco_orario_sveglia_20.model.Parking

@Database(
    entities = [Parking::class],
    version = 1
)
abstract class ParkingDatabase: RoomDatabase() {
    abstract val dao: ParkingDao

    companion object{
        @Volatile
        private var instance: ParkingDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?:
        synchronized(LOCK){
            instance ?:
            createDB(context).also{
                instance = it
            }
        }

        private fun createDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ParkingDatabase::class.java,
                "parking_db"
            ).build()
    }
}