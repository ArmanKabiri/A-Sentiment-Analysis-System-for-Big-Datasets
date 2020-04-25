/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SA;

import Aggregation.Aggregator_Average_Polarity;
import Modules.Lexicon;
import Modules.LexiconRecord;
import Modules.PolarityLabel;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author arman
 */
public class SentimentPredictor {

    private Lexicon lexicon = null;
    private Aggregator_Average_Polarity aggregator = null;

    public SentimentPredictor(Lexicon lexicon) {
        this.lexicon = lexicon;
        aggregator = new Aggregator_Average_Polarity();
    }

    public boolean run_Alt(String text) {
        ArrayList<Boolean> polarities = new ArrayList<>();
        for (LexiconRecord lexiconRecord : lexicon.records) {
            String regex = "((^)|(\\s)|[.!,;:?])" + lexiconRecord.text + "(($)|(\\s)|(\\z)|[.!,;:?])";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String matched = matcher.group();
                polarities.add(((PolarityLabel) lexiconRecord.getLabel()).value);
            }
        }

        if (polarities.isEmpty()) {
            return true;
        } else {
            boolean polarityResult = aggregator.run(polarities);
            return polarityResult;
        }
    }
    
    public Boolean run(String text) {
        ArrayList<Boolean> polarities = new ArrayList<>();
        for (LexiconRecord lexiconRecord : lexicon.records) {
            if (text.contains(lexiconRecord.text)) {
                polarities.add(((PolarityLabel) lexiconRecord.getLabel()).value);
            }
        }

        if (polarities.isEmpty()) {
            return null;
        } else {
            boolean polarityResult = aggregator.run(polarities);
            return polarityResult;
        }
    }
}