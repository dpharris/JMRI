package jmri.jmrit.logixng.digital.actions_with_change.configurexml;

import jmri.InstanceManager;
import jmri.jmrit.logixng.digital.actions_with_change.OnChangeAction;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.DigitalActionWithChangeManager;

/**
 * Handle XML configuration for ActionLightXml objects.
 *
 * @author Bob Jacobsen Copyright: Copyright (c) 2004, 2008, 2010
 * @author Daniel Bergqvist Copyright (C) 2019
 */
public class OnChangeActionXml extends jmri.managers.configurexml.AbstractNamedBeanManagerConfigXML {

    public OnChangeActionXml() {
    }

    /**
     * Default implementation for storing the contents of a SE8cSignalHead
     *
     * @param o Object to store, of type TripleTurnoutSignalHead
     * @return Element containing the complete info
     */
    @Override
    public Element store(Object o) {
        OnChangeAction p = (OnChangeAction) o;

        Element element = new Element("on-change");
        element.setAttribute("class", this.getClass().getName());
        element.addContent(new Element("systemName").addContent(p.getSystemName()));
        
        storeCommon(p, element);

        element.setAttribute("whichChange", p.getWhichChange().name());
        
        Element e2 = new Element("socket");
        e2.addContent(new Element("socketName").addContent(p.getChild(1).getName()));
        MaleSocket socket = p.getActionSocket().getConnectedSocket();
        String socketSystemName;
        if (socket != null) {
            socketSystemName = socket.getSystemName();
        } else {
            socketSystemName = p.getActionSocketSystemName();
        }
        if (socketSystemName != null) {
            e2.addContent(new Element("systemName").addContent(socketSystemName));
        }
        element.addContent(e2);

        return element;
    }
    
    @Override
    public boolean load(Element shared, Element perNode) {
        
        Attribute whichChangeAttribute = shared.getAttribute("whichChange");
        OnChangeAction.ChangeType whichChange = OnChangeAction.ChangeType.valueOf(whichChangeAttribute.getValue());
        
        String sys = getSystemName(shared);
        String uname = getUserName(shared);
        OnChangeAction h = new OnChangeAction(sys, uname, whichChange);

        loadCommon(h, shared);
        
        Element socketName = shared.getChild("socket").getChild("socketName");
        h.getChild(0).setName(socketName.getTextTrim());
        Element socketSystemName = shared.getChild("socket").getChild("systemName");
        if (socketSystemName != null) {
            h.setActionSocketSystemName(socketSystemName.getTextTrim());
        }
        
        InstanceManager.getDefault(DigitalActionWithChangeManager.class).registerAction(h);
        return true;
    }
    
    @Override
    public void load(Element element, Object o) {
        log.error("Invalid method called");
    }

    private final static Logger log = LoggerFactory.getLogger(OnChangeActionXml.class);
}
