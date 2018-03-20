# AndroidStudioStringsTranslationPlugin
一个用于Android Studio里对strings.xml文件进行翻译的插件

# 写在前面
在对一个Android app的做国际化语言的时候，需要对strings.xml里的内容进行逐个的翻译和建立相应的文件夹，这个插件主要就是用来解决这个问题。 

鉴于国内的原因，这里的翻译是采用百度翻译。

目前支持的语言有：简体中文，繁体中文（香港地区），繁体中文（台湾地区），英语（美国），日语，韩语。

# 操作方法

1. 在Android Studio里安装插件AndroidLocalizeLanguage.jar

2. 重启生效后，右键默认的strings.xml文件，在弹出菜单里选择“Translate to other language”选项

![](https://github.com/JDNew/AndroidStudioStringsTranslationPlugin/blob/master/src/pictures/WX20180320-115939.png)

3. 在弹出的窗口里选择要翻译成的语言（至少选一门）。*注：默认翻译的原文是英文，如果是strings.xml里是中文的话，记得勾选“strings.xml为中文内容?”选项。*

![](https://github.com/JDNew/AndroidStudioStringsTranslationPlugin/blob/master/src/pictures/WX20180320-115950.png)

4. 点击确定，等待任务执行完成，刷新列表即可。

