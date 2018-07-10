package ClientSide

import Utils.GeneradorArchivo
import Utils.TablaTiempos
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class ClientKt {

    private var client: Socket = Socket()
    private lateinit var reader: Scanner
    private lateinit var writer: OutputStream
    private var running: Boolean = true

    fun run() {
        try {
            val hostAddress = readLine()
            println("Tratando de conectar a " + hostAddress)
            client.connect(InetSocketAddress(hostAddress,9999))
        }catch (e: Exception){
            println(e.message)
            e.printStackTrace()
        }
        reader = Scanner(client.getInputStream())
        writer = client.getOutputStream()

        var textIn = reader.nextLine()
        println("MENSAJE DEL SERVIDOR:\n" + textIn)

        while (running) {

            try {
                println("Escriba su mensaje:")
                val keyboard = Scanner(System.`in`)
                val textOut: String = keyboard.nextLine()

                val textParts = textOut.split(" ")
                when (textParts[0]){
                    "TRANSMITIR" -> {
                        //Analizamos el path de la entrada
                        if (textParts.size > 1){
                            when (textParts.size){
                                2 -> {
                                    //Creación de archivos
                                    for (i in 1..textParts[1].toInt())
                                        GeneradorArchivo.getInstance().generateRandomSizeFile();
                                    println("Tamaño medio generado fue de ${GeneradorArchivo.getInstance().meanSize} KB\n")
                                }
                                else -> {
                                    //Creación de archivos
                                    for (i in 1..textParts[1].toInt())
                                        GeneradorArchivo.getInstance().generateFileOfSpecificSize(textParts[2].toInt())
                                    println("Tamaño medio generado fue de ${GeneradorArchivo.getInstance().meanSize} KB\n")
                                }
                            }

                            //Inicializamos el registro de tiempos
                            val registro = TablaTiempos()
                            //Vemos si está en el generador de archivos
                            val listFiles: LinkedList<javafx.util.Pair<Int, String>> = GeneradorArchivo.getInstance().filesList
                            listFiles.forEach {
                                //Iniciamos el timer
                                registro.startTimer()
                                //Inicia la transmisión
                                transmitir(it.value, it.key)
                                //Detenemos el tiempo y registramos
                                registro.stopTimer()
                                registro.registerNewTime(it.value,it.key)
                            }
                            //Exportamos el registro de tiempos
                            registro.exportToCSV("registro1")

                        }else {
                            println("Ingrese más parámetros")
                        }

                    }

                    "SALIR" -> shutdown()

                    else -> {
                        write(textOut)
                        println("MENSAJE ENVIADO:\n\t" + textOut)

                        textIn = reader.nextLine()
                        println("MENSAJE DEL SERVIDOR:\n" + textIn)
                    }

                }

            } catch (ex: Exception) {
                shutdown()
            }

        }
    }

    fun transmitir(path: String, size: Int): Boolean{
        write("TRANSMITIR " + path + " " + size)
        println("Intentando transmitir el archivo $path...")

        //println("MENSAJE DEL SERVIDOR:\n" + reader.nextLine())
        reader.nextLine()
        println("Transmitiendo archivo $path -------------------------------TOTAL SIZE: $size KB")
        var currentKB = 0
        File(path).forEachBlock( 1024, { messageSegment, segmentSize -> run{
            write(messageSegment.toString(Charset.defaultCharset()))

            reader.nextLine()
            currentKB++
            if (currentKB % (size / 50) == 0) print("█")
        }
        })

        write("TERMINAR")

        reader.nextLine()
        println("\n$path ha sido transmitido correctamente\n")
        return true
    }

    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }

    private fun shutdown() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")

        GeneradorArchivo.getInstance().deleteAllFilesGenerated();
    }

}