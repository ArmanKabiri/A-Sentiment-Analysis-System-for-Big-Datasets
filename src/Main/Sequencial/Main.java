/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main.Sequencial;

import Aggregation.Aggregator_Average_Polarity;
import Main.Hadoop.SAReducer;
import SA.SentimentPredictor;
import Modules.CommentRecord;
import Modules.DataSet;
import Modules.Enums;
import Modules.Lexicon;
import Modules.LexiconRecord;
import Modules.MetaData;
import Modules.PolarityLabel;
import Modules.SentenceRecord;
import Utils.Data_Preprocessor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author arman
 */
public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Main job = new Main();
        DataSet dataSet = job.loadDataset(args[0], args[3]);
        Lexicon lexicon = job.loadLexicon(args[2], args[4]);
        job.makePredictions(dataSet, lexicon);
        job.saveResults(dataSet, args[1]);
        long endTime = System.currentTimeMillis();
        System.out.println("Sequencial RunTime: " + ((endTime - startTime) / 1000) + "seconds");
    }

    private DataSet loadDataset(String pathFolder, String metaDataPath) {

        Data_Preprocessor data_Preprocessor = new Data_Preprocessor();
        ArrayList<CommentRecord> records = new ArrayList<>();
        MetaData DataSet_Metadata = null;
        try {
            DataSet_Metadata = MetaData.loadMetaData(metaDataPath);
            List<File> files = (List<File>) FileUtils.listFiles(new File(pathFolder), new String[]{"csv"}, true);
            for (File file : files) {
                List<String> lines = FileUtils.readLines(file);
                double counter = 0;
                for (String line : lines) {
                    CommentRecord record = loadRecord_Csv(line, DataSet_Metadata);
                    data_Preprocessor.run(record);
                    records.add(record);
                    if (counter++ % 1000 == 0) {
                        System.out.println("Mapping: " + (counter / lines.size()));
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new DataSet(records, DataSet_Metadata);
    }

    private CommentRecord loadRecord_Csv(String rawString, MetaData inputFile_Metadata) {

        CommentRecord record = new CommentRecord();
        String[] values = rawString.split(inputFile_Metadata.PropertiesDelimiter);
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
        return record;
    }

    private Lexicon loadLexicon(String lexiconPath, String metaDataPath) {

        ArrayList<LexiconRecord> lexiconRecords = new ArrayList<>();
        MetaData lexicon_Metadata = null;
        try {
            lexicon_Metadata = MetaData.loadMetaData(metaDataPath);
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
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Lexicon(lexiconRecords, lexicon_Metadata);
    }

    private void makePredictions(DataSet dataSet, Lexicon lexicon) {

        SentimentPredictor sentimentPredictor = new SentimentPredictor(lexicon);
        Aggregator_Average_Polarity aggregator = new Aggregator_Average_Polarity();

        double counter = 0;
        for (CommentRecord review : dataSet.records) {
            ArrayList<Boolean> polarities = new ArrayList<>();
            for (SentenceRecord sentence : review.sentences) {
                Boolean prediction = sentimentPredictor.run(sentence.text);
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
            review.setEstimation(new PolarityLabel(finalPrediction));
            if (counter++ % 1000 == 0) {
                System.out.println("Reducing: " + (counter / dataSet.records.size()));
            }
        }
    }

    private void saveResults(DataSet dataSet, String outputPath) {

        try {
            File outputFile = new File(outputPath + "/sequencialOutput.txt");
            StringBuilder strBuilder = new StringBuilder();
            for (CommentRecord review : dataSet.records) {
                strBuilder.append(review.id).append("\t").append(((PolarityLabel) review.getLabel()).value ? "positive" : "negative").append(",")
                        .append(((PolarityLabel) review.getEstimation()).value ? "positive" : "negative").append("\n");
            }
            FileUtils.writeStringToFile(outputFile, strBuilder.toString().trim());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
