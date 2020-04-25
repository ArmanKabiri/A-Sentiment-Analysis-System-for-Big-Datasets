/**
 * @author : Arman Kabiri
 * @Email : "arman73k@gmail.com"
 * @Linked in : https://www.linkedin.com/in/armankabiri73
 * @Created at Feb 15, 2018 , 12:27:54 PM
 */
package Modules;

import java.util.ArrayList;

public class DataSetRecord extends Record {

    private Label estimation;

    public DataSetRecord(DataSetRecord record) {
        super(record);
        estimation = record.estimation;
    }

    public DataSetRecord() {
    }

    public void setEstimation(Label estimation) {
        this.estimation = estimation;
    }

    public Label getEstimation() {
        return estimation;
    }

    @Override
    public DataSetRecord get() {
        return this;
    }

    @Override
    public String toString() {
            StringBuilder strBuilder = new StringBuilder();
            if (text.length() > 20) {
                strBuilder.append(text.substring(0, 20));
            }
            else {
                strBuilder.append(text);
            }
            strBuilder.append("...");
            if (label != null) {
                strBuilder.append(" : ").append(label.toString());
            }
            if (estimation != null) {
                strBuilder.append(" -> ").append(estimation.toString());
            }
            return strBuilder.toString();
    }
}
