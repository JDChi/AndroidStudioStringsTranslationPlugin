package common;

import java.util.ArrayList;
import java.util.List;

public class SupportLanguage {

    private List<LanguageType> languageList = new ArrayList<>();

    public SupportLanguage(){
        final String FULL_ZH = "简体中文(中国大陆)";
        final String FULL_ZH_HK = "繁體中文(香港地區)";
        final String FULL_ZH_TW = "繁體中文(台灣地區)";
        final String FULL_EN = "English(USA)";
        final String FULL_JP = "Japanese(日本語)";
        final String FULL_KOR = "Korean(한국어)";

        final String ZH = "zh";
        final String EN = "en";
        final String CHT = "cht";//繁体中文
        final String JP = "jp";
        final String KOR = "kor";

        languageList.add(new LanguageType(FULL_ZH , ZH));
        languageList.add(new LanguageType(FULL_ZH_HK , CHT));
        languageList.add(new LanguageType(FULL_ZH_TW , CHT));
        languageList.add(new LanguageType(FULL_EN , EN));
        languageList.add(new LanguageType(FULL_JP , JP));
        languageList.add(new LanguageType(FULL_KOR , KOR));
    }

    public List<LanguageType> getLanguageList() {
        return languageList;
    }

    public class LanguageType{
        String fullName;
        String shortName;

        LanguageType(String fullName , String shortName){
            this.fullName = fullName;
            this.shortName = shortName;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }
    }
}
