/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author Jakob
 */
/**
 *
 * @author Jakob
 */
public class ReadAndWriteToFile {

    public static File textFile;

    public static void selectFile() throws IOException {
        File file = null;
        JFileChooser fc = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        fc.setCurrentDirectory(workingDirectory);
        fc.setDialogTitle("Choose file");
        fc.setApproveButtonText("Choose textfile");
        int returnVal = fc.showOpenDialog(new JFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            System.out.println(file.getAbsolutePath());
        } else {
            System.out.println("Aborted");
        }
        if (file == null || !file.exists()) {
            file.createNewFile();
        }
        textFile = file;
    }

    public static void writeToFile(String addString) throws IOException {
        String oldData = readFromFile();
        System.out.println("olddata: " + oldData);
        FileWriter fw = new FileWriter(textFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        if (oldData.equals("")) {
            bw.write(addString);
        } else {
            bw.write(oldData + addString);
        }
        bw.close();
    }

    public static String readFromFile() throws FileNotFoundException, IOException {
        String strFromFile = "";
        BufferedReader br = new BufferedReader(new FileReader(textFile));

        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }
        strFromFile = sb.toString();
        br.close();
        System.out.println("in readfromfile strFromfile:  " + strFromFile);
        return strFromFile;
    }

    public static void emptyFile() throws IOException {
        FileWriter fw = new FileWriter(textFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("");
        bw.close();
    }

}
