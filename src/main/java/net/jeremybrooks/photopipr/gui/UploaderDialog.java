/*
 *  PhotoPipr is Copyright 2017-2022 by Jeremy Brooks
 *
 *  This file is part of PhotoPipr.
 *
 *   PhotoPipr is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   PhotoPipr is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with PhotoPipr.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Created by JFormDesigner on Sat Mar 28 19:14:07 PDT 2020
 */

package net.jeremybrooks.photopipr.gui;

import net.jeremybrooks.jinx.JinxConstants;
import net.jeremybrooks.jinx.response.groups.Groups;
import net.jeremybrooks.photopipr.PPConstants;
import net.jeremybrooks.photopipr.model.GroupRule;
import net.jeremybrooks.photopipr.model.UploadAction;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serial;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static net.jeremybrooks.photopipr.PPConstants.UPLOAD_DONE_ACTION_DELETE;
import static net.jeremybrooks.photopipr.PPConstants.UPLOAD_DONE_ACTION_MOVE;

/**
 * @author Jeremy Brooks
 */
public class UploaderDialog extends JDialog {
    @Serial
    private static final long serialVersionUID = 4023566266781050434L;
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.uploader");
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("");

    private final DefaultListModel<GroupRule> groupRuleListModel = new DefaultListModel<>();

    private final WorkflowWindow workflowWindow;
    private final UploadAction uploadAction;

    private final GroupListModel availableGroupsModel;
    private final GroupListModel selectedGroupsModel = new GroupListModel();

    public UploaderDialog(WorkflowWindow owner, UploadAction uploadAction, List<Groups.Group> groups) {
        super(owner, true);
        this.workflowWindow = owner;
        this.uploadAction = uploadAction;
        initComponents();

        txtSource.setText(uploadAction.getSourcePath());
        spinnerQuantity.setValue(uploadAction.getQuantity());
        spinnerInterval.setValue(uploadAction.getInterval());
        cmbSelectionOrder.setSelectedIndex(uploadAction.getSelectionOrderIndex());
        cbxPrivate.setSelected(uploadAction.isMakePrivate());
        switch (uploadAction.getSafetyLevel()) {
            case "moderate" -> radioModerate.setSelected(true);
            case "restricted" -> radioRestricted.setSelected(true);
            default -> radioSafe.setSelected(true);
        }
        lstGroupRules.setModel(groupRuleListModel);
        lstGroupRules.setCellRenderer(new GroupRuleListCellRenderer());
        groupRuleListModel.addAll(uploadAction.getGroupRules());

        if (groups == null) {
            availableGroupsModel = new GroupListModel();
        } else {
            availableGroupsModel = new GroupListModel(groups);
        }
        lstGroups.setModel(availableGroupsModel);
        lstGroups.setCellRenderer(new GroupListCellRenderer());

        lstSelectedGroups.setModel(selectedGroupsModel);
        lstSelectedGroups.setCellRenderer(new GroupListCellRenderer());

        radioDelete.setSelected(uploadAction.getPostUploadAction().equals(UPLOAD_DONE_ACTION_DELETE));
        radioMove.setSelected(uploadAction.getPostUploadAction().equals(UPLOAD_DONE_ACTION_MOVE));
        txtMoveTo.setText(uploadAction.getMovePath());
        cbxCreateFolders.setSelected(uploadAction.isCreateFolders());
        txtDateFormat.setText(uploadAction.getDateFormat());

        updateSampleDateFormat();
        updateMoveFolderSelection();
    }

    private void radioMoveActionPerformed() {
        updateMoveFolderSelection();
    }

    private void radioDeleteActionPerformed() {
        updateMoveFolderSelection();
    }

    private void cbxUseDateActionPerformed() {
        updateDateFolderComponents();
    }

    private void updateMoveFolderSelection() {
        boolean enabled = radioMove.isSelected();
        txtMoveTo.setEnabled(enabled);
        btnBrowseMove.setEnabled(enabled);
        cbxCreateFolders.setEnabled(enabled);
        updateDateFolderComponents();
    }

    private void updateDateFolderComponents() {
        boolean enabled = cbxCreateFolders.isSelected() && radioMove.isSelected();
        txtDateFormat.setEnabled(enabled);
        lblDateFormat.setEnabled(enabled);
        lblDateSample.setEnabled(enabled);
        btnDefaultDate.setEnabled(enabled);
        lblFolderNameSample.setEnabled(enabled);
    }

    private void btnCancelActionPerformed() {
        int confirm = JOptionPane.showConfirmDialog(this,
                resourceBundle.getString("UploaderDialog.confirmExit.message"),
                resourceBundle.getString("UploaderDialog.confirmExit.title"),
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            setVisible(false);
            dispose();
        }
    }

    private void updateSampleDateFormat() {
        simpleDateFormat.applyPattern(txtDateFormat.getText().trim());
        lblDateSample.setText(simpleDateFormat.format(new Date()));
    }

    private void btnDefaultDateActionPerformed() {
        txtDateFormat.setText("yyyy-MM-dd");
        updateSampleDateFormat();
    }

    private void txtDateFormatKeyTyped() {
        updateSampleDateFormat();
    }

    private void btnSaveActionPerformed() {
        if (validateInput()) {
            uploadAction.setSourcePath(txtSource.getText().trim());
            uploadAction.setQuantity(((SpinnerNumberModel) spinnerQuantity.getModel()).getNumber().intValue());
            uploadAction.setInterval(((SpinnerNumberModel) spinnerInterval.getModel()).getNumber().intValue());
            uploadAction.setSelectionOrderIndex(cmbSelectionOrder.getSelectedIndex());
            uploadAction.setMakePrivate(cbxPrivate.isSelected());

            uploadAction.getGroupRules().clear();
            groupRuleListModel.elements().asIterator()
                    .forEachRemaining(groupRule -> uploadAction.getGroupRules().add(groupRule));

            if (radioDelete.isSelected()) {
                uploadAction.setPostUploadAction(UPLOAD_DONE_ACTION_DELETE);
            } else {
                uploadAction.setPostUploadAction(UPLOAD_DONE_ACTION_MOVE);
            }
            uploadAction.setMovePath(txtMoveTo.getText().trim());
            uploadAction.setCreateFolders(cbxCreateFolders.isSelected());
            uploadAction.setDateFormat(txtDateFormat.getText().trim());

            if (radioModerate.isSelected()) {
                uploadAction.setSafetyLevel(JinxConstants.SafetyLevel.moderate.name());
            } else if (radioRestricted.isSelected()) {
                uploadAction.setSafetyLevel(JinxConstants.SafetyLevel.restricted.name());
            } else {
                uploadAction.setSafetyLevel(JinxConstants.SafetyLevel.safe.name());
            }

            List<GroupRule> rules = uploadAction.getGroupRules();
            rules.clear();
            groupRuleListModel.elements().asIterator().forEachRemaining(rules::add);

            workflowWindow.saveWorkflows();
            setVisible(false);
            dispose();
        }
    }

    private boolean validateInput() {
        // must have a valid source directory
        if (txtSource.getText().isEmpty() ||
                !(new File(txtSource.getText()).exists())) {
            JOptionPane.showMessageDialog(this,
                    resourceBundle.getString("UploaderDialog.validationSourceDir.message"),
                    resourceBundle.getString("UploaderDialog.validation.title"),
                    JOptionPane.ERROR_MESSAGE);
            tabbedPane1.setSelectedIndex(0);
            btnBrowse.requestFocus();
            return false;
        }

        // if "move" is set, must have a valid directory
        if (txtMoveTo.getText().equals(UPLOAD_DONE_ACTION_MOVE) &&
                !new File(txtMoveTo.getText()).exists()) {
            JOptionPane.showMessageDialog(this,
                    resourceBundle.getString("UploaderDialog.validationMoveDir.message"),
                    resourceBundle.getString("UploaderDialog.validation.title"),
                    JOptionPane.ERROR_MESSAGE);
            tabbedPane1.setSelectedIndex(1);
            btnBrowseMove.requestFocus();
            return false;
        }

        return true;
    }

    private void btnBrowseActionPerformed() {
        JFileChooser jFileChooser = new JFileChooser();
        if (txtSource.getText().isBlank()) {
            jFileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        } else {
            jFileChooser.setCurrentDirectory(new File(txtSource.getText()));
        }
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.setDialogTitle(resourceBundle.getString("UploaderDialog.selectSourceDialog.title"));
        if (jFileChooser.showDialog(this,
                resourceBundle.getString("UploaderDialog.selectDirectoryDialog.buttonText")) == JFileChooser.APPROVE_OPTION) {
            txtSource.setText(jFileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void btnBrowseMoveActionPerformed() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (txtMoveTo.getText().isBlank()) {
            jFileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        } else {
            jFileChooser.setCurrentDirectory(new File(txtMoveTo.getText()));
        }
        jFileChooser.setDialogTitle(resourceBundle.getString("UploaderDialog.selectMoveDialog.title"));
        int choice = jFileChooser.showDialog(this,
                resourceBundle.getString("UploaderDialog.selectDirectoryDialog.buttonText"));
        if (choice == JFileChooser.APPROVE_OPTION) {
            File f = jFileChooser.getSelectedFile();
            if (f.exists() && f.isDirectory()) {
              txtMoveTo.setText(f.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(this,
                        resourceBundle.getString("UploaderDialog.invalidLocation.message") + "\n" + f.getAbsolutePath(),
                        resourceBundle.getString("UploaderDialog.invalidLocation.title"),
                        JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    private void btnAddRule() {
        if (txtTags.getText().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    resourceBundle.getString("UploaderDialog.missingTags.message"),
                    resourceBundle.getString("UploaderDialog.missingTags.title"),
                    JOptionPane.ERROR_MESSAGE);
        } else if (selectedGroupsModel.getSize() == 0) {
            JOptionPane.showMessageDialog(this,
                    resourceBundle.getString("UploaderDialog.missingGroups.message"),
                    resourceBundle.getString("UploaderDialog.missingGroups.title"),
                    JOptionPane.ERROR_MESSAGE);
        } else {
            GroupRule rule = new GroupRule();
            if (radioAllTags.isSelected()) {
                rule.setTagMode(PPConstants.tagMode.ALL.name());
            } else {
                rule.setTagMode(PPConstants.tagMode.ANY.name());
            }
            rule.setTags(Arrays.stream(txtTags.getText().split(","))
                    .map(String::trim).toList());
            for (int i = 0; i < selectedGroupsModel.getSize(); i++) {
                Groups.Group group = selectedGroupsModel.getElementAt(i);
                rule.addGroup(group.getGroupId(), group.getName());
            }
            groupRuleListModel.addElement(rule);

            // reset the rule creation GUI
            radioAllTags.setSelected(true);
            txtGroupFilter.setText("");
            txtTags.setText("");
            availableGroupsModel.setFilterText("");
            selectedGroupsModel.clear();
        }

    }

    private void txtGroupFilterKeyReleased() {
        availableGroupsModel.setFilterText(txtGroupFilter.getText());
    }

    private void btnAddGroupToList() {
        selectedGroupsModel.addItems(lstGroups.getSelectedValuesList());
    }

    private void btnRemoveGroupFromList() {
        selectedGroupsModel.removeItems(lstSelectedGroups.getSelectedValuesList());
    }

    private void lstGroupsMouseClicked(MouseEvent e) {
       if (e.getClickCount() == 2) {
           selectedGroupsModel.addItem(lstGroups.getSelectedValue());
       }
    }

    private void lstSelectedGroupsMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            selectedGroupsModel.removeItem(lstSelectedGroups.getSelectedValue());
        }
    }

    private void btnDeleteRule() {
        int index = lstGroupRules.getSelectedIndex();
        if (index > -1) {
            groupRuleListModel.remove(index);
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        ResourceBundle bundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.uploader");
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        tabbedPane1 = new JTabbedPane();
        pnlBasic = new JPanel();
        label2 = new JLabel();
        txtSource = new JTextField();
        btnBrowse = new JButton();
        label3 = new JLabel();
        spinnerQuantity = new JSpinner();
        lblCriteria = new JLabel();
        cmbSelectionOrder = new JComboBox<>();
        // ORDER MATTERS HERE
        // DO NOT REORDER
        cmbSelectionOrder.setModel(new DefaultComboBoxModel<>(new String[] {
                      bundle.getString("UploaderDialog.cmbSelectionOrder.random"),
        bundle.getString("UploaderDialog.cmbSelectionOrder.newest"),
        bundle.getString("UploaderDialog.cmbSelectionOrder.oldest"),
        bundle.getString("UploaderDialog.cmbSelectionOrder.asc"),
        bundle.getString("UploaderDialog.cmbSelectionOrder.desc")
                    }));
        lblInterval = new JLabel();
        spinnerInterval = new JSpinner();
        cbxPrivate = new JCheckBox();
        label1 = new JLabel();
        panel6 = new JPanel();
        radioSafe = new JRadioButton();
        radioModerate = new JRadioButton();
        radioRestricted = new JRadioButton();
        panel1 = new JPanel();
        label7 = new JLabel();
        txtTags = new JTextField();
        panel9 = new JPanel();
        radioAllTags = new JRadioButton();
        radioAnyTags = new JRadioButton();
        panel7 = new JPanel();
        label6 = new JLabel();
        txtGroupFilter = new JTextField();
        scrollPane3 = new JScrollPane();
        lstGroups = new JList<>();
        panel8 = new JPanel();
        label4 = new JLabel();
        label5 = new JLabel();
        btnAddGroupToList = new JButton();
        btnRemoveGroupFromList = new JButton();
        scrollPane4 = new JScrollPane();
        lstSelectedGroups = new JList<>();
        panel3 = new JPanel();
        btnAddRule = new JButton();
        panel4 = new JPanel();
        scrollPane1 = new JScrollPane();
        lstGroupRules = new JList<>();
        panel5 = new JPanel();
        btnDeleteRule = new JButton();
        pnlArchive = new JPanel();
        radioMove = new JRadioButton();
        txtMoveTo = new JTextField();
        btnBrowseMove = new JButton();
        cbxCreateFolders = new JCheckBox();
        panel2 = new JPanel();
        lblDateFormat = new JLabel();
        txtDateFormat = new JTextField();
        lblFolderNameSample = new JLabel();
        lblDateSample = new JLabel();
        btnDefaultDate = new JButton();
        radioDelete = new JRadioButton();
        buttonBar = new JPanel();
        btnCancel = new JButton();
        btnSave = new JButton();

        //======== this ========
        setTitle(bundle.getString("UploaderDialog.this.title"));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setModal(true);
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BorderLayout());

                //======== tabbedPane1 ========
                {

                    //======== pnlBasic ========
                    {
                        pnlBasic.setLayout(new GridBagLayout());
                        ((GridBagLayout)pnlBasic.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                        ((GridBagLayout)pnlBasic.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
                        ((GridBagLayout)pnlBasic.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)pnlBasic.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                        //---- label2 ----
                        label2.setText(bundle.getString("UploaderDialog.label2.text"));
                        pnlBasic.add(label2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 5, 5, 5), 0, 0));

                        //---- txtSource ----
                        txtSource.setEditable(false);
                        pnlBasic.add(txtSource, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 5, 5, 5), 0, 0));

                        //---- btnBrowse ----
                        btnBrowse.setText(bundle.getString("UploaderDialog.btnBrowse.text"));
                        btnBrowse.addActionListener(e -> btnBrowseActionPerformed());
                        pnlBasic.add(btnBrowse, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 5, 5, 0), 0, 0));

                        //---- label3 ----
                        label3.setText(bundle.getString("UploaderDialog.label3.text"));
                        pnlBasic.add(label3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 5, 5, 5), 0, 0));

                        //---- spinnerQuantity ----
                        spinnerQuantity.setModel(new SpinnerNumberModel(1, 1, null, 1));
                        pnlBasic.add(spinnerQuantity, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 5, 5, 5), 0, 0));

                        //---- lblCriteria ----
                        lblCriteria.setText(bundle.getString("UploaderDialog.lblCriteria.text"));
                        pnlBasic.add(lblCriteria, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 5, 5, 5), 0, 0));
                        pnlBasic.add(cmbSelectionOrder, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                        //---- lblInterval ----
                        lblInterval.setText(bundle.getString("UploaderDialog.lblInterval.text"));
                        pnlBasic.add(lblInterval, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 5, 5, 5), 0, 0));

                        //---- spinnerInterval ----
                        spinnerInterval.setModel(new SpinnerNumberModel(1, 0, null, 1));
                        pnlBasic.add(spinnerInterval, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 5, 5, 5), 0, 0));

                        //---- cbxPrivate ----
                        cbxPrivate.setText(bundle.getString("UploaderDialog.cbxPrivate.text"));
                        pnlBasic.add(cbxPrivate, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(3, 3, 8, 5), 0, 0));

                        //---- label1 ----
                        label1.setText(bundle.getString("UploaderDialog.label1.text"));
                        pnlBasic.add(label1, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                            GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 5, 0, 5), 0, 0));

                        //======== panel6 ========
                        {
                            panel6.setLayout(new GridLayout(3, 0));

                            //---- radioSafe ----
                            radioSafe.setText(bundle.getString("UploaderDialog.radioSafe.text"));
                            panel6.add(radioSafe);

                            //---- radioModerate ----
                            radioModerate.setText(bundle.getString("UploaderDialog.radioModerate.text"));
                            panel6.add(radioModerate);

                            //---- radioRestricted ----
                            radioRestricted.setText(bundle.getString("UploaderDialog.radioRestricted.text"));
                            panel6.add(radioRestricted);
                        }
                        pnlBasic.add(panel6, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(3, 3, 3, 8), 0, 0));
                    }
                    tabbedPane1.addTab(bundle.getString("UploaderDialog.pnlBasic.tab.title"), pnlBasic);

                    //======== panel1 ========
                    {
                        panel1.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0};
                        ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
                        ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                        //---- label7 ----
                        label7.setText(bundle.getString("UploaderDialog.label7.text"));
                        panel1.add(label7, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));
                        panel1.add(txtTags, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== panel9 ========
                        {
                            panel9.setLayout(new GridLayout(2, 0));

                            //---- radioAllTags ----
                            radioAllTags.setText(bundle.getString("UploaderDialog.radioAllTags.text"));
                            radioAllTags.setSelected(true);
                            panel9.add(radioAllTags);

                            //---- radioAnyTags ----
                            radioAnyTags.setText(bundle.getString("UploaderDialog.radioAnyTags.text"));
                            panel9.add(radioAnyTags);
                        }
                        panel1.add(panel9, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                        //======== panel7 ========
                        {
                            panel7.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel7.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                            ((GridBagLayout)panel7.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                            ((GridBagLayout)panel7.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
                            ((GridBagLayout)panel7.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

                            //---- label6 ----
                            label6.setText(bundle.getString("UploaderDialog.label6.text"));
                            panel7.add(label6, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));

                            //---- txtGroupFilter ----
                            txtGroupFilter.addKeyListener(new KeyAdapter() {
                                @Override
                                public void keyReleased(KeyEvent e) {
                                    txtGroupFilterKeyReleased();
                                }
                            });
                            panel7.add(txtGroupFilter, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));

                            //======== scrollPane3 ========
                            {

                                //---- lstGroups ----
                                lstGroups.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        lstGroupsMouseClicked(e);
                                    }
                                });
                                scrollPane3.setViewportView(lstGroups);
                            }
                            panel7.add(scrollPane3, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));

                            //======== panel8 ========
                            {
                                panel8.setLayout(new GridLayout(6, 0));

                                //---- label4 ----
                                label4.setText(bundle.getString("UploaderDialog.label4.text"));
                                panel8.add(label4);

                                //---- label5 ----
                                label5.setText(bundle.getString("UploaderDialog.label5.text"));
                                panel8.add(label5);

                                //---- btnAddGroupToList ----
                                btnAddGroupToList.setText(bundle.getString("UploaderDialog.btnAddGroupToList.text"));
                                btnAddGroupToList.addActionListener(e -> btnAddGroupToList());
                                panel8.add(btnAddGroupToList);

                                //---- btnRemoveGroupFromList ----
                                btnRemoveGroupFromList.setText(bundle.getString("UploaderDialog.btnRemoveGroupFromList.text"));
                                btnRemoveGroupFromList.addActionListener(e -> btnRemoveGroupFromList());
                                panel8.add(btnRemoveGroupFromList);
                            }
                            panel7.add(panel8, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));

                            //======== scrollPane4 ========
                            {

                                //---- lstSelectedGroups ----
                                lstSelectedGroups.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        lstSelectedGroupsMouseClicked(e);
                                    }
                                });
                                scrollPane4.setViewportView(lstSelectedGroups);
                            }
                            panel7.add(scrollPane4, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        panel1.add(panel7, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== panel3 ========
                        {
                            panel3.setLayout(new FlowLayout(FlowLayout.RIGHT));

                            //---- btnAddRule ----
                            btnAddRule.setText(bundle.getString("UploaderDialog.btnAddRule.text"));
                            btnAddRule.addActionListener(e -> btnAddRule());
                            panel3.add(btnAddRule);
                        }
                        panel1.add(panel3, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== panel4 ========
                        {
                            panel4.setBorder(new TitledBorder(bundle.getString("UploaderDialog.panel4.border")));
                            panel4.setLayout(new BorderLayout());

                            //======== scrollPane1 ========
                            {

                                //---- lstGroupRules ----
                                lstGroupRules.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                                lstGroupRules.setVisibleRowCount(4);
                                scrollPane1.setViewportView(lstGroupRules);
                            }
                            panel4.add(scrollPane1, BorderLayout.CENTER);

                            //======== panel5 ========
                            {
                                panel5.setLayout(new FlowLayout(FlowLayout.RIGHT));

                                //---- btnDeleteRule ----
                                btnDeleteRule.setText(bundle.getString("UploaderDialog.btnDeleteRule.text"));
                                btnDeleteRule.addActionListener(e -> btnDeleteRule());
                                panel5.add(btnDeleteRule);
                            }
                            panel4.add(panel5, BorderLayout.SOUTH);
                        }
                        panel1.add(panel4, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    tabbedPane1.addTab(bundle.getString("UploaderDialog.panel1.tab.title"), panel1);

                    //======== pnlArchive ========
                    {
                        pnlArchive.setLayout(new GridBagLayout());
                        ((GridBagLayout)pnlArchive.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                        ((GridBagLayout)pnlArchive.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
                        ((GridBagLayout)pnlArchive.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)pnlArchive.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                        //---- radioMove ----
                        radioMove.setText(bundle.getString("UploaderDialog.radioMove.text"));
                        radioMove.setSelected(true);
                        radioMove.addActionListener(e -> radioMoveActionPerformed());
                        pnlArchive.add(radioMove, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                        //---- txtMoveTo ----
                        txtMoveTo.setEditable(false);
                        pnlArchive.add(txtMoveTo, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                        //---- btnBrowseMove ----
                        btnBrowseMove.setText(bundle.getString("UploaderDialog.btnBrowseMove.text"));
                        btnBrowseMove.addActionListener(e -> btnBrowseMoveActionPerformed());
                        pnlArchive.add(btnBrowseMove, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- cbxCreateFolders ----
                        cbxCreateFolders.setText(bundle.getString("UploaderDialog.cbxCreateFolders.text"));
                        cbxCreateFolders.addActionListener(e -> cbxUseDateActionPerformed());
                        pnlArchive.add(cbxCreateFolders, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                        //======== panel2 ========
                        {
                            panel2.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                            ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                            ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
                            ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

                            //---- lblDateFormat ----
                            lblDateFormat.setText(bundle.getString("UploaderDialog.lblDateFormat.text"));
                            panel2.add(lblDateFormat, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 5), 0, 0));

                            //---- txtDateFormat ----
                            txtDateFormat.setText(bundle.getString("UploaderDialog.txtDateFormat.text"));
                            txtDateFormat.addKeyListener(new KeyAdapter() {
                                @Override
                                public void keyTyped(KeyEvent e) {
                                    txtDateFormatKeyTyped();
                                }
                            });
                            panel2.add(txtDateFormat, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //---- lblFolderNameSample ----
                            lblFolderNameSample.setText(bundle.getString("UploaderDialog.lblFolderNameSample.text"));
                            panel2.add(lblFolderNameSample, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 5), 0, 0));

                            //---- lblDateSample ----
                            lblDateSample.setText(bundle.getString("UploaderDialog.lblDateSample.text"));
                            panel2.add(lblDateSample, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 5, 5, 0), 0, 0));

                            //---- btnDefaultDate ----
                            btnDefaultDate.setText(bundle.getString("UploaderDialog.btnDefaultDate.text"));
                            btnDefaultDate.addActionListener(e -> btnDefaultDateActionPerformed());
                            panel2.add(btnDefaultDate, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));
                        }
                        pnlArchive.add(panel2, new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- radioDelete ----
                        radioDelete.setText(bundle.getString("UploaderDialog.radioDelete.text"));
                        radioDelete.addActionListener(e -> radioDeleteActionPerformed());
                        pnlArchive.add(radioDelete, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    tabbedPane1.addTab(bundle.getString("UploaderDialog.pnlArchive.tab.title"), pnlArchive);
                }
                contentPanel.add(tabbedPane1, BorderLayout.NORTH);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

                //---- btnCancel ----
                btnCancel.setText(bundle.getString("UploaderDialog.btnCancel.text"));
                btnCancel.addActionListener(e -> btnCancelActionPerformed());
                buttonBar.add(btnCancel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- btnSave ----
                btnSave.setText(bundle.getString("UploaderDialog.btnSave.text"));
                btnSave.addActionListener(e -> btnSaveActionPerformed());
                buttonBar.add(btnSave, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(605, 650);
        setLocationRelativeTo(getOwner());

        //---- buttonGroup2 ----
        var buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(radioSafe);
        buttonGroup2.add(radioModerate);
        buttonGroup2.add(radioRestricted);

        //---- buttonGroup3 ----
        var buttonGroup3 = new ButtonGroup();
        buttonGroup3.add(radioAllTags);
        buttonGroup3.add(radioAnyTags);

        //---- buttonGroup1 ----
        var buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(radioMove);
        buttonGroup1.add(radioDelete);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JTabbedPane tabbedPane1;
    private JPanel pnlBasic;
    private JLabel label2;
    private JTextField txtSource;
    private JButton btnBrowse;
    private JLabel label3;
    private JSpinner spinnerQuantity;
    private JLabel lblCriteria;
    private JComboBox<String> cmbSelectionOrder;
    private JLabel lblInterval;
    private JSpinner spinnerInterval;
    private JCheckBox cbxPrivate;
    private JLabel label1;
    private JPanel panel6;
    private JRadioButton radioSafe;
    private JRadioButton radioModerate;
    private JRadioButton radioRestricted;
    private JPanel panel1;
    private JLabel label7;
    private JTextField txtTags;
    private JPanel panel9;
    private JRadioButton radioAllTags;
    private JRadioButton radioAnyTags;
    private JPanel panel7;
    private JLabel label6;
    private JTextField txtGroupFilter;
    private JScrollPane scrollPane3;
    private JList<Groups.Group> lstGroups;
    private JPanel panel8;
    private JLabel label4;
    private JLabel label5;
    private JButton btnAddGroupToList;
    private JButton btnRemoveGroupFromList;
    private JScrollPane scrollPane4;
    private JList<Groups.Group> lstSelectedGroups;
    private JPanel panel3;
    private JButton btnAddRule;
    private JPanel panel4;
    private JScrollPane scrollPane1;
    private JList<GroupRule> lstGroupRules;
    private JPanel panel5;
    private JButton btnDeleteRule;
    private JPanel pnlArchive;
    private JRadioButton radioMove;
    private JTextField txtMoveTo;
    private JButton btnBrowseMove;
    private JCheckBox cbxCreateFolders;
    private JPanel panel2;
    private JLabel lblDateFormat;
    private JTextField txtDateFormat;
    private JLabel lblFolderNameSample;
    private JLabel lblDateSample;
    private JButton btnDefaultDate;
    private JRadioButton radioDelete;
    private JPanel buttonBar;
    private JButton btnCancel;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
