package Utils

import java.io.PrintWriter
import java.util.*

class TablaTiempos {
    private var tiempos: LinkedList<Tiempo> = LinkedList<Tiempo>()
    private var currentTime: Long = 0

    fun exportToCSV(fileName: String): String {
        var result: String = ""
        //Componemos el string
        result += "nombreArchivo,tamano,duracion\n"
        tiempos.forEach{ tiempo -> run{
            result += "${tiempo.getPathFile()},${tiempo.getSize()},${tiempo.getDuration()}\n"
            }
        }
        //Escribimos el archivo
        val writer = PrintWriter("$fileName.csv")
        writer.println(result)
        writer.close()

        return result
    }

    fun startTimer(): Long {
        currentTime = System.nanoTime()
        return currentTime
    }

    fun stopTimer(): Long {
        currentTime = System.nanoTime() - currentTime
        return currentTime
    }

    fun registerNewTime(pathFile: String, size: Int){
        tiempos.add(Tiempo(pathFile,size,currentTime))
    }

    fun clear(){
        tiempos.clear()
    }

    private inner class Tiempo(pathFile: String, size: Int, duration: Long) {
        private var pathFile: String = pathFile
        private var size: Int = size
        private var duration: Long = duration

        fun getPathFile(): String {return pathFile}

        fun getSize(): Int {return size}

        fun getDuration(): Long {return duration}

    }
}