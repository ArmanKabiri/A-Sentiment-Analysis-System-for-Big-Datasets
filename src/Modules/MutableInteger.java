/**
 * @author : Arman Kabiri
 * @Email : "arman73k@gmail.com"
 * @Linked in : https://www.linkedin.com/in/armankabiri73
 * @Created at Feb 11, 2017 , 9:50:06 PM
 */
package Modules;

public class MutableInteger implements Comparable<MutableInteger> {

    private int val;

    public MutableInteger(int val) {
        this.val = val;
    }

    public synchronized void increment(int inc) {
        this.val += inc;
    }

    public synchronized int getVal() {
        return this.val;
    }

    public synchronized void setVal(int val) {
        this.val = val;
    }

    public synchronized int get_incOne() {
        return this.val++;
    }

    @Override
    public int compareTo(MutableInteger o) {
        if (val > o.val) {
            return 1;
        } else if (val < o.val) {
            return -1;
        } else {
            return 0;
        }
    }
}
