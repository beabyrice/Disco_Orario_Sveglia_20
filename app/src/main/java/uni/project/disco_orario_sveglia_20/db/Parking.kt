package uni.project.disco_orario_sveglia_20.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parking_session")
data class Parking(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "latitude")
    val latitude: Float,

    @ColumnInfo(name = "longitude")
    val longitude: Float,

    @ColumnInfo(name = "arrival_time")
    val arrivalTime: Long,

    @ColumnInfo(name = "parking_duration")
    val parkingDuration: Long

)
