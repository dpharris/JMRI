package jmri.jmrix.openlcb.swing.stleditor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;

import jmri.jmrix.can.CanSystemConnectionMemo;
import jmri.util.JmriJFrame;
import jmri.util.swing.JComboBoxUtil;
import jmri.util.swing.JmriPanel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static org.openlcb.MimicNodeStore.ADD_PROP_NODE;
import static org.openlcb.MimicNodeStore.CLEAR_ALL_NODES;
import static org.openlcb.MimicNodeStore.NodeMemo.UPDATE_PROP_SIMPLE_NODE_IDENT;

import org.openlcb.*;
import org.openlcb.implementations.*;
import org.openlcb.swing.*;
import org.openlcb.swing.memconfig.MemConfigDescriptionPane;
import org.openlcb.cdi.impl.ConfigRepresentation;
// import org.openlcb.MimicNodeStore.NodeMemo;


/**
 * Pane for editing STL logic.
 *
 * TODO
 *    Finish Operator enum
 *    Make the node selector full width
 *    Retain logic split location
 *    Select by group
 *    Implement CDI connection
 *    Figure out the conversion logic
 *
 * @author Dave Sand Copyright (C) 2024
 * @since 5.7.x
 */
public class StlEditorPane extends jmri.util.swing.JmriPanel
        implements jmri.jmrix.can.swing.CanPanelInterface {

    private CanSystemConnectionMemo _canMemo;
    private OlcbInterface _iface;
    private ConfigRepresentation _cdi;
    private MimicNodeStore _store;

//     NodeSelector nodeSelector;

    boolean cancelled = false;
    boolean ready = false;
    static boolean _dirty = false;
    int _logicRow = -1;     // The last selected row, -1 for none

    JLabel statusField;

    JComboBox<NodeEntry> _nodeBox;
    private DefaultComboBoxModel<NodeEntry> _nodeModel = new DefaultComboBoxModel<NodeEntry>();

    JComboBox<Operator> _operators = new JComboBox<>(Operator.values());

    private List<GroupList> _groupList = new ArrayList<>();
    private List<LogicList> _logicList = new ArrayList<>();
    private List<InputList> _inputList = new ArrayList<>();
    private List<OutputList> _outputList = new ArrayList<>();
    private List<ReceiverList> _receiverList = new ArrayList<>();
    private List<TransmitterList> _transmitterList = new ArrayList<>();

    private JTable _groupTable;
    private JTable _logicTable;
    private JTable _inputTable;
    private JTable _outputTable;
    private JTable _receiverTable;
    private JTable _transmitterTable;

    private JScrollPane _logicScrollPane;

    private JTabbedPane _detailTabs;

    private JPanel _editButtons;
    private JButton _addButton;
    private JButton _insertButton;
    private JButton _moveUpButton;
    private JButton _moveDownButton;
    private JButton _deleteButton;
    private JButton _refreshButton;
    private JButton _storeButton;

    public StlEditorPane() {
    }

    @Override
    public void initComponents(CanSystemConnectionMemo memo) {
        _canMemo = memo;
        _iface = memo.get(OlcbInterface.class);
        _store = memo.get(MimicNodeStore.class);

        // Add to GUI here
        setLayout(new BorderLayout());
//         var header = new JPanel();
//         add(header, BorderLayout.NORTH);

        var footer = new JPanel();
        footer.setLayout(new BorderLayout());

        _addButton = new JButton(Bundle.getMessage("ButtonAdd"));
        _insertButton = new JButton(Bundle.getMessage("ButtonInsert"));
        _moveUpButton = new JButton(Bundle.getMessage("ButtonMoveUp"));
        _moveDownButton = new JButton(Bundle.getMessage("ButtonMoveDown"));
        _deleteButton = new JButton(Bundle.getMessage("ButtonDelete"));
        _refreshButton = new JButton(Bundle.getMessage("ButtonRefresh"));
        _storeButton = new JButton(Bundle.getMessage("ButtonStore"));


        _addButton.addActionListener(this::pushedAddButton);
        _insertButton.addActionListener(this::pushedInsertButton);
        _moveUpButton.addActionListener(this::pushedMoveUpButton);
        _moveDownButton.addActionListener(this::pushedMoveDownButton);
        _deleteButton.addActionListener(this::pushedDeleteButton);
//         _refreshButton.addActionListener(this::pushedRefreshButton);
//         _storeButton.addActionListener(this::pushedStoreButton);

        _editButtons = new JPanel();
        _editButtons.add(_addButton);
        _editButtons.add(_insertButton);
        _editButtons.add(_moveUpButton);
        _editButtons.add(_moveDownButton);
        _editButtons.add(_deleteButton);
        footer.add(_editButtons, BorderLayout.WEST);

        var dataButtons = new JPanel();
        dataButtons.add(_refreshButton);
        dataButtons.add(_storeButton);
        footer.add(dataButtons, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);

        // Define the node selector which goes in the header
        var nodeSelector = new JPanel();
        nodeSelector.setLayout(new FlowLayout());
//         header.add(nodeSelector);

        _nodeBox = new JComboBox<NodeEntry>(_nodeModel);

        // Load node selector combo box
        for (MimicNodeStore.NodeMemo nodeMemo : _store.getNodeMemos() ) {
            newNodeInList(nodeMemo);
        }

        _nodeBox.addActionListener(this::nodeSelected);
        JComboBoxUtil.setupComboBoxMaxRows(_nodeBox);


        _nodeBox.revalidate();
//         _nodeBox.repaint();
        nodeSelector.add(_nodeBox);
        add(nodeSelector, BorderLayout.NORTH);

        // Define the center section of the window which consists of 5 tabs
        _detailTabs = new JTabbedPane();

        _detailTabs.add(Bundle.getMessage("ButtonStl"), buildLogicPanel());  // NOI18N
        _detailTabs.add(Bundle.getMessage("ButtonI"), buildInputPanel());  // NOI18N
        _detailTabs.add(Bundle.getMessage("ButtonQ"), buildOutputPanel());  // NOI18N
        _detailTabs.add(Bundle.getMessage("ButtonY"), buildReceiverPanel());  // NOI18N
        _detailTabs.add(Bundle.getMessage("ButtonZ"), buildTransmitterPanel());  // NOI18N

        _detailTabs.addChangeListener(this::tabSelected);

        add(_detailTabs, BorderLayout.CENTER);

        setReady(false);
    }

    // --------------  tab configurations ---------

    private JScrollPane buildGroupPanel() {
        // Create scroll pane
        var model = new GroupModel();
        _groupTable = new JTable(model);
        var scrollPane = new JScrollPane(_groupTable);

        // resize columns
        for (int i = 0; i < model.getColumnCount(); i++) {
            int width = model.getPreferredWidth(i);
            _groupTable.getColumnModel().getColumn(i).setPreferredWidth(width);
        }

        return scrollPane;
    }

    private JSplitPane buildLogicPanel() {
        // Create scroll pane
        var model = new LogicModel();
        _logicTable = new JTable(model);
        _logicScrollPane = new JScrollPane(_logicTable);

        // resize columns
        for (int i = 0; i < _logicTable.getColumnCount(); i++) {
            int width = model.getPreferredWidth(i);
            _logicTable.getColumnModel().getColumn(i).setPreferredWidth(width);
        }

        // Use the operators combo box for the operator column
        var col = _logicTable.getColumnModel().getColumn(1);
        col.setCellEditor(new DefaultCellEditor(_operators));
        JComboBoxUtil.setupComboBoxMaxRows(_operators);

        var  selectionModel = _logicTable.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(this::handleRowSelection);

        var logicPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildGroupPanel(), _logicScrollPane);
        return logicPanel;
    }

    private JScrollPane buildInputPanel() {
        // Create scroll pane
        var model = new InputModel();
        _inputTable = new JTable(model);
        _inputTable.setRowSelectionAllowed(false);
        var scrollPane = new JScrollPane(_inputTable);

        // resize columns
        for (int i = 0; i < model.getColumnCount(); i++) {
            int width = model.getPreferredWidth(i);
            _inputTable.getColumnModel().getColumn(i).setPreferredWidth(width);
        }

        return scrollPane;
    }

    private JScrollPane buildOutputPanel() {
        // Create scroll pane
        var model = new OutputModel();
        _outputTable = new JTable(model);
        _outputTable.setRowSelectionAllowed(false);
        var scrollPane = new JScrollPane(_outputTable);

        // resize columns
        for (int i = 0; i < model.getColumnCount(); i++) {
            int width = model.getPreferredWidth(i);
            _outputTable.getColumnModel().getColumn(i).setPreferredWidth(width);
        }

        return scrollPane;
    }

    private JScrollPane buildReceiverPanel() {
        // Create scroll pane
        var model = new ReceiverModel();
        _receiverTable = new JTable(model);
        _receiverTable.setRowSelectionAllowed(false);
        var scrollPane = new JScrollPane(_receiverTable);

        // resize columns
        for (int i = 0; i < model.getColumnCount(); i++) {
            int width = model.getPreferredWidth(i);
            _receiverTable.getColumnModel().getColumn(i).setPreferredWidth(width);
        }

        return scrollPane;
    }

    private JScrollPane buildTransmitterPanel() {
        // Create scroll pane
        var model = new TransmitterModel();
        _transmitterTable = new JTable(model);
        _transmitterTable.setRowSelectionAllowed(false);
        var scrollPane = new JScrollPane(_transmitterTable);

        // resize columns
        for (int i = 0; i < model.getColumnCount(); i++) {
            int width = model.getPreferredWidth(i);
            _transmitterTable.getColumnModel().getColumn(i).setPreferredWidth(width);
        }

        return scrollPane;
    }

    private void tabSelected(ChangeEvent e) {
        if (_detailTabs.getSelectedIndex() == 0) {
            _editButtons.setVisible(true);
        } else {
            _editButtons.setVisible(false);
        }
    }

    // --------------  Logic table methods ---------

    public void handleRowSelection(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            _logicRow = _logicTable.getSelectedRow();
            _moveUpButton.setEnabled(_logicRow > 0);
            _moveDownButton.setEnabled(_logicRow < _logicTable.getRowCount() - 1);
        }
    }

    private void pushedAddButton(ActionEvent e) {
        _logicList.add(new LogicList("", null, "", ""));
        _logicRow = _logicList.size() - 1;
        _logicTable.revalidate();
        _logicTable.setRowSelectionInterval(_logicRow, _logicRow);
        _logicScrollPane.revalidate();
    }

    private void pushedInsertButton(ActionEvent e) {
        if (_logicRow >= 0 && _logicRow < _logicList.size()) {
            _logicList.add(_logicRow, new LogicList("", null, "", ""));
            _logicTable.revalidate();
            _logicTable.setRowSelectionInterval(_logicRow, _logicRow);
            _logicScrollPane.revalidate();
        }
    }

    private void pushedMoveUpButton(ActionEvent e) {
        if (_logicRow >= 0 && _logicRow < _logicList.size()) {
            var logicRow = _logicList.remove(_logicRow);
            _logicList.add(_logicRow - 1, logicRow);
            _logicRow--;
            _logicTable.revalidate();
            _logicTable.setRowSelectionInterval(_logicRow, _logicRow);
        }
    }

    private void pushedMoveDownButton(ActionEvent e) {
        if (_logicRow >= 0 && _logicRow < _logicList.size()) {
            var logicRow = _logicList.remove(_logicRow);
            _logicList.add(_logicRow + 1, logicRow);
            _logicRow++;
            _logicTable.revalidate();
            _logicTable.setRowSelectionInterval(_logicRow, _logicRow);
        }
    }

    private void pushedDeleteButton(ActionEvent e) {
        if (_logicRow >= 0 && _logicRow < _logicList.size()) {
            _logicList.remove(_logicRow);
            _logicTable.revalidate();
            _logicScrollPane.revalidate();
        }
    }

    // --------------  node selector ---------

    void setReady(boolean t) {
        ready = t;
    }

    /**
     * When a node is selected, load the CDI.
     * @parm e The combo box action event.
     */
    private void nodeSelected(ActionEvent e) {
        var item = _nodeBox.getSelectedItem();
        log.info("nodeSelected: {}", _nodeBox.getSelectedItem());

        // Load data
        loadGroups();
        loadLogic();
        loadInputs();
        loadOutputs();
        loadReceivers();
        loadTransmitters();

        setReady(true);
    }

    private void newNodeInList(MimicNodeStore.NodeMemo nodeMemo) {
        // Add filter for Tower LCC+Q

//         int i = 0;
//         if (_nodeModel.getIndexOf(nodeMemo.getNodeID()) >= 0) {
//             // already exists. Do nothing.
//             return;
//         }
        NodeEntry e = new NodeEntry(nodeMemo);

//         while ((i < _nodeModel.getSize()) && (_nodeModel.getElementAt(i).compareTo(e) < 0)) {
//             ++i;
//         }
//         _nodeModel.insertElementAt(e, i);
        _nodeModel.addElement(e);
    }

    void pushedCancel(ActionEvent e) {
        if (ready) {
            cancelled = true;
        }
    }

    // Notifies that the contents of a given entry have changed. This will delete and re-add the
    // entry to the model, forcing a refresh of the box.
    private void updateComboBoxModelEntry(NodeEntry nodeEntry) {
        int idx = _nodeModel.getIndexOf(nodeEntry.getNodeID());
        if (idx < 0) {
            return;
        }
        NodeEntry last = _nodeModel.getElementAt(idx);
        if (last != nodeEntry) {
            // not the same object -- we're talking about an abandoned entry.
            nodeEntry.dispose();
            return;
        }
        NodeEntry sel = (NodeEntry) _nodeModel.getSelectedItem();
        _nodeModel.removeElementAt(idx);
        _nodeModel.insertElementAt(nodeEntry, idx);
        _nodeModel.setSelectedItem(sel);
    }

    protected class NodeEntry implements Comparable<NodeEntry>, PropertyChangeListener {
        final MimicNodeStore.NodeMemo nodeMemo;
        String description = "";

        NodeEntry(MimicNodeStore.NodeMemo memo) {
            this.nodeMemo = memo;
            memo.addPropertyChangeListener(this);
            updateDescription();
        }

        /**
         * Constructor for prototype display value
         *
         * @param description prototype display value
         */
        private NodeEntry(String description) {
            this.nodeMemo = null;
            this.description = description;
        }

        public NodeID getNodeID() {
            return nodeMemo.getNodeID();
        }

        private void updateDescription() {
            int termCount = 99;
            SimpleNodeIdent ident = nodeMemo.getSimpleNodeIdent();
            StringBuilder sb = new StringBuilder();
            sb.append(nodeMemo.getNodeID().toString());
            int count = 0;
            if (count < termCount) {
                count += addToDescription(ident.getUserName(), sb);
            }
            if (count < termCount) {
                count += addToDescription(ident.getUserDesc(), sb);
            }
            if (count < termCount) {
                if (!ident.getMfgName().isEmpty() || !ident.getModelName().isEmpty()) {
                    count += addToDescription(ident.getMfgName() + " " +ident.getModelName(),
                        sb);
                }
            }
            if (count < termCount) {
                count += addToDescription(ident.getSoftwareVersion(), sb);
            }
            String newDescription = sb.toString();
            if (!description.equals(newDescription)) {
                description = newDescription;
                // update combo box model.
//                 updateComboBoxModelEntry(this);
            }
        }

        private int addToDescription(String s, StringBuilder sb) {
            if (s.isEmpty()) {
                return 0;
            }
            sb.append(" - ");
            sb.append(s);
            return 1;
        }

        private long reorder(long n) {
            return (n < 0) ? Long.MAX_VALUE - n : Long.MIN_VALUE + n;
        }

        @Override
        public int compareTo(NodeEntry otherEntry) {
            long l1 = reorder(getNodeID().toLong());
            long l2 = reorder(otherEntry.getNodeID().toLong());
            return Long.compare(l1, l2);
        }

        @Override
        public String toString() {
            return description;
        }

        @Override
        @SuppressFBWarnings(value = "EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS",
                justification = "Purposefully attempting lookup using NodeID argument in model " +
                        "vector.")
        public boolean equals(Object o) {
            if (o instanceof NodeEntry) {
                return getNodeID().equals(((NodeEntry) o).getNodeID());
            }
            if (o instanceof NodeID) {
                return getNodeID().equals(o);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return getNodeID().hashCode();
        }

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            //log.warning("Received model entry update for " + nodeMemo.getNodeID());
            if (propertyChangeEvent.getPropertyName().equals(UPDATE_PROP_SIMPLE_NODE_IDENT)) {
                updateDescription();
            }
        }

        public void dispose() {
            //log.warning("dispose of " + nodeMemo.getNodeID().toString());
            nodeMemo.removePropertyChangeListener(this);
        }
    }

    // --------------  load lists ---------

    private void loadGroups() {
        _groupList.clear();
        for (int i = 0; i < 16; i++) {
            if (i < 1) {
                _groupList.add(new GroupList("Demo"));
            } else {
                _groupList.add(new GroupList("group " + (i + 1)));
            }
        }

        _groupTable.revalidate();
    }

    private void loadLogic() {
        _logicList.clear();
        _logicList.add(new LogicList("L001:", Operator.A, "O-E14 /* Check for block occupancy */", "I0.0"));
        _logicList.add(new LogicList("", Operator.Op, "", ""));
        _logicList.add(new LogicList("", Operator.AN, "T-Hopkins-West /* turnout closed */", "I1.0"));
        _logicList.add(new LogicList("", Operator.A, "O-Main /* block occupied */", "I0.1"));
        _logicList.add(new LogicList("", Operator.Cp, "", ""));
        _logicList.add(new LogicList("", Operator.Op, "", ""));
        _logicList.add(new LogicList("", Operator.A, "T-Hopkins-West /* turnout thrown */", "I1.0"));
        _logicList.add(new LogicList("", Operator.A, "O-Side /* block occupied */", "I0.2"));
        _logicList.add(new LogicList("", Operator.Cp, "", ""));
        _logicList.add(new LogicList("", Operator.R, "Mast /* use the stop aspect */", "Q0.0"));
        _logicList.add(new LogicList("", Operator.JU, "L009", ""));
        _logicList.add(new LogicList("L002:", null, "/* set the other aspects */", ""));
        _logicList.add(new LogicList("L009:", null, "/* Finished the signal logic */", ""));

        _logicTable.revalidate();
    }

    private void loadInputs() {
        _inputList.clear();
        for (int i = 0; i < 128; i++) {
            if (i == 0) {
                _inputList.add(new InputList("O-E14", "03.00.02.23.03.21.C1.22", "03.00.02.23.03.21.C1.21"));
            }
            else if (i == 1) {
                _inputList.add(new InputList("O-Main", "03.00.02.23.03.21.C5.22", "03.00.02.23.03.21.C5.21"));
            }
            else if (i == 2) {
                _inputList.add(new InputList("O-Side", "03.00.02.23.03.21.C6.22", "03.00.02.23.03.21.C6.21"));
            }
            else if (i < 6) {
                _inputList.add(new InputList("some user name", "00.01.02.03.04.05.06.07", "99.88.77.66.55.44.33.22"));
            } else {
                _inputList.add(new InputList("", "00.01.02.03.04.05.06.07", "99.88.77.66.55.44.33.22"));
            }
        }

        _inputTable.revalidate();
    }

    private void loadOutputs() {
        _outputList.clear();
        for (int i = 0; i < 128; i++) {
            if (i == 0) {
                _outputList.add(new OutputList("Mast", "03.00.02.23.03.30.C1.31", "03.00.02.23.03.30.C1.36"));
            }
            else if (i < 6) {
                _outputList.add(new OutputList("some user name", "00.01.02.03.04.05.06.07", "99.88.77.66.55.44.33.22"));
            } else {
                _outputList.add(new OutputList("", "00.01.02.03.04.05.06.07", "99.88.77.66.55.44.33.22"));
            }
        }

        _outputTable.revalidate();
    }

    private void loadReceivers() {
        _receiverList.clear();
        for (int i = 0; i < 16; i++) {
            if (i < 6) {
                _receiverList.add(new ReceiverList("some user name", "00.01.02.03.04.05.06.07"));
            } else {
                _receiverList.add(new ReceiverList("", "00.01.02.03.04.05.06.07"));
            }
        }

        _receiverTable.revalidate();
    }

    private void loadTransmitters() {
        _transmitterList.clear();
        for (int i = 0; i < 16; i++) {
            if (i < 6) {
                _transmitterList.add(new TransmitterList("some user name", "00.01.02.03.04.05.06.07"));
            } else {
                _transmitterList.add(new TransmitterList("", "00.01.02.03.04.05.06.07"));
            }
        }

        _transmitterTable.revalidate();
    }

    // --------------  table lists ---------

    /**
     * The name and assigned event id for a circuit transmitter.
     */
    private static class GroupList {
        String _name;

        GroupList(String name) {
            _name = name;
        }

        String getName() {
            return _name;
        }

        void setName(String newName) {
            _name = newName;
            _dirty = true;
        }
    }

    /**
     * The definition of a logic row
     */
    private static class LogicList {
        String _label;
        Operator _oper;
        String _name;
        String _variable;

        LogicList(String label, Operator oper, String name, String variable) {
            _label = label;
            _oper = oper;
            _name = name;
            _variable = variable;
        }

        String getLabel() {
            return _label;
        }

        void setLabel(String newLabel) {
            _label = newLabel;
            _dirty = true;
        }

        Operator getOper() {
            return _oper;
        }

        void setOper(Operator newOper) {
            _oper = newOper;
            _dirty = true;
        }

        String getName() {
            return _name;
        }

        void setName(String newName) {
            _name = newName;
            _dirty = true;
        }

        String getVariable() {
            return _variable;
        }

        void setVariable(String newVariable) {
            _variable = newVariable;
        }
    }

    /**
     * The name and assigned true and false events for an Input.
     */
    private static class InputList {
        String _name;
        String _eventTrue;
        String _eventFalse;

        InputList(String name, String eventTrue, String eventFalse) {
            _name = name;
            _eventTrue = eventTrue;
            _eventFalse = eventFalse;
        }

        String getName() {
            return _name;
        }

        void setName(String newName) {
            _name = newName;
            _dirty = true;
        }

        String getEventTrue() {
            return _eventTrue;
        }

        void setEventTrue(String newEventTrue) {
            _eventTrue = newEventTrue;
            _dirty = true;
        }

        String getEventFalse() {
            return _eventFalse;
        }

        void setEventFalse(String newEventFalse) {
            _eventFalse = newEventFalse;
            _dirty = true;
        }
    }

    /**
     * The name and assigned true and false events for an Output.
     */
    private static class OutputList {
        String _name;
        String _eventTrue;
        String _eventFalse;

        OutputList(String name, String eventTrue, String eventFalse) {
            _name = name;
            _eventTrue = eventTrue;
            _eventFalse = eventFalse;
        }

        String getName() {
            return _name;
        }

        void setName(String newName) {
            _name = newName;
            _dirty = true;
        }

        String getEventTrue() {
            return _eventTrue;
        }

        void setEventTrue(String newEventTrue) {
            _eventTrue = newEventTrue;
            _dirty = true;
        }

        String getEventFalse() {
            return _eventFalse;
        }

        void setEventFalse(String newEventFalse) {
            _eventFalse = newEventFalse;
            _dirty = true;
        }
    }

    /**
     * The name and assigned event id for a circuit receiver.
     */
    private static class ReceiverList {
        String _name;
        String _eventid;

        ReceiverList(String name, String eventid) {
            _name = name;
            _eventid = eventid;
        }

        String getName() {
            return _name;
        }

        void setName(String newName) {
            _name = newName;
            _dirty = true;
        }

        String getEventId() {
            return _eventid;
        }

        void setEventId(String newEventid) {
            _eventid = newEventid;
            _dirty = true;
        }
    }

    /**
     * The name and assigned event id for a circuit transmitter.
     */
    private static class TransmitterList {
        String _name;
        String _eventid;

        TransmitterList(String name, String eventid) {
            _name = name;
            _eventid = eventid;
        }

        String getName() {
            return _name;
        }

        void setName(String newName) {
            _name = newName;
            _dirty = true;
        }

        String getEventId() {
            return _eventid;
        }

        void setEventId(String newEventid) {
            _eventid = newEventid;
            _dirty = true;
        }
    }

    // --------------  table models ---------

    /**
     * TableModel for Group table entries.
     */
    class GroupModel extends AbstractTableModel {

        GroupModel() {
        }

        public static final int NAME_COLUMN = 0;

        @Override
        public int getRowCount() {
            return _groupList.size();
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Class<?> getColumnClass(int c) {
            return String.class;
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
                case NAME_COLUMN:
                    return Bundle.getMessage("ColumnName");  // NOI18N
                default:
                    return "unknown";  // NOI18N
            }
        }

        @Override
        public Object getValueAt(int r, int c) {
            switch (c) {
                case NAME_COLUMN:
                    return _groupList.get(r).getName();
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object type, int r, int c) {
            switch (c) {
                case NAME_COLUMN:
                    _groupList.get(r).setName((String) type);
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean isCellEditable(int r, int c) {
            return (c == NAME_COLUMN);
        }

        public int getPreferredWidth(int col) {
            switch (col) {
                case NAME_COLUMN:
                    return new JTextField(20).getPreferredSize().width;
                default:
                    log.warn("Unexpected column in getPreferredWidth: {}", col);  // NOI18N
                    return new JTextField(8).getPreferredSize().width;
            }
        }
    }

    /**
     * TableModel for STL table entries.
     */
    class LogicModel extends AbstractTableModel {

        LogicModel() {
        }

        public static final int LABEL_COLUMN = 0;
        public static final int OPER_COLUMN = 1;
        public static final int NAME_COLUMN = 2;
        public static final int VAR_COLUMN = 3;

        @Override
        public int getRowCount() {
            return _logicList.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Class<?> getColumnClass(int c) {
            if (c == OPER_COLUMN) return JComboBox.class;
            return String.class;
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
                case LABEL_COLUMN:
                    return Bundle.getMessage("ColumnLabel");  // NOI18N
                case OPER_COLUMN:
                    return Bundle.getMessage("ColumnOper");  // NOI18N
                case NAME_COLUMN:
                    return Bundle.getMessage("ColumnName");  // NOI18N
                case VAR_COLUMN:
                    return Bundle.getMessage("ColumnVar");  // NOI18N
                default:
                    return "unknown";  // NOI18N
            }
        }

        @Override
        public Object getValueAt(int r, int c) {
            switch (c) {
                case LABEL_COLUMN:
                    return _logicList.get(r).getLabel();
                case OPER_COLUMN:
                    return _logicList.get(r).getOper();
                case NAME_COLUMN:
                    return _logicList.get(r).getName();
                case VAR_COLUMN:
                    return _logicList.get(r).getVariable();
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object type, int r, int c) {
            switch (c) {
                case LABEL_COLUMN:
                    _logicList.get(r).setLabel((String) type);
                    break;
                case OPER_COLUMN:
                    var z = (Operator) type;
                    if (z != null) {
                        if (z.name().startsWith("z")) {
                            return;
                        }
                        if (z.name().equals("x0")) {
                            _logicList.get(r).setOper(null);
                            return;
                        }
                    }
                    _logicList.get(r).setOper((Operator) type);
                    break;
                case NAME_COLUMN:
                    _logicList.get(r).setName((String) type);
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean isCellEditable(int r, int c) {
            return ((c == LABEL_COLUMN || c == OPER_COLUMN || c == NAME_COLUMN));
        }

        public int getPreferredWidth(int col) {
            switch (col) {
                case LABEL_COLUMN:
                case VAR_COLUMN:
                    return new JTextField(6).getPreferredSize().width;
                case OPER_COLUMN:
                    return new JTextField(20).getPreferredSize().width;
                case NAME_COLUMN:
                    return new JTextField(60).getPreferredSize().width;
                default:
                    log.warn("Unexpected column in getPreferredWidth: {}", col);  // NOI18N
                    return new JTextField(8).getPreferredSize().width;
            }
        }
    }

    /**
     * TableModel for Input table entries.
     */
    class InputModel extends AbstractTableModel {

        InputModel() {
        }

        public static final int INPUT_COLUMN = 0;
        public static final int NAME_COLUMN = 1;
        public static final int TRUE_COLUMN = 2;
        public static final int FALSE_COLUMN = 3;

        @Override
        public int getRowCount() {
            return _inputList.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Class<?> getColumnClass(int c) {
            return String.class;
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
                case INPUT_COLUMN:
                    return Bundle.getMessage("ColumnInput");  // NOI18N
                case NAME_COLUMN:
                    return Bundle.getMessage("ColumnName");  // NOI18N
                case TRUE_COLUMN:
                    return Bundle.getMessage("ColumnTrue");  // NOI18N
                case FALSE_COLUMN:
                    return Bundle.getMessage("ColumnFalse");  // NOI18N
                default:
                    return "unknown";  // NOI18N
            }
        }

        @Override
        public Object getValueAt(int r, int c) {
            switch (c) {
                case INPUT_COLUMN:
                    int grp = r / 8;
                    int rem = r % 8;
                    return "I" + grp + "." + rem;
                case NAME_COLUMN:
                    return _inputList.get(r).getName();
                case TRUE_COLUMN:
                    return _inputList.get(r).getEventTrue();
                case FALSE_COLUMN:
                    return _inputList.get(r).getEventFalse();
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object type, int r, int c) {
            switch (c) {
                case NAME_COLUMN:
                    _inputList.get(r).setName((String) type);
                    break;
                case TRUE_COLUMN:
                    _inputList.get(r).setEventTrue((String) type);
                    break;
                case FALSE_COLUMN:
                    _inputList.get(r).setEventFalse((String) type);
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean isCellEditable(int r, int c) {
            return ((c == NAME_COLUMN) || (c == TRUE_COLUMN) || (c == FALSE_COLUMN));
        }

        public int getPreferredWidth(int col) {
            switch (col) {
                case INPUT_COLUMN:
                    return new JTextField(6).getPreferredSize().width;
                case NAME_COLUMN:
                    return new JTextField(50).getPreferredSize().width;
                case TRUE_COLUMN:
                    return new JTextField(20).getPreferredSize().width;
                case FALSE_COLUMN:
                    return new JTextField(20).getPreferredSize().width;
                default:
                    log.warn("Unexpected column in getPreferredWidth: {}", col);  // NOI18N
                    return new JTextField(8).getPreferredSize().width;
            }
        }
    }

    /**
     * TableModel for Output table entries.
     */
    class OutputModel extends AbstractTableModel {
        OutputModel() {
        }

        public static final int OUTPUT_COLUMN = 0;
        public static final int NAME_COLUMN = 1;
        public static final int TRUE_COLUMN = 2;
        public static final int FALSE_COLUMN = 3;

        @Override
        public int getRowCount() {
            return _outputList.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Class<?> getColumnClass(int c) {
            return String.class;
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
                case OUTPUT_COLUMN:
                    return Bundle.getMessage("ColumnInput");  // NOI18N
                case NAME_COLUMN:
                    return Bundle.getMessage("ColumnName");  // NOI18N
                case TRUE_COLUMN:
                    return Bundle.getMessage("ColumnTrue");  // NOI18N
                case FALSE_COLUMN:
                    return Bundle.getMessage("ColumnFalse");  // NOI18N
                default:
                    return "unknown";  // NOI18N
            }
        }

        @Override
        public Object getValueAt(int r, int c) {
            switch (c) {
                case OUTPUT_COLUMN:
                    int grp = r / 8;
                    int rem = r % 8;
                    return "Q" + grp + "." + rem;
                case NAME_COLUMN:
                    return _outputList.get(r).getName();
                case TRUE_COLUMN:
                    return _outputList.get(r).getEventTrue();
                case FALSE_COLUMN:
                    return _outputList.get(r).getEventFalse();
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object type, int r, int c) {
            switch (c) {
                case NAME_COLUMN:
                    _inputList.get(r).setName((String) type);
                    break;
                case TRUE_COLUMN:
                    _inputList.get(r).setEventTrue((String) type);
                    break;
                case FALSE_COLUMN:
                    _inputList.get(r).setEventFalse((String) type);
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean isCellEditable(int r, int c) {
            return ((c == NAME_COLUMN) || (c == TRUE_COLUMN) || (c == FALSE_COLUMN));
        }

        public int getPreferredWidth(int col) {
            switch (col) {
                case OUTPUT_COLUMN:
                    return new JTextField(6).getPreferredSize().width;
                case NAME_COLUMN:
                    return new JTextField(50).getPreferredSize().width;
                case TRUE_COLUMN:
                    return new JTextField(20).getPreferredSize().width;
                case FALSE_COLUMN:
                    return new JTextField(20).getPreferredSize().width;
                default:
                    log.warn("Unexpected column in getPreferredWidth: {}", col);  // NOI18N
                    return new JTextField(8).getPreferredSize().width;
            }
        }
    }

    /**
     * TableModel for circuit receiver table entries.
     */
    class ReceiverModel extends AbstractTableModel {

        ReceiverModel() {
        }

        public static final int CIRCUIT_COLUMN = 0;
        public static final int NAME_COLUMN = 1;
        public static final int EVENTID_COLUMN = 2;

        @Override
        public int getRowCount() {
            return _receiverList.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Class<?> getColumnClass(int c) {
            return String.class;
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
                case CIRCUIT_COLUMN:
                    return Bundle.getMessage("ColumnCircuit");  // NOI18N
                case NAME_COLUMN:
                    return Bundle.getMessage("ColumnName");  // NOI18N
                case EVENTID_COLUMN:
                    return Bundle.getMessage("ColumnEventID");  // NOI18N
                default:
                    return "unknown";  // NOI18N
            }
        }

        @Override
        public Object getValueAt(int r, int c) {
            switch (c) {
                case CIRCUIT_COLUMN:
                    return "Y" + r;
                case NAME_COLUMN:
                    return _receiverList.get(r).getName();
                case EVENTID_COLUMN:
                    return _receiverList.get(r).getEventId();
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object type, int r, int c) {
            switch (c) {
                case NAME_COLUMN:
                    _receiverList.get(r).setName((String) type);
                    break;
                case EVENTID_COLUMN:
                    _receiverList.get(r).setEventId((String) type);
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean isCellEditable(int r, int c) {
            return ((c == NAME_COLUMN) || (c == EVENTID_COLUMN));
        }

        public int getPreferredWidth(int col) {
            switch (col) {
                case CIRCUIT_COLUMN:
                    return new JTextField(6).getPreferredSize().width;
                case NAME_COLUMN:
                    return new JTextField(50).getPreferredSize().width;
                case EVENTID_COLUMN:
                    return new JTextField(20).getPreferredSize().width;
                default:
                    log.warn("Unexpected column in getPreferredWidth: {}", col);  // NOI18N
                    return new JTextField(8).getPreferredSize().width;
            }
        }
    }

    /**
     * TableModel for circuit transmitter table entries.
     */
    class TransmitterModel extends AbstractTableModel {

        TransmitterModel() {
        }

        public static final int CIRCUIT_COLUMN = 0;
        public static final int NAME_COLUMN = 1;
        public static final int EVENTID_COLUMN = 2;

        @Override
        public int getRowCount() {
            return _transmitterList.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Class<?> getColumnClass(int c) {
            return String.class;
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
                case CIRCUIT_COLUMN:
                    return Bundle.getMessage("ColumnCircuit");  // NOI18N
                case NAME_COLUMN:
                    return Bundle.getMessage("ColumnName");  // NOI18N
                case EVENTID_COLUMN:
                    return Bundle.getMessage("ColumnEventID");  // NOI18N
                default:
                    return "unknown";  // NOI18N
            }
        }

        @Override
        public Object getValueAt(int r, int c) {
            switch (c) {
                case CIRCUIT_COLUMN:
                    return "Z" + r;
                case NAME_COLUMN:
                    return _transmitterList.get(r).getName();
                case EVENTID_COLUMN:
                    return _transmitterList.get(r).getEventId();
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object type, int r, int c) {
            switch (c) {
                case NAME_COLUMN:
                    _transmitterList.get(r).setName((String) type);
                    break;
                case EVENTID_COLUMN:
                    _transmitterList.get(r).setEventId((String) type);
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean isCellEditable(int r, int c) {
            return ((c == NAME_COLUMN) || (c == EVENTID_COLUMN));
        }

        public int getPreferredWidth(int col) {
            switch (col) {
                case CIRCUIT_COLUMN:
                    return new JTextField(6).getPreferredSize().width;
                case NAME_COLUMN:
                    return new JTextField(50).getPreferredSize().width;
                case EVENTID_COLUMN:
                    return new JTextField(20).getPreferredSize().width;
                default:
                    log.warn("Unexpected column in getPreferredWidth: {}", col);  // NOI18N
                    return new JTextField(8).getPreferredSize().width;
            }
        }
    }

    // --------------  Operator Enum ---------

    public enum Operator {
        x0(Bundle.getMessage("Separator0")),
        z1(Bundle.getMessage("Separator1")),
        A(Bundle.getMessage("OperatorA")),
        AN(Bundle.getMessage("OperatorAN")),
        O(Bundle.getMessage("OperatorO")),
        ON(Bundle.getMessage("OperatorON")),
        X(Bundle.getMessage("OperatorX")),
        XN(Bundle.getMessage("OperatorXN")),

        z2(Bundle.getMessage("Separator2")),    // The STL parens are represented by lower case p
        Ap(Bundle.getMessage("OperatorAp")),
        ANp(Bundle.getMessage("OperatorANp")),
        Op(Bundle.getMessage("OperatorOp")),
        ONp(Bundle.getMessage("OperatorONp")),
        Xp(Bundle.getMessage("OperatorXp")),
        XNp(Bundle.getMessage("OperatorXNp")),
        Cp(Bundle.getMessage("OperatorCp")),    // Close paren

        z3(Bundle.getMessage("Separator3")),
        EQ(Bundle.getMessage("OperatorEQ")),    // = operator
        R(Bundle.getMessage("OperatorR")),
        S(Bundle.getMessage("OperatorS")),

        z4(Bundle.getMessage("Separator4")),
        NOT(Bundle.getMessage("OperatorNOT")),
        SET(Bundle.getMessage("OperatorSET")),
        CLR(Bundle.getMessage("OperatorCLR")),
        SAVE(Bundle.getMessage("OperatorSAVE")),

        z5(Bundle.getMessage("Separator5")),
        JU(Bundle.getMessage("OperatorJU")),


        z6(Bundle.getMessage("Separator6")),

        z7(Bundle.getMessage("Separator7"));


        private final String _text;

        private Operator(String text) {
            this._text = text;
        }

        @Override
        public String toString() {
            return _text;
        }

    }

    // --------------  misc items ---------

    @Override
    public void dispose() {
        // and complete this
        super.dispose();
    }

    @Override
    public String getHelpTarget() {
        return "package.jmri.jmrix.openlcb.swing.memtool.MemoryToolPane";
    }

    @Override
    public String getTitle() {
        if (_canMemo != null) {
            return (_canMemo.getUserName() + " STL Editor");
        }
        return Bundle.getMessage("TitleSTLEditor");
    }

    /**
     * Nested class to create one of these using old-style defaults
     */
    public static class Default extends jmri.jmrix.can.swing.CanNamedPaneAction {

        public Default() {
            super("STL Editor",
                    new jmri.util.swing.sdi.JmriJFrameInterface(),
                    StlEditorPane.class.getName(),
                    jmri.InstanceManager.getDefault(jmri.jmrix.can.CanSystemConnectionMemo.class));
        }
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StlEditorPane.class);
}
