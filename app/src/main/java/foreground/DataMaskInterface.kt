package foreground


abstract class DataMaskInterface(var connector: DataMaskConnector, var writer: DataMaskWriter) {
    abstract fun doWork()
}

abstract class DataMaskConnector(connectionParams: Map<String, String>) {
    var connected = false
    abstract fun connect(): Boolean
    abstract fun disconnect()
    abstract fun fetch(): Iterator<String>
    abstract fun send(data: String)
}

abstract class DataMaskWriter(writingParams: Map<String, Any>) {
    abstract fun write(line: String, fileName: String = "data.txt")
}

