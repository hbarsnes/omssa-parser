package de.proteinms.omxparser.tools;

import java.awt.Frame;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * A dialog that lets you choose the files the to be viewed in OMSSA Viewer.
 *
 * @author Harald Barsnes
 *
 * Created December 2008.
 */
public class OmssaViewerFileSelection extends javax.swing.JDialog {

    /**
     * The last selected folder. Defaults to user.home.
     */
    private String lastSelectedFolder = "user.home";
    private Frame parent;

    /**
     * Creates new OmssaViewerFileSelection dialog and makes it visible.
     *
     * @param parent the parent frame (the OMSSA Viewer)
     * @param modal if the dialog is to be modal
     * @param omxFile the OMSSA omx file to parse
     * @param modsFile the mods.xml file
     * @param userModsFile the usermods.xml file
     * @param lastSelectedFolder the last selected folder
     */
    public OmssaViewerFileSelection(java.awt.Frame parent, boolean modal,
            String omxFile, String modsFile, String userModsFile, String lastSelectedFolder) {
        super(parent, modal);
        initComponents();

        this.lastSelectedFolder = lastSelectedFolder;

        if (omxFile != null) {
            omxFileJTextField.setText(omxFile);
        }

        if (modsFile != null) {
            modsFileJTextField.setText(modsFile);
        }

        if (userModsFile != null) {
            userModsJTextField.setText(userModsFile);
        }

        if (omxFileJTextField.getText().length() > 0) {
            okJButton.setEnabled(true);
        }

        this.parent = parent;

        // only works for Java 1.6 and newer
//        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().
//                getResource("/de/proteinms/omxparser/icons/omssaviewer.GIF")));

        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        helpJButton = new javax.swing.JButton();
        aboutJButton = new javax.swing.JButton();
        filesPanel = new javax.swing.JPanel();
        omxFileJTextField = new javax.swing.JTextField();
        findUserModsFileJButton = new javax.swing.JButton();
        findModsFileJButton = new javax.swing.JButton();
        userModsJTextField = new javax.swing.JTextField();
        modsFileJTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        findOmxFileJButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        okJButton = new javax.swing.JButton();
        cancelJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("OMSSA Viewer - File Selection");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        helpJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/proteinms/omxparser/icons/help.GIF"))); // NOI18N
        helpJButton.setToolTipText("Help");
        helpJButton.setBorderPainted(false);
        helpJButton.setContentAreaFilled(false);
        helpJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpJButtonActionPerformed(evt);
            }
        });

        aboutJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/proteinms/omxparser/icons/omssaviewer16.GIF"))); // NOI18N
        aboutJButton.setToolTipText("About");
        aboutJButton.setBorderPainted(false);
        aboutJButton.setContentAreaFilled(false);
        aboutJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutJButtonActionPerformed(evt);
            }
        });

        filesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Input Files"));

        omxFileJTextField.setEditable(false);
        omxFileJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        omxFileJTextField.setToolTipText("The OMSSA omx file");

        findUserModsFileJButton.setText("Browse");
        findUserModsFileJButton.setToolTipText("Select File");
        findUserModsFileJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findUserModsFileJButtonActionPerformed(evt);
            }
        });

        findModsFileJButton.setText("Browse");
        findModsFileJButton.setToolTipText("Select File");
        findModsFileJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findModsFileJButtonActionPerformed(evt);
            }
        });

        userModsJTextField.setEditable(false);
        userModsJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        userModsJTextField.setText("(optional)");
        userModsJTextField.setToolTipText("The OMSSA usermods.xml file");

        modsFileJTextField.setEditable(false);
        modsFileJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        modsFileJTextField.setText("(optional)");
        modsFileJTextField.setToolTipText("The OMSSA mods.xml file");

        jLabel3.setText("User Modifications File:");
        jLabel3.setToolTipText("The OMSSA usermods.xml file");

        jLabel2.setText("Modifications File:");
        jLabel2.setToolTipText("The OMSSA mods.xml file");

        findOmxFileJButton.setText("Browse");
        findOmxFileJButton.setToolTipText("Select File");
        findOmxFileJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findOmxFileJButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Search Results File:");
        jLabel1.setToolTipText("The OMSSA omx file");

        org.jdesktop.layout.GroupLayout filesPanelLayout = new org.jdesktop.layout.GroupLayout(filesPanel);
        filesPanel.setLayout(filesPanelLayout);
        filesPanelLayout.setHorizontalGroup(
            filesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(filesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(filesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(filesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3)
                        .add(jLabel2)))
                .add(18, 18, 18)
                .add(filesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(filesPanelLayout.createSequentialGroup()
                        .add(userModsJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 381, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(findUserModsFileJButton))
                    .add(filesPanelLayout.createSequentialGroup()
                        .add(modsFileJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 383, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(findModsFileJButton))
                    .add(filesPanelLayout.createSequentialGroup()
                        .add(omxFileJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 383, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(findOmxFileJButton)))
                .addContainerGap())
        );

        filesPanelLayout.linkSize(new java.awt.Component[] {modsFileJTextField, omxFileJTextField, userModsJTextField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        filesPanelLayout.setVerticalGroup(
            filesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(filesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(filesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(omxFileJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1)
                    .add(findOmxFileJButton))
                .add(7, 7, 7)
                .add(filesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(findModsFileJButton)
                    .add(modsFileJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(filesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(findUserModsFileJButton)
                    .add(userModsJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addContainerGap())
        );

        okJButton.setText("OK");
        okJButton.setEnabled(false);
        okJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okJButtonActionPerformed(evt);
            }
        });

        cancelJButton.setText("Cancel");
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(filesPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(helpJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(aboutJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(okJButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelJButton)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {cancelJButton, okJButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(filesPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(helpJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(aboutJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(okJButton)
                    .add(cancelJButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Opens a file chooser for the user to locate the usermods.xml file.
     *
     * @param evt
     */
    private void findUserModsFileJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findUserModsFileJButtonActionPerformed

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        JFileChooser chooser;

        if (userModsJTextField.getText().equalsIgnoreCase("(optional)")) {
            chooser = new JFileChooser(lastSelectedFolder);
        } else {
            chooser = new JFileChooser(userModsJTextField.getText());
        }

        chooser.setFileFilter(new XmlFileFilter());
        chooser.setDialogTitle("Locate the usermods.xml file");

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = (chooser.getSelectedFile().getAbsoluteFile().getPath());
            //userModsJTextField.setHorizontalAlignment(JTextField.LEFT);

            if (!chooser.getSelectedFile().getName().toLowerCase().equalsIgnoreCase("usermods.xml")) {
                JOptionPane.showMessageDialog(this,
                        "This is not the usermods.xml file. Please select " +
                        "the usermods.xml file in the OMMSA installation folder.",
                        "Wrong File!", JOptionPane.ERROR_MESSAGE);
                userModsJTextField.setText(path);
                findUserModsFileJButtonActionPerformed(null);
            } else {
                userModsJTextField.setText(path);
                lastSelectedFolder = path;

                if (omxFileJTextField.getText().length() > 0) {
                    okJButton.setEnabled(true);
                }
            }
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_findUserModsFileJButtonActionPerformed

    /**
     * Opens a file chooser for the user to locate the mods.xml file.
     *
     * @param evt
     */
    private void findModsFileJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findModsFileJButtonActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        JFileChooser chooser;

        if (modsFileJTextField.getText().equalsIgnoreCase("(optional)")) {
            chooser = new JFileChooser(lastSelectedFolder);
        } else {
            chooser = new JFileChooser(modsFileJTextField.getText());
        }

        chooser.setFileFilter(new XmlFileFilter());
        chooser.setDialogTitle("Locate the mods.xml file");

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = (chooser.getSelectedFile().getAbsoluteFile().getPath());
            //modsFileJTextField.setHorizontalAlignment(JTextField.LEFT);

            if (!chooser.getSelectedFile().getName().toLowerCase().equalsIgnoreCase("mods.xml")) {
                JOptionPane.showMessageDialog(this,
                        "This is not the mods.xml file. Please select " +
                        "the mods.xml file in the OMMSA installation folder.",
                        "Wrong File!", JOptionPane.ERROR_MESSAGE);
                modsFileJTextField.setText(path);
                findModsFileJButtonActionPerformed(null);
            } else {
                modsFileJTextField.setText(path);
                lastSelectedFolder = path;

                if (omxFileJTextField.getText().length() > 0) {
                    okJButton.setEnabled(true);
                }
            }
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_findModsFileJButtonActionPerformed

    /**
     * Opens a file chooser for the user to locate the omx file.
     *
     * @param evt
     */
    private void findOmxFileJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findOmxFileJButtonActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        JFileChooser chooser;

        if (omxFileJTextField.getText().length() == 0) {
            chooser = new JFileChooser(lastSelectedFolder);
        } else {
            chooser = new JFileChooser(omxFileJTextField.getText());
        }

        chooser.setFileFilter(new OmxFileFilter());
        chooser.setDialogTitle("Locate the OMSSA omx file");

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = (chooser.getSelectedFile().getAbsoluteFile().getPath());
            //omxFileJTextField.setHorizontalAlignment(JTextField.LEFT);

            if (!chooser.getSelectedFile().getName().toLowerCase().endsWith(".omx")) {
                JOptionPane.showMessageDialog(this,
                        "This is not an omx file. Please select " +
                        "an OMSSA omx file.",
                        "Wrong File!", JOptionPane.ERROR_MESSAGE);
                omxFileJTextField.setText(path);
                findOmxFileJButtonActionPerformed(null);
            } else {
                omxFileJTextField.setText(path);
                lastSelectedFolder = path;

                if (omxFileJTextField.getText().length() > 0) {
                    okJButton.setEnabled(true);
                }
            }
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_findOmxFileJButtonActionPerformed

    /**
     * @see #cancelJButtonActionPerformed(java.awt.event.ActionEvent)
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cancelJButtonActionPerformed(null);
    }//GEN-LAST:event_formWindowClosing

    /**
     * Opens a frame containing the help manual for OMSSA Viewer File Selection.
     *
     * @param evt
     */
    private void helpJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpJButtonActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpWindow(this, getClass().getResource("/de/proteinms/omxparser/helpfiles/OmssaViewer.html"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_helpJButtonActionPerformed

    /**
     * Opens a frame containing the About OMSSA Viewer information.
     *
     * @param evt
     */
    private void aboutJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutJButtonActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpWindow(this, getClass().getResource("/de/proteinms/omxparser/helpfiles/AboutOmssaViewer.html"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_aboutJButtonActionPerformed

    /**
     * Closes the dialog and opens an OMMSA Viewer, or inserts the new file selections
     * if a Viewer is already created.
     *
     * @param evt
     */
    private void okJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okJButtonActionPerformed
        this.setVisible(false);

        String modsFile = null;
        String userModsFile = null;

        if (!modsFileJTextField.getText().equalsIgnoreCase("(optional)")) {
            modsFile = modsFileJTextField.getText();
        }

        if (!userModsJTextField.getText().equalsIgnoreCase("(optional)")) {
            userModsFile = userModsJTextField.getText();
        }

        if (parent != null) {
            parent.setVisible(true);
            ((OmssaViewer) parent).insertOmxFile(omxFileJTextField.getText(), modsFile, userModsFile, lastSelectedFolder);
        } else {
            new OmssaViewer(omxFileJTextField.getText(), modsFile, userModsFile, lastSelectedFolder);
        }

        this.dispose();
    }//GEN-LAST:event_okJButtonActionPerformed

    /**
     * If the dialog is opened from OMSSA Viewer, the dialog is simply closed.
     * If the dialog has no parent, the program is terminated.
     *
     * @param evt
     */
    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed
        if (parent == null) {
            System.exit(0);
        } else {
            if(!parent.isVisible()){
                System.exit(0);
            } else{
                this.setVisible(false);
                this.dispose();
            }
        }
    }//GEN-LAST:event_cancelJButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutJButton;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JPanel filesPanel;
    private javax.swing.JButton findModsFileJButton;
    private javax.swing.JButton findOmxFileJButton;
    private javax.swing.JButton findUserModsFileJButton;
    private javax.swing.JButton helpJButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField modsFileJTextField;
    private javax.swing.JButton okJButton;
    private javax.swing.JTextField omxFileJTextField;
    private javax.swing.JTextField userModsJTextField;
    // End of variables declaration//GEN-END:variables
}
