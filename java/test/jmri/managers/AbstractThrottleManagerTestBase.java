package jmri.managers;

import jmri.LocoAddress;
import jmri.DccLocoAddress;
import jmri.DccThrottle;
import jmri.Throttle;
import jmri.ThrottleListener;
import jmri.ThrottleManager;
import jmri.util.JUnitUtil;

import org.junit.Assert;
import org.junit.jupiter.api.*;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

/**
 * Base for ThrottleManager tests in specific jmrix.packages
 * <p>
 * This is not itself a test class, e.g. should not be added to a suite.
 * Instead, this forms the base for test classes, including providing some
 * common tests
 *
 * @author   Bob Jacobsen 
 * @author   Paul Bender Copyright (C) 2016
 */
public abstract class AbstractThrottleManagerTestBase {

    /**
     * Overload to load tm with actual object; create scaffolds as needed
     */
    abstract public void setUp(); 

    protected ThrottleManager tm = null; // holds objects under test

    protected boolean throttleFoundResult = false;
    protected boolean throttleNotFoundResult = false;
    protected boolean throttleStealResult = false;

    protected class ThrottleListen implements ThrottleListener {

        private DccThrottle foundThrottle = null;

        @Override
        public void notifyThrottleFound(DccThrottle t){
            throttleFoundResult = true;
            foundThrottle = t;
        }

        @Override
        public void notifyFailedThrottleRequest(LocoAddress address, String reason){
            throttleNotFoundResult = true;
        }

        @Override
        public void notifyDecisionRequired(LocoAddress address, DecisionType question) {
            if ( question == DecisionType.STEAL ){
                throttleStealResult = true;
            }
        }

        protected DccThrottle getThrottle() {
            return foundThrottle;
        }

    }

    @AfterEach
    public void postTestReset(){
       throttleFoundResult = false;
       throttleNotFoundResult = false;
       throttleStealResult = false;
    }

    // start of common tests
    // test creation - real work is in the setup() routine
    @Test
    public void testCreate() {
        Assert.assertNotNull(tm);
    }

    @Test
    public void getUserName() {
        Assert.assertNotNull(tm.getUserName());
    }

    @Test
    public void hasDispatchFunction() {
        Assert.assertNotNull(tm.hasDispatchFunction());
    }

    @Test
    public void addressTypeUnique() {
        Assert.assertNotNull(tm.addressTypeUnique());
    }

    @Test
    public void canBeLongAddress() {
       Assert.assertNotNull(tm.canBeLongAddress(50));
    }

    @Test
    public void canBeShortAddress() {
       Assert.assertNotNull(tm.canBeShortAddress(50));
    }

    @Test
    public void supportedSpeedModes() {
        Assert.assertNotNull(tm.supportedSpeedModes());
    }

    @Test
    public void getAddressTypes() {
        Assert.assertNotNull(tm.getAddressTypes());
    }

    @Test
    public void getAddressProtocolTypes() {
        Assert.assertNotNull(tm.getAddressProtocolTypes());
    }

    @Test
    public void testGetAddressNullValue() {
        Assert.assertNull("null address value", tm.getAddress(null, ""));
    }

    @Test
    public void testGetAddressNullProtocol() {
        Assert.assertNull("null protocol", tm.getAddress("42", (String)null));
    }

    @Test
    public void testGetAddressShort() {
        Assert.assertNotNull("short address value", tm.getAddress("42", LocoAddress.Protocol.DCC));
    }

    @Test
    public void testGetAddressLong() {
        Assert.assertNotNull("long address value", tm.getAddress("4200", LocoAddress.Protocol.DCC));
    }

    @Test
    public void testGetAddressShortString() {
        Assert.assertNotNull("short address value from strings", tm.getAddress("42", "DCC"));
    }

    @Test
    public void testGetAddressLongString() {
        Assert.assertNotNull("long address value from strings", tm.getAddress("4200", "DCC"));
    }

    @Test
    public void testGetThrottleInfo() {
        DccLocoAddress addr = new DccLocoAddress(42, false);
        Assert.assertEquals("throttle use 0", 0, tm.getThrottleUsageCount(addr));
        Assert.assertEquals("throttle use 0", 0, tm.getThrottleUsageCount(42, false));
        Assert.assertNull("NULL", tm.getThrottleInfo(addr, Throttle.F28));
        ThrottleListen throtListen = new ThrottleListen();
        tm.requestThrottle(addr, throtListen, true);
        JUnitUtil.waitFor(()-> (tm.getThrottleInfo(addr, Throttle.ISFORWARD) != null), "reply didn't arrive");

        Assert.assertTrue(throttleFoundResult);
        Assert.assertFalse( throttleNotFoundResult );
        Assert.assertFalse( throttleStealResult );

        Assert.assertNotNull("is forward", tm.getThrottleInfo(addr, Throttle.ISFORWARD));
        Assert.assertNotNull("speed setting", tm.getThrottleInfo(addr, Throttle.SPEEDSETTING));
        Assert.assertNotNull("speed increment", tm.getThrottleInfo(addr, Throttle.SPEEDINCREMENT));
        Assert.assertNotNull("speed step mode", tm.getThrottleInfo(addr, Throttle.SPEEDSTEPMODE));
        Assert.assertNotNull("F0", tm.getThrottleInfo(addr, Throttle.F0));
        Assert.assertNotNull("F1", tm.getThrottleInfo(addr, Throttle.F1));
        Assert.assertNotNull("F2", tm.getThrottleInfo(addr, Throttle.F2));
        Assert.assertNotNull("F3", tm.getThrottleInfo(addr, Throttle.F3));
        Assert.assertNotNull("F4", tm.getThrottleInfo(addr, Throttle.F4));
        Assert.assertNotNull("F5", tm.getThrottleInfo(addr, Throttle.F5));
        Assert.assertNotNull("F6", tm.getThrottleInfo(addr, Throttle.F6));
        Assert.assertNotNull("F7", tm.getThrottleInfo(addr, Throttle.F7));
        Assert.assertNotNull("F8", tm.getThrottleInfo(addr, Throttle.F8));
        Assert.assertNotNull("F9", tm.getThrottleInfo(addr, Throttle.F9));
        Assert.assertNotNull("F10", tm.getThrottleInfo(addr, Throttle.F10));
        Assert.assertNotNull("F11", tm.getThrottleInfo(addr, Throttle.F11));
        Assert.assertNotNull("F12", tm.getThrottleInfo(addr, Throttle.F12));
        Assert.assertNotNull("F13", tm.getThrottleInfo(addr, Throttle.F13));
        Assert.assertNotNull("F14", tm.getThrottleInfo(addr, Throttle.F14));
        Assert.assertNotNull("F15", tm.getThrottleInfo(addr, Throttle.F15));
        Assert.assertNotNull("F16", tm.getThrottleInfo(addr, Throttle.F16));
        Assert.assertNotNull("F17", tm.getThrottleInfo(addr, Throttle.F17));
        Assert.assertNotNull("F18", tm.getThrottleInfo(addr, Throttle.F18));
        Assert.assertNotNull("F19", tm.getThrottleInfo(addr, Throttle.F19));
        Assert.assertNotNull("F20", tm.getThrottleInfo(addr, Throttle.F20));
        Assert.assertNotNull("F21", tm.getThrottleInfo(addr, Throttle.F21));
        Assert.assertNotNull("F22", tm.getThrottleInfo(addr, Throttle.F22));
        Assert.assertNotNull("F23", tm.getThrottleInfo(addr, Throttle.F23));
        Assert.assertNotNull("F24", tm.getThrottleInfo(addr, Throttle.F24));
        Assert.assertNotNull("F25", tm.getThrottleInfo(addr, Throttle.F25));
        Assert.assertNotNull("F26", tm.getThrottleInfo(addr, Throttle.F26));
        Assert.assertNotNull("F27", tm.getThrottleInfo(addr, Throttle.F27));
        Assert.assertNotNull("F28", tm.getThrottleInfo(addr, Throttle.F28));
        Assert.assertNull("NULL", tm.getThrottleInfo(addr, "NOT A VARIABLE"));
        Assert.assertEquals("throttle use 1 addr", 1, tm.getThrottleUsageCount(addr));
        Assert.assertEquals("throttle use 1 int b", 1, tm.getThrottleUsageCount(42, false));
        Assert.assertEquals("throttle use 0", 0, tm.getThrottleUsageCount(77, true));
        
        // remove listener on throttle created in process
        DccThrottle throttle = throtListen.getThrottle();
        Assert.assertNotNull(throttle);
        tm.releaseThrottle(throttle, throtListen);
        JUnitUtil.waitFor(()-> (tm.getThrottleUsageCount(addr) == 0), "throttle still in use after release");
    }

    // private final static Logger log = LoggerFactory.getLogger(AbstractThrottleManagerTestBase.class);
}
