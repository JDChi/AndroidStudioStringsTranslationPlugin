package common;

import java.util.ArrayList;
import java.util.List;

public class SupportLanguage {

    private List<String> languageList = new ArrayList<>();

    public SupportLanguage(){
        final String ZH = "简体中文(中国大陆)";
        final String ZH_HK = "繁體中文(香港地區)";
        final String ZH_TW = "繁體中文(台灣地區)";
        final String EN = "English";

        languageList.add(ZH);
        languageList.add(ZH_HK);
        languageList.add(ZH_TW);
        languageList.add(EN);
    }

    public List<String> getLanguageList() {
        return languageList;
    }
}
