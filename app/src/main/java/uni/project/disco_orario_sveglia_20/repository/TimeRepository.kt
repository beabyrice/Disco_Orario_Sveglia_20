package uni.project.disco_orario_sveglia_20.repository

import java.time.LocalTime

object TimeRepository {

    fun isValidTime(timeString : String) : Boolean{
        val regexPattern = Regex("^([01]\\d|2[0-3]):([0-5]\\d)$") //example -> correct: 13:45 wrong : 25:72
        return regexPattern.matches(timeString)
    }

    fun getLongCurrentTime() : Long{
        return (LocalTime.now().toSecondOfDay() * 1000).toLong()
    }

    fun getLongSecondsFromString(time : String): Long{
        val (hoursStr, minutesStr) = time.split(":")
        val hours = hoursStr.toInt()
        val minutes = minutesStr.toInt()

        return ((hours * 3600 + minutes * 60)*1000).toLong()
    }
}