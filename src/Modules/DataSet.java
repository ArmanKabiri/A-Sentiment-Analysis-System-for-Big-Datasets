/**
 * @author : Arman Kabiri
 * @Email : "arman73k@gmail.com"
 * @Linked in : https://www.linkedin.com/in/armankabiri73
 * @Created at Feb 15, 2018 , 10:29:52 AM
 */
package Modules;

import java.util.ArrayList;

public class DataSet {

    public ArrayList<CommentRecord> records = new ArrayList<>();
    public MetaData metaData;

    public DataSet(ArrayList<CommentRecord> records, MetaData metaData) {
        this.records = records;
        this.metaData = metaData;
    }

    public DataSet() {
    }

}
