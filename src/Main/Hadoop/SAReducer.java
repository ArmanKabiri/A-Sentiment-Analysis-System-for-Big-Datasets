package Main.Hadoop;

import SA.SentimentPredictor;
import Aggregation.Aggregator_Average_Polarity;
import Modules.Enums;
import Modules.Lexicon;
import Modules.LexiconRecord;
import Modules.MetaData;
import Modules.PolarityLabel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 ** @author Arman Kabiri
 */
public class SAReducer extends Reducer<IntWritable, MapWritable, IntWritable, Text> {

    private static final Log _log = LogFactory.getLog(SAReducer.class);
    private Lexicon lexicon;
    private MetaData lexicon_Metadata;
    private SentimentPredictor sentimentPredictor;
    private Aggregator_Average_Polarity aggregator;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {

        aggregator = new Aggregator_Average_Polarity();
        try {
            Path[] lexiconFiles = DistributedCache.getLocalCacheFiles(context.getConfiguration());
            // Path[] lexiconFiles = {new
            // Path("/Users/arman/Desktop/Sentiment-Analysis-Hadoop/CacheData/Lexicon.csv"),
            // new
            // Path("/Users/arman/Desktop/Sentiment-Analysis-Hadoop/CacheData/Lexicon_MetaData.json")};
            String lexiconPath = null, lexiconMetaDataPath = null;
            if (lexiconFiles != null && lexiconFiles.length > 1) { // At least
                // two files
                // should
                // exist
                for (Path driverFile : lexiconFiles) {
                    if (driverFile.getName().equalsIgnoreCase("lexicon.csv")) {
                        lexiconPath = driverFile.toString();
                    }
                    if (driverFile.getName().equalsIgnoreCase("lexicon_MetaData.json")) {
                        lexiconMetaDataPath = driverFile.toString();
                    }
                }
                lexicon_Metadata = MetaData.loadMetaData(lexiconMetaDataPath);
                if (lexicon_Metadata.fileType == Enums.FileType.excel) {
//                    lexicon = loadLexicon_Excel(lexiconPath, lexicon_Metadata);
                } else if (lexicon_Metadata.fileType == Enums.FileType.csv) {
                    lexicon = loadLexicon_Csv(lexiconPath, lexicon_Metadata);
                }
                sentimentPredictor = new SentimentPredictor(lexicon);
            }
        } catch (Exception ex) {
            System.err.println("Exception in reducer setup: " + ex.getMessage());
        }
        System.out.println("A Reducer is Created.");
        _log.info("A Reducer is Created.");
    }

    @Override
    public void reduce(IntWritable key, Iterable<MapWritable> values, Context context)
            throws IOException, InterruptedException {

        int commentID = key.get();
        boolean commentLabel = false;
        ArrayList<Boolean> polarities = new ArrayList<>();
        for (MapWritable value : values) {
            String sentenceText = ((Text) value.get(new IntWritable(1))).toString();
            commentLabel = ((BooleanWritable) value.get(new IntWritable(2))).get();
            Boolean prediction = sentimentPredictor.run(sentenceText);
            if (prediction != null) {
                polarities.add(prediction);
            }
        }
        boolean finalPrediction;
        if (!polarities.isEmpty()) {
            finalPrediction = aggregator.run(polarities);
        } else {
            finalPrediction = true;
        }

        context.write(key,
                new Text((commentLabel ? "positive" : "negative") + "," + (finalPrediction ? "positive" : "negative")));
        _log.debug("Sentences of the comment #" + commentID + " are reduced.");

    }

//    private Lexicon loadLexicon_Excel(String lexiconPath, MetaData lexicon_Metadata) {
//
//        ArrayList<LexiconRecord> lexiconRecords = new ArrayList<>();
//        try {
//            int idCounter = 1;
//            FileInputStream excelFile;
//            excelFile = new FileInputStream(new File(lexiconPath));
//            Workbook workbook = new XSSFWorkbook(excelFile);
//            Sheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rows = sheet.iterator();
//            if (rows.hasNext()) {
//                rows.next(); // skip header
//            }
//            while (rows.hasNext()) {
//                Row currentRow = rows.next();
//                Cell wordCell = currentRow.getCell(lexicon_Metadata.textPropertyColumnNumber);
//                Cell labelCell = currentRow.getCell(lexicon_Metadata.labelPropertyColumnNumber);
//                LexiconRecord record = new LexiconRecord();
//                record.id = idCounter++;
//                record.text = wordCell.getStringCellValue();
//
//                // Labeling
//                if (lexicon_Metadata.labelType == Enums.LabelType.polarity) {
//                    boolean polarity = labelCell.getStringCellValue()
//                            .equalsIgnoreCase(lexicon_Metadata.positiveValueName);
//                    record.setLabel(new PolarityLabel(polarity));
//                } else if (lexicon_Metadata.labelType == Enums.LabelType.score) {
//                    double score = labelCell.getNumericCellValue();
//                    record.setLabel(new PolarityLabel(
//                            score > ((lexicon_Metadata.scoreUpperBound + lexicon_Metadata.scoreLowerBound) / 2)));
//                }
//
//                lexiconRecords.add(record);
//            }
//            excelFile.close();
//        } catch (IOException ex) {
//            Logger.getLogger(SAReducer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return new Lexicon(lexiconRecords, lexicon_Metadata);
//    }

    private Lexicon loadLexicon_Csv(String lexiconPath, MetaData lexicon_Metadata) {

        ArrayList<LexiconRecord> lexiconRecords = new ArrayList<>();
        try {
            List<String> lines = FileUtils.readLines(new File(lexiconPath));
            for (String line : lines) {
                LexiconRecord record = new LexiconRecord();
                String[] values = line.split(lexicon_Metadata.PropertiesDelimiter);
                record.text = values[lexicon_Metadata.textPropertyIndex].trim();

                // Labeling
                if (lexicon_Metadata.labelType == Enums.LabelType.polarity) {
                    boolean polarity = values[lexicon_Metadata.labelPropertyIndex].trim()
                            .equalsIgnoreCase(lexicon_Metadata.positiveValueName);
                    record.setLabel(new PolarityLabel(polarity));
                } else if (lexicon_Metadata.labelType == Enums.LabelType.score) {
                    double score = Double.valueOf(values[lexicon_Metadata.labelPropertyIndex].trim());
                    record.setLabel(new PolarityLabel(
                            score > ((lexicon_Metadata.scoreUpperBound + lexicon_Metadata.scoreLowerBound) / 2)));
                }
                lexiconRecords.add(record);
            }
        } catch (IOException ex) {
            Logger.getLogger(SAReducer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Lexicon(lexiconRecords, lexicon_Metadata);
    }
}
