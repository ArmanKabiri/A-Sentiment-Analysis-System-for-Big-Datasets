/**
 * @author : Arman Kabiri
 * @Email : "arman73k@gmail.com"
 * @Linked in : https://www.linkedin.com/in/armankabiri73
 * @Created at Feb 11, 2017 , 6:32:14 PM
 */
package Modules;

public class Enums {

    public enum AnalysisMethodInUse {
        ml, lexicon
    };

    public enum FeatureExtractionType {
        uniGrams, biGrams, lexicon
    }

    public enum FeatureSelectionType {
        occurrenceFilter, stopWordsFilter, limitNGramsToLexicon
    }

    public enum LexiconInUse {
        MPQA_Polarity, BingLiu_Polarity, BingLiu_Score,MPQA_Score, SentiWordNet_Score, SentiSTR_Score, NRC_Polarity, SocialSent_Score, SenticNet_Score, NRC_Score
    }

    public enum AggregationMethod {
        DS, average
    }

    public enum LexiconBasedRule {
        questionSentences
    }

    public enum LabelType {
        polarity, score
    }

    public enum FileType {
        excel, json, xml, csv
    }

}
