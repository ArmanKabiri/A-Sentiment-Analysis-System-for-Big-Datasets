/**
 * @author : Arman Kabiri
 * @Email : "arman73k@gmail.com"
 * @Linked in : https://www.linkedin.com/in/armankabiri73
 * @Created at Mar 10, 2018 , 2:50:07 PM
 */
package Utils;

import Modules.CommentRecord;
import Modules.DataSet;
import Modules.SentenceRecord;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Data_Preprocessor {

    private StanfordCoreNLP pipeline;

    /**
     * This method normalizes, splits sentences, uncapitalizes, and lemmatizes
     * the reviews.
     */
    public Data_Preprocessor() {
        //    	initializeStanfordNLP();
    }

    private void initializeStanfordNLP() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        props.setProperty("ssplit.newlineIsSentenceBreak", "always");   //Treat endlines as sentece splitter
        pipeline = new StanfordCoreNLP(props);
    }

    public void run(CommentRecord review) {
        normalize(review);
        lemmatizeSentences2(review);
    }

    public void run(DataSet dataset) {
        for (CommentRecord review : dataset.records) {
            normalize(review);
            lemmatizeSentences2(review);
        }
    }

    private void normalize(CommentRecord review) {

        String text = review.text;
        text = text.trim();
        text = text.replaceAll("(\n)+", ". ");
        text = text.replaceAll("(\\s)+", " ");
        text = text.replaceAll("(…)+", ".");
        text = text.replaceAll("[. ]*[.][ .]*", ".");
        text = text.replaceAll("[.! ]*[!][.! ]*", "!");
        text = text.replaceAll("[.!? ]*[?][.!? ]*", "?");
        text = text.replaceAll("(\\s)*(!)+", "! ");
        text = text.replaceAll("(\\s)*[?]+", "? ");
        text = text.replaceAll("(\\s)*[.]+", ". ");
        review.text = text.trim();

    }

    private void lemmatizeSentences1(CommentRecord review) {

        //            StringBuilder reviewText = new StringBuilder();
        Annotation document = new Annotation(review.text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            SentenceRecord senteceRecord = new SentenceRecord();
            StringBuilder sentenceText = new StringBuilder();
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                sentenceText.append(token.get(CoreAnnotations.LemmaAnnotation.class)).append(" ");
            }
            senteceRecord.text = sentenceText.toString().trim();
            review.sentences.add(senteceRecord);
            //                reviewText.append(senteceRecord.text).append(". ");
        }
        //            review.text = reviewText.toString().trim();
    }

    private void lemmatizeSentences2(CommentRecord review) {

        String regex = "[.!?]+";
        String[] sentences = review.text.split(regex);
        List<SentenceRecord> sentencesList = new ArrayList<>();
        for (String sentence : sentences) {
            sentencesList.add(new SentenceRecord(sentence));
        }
        review.sentences.addAll(sentencesList);
    }
}
