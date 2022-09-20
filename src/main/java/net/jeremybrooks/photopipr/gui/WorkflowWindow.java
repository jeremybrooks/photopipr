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
 * Created by JFormDesigner on Wed May 13 21:11:17 PDT 2020
 */

package net.jeremybrooks.photopipr.gui;

import net.jeremybrooks.jinx.response.groups.Groups;
import net.jeremybrooks.photopipr.ConfigurationManager;
import net.jeremybrooks.photopipr.JinxFactory;
import net.jeremybrooks.photopipr.model.Action;
import net.jeremybrooks.photopipr.model.Configuration;
import net.jeremybrooks.photopipr.model.FinishAction;
import net.jeremybrooks.photopipr.model.TimerAction;
import net.jeremybrooks.photopipr.model.UploadAction;
import net.jeremybrooks.photopipr.model.Workflow;
import net.jeremybrooks.photopipr.worker.WorkflowRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.desktop.QuitResponse;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

/**
 * @author Jeremy Brooks
 */
public class WorkflowWindow extends JFrame {
    private static final Logger logger = LogManager.getLogger();
    @Serial
    private static final long serialVersionUID = -7390201523601312376L;
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.workflow");
    private final DefaultComboBoxModel<Workflow> workflowComboBoxModel = new DefaultComboBoxModel<>();
    private final DefaultListModel<Action> actionListModel = new DefaultListModel<>();
    private List<Groups.Group> groups;
    private static WorkflowWindow workflowWindow;
    private boolean busy;

    private final DisabledGlassPane disabledGlassPane = new DisabledGlassPane();

    private final Comparator<Workflow> workflowComparator = Comparator.comparing(workflow -> workflow.getName().toLowerCase());

    public WorkflowWindow(List<Workflow> workflows) {
        try {
            initComponents();
            SwingUtilities.getRootPane(this).setGlassPane(disabledGlassPane);

            setTitle(String.format("%s : %s",
                    Optional.ofNullable(WorkflowWindow.class.getPackage().getImplementationTitle()).orElse("PhotoPipr"),
                    Optional.ofNullable(WorkflowWindow.class.getPackage().getImplementationVersion()).orElse("0.0.0")));

            lstActions.setModel(actionListModel);
            lstActions.setCellRenderer(new ActionListCellRenderer());

            // build UI based on the workflows
            workflows.sort(workflowComparator);
            workflowComboBoxModel.addAll(workflows);
            cmbWorkflows.setModel(workflowComboBoxModel);
            cmbWorkflows.setRenderer(new WorkflowComboBoxRenderer());
            // setting the selected item will trigger the change listener and populate the Action list
            if (workflowComboBoxModel.getSize() > 0) {
                cmbWorkflows.setSelectedIndex(0);
            }

            // set the size and location
            Configuration configuration = ConfigurationManager.getConfig();
            setSize(configuration.getWindowWidth(), configuration.getWindowHeight());
            setLocation(configuration.getWindowPositionX(), configuration.getWindowPositionY());

            if (workflows.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        resourceBundle.getString("WorkflowWindow.noWorkflowsDialog.message"),
                        resourceBundle.getString("WorkflowWindow.noWorkflowsDialog.header"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
            workflowWindow = this;
            busy = false;

            // hide some menu items if running on macOS
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                mnuFile.setVisible(false);
                mnuAbout.setVisible(false);
            }
        } catch (Exception e) {
            logger.error("Error while initializing main window.", e);
            JOptionPane.showMessageDialog(this,
                    resourceBundle.getString("WorkflowWindow.startupError.message"),
                    resourceBundle.getString("WorkflowWindow.startupError.title"),
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static WorkflowWindow getInstance() {
        return workflowWindow;
    }

    public void confirmAndExit(QuitResponse quitResponse) {
        int exit = JOptionPane.YES_OPTION;
        if (busy) {
            exit = JOptionPane.showConfirmDialog(this,
                    resourceBundle.getString("WorkflowWindow.busyquit.message"),
                    resourceBundle.getString("WorkflowWindow.busyquit.title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
        }
        if (exit == JOptionPane.YES_OPTION) {
            if (quitResponse != null) {
                quitResponse.performQuit();
            } else {
                System.exit(0);
            }
        } else {

            if (quitResponse != null) {
                quitResponse.cancelQuit();
            }
        }
    }

    public void loadGroups() {
        disabledGlassPane.activate("Loading groups from Flickr...");
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                logger.info("Loading groups...");
                Groups groupList = JinxFactory.getInstance().getPeopleApi().getGroups(JinxFactory.getInstance().getNsid(), null);
                logger.info("Got {} groups", groupList.getGroupList().size());
                groups = groupList.getGroupList()
                        .stream()
                        .sorted(Comparator.comparing(group -> group.getName().toLowerCase()))
                        .toList();
            } catch (Exception e) {
                groups = new ArrayList<>();
                logger.error("Could not load groups.", e);
                JOptionPane.showMessageDialog(this,
                        """
                                Could not load groups from Flickr.
                                Adding photos to groups during upload
                                may experience problems.""",
                        "Group Load Error",
                        JOptionPane.INFORMATION_MESSAGE);
            } finally {
                disabledGlassPane.deactivate();
            }
        });
    }

    /**
     * Get the current workflows as a List.
     *
     * <p>This method will get the workflows from the model that backs the
     * user interface, then return it as an unmodifiable list. The models
     * that back the user interface will be up-to-date.</p>
     *
     * @return list reflecting the current state of the Workflows.
     */
    public List<Workflow> getWorkflows() {
        List<Workflow> list = new ArrayList<>();
        for (int i = 0; i < workflowComboBoxModel.getSize(); i++) {
            list.add(workflowComboBoxModel.getElementAt(i));
        }
        return list;
    }

    /**
     * Get the current workflows as an unmodifiable List.
     *
     * <p>This method will get the workflows from the model that backs the
     * user interface, then return it as an unmodifiable list. The models
     * that back the user interface will be up-to-date.</p>
     *
     * @return unmodifiable list reflecting the current state of the Workflows.
     */
    public List<Workflow> getUnmodifiableWorkflows() {
        return Collections.unmodifiableList(getWorkflows());
    }

    void sortWorkflowComboBox(Workflow selectedWorkflow) {
        List<Workflow> list = getWorkflows();
        list.sort(workflowComparator);
        workflowComboBoxModel.removeAllElements();
        workflowComboBoxModel.addAll(list);
        workflowComboBoxModel.setSelectedItem(selectedWorkflow);
    }

    // ------------------------- callbacks -------------------------
    void addNewWorkflow(Workflow workflow) {
        workflowComboBoxModel.addElement(workflow);
        sortWorkflowComboBox(workflow);
        actionListModel.clear();
        actionListModel.addAll(workflow.getActions());
        saveWorkflows();
    }

    void renameWorkflow(String name) {
        Workflow selectedWorkflow = (Workflow) workflowComboBoxModel.getSelectedItem();
        selectedWorkflow.setName(name);
        sortWorkflowComboBox(selectedWorkflow);
        saveWorkflows();
    }

    void addNewAction(Action action) {
        Workflow workflow = (Workflow) workflowComboBoxModel.getSelectedItem();
        workflow.addAction(action);
        saveWorkflows();
        actionListModel.clear();
        actionListModel.addAll(workflow.getActions());

        // make the newly added action the selected action
        // the addAction method puts the new action just before the Finish Action,
        // which is always last in the list.
        lstActions.setSelectedIndex(actionListModel.size() - 2);
    }

    void saveWorkflows() {
        try {
            ConfigurationManager.saveWorkflows(getWorkflows());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    resourceBundle.getString("WorkflowWindow.dialog.saveError.message"),
                    resourceBundle.getString("WorkflowWindow.dialog.saveError.title"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------- end callbacks -------------------------


    private void mnuNewWorkflow() {
        SwingUtilities.invokeLater(() -> new NewWorkflowDialog(this).setVisible(true));
    }

    private void mnuDeleteWorkflow() {
        if (workflowComboBoxModel.getSize() > 0) {
            Workflow workflow = (Workflow) workflowComboBoxModel.getSelectedItem();
            int option = JOptionPane.showConfirmDialog(this,
                    String.format(resourceBundle.getString("WorkflowWindow.deleteWorkflowDialog.message"), workflow.getName()),
                    resourceBundle.getString("WorkflowWindow.deleteWorkflowDialog.header"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                workflowComboBoxModel.removeElement(workflow);
                actionListModel.clear();
                if (workflowComboBoxModel.getSize() == 0) {
                    // There's something buggy with the JComboBox where when the last
                    // item is removed, the combo box still shows the text from the
                    // last item.
                    // To work around this, we add an empty Workflow to force display
                    // of an empty String, then remove all elements.
                    workflowComboBoxModel.addElement(new Workflow());
                    workflowComboBoxModel.removeAllElements();
                } else {
                    Workflow w = (Workflow) workflowComboBoxModel.getSelectedItem();
                    actionListModel.addAll(w.getActions());
                }
            }
        }
    }

    private void mnuRenameWorkflow() {
        if (workflowComboBoxModel.getSize() > 0) {
            Workflow workflow = (Workflow) workflowComboBoxModel.getSelectedItem();
            SwingUtilities.invokeLater(() -> new WorkflowRenameDialog(this, workflow.getName()).setVisible(true));
        }
    }

    private void mnuWorkflowMenuSelected() {
        boolean hasWorkflows = (workflowComboBoxModel.getSize() > 0);
        mnuDeleteWorkflow.setEnabled(hasWorkflows);
        mnuRenameWorkflow.setEnabled(hasWorkflows);
        mnuRunWorkflow.setEnabled(hasWorkflows);
        mnuAddAction.setEnabled(hasWorkflows);
        mnuAddUploadAction.setEnabled(hasWorkflows);
        mnuAddTimedAction.setEnabled(hasWorkflows);
    }

    private void mnuAddUploadAction() {
        UploadAction uploadAction = new UploadAction();
        addNewAction(uploadAction);
        SwingUtilities.invokeLater(() -> new UploaderDialog(this, uploadAction, groups).setVisible(true));
    }


    private void cmbWorkflowsItemStateChanged() {
        Workflow workflow = (Workflow) workflowComboBoxModel.getSelectedItem();
        actionListModel.clear();
        if (null != workflow) {
            actionListModel.addAll(workflow.getActions());
        }
    }

    private void mnuAddTimedAction() {
        TimerAction timerAction = new TimerAction();
        addNewAction(timerAction);
        SwingUtilities.invokeLater(() ->
                new TimerActionDialog(this, timerAction).setVisible(true));
    }

    private void lstWorkflowMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
            int index = lstActions.getSelectedIndex();
            Action action = actionListModel.getElementAt(index);
            if (action instanceof TimerAction) {
                SwingUtilities.invokeLater(() -> new TimerActionDialog(this, (TimerAction) action).setVisible(true));
            } else if (action instanceof FinishAction) {
                SwingUtilities.invokeLater(() -> new FinishActionDialog(this, (FinishAction) action).setVisible(true));
            } else if (action instanceof UploadAction) {
                SwingUtilities.invokeLater(() -> new UploaderDialog(this, (UploadAction) action, groups).setVisible(true));
            } else {
                logger.error("Unexpected Action class {}", action.getClass().getName());
                JOptionPane.showMessageDialog(this,
                        String.format(resourceBundle.getString("WorkflowWindow.dialog.badType.message"), action.getClass().getName()),
                        resourceBundle.getString("WorkflowWindow.dialog.badType.title"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mnuQuit() {
        System.exit(0);
    }

    private void mnuRunWorkflow() {
        new WorkflowRunner(actionListModel).execute();
    }

    private void thisComponentMoved() {
        Configuration configuration = ConfigurationManager.getConfig();
        configuration.setWindowPositionX(getX());
        configuration.setWindowPositionY(getY());
        ConfigurationManager.saveConfiguration();
    }

    private void thisComponentResized() {
        Configuration configuration = ConfigurationManager.getConfig();
        configuration.setWindowHeight(getHeight());
        configuration.setWindowWidth(getWidth());
        ConfigurationManager.saveConfiguration();
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
        if (busy) {
            disabledGlassPane.activate("");
        } else {
            disabledGlassPane.deactivate();
        }
    }

    private void mnuDeleteAction() {
        int index = lstActions.getSelectedIndex();
        if (index > -1) {
            Action action = lstActions.getSelectedValue();
            if (action instanceof FinishAction) {
                JOptionPane.showMessageDialog(this,
                        resourceBundle.getString("WorkflowWindow.cantdelete.message"),
                        resourceBundle.getString("WorkflowWindow.cantdelete.title"),
                        JOptionPane.ERROR_MESSAGE);
            } else {
                Workflow workflow = (Workflow) workflowComboBoxModel.getSelectedItem();
                workflow.removeAction(action);
                actionListModel.clear();
                actionListModel.addAll(workflow.getActions());
                saveWorkflows();
            }
        }
    }

    private void mnuAbout() {
        new AboutDialog(this).setVisible(true);
    }

    private void mnuPreferences() {
        new PreferencesDialog(this).setVisible(true);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        ResourceBundle bundle = ResourceBundle.getBundle("net.jeremybrooks.photopipr.workflow");
        menuBar1 = new JMenuBar();
        mnuFile = new JMenu();
        mnuPreferences = new JMenuItem();
        mnuQuit = new JMenuItem();
        mnuWorkflow = new JMenu();
        mnuNewWorkflow = new JMenuItem();
        mnuRenameWorkflow = new JMenuItem();
        mnuDeleteWorkflow = new JMenuItem();
        mnuRunWorkflow = new JMenuItem();
        mnuAddAction = new JMenu();
        mnuAddUploadAction = new JMenuItem();
        mnuAddTimedAction = new JMenuItem();
        mnuDeleteAction = new JMenuItem();
        muHelp = new JMenu();
        menuItem1 = new JMenuItem();
        mnuAbout = new JMenuItem();
        label1 = new JLabel();
        cmbWorkflows = new JComboBox<>();
        scrollPane1 = new JScrollPane();
        lstActions = new JList<>();
        lblStatus = new JLabel();

        //======== this ========
        setIconImage(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/icon-256.png")).getImage());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                thisComponentMoved();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                thisComponentResized();
            }
        });
        var contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout) contentPane.getLayout()).columnWidths = new int[]{0, 0, 0};
        ((GridBagLayout) contentPane.getLayout()).rowHeights = new int[]{0, 0, 0, 0};
        ((GridBagLayout) contentPane.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0E-4};
        ((GridBagLayout) contentPane.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 1.0E-4};

        //======== menuBar1 ========
        {

            //======== mnuFile ========
            {
                mnuFile.setText(bundle.getString("WorkflowWindow.mnuFile.text"));

                //---- mnuPreferences ----
                mnuPreferences.setText(bundle.getString("WorkflowWindow.mnuPreferences.text"));
                mnuPreferences.addActionListener(e -> mnuPreferences());
                mnuFile.add(mnuPreferences);

                //---- mnuQuit ----
                mnuQuit.setText(bundle.getString("WorkflowWindow.mnuQuit.text"));
                mnuQuit.addActionListener(e -> mnuQuit());
                mnuFile.add(mnuQuit);
            }
            menuBar1.add(mnuFile);

            //======== mnuWorkflow ========
            {
                mnuWorkflow.setText(bundle.getString("WorkflowWindow.mnuWorkflow.text"));
                mnuWorkflow.addMenuListener(new MenuListener() {
                    @Override
                    public void menuCanceled(MenuEvent e) {
                    }

                    @Override
                    public void menuDeselected(MenuEvent e) {
                    }

                    @Override
                    public void menuSelected(MenuEvent e) {
                        mnuWorkflowMenuSelected();
                    }
                });

                //---- mnuNewWorkflow ----
                mnuNewWorkflow.setText(bundle.getString("WorkflowWindow.mnuNewWorkflow.text"));
                mnuNewWorkflow.addActionListener(e -> mnuNewWorkflow());
                mnuWorkflow.add(mnuNewWorkflow);

                //---- mnuRenameWorkflow ----
                mnuRenameWorkflow.setText(bundle.getString("WorkflowWindow.mnuRenameWorkflow.text"));
                mnuRenameWorkflow.addActionListener(e -> mnuRenameWorkflow());
                mnuWorkflow.add(mnuRenameWorkflow);

                //---- mnuDeleteWorkflow ----
                mnuDeleteWorkflow.setText(bundle.getString("WorkflowWindow.mnuDeleteWorkflow.text"));
                mnuDeleteWorkflow.addActionListener(e -> mnuDeleteWorkflow());
                mnuWorkflow.add(mnuDeleteWorkflow);

                //---- mnuRunWorkflow ----
                mnuRunWorkflow.setText(bundle.getString("WorkflowWindow.mnuRunWorkflow.text"));
                mnuRunWorkflow.addActionListener(e -> mnuRunWorkflow());
                mnuWorkflow.add(mnuRunWorkflow);
                mnuWorkflow.addSeparator();

                //======== mnuAddAction ========
                {
                    mnuAddAction.setText(bundle.getString("WorkflowWindow.mnuAddAction.text"));

                    //---- mnuAddUploadAction ----
                    mnuAddUploadAction.setText(bundle.getString("WorkflowWindow.mnuAddUploadAction.text"));
                    mnuAddUploadAction.addActionListener(e -> mnuAddUploadAction());
                    mnuAddAction.add(mnuAddUploadAction);

                    //---- mnuAddTimedAction ----
                    mnuAddTimedAction.setText(bundle.getString("WorkflowWindow.mnuAddTimedAction.text"));
                    mnuAddTimedAction.addActionListener(e -> mnuAddTimedAction());
                    mnuAddAction.add(mnuAddTimedAction);
                }
                mnuWorkflow.add(mnuAddAction);

                //---- mnuDeleteAction ----
                mnuDeleteAction.setText(bundle.getString("WorkflowWindow.mnuDeleteAction.text"));
                mnuDeleteAction.addActionListener(e -> mnuDeleteAction());
                mnuWorkflow.add(mnuDeleteAction);
            }
            menuBar1.add(mnuWorkflow);

            //======== muHelp ========
            {
                muHelp.setText(bundle.getString("WorkflowWindow.muHelp.text"));

                //---- menuItem1 ----
                menuItem1.setText(bundle.getString("WorkflowWindow.menuItem1.text"));
                muHelp.add(menuItem1);

                //---- mnuAbout ----
                mnuAbout.setText(bundle.getString("WorkflowWindow.mnuAbout.text"));
                mnuAbout.addActionListener(e -> mnuAbout());
                muHelp.add(mnuAbout);
            }
            menuBar1.add(muHelp);
        }
        setJMenuBar(menuBar1);

        //---- label1 ----
        label1.setText(bundle.getString("WorkflowWindow.label1.text"));
        contentPane.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 10, 10), 0, 0));

        //---- cmbWorkflows ----
        cmbWorkflows.addItemListener(e -> cmbWorkflowsItemStateChanged());
        contentPane.add(cmbWorkflows, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

        //======== scrollPane1 ========
        {

            //---- lstActions ----
            lstActions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            lstActions.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    lstWorkflowMouseClicked(e);
                }
            });
            scrollPane1.setViewportView(lstActions);
        }
        contentPane.add(scrollPane1, new GridBagConstraints(0, 1, 2, 1, 0.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 10, 5), 0, 0));

        //---- lblStatus ----
        lblStatus.setText(bundle.getString("WorkflowWindow.lblStatus.text"));
        contentPane.add(lblStatus, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(3, 5, 3, 0), 0, 0));
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar menuBar1;
    private JMenu mnuFile;
    private JMenuItem mnuPreferences;
    private JMenuItem mnuQuit;
    private JMenu mnuWorkflow;
    private JMenuItem mnuNewWorkflow;
    private JMenuItem mnuRenameWorkflow;
    private JMenuItem mnuDeleteWorkflow;
    private JMenuItem mnuRunWorkflow;
    private JMenu mnuAddAction;
    private JMenuItem mnuAddUploadAction;
    private JMenuItem mnuAddTimedAction;
    private JMenuItem mnuDeleteAction;
    private JMenu muHelp;
    private JMenuItem menuItem1;
    private JMenuItem mnuAbout;
    private JLabel label1;
    private JComboBox<Workflow> cmbWorkflows;
    private JScrollPane scrollPane1;
    private JList<Action> lstActions;
    private JLabel lblStatus;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
