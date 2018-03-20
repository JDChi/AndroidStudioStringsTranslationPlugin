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
import common.SupportLanguage;
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

    private ArrayList<AndroidString> originStringList;//源列表
    private HttpUtil httpUtil;
    private List<AndroidString> translateResultList;//翻译结果的列表（key和value）
    private List<SupportLanguage.LanguageType> selectedLanguageList;//已选择的语言列表（内容是缩写）
    private boolean isDefaultChinese;

    public BackgroundTask(@Nullable Project project, @Nls @NotNull String title, VirtualFile virtualFile, List<SupportLanguage.LanguageType> selectedLanguageList, boolean isDefaultChinese) {
        super(project, title);
        chooseFile = virtualFile;
        translateResultList = new ArrayList<>();
        httpUtil = new HttpUtil();
        httpUtil.setiCallbackListener(this);
        this.selectedLanguageList = selectedLanguageList;
        this.isDefaultChinese = isDefaultChinese;

    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {

        originStringList = getStringList(chooseFile.getPath());
        String url;


        for (int i = 0; i < selectedLanguageList.size(); i++) {
            //获取要翻译的内容，并请求翻译
            for (int j = 0; j < originStringList.size(); j++) {
                url = appendUrl(originStringList.get(j).getValue() , selectedLanguageList.get(i).getShortName());
                httpUtil.doGet(url);
            }

            //将key写入新的列表
            for (int k = 0; k < originStringList.size(); k++) {
                translateResultList.get(k).setKey(originStringList.get(k).getKey());
            }
            writeIntoXml(translateResultList , selectedLanguageList.get(i).getDirectoryName());
            translateResultList.clear();
        }

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
     * @param shortName 语言简写
     * @param translateString 要翻译的文本（如果是一次上传多个文本的话，中间要用 \n 分隔）
     */
    private String appendUrl(String translateString , String shortName) {


        String salt = String.valueOf(System.currentTimeMillis());

        String rawData = BaiduApp.APP_ID + translateString + salt + BaiduApp.APP_SECRET;
        String sign = MD5Util.md5Lower32(rawData);

        String from;
        if (isDefaultChinese) {
            from = Constant.ZH;
        }else {
            from = Constant.EN;
        }

        String url = Constant.TRANSLATE_URL
                + "?q=" + HttpUtil.encode(translateString)
                + "&from=" + from
                + "&to=" + shortName
                + "&appid=" + BaiduApp.APP_ID
                + "&salt=" + salt
                + "&sign=" + sign;

        return url;
    }

    /**
     * 将结果写入到xml文件里
     * @param androidStringList
     * @param directoryName
     */
    private void writeIntoXml(List<AndroidString> androidStringList, String directoryName) {
        //目标文件夹
        String valuesDirectoryPath = chooseFile.getPath().substring(0 , chooseFile.getPath().indexOf("/values/")) + "/values-"+ directoryName;
//        System.out.println(path);
        File valuesDirectory = new File(valuesDirectoryPath);
        if (!valuesDirectory.exists()) {
            valuesDirectory.mkdirs();
        }
        //目标文件
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

        //将结果写入新的列表
        translateResultList.add(new AndroidString("", dst));
    }

    @Override
    public void fail(String result) {

    }
}
