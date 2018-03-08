package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import dialog.ChooseLanguageDialog;
import task.BackgroundTask;


public class TranslateAction extends AnAction {

    private VirtualFile chooseFile;
    private ChooseLanguageDialog chooseLanguageDialog;
    private Project project;


    /**
     * 这个方法用于点击后的实现
     * @param e
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        chooseFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
//        System.out.println(chooseFile.getPath());
        project = CommonDataKeys.PROJECT.getData(e.getDataContext());

        chooseLanguageDialog = new ChooseLanguageDialog(project, "Translating strings.xml", false);
        chooseLanguageDialog.setiConfirmListener(new ChooseLanguageDialog.IConfirmListener() {
            @Override
            public void confirm() {
                operateStringXmlFile(chooseFile.getPath());
                chooseLanguageDialog.close(-1);
            }
        });

        chooseLanguageDialog.show();

    }

    /**
     * 操作strings.xml文件
     *
     * @param filePath
     */
    private void operateStringXmlFile(String filePath) {
        new BackgroundTask(project, "write to xml", chooseFile ).queue();
    }

    /**
     * 这个方法先于点击
     * @param e
     */
    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        adjustStringsXmlFile(e);

    }

    /**
     * 判断是否为strings.xml文件
     * @param e
     */
    private void adjustStringsXmlFile(AnActionEvent e) {
        chooseFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (!chooseFile.getName().equals("strings.xml")) {
            e.getPresentation().setEnabled(false);
            e.getPresentation().setVisible(false);
        }
    }





}
