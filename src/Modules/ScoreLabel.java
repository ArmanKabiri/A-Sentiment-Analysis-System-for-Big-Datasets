/**
 * @author : Arman Kabiri
 * @Email : "arman73k@gmail.com"
 * @Linked in : https://www.linkedin.com/in/armankabiri73
 * @Created at Feb 14, 2018 , 5:33:49 PM
 */
package Modules;

public class ScoreLabel extends Label {

    public double value, lowerBound, upperBound;

    public ScoreLabel(double value, double lowerBound, double upperBound) {
        this.value = value;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

}
