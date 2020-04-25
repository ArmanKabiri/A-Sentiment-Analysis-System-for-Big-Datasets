/**
 * @author : Arman Kabiri
 * @Email : "arman73k@gmail.com"
 * @Linked in : https://www.linkedin.com/in/armankabiri73
 * @Created at Feb 14, 2018 , 5:33:31 PM
 */
package Modules;

public class PolarityLabel extends Label {

    public boolean value;

    public PolarityLabel(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

}
