package uni.project.disco_orario_sveglia_20.alarm

class CountDownTimer(
    millisInFuture : Long,
    countDownInterval : Long
) {
    private var millisUntilFinished = millisInFuture
    private var timer = Timer(this, millisInFuture, countDownInterval)
    private var isRunning = false
    var onTick: ((millisUntilFinished:Long) -> Unit)? = null
    var onFinish: (() -> Unit)? = null

    private class Timer(
        private val parent: CountDownTimer,
        millisInFuture: Long,
        countDownInterval: Long
    ) : android.os.CountDownTimer(millisInFuture, countDownInterval){

        var millisUntilFinished = parent.millisUntilFinished
        override fun onTick(millisUntilFinished: Long) {
            this.millisUntilFinished = millisUntilFinished
            parent.onTick?.invoke(millisUntilFinished)
        }

        override fun onFinish() {
            millisUntilFinished = 0
            parent.onFinish?.invoke()
        }

    }

    fun startTimer(){
        timer.start()
        isRunning = true
    }

    fun destroyTimer(){

    }
}