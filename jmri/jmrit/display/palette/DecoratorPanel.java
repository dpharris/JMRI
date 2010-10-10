
package jmri.jmrit.display.palette;

import java.awt.Color;
import java.awt.Font;

//import java.awt.event.ActionListener;
//import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.datatransfer.Transferable; 
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;


//import java.util.Hashtable;
//import java.util.Iterator;

import javax.swing.*;

//import jmri.jmrit.catalog.NamedIcon;
import jmri.jmrit.display.Editor;
import jmri.jmrit.display.Positionable;
import jmri.jmrit.display.PositionableLabel;
import jmri.jmrit.display.PositionablePopupUtil;

/**
*  ItemPanel for text labels
*/
public class DecoratorPanel extends JPanel implements ChangeListener, ItemListener {

    static final String[] JUSTIFICATION = {ItemPalette.rbp.getString("left"), 
                                            ItemPalette.rbp.getString("center"),
                                            ItemPalette.rbp.getString("right")};

    static final String[] STYLES = {ItemPalette.rbp.getString("plain"), 
                                    ItemPalette.rbp.getString("bold"),
                                    ItemPalette.rbp.getString("italic"),
                                    ItemPalette.rbp.getString("bold/italic")};

    static final String[] FONTSIZE = {"6", "8", "10", "11", "12", "14", "16",
                                        "20", "24", "28", "32", "36"};
    public static final int SIZE = 1;
    public static final int STYLE = 2;
    public static final int JUST = 3;

    AJComboBox _fontSizeBox;
    AJComboBox _fontStyleBox;
    AJComboBox _fontJustBox;

    JRadioButton _foregrndButton = new JRadioButton(ItemPalette.rbp.getString("fontColor"));
    JRadioButton _backgrndButton = new JRadioButton(ItemPalette.rbp.getString("backColor"));
    JRadioButton _borderButton = new JRadioButton(ItemPalette.rbp.getString("borderColor"));

    public static final int BORDER = 1;
    public static final int MARGIN = 2;
    public static final int FWIDTH = 3;
    public static final int FHEIGHT = 4;

    AJSpinner _borderSpin;
    AJSpinner _marginSpin; 
    AJSpinner _widthSpin;
    AJSpinner _heightSpin;

    JColorChooser _chooser;
    JPanel _preview;
    Positionable _item;
    PositionableLabel _sample;

    Editor _editor;
    public DecoratorPanel(Editor editor) {
        _editor = editor;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void setText(String text) {
        if (_sample!=null) {
            _sample.setText(text);
        }
    }

    static class AJComboBox extends JComboBox {
        int _which;
        AJComboBox(String[] items, int which) {
            super(items);
            _which = which;
        }
    }

    private JPanel makeBoxPanel(String caption, JComboBox box) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(ItemPalette.rbp.getString(caption)));
        box.addItemListener(this);
        panel.add(box);
        return panel;
    }

    static class AJSpinner extends JSpinner {
        int _which;

        AJSpinner(SpinnerModel model, int which) {
            super(model);
            _which = which;
        }
    }

    private JPanel makeSpinPanel(String caption, JSpinner spin) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(ItemPalette.rbp.getString(caption)));
        spin.addChangeListener(this);
        panel.add(spin);
        return panel;
    }

    public void initDecoratorPanel(Positionable pos) {

        _preview = new JPanel();
        _preview.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 1)));
        if (pos==null) {
            _sample = new DragDecoratorLabel(ItemPalette.rbp.getString("sample"), _editor);
            _sample.setSize(_sample.getPreferredSize().width, _sample.getPreferredSize().height);
            _sample.setDisplayLevel(Editor.LABELS);
            _sample.setVisible(true);
            _preview.add(_sample);
        } else {
            _item = pos.clone();
            _item.setVisible(true);
            _preview.add((JComponent)_item);
        }
        PositionablePopupUtil util = getPositionablePopupUtil();
        JPanel fontPanel = new JPanel();
        _fontSizeBox = new AJComboBox(FONTSIZE, SIZE);
        fontPanel.add(makeBoxPanel("fontSize", _fontSizeBox));
        int row = 4;
        for (int i=0; i<FONTSIZE.length; i++) {
            if (util.getFontSize()==Integer.parseInt(FONTSIZE[i])) {
                row = i;
                break;
            }
        }
        _fontSizeBox.setSelectedIndex(row);

        _fontStyleBox = new AJComboBox(STYLES, STYLE);
        fontPanel.add(makeBoxPanel("fontStyle", _fontStyleBox));
        _fontStyleBox.setSelectedIndex(util.getFont().getStyle());

        _fontJustBox = new AJComboBox(JUSTIFICATION, JUST);
        fontPanel.add(makeBoxPanel("justification", _fontJustBox));
        switch (util.getJustification()){
            case PositionablePopupUtil.LEFT:     row = 0;
                            break;
            case PositionablePopupUtil.RIGHT:    row = 2;
                            break;
            case PositionablePopupUtil.CENTRE:   row = 1;
                            break;
            default     :   row = 2;
        }
        _fontJustBox.setSelectedIndex(row);
        add(fontPanel);
        

        JPanel sizePanel = new JPanel();
        SpinnerNumberModel model = new SpinnerNumberModel(util.getBorderSize(),0,100,1);
        _borderSpin = new AJSpinner(model, BORDER);
        sizePanel.add(makeSpinPanel("borderSize", _borderSpin));
        model = new SpinnerNumberModel(util.getMargin(),0,100,1);
        _marginSpin = new AJSpinner(model, MARGIN);
        sizePanel.add(makeSpinPanel("marginSize", _marginSpin));
        model = new SpinnerNumberModel(util.getFixedWidth(),0,1000,1);
        _widthSpin = new AJSpinner(model, FWIDTH);
        sizePanel.add(makeSpinPanel("fixedWidth", _widthSpin));
        model = new SpinnerNumberModel(util.getFixedHeight(),0,1000,1);
        _heightSpin = new AJSpinner(model, FHEIGHT);
        sizePanel.add(makeSpinPanel("fixedHeight", _heightSpin));
        add(sizePanel);

        JPanel colorPanel = new JPanel();
        ButtonGroup group = new ButtonGroup();
        group.add(_foregrndButton);
        colorPanel.add(_foregrndButton);
        group.add(_backgrndButton);
        colorPanel.add(_backgrndButton);
        group.add(_borderButton);
        colorPanel.add(_borderButton);
        _foregrndButton.setSelected(true);
        add(colorPanel);

        _chooser = new JColorChooser(_editor.getTargetPanel().getBackground());
        _chooser.getSelectionModel().addChangeListener(this);
        _chooser.setPreviewPanel(_preview);
        if (_sample==null) {
            _preview.setSize(4*_item.getPreferredSize().width, 3*_item.getPreferredSize().height);
        } else {
            _preview.setSize(4*_sample.getPreferredSize().width, 3*_sample.getPreferredSize().height);
        }
        _preview.setBackground(_editor.getTargetPanel().getBackground());
        add(_chooser);

    }

    public void stateChanged(ChangeEvent e) {
        PositionablePopupUtil util = getPositionablePopupUtil();
        Object obj = e.getSource();
        if (obj instanceof AJSpinner) {
            int num = ((Number)((AJSpinner)obj).getValue()).intValue();
            switch (((AJSpinner)obj)._which) {
                case BORDER:
                    util.setBorderSize(num); 
                    break;
                case MARGIN:
                    util.setMargin(num); 
                    break;
                case FWIDTH:
                    util.setFixedWidth(num); 
                    break;
                case FHEIGHT:
                    util.setFixedHeight(num); 
                    break;
            }
        } else {
            if (_foregrndButton.isSelected()) {
                util.setForeground(_chooser.getColor());
            } else if (_backgrndButton.isSelected()) {
                util.setBackgroundColor(_chooser.getColor());
            } else if (_borderButton.isSelected()) {
                util.setBorderColor(_chooser.getColor());
            }
        }
    }

    public PositionablePopupUtil getPositionablePopupUtil() {
        PositionablePopupUtil util;
        if (_sample==null) {
            util = _item.getPopupUtility();
        } else {
            util = _sample.getPopupUtility();
        }
        return util;
    }

    public void itemStateChanged(ItemEvent e) {   
        PositionablePopupUtil util = getPositionablePopupUtil();
        Object obj = e.getSource();
        if (obj instanceof AJComboBox) {
            switch (((AJComboBox)obj)._which) {
                case SIZE:
                    String size = (String)((AJComboBox)obj).getSelectedItem();
                    util.setFontSize(Float.valueOf(size));
                    break;
                case STYLE:
                    int style = 0;
                    switch (((AJComboBox)obj).getSelectedIndex()) {
                        case 0:
                            style = Font.PLAIN;
                            break;
                        case 1:
                            style = Font.BOLD;
                            break;
                        case 2:
                            style = Font.ITALIC;
                            break;
                        case 3:
                            style = (Font.BOLD | Font.ITALIC);
                            break;
                    }
                    if (_sample==null) {
                        util.setFontStyle(style);
                    } else {
                        _sample.setFont(jmri.util.FontUtil.deriveFont(_sample.getFont(), style));
                        _sample.updateSize();
                    }
                    break;
                case JUST:
                    int just = 0;
                    switch (((AJComboBox)obj).getSelectedIndex()) {
                        case 0:
                            just = PositionablePopupUtil.LEFT;
                            break;
                        case 1:
                            just = PositionablePopupUtil.CENTRE;
                            break;
                        case 2:
                            just = PositionablePopupUtil.RIGHT;
                            break;
                    }
                    util.setJustification(just); 
                    break;
            }
        }
    }

    /**
    * Export a Positionable item from panel 
    */
    class DragDecoratorLabel extends PositionableLabel implements DragGestureListener, DragSourceListener, Transferable {    

        DataFlavor dataFlavor;

        public DragDecoratorLabel(String s, Editor editor) {
            super(s, editor);
            DragSource dragSource = DragSource.getDefaultDragSource();
            dragSource.createDefaultDragGestureRecognizer(this,
                        DnDConstants.ACTION_COPY, this);
            try {
                dataFlavor = new DataFlavor(Editor.POSITIONABLE_FLAVOR);
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
            //if (log.isDebugEnabled()) log.debug("DragJLabel ctor");
        }
        /**************** DragGestureListener ***************/
        public void dragGestureRecognized(DragGestureEvent e) {
            if (log.isDebugEnabled()) log.debug("DragPositionable.dragGestureRecognized ");
            //Transferable t = getTransferable(this);
            e.startDrag(DragSource.DefaultCopyDrop, this, this); 
        }
        /**************** DragSourceListener ************/
        public void dragDropEnd(DragSourceDropEvent e) {
            }
        public void dragEnter(DragSourceDragEvent e) {
            }
        public void dragExit(DragSourceEvent e) {
            }
        public void dragOver(DragSourceDragEvent e) {
            }
        public void dropActionChanged(DragSourceDragEvent e) {
            }
        /*************** Transferable *********************/
        public DataFlavor[] getTransferDataFlavors() {
            //if (log.isDebugEnabled()) log.debug("DragPositionable.getTransferDataFlavors ");
            return new DataFlavor[] { dataFlavor };
        }
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            //if (log.isDebugEnabled()) log.debug("DragPositionable.isDataFlavorSupported ");
            return dataFlavor.equals(flavor);
        }
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException,IOException {
            if (!isDataFlavorSupported(flavor)) {
                return null;
            }
            return _sample.clone();
        }
    }

    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DecoratorPanel.class.getName());
}
