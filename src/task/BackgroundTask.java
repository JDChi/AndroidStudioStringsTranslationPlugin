package task;

import bean.AndroidString;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import common.Constant;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class BackgroundTask extends Task.Backgroundable {

    private VirtualFile chooseFile;
    private List<AndroidString> androidStringList;
    public BackgroundTask(@Nullable Project project, @Nls @NotNull String title, VirtualFile virtualFile, List<AndroidString> translateResultList) {
        super(project, title);
        chooseFile = virtualFile;
        androidStringList = translateResultList;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
         writeIntoXml(androidStringList);
    }

    /**
     * 将结果写入到xml文件里
     * @param androidStringList
     */
    private void writeIntoXml(List<AndroidString> androidStringList) {
        String valuesDirectoryPath = chooseFile.getPath().substring(0 , chooseFile.getPath().indexOf("/values/")) + "/values-"+ Constant.ZH;
//        System.out.println(path);
        File valuesDirectory = new File(valuesDirectoryPath);
        if (!valuesDirectory.exists()) {
            valuesDirectory.mkdirs();
        }

        File stringXml = new File(valuesDirectoryPath + "/strings.xml");
        if (!stringXml.exists()) {
            try {
                stringXml.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter fileWriter = new FileWriter(stringXml);
            fileWriter.write("<resources>\n");
            for (int i = 0; i < androidStringList.size(); i++) {
                fileWriter.write("<string name=\"" + androidStringList.get(i).getKey() +"\">"+androidStringList.get(i).getValue() + "</string>\n");
            }

            fileWriter.write("\n</resources>");
            fileWriter.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
