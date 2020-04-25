/**
 * @author : Arman Kabiri
 * @Email : "arman73k@gmail.com"
 * @Linked in : https://www.linkedin.com/in/armankabiri73
 * @Created at Feb 15, 2018 , 10:36:30 AM
 */
package Modules;

import java.util.ArrayList;

public class Lexicon {

    public ArrayList<LexiconRecord> records;
    public MetaData metaData;

    public Lexicon(ArrayList<LexiconRecord> records, MetaData metaData) {
        this.records = records;
        this.metaData = metaData;
    }

    public Lexicon() {
    }

}
