// AudioListenerFrame.java
package jmri.jmrit.audio.swing;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import jmri.Audio;
import jmri.AudioException;
import jmri.InstanceManager;
import jmri.implementation.AbstractAudio;
import jmri.jmrit.audio.AudioListener;
import jmri.jmrit.beantable.AudioTableAction.AudioTableDataModel;

/**
 * Define a GUI to edit AudioListener objects
 *
 * <hr>
 * This file is part of JMRI.
 * <P>
 * JMRI is free software; you can redistribute it and/or modify it under the
 * terms of version 2 of the GNU General Public License as published by the Free
 * Software Foundation. See the "COPYING" file for a copy of this license.
 * <P>
 * JMRI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <P>
 *
 * @author Matthew Harris copyright (c) 2009
 * @version $Revision$
 */
public class AudioListenerFrame extends AbstractAudioFrame {

    /**
     *
     */
    private static final long serialVersionUID = 4006867664747801687L;
    JPanelVector3f position = new JPanelVector3f(Bundle.getMessage("LabelPosition"),
            Bundle.getMessage("UnitUnits"));
    JPanelVector3f velocity = new JPanelVector3f(Bundle.getMessage("LabelVelocity"),
            Bundle.getMessage("UnitU/S"));
    JLabel oriAtLabel = new JLabel(Bundle.getMessage("LabelOrientationAt"));
    JPanelVector3f oriAt = new JPanelVector3f("", Bundle.getMessage("UnitUnits"));
    JLabel oriUpLabel = new JLabel(Bundle.getMessage("LabelOrientationUp"));
    JPanelVector3f oriUp = new JPanelVector3f("", Bundle.getMessage("UnitUnits"));
    JPanelSliderf gain = new JPanelSliderf(Bundle.getMessage("LabelGain"), 0.0f, 1.0f, 5, 4);
    JSpinner metersPerUnit = new JSpinner();
    JLabel metersPerUnitLabel = new JLabel(Bundle.getMessage("UnitM/U"));

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public AudioListenerFrame(String title, AudioTableDataModel model) {
        super(title, model);
        layoutFrame();
    }

    @Override
    @SuppressWarnings("UnnecessaryBoxing")
    public void layoutFrame() {
        super.layoutFrame();
        JPanel p;

        main.add(position);
        main.add(velocity);

        p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(Bundle.getMessage("LabelOrientation")),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        p.add(oriAtLabel);
        p.add(oriAt);
        p.add(oriUpLabel);
        p.add(oriUp);
        main.add(p);

        main.add(gain);

        p = new JPanel();
        p.setLayout(new FlowLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(Bundle.getMessage("LabelMetersPerUnit")),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        metersPerUnit.setPreferredSize(new JTextField(8).getPreferredSize());
        metersPerUnit.setModel(
                new SpinnerNumberModel(new Float(0f), new Float(0f), new Float(65536f), new Float(0.0001f)));
        metersPerUnit.setEditor(new JSpinner.NumberEditor(metersPerUnit, "0.0000"));
        p.add(metersPerUnit);
        p.add(metersPerUnitLabel);
        main.add(p);

        p = new JPanel();
        JButton apply;
        p.add(apply = new JButton(rb.getString("ButtonApply")));
        apply.addActionListener((ActionEvent e) -> {
            applyPressed(e);
        });
        JButton ok;
        p.add(ok = new JButton(rb.getString("ButtonOK")));
        ok.addActionListener((ActionEvent e) -> {
            applyPressed(e);
            frame.dispose();
        });
        JButton cancel;
        p.add(cancel = new JButton(rb.getString("ButtonCancel")));
        cancel.addActionListener((ActionEvent e) -> {
            frame.dispose();
        });
        frame.getContentPane().add(p);
    }

    @Override
    public void resetFrame() {
        // Not required
    }

    /**
     * Method to populate the Edit Listener frame with current values
     */
    @Override
    public void populateFrame(Audio a) {
        super.populateFrame(a);
        AudioListener l = (AudioListener) a;
        position.setValue(l.getPosition());
        velocity.setValue(l.getVelocity());
        oriAt.setValue(l.getOrientation(Audio.AT));
        oriUp.setValue(l.getOrientation(Audio.UP));
        gain.setValue(l.getGain());
        metersPerUnit.setValue(l.getMetersPerUnit());
    }

    void applyPressed(ActionEvent e) {
        String user = userName.getText();
        if (user.equals("")) {
            user = null;
        }
        String sName = sysName.getText().toUpperCase();
        AudioListener l;
        try {
            l = (AudioListener) InstanceManager.audioManagerInstance().provideAudio(sName);
            l.setUserName(user);
            l.setPosition(position.getValue());
            l.setVelocity(velocity.getValue());
            l.setOrientation(oriAt.getValue(), oriUp.getValue());
            l.setGain(gain.getValue());
            l.setMetersPerUnit(AbstractAudio.roundDecimal((Float) metersPerUnit.getValue(), 4d));

            // Notify changes
            model.fireTableDataChanged();
        } catch (AudioException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), rb.getString("AudioCreateErrorTitle"), JOptionPane.ERROR_MESSAGE);
        }
    }

    //private static final Logger log = LoggerFactory.getLogger(AudioListenerFrame.class.getName());
}

/* @(#)AudioListenerFrame.java */
