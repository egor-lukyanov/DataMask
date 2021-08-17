package foreground

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File

class Run(private val conn: DataMaskInterface) {

    fun main() {
        conn.doWork()
    }
}

class Worker(appContext: Context, workerParams: WorkerParameters):

    Worker(appContext, workerParams) {

    private val context = appContext

    @RequiresApi(Build.VERSION_CODES.R)
    override fun doWork(): Result {
        val dataDir = File(applicationContext.filesDir.absolutePath, "/data_mask")
        try {
            val dataMask =  DataMaskIP2File(
                connectionParams = mapOf("ip" to "192.168.88.35"),
                writerParams = mapOf("dir" to dataDir.absolutePath),
                envirParams = mapOf("context" to context)
            )
            dataMask.doWork()

        } catch (ex: Exception) {
            return Result.retry()
        }

        return Result.success()
    }
}

