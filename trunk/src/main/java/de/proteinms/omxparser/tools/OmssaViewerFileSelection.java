package de.proteinms.omxparser.tools;

import java.awt.Frame;
import java.awt.Toolkit;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A dialog that lets you choose the files the to be viewed in the OMSSA Viewer.
 *
 * @author Harald Barsnes
 *
 * Created December 2008.
 */
public class OmssaViewerFileSelection extends javax.swing.JDialog {

    /**
     * The last selected folder. Defaults to user.home.
     */
    String lastSelectedFolder = "user.home";
    Frame parent;

    /**
     * Creates new OmssaViewerFileSelection dialog and makes it visible.
     *
     * @param parent the parent frame (the OMSSA Viewer)
     * @param modal
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

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().
                getResource("/de/proteinms/omxparser/icons/omssaviewer.GIF")));

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

        jLabel1 = new javax.swing.JLabel();
        omxFileJTextField = new javax.swing.JTextField();
        findUserModsFileJButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        modsFileJTextField = new javax.swing.JTextField();
        findModsFileJButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        userModsJTextField = new javax.swing.JTextField();
        findOmxFileJButton = new javax.swing.JButton();
        okJButton = new javax.swing.JButton();
        cancelJButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        helpJButton = new javax.swing.JButton();
        aboutJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("OMSSA Viewer - File Selection");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("Search Results File:");
        jLabel1.setToolTipText("The OMSSA omx file");

        omxFileJTextField.setEditable(false);
        omxFileJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        omxFileJTextField.setToolTipText("The OMSSA omx file");

        findUserModsFileJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/proteinms/omxparser/icons/Directory.gif"))); // NOI18N
        findUserModsFileJButton.setToolTipText("Select File");
        findUserModsFileJButton.setBorderPainted(false);
        findUserModsFileJButton.setContentAreaFilled(false);
        findUserModsFileJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findUserModsFileJButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Modifications File:");
        jLabel2.setToolTipText("The OMSSA mods.xml file");

        modsFileJTextField.setEditable(false);
        modsFileJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        modsFileJTextField.setText("(optional)");
        modsFileJTextField.setToolTipText("The OMSSA mods.xml file");

        findModsFileJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/proteinms/omxparser/icons/Directory.gif"))); // NOI18N
        findModsFileJButton.setToolTipText("Select File");
        findModsFileJButton.setBorderPainted(false);
        findModsFileJButton.setContentAreaFilled(false);
        findModsFileJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findModsFileJButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("User Modifications File:");
        jLabel3.setToolTipText("The OMSSA usermods.xml file");

        userModsJTextField.setEditable(false);
        userModsJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        userModsJTextField.setText("(optional)");
        userModsJTextField.setToolTipText("The OMSSA usermods.xml file");

        findOmxFileJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/proteinms/omxparser/icons/Directory.gif"))); // NOI18N
        findOmxFileJButton.setToolTipText("Select File");
        findOmxFileJButton.setBorderPainted(false);
        findOmxFileJButton.setContentAreaFilled(false);
        findOmxFileJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findOmxFileJButtonActionPerformed(evt);
            }
        });

        okJButton.setText("Ok");
        okJButton.setEnabled(false);
        okJButton.setMaximumSize(new java.awt.Dimension(80, 20));
        okJButton.setMinimumSize(new java.awt.Dimension(80, 20));
        okJButton.setPreferredSize(new java.awt.Dimension(80, 20));
        okJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okJButtonActionPerformed(evt);
            }
        });

        cancelJButton.setText("Cancel");
        cancelJButton.setMaximumSize(new java.awt.Dimension(80, 20));
        cancelJButton.setMinimumSize(new java.awt.Dimension(80, 20));
        cancelJButton.setPreferredSize(new java.awt.Dimension(80, 20));
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });

        jLabel4.setFont(jLabel4.getFont().deriveFont((jLabel4.getFont().getStyle() | java.awt.Font.ITALIC)));
        jLabel4.setText("Select the OMSSA omx file you want to view, the modification files (optional), and click OK.");

        helpJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/proteinms/omxparser/icons/help.GIF"))); // NOI18N
        helpJButton.setToolTipText("Help");
        helpJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpJButtonActionPerformed(evt);
            }
        });

        aboutJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/proteinms/omxparser/icons/omssaviewer16.GIF"))); // NOI18N
        aboutJButton.setToolTipText("About");
        aboutJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addContainerGap(132, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(modsFileJTextField)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(findModsFileJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(omxFileJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(findOmxFileJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(userModsJTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(findUserModsFileJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(helpJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(aboutJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 336, Short.MAX_VALUE)
                        .addComponent(okJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(findOmxFileJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(omxFileJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(modsFileJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(findModsFileJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(userModsJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(findUserModsFileJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cancelJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(okJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(helpJButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aboutJButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "XML file (*.xml)", "xml");
        chooser.setFileFilter(filter);

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

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "XML file (*.xml)", "xml");
        chooser.setFileFilter(filter);

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

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "OMSSA omx file (*.omx)", "omx");
        chooser.setFileFilter(filter);

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
            ((OmssaViewer) parent).insertOmxFile(omxFileJTextField.getText(), modsFile, userModsFile, lastSelectedFolder);
        } else {
            new OmssaViewer(omxFileJTextField.getText(), modsFile, userModsFile, lastSelectedFolder);
        }

        this.dispose();
    }//GEN-LAST:event_okJButtonActionPerformed

    /**
     * If the dialog is opened from the OMSSA Viewer, the dialog is simply closed.
     * If the dialog has no parent, the program is terminated.
     *
     * @param evt
     */
    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed
        if (parent == null) {
            System.exit(0);
        } else {
            this.setVisible(false);
            this.dispose();
        }
    }//GEN-LAST:event_cancelJButtonActionPerformed

    /**
     * See cancelJButtonActionPerformed
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutJButton;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JButton findModsFileJButton;
    private javax.swing.JButton findOmxFileJButton;
    private javax.swing.JButton findUserModsFileJButton;
    private javax.swing.JButton helpJButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField modsFileJTextField;
    private javax.swing.JButton okJButton;
    private javax.swing.JTextField omxFileJTextField;
    private javax.swing.JTextField userModsJTextField;
    // End of variables declaration//GEN-END:variables
}
