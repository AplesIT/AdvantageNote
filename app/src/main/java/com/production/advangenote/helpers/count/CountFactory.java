package com.production.advangenote.helpers.count;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.helpers.LanguageHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author vietnh
 * @name CountFactory
 * @date 10/1/20
 **/
public class CountFactory {

    private CountFactory() {
    }
    private static Logger logger= LoggerFactory.getLogger("CountFactory.class");

    public static WordCounter getWordCounter() {
        try {
            String locale = LanguageHelper.getCurrentLocaleAsString(AdvantageNotes.getAppContext());
            return getCounterInstanceByLocale(locale);
        } catch (Exception e) {
            logger.info("Error retrieving locale or context: " + e.getLocalizedMessage(), e);
            return new DefaultWordCounter();
        }
    }

    static WordCounter getCounterInstanceByLocale(String locale) {
        switch (locale) {
            case "ja_JP":
            case "zh_CN":
            case "zh_TW":
                return new IdeogramsWordCounter();
            default:
                return new DefaultWordCounter();
        }
    }
}
