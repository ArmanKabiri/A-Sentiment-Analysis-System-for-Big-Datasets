/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modules;

/**
 *
 * @author arman
 */
public class SentenceRecord extends DataSetRecord {

    public int reviewID = 0;

    public SentenceRecord(String text) {
        super();
        this.text = text;
    }

    public SentenceRecord(SentenceRecord sentence) {
        super(sentence);
        this.reviewID = sentence.reviewID;
    }

    public SentenceRecord() {
        super();
    }
}
