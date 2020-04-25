/**
 * @author : Arman Kabiri
 * @Email : "arman73k@gmail.com"
 * @Linked in : https://www.linkedin.com/in/armankabiri73
 * @Created at Feb 11, 2017 , 6:38:36 PM
 */
package Modules;

public class Record {

    public int id;
    public String text;
    protected Label label;

    public Record(Record record) {
        id = record.id;
        text = record.text;
        label = record.label;
    }

    public Record() {
    }

    public Record get() {
        return this;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public Label getLabel() {
        return label;
    }
}