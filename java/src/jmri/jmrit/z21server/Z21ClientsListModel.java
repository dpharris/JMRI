package jmri.jmrit.z21server;

/**
 * z21server - connected clients table
 *
 * @author Brett Hoffman Copyright (C) 2009
 * @author Created by Brett Hoffman on:
 * @author 11/11/09.
 * @author Eckart Meyer (C) 2025
 */
import java.net.InetAddress;
import java.util.ArrayList;
//import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Z21ClientsListModel extends AbstractTableModel {

    //ArrayList<DeviceServer> deviceList;
    //DeviceServer[] deviceList;
    ArrayList<InetAddress> addrList = new ArrayList<>(ClientManager.getInstance().getRegisteredClients().keySet());

    //static final ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrit.withrottle.z21serverBundle");

    Z21ClientsListModel() {
    
    }
//    Z21ClientsListModel(ArrayList<DeviceServer> deviceList) {
//
//        this.deviceList = deviceList;
//
//    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public int getRowCount() {
//        return deviceList.size();
        return ClientManager.getInstance().getRegisteredClients().size();
    }

    /**
     * Added column LabelRosterId since 4.15.4. 
     */
    @Override
    public String getColumnName(int col) {
        String title;
        switch (col) {
            case 0: {
                title = Bundle.getMessage("LabelDeviceName");
                break;
            }
            case 1: {
                title = Bundle.getMessage("LabelAddress");
                break;
            }
            case 2: {
                title = Bundle.getMessage("LabelRosterId");
                break;
            }
            default: {
                title = "";
            }
        }
        return title;
    }

    @Override
    public String getValueAt(int row, int col) {
        //ArrayList<InetAddress> addrList = new ArrayList<>(ClientManager.getInstance().getRegisteredClients().keySet());
        if (addrList.size() < 1) {
            return null;
        }
        // some error checking
        if (row >= addrList.size()) {
            log.debug("row is greater than device list size");
            return null;
        }
        AppClient client = ClientManager.getInstance().getRegisteredClients().get(addrList.get(row));
        if (col == 0) {
            //return ClientManager.getInstance().getRegisteredClients().get(addrList.get(row)).toString();
            return addrList.get(row).toString();
        } else if (col == 1) {
            return (client != null  &&  client.getActiveThrottle() != null) ? client.getActiveThrottle().getLocoAddress().toString() : "";
        } else {
            return (client != null  &&  client.getActiveThrottle() != null) ? client.getActiveRosterIdString() : "";
        }
    }

    public void updateClientList() {
        addrList = new ArrayList<>(ClientManager.getInstance().getRegisteredClients().keySet());
//        this.deviceList = deviceList;
        this.fireTableDataChanged();
    }

    private final static Logger log = LoggerFactory.getLogger(Z21ClientsListModel.class);
}
