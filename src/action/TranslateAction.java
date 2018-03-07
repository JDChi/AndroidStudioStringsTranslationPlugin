package action;


import bean.AndroidString;
import bean.BaiduTranslateResult;
import com.google.gson.Gson;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import common.BaiduApp;
import common.Constant;
import dialog.ChooseLanguageDialog;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import task.BackgroundTask;
import util.HttpUtil;
import util.MD5Util;

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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslateAction extends AnAction implements HttpUtil.ICallbackListener {

    private VirtualFile chooseFile;
    private HttpUtil httpUtil;
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
        httpUtil = new HttpUtil();
        httpUtil.setiCallbackListener(this);
        chooseLanguageDialog = new ChooseLanguageDialog(project, "翻译", false);
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

    private ArrayList<AndroidString> originStringList;
    private int page;

    /**
     * 读取strings.xml文件
     *
     * @param filePath
     */
    private void operateStringXmlFile(String filePath) {

        originStringList = getStringList(filePath);
        List<AndroidString> subStringList;


        int count = 1;
        int limit = 5;
        page = originStringList.size() / limit;
        int fromIndex = 0;
        int toIndex = limit;
        StringBuilder translateString = new StringBuilder();
        String url;

//        for (int i = 0; i < stringList.size(); i++) {
//            System.out.println(stringList.get(i).getValue());
//        }
//        for (int i = 0; i < page; i++) {
//            subStringList = originStringList.subList(fromIndex, toIndex);
//            System.out.println(subStringList.toString());
//            for (int j = 0; j < subStringList.size(); j++) {
//
//                translateString = translateString.append(subStringList.get(j).getValue()).append("\\n");
//            }

//            System.out.println(translateString.toString().substring(0 , translateString.toString().length() - 2));

//            url = appendUrl(translateString.toString().substring(0, translateString.toString().length() - 2));
//            httpUtil.doGet(url);


//            fromIndex = toIndex;
//            toIndex = toIndex + limit;
//            translateString.setLength(0);
//        }

        for (int i = 0; i < originStringList.size(); i++) {
            url = appendUrl(originStringList.get(i).getValue());
            httpUtil.doGet(url);
        }

        for (int i = 0; i < originStringList.size(); i++) {
            translateResultList.get(i).setKey(originStringList.get(i).getKey());
        }

//        writeIntoXml(translateResultList);
        new BackgroundTask(project, "write to xml", chooseFile , translateResultList).queue();
    }

    /**
     * 遍历strings.xml里的所有内容，并存储到列表里
     *
     * @param chooseFile
     * @return
     */
    private ArrayList<AndroidString> getStringList(String chooseFile) {
        ArrayList<AndroidString> originStringList = new ArrayList<>();
        AndroidString originAndroidString;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(chooseFile));
            NodeList nodeList = document.getElementsByTagName("string");
//            System.out.println(nodeList.getLength());

            for (int i = 0; i < nodeList.getLength(); i++) {
                String key = document.getElementsByTagName("string").item(i).getAttributes().getNamedItem("name")
                        .getNodeValue();
                String value = document.getElementsByTagName("string").item(i).getFirstChild().getNodeValue()
                        .replace("\\n", " ");
                //把内容里的\n符号去掉，因为会与上传多个文字的分隔符冲突
                originAndroidString = new AndroidString(key, value);
                originStringList.add(originAndroidString);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return originStringList;
    }

    /**
     * 拼接url地址
     *
     * @param translateString 要翻译的文本（如果是一次上传多个文本的话，中间要用 \n 分隔）
     */
    private String appendUrl(String translateString) {


        String salt = String.valueOf(System.currentTimeMillis());

        String rawData = BaiduApp.APP_ID + translateString + salt + BaiduApp.APP_SECRET;
        String sign = MD5Util.md5Lower32(rawData);

        String url = Constant.TRANSLATE_URL
                + "?q=" + HttpUtil.encode(translateString)
                + "&from=" + Constant.ZH
                + "&to=" + Constant.EN
                + "&appid=" + BaiduApp.APP_ID
                + "&salt=" + salt
                + "&sign=" + sign;

        return url;
    }

    private List<AndroidString> translateResultList = new ArrayList<>();

    @Override
    public void success(String result) {
        Gson gson = new Gson();
        BaiduTranslateResult baiduTranslateResult = gson.fromJson(result, BaiduTranslateResult.class);
        String dst = baiduTranslateResult.getTrans_result().get(0).getDst();
//        System.out.println(dst);
//        String values[] = dst.split("\\\\n");
//        for (int i = 0; i < values.length; i++) {
//            System.out.println(values[i].toString());
//            translateResultList.add(new AndroidString(values[i] , values[i]));
//        }

        translateResultList.add(new AndroidString("", dst));



    }

    @Override
    public void fail(String result) {

    }
}
