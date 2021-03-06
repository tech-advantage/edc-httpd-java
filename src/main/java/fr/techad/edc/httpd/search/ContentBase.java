package fr.techad.edc.httpd.search;

import fr.techad.edc.httpd.WebServerConfig;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 24/04/2018.
 */
public class ContentBase {

    public static final String DOC_ID = "id";
    public static final String DOC_STRATEGY_ID = "strategyId";
    public static final String DOC_STRATEGY_LABEL = "strategyLabel";
    public static final String DOC_LANGUAGE_CODE = "languageCode";
    public static final String DOC_LABEL = "label";
    public static final String DOC_TYPE = "type";
    public static final String DOC_CONTENT = "content";
    public static final String DOC_URL = "url";
    private final Path indexPath;

    protected ContentBase(WebServerConfig webServerConfig) {
        String configIndexPath = webServerConfig.getIndexPath();
        if (StringUtils.isBlank(configIndexPath))
            configIndexPath = System.getProperty("user.home") + "/.edc/index";
        this.indexPath = Paths.get(configIndexPath);
    }

    protected Path getIndexPath() {
        return this.indexPath;
    }
}
