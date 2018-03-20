package dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import common.SupportLanguage;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

public class ChooseLanguageDialog extends DialogWrapper {

    private JCheckBox[] jCheckBoxes;
    private List<SupportLanguage.LanguageType> selectedLanguageList;//已选择的语言列表
    List<SupportLanguage.LanguageType> supportLanguageList;//支持的语言列表
    private boolean isDefaultChinese;

    public interface IConfirmListener {
        void confirm(List<SupportLanguage.LanguageType> selectedLanguageList, boolean isDefaultChinese);
    }

    private IConfirmListener iConfirmListener;

    public void setiConfirmListener(IConfirmListener iConfirmListener) {
        this.iConfirmListener = iConfirmListener;
    }

    public ChooseLanguageDialog(@Nullable Project project, String title, boolean canBeParent) {
        super(project, canBeParent);
        __init(title);
        selectedLanguageList = new ArrayList<>();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        SupportLanguage supportLanguage = new SupportLanguage();
        supportLanguageList = supportLanguage.getLanguageList();
        int cols = 2;
        int rows = 0;
        if (supportLanguageList.size() % 2 == 0) {
            rows = rows + supportLanguageList.size() / 2;
        } else if (supportLanguageList.size() % 2 == 1) {
            rows = rows + supportLanguageList.size() / 2 + 1;
        }

        JPanel jGridPanel = new JPanel(new GridLayout(rows, cols));
        jCheckBoxes = new JCheckBox[supportLanguage.getLanguageList().size()];
        for (int i = 0; i < jCheckBoxes.length; i++) {
            jCheckBoxes[i] = new JCheckBox(supportLanguageList.get(i).getFullName());
            jGridPanel.add(jCheckBoxes[i]);
        }


        return jGridPanel;
    }


    @Nullable
    @Override
    protected JComponent createNorthPanel() {
        JPanel jIsChinesePanel = new JPanel();
        JCheckBox cb_chinese = new JCheckBox("strings.xml为中文内容?");
        cb_chinese.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    isDefaultChinese = true;
                } else {
                    isDefaultChinese = false;
                }
            }
        });

        jIsChinesePanel.add(cb_chinese);
        return jIsChinesePanel;
    }

    @Nullable
    @Override
    protected JComponent createSouthPanel() {
        JPanel jSelectAllPanel = new JPanel();

        JCheckBox cb_selectAll = new JCheckBox("选择全部");
        JButton bt_confirm = new JButton("确定");
        bt_confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                for (int i = 0; i < jCheckBoxes.length; i++) {
                    if (jCheckBoxes[i].isSelected()) {
                        selectedLanguageList.add(supportLanguageList.get(i));
                    }
                }

                if (selectedLanguageList.isEmpty()) {
                    Messages.showInfoMessage("请至少选择一种语言", "错误");
                } else {
                    iConfirmListener.confirm(selectedLanguageList , isDefaultChinese);
                }


            }
        });
        cb_selectAll.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    for (int i = 0; i < jCheckBoxes.length; i++) {
                        jCheckBoxes[i].setSelected(true);
                    }
                } else {
                    for (int i = 0; i < jCheckBoxes.length; i++) {
                        jCheckBoxes[i].setSelected(false);
                    }
                }
            }
        });
        jSelectAllPanel.add(cb_selectAll);
        jSelectAllPanel.add(bt_confirm);
        return jSelectAllPanel;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }

    @Override
    public void doCancelAction() {
        close(-1);
    }

    protected void __init(String title) {
        setTitle(title);
        init();
    }

    /**
     * 获取在屏幕中间的位置
     *
     * @param jFrame
     * @return
     */
//    private Point getCenterLoaction(JFrame jFrame) {
//        Toolkit toolkit = Toolkit.getDefaultToolkit();
//        Dimension dimension = toolkit.getScreenSize();
//        int width = jFrame.getWidth();
//        int height = jFrame.getHeight();
//        Point point = new Point(dimension.width / 2 - width / 2, dimension.height / 2 - height / 2);
//        return point;
//    }

}
