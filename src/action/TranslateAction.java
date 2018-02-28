package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TranslateAction extends AnAction {

    private JCheckBox checkBox ;
    private JCheckBox checkBox1;
    private JFrame jFrame ;
    private VirtualFile chooseFile;

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        chooseFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (chooseFile.getName().equals("strings.xml")) {
//            System.out.println(chooseFile.getPath());
            showChooseLanguageDialog();
        }else {
            Messages.showErrorDialog("该文件不是strings.xml，请重新选择" , "错误信息");
        }
    }

    /**
     * 显示选择语言的窗口
     */
    private void showChooseLanguageDialog() {
        jFrame = new JFrame("Android语言翻译");
        jFrame.setLayout(new BorderLayout());
//        jFrame.add("North" , addNorthLayout());
        jFrame.add("Center" , addCenterLayout());
        jFrame.add("South" , addSouthLayout());
        jFrame.setSize(500 , 500);
        jFrame.setLocation(getCenterLoaction(jFrame));
        jFrame.setVisible(true);
    }


    /**
     * 南边的布局
     * @return
     */
    private Component addSouthLayout() {
        JPanel jSelectAllPanel = new JPanel();

        JCheckBox cb_selectAll = new JCheckBox("选择全部");
        JButton bt_confirm = new JButton("确定");


        bt_confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                openStringXmlFile(chooseFile.getPath());










//                jFrame.setVisible(false);
            }
        });

        cb_selectAll.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                if (abstractButton.isSelected()) {
                    checkBox.setSelected(true);
                    checkBox1.setSelected(true);
                }else {
                    checkBox.setSelected(false);
                    checkBox1.setSelected(false);
                }
            }
        });

        jSelectAllPanel.add(cb_selectAll);
        jSelectAllPanel.add(bt_confirm);

        return jSelectAllPanel;
    }

    private void openStringXmlFile(String chooseFile) {
        ArrayList<Map<String , String>> stringList = new ArrayList<>();
        Map<String , String> stringMap = new HashMap<>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(chooseFile));
            NodeList nodeList = document.getElementsByTagName("resources");
            for (int i = 0; i < nodeList.getLength(); i++) {
                String key = document.getElementsByTagName("string").item(i).getAttributes().getNamedItem("name").getNodeValue();
                String value = document.getElementsByTagName("string").item(i).getFirstChild().getNodeValue();
                stringMap.put(key , value);
            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 中间的布局
     * @return
     */
    private Component addCenterLayout() {
        JPanel jGridPanel = new JPanel(new GridLayout(1 , 2));
        checkBox = new JCheckBox("English");
        checkBox1 = new JCheckBox("中文");

        jGridPanel.add(checkBox);
        jGridPanel.add(checkBox1);

        return jGridPanel;
    }

    /**
     * 获取在屏幕中间的位置
     * @param jFrame
     * @return
     */
    private Point getCenterLoaction(JFrame jFrame) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        int width = jFrame.getWidth();
        int height = jFrame.getHeight();
        Point point = new Point(dimension.width / 2 - width / 2 , dimension.height / 2 - height / 2);
        return point;
    }


}
