package task;

import bean.AndroidString;
import bean.BaiduTranslateResult;
import com.google.gson.Gson;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import common.BaiduApp;
import common.Constant;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import util.HttpUtil;
import util.MD5Util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackgroundTask extends Task.Backgroundable implements HttpUtil.ICallbackListener {

    private VirtualFile chooseFile;

    private ArrayList<AndroidString> originStringList;
    private HttpUtil httpUtil;
    private List<AndroidString> translateResultList ;

    public BackgroundTask(@Nullable Project project, @Nls @NotNull String title, VirtualFile virtualFile) {
        super(project, title);
        chooseFile = virtualFile;
        translateResultList = new ArrayList<>();
        httpUtil = new HttpUtil();
        httpUtil.setiCallbackListener(this);

    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        originStringList = getStringList(chooseFile.getPath());
        String url;
        for (int i = 0; i < originStringList.size(); i++) {
            url = appendUrl(originStringList.get(i).getValue());
            httpUtil.doGet(url);
        }

        for (int i = 0; i < originStringList.size(); i++) {
            translateResultList.get(i).setKey(originStringList.get(i).getKey());
        }
         writeIntoXml(translateResultList);
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

    @Override
    public void success(String result) {
        Gson gson = new Gson();
        BaiduTranslateResult baiduTranslateResult = gson.fromJson(result, BaiduTranslateResult.class);
        String dst = baiduTranslateResult.getTrans_result().get(0).getDst();

        translateResultList.add(new AndroidString("", dst));
    }

    @Override
    public void fail(String result) {

    }
}
