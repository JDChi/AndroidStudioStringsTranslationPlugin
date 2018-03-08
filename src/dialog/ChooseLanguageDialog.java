package dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import common.SupportLanguage;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class ChooseLanguageDialog extends DialogWrapper {

    private JCheckBox checkBox;
    private JCheckBox checkBox1;
    private JFrame jFrame;
    private JCheckBox[] jCheckBoxes;

    public interface IConfirmListener {
        void confirm();
    }

    private IConfirmListener iConfirmListener;

    public void setiConfirmListener(IConfirmListener iConfirmListener) {
        this.iConfirmListener = iConfirmListener;
    }

    public ChooseLanguageDialog(@Nullable Project project, String title, boolean canBeParent) {
        super(project, canBeParent);
        __init(title);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        SupportLanguage supportLanguage = new SupportLanguage();
        List<String> supportLanguageList = supportLanguage.getLanguageList();
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
            jCheckBoxes[i] = new JCheckBox(supportLanguageList.get(i));
            jGridPanel.add(jCheckBoxes[i]);
        }


        return jGridPanel;
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
                iConfirmListener.confirm();
            }
        });
        cb_selectAll.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    for (int i = 0; i < jCheckBoxes.length; i++) {
                        jCheckBoxes[i].setSelected(true);
                    }
                }else {
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
