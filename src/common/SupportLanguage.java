package common;

import java.util.ArrayList;
import java.util.List;

public class SupportLanguage {

    private List<LanguageType> languageList = new ArrayList<>();

    public SupportLanguage(){
        final String FULL_ZH = "简体中文(中国大陆)";
        final String FULL_ZH_HK = "繁體中文(香港地區)";
        final String FULL_ZH_TW = "繁體中文(台灣地區)";
        final String FULL_EN_US = "English(USA)";
        final String FULL_JP = "Japanese(日本語)";
        final String FULL_KOR = "Korean(한국어)";

        final String ZH = "zh";
        final String EN = "en";
        final String CHT = "cht";//繁体中文
        final String JP = "jp";
        final String KOR = "kor";

        final String DR_ZH = "zh-rCN";
        final String DR_ZH_HK = "zh-rHK";
        final String DR_ZH_TW = "zh-rTW";
        final String DR_EN_US = "en-rUS";
        final String DR_JP = "ja-rJP";
        final String DR_KOR = "ko-rKR";

        languageList.add(new LanguageType(FULL_ZH , ZH , DR_ZH));
        languageList.add(new LanguageType(FULL_ZH_HK , CHT , DR_ZH_HK));
        languageList.add(new LanguageType(FULL_ZH_TW , CHT , DR_ZH_TW));
        languageList.add(new LanguageType(FULL_EN_US , EN , DR_EN_US));
        languageList.add(new LanguageType(FULL_JP , JP , DR_JP));
        languageList.add(new LanguageType(FULL_KOR , KOR , DR_KOR));
    }

    public List<LanguageType> getLanguageList() {
        return languageList;
    }

    public class LanguageType{
        String fullName;
        String shortName;
        String directoryName;

        LanguageType(String fullName , String shortName , String directoryName){
            this.fullName = fullName;
            this.shortName = shortName;
            this.directoryName = directoryName;
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

        public String getDirectoryName() {
            return directoryName;
        }

        public void setDirectoryName(String directoryName) {
            this.directoryName = directoryName;
        }
    }
}
