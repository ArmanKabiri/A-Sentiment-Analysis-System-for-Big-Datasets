/**
 * @author : Arman Kabiri
 * @Email : "arman73k@gmail.com"
 * @Linked in : https://www.linkedin.com/in/armankabiri73
 * @Created at Feb 15, 2018 , 10:30:49 AM
 */
package Modules;

import java.io.File;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

public class MetaData {

    //Gerenal
    public Enums.LabelType labelType;
    public Enums.FileType fileType;
    public String positiveValueName, negativeValueName;
    public double scoreLowerBound, scoreUpperBound;
    //Json
    public String labelPropertyName;
    public String textPropertyName;
    public String IDPropertyName;
    //Excel
    public int textPropertyColumnNumber, labelPropertyColumnNumber;
    //XML
    public String review_tag, label_tag, text_tag;
    //CSV
    public int idPropertyIndex, textPropertyIndex, labelPropertyIndex;
    public String PropertiesDelimiter;
    
    public static MetaData loadMetaData(String inputURL) throws Exception {
        
        MetaData metaData = null;
        File jsonFile = null;
        jsonFile = new File(inputURL);
        String rawStr = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
        JSONObject jsonObj = new JSONObject(rawStr);
        metaData = new MetaData();
        
        metaData.fileType = Enums.FileType.valueOf(jsonObj.optString("FileType"));
        metaData.labelType = jsonObj.optString("LabelType").equalsIgnoreCase("Polarity") ? Enums.LabelType.polarity
                : jsonObj.optString("LabelType").equalsIgnoreCase("Score") ? Enums.LabelType.score : null;
        
        if (metaData.fileType == Enums.FileType.json) {
            metaData.labelPropertyName = jsonObj.optString("LabelPropertyName");
            metaData.textPropertyName = jsonObj.optString("TextPropertyName");
            metaData.IDPropertyName = jsonObj.optString("IDPropertyName");
        }
        if (metaData.fileType == Enums.FileType.excel) {
            metaData.textPropertyColumnNumber = jsonObj.optInt("TextPropertyColumnNumber");
            metaData.labelPropertyColumnNumber = jsonObj.optInt("LabelPropertyColumnNumber");
        }
        if (metaData.fileType == Enums.FileType.xml) {
            metaData.review_tag = jsonObj.optString("Review_Tag");
            metaData.text_tag = jsonObj.optString("Text_Tag");
            metaData.label_tag = jsonObj.optString("Label_Tag");
        }
        if (metaData.fileType == Enums.FileType.csv) {
            metaData.idPropertyIndex = jsonObj.optInt("IDPropertyIndex");
            metaData.textPropertyIndex = jsonObj.optInt("TextPropertyIndex");
            metaData.labelPropertyIndex = jsonObj.optInt("LabelPropertyIndex");
            metaData.PropertiesDelimiter = jsonObj.optString("PropertiesDelimiter");    // Character
        }
        
        if (metaData.labelType == Enums.LabelType.polarity) {
            metaData.negativeValueName = jsonObj.optString("NegativeValueName", null);
            metaData.positiveValueName = jsonObj.optString("PositiveValueName", null);
        } else if (metaData.labelType == Enums.LabelType.score) {
            metaData.scoreLowerBound = jsonObj.optDouble("ScoreLowerBound");
            metaData.scoreUpperBound = jsonObj.optDouble("ScoreUpperBound");
        }
        
        return metaData;
    }
}
