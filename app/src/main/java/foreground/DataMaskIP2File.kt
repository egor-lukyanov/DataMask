package foreground

import android.content.ContextWrapper
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.net.Socket
import java.util.*


object RandomStringUUID {
    fun uuid(): String {
        return UUID.randomUUID().toString()
    }
}

class DataMaskIpConnector(connectionParams: Map<String, String>): DataMaskConnector(connectionParams) {

    private val ip = connectionParams["ip"] ?: "127.0.0.1"
    private val port = connectionParams["port"]?.toInt() ?: 8888
    private lateinit var socket: Socket


    override fun connect(): Boolean {
        while (! connected) {
            try {
                socket = Socket(ip, port)
                socket.soTimeout = 7000
                connected = true
            } catch (e: java.net.ConnectException) {
                print("$e\n")
                sleep(2000)
                continue
            }
        }
        return connected
    }

    override fun disconnect() {
        socket.close()
        connected = false
    }

    override fun fetch(): Iterator<String> {
        if (socket.isConnected) {
            return BufferedReader(InputStreamReader(socket.getInputStream())).lineSequence().iterator()
        }
        return listOf<String>().iterator()
    }

    override fun send(data: String): Unit {
        socket.getOutputStream().write(data.toByteArray())
    }
}

class DataMaskFileWriter(writerParams: Map<String, Any>): DataMaskWriter(writerParams) {

    private val path = File(writerParams["dir"].toString())

    override fun write(line: String, fileName: String) {
        val file = File(path, fileName)
        file.appendText("$line\n")
    }
}

abstract class DataMaskIP2FileInterface(connectionParams: Map<String, String>, writerParams: Map<String, String>):
    DataMaskInterface(DataMaskIpConnector(connectionParams), DataMaskFileWriter(writerParams))

class DataMaskIP2File(connectionParams: Map<String, String>, writerParams: Map<String, Any>, envirParams: Map<String, Any>):
    DataMaskInterface(DataMaskIpConnector(connectionParams), DataMaskFileWriter(writerParams)) {

    private val startTransmit = "BEGIN"
    private val okResponse = "OK"
    private val finishTransmit = "FIN"

    private val context: ContextWrapper = envirParams["context"] as ContextWrapper
    private val workingDir = File(context.filesDir.absolutePath, "/data_mask")

    private val persistor = File(workingDir, "current_file.txt")

    private var fileName: String = getCurrentFile()
        set(value) {
            println(value)
            val name = "$value.txt"
            field = name
            persistor.writeText(name)
        }

    private fun clearDir() {
        workingDir.listFiles()?.filter { it.name !=  persistor.name }?.map { it.delete() }
    }


    private fun getCurrentFile(): String {
        return when (persistor.exists()) {
            true -> persistor.readText()
            false -> "noname.txt"
        }
    }

    private fun makeFileName(): String {
        return RandomStringUUID.uuid()
    }

    private fun mainLoop(): Unit {

        println("STARTING SERVICE LOOP")
        clearDir()

        while (true) {

            connector.connect()
            connector.send(startTransmit)

            this.fileName = makeFileName()

            val data = connector.fetch()

            try {
                for (line in data) {
                    when (line) {
                        startTransmit -> continue
                        finishTransmit -> {connector.disconnect()
                                            break}
                        else -> writer.write(line, this.fileName)
                    }
                }
                connector.disconnect()
            } catch (e: Exception) {
                print(e)

            }

        }

    }

    override fun doWork() {
        this.mainLoop()
    }

}