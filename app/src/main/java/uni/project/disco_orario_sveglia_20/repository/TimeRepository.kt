package uni.project.disco_orario_sveglia_20.repository

object TimeRepository {

    fun isValidTime(timeString : String) : Boolean{
        val regexPattern = Regex("^([01]\\d|2[0-3]):([0-5]\\d)$")
        return regexPattern.matches(timeString)
    }

    fun getLongSecondsFromString(time : String): Long{
        val (hoursStr, minutesStr) = time.split(":")
        val hours = hoursStr.toInt()
        val minutes = minutesStr.toInt()

        return ((hours * 3600 + minutes * 60)*1000).toLong()
    }

    fun timerFormat(millisUntilFinished : Long) : String{
        val hours = (millisUntilFinished/1000)/3600
        val minutes = ((millisUntilFinished/1000)%3600)/60
        val seconds = (millisUntilFinished/1000)%60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}