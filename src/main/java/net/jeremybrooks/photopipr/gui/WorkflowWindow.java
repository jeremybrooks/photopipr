/*
 *  PhotoPipr is Copyright 2017-2025 by Jeremy Brooks
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
import net.jeremybrooks.photopipr.Main;
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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.desktop.QuitResponse;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serial;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    private WorkflowRunner workflowRunner;

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
            updateMenuStates();

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
                saveWorkflows();
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
        mnuDeleteWorkflow.setEnabled(hasWorkflows && !busy);
        mnuRenameWorkflow.setEnabled(hasWorkflows && !busy);
        mnuRunWorkflow.setEnabled(hasWorkflows && !busy);
        mnuAddAction.setEnabled(hasWorkflows && !busy);
        mnuAddUploadAction.setEnabled(hasWorkflows && !busy);
        mnuAddTimedAction.setEnabled(hasWorkflows && !busy);
        mnuStopWorkflow.setEnabled(hasWorkflows && busy);
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

    private void mnuQuit() {
        System.exit(0);
    }

    private void mnuRunWorkflow() {
        startWorkflowRunner(0);
    }

    private void startWorkflowRunner(int startIndex) {
        lstActions.clearSelection();
        workflowRunner = new WorkflowRunner(actionListModel, startIndex);
        workflowRunner.execute();
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
        updateMenuStates();
        cmbWorkflows.setEnabled(!busy);
        if (busy) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            setCursor(null);
        }
    }

    private void updateMenuStates() {
        mnuNewWorkflow.setEnabled(!busy);
        mnuRenameWorkflow.setEnabled(!busy);
        mnuDeleteWorkflow.setEnabled(!busy);
        mnuRunWorkflow.setEnabled(!busy);
        mnuStopWorkflow.setEnabled(busy);
        mnuAddAction.setEnabled(!busy);
        mnuAddTimedAction.setEnabled(!busy);
        mnuAddUploadAction.setEnabled(!busy);
        mnuDeleteAction.setEnabled(!busy);
    }

    private void mnuDeleteAction() {
        deleteAction();
    }

    private void mnuAbout() {
        new AboutDialog(this).setVisible(true);
    }

    private void mnuPreferences() {
        new PreferencesDialog(this).setVisible(true);
    }

    private void editAction() {
        int index = lstActions.getSelectedIndex();
        if (index > -1) {
            Action action = actionListModel.getElementAt(index);
            if (action.getStatus() == Action.Status.RUNNING) {
                JOptionPane.showMessageDialog(this,
                        resourceBundle.getString("WorkflowWindow.actionBusy.message"),
                        resourceBundle.getString("WorkflowWindow.actionBusy.title"),
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                switch (action) {
                    case TimerAction ignored ->
                            SwingUtilities.invokeLater(() -> new TimerActionDialog(this, (TimerAction) action).setVisible(true));
                    case FinishAction ignored1 ->
                            SwingUtilities.invokeLater(() -> new FinishActionDialog(this, (FinishAction) action).setVisible(true));
                    case UploadAction ignored ->
                            SwingUtilities.invokeLater(() -> new UploaderDialog(this, (UploadAction) action, groups).setVisible(true));
                    default -> {
                    }
                }
            }
        }
    }


    private void deleteAction() {
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

    /*
     * Check for popup in both mouse pressed and mouse released for maximum compatibility.
     */
    private void lstActionsMousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showActionContextMenu(e);
        } else if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
            editAction();
        }
    }

    /*
     * Check for popup in both mouse pressed and mouse released for maximum compatibility.
     */
    private void lstActionsMouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showActionContextMenu(e);
        }
    }

    private void showActionContextMenu(MouseEvent e) {
        if (e.getComponent() instanceof JList<?> list) {
            int index = e.getY() / (int) list.getCellBounds(0, 0).getHeight();
            // show popup if the index is in bounds of the model size
            if (index < list.getModel().getSize()) {
                if (busy) {
                    JOptionPane.showMessageDialog(this,
                            resourceBundle.getString("WorkflowWindow.workflowBusy.message"),
                            resourceBundle.getString("WorkflowWindow.workflowBusy.title"),
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    list.setSelectedIndex(index);
                    mnuCtxMoveDown.setEnabled(canActionMoveDown());
                    mnuCtxMoveUp.setEnabled(canActionMoveUp());
                    mnuCtxAction.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
    }

    private void mnuCtxEditAction() {
        editAction();
    }

    private void mnuCtxDeleteAction() {
        deleteAction();
    }

    private void mnuCtxMoveUp() {
        if (canActionMoveUp()) {
            int index = lstActions.getSelectedIndex();
            Action action = actionListModel.remove(index);
            actionListModel.add(index - 1, action);
        }
    }

    private void mnuCtxMoveDown() {
        if (canActionMoveDown()) {
            int index = lstActions.getSelectedIndex();
            Action action = actionListModel.remove(index);
            actionListModel.add(index + 1, action);
        }
    }

    private boolean canActionMoveUp() {
        int index = lstActions.getSelectedIndex();
        return index > 0 && index < (lstActions.getModel().getSize() - 1);
    }

    private boolean canActionMoveDown() {
        return lstActions.getSelectedIndex() < (lstActions.getModel().getSize() - 2);
    }

    private void mnuCtxAddUpload() {
        mnuAddUploadAction();
    }

    private void mnuCtxAddTime() {
        mnuAddTimedAction();
    }

    /**
     * Called by the WorkflowRunner when it has completed.
     */
    public void workflowRunnerFinished() {
        workflowRunner = null;
        setBusy(false);
    }

    private void mnuStopWorkflow() {
        if (workflowRunner != null) {
            workflowRunner.cancelWorkflow();
        }
    }

    private void mnuCtxRunFromHere() {
        startWorkflowRunner(lstActions.getSelectedIndex());
    }

    private void mnuCompress() {
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle(resourceBundle.getString("WorkflowWindow.ziplogs.dialog.title.text"));
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
            String filename = "photopipr-logs-"
                    + sdf.format(new Date()) + ".zip";
            File zipFile = new File(jfc.getSelectedFile(), filename);
            logger.info("Creating archive {}", zipFile.getAbsolutePath());

            List<Path> configList = new ArrayList<>();
            List<Path> logList = new ArrayList<>();
            try (Stream<Path> s = Files.list(Main.APP_HOME)) {
                configList.addAll(s.filter(p -> p.getFileName().toString().endsWith(".json"))
                        .toList());
            } catch (Exception e) {
                logger.warn("Error while reading config files.", e);
            }
            try (Stream<Path> s = Files.list(Paths.get(Main.APP_HOME.toString(), "logs"))) {
                logList.addAll(s.filter(p -> p.getFileName().toString().endsWith(".log"))
                        .toList());
            } catch (Exception e) {
                logger.warn("Error while reading log files.", e);
            }

            byte[] buf = new byte[1024];
            try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
                zipFile.createNewFile();
                logger.info("Adding {} config files to zip.", configList.size());
                for (Path configFile : configList) {
                    logger.info("Adding file {} to archive.", configFile);
                    try (InputStream in = Files.newInputStream(configFile)) {
                        out.putNextEntry(new ZipEntry(configFile.getFileName().toString()));
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        out.closeEntry();
                    }
                }
                out.putNextEntry(new ZipEntry("logs/"));
                logger.info("Adding {} log files to zip.", logList.size());
                for (Path logFile : logList) {
                    logger.info("Adding file {} to archive.", logFile);
                    try (InputStream in = Files.newInputStream(logFile)) {
                        out.putNextEntry(new ZipEntry("logs/" + logFile.getFileName()));
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        out.closeEntry();
                    }
                }
                JOptionPane.showMessageDialog(this,
                        resourceBundle.getString("WorkflowWindow.dialog.zipfile.created.message1") +
                                " " + new File(jfc.getSelectedFile(), filename).getAbsolutePath() + "\n" +
                                resourceBundle.getString("WorkflowWindow.dialog.zipfile.created.message2"),
                        resourceBundle.getString("WorkflowWindow.dialog.zipfile.created.title"),
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                logger.warn("ERROR CREATING ZIP.", e);
                JOptionPane.showMessageDialog(this,
                        resourceBundle.getString("WorkflowWindow.dialog.zipfile.error.message"),
                        resourceBundle.getString("WorkflowWindow.dialog.zipfile.error.title"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void menuItem1() {
        int option =
                JOptionPane.showConfirmDialog(this,
                        resourceBundle.getString("WorkflowWindow.dialog.help.message"),
                        resourceBundle.getString("WorkflowWindow.dialog.help.title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            try {
                Desktop.getDesktop().browse(new URL("https://jeremybrooks.net/photopipr/faq.html").toURI());
            } catch (Exception e) {
                logger.error("Could not open help URL.", e);
                JOptionPane.showMessageDialog(this,
                        resourceBundle.getString("WorkflowWindow.dialog.browserError.message"),
                        resourceBundle.getString("WorkflowWindow.dialog.browserError.title"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
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
        mnuStopWorkflow = new JMenuItem();
        mnuAddAction = new JMenu();
        mnuAddUploadAction = new JMenuItem();
        mnuAddTimedAction = new JMenuItem();
        mnuDeleteAction = new JMenuItem();
        muHelp = new JMenu();
        menuItem1 = new JMenuItem();
        mnuAbout = new JMenuItem();
        mnuCompress = new JMenuItem();
        label1 = new JLabel();
        cmbWorkflows = new JComboBox<>();
        scrollPane1 = new JScrollPane();
        lstActions = new JList<>();
        lblStatus = new JLabel();
        mnuCtxAction = new JPopupMenu();
        mnuCtxEditAction = new JMenuItem();
        mnuCtxMoveUp = new JMenuItem();
        mnuCtxMoveDown = new JMenuItem();
        mnuCtxDeleteAction = new JMenuItem();
        menu1 = new JMenu();
        mnuCtxAddUpload = new JMenuItem();
        mnuCtxAddTime = new JMenuItem();
        mnuCtxRunFromHere = new JMenuItem();

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
        ((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {0, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
        ((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

        //======== menuBar1 ========
        {

            //======== mnuFile ========
            {
                mnuFile.setText(bundle.getString("WorkflowWindow.mnuFile.text"));

                //---- mnuPreferences ----
                mnuPreferences.setText(bundle.getString("WorkflowWindow.mnuPreferences.text"));
                mnuPreferences.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/14-gear-16.png")));
                mnuPreferences.addActionListener(e -> mnuPreferences());
                mnuFile.add(mnuPreferences);

                //---- mnuQuit ----
                mnuQuit.setText(bundle.getString("WorkflowWindow.mnuQuit.text"));
                mnuQuit.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/602-exit-16.png")));
                mnuQuit.addActionListener(e -> mnuQuit());
                mnuFile.add(mnuQuit);
            }
            menuBar1.add(mnuFile);

            //======== mnuWorkflow ========
            {
                mnuWorkflow.setText(bundle.getString("WorkflowWindow.mnuWorkflow.text"));
                mnuWorkflow.addMenuListener(new MenuListener() {
                    @Override
                    public void menuCanceled(MenuEvent e) {}
                    @Override
                    public void menuDeselected(MenuEvent e) {}
                    @Override
                    public void menuSelected(MenuEvent e) {
                        mnuWorkflowMenuSelected();
                    }
                });

                //---- mnuNewWorkflow ----
                mnuNewWorkflow.setText(bundle.getString("WorkflowWindow.mnuNewWorkflow.text"));
                mnuNewWorkflow.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/33-circle-plus-16.png")));
                mnuNewWorkflow.addActionListener(e -> mnuNewWorkflow());
                mnuWorkflow.add(mnuNewWorkflow);

                //---- mnuRenameWorkflow ----
                mnuRenameWorkflow.setText(bundle.getString("WorkflowWindow.mnuRenameWorkflow.text"));
                mnuRenameWorkflow.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/591-pencil-16.png")));
                mnuRenameWorkflow.addActionListener(e -> mnuRenameWorkflow());
                mnuWorkflow.add(mnuRenameWorkflow);

                //---- mnuDeleteWorkflow ----
                mnuDeleteWorkflow.setText(bundle.getString("WorkflowWindow.mnuDeleteWorkflow.text"));
                mnuDeleteWorkflow.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/34-circle.minus-16.png")));
                mnuDeleteWorkflow.addActionListener(e -> mnuDeleteWorkflow());
                mnuWorkflow.add(mnuDeleteWorkflow);

                //---- mnuRunWorkflow ----
                mnuRunWorkflow.setText(bundle.getString("WorkflowWindow.mnuRunWorkflow.text"));
                mnuRunWorkflow.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/36-circle-play-16.png")));
                mnuRunWorkflow.addActionListener(e -> mnuRunWorkflow());
                mnuWorkflow.add(mnuRunWorkflow);

                //---- mnuStopWorkflow ----
                mnuStopWorkflow.setText(bundle.getString("WorkflowWindow.mnuStopWorkflow.text"));
                mnuStopWorkflow.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/1243-stop-16.png")));
                mnuStopWorkflow.addActionListener(e -> mnuStopWorkflow());
                mnuWorkflow.add(mnuStopWorkflow);
                mnuWorkflow.addSeparator();

                //======== mnuAddAction ========
                {
                    mnuAddAction.setText(bundle.getString("WorkflowWindow.mnuAddAction.text"));
                    mnuAddAction.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/33-circle-plus-16.png")));

                    //---- mnuAddUploadAction ----
                    mnuAddUploadAction.setText(bundle.getString("WorkflowWindow.mnuAddUploadAction.text"));
                    mnuAddUploadAction.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/168-upload-photo-2-16.png")));
                    mnuAddUploadAction.addActionListener(e -> mnuAddUploadAction());
                    mnuAddAction.add(mnuAddUploadAction);

                    //---- mnuAddTimedAction ----
                    mnuAddTimedAction.setText(bundle.getString("WorkflowWindow.mnuAddTimedAction.text"));
                    mnuAddTimedAction.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/1097-timer-2-16.png")));
                    mnuAddTimedAction.addActionListener(e -> mnuAddTimedAction());
                    mnuAddAction.add(mnuAddTimedAction);
                }
                mnuWorkflow.add(mnuAddAction);

                //---- mnuDeleteAction ----
                mnuDeleteAction.setText(bundle.getString("WorkflowWindow.mnuDeleteAction.text"));
                mnuDeleteAction.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/34-circle.minus-16.png")));
                mnuDeleteAction.addActionListener(e -> mnuDeleteAction());
                mnuWorkflow.add(mnuDeleteAction);
            }
            menuBar1.add(mnuWorkflow);

            //======== muHelp ========
            {
                muHelp.setText(bundle.getString("WorkflowWindow.muHelp.text"));

                //---- menuItem1 ----
                menuItem1.setText(bundle.getString("WorkflowWindow.menuItem1.text"));
                menuItem1.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/441-help-symbol1-16.png")));
                menuItem1.addActionListener(e -> menuItem1());
                muHelp.add(menuItem1);

                //---- mnuAbout ----
                mnuAbout.setText(bundle.getString("WorkflowWindow.mnuAbout.text"));
                mnuAbout.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/59-info-symbol-16.png")));
                mnuAbout.addActionListener(e -> mnuAbout());
                muHelp.add(mnuAbout);

                //---- mnuCompress ----
                mnuCompress.setText(bundle.getString("WorkflowWindow.mnuCompress.text"));
                mnuCompress.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/1197-file-type-zip-16.png")));
                mnuCompress.addActionListener(e -> mnuCompress());
                muHelp.add(mnuCompress);
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
                public void mousePressed(MouseEvent e) {
                    lstActionsMousePressed(e);
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    lstActionsMouseReleased(e);
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

        //======== mnuCtxAction ========
        {

            //---- mnuCtxEditAction ----
            mnuCtxEditAction.setText(bundle.getString("WorkflowWindow.mnuCtxEditAction.text"));
            mnuCtxEditAction.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/591-pencil-16.png")));
            mnuCtxEditAction.addActionListener(e -> mnuCtxEditAction());
            mnuCtxAction.add(mnuCtxEditAction);

            //---- mnuCtxMoveUp ----
            mnuCtxMoveUp.setText(bundle.getString("WorkflowWindow.mnuCtxMoveUp.text"));
            mnuCtxMoveUp.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/27-circle-north-16.png")));
            mnuCtxMoveUp.addActionListener(e -> mnuCtxMoveUp());
            mnuCtxAction.add(mnuCtxMoveUp);

            //---- mnuCtxMoveDown ----
            mnuCtxMoveDown.setText(bundle.getString("WorkflowWindow.mnuCtxMoveDown.text"));
            mnuCtxMoveDown.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/23-circle-south-16.png")));
            mnuCtxMoveDown.addActionListener(e -> mnuCtxMoveDown());
            mnuCtxAction.add(mnuCtxMoveDown);

            //---- mnuCtxDeleteAction ----
            mnuCtxDeleteAction.setText(bundle.getString("WorkflowWindow.mnuCtxDeleteAction.text"));
            mnuCtxDeleteAction.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/34-circle.minus-16.png")));
            mnuCtxDeleteAction.addActionListener(e -> mnuCtxDeleteAction());
            mnuCtxAction.add(mnuCtxDeleteAction);

            //======== menu1 ========
            {
                menu1.setText(bundle.getString("WorkflowWindow.menu1.text"));
                menu1.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/33-circle-plus-16.png")));

                //---- mnuCtxAddUpload ----
                mnuCtxAddUpload.setText(bundle.getString("WorkflowWindow.mnuCtxAddUpload.text"));
                mnuCtxAddUpload.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/168-upload-photo-2-16.png")));
                mnuCtxAddUpload.addActionListener(e -> mnuCtxAddUpload());
                menu1.add(mnuCtxAddUpload);

                //---- mnuCtxAddTime ----
                mnuCtxAddTime.setText(bundle.getString("WorkflowWindow.mnuCtxAddTime.text"));
                mnuCtxAddTime.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/1097-timer-2-16.png")));
                mnuCtxAddTime.addActionListener(e -> mnuCtxAddTime());
                menu1.add(mnuCtxAddTime);
            }
            mnuCtxAction.add(menu1);
            mnuCtxAction.addSeparator();

            //---- mnuCtxRunFromHere ----
            mnuCtxRunFromHere.setText(bundle.getString("WorkflowWindow.mnuCtxRunFromHere.text"));
            mnuCtxRunFromHere.setIcon(new ImageIcon(getClass().getResource("/net/jeremybrooks/photopipr/icons/36-circle-play-16.png")));
            mnuCtxRunFromHere.addActionListener(e -> mnuCtxRunFromHere());
            mnuCtxAction.add(mnuCtxRunFromHere);
        }
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
    private JMenuItem mnuStopWorkflow;
    private JMenu mnuAddAction;
    private JMenuItem mnuAddUploadAction;
    private JMenuItem mnuAddTimedAction;
    private JMenuItem mnuDeleteAction;
    private JMenu muHelp;
    private JMenuItem menuItem1;
    private JMenuItem mnuAbout;
    private JMenuItem mnuCompress;
    private JLabel label1;
    private JComboBox<Workflow> cmbWorkflows;
    private JScrollPane scrollPane1;
    private JList<Action> lstActions;
    private JLabel lblStatus;
    private JPopupMenu mnuCtxAction;
    private JMenuItem mnuCtxEditAction;
    private JMenuItem mnuCtxMoveUp;
    private JMenuItem mnuCtxMoveDown;
    private JMenuItem mnuCtxDeleteAction;
    private JMenu menu1;
    private JMenuItem mnuCtxAddUpload;
    private JMenuItem mnuCtxAddTime;
    private JMenuItem mnuCtxRunFromHere;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
