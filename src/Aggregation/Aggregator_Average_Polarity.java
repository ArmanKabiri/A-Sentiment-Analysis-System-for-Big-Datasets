/**
 * @author : Arman Kabiri
 * @Email : "arman73k@gmail.com"
 * @Linked in : https://www.linkedin.com/in/armankabiri73
 * @Created at Dec 22, 2016 , 9:04:12 PM
 */
package Aggregation;

import Modules.MutableInteger;
import java.util.ArrayList;

public class Aggregator_Average_Polarity{
    
    public Boolean run(ArrayList<Boolean> labels) {
        MutableInteger positiveCounter = new MutableInteger(0);
        for(Boolean label:labels){
        	if (label) {
                positiveCounter.increment(1);
            }
        }
       
        return (positiveCounter.getVal() >= labels.size() - positiveCounter.getVal());
    }

    @Override
    public String toString() {
        return "Average_Polarity";
    }
}