package Main.Hadoop;

import Modules.CommentRecord;
import Modules.Enums;
import Modules.Enums.LabelType;
import Modules.MetaData;
import Modules.PolarityLabel;
import Modules.SentenceRecord;
import Utils.Data_Preprocessor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Arman Kabiri
 *
 */
public class SAMapper extends Mapper<LongWritable, Text, IntWritable, MapWritable> {

    private static final Log _log = LogFactory.getLog(SAMapper.class);

    private Data_Preprocessor data_Preprocessor = new Data_Preprocessor();
    private MetaData inputFile_Metadata;

    @Override
    protected void setup(Mapper.Context context) throws IOException, InterruptedException {
        //Loading MetaData File from Cache:
        try {
            Path[] dataSetMetaData = DistributedCache.getLocalCacheFiles(context.getConfiguration());
//            Path[] dataSetMetaData = {new Path("/Users/arman/Desktop/Sentiment-Analysis-Hadoop/CacheData/DataSet_MetaData.json")};
            if (dataSetMetaData != null && dataSetMetaData.length > 0) {
                for (Path driverFile : dataSetMetaData) {
                    if (driverFile.getName().equalsIgnoreCase("DataSet_MetaData.json")) {
                        inputFile_Metadata = MetaData.loadMetaData(driverFile.toString());
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Exception in reducer setup: " + ex.getMessage());
        }
        System.out.println("A Mapper is Created.");
        _log.info("A Mapper is Created.");
    }

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        try {
            CommentRecord commentRecord = null;
            if (inputFile_Metadata.fileType == Enums.FileType.json) {
                commentRecord = loadRecord_Json(value.toString());
            } else if (inputFile_Metadata.fileType == Enums.FileType.csv) {
                commentRecord = loadRecord_Csv(value.toString());
            }

            for (SentenceRecord sentence : commentRecord.sentences) {
                MapWritable commentValue = new MapWritable();
                // Sentence Text
                commentValue.put(new IntWritable(1), new Text(sentence.text));
                // Review Labeled Polarity
                commentValue.put(new IntWritable(2), new BooleanWritable(((PolarityLabel) commentRecord.getLabel()).value));
                context.write(new IntWritable(commentRecord.id), commentValue);
            }
            _log.debug("Comment sentences with ID " + commentRecord.id + " is mapped.");

        } catch (Exception ex) {
            Logger.getLogger(SAMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private CommentRecord loadRecord_Json(String rawText) throws JSONException {

        JSONObject jsonObj = new JSONObject(rawText);
        LabelType labelType = Enums.LabelType.polarity;
        CommentRecord record = new CommentRecord();
        record.id = jsonObj.optInt(inputFile_Metadata.IDPropertyName);
        record.text = jsonObj.optString(inputFile_Metadata.textPropertyName, null);

        //Labeling
        if (inputFile_Metadata.labelType == Enums.LabelType.polarity) {
            boolean polarity = jsonObj.optString(inputFile_Metadata.labelPropertyName, null).equalsIgnoreCase(inputFile_Metadata.positiveValueName);
            record.setLabel(new PolarityLabel(polarity));
        } else if (inputFile_Metadata.labelType == Enums.LabelType.score) {
            double score = jsonObj.optDouble(inputFile_Metadata.labelPropertyName);
            record.setLabel(new PolarityLabel(score > ((inputFile_Metadata.scoreUpperBound + inputFile_Metadata.scoreLowerBound) / 2)));
        }

    	System.out.print("PrepProcessor is going to start.");

        data_Preprocessor.run(record);
        return record;
    }

    private CommentRecord loadRecord_Csv(String toString) {

        CommentRecord record = new CommentRecord();
        String[] values = toString.split(inputFile_Metadata.PropertiesDelimiter);
        record.id = Integer.valueOf(values[inputFile_Metadata.idPropertyIndex].trim());
        record.text = values[inputFile_Metadata.textPropertyIndex].trim();

        //Labeling
        if (inputFile_Metadata.labelType == Enums.LabelType.polarity) {
            boolean polarity = values[inputFile_Metadata.labelPropertyIndex].trim().equalsIgnoreCase(inputFile_Metadata.positiveValueName);
            record.setLabel(new PolarityLabel(polarity));
        } else if (inputFile_Metadata.labelType == Enums.LabelType.score) {
            double score = Double.valueOf(values[inputFile_Metadata.labelPropertyIndex].trim());
            record.setLabel(new PolarityLabel(score > ((inputFile_Metadata.scoreUpperBound + inputFile_Metadata.scoreLowerBound) / 2)));
        }
    	System.out.print("PrepProcessor is going to start.");
        data_Preprocessor.run(record);
//    	record.sentences.add(new SentenceRecord(record.text));
        return record;
    }
}