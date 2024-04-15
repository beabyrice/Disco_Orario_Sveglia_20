package uni.project.disco_orario_sveglia_20.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parking_session")
data class Parking(

    @PrimaryKey
    val id: Int = 0,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "arrival_time")
    val arrivalTime: Long,

    @ColumnInfo(name = "parking_duration")
    val parkingDuration: Long

)
