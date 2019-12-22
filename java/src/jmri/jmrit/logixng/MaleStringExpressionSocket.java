package jmri.jmrit.logixng;

/**
 * A LogixNG male StringExpressionBean socket.
 */
public interface MaleStringExpressionSocket
        extends MaleSocket, StringExpressionBean {

    /**
     * {@inheritDoc}
     * <P>
     * This method must ensure that the value is not a Double.NaN, negative
     * infinity or positive infinity. If that is the case, it must throw an
     * IllegalArgumentException before checking if an error has occured.
     */
    @Override
    public String evaluate() throws Exception;
    
}
