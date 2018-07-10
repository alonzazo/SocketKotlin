package Utils;

import javafx.util.Pair;

import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Random;

public class GeneradorArchivo {
    private static GeneradorArchivo singleton;
    private static int countFiles;
    //Lista de archivos con su tamaño asociado
    private static LinkedList< Pair< Integer, String > > filesList;

    private String tempFilePath;
    private PrintWriter tempWriter;
    private Integer tempSize;

    private GeneradorArchivo(){
       countFiles  = 0;
    }

    public static GeneradorArchivo getInstance(){
        if (singleton == null) {
            singleton = new GeneradorArchivo();
            filesList = new LinkedList<Pair<Integer, String>>();
        }
        return singleton;
    }

    public Integer getTempSize(){return tempSize;}

    public String getTempFilePath(){return tempFilePath;}

    public boolean createFile(String path){
        if (tempFilePath == null && tempWriter == null){
            try {
                tempWriter = new PrintWriter( path, "UTF-8");
                tempFilePath = path;
                tempSize = 0;
                return true;
            }
            catch (Exception e){
                System.err.print(e.getMessage());
                e.printStackTrace();
                return false;
            }
        } else {
            System.err.print("ERROR Archivo en escritura actualmente");
            return false;
        }
    }

    public boolean isWriting(){
        return tempFilePath != null && tempWriter != null;
    }

    public boolean writeInFile(String buffer){
        if (tempFilePath != null && tempWriter != null){
            tempWriter.write(buffer);
            tempSize += (buffer.length()/1024);
            return true;
        }else {
            return false;
        }
    }

    public boolean closeFile(){
        if (tempFilePath != null && tempWriter != null){
            tempWriter.close();
            filesList.add(new Pair(tempSize,tempFilePath));
            tempWriter = null;
            tempFilePath = null;
            tempSize = 0;
            return true;
        } else {
            return false;
        }
    }

    public String generateRandomSizeFile(){

        countFiles++;
        Random sizeSeed = new Random();
        int size = sizeSeed.nextInt(100000);
        String path = String.valueOf(countFiles)  + ".txt";
        try {
            PrintWriter writer = new PrintWriter( path, "UTF-8");
            System.out.println("Starting to write " + path + " file-------------------------------TOTAL SIZE: " + size + " KB");
            for (int j = 0; j < size; j++) {
                for (int i = 0; i < 1024; i++) writer.print('c');//Escribe 1Kb
                if (j % (size/50) == 0) System.out.print("█");
            }
            writer.close();
            System.out.println("\n" + path + " file was written succesfully");
            filesList.add(new Pair<Integer, String>(size,path));
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
        return path;
    }

    public String generateFileOfSpecificSize(int sizeKB){

        countFiles++;
        String path = String.valueOf(countFiles)  + ".txt";
        try {
            PrintWriter writer = new PrintWriter( path, "UTF-8");
            System.out.println("Starting to write " + path + " file-------------------------------TOTAL SIZE: " + sizeKB + " KB");
            for (int j = 0; j < sizeKB; j++) {
                for (int i = 0; i < 1024; i++) writer.print('c');//Escribe 1Kb
                if (j % (sizeKB/50) == 0) System.out.print("█");
            }
            writer.close();
            System.out.println("\n" + path + " file was written succesfully");
            filesList.add(new Pair<Integer, String>(sizeKB,path));
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
        return path;
    }

    public boolean deleteAllFilesGenerated(){
        for (Pair<Integer, String> fileRef : filesList){
            File file = new File(fileRef.getValue());

            if(file.delete()){
                System.out.println(file.getName() + " was deleted");
            }else{
                System.out.println("Delete operation has failed");
                return false;
            }
        }
        filesList.clear();
        countFiles = 0;
        return true;
    }

    public int getMeanSize(){
        int result = filesList.getFirst().getKey();
        for (Pair<Integer, String> file: filesList) result = (result + file.getKey())/2;
        return result;
    }

    public LinkedList<Pair<Integer, String>> getFilesList() {
        return filesList;
    }
}
