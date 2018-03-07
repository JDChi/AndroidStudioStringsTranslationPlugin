package dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChooseLanguageDialog extends DialogWrapper{

    private JCheckBox checkBox;
    private JCheckBox checkBox1;
    private JFrame jFrame;
    public interface IConfirmListener{
        void confirm();
    }
    private IConfirmListener iConfirmListener;

    public void setiConfirmListener(IConfirmListener iConfirmListener) {
        this.iConfirmListener = iConfirmListener;
    }

    public ChooseLanguageDialog(@Nullable Project project, String title , boolean canBeParent) {
        super(project, canBeParent);
        __init(title);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel jGridPanel = new JPanel(new GridLayout(1, 2));
        checkBox = new JCheckBox("English");
        checkBox1 = new JCheckBox("中文");

        jGridPanel.add(checkBox);
        jGridPanel.add(checkBox1);

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
