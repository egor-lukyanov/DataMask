package foreground

import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.ByteBuffer

class DataParser(val packet: ByteArray) {

    var result = mutableMapOf<String, Number>()

    private val sensors = mapOf(
        16 to "eco2", 17 to "o2", 18 to "t_body", 19 to "humidity",
        20 to "tvoc", 21 to "pressure", 22 to "t_ext", 23 to "ecg",
        24 to "ax", 25 to "ay", 26 to "az", 27 to "gx", 28 to "gy",
        29 to "gz", 30 to "mx", 31 to "my", 32 to "mz", 33 to "spo2_red",
        34 to "spo2_ir", 35 to "spo2_green", 36 to "spo2_lvl",
        37 to "alt", 38 to "hr", 39 to "steps", 40 to "pwr_lvl",
        41 to "tvoc_ext", 42 to "eco2_ext", 43 to "hum_ext"
    )

    private fun hasCorrectFlags(): Boolean {
        return (packet.sliceArray(0 .. 1).toString() == "f0aa") and
                (packet.sliceArray(packet.size - 2 until packet.size).toString() == "f0aa")
    }

    private fun hasCorrectLength(): Boolean {
        println(packet.sliceArray(2 .. 3))
        return  ByteBuffer.wrap(packet.sliceArray(2 .. 3)).int == packet.size - 6
    }

    private fun getTime(): Int {
        return ByteBuffer.wrap(packet.sliceArray(4 .. 7)).int
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun parseData(): Boolean  {
        if (this.hasCorrectFlags() and this.hasCorrectLength()) {
            result["_time"] = getTime()

            var i = 0
            while (i < (packet.size - 4) / 5) {

                val sensor = sensors.getOrDefault(
                    ByteBuffer.wrap(packet.sliceArray(8 + i * 5 .. 9 + i * 5)).int,
                    "undefined_sensor"
                )
                val value = ByteBuffer.wrap(packet.sliceArray(9 + i * 5 .. 9 + i * 5 + 4)).float
                result[sensor] = value
                i += 1

            }

            return true

        }

        return false
    }

}

@RequiresApi(Build.VERSION_CODES.N)
fun main() {
    val x = DataParser(byteArrayOf(1,2,3,4,5,6,7,8))
    println(x.parseData())
}

