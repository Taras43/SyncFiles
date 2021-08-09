import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import static javax.swing.JOptionPane.*;

class Setting implements Serializable {
    //static String[] setting;
    static void writeSetting (String [] setting){

    try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("setting.dat")))
    {
        objectOutputStream.writeObject(setting);
    }
        catch(Exception ex){

            System.out.println(ex.getMessage());
        }
    }

    static String[] readSetting() {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("setting.dat")))
        {
            String [] setting =(String[]) objectInputStream.readObject();
            return setting;
        }
        catch(Exception ex){
            String setting[] = {"", ""};
            return setting;
        }
    }
    static void writeOldSyncFiles (ArrayList<String> oldSyncFiles ){

        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("oldSyncFiles.dat")))
        {
            objectOutputStream.writeObject(oldSyncFiles);
        }
        catch(Exception ex){

            System.out.println(ex.getMessage());
        }
    }

    static ArrayList<String> readOldSyncFiles() {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("oldSyncFiles.dat")))
        {
            ArrayList<String> oldSyncFiles =(ArrayList<String> ) objectInputStream.readObject();
            return oldSyncFiles;
        }
        catch(Exception ex){
            ArrayList<String> oldSyncFiles = new ArrayList<>();
            return oldSyncFiles;
        }
    }
}


 class Frame implements ActionListener {
     JButton buttonStartSync;
     JButton buttonCancel;
     JButton choiceSyncFolderFirst;
     JButton choiceSyncFolderSecond;
     JTextField fieldSyncFolderFirst;
     JTextField fieldSyncFolderSecond;
     String folderFirstSyncFromSetting;
     String folderSecondSyncFromSetting;
     JLabel label;
     JFrame frame = new JFrame("Синхронизатор Тараса");

     Frame() {
         String[] setting = Setting.readSetting();
         //lastSyncTime = Long.parseLong(setting[0]);
         folderFirstSyncFromSetting = setting[0];
         folderSecondSyncFromSetting = setting[1];
         //JFrame frame = new JFrame("Синхронизатор Тараса");
         buttonStartSync = new JButton("Начать синхронизацию");
         buttonCancel = new JButton("Отмена");
         choiceSyncFolderFirst = new JButton("Выбрать");
         choiceSyncFolderSecond = new JButton("Выбрать");
         fieldSyncFolderFirst = new JTextField();
         fieldSyncFolderSecond = new JTextField();
         fieldSyncFolderFirst.setText(folderFirstSyncFromSetting);
         fieldSyncFolderSecond.setText(folderSecondSyncFromSetting);
         label = new JLabel("Синхронизируемы папки");

         frame.setSize(new Dimension(630, 250));
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setLayout(null);
         label.setBounds(50, 20, 300, 20);
         fieldSyncFolderFirst.setBounds(50, 50, 400, 20);
         choiceSyncFolderFirst.setBounds(470, 50, 100, 20);
         fieldSyncFolderSecond.setBounds(50, 80, 400, 20);
         choiceSyncFolderSecond.setBounds(470, 80, 100, 20);
         buttonCancel.setBounds(50, 140, 250, 30);
         buttonStartSync.setBounds(320, 140, 250, 30);
         choiceSyncFolderFirst.setActionCommand("choiceSyncFolderFirst");
         choiceSyncFolderSecond.setActionCommand("choiceSyncFolderSecond");
         buttonStartSync.setActionCommand("buttonStartSync");
         buttonCancel.setActionCommand("buttonCancel");


         frame.add(label);
         frame.add(fieldSyncFolderFirst);
         frame.add(choiceSyncFolderFirst);
         frame.add(fieldSyncFolderSecond);
         frame.add(choiceSyncFolderSecond);
         frame.add(buttonCancel);
         frame.add(buttonStartSync);
         frame.setVisible(true);

         buttonStartSync.addActionListener(this);
         buttonCancel.addActionListener(this);
         choiceSyncFolderFirst.addActionListener(this);
         choiceSyncFolderSecond.addActionListener(this);


     }

     @Override
     public void actionPerformed(ActionEvent e) {
         if (e.getActionCommand() == "choiceSyncFolderFirst") {
             JFileChooser fileChooserFirst = new JFileChooser();
             fileChooserFirst.showOpenDialog(null);
             fileChooserFirst.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
             String newFolderForSyncFirst = (fileChooserFirst.getCurrentDirectory()).toString();
             fieldSyncFolderFirst.setText(newFolderForSyncFirst);
         } else if (e.getActionCommand() == "choiceSyncFolderSecond") {
             JFileChooser fileChooserSecond = new JFileChooser();
             fileChooserSecond.showOpenDialog(null);
             fileChooserSecond.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
             String newFolderForSyncSecond = (fileChooserSecond.getCurrentDirectory()).toString();
             fieldSyncFolderSecond.setText(newFolderForSyncSecond);
         } else if (e.getActionCommand() == "buttonCancel") {
             System.exit(0);

         } else if (e.getActionCommand() == "buttonStartSync") {
             String stringFolderSyncFirst = fieldSyncFolderFirst.getText();
             Path pathFolderSyncFirst = Paths.get(stringFolderSyncFirst);
             String stringFolderSyncSecond = fieldSyncFolderSecond.getText();
             System.out.println(stringFolderSyncFirst);
             System.out.println(stringFolderSyncSecond);
             Path pathFolderSyncSecond = Paths.get(stringFolderSyncSecond);
             System.out.println("button press");
             if (Files.isDirectory(pathFolderSyncFirst) & Files.isDirectory(pathFolderSyncSecond)) {
                 boolean conformityFirstFolder = stringFolderSyncFirst.equals(folderFirstSyncFromSetting);
                 boolean conformitySecondFolder = stringFolderSyncSecond.equals(folderSecondSyncFromSetting);
                 System.out.println(conformityFirstFolder);
                 System.out.println(conformitySecondFolder);
                 if (conformityFirstFolder == false & conformitySecondFolder == false) {
                    // System.out.println("time chainge");
                    // Date date = new Date();
                     ArrayList <String> arrayList = new ArrayList<String>();
                     Setting.writeOldSyncFiles(arrayList);
                 }

                 try {
                     System.out.println("try");
                     SyncTask.setOldSyncFiles(Setting.readOldSyncFiles());
                     SyncTask syncTaskFirst = new SyncTask(stringFolderSyncFirst,stringFolderSyncSecond);
                     SyncTask syncTaskSecond = new SyncTask(stringFolderSyncSecond,stringFolderSyncFirst);
                     syncTaskFirst.deleteFiles();
                     syncTaskFirst.deleteFolders();
                     syncTaskFirst.copyFolder();
                     syncTaskFirst.copyFiles();
                     syncTaskSecond.deleteFiles();
                     syncTaskSecond.deleteFolders();
                     syncTaskSecond.copyFolder();
                     syncTaskSecond.copyFiles();
                     SyncTask.refreschOldSyncFiles(syncTaskFirst);
                     Setting.writeOldSyncFiles(SyncTask.oldSyncFiles);



                 } catch (IOException ioException) {
                     ioException.printStackTrace();
                 }
                 String[] newSetting = {stringFolderSyncFirst, stringFolderSyncSecond};
                 Setting.writeSetting(newSetting);
                 System.out.println("Синхронизация окончена");
                 System.exit(0);


             } else {
                 if (Files.isDirectory(pathFolderSyncFirst) != true) {
                     JOptionPane.showMessageDialog(frame, "Первой папки не существует", "Ошибка", ERROR_MESSAGE);
                 } else {
                     JOptionPane.showMessageDialog(frame, "Второй папки не существует", "Ошибка", ERROR_MESSAGE);
                 }
             }
         }
     }

 }
 public class SyncFiles {

    public static void main(String[] args) {
        Frame frame = new Frame();

    }


}
class SyncTask {
    static ArrayList<String> oldSyncFiles;
    ArrayList<Path> deleteFolders = new ArrayList();
    ArrayList<Path> deleteFiles = new ArrayList();
    ArrayList<Path> copyFiles = new ArrayList();
    ArrayList<Path> copyFolders = new ArrayList();
    String firstFolderSyncString;
    String secondFolderSyncString;

    SyncTask(String firstFolderSyncString, String secondFolderSyncString) throws IOException {
        this.secondFolderSyncString = secondFolderSyncString;
        this.firstFolderSyncString = firstFolderSyncString;
        Path firstFolderSync = Paths.get(firstFolderSyncString);
       // Path secondFolderSync = Paths.get(secondFolderSyncString);
        ArrayList<Path> filesFirstFolder = (ArrayList<Path>) (Files.walk(firstFolderSync)).collect(Collectors.toList());
        filesFirstFolder.remove(0);//remove the path to the root folder
       // filesFirstFolder.remove(0);
        for (Path fileFirstFolder:filesFirstFolder) {
            File file = new File(fileFirstFolder.toString());
            if ((Files.isDirectory(fileFirstFolder)) == false) {//если путь ведёт к файлу
                String p = fileFirstFolder.toString();
                String sd = p.replace(firstFolderSyncString, secondFolderSyncString);
                Path dateSecondFolder1 = Paths.get(sd);
                File file2 = new File(sd);


                if (file2.exists()) {//если файл с таким путём существует во второй папке
                    long modifiedTimeFileFromFirst = Files.getLastModifiedTime(fileFirstFolder).toMillis();
                    long modifiedTimeFileFromSecond = Files.getLastModifiedTime(dateSecondFolder1).toMillis();
                    if (modifiedTimeFileFromFirst > modifiedTimeFileFromSecond) {//если время последенего изменения больше
                        copyFiles.add(fileFirstFolder);
                    } else if (modifiedTimeFileFromFirst < modifiedTimeFileFromSecond) {//если время последнего изменения меньше
                        deleteFiles.add(fileFirstFolder);
                    }
                } else {// если файла с таким путём не существует во второй папке
                    String h = fileFirstFolder.toString();
                    String gh = h.replace(firstFolderSyncString, "");
                    if (oldSyncFiles.contains(gh)) {//если файл раньше синхронизировался
                        deleteFiles.add(fileFirstFolder);
                    } else {//если файл раньше не синхронизировался
                        copyFiles.add(fileFirstFolder);
                    }

                }
            } else {// если путь ведёт к папке
                String p = fileFirstFolder.toString();
                String yt = p.replace(firstFolderSyncString, secondFolderSyncString);
                //Path dateSecondFolder1 = Paths.get(yt);
                //File file = new File(yt);
                if (file.exists() == false) {//if a folder with this path does not exist in the second folder
                    String h = fileFirstFolder.toString();
                    h=h.replace(firstFolderSyncString, "");
                    if (oldSyncFiles.contains(h)) {//if the folder was synced
                        deleteFolders.add(fileFirstFolder);
                    } else {//if the folder was not synced
                        copyFolders.add(fileFirstFolder);
                    }
                }

            }
        };


    }
    void deleteFiles() throws IOException {
        for(Path deleteFile:deleteFiles){
            Files.delete(deleteFile);
        }
    }
    void deleteFolders() throws IOException {
        Collections.reverse(deleteFolders);
        for(Path deleteFolder:deleteFolders){
            Files.delete(deleteFolder);
        }
    }
    void copyFiles() throws IOException {
        for (Path copyFile:copyFiles){
            String dfg = copyFile.toString();
            dfg=dfg.replace(firstFolderSyncString,secondFolderSyncString);
            Path path = Paths.get(dfg);
            Files.copy(copyFile,path);
        }
    }
    void copyFolder() throws IOException {
        for (Path copyFolder:copyFolders){
            String dfg = copyFolder.toString();
            dfg=dfg.replace(firstFolderSyncString,secondFolderSyncString);
            Path path = Paths.get(dfg);
            Files.copy(copyFolder,path);
    }
}
static void setOldSyncFiles(ArrayList<String> oldSyncFiles1){
    oldSyncFiles=oldSyncFiles1;
}
static void refreschOldSyncFiles(SyncTask syncTask) throws IOException {
    Path firstFolderSync = Paths.get(syncTask.firstFolderSyncString);
    // Path secondFolderSync = Paths.get(secondFolderSyncString);
    oldSyncFiles.clear();
    ArrayList<Path> filesFirstFolder = (ArrayList<Path>) (Files.walk(firstFolderSync)).collect(Collectors.toList());
    filesFirstFolder.remove(0);//remove the path to the root folder
    for (Path fileFirstFolder:filesFirstFolder) {
        String string = fileFirstFolder.toString();
        string = string.replace(syncTask.firstFolderSyncString,"");
        oldSyncFiles.add(string);
    }


}

}
