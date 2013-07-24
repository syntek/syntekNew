/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syntek;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anonymous
 */
public class HandleFolder {

    static String PATH = getPathApplication() + "\\src\\file\\";
    static String PATH_TEMP = getPathApplication() + "\\src\\Temp\\";
    static String PATH_SAVED_FILE = null;

    public static String getPathApplication() {
        try {
            return new File(".").getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(HandleFolder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String getFileNameFromURL(String url) {
        String[] temp = url.split("\\\\");
        return temp[temp.length - 1];
    }

    public static void execCommand(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", cmd});
        } catch (IOException ex) {
            Logger.getLogger(HandleFolder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void createFolderDocument(String documentID) {
        String cmd = "mkdir " + PATH + documentID + "";
        System.out.println(cmd);
        execCommand(cmd);
    }

    public static void deleteFolderDocument(String documentID) {
        String cmd = "RD /S /Q " + PATH + documentID + "";
        execCommand(cmd);
    }

    public static String generateFileName(String oldName) {
        Calendar calendar = Calendar.getInstance();
        String[] temp = calendar.getTime().toString().split(" ");
        for (int i = 0; i < temp.length; i++) {
            System.out.println(temp[i]);
        }

        String t = oldName.substring(0, oldName.length() - 5);

        String name = t + "_" + temp[5] + "_" + temp[1] + "_" + temp[0] + "_" + temp[2] + ".docx";
        //System.out.println(name);
        return name;
    }

    public static void copyFile(String urlFileCopy, String documentID) {
        createFolderDocument(documentID);
        String command = "copy " + urlFileCopy + " " + PATH + documentID;
        execCommand(command);

        String newName = generateFileName(getFileNameFromURL(urlFileCopy));
        String commandRename = "rename \"" + PATH + documentID + "\\" + getFileNameFromURL(urlFileCopy) + "\" " + newName;

        System.out.println(commandRename);
        execCommand(commandRename);
        PATH_SAVED_FILE = PATH + documentID + "\\" + newName;
    }

    public static void exportFile(String urlFile, String path_save) {
        String cmd = "copy " + urlFile + " " + path_save;
        execCommand(cmd);
    }

    public static void main(String[] args) {
    }
}
