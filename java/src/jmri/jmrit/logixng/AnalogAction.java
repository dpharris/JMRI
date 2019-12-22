package jmri.jmrit.logixng;

/**
 * A LogixNG analog action.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public interface AnalogAction extends Base {

    /**
     * Set an analog value.
     * 
     * @param value the value. The male socket that holds this action ensures
     * that this value is not Double.NaN or an infinite value.
     */
    public void setValue(double value) throws Exception;
    
}
