/**
 * @author : Arman Kabiri
 * @Email : "arman73k@gmail.com"
 * @Linked in : https://www.linkedin.com/in/armankabiri73
 * @Created at Oct 21, 2016 , 5:18:40 PM
 */
package Modules;

public class LexiconRecord extends Record {

    public LexiconRecord() {
    }

    public LexiconRecord(LexiconRecord lexRec) {
        super(lexRec);
    }

    @Override
    public LexiconRecord get() {
        return this;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj instanceof LexiconRecord) {
//            boolean a = this.text.equals(((LexiconRecord) obj).text);
//            if (a == true) {
//                int a21 = 2;
//            }
//            return a;
//        } else {
//            return false;
//        }
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 3;
//        hash = 83 * hash + Objects.hashCode(this.text);
//        return hash;
//    }
    @Override
    public String toString() {
        return text + " : " + label.toString();
    }
}
