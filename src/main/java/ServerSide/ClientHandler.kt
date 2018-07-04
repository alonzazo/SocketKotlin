package ServerSide
import Utils.GeneradorArchivo
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class ClientHandler (client: Socket) {
    private val client: Socket = client
    private val reader: Scanner = Scanner(client.getInputStream())
    private val writer: OutputStream = client.getOutputStream()
    private var running: Boolean = false
    private var tempSize: Int = 0

    fun run() {
        running = true
        // Welcome message
        write("Welcome to the server!" +
                "To Exit, write: 'EXIT'.")

        var writingFile: Boolean = false
        while (running) {
            try {
                val text = reader.nextLine()
                if (text == "EXIT"){
                    shutdown()
                    continue
                }

                var words: List<String>  = text.split(" ")
                if (!GeneradorArchivo.getInstance().isWriting){
                    when (words[0]){
                        "TRANSMITIR" -> {
                            try {
                                if (words[1].trim() != "" && words[2].trim() != ""){
                                    write("Iniciando recepción de archivo: ${words[1]}-----------------------------------${words[2]} KB")
                                    println("Iniciando recepción de archivo: ${words[1]}-----------------------------------${words[2]} KB")

                                    GeneradorArchivo.getInstance().createFile("D" + words[1])
                                    tempSize = words[2].toInt()

                                }else {
                                    write("ERROR Nombre del archivo inválido")
                                    println("Error en el nombre del archivo")
                                }
                            }
                            catch (e: IndexOutOfBoundsException){
                                write("ERROR Nombre del archivo inválido")
                                println("Error en el nombre del archivo")
                            }
                        }
                        else -> {
                            println("MENSAJE RECIBIDO:\n\t" + text)
                            write("\tEsto es una respuesta a su mensaje")
                        }
                    }
                } else {
                    when (words[0]){
                        "TERMINAR" -> {
                            println("\n${GeneradorArchivo.getInstance().tempFilePath} ha sido recibido correctamente\n")
                            GeneradorArchivo.getInstance().closeFile()
                            write("\tServidor ha cerrado el archivo")

                        }
                        else -> {
                            write("\tServidor ha escrito ${text.length/1024} KB")
                            GeneradorArchivo.getInstance().writeInFile(text)
                            if (GeneradorArchivo.getInstance().tempSize % (tempSize / 50) == 0) print("█")
                        }
                    }
                }


            } catch (ex: Exception) {
                // TODO: Implement exception handling
                shutdown()
            } finally {

            }

        }
    }

    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }

    private fun shutdown() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")
        GeneradorArchivo.getInstance().deleteAllFilesGenerated()
    }

}
