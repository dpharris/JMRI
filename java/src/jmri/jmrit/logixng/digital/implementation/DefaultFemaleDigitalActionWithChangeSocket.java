package jmri.jmrit.logixng.digital.implementation;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import jmri.InstanceManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.implementation.AbstractFemaleSocket;
import jmri.jmrit.logixng.DigitalActionWithChangeManager;
import jmri.jmrit.logixng.FemaleDigitalActionWithChangeSocket;
import jmri.jmrit.logixng.MaleDigitalActionWithChangeSocket;

/**
 *
 */
public final class DefaultFemaleDigitalActionWithChangeSocket
        extends AbstractFemaleSocket
        implements FemaleDigitalActionWithChangeSocket {


    public DefaultFemaleDigitalActionWithChangeSocket(Base parent, FemaleSocketListener listener, String name) {
        super(parent, listener, name);
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate() {
        // Female sockets have special handling
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isCompatible(MaleSocket socket) {
        return socket instanceof MaleDigitalActionWithChangeSocket;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean supportsEnableExecution() {
        
        if (isConnected()) {
            return ((MaleDigitalActionWithChangeSocket)getConnectedSocket())
                    .supportsEnableExecution();
        } else {
            throw new UnsupportedOperationException("Socket is not connected");
        }
    }
    
    @Override
    public void execute(boolean hasChangedToTrue) {
        if (isConnected()) {
            ((MaleDigitalActionWithChangeSocket)getConnectedSocket())
                    .execute(hasChangedToTrue);
        }
    }

    @Override
    public String getShortDescription(Locale locale) {
        return Bundle.getMessage(locale, "DefaultFemaleDigitalActionWithChangeSocket_Short");
    }

    @Override
    public String getLongDescription(Locale locale) {
        return Bundle.getMessage(locale, "DefaultFemaleDigitalActionWithChangeSocket_Long", getName());
    }

    /** {@inheritDoc} */
    @Override
    public String getExampleSystemName() {
        return InstanceManager.getDefault(DigitalActionWithChangeManager.class).getSystemNamePrefix() + "DC10";
    }

    /** {@inheritDoc} */
    @Override
    public String getNewSystemName() {
        return InstanceManager.getDefault(DigitalActionWithChangeManager.class)
                .getNewSystemName();
    }

    @Override
    public Map<Category, List<Class<? extends Base>>> getConnectableClasses() {
        return InstanceManager.getDefault(DigitalActionWithChangeManager.class).getActionClasses();
    }

    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
        // Do nothing
    }

}
