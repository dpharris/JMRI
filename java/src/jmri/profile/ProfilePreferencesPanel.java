/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmri.profile;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import jmri.swing.PreferencesPanel;
import jmri.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rhwood
 */
public final class ProfilePreferencesPanel extends JPanel implements PreferencesPanel, ListSelectionListener {

    private static final Logger log = LoggerFactory.getLogger(ProfilePreferencesPanel.class);

    /**
     * Creates new form ProfilePreferencesPanel
     */
    public ProfilePreferencesPanel() {
        initComponents();
        this.chkStartWithActiveProfile.setSelected(ProfileManager.defaultManager().isAutoStartActiveProfile());
        this.valueChanged(null);
        int index = ProfileManager.defaultManager().getAllProfiles().indexOf(ProfileManager.defaultManager().getActiveProfile());
        this.profilesTbl.setRowSelectionInterval(index, index);
        // Hide until I can figure out good way to export a profile
        // Should I include items in external user/roster/etc directories?
        this.btnExportProfile.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        profilesPopupMenu = new JPopupMenu();
        renameMI = new JMenuItem();
        jSeparator1 = new JPopupMenu.Separator();
        copyMI = new JMenuItem();
        deleteMI = new JMenuItem();
        jTabbedPane1 = new JTabbedPane();
        enabledPanel = new JPanel();
        chkStartWithActiveProfile = new JCheckBox();
        jScrollPane1 = new JScrollPane();
        profilesTbl = new JTable() {
            //Implement table cell tool tips.
            public String getToolTipText(MouseEvent e) {
                try {
                    return getValueAt(rowAtPoint(e.getPoint()), -1).toString();
                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }
                return null;
            }};
            btnOpenExistingProfile = new JButton();
            btnDeleteProfile = new JButton();
            btnCreateNewProfile = new JButton();
            btnActivateProfile = new JButton();
            btnExportProfile = new JButton();
            btnCopyProfile = new JButton();
            searchPathsPanel = new JPanel();
            jScrollPane2 = new JScrollPane();
            searchPaths = new JList();
            btnRemoveSearchPath = new JButton();
            btnAddSearchPath = new JButton();

            profilesPopupMenu.addPopupMenuListener(new PopupMenuListener() {
                public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
                    profilesPopupMenuPopupMenuWillBecomeVisible(evt);
                }
                public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
                }
                public void popupMenuCanceled(PopupMenuEvent evt) {
                }
            });

            ResourceBundle bundle = ResourceBundle.getBundle("jmri/profile/Bundle"); // NOI18N
            renameMI.setText(bundle.getString("ProfilePreferencesPanel.renameMI.text")); // NOI18N
            renameMI.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    renameMIActionPerformed(evt);
                }
            });
            profilesPopupMenu.add(renameMI);
            profilesPopupMenu.add(jSeparator1);

            copyMI.setText(bundle.getString("ProfilePreferencesPanel.copyMI.text")); // NOI18N
            profilesPopupMenu.add(copyMI);

            deleteMI.setText(bundle.getString("ProfilePreferencesPanel.deleteMI.text")); // NOI18N
            profilesPopupMenu.add(deleteMI);

            chkStartWithActiveProfile.setText(bundle.getString("ProfilePreferencesPanel.chkStartWithActiveProfile.text")); // NOI18N
            chkStartWithActiveProfile.setToolTipText(bundle.getString("ProfilePreferencesPanel.chkStartWithActiveProfile.toolTipText")); // NOI18N
            chkStartWithActiveProfile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    chkStartWithActiveProfileActionPerformed(evt);
                }
            });

            profilesTbl.setModel(new ProfileTableModel());
            profilesTbl.getSelectionModel().addListSelectionListener(this);
            profilesTbl.getTableHeader().setReorderingAllowed(false);
            jScrollPane1.setViewportView(profilesTbl);

            btnOpenExistingProfile.setText(bundle.getString("ProfilePreferencesPanel.btnOpenExistingProfile.text")); // NOI18N
            btnOpenExistingProfile.setToolTipText(bundle.getString("ProfilePreferencesPanel.btnOpenExistingProfile.toolTipText")); // NOI18N
            btnOpenExistingProfile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnOpenExistingProfileActionPerformed(evt);
                }
            });

            btnDeleteProfile.setText(bundle.getString("ProfilePreferencesPanel.btnDeleteProfile.text")); // NOI18N
            btnDeleteProfile.setToolTipText(bundle.getString("ProfilePreferencesPanel.btnDeleteProfile.toolTipText")); // NOI18N
            btnDeleteProfile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnDeleteProfileActionPerformed(evt);
                }
            });

            btnCreateNewProfile.setText(bundle.getString("ProfilePreferencesPanel.btnCreateNewProfile.text")); // NOI18N
            btnCreateNewProfile.setToolTipText(bundle.getString("ProfilePreferencesPanel.btnCreateNewProfile.toolTipText")); // NOI18N
            btnCreateNewProfile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnCreateNewProfileActionPerformed(evt);
                }
            });

            btnActivateProfile.setText(bundle.getString("ProfilePreferencesPanel.btnActivateProfile.text")); // NOI18N
            btnActivateProfile.setToolTipText(bundle.getString("ProfilePreferencesPanel.btnActivateProfile.toolTipText")); // NOI18N
            btnActivateProfile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnActivateProfileActionPerformed(evt);
                }
            });

            btnExportProfile.setText(bundle.getString("ProfilePreferencesPanel.btnExportProfile.text")); // NOI18N
            btnExportProfile.setToolTipText(bundle.getString("ProfilePreferencesPanel.btnExportProfile.toolTipText")); // NOI18N
            btnExportProfile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnExportProfileActionPerformed(evt);
                }
            });

            btnCopyProfile.setText(bundle.getString("ProfilePreferencesPanel.btnCopyProfile.text")); // NOI18N
            btnCopyProfile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnCopyProfileActionPerformed(evt);
                }
            });

            GroupLayout enabledPanelLayout = new GroupLayout(enabledPanel);
            enabledPanel.setLayout(enabledPanelLayout);
            enabledPanelLayout.setHorizontalGroup(
                enabledPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(enabledPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(enabledPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1)
                        .addGroup(enabledPanelLayout.createSequentialGroup()
                            .addGroup(enabledPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(chkStartWithActiveProfile)
                                .addGroup(enabledPanelLayout.createSequentialGroup()
                                    .addComponent(btnActivateProfile)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnOpenExistingProfile)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnCreateNewProfile)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnCopyProfile)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnExportProfile)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnDeleteProfile)))
                            .addGap(0, 0, Short.MAX_VALUE)))
                    .addContainerGap())
            );
            enabledPanelLayout.setVerticalGroup(
                enabledPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(enabledPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(enabledPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(btnOpenExistingProfile)
                        .addComponent(btnCreateNewProfile)
                        .addComponent(btnActivateProfile)
                        .addComponent(btnExportProfile)
                        .addComponent(btnDeleteProfile)
                        .addComponent(btnCopyProfile))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(chkStartWithActiveProfile)
                    .addContainerGap())
            );

            jTabbedPane1.addTab(bundle.getString("ProfilePreferencesPanel.enabledPanel.TabConstraints.tabTitle"), enabledPanel); // NOI18N

            searchPaths.setModel(new SearchPathsListModel());
            searchPaths.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent evt) {
                    searchPathsValueChanged(evt);
                }
            });
            jScrollPane2.setViewportView(searchPaths);

            btnRemoveSearchPath.setText(bundle.getString("ProfilePreferencesPanel.btnRemoveSearchPath.text")); // NOI18N
            btnRemoveSearchPath.setToolTipText(bundle.getString("ProfilePreferencesPanel.btnRemoveSearchPath.toolTipText")); // NOI18N
            btnRemoveSearchPath.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnRemoveSearchPathActionPerformed(evt);
                }
            });

            btnAddSearchPath.setText(bundle.getString("ProfilePreferencesPanel.btnAddSearchPath.text")); // NOI18N
            btnAddSearchPath.setToolTipText(bundle.getString("ProfilePreferencesPanel.btnAddSearchPath.toolTipText")); // NOI18N
            btnAddSearchPath.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnAddSearchPathActionPerformed(evt);
                }
            });

            GroupLayout searchPathsPanelLayout = new GroupLayout(searchPathsPanel);
            searchPathsPanel.setLayout(searchPathsPanelLayout);
            searchPathsPanelLayout.setHorizontalGroup(
                searchPathsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(searchPathsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(searchPathsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(searchPathsPanelLayout.createSequentialGroup()
                            .addComponent(btnAddSearchPath)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnRemoveSearchPath)
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE))
                    .addContainerGap())
            );
            searchPathsPanelLayout.setVerticalGroup(
                searchPathsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(searchPathsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(searchPathsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAddSearchPath)
                        .addComponent(btnRemoveSearchPath))
                    .addContainerGap())
            );

            jTabbedPane1.addTab(bundle.getString("ProfilePreferencesPanel.searchPathsPanel.TabConstraints.tabTitle_1"), searchPathsPanel); // NOI18N

            GroupLayout layout = new GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPane1, GroupLayout.Alignment.TRAILING)
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPane1)
            );

            jTabbedPane1.getAccessibleContext().setAccessibleName(bundle.getString("ProfilePreferencesPanel.enabledPanel.TabConstraints.tabTitle")); // NOI18N
        }// </editor-fold>//GEN-END:initComponents

    private void renameMIActionPerformed(ActionEvent evt) {//GEN-FIRST:event_renameMIActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_renameMIActionPerformed

    private void profilesPopupMenuPopupMenuWillBecomeVisible(PopupMenuEvent evt) {//GEN-FIRST:event_profilesPopupMenuPopupMenuWillBecomeVisible
        if (profilesTbl.getSelectedRowCount() == 1) {
            this.renameMI.setEnabled(true);
        }
    }//GEN-LAST:event_profilesPopupMenuPopupMenuWillBecomeVisible

    private void btnAddSearchPathActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddSearchPathActionPerformed
        JFileChooser chooser = new JFileChooser(FileUtil.getPreferencesPath());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setFileFilter(new ProfileFileFilter());
        chooser.setFileView(new ProfileFileView());
        // TODO: Use NetBeans OpenDialog if its availble
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ProfileManager.defaultManager().addSearchPath(chooser.getSelectedFile());
                searchPaths.setSelectedValue(chooser.getSelectedFile(), true);
            } catch (IOException ex) {
                log.warn("Unable to write profiles while adding search path {}", chooser.getSelectedFile().getPath(), ex);
                JOptionPane.showMessageDialog(this,
                        Bundle.getMessage("ProfilePreferencesPanel.btnAddSearchPath.errorMessage", chooser.getSelectedFile().getPath(), ex.getLocalizedMessage()),
                        Bundle.getMessage("ProfilePreferencesPanel.btnAddSearchPath.errorTitle"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnAddSearchPathActionPerformed

    private void btnRemoveSearchPathActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveSearchPathActionPerformed
        File path = (File) searchPaths.getSelectedValue();
        try {
            ProfileManager.defaultManager().removeSearchPath(path);
        } catch (IOException ex) {
            log.warn("Unable to write profiles while removing search path {}", path.getPath(), ex);
            JOptionPane.showMessageDialog(this,
                    Bundle.getMessage("ProfilePreferencesPanel.btnRemoveSearchPath.errorMessage", path.getPath(), ex.getLocalizedMessage()),
                    Bundle.getMessage("ProfilePreferencesPanel.btnRemoveSearchPath.errorTitle"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnRemoveSearchPathActionPerformed

    private void searchPathsValueChanged(ListSelectionEvent evt) {//GEN-FIRST:event_searchPathsValueChanged
        if (searchPaths.getSelectedValue() == null || searchPaths.getSelectedValue().equals(new File(FileUtil.getPreferencesPath()))) {
            this.btnRemoveSearchPath.setEnabled(false);
        } else {
            this.btnRemoveSearchPath.setEnabled(true);
        }
    }//GEN-LAST:event_searchPathsValueChanged

    private void btnExportProfileActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnExportProfileActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("ZIP Archives", "zip"));
        chooser.setFileView(new ProfileFileView());
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            // TODO: make ZIP archive of Profile
        }
    }//GEN-LAST:event_btnExportProfileActionPerformed

    private void btnActivateProfileActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnActivateProfileActionPerformed
        try {
            Profile p = ProfileManager.defaultManager().getProfiles(profilesTbl.getSelectedRow());
            ProfileManager.defaultManager().setNextActiveProfile(p);
            ProfileManager.defaultManager().saveActiveProfile(p, ProfileManager.defaultManager().isAutoStartActiveProfile());
        } catch (IOException ex) {
            log.error("Unable to save profile preferences", ex);
            JOptionPane.showMessageDialog(this, "Usable to save profile preferences.\n" + ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnActivateProfileActionPerformed

    private void btnCreateNewProfileActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCreateNewProfileActionPerformed
        AddProfileDialog apd = new AddProfileDialog((Frame) SwingUtilities.getWindowAncestor(this), true, true);
        apd.setLocationRelativeTo(this);
        apd.setVisible(true);
    }//GEN-LAST:event_btnCreateNewProfileActionPerformed

    private void btnDeleteProfileActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnDeleteProfileActionPerformed
        Profile deletedProfile = ProfileManager.defaultManager().getAllProfiles().get(profilesTbl.getSelectedRow());
        int result = JOptionPane.showOptionDialog(this,
                Bundle.getMessage("ProfilePreferencesPanel.btnDeleteProfile.dlgMessage", deletedProfile.getName()), // NOI18N
                Bundle.getMessage("ProfilePreferencesPanel.btnDeleteProfile.dlgTitle", deletedProfile.getName()), // NOI18N
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, // use default icon
                new String[]{
                    Bundle.getMessage("ProfilePreferencesPanel.btnDeleteProfile.text"), // NOI18N
                    Bundle.getMessage("AddProfileDialog.btnCancel.text") // NOI18N
                },
                JOptionPane.CANCEL_OPTION
        );
        if (result == JOptionPane.OK_OPTION) {
            if (!FileUtil.delete(deletedProfile.getPath())) {
                log.warn("Unable to delete profile directory {}", deletedProfile.getPath());
                JOptionPane.showMessageDialog(this,
                        Bundle.getMessage("ProfilePreferencesPanel.btnDeleteProfile.errorMessage", deletedProfile.getPath()),
                        Bundle.getMessage("ProfilePreferencesPanel.btnDeleteProfile.errorMessage"),
                        JOptionPane.ERROR_MESSAGE);
            }
            ProfileManager.defaultManager().removeProfile(deletedProfile);
            log.info("Removed profile \"{}\" from {}", deletedProfile.getName(), deletedProfile.getPath());
            profilesTbl.repaint();
        }
    }//GEN-LAST:event_btnDeleteProfileActionPerformed

    private void btnOpenExistingProfileActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnOpenExistingProfileActionPerformed
        JFileChooser chooser = new JFileChooser(FileUtil.getPreferencesPath());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new ProfileFileFilter());
        chooser.setFileView(new ProfileFileView());
        // TODO: Use NetBeans OpenDialog if its availble
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Profile p = new Profile(chooser.getSelectedFile());
                ProfileManager.defaultManager().addProfile(p);
                int index = ProfileManager.defaultManager().getAllProfiles().indexOf(p);
                profilesTbl.setRowSelectionInterval(index, index);
            } catch (IOException ex) {
                log.warn("{} is not a profile directory", chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this,
                        Bundle.getMessage("ProfilePreferencesPanel.btnOpenExistingProfile.errorMessage", chooser.getSelectedFile().getPath()),
                        Bundle.getMessage("ProfilePreferencesPanel.btnOpenExistingProfile.errorMessage"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnOpenExistingProfileActionPerformed

    private void chkStartWithActiveProfileActionPerformed(ActionEvent evt) {//GEN-FIRST:event_chkStartWithActiveProfileActionPerformed
        ProfileManager.defaultManager().setAutoStartActiveProfile(this.chkStartWithActiveProfile.isSelected());
        try {
            ProfileManager.defaultManager().saveActiveProfile();
        } catch (IOException ex) {
            log.error("Unable to save active profile.", ex);
        }
    }//GEN-LAST:event_chkStartWithActiveProfileActionPerformed

    private void btnCopyProfileActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCopyProfileActionPerformed
        AddProfileDialog apd = new AddProfileDialog((Frame) SwingUtilities.getWindowAncestor(this), true, true);
        apd.setSourceProfile(ProfileManager.defaultManager().getAllProfiles().get(profilesTbl.getSelectedRow()));
        apd.setLocationRelativeTo(this);
        apd.setVisible(true);
    }//GEN-LAST:event_btnCopyProfileActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnActivateProfile;
    private JButton btnAddSearchPath;
    private JButton btnCopyProfile;
    private JButton btnCreateNewProfile;
    private JButton btnDeleteProfile;
    private JButton btnExportProfile;
    private JButton btnOpenExistingProfile;
    private JButton btnRemoveSearchPath;
    private JCheckBox chkStartWithActiveProfile;
    private JMenuItem copyMI;
    private JMenuItem deleteMI;
    private JPanel enabledPanel;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JPopupMenu.Separator jSeparator1;
    private JTabbedPane jTabbedPane1;
    private JPopupMenu profilesPopupMenu;
    private JTable profilesTbl;
    private JMenuItem renameMI;
    private JList searchPaths;
    private JPanel searchPathsPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getPreferencesItem() {
        return "Profiles"; // NOI18N
    }

    @Override
    public String getPreferencesItemText() {
        return Bundle.getMessage("ProfilePreferencesPanel.enabledPanel.TabConstraints.tabTitle");
    }

    @Override
    public String getTabbedPreferencesTitle() {
        return null;
    }

    @Override
    public String getLabelKey() {
        return null;
    }

    @Override
    public JComponent getPreferencesComponent() {
        return this;
    }

    @Override
    public boolean isPersistant() {
        return false;
    }

    @Override
    public String getPreferencesTooltip() {
        return null;
    }

    @Override
    public void savePreferences() {
        // Nothing to do since ProfileManager preferences are saved immediately
    }

    public void dispose() {
        ProfileManager.defaultManager().removePropertyChangeListener((ProfileTableModel) profilesTbl.getModel());
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (profilesTbl.getSelectedRow() != -1 && profilesTbl.getSelectedRow() < ProfileManager.defaultManager().getAllProfiles().size()) {
            Profile p = ProfileManager.defaultManager().getAllProfiles().get(profilesTbl.getSelectedRow());
            this.btnDeleteProfile.setEnabled(!p.equals(ProfileManager.defaultManager().getActiveProfile()));
            if (ProfileManager.defaultManager().getNextActiveProfile() != null) {
                this.btnActivateProfile.setEnabled(!p.equals(ProfileManager.defaultManager().getNextActiveProfile()));
            } else {
                this.btnActivateProfile.setEnabled(!p.equals(ProfileManager.defaultManager().getActiveProfile()));
            }
            this.btnCopyProfile.setEnabled(true);
            this.btnExportProfile.setEnabled(true);
        } else {
            this.btnDeleteProfile.setEnabled(false);
            this.btnCopyProfile.setEnabled(false);
            this.btnExportProfile.setEnabled(false);
            this.btnActivateProfile.setEnabled(false);
        }
    }

    @Override
    public boolean isDirty() {
        return false; // ProfileManager preferences are saved immediately, so this is always false
    }
    /* Comment out until I get around to utilizing this, so Jenkins does not throw warnings.
    private static class ZipFileFilter extends FileFilter {

        public ZipFileFilter() {
        }

        @Override
        public boolean accept(File f) {
            if (!f.isDirectory()) {
                int i = f.getName().lastIndexOf('.');
                if (i > 0 && i < f.getName().length() - 1) {
                    return f.getName().substring(i + 1).toLowerCase().equalsIgnoreCase("zip"); // NOI18N
                }
                return false;
            }
            return true;
        }

        @Override
        public String getDescription() {
            return "Zip archives (.zip)";
        }
    }
    */
}
