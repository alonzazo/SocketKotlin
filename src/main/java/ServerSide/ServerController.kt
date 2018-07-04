package ServerSide

import java.net.ServerSocket
import kotlin.concurrent.thread

fun main(args: Array<String>){
    val server = ServerSocket(9999)
    println("Server is running on port ${server.localPort}")

    //Inicio de server
    while (true) {
        val client = server.accept()
        println("Client connected: ${client.inetAddress.hostAddress}")

        // Run client in it's own thread.
        thread { ClientHandler(client).run() }
    }

}
