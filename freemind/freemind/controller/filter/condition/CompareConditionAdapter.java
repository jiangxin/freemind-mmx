/*
 * Created on 17.05.2005
 *
 */
package freemind.controller.filter.condition;



abstract class CompareConditionAdapter extends NodeCondition{
    
    private String conditionValue;
    private boolean ignoreCase;
    
    CompareConditionAdapter(String description, 
            String value,
            boolean ignoreCase) {
        super(description);   
        this.conditionValue = value; 
        this.ignoreCase=ignoreCase;
    }
    
    protected int compareTo(String nodeValue) throws NumberFormatException{
        try{
            int i2 = Integer.parseInt(conditionValue);
            int i1 = Integer.parseInt(nodeValue);
            return i1 < i2 ? -1 : (i1 == i2 ? 0 : 1);
        }
        catch(NumberFormatException  fne)
        {
        };
        double d2;
        try{
             d2 = Double.parseDouble(conditionValue);
        }
        catch(NumberFormatException  fne)
        {
            return ignoreCase ?  nodeValue.compareToIgnoreCase(conditionValue) : nodeValue.compareTo(conditionValue);
        };
        double d1 = Double.parseDouble(nodeValue);
        return Double.compare(d1, d2);
    }
    
}