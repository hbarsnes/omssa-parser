package de.proteinms.omxparser.tools;

import be.proteomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import be.proteomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyKrupp;
import de.proteinms.omxparser.OmssaOmxFile;
import de.proteinms.omxparser.util.MSHitSet;
import de.proteinms.omxparser.util.MSHits;
import de.proteinms.omxparser.util.MSMZHit;
import de.proteinms.omxparser.util.MSModHit;
import de.proteinms.omxparser.util.MSPepHit;
import de.proteinms.omxparser.util.MSSpectrum;
import de.proteinms.omxparser.util.OmssaModification;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;

/**
 * A simply viewer for OMMSA omx files to show how the omssa-parser library
 * can be used.
 *
 * @author Harald Barsnes
 *
 * Created December 2008.
 */
public class OmssaViewer extends javax.swing.JFrame {

    private OmssaOmxFile omssaOmxFile;
    private ProgressDialog progressDialog;
    private SpectrumPanel spectrumPanel;
    private String omxFile,  modsFile,  userModsFile;
    private HashMap<Integer, ArrayList<Double>> allMzValues;
    private HashMap<Integer, ArrayList<Double>> allAbundanceValues;
    private HashMap<MSSpectrum, MSHitSet> spectrumHitSetMap;
    private HashMap<Integer, MSSpectrum> spectra;
    private Vector spectraJXTableColumnToolTips;
    private Vector spectrumJTableColumnToolTips;
    private Vector spectrumJXTableColumnToolTips;
    private Vector identificationsJXTableColumnToolTips;
    private HashMap<String, Vector<DefaultSpectrumAnnotation>> allAnnotations;
    /**
     * The MSSearchSettings_msmstol used in the omx file
     */
    private double ionCoverageErrorMargin;
    /**
     * The MSResponse_scale used in the omx file.
     */
    private int omssaResponseScale;
    /**
     * The list of ionstypes used in the omx file.
     */
    private List<Integer> usedIonTypes;
    /**
     * The ion coverage legend shown at the bottom of OMSSA Viewer.
     */
    private String ionCoverageLegend = "Ion Coverage: b-ions underlined, y-ions red font";
    /**
     * The last folder opened by the user. Defaults to user.home.
     */
    private String lastSelectedFolder = "user.home";
    /**
     * The version number of OMSSA Viewer. (Just a number. Not referenced in pom file.)
     */
    private String ommsaViewerVersion = "v1.5.0";
    /**
     * The version number of OMSSA Parser. Should be the same as the
     * version number in the pom file.
     */
    private static String ommsaParserVersion = "1.3.0";
    /**
     * If set to true all the output that is normally sent to the terminal will
     * be sent to a file called ErrorLog.txt in the Properties folder.
     */
    private static boolean useErrorLog = true;

    /**
     * First checks if a newer version of the omssa-parser is available,
     * then creates an error log file (if useErrorLog == true) and finally
     * opens the OmssaViewerFileSelection dialog.
     *
     * @param args
     */
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

                try {
                    PlasticLookAndFeel.setPlasticTheme(new SkyKrupp());
                    UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
                } catch (UnsupportedLookAndFeelException e) {
                    Util.writeToErrorLog("Error setting the look and feel: ");
                    e.printStackTrace();
                }

                // check if a newer version of the omssa-parser is available
                try {

                    boolean deprecatedOrDeleted = false;

                    URL downloadPage = new URL(
                            "http://code.google.com/p/omssa-parser/downloads/detail?name=omssaparser-" +
                            ommsaParserVersion + ".zip");

                    int respons =
                            ((java.net.HttpURLConnection) downloadPage.openConnection()).getResponseCode();

                    // 404 means that the file no longer exists, which means that
                    // the running version is no longer available for download,
                    // which again means that a never version is available.
                    if (respons == 404) {
                        deprecatedOrDeleted = true;
                    } else {

                        // also need to check if the available running version has been
                        // deprecated (but not deleted)
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(downloadPage.openStream()));

                        String inputLine;

                        while ((inputLine = in.readLine()) != null && !deprecatedOrDeleted) {
                            if (inputLine.lastIndexOf("Deprecated") != -1 &&
                                    inputLine.lastIndexOf("Deprecated Downloads") == -1 ||
                                    inputLine.lastIndexOf("Deprecated downloads") == -1) {
                                deprecatedOrDeleted = true;
                            //JOptionPane.showMessageDialog(null, "Deprecated!!!!");
                            }
                        }

                        in.close();
                    }

                    if (deprecatedOrDeleted) {
                        int option = JOptionPane.showConfirmDialog(null,
                                "A newer version of OMSSA Parser is available.\n" +
                                "Do you want to upgrade?\n\n" +
                                "Selecting \'Yes\' will open the OMSSA Parser web page\n" +
                                "where you can download the latest version.",
                                "OMSSA Parser - Upgrade Available",
                                JOptionPane.YES_NO_CANCEL_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            BareBonesBrowserLaunch.openURL("http://code.google.com/p/omssa-parser");
                            System.exit(0);
                        } else if (option == JOptionPane.CANCEL_OPTION) {
                            System.exit(0);
                        }
                    }
                } catch (MalformedURLException e) {
                    //e.printStackTrace();
                } catch (IOException e) {
                    //e.printStackTrace();
                }

                // creates the error log file
                if (useErrorLog) {
                    try {
                        String path = "" + this.getClass().getProtectionDomain().getCodeSource().getLocation();
                        path = path.substring(5, path.lastIndexOf("/")) + File.separator + "Properties/ErrorLog.txt";
                        path = path.replace("%20", " ");

                        File file = new File(path);
                        System.setOut(new java.io.PrintStream(new FileOutputStream(file, true)));
                        System.setErr(new java.io.PrintStream(new FileOutputStream(file, true)));

                        // creates a new error log file if it does not exist
                        if (!file.exists()) {
                            file.createNewFile();

                            FileWriter w = new FileWriter(file);
                            BufferedWriter bw = new BufferedWriter(w);

                            bw.close();
                            w.close();
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null,
                                "An error occured when creating the ErrorLog.\n" +
                                e.getMessage(),
                                "Error Creating ErrorLog",
                                JOptionPane.ERROR_MESSAGE);
                        System.out.println("Error when creating ErrorLog: ");
                        e.printStackTrace();
                    }
                }

                new OmssaViewerFileSelection(new OmssaViewer(), false, null, null, null, "user.home");
            }
        });
    }

    /**
     * Creates an empty non-visible OmssaViewer frame.
     */
    public OmssaViewer(){

        initComponents();

        setMinimumSize(new java.awt.Dimension(900, 600));

        // sets the frames icon image
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().
                getResource("/de/proteinms/omxparser/icons/omssaviewer.GIF")));

        // use scientific notation for the P- and E-values in the identification table
        identificationsJXTable.setDefaultRenderer(Float.class, new ScientificNumberTableCellRenderer());

        // sets the column sizes
        spectraJXTable.getColumn(" ").setMaxWidth(35);
        spectraJXTable.getColumn(" ").setMinWidth(35);
        spectraJXTable.getColumn("m/z").setMaxWidth(65);
        spectraJXTable.getColumn("m/z").setMinWidth(65);
        spectraJXTable.getColumn("Charge").setMaxWidth(65);
        spectraJXTable.getColumn("Charge").setMinWidth(65);
        spectraJXTable.getColumn("Identified").setMaxWidth(80);
        spectraJXTable.getColumn("Identified").setMinWidth(80);

        spectrumJXTable.getColumn(" ").setMaxWidth(35);
        spectrumJXTable.getColumn(" ").setMinWidth(35);

        identificationsJXTable.getColumn(" ").setMaxWidth(35);
        identificationsJXTable.getColumn(" ").setMinWidth(35);
        identificationsJXTable.getColumn("Start").setMaxWidth(45);
        identificationsJXTable.getColumn("Start").setMinWidth(45);
        identificationsJXTable.getColumn("End").setMaxWidth(45);
        identificationsJXTable.getColumn("End").setMinWidth(45);
        identificationsJXTable.getColumn("Exp. Mass").setMaxWidth(75);
        identificationsJXTable.getColumn("Exp. Mass").setMinWidth(75);
        identificationsJXTable.getColumn("Theo. Mass").setMaxWidth(75);
        identificationsJXTable.getColumn("Theo. Mass").setMinWidth(75);
        identificationsJXTable.getColumn("E-value").setMinWidth(75);
        identificationsJXTable.getColumn("E-value").setMaxWidth(75);
        identificationsJXTable.getColumn("P-value").setMinWidth(75);
        identificationsJXTable.getColumn("P-value").setMaxWidth(75);
        identificationsJXTable.getColumn("Accession").setPreferredWidth(10);

        // adds auto row sorters
//        spectraJTable.setAutoCreateRowSorter(true);
//        spectrumJTable.setAutoCreateRowSorter(true);
//        identificationsJTable.setAutoCreateRowSorter(true);

        // disables column reordering
        spectraJXTable.getTableHeader().setReorderingAllowed(false);
        spectrumJXTable.getTableHeader().setReorderingAllowed(false);
        identificationsJXTable.getTableHeader().setReorderingAllowed(false);

        // adds column header tooltips
        spectraJXTableColumnToolTips = new Vector();
        spectraJXTableColumnToolTips.add("Spectrum Number");
        spectraJXTableColumnToolTips.add("Spectrum File Name");
        spectraJXTableColumnToolTips.add("Precursor Mass Over Charge Ratio");
        spectraJXTableColumnToolTips.add("Precursor Charge");
        spectraJXTableColumnToolTips.add("Spectrum Identified");

        spectrumJTableColumnToolTips = new Vector();
        spectrumJTableColumnToolTips.add(null);
        spectrumJTableColumnToolTips.add("Mass Over Charge Ratio");
        spectrumJTableColumnToolTips.add("Abundance");

        spectrumJXTableColumnToolTips = new Vector();
        spectrumJXTableColumnToolTips.add(null);
        spectrumJXTableColumnToolTips.add("Mass Over Charge Ratio");
        spectrumJXTableColumnToolTips.add("Abundance");

        identificationsJXTableColumnToolTips = new Vector();
        identificationsJXTableColumnToolTips.add("Spectrum Number");
        identificationsJXTableColumnToolTips.add("Peptide Sequence");
        identificationsJXTableColumnToolTips.add("Modified Peptide Sequence");
        identificationsJXTableColumnToolTips.add("Peptide Start Index");
        identificationsJXTableColumnToolTips.add("Peptide End Index");
        identificationsJXTableColumnToolTips.add("Experimental Mass");
        identificationsJXTableColumnToolTips.add("Theoretical Mass");
        identificationsJXTableColumnToolTips.add("E-value");
        identificationsJXTableColumnToolTips.add("P-value");
        identificationsJXTableColumnToolTips.add("Protein Accession Number");
        identificationsJXTableColumnToolTips.add("Protein Description");

        setLocationRelativeTo(null);
    }

    /**
     * Creates new OmssaViewer frame, makes it visible and starts parsing the input files.
     *
     * @param aOmxFile the OMSSA omx file to parse
     * @param aModsFile the mods.xml file
     * @param aUserModsFile the usermods.xml file
     * @param lastSelectedFolder the last selected folder
     */
    public OmssaViewer(String aOmxFile, String aModsFile, String aUserModsFile, String lastSelectedFolder) {
        initComponents();

        setMinimumSize(new java.awt.Dimension(900, 600));
        
        // sets the frames icon image
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().
                getResource("/de/proteinms/omxparser/icons/omssaviewer.GIF")));

        // use scientific notation for the P- and E-values in the identification table
        identificationsJXTable.setDefaultRenderer(Float.class, new ScientificNumberTableCellRenderer());

        // sets the column sizes
        spectraJXTable.getColumn(" ").setMaxWidth(35);
        spectraJXTable.getColumn(" ").setMinWidth(35);
        spectraJXTable.getColumn("m/z").setMaxWidth(65);
        spectraJXTable.getColumn("m/z").setMinWidth(65);
        spectraJXTable.getColumn("Charge").setMaxWidth(65);
        spectraJXTable.getColumn("Charge").setMinWidth(65);
        spectraJXTable.getColumn("Identified").setMaxWidth(80);
        spectraJXTable.getColumn("Identified").setMinWidth(80);

        spectrumJXTable.getColumn(" ").setMaxWidth(35);
        spectrumJXTable.getColumn(" ").setMinWidth(35);

        identificationsJXTable.getColumn(" ").setMaxWidth(35);
        identificationsJXTable.getColumn(" ").setMinWidth(35);
        identificationsJXTable.getColumn("Start").setMaxWidth(45);
        identificationsJXTable.getColumn("Start").setMinWidth(45);
        identificationsJXTable.getColumn("End").setMaxWidth(45);
        identificationsJXTable.getColumn("End").setMinWidth(45);
        identificationsJXTable.getColumn("Exp. Mass").setMaxWidth(75);
        identificationsJXTable.getColumn("Exp. Mass").setMinWidth(75);
        identificationsJXTable.getColumn("Theo. Mass").setMaxWidth(75);
        identificationsJXTable.getColumn("Theo. Mass").setMinWidth(75);
        identificationsJXTable.getColumn("E-value").setMinWidth(75);
        identificationsJXTable.getColumn("E-value").setMaxWidth(75);
        identificationsJXTable.getColumn("P-value").setMinWidth(75);
        identificationsJXTable.getColumn("P-value").setMaxWidth(75);
        identificationsJXTable.getColumn("Accession").setPreferredWidth(10);

        // adds auto row sorters
//        spectraJTable.setAutoCreateRowSorter(true);
//        spectrumJTable.setAutoCreateRowSorter(true);
//        identificationsJTable.setAutoCreateRowSorter(true);

        // disables column reordering
        spectraJXTable.getTableHeader().setReorderingAllowed(false);
        spectrumJXTable.getTableHeader().setReorderingAllowed(false);
        identificationsJXTable.getTableHeader().setReorderingAllowed(false);

        // adds column header tooltips
        spectraJXTableColumnToolTips = new Vector();
        spectraJXTableColumnToolTips.add("Spectrum Number");
        spectraJXTableColumnToolTips.add("Spectrum File Name");
        spectraJXTableColumnToolTips.add("Precursor Mass Over Charge Ratio");
        spectraJXTableColumnToolTips.add("Precursor Charge");
        spectraJXTableColumnToolTips.add("Spectrum Identified");

        spectrumJTableColumnToolTips = new Vector();
        spectrumJTableColumnToolTips.add(null);
        spectrumJTableColumnToolTips.add("Mass Over Charge Ratio");
        spectrumJTableColumnToolTips.add("Abundance");

        spectrumJXTableColumnToolTips = new Vector();
        spectrumJXTableColumnToolTips.add(null);
        spectrumJXTableColumnToolTips.add("Mass Over Charge Ratio");
        spectrumJXTableColumnToolTips.add("Abundance");

        identificationsJXTableColumnToolTips = new Vector();
        identificationsJXTableColumnToolTips.add("Spectrum Number");
        identificationsJXTableColumnToolTips.add("Peptide Sequence");
        identificationsJXTableColumnToolTips.add("Modified Peptide Sequence");
        identificationsJXTableColumnToolTips.add("Peptide Start Index");
        identificationsJXTableColumnToolTips.add("Peptide End Index");
        identificationsJXTableColumnToolTips.add("Experimental Mass");
        identificationsJXTableColumnToolTips.add("Theoretical Mass");
        identificationsJXTableColumnToolTips.add("E-value");
        identificationsJXTableColumnToolTips.add("P-value");
        identificationsJXTableColumnToolTips.add("Protein Accession Number");
        identificationsJXTableColumnToolTips.add("Protein Description");

        setLocationRelativeTo(null);
        setVisible(true);

        insertOmxFile(aOmxFile, aModsFile, aUserModsFile, lastSelectedFolder);
    }

    /**
     * Parses the given omx file (and modification files) and inserts the details
     * into the OMSSA Viewer tables.
     *
     * @param aOmxFile the OMSSA omx file to parse
     * @param aModsFile the mods.xml file
     * @param aUserModsFile the usermods.xml file
     * @param lastSelectedFolder the last selected folder
     */
    public void insertOmxFile(String aOmxFile, String aModsFile, String aUserModsFile, String lastSelectedFolder) {

        setTitle("OMSSA Viewer " + ommsaViewerVersion + "  -  [" + new File(aOmxFile).getPath() + "]");

        this.lastSelectedFolder = lastSelectedFolder;

        omxFile = aOmxFile;
        modsFile = aModsFile;
        userModsFile = aUserModsFile;

        exportSelectedSpectrumJMenuItem.setEnabled(false);

        progressDialog = new ProgressDialog(this, true);

        final Thread t = new Thread(new Runnable() {

            public void run() {
                progressDialog.setTitle("Parsing OMX File. Please Wait.");
                progressDialog.setIntermidiate(true);
                progressDialog.setVisible(true);
            }
        }, "ProgressDialog");

        t.start();


        new Thread("ParserThread") {

            @Override
            public void run() {

                // turn off the auto row sorting
                spectraJXTable.setSortable(false);
//                spectraJTable.setRowSorter(null);
//                spectrumJTable.setRowSorter(null);
//                identificationsJTable.setRowSorter(null);

                // empty the tables and clear the spectrum panel
                while (((DefaultTableModel) spectraJXTable.getModel()).getRowCount() > 0) {
                    ((DefaultTableModel) spectraJXTable.getModel()).removeRow(0);
                }

                while (((DefaultTableModel) spectrumJXTable.getModel()).getRowCount() > 0) {
                    ((DefaultTableModel) spectrumJXTable.getModel()).removeRow(0);
                }

                while (((DefaultTableModel) identificationsJXTable.getModel()).getRowCount() > 0) {
                    ((DefaultTableModel) identificationsJXTable.getModel()).removeRow(0);
                }

                modificationDetailsJLabel.setText("");

                while (spectrumJPanel.getComponents().length > 0) {
                    spectrumJPanel.remove(0);
                }

                spectrumJPanel.validate();
                spectrumJPanel.repaint();

                // parses the file
                try {
                    omssaOmxFile = new OmssaOmxFile(omxFile, modsFile, userModsFile);
                } catch (OutOfMemoryError error) {
                    progressDialog.setVisible(false);
                    progressDialog.dispose();
                    Runtime.getRuntime().gc();
                    JOptionPane.showMessageDialog(null,
                            "The task used up all the available memory and had to be stopped.\n" +
                            "Memory boundaries are set in ../Properties/JavaOptions.txt.",
                            "Out of Memory Error",
                            JOptionPane.ERROR_MESSAGE);
                    Util.writeToErrorLog("OMSSA Viewer: Ran out of memory!");
                    error.printStackTrace();
                    System.exit(0);
                }


                // extract MSSearchSettings_msmstol
                ionCoverageErrorMargin =
                        omssaOmxFile.getParserResult().MSSearch_request.MSRequest.get(0).MSRequest_settings.MSSearchSettings.MSSearchSettings_msmstol;

                // extract MSResponse_scale
                omssaResponseScale =
                        omssaOmxFile.getParserResult().MSSearch_response.MSResponse.get(0).MSResponse_scale;

                // extract the ion types used
                usedIonTypes =
                        omssaOmxFile.getParserResult().MSSearch_request.MSRequest.get(0).MSRequest_settings.MSSearchSettings.MSSearchSettings_ionstosearch.MSIonType;

                // iterate the spectrum to hitset map
                // extract and store details about the spectra
                spectrumHitSetMap = omssaOmxFile.getSpectrumToHitSetMap();
                Iterator<MSSpectrum> iterator = spectrumHitSetMap.keySet().iterator();

                allMzValues = new HashMap<Integer, ArrayList<Double>>();
                allAbundanceValues = new HashMap<Integer, ArrayList<Double>>();
                spectra = new HashMap<Integer, MSSpectrum>();

                while (iterator.hasNext()) {

                    MSSpectrum tempSpectrum = iterator.next();

                    spectra.put(new Integer(tempSpectrum.MSSpectrum_number), tempSpectrum);

                    // OMSSA question: possible with more than one file name per spectrum??
                    String fileName = tempSpectrum.MSSpectrum_ids.MSSpectrum_ids_E.get(0);

                    // extract the charge of the precursor
                    String chargeString = "0";

                    // if more than one charge is found the charge is considered as unknown and is set to 0
                    if (tempSpectrum.MSSpectrum_charge.MSSpectrum_charge_E.size() == 1) {
                        chargeString = "" + tempSpectrum.MSSpectrum_charge.MSSpectrum_charge_E.get(0);
                    }

                    chargeString = chargeString.replaceFirst("\\+", "");

                    int omssaAbundanceScale = tempSpectrum.MSSpectrum_iscale;

                    ArrayList<Double> currentRealMzValues = new ArrayList();
                    ArrayList<Double> currentRealAbundanceValues = new ArrayList();

                    List<Integer> currentMzValuesAsIntegers = tempSpectrum.MSSpectrum_mz.MSSpectrum_mz_E;
                    List<Integer> currentAbundanceValuesAsIntegers = tempSpectrum.MSSpectrum_abundance.MSSpectrum_abundance_E;

                    for (int i = 0; i < currentMzValuesAsIntegers.size(); i++) {
                        currentRealMzValues.add(currentMzValuesAsIntegers.get(i).doubleValue() / omssaResponseScale);
                        currentRealAbundanceValues.add(currentAbundanceValuesAsIntegers.get(i).doubleValue() / omssaAbundanceScale);
                    }

                    allMzValues.put(new Integer(tempSpectrum.MSSpectrum_number),
                            currentRealMzValues);
                    allAbundanceValues.put(new Integer(tempSpectrum.MSSpectrum_number),
                            currentRealAbundanceValues);


                    boolean identified = false;

                    MSHitSet msHitSet = spectrumHitSetMap.get(tempSpectrum);
                    List<MSHits> allMSHits = msHitSet.MSHitSet_hits.MSHits;

                    if (allMSHits.size() > 0) {
                        identified = true;
                    }

                    ((DefaultTableModel) spectraJXTable.getModel()).addRow(new Object[]{
                                new Integer(tempSpectrum.MSSpectrum_number),
                                fileName,
                                ((double) tempSpectrum.MSSpectrum_precursormz) / omssaResponseScale,
                                chargeString,
                                new Boolean(identified)
                            });
                }

                // switch the auto row sorting back on
                spectraJXTable.setSortable(true);
//                spectraJTable.setAutoCreateRowSorter(true);
//                spectrumJTable.setAutoCreateRowSorter(true);
//                identificationsJTable.setAutoCreateRowSorter(true);

                progressDialog.setVisible(false);
                progressDialog.dispose();
            }
        }.start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        copySpectraJPopupMenu = new javax.swing.JPopupMenu();
        copySpectraJMenuItem = new javax.swing.JMenuItem();
        copySpectrumJPopupMenu = new javax.swing.JPopupMenu();
        copySpectrumJMenuItem = new javax.swing.JMenuItem();
        copyIdentificationsJPopupMenu = new javax.swing.JPopupMenu();
        copyIdentificationsJMenuItem = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        spectraJXTable = new JXTable() {
            protected JXTableHeader createDefaultTableHeader() {
                return new JXTableHeader(columnModel) {
                    public String getToolTipText(MouseEvent e) {
                        String tip = null;
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        int realIndex = columnModel.getColumn(index).getModelIndex();
                        tip = (String) spectraJXTableColumnToolTips.get(realIndex);
                        return tip;
                    }
                };
            }
        };
        jPanel2 = new javax.swing.JPanel();
        modificationDetailsJLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        identificationsJXTable = new JXTable() {
            protected JXTableHeader createDefaultTableHeader() {
                return new JXTableHeader(columnModel) {
                    public String getToolTipText(MouseEvent e) {
                        String tip = null;
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        int realIndex = columnModel.getColumn(index).getModelIndex();
                        tip = (String) identificationsJXTableColumnToolTips.get(realIndex);
                        return tip;
                    }
                };
            }
        };
        jPanel3 = new javax.swing.JPanel();
        spectrumJPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        aIonsJCheckBox = new javax.swing.JCheckBox();
        bIonsJCheckBox = new javax.swing.JCheckBox();
        cIonsJCheckBox = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        yIonsJCheckBox = new javax.swing.JCheckBox();
        xIonsJCheckBox = new javax.swing.JCheckBox();
        zIonsJCheckBox = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        chargeOneJCheckBox = new javax.swing.JCheckBox();
        chargeTwoJCheckBox = new javax.swing.JCheckBox();
        chargeOverTwoJCheckBox = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        spectrumJXTable = new JXTable() {
            protected JXTableHeader createDefaultTableHeader() {
                return new JXTableHeader(columnModel) {
                    public String getToolTipText(MouseEvent e) {
                        String tip = null;
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        int realIndex = columnModel.getColumn(index).getModelIndex();
                        tip = (String) spectrumJXTableColumnToolTips.get(realIndex);
                        return tip;
                    }
                };
            }
        };
        jMenuBar1 = new javax.swing.JMenuBar();
        fileJMenu = new javax.swing.JMenu();
        openJMenuItem = new javax.swing.JMenuItem();
        exitJMenuItem = new javax.swing.JMenuItem();
        exportJMenu = new javax.swing.JMenu();
        exportSpectraFilesTableJMenuItem = new javax.swing.JMenuItem();
        exportAllIdentificationsJMenuItem = new javax.swing.JMenuItem();
        exportSelectedSpectrumJMenuItem = new javax.swing.JMenuItem();
        exportAllSpectraJMenuItem = new javax.swing.JMenuItem();
        helpJMenu = new javax.swing.JMenu();
        helpJMenuItem = new javax.swing.JMenuItem();
        aboutJMenuItem = new javax.swing.JMenuItem();

        copySpectraJMenuItem.setMnemonic('C');
        copySpectraJMenuItem.setText("Copy");
        copySpectraJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copySpectraJMenuItemActionPerformed(evt);
            }
        });
        copySpectraJPopupMenu.add(copySpectraJMenuItem);

        copySpectrumJMenuItem.setMnemonic('C');
        copySpectrumJMenuItem.setText("Copy");
        copySpectrumJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copySpectrumJMenuItemActionPerformed(evt);
            }
        });
        copySpectrumJPopupMenu.add(copySpectrumJMenuItem);

        copyIdentificationsJMenuItem.setMnemonic('C');
        copyIdentificationsJMenuItem.setText("Copy");
        copyIdentificationsJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyIdentificationsJMenuItemActionPerformed(evt);
            }
        });
        copyIdentificationsJPopupMenu.add(copyIdentificationsJMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("OMSSA Viewer v1.0");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Spectra Files", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N

        spectraJXTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                " ", "Filename", "m/z", "Charge", "Identified"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spectraJXTable.setOpaque(false);
        spectraJXTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                spectraJXTableKeyReleased(evt);
            }
        });
        spectraJXTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spectraJXTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(spectraJXTable);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Identifications", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N

        modificationDetailsJLabel.setFont(modificationDetailsJLabel.getFont().deriveFont((modificationDetailsJLabel.getFont().getStyle() | java.awt.Font.ITALIC)));

        jLabel1.setFont(jLabel1.getFont().deriveFont((jLabel1.getFont().getStyle() | java.awt.Font.ITALIC)));
        jLabel1.setText("Legend:   ");

        identificationsJXTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                " ", "Sequence", "Modified Sequence", "Start", "End", "Exp. Mass", "Theo. Mass", "E-value", "P-value", "Accession", "Description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        identificationsJXTable.setOpaque(false);
        identificationsJXTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                identificationsJXTableKeyReleased(evt);
            }
        });
        identificationsJXTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                identificationsJXTableMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(identificationsJXTable);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(modificationDetailsJLabel))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1146, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(modificationDetailsJLabel)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Spectrum", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N

        spectrumJPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        spectrumJPanel.setLayout(new javax.swing.BoxLayout(spectrumJPanel, javax.swing.BoxLayout.LINE_AXIS));

        aIonsJCheckBox.setSelected(true);
        aIonsJCheckBox.setText("a");
        aIonsJCheckBox.setToolTipText("Show a-ions");
        aIonsJCheckBox.setMaximumSize(new java.awt.Dimension(39, 23));
        aIonsJCheckBox.setMinimumSize(new java.awt.Dimension(39, 23));
        aIonsJCheckBox.setPreferredSize(new java.awt.Dimension(39, 23));
        aIonsJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aIonsJCheckBoxActionPerformed(evt);
            }
        });

        bIonsJCheckBox.setSelected(true);
        bIonsJCheckBox.setText("b");
        bIonsJCheckBox.setToolTipText("Show b-ions");
        bIonsJCheckBox.setMaximumSize(new java.awt.Dimension(39, 23));
        bIonsJCheckBox.setMinimumSize(new java.awt.Dimension(39, 23));
        bIonsJCheckBox.setPreferredSize(new java.awt.Dimension(39, 23));
        bIonsJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bIonsJCheckBoxActionPerformed(evt);
            }
        });

        cIonsJCheckBox.setSelected(true);
        cIonsJCheckBox.setText("c");
        cIonsJCheckBox.setToolTipText("Show c-ions");
        cIonsJCheckBox.setMaximumSize(new java.awt.Dimension(39, 23));
        cIonsJCheckBox.setMinimumSize(new java.awt.Dimension(39, 23));
        cIonsJCheckBox.setPreferredSize(new java.awt.Dimension(39, 23));
        cIonsJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cIonsJCheckBoxActionPerformed(evt);
            }
        });

        yIonsJCheckBox.setSelected(true);
        yIonsJCheckBox.setText("y");
        yIonsJCheckBox.setToolTipText("Show y-ions");
        yIonsJCheckBox.setMaximumSize(new java.awt.Dimension(39, 23));
        yIonsJCheckBox.setMinimumSize(new java.awt.Dimension(39, 23));
        yIonsJCheckBox.setPreferredSize(new java.awt.Dimension(39, 23));
        yIonsJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yIonsJCheckBoxActionPerformed(evt);
            }
        });

        xIonsJCheckBox.setSelected(true);
        xIonsJCheckBox.setText("x");
        xIonsJCheckBox.setToolTipText("Show x-ions");
        xIonsJCheckBox.setMaximumSize(new java.awt.Dimension(39, 23));
        xIonsJCheckBox.setMinimumSize(new java.awt.Dimension(39, 23));
        xIonsJCheckBox.setPreferredSize(new java.awt.Dimension(39, 23));
        xIonsJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xIonsJCheckBoxActionPerformed(evt);
            }
        });

        zIonsJCheckBox.setSelected(true);
        zIonsJCheckBox.setText("z");
        zIonsJCheckBox.setToolTipText("Show z-ions");
        zIonsJCheckBox.setMaximumSize(new java.awt.Dimension(39, 23));
        zIonsJCheckBox.setMinimumSize(new java.awt.Dimension(39, 23));
        zIonsJCheckBox.setPreferredSize(new java.awt.Dimension(39, 23));
        zIonsJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zIonsJCheckBoxActionPerformed(evt);
            }
        });

        chargeOneJCheckBox.setSelected(true);
        chargeOneJCheckBox.setText("+");
        chargeOneJCheckBox.setToolTipText("Show ions with charge 1");
        chargeOneJCheckBox.setMaximumSize(new java.awt.Dimension(39, 23));
        chargeOneJCheckBox.setMinimumSize(new java.awt.Dimension(39, 23));
        chargeOneJCheckBox.setPreferredSize(new java.awt.Dimension(39, 23));
        chargeOneJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chargeOneJCheckBoxActionPerformed(evt);
            }
        });

        chargeTwoJCheckBox.setSelected(true);
        chargeTwoJCheckBox.setText("++");
        chargeTwoJCheckBox.setToolTipText("Show ions with charge 2");
        chargeTwoJCheckBox.setMaximumSize(new java.awt.Dimension(39, 23));
        chargeTwoJCheckBox.setMinimumSize(new java.awt.Dimension(39, 23));
        chargeTwoJCheckBox.setPreferredSize(new java.awt.Dimension(39, 23));
        chargeTwoJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chargeTwoJCheckBoxActionPerformed(evt);
            }
        });

        chargeOverTwoJCheckBox.setSelected(true);
        chargeOverTwoJCheckBox.setText(">2");
        chargeOverTwoJCheckBox.setToolTipText("Show ions with charge >2");
        chargeOverTwoJCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chargeOverTwoJCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(yIonsJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                        .add(chargeOneJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                        .add(2, 2, 2))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, zIonsJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .add(xIonsJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, chargeTwoJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, chargeOverTwoJCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                        .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(bIonsJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .add(aIonsJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .add(cIonsJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(aIonsJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(bIonsJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cIonsJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 13, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(xIonsJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(yIonsJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(zIonsJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(5, 5, 5)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
                .add(3, 3, 3)
                .add(chargeOneJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chargeTwoJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chargeOverTwoJCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(13, 13, 13))
        );

        spectrumJXTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                " ", "m/z", "Abundance"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spectrumJXTable.setOpaque(false);
        spectrumJXTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spectrumJXTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(spectrumJXTable);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(spectrumJPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(spectrumJPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                .addContainerGap())
        );

        fileJMenu.setMnemonic('F');
        fileJMenu.setText("File");

        openJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        openJMenuItem.setMnemonic('O');
        openJMenuItem.setText("Open");
        openJMenuItem.setToolTipText("Open a New OMX File");
        openJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(openJMenuItem);

        exitJMenuItem.setMnemonic('x');
        exitJMenuItem.setText("Exit");
        exitJMenuItem.setToolTipText("Exit OMSSA Viewer");
        exitJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitJMenuItemActionPerformed(evt);
            }
        });
        fileJMenu.add(exitJMenuItem);

        jMenuBar1.add(fileJMenu);

        exportJMenu.setMnemonic('E');
        exportJMenu.setText("Export");

        exportSpectraFilesTableJMenuItem.setMnemonic('P');
        exportSpectraFilesTableJMenuItem.setText("Spectra Files Table");
        exportSpectraFilesTableJMenuItem.setToolTipText("Export the Spectra Files Table as Tab Delimited Text File");
        exportSpectraFilesTableJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportSpectraFilesTableJMenuItemActionPerformed(evt);
            }
        });
        exportJMenu.add(exportSpectraFilesTableJMenuItem);

        exportAllIdentificationsJMenuItem.setMnemonic('I');
        exportAllIdentificationsJMenuItem.setText("All Identifications");
        exportAllIdentificationsJMenuItem.setToolTipText("Export All Identifications as Tab Delimited Text File");
        exportAllIdentificationsJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportAllIdentificationsJMenuItemActionPerformed(evt);
            }
        });
        exportJMenu.add(exportAllIdentificationsJMenuItem);

        exportSelectedSpectrumJMenuItem.setMnemonic('S');
        exportSelectedSpectrumJMenuItem.setText("Selected Spectrum");
        exportSelectedSpectrumJMenuItem.setToolTipText("Export the Selected Spectrum as Tab Delimited Text File");
        exportSelectedSpectrumJMenuItem.setEnabled(false);
        exportSelectedSpectrumJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportSelectedSpectrumJMenuItemActionPerformed(evt);
            }
        });
        exportJMenu.add(exportSelectedSpectrumJMenuItem);

        exportAllSpectraJMenuItem.setMnemonic('S');
        exportAllSpectraJMenuItem.setText("All Spectra");
        exportAllSpectraJMenuItem.setToolTipText("Export all the Spectra as DTA Files");
        exportAllSpectraJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportAllSpectraJMenuItemActionPerformed(evt);
            }
        });
        exportJMenu.add(exportAllSpectraJMenuItem);

        jMenuBar1.add(exportJMenu);

        helpJMenu.setMnemonic('H');
        helpJMenu.setText("Help");

        helpJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        helpJMenuItem.setMnemonic('H');
        helpJMenuItem.setText("Help");
        helpJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpJMenuItemActionPerformed(evt);
            }
        });
        helpJMenu.add(helpJMenuItem);

        aboutJMenuItem.setMnemonic('a');
        aboutJMenuItem.setText("About");
        aboutJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutJMenuItemActionPerformed(evt);
            }
        });
        helpJMenu.add(aboutJMenuItem);

        jMenuBar1.add(helpJMenu);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Terminates the program.
     *
     * @param evt
     */
    private void exitJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitJMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitJMenuItemActionPerformed

    /**
     * When the user selects a row in the spectra files table, the identifications (if any)
     * are inserted in the identification table at the bottom of the frame, and the spectrum
     * details are shown in the spectrum panel and table to the right.
     *
     * @param evt
     */
    
    /**
     * Enables copy to clipboard functionality from a popup menu.
     *
     * @param evt
     */
    private void copySpectraJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copySpectraJMenuItemActionPerformed
        TransferHandler th = spectraJXTable.getTransferHandler();
        if (th != null) {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            th.exportToClipboard(spectraJXTable, cb, TransferHandler.COPY);
        }
}//GEN-LAST:event_copySpectraJMenuItemActionPerformed

    /**
     * See copySpectraJMenuItemActionPerformed
     */
    private void copySpectrumJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copySpectrumJMenuItemActionPerformed
        TransferHandler th = spectrumJXTable.getTransferHandler();
        if (th != null) {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            th.exportToClipboard(spectrumJXTable, cb, TransferHandler.COPY);
        }
}//GEN-LAST:event_copySpectrumJMenuItemActionPerformed

    /**
     * See copySpectraJMenuItemActionPerformed
     */
    private void copyIdentificationsJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyIdentificationsJMenuItemActionPerformed
        TransferHandler th = identificationsJXTable.getTransferHandler();
        if (th != null) {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            th.exportToClipboard(identificationsJXTable, cb, TransferHandler.COPY);
        }
}//GEN-LAST:event_copyIdentificationsJMenuItemActionPerformed

    /**
     * Filters the annotations and returns the annotations matching the selected
     * list next to the spectrum panel.
     * 
     * @param annotations the annotations to be filtered
     * @return the filtered annotations
     */
    private Vector<DefaultSpectrumAnnotation> filterAnnotations(Vector<DefaultSpectrumAnnotation> annotations) {

        Vector<DefaultSpectrumAnnotation> filteredAnnotations = new Vector();

        for (int i = 0; i < annotations.size(); i++) {
            String currentLabel = annotations.get(i).getLabel();

            boolean useAnnotation = true;

            // check ion type
            if (currentLabel.lastIndexOf("a") != -1) {
                if (!aIonsJCheckBox.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("b") != -1) {
                if (!bIonsJCheckBox.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("c") != -1) {
                if (!cIonsJCheckBox.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("x") != -1) {
                if (!xIonsJCheckBox.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("y") != -1) {
                if (!yIonsJCheckBox.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("z") != -1) {
                if (!zIonsJCheckBox.isSelected()) {
                    useAnnotation = false;
                }
            }

            // check ion charge
            if (useAnnotation) {
                if (currentLabel.lastIndexOf("+") == -1) {
                    if (!chargeOneJCheckBox.isSelected()) {
                        useAnnotation = false;
                    }
                } else if (currentLabel.lastIndexOf("+++") != -1) {
                    if (!chargeOverTwoJCheckBox.isSelected()) {
                        useAnnotation = false;
                    }
                } else if (currentLabel.lastIndexOf("++") != -1) {
                    if (!chargeTwoJCheckBox.isSelected()) {
                        useAnnotation = false;
                    }
                }
            }

            if (useAnnotation) {
                filteredAnnotations.add(annotations.get(i));
            }
        }

        return filteredAnnotations;
    }

    /**
     * Opens a OmssaViewerFileSelection dialog for opening a different omx file.
     *
     * @param evt
     */
    private void openJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openJMenuItemActionPerformed
        new OmssaViewerFileSelection(this, false, omxFile, modsFile, userModsFile, lastSelectedFolder);
    }//GEN-LAST:event_openJMenuItemActionPerformed

    /**
     * Export the contents of the spectra files table to a tab delimited text file.
     *
     * @param evt
     */
    private void exportSpectraFilesTableJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportSpectraFilesTableJMenuItemActionPerformed
        JFileChooser chooser = new JFileChooser(lastSelectedFolder);
        chooser.setFileFilter(new TxtFileFilter());
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle("Export Spectra File Details");

        File selectedFile;

        int returnVal = chooser.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            selectedFile = chooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
            }

            while (selectedFile.exists()) {
                int option = JOptionPane.showConfirmDialog(this,
                        "The  file " + chooser.getSelectedFile().getName() +
                        " already exists. Do you want to replace this file?",
                        "Replace File?", JOptionPane.YES_NO_CANCEL_OPTION);

                if (option == JOptionPane.NO_OPTION) {
                    chooser = new JFileChooser(lastSelectedFolder);
                    chooser.setFileFilter(new TxtFileFilter());
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setDialogTitle("Export Spectra File Details");

                    returnVal = chooser.showSaveDialog(this);

                    if (returnVal == JFileChooser.CANCEL_OPTION) {
                        return;
                    } else {
                        selectedFile = chooser.getSelectedFile();

                        if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                            selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
                        }
                    }
                } else { // YES option
                    break;
                }
            }

            this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

            try {

                selectedFile = chooser.getSelectedFile();

                if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
                }

                if (selectedFile.exists()) {
                    selectedFile.delete();
                }

                selectedFile.createNewFile();

                FileWriter f = new FileWriter(selectedFile);

                // add the column headers
                for (int j = 0; j < spectraJXTable.getColumnCount() - 1; j++) {
                    f.write(spectraJXTable.getColumnName(j) + "\t");
                }

                f.write(spectraJXTable.getColumnName(spectraJXTable.getColumnCount() - 1) + "\n");

                // add the table contents
                for (int i = 0; i < spectraJXTable.getRowCount(); i++) {
                    for (int j = 0; j < spectraJXTable.getColumnCount() - 1; j++) {
                        f.write(spectraJXTable.getValueAt(i, j) + "\t");
                    }

                    f.write(spectraJXTable.getValueAt(i, spectraJXTable.getColumnCount() - 1) + "\n");
                }

                f.close();

                lastSelectedFolder = selectedFile.getPath();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "An error occured when exporting the spectra file details.\n" +
                        "See ../Properties/ErrorLog.txt for more details.",
                        "Error Exporting Spectra Files",
                        JOptionPane.ERROR_MESSAGE);
                Util.writeToErrorLog("Error when exporting spectra file details: ");
                ex.printStackTrace();
            }

            this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        }
}//GEN-LAST:event_exportSpectraFilesTableJMenuItemActionPerformed

    /**
     * Export the contents of the identification table to a tab delimited text file.
     *
     * @param evt
     */
    private void exportSelectedSpectrumJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportSelectedSpectrumJMenuItemActionPerformed
        JFileChooser chooser = new JFileChooser(lastSelectedFolder);
        chooser.setFileFilter(new TxtFileFilter());
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle("Export Selected Spectrum");

        File selectedFile;

        int returnVal = chooser.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            selectedFile = chooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
            }

            while (selectedFile.exists()) {
                int option = JOptionPane.showConfirmDialog(this,
                        "The  file " + chooser.getSelectedFile().getName() +
                        " already exists. Do you want to replace this file?",
                        "Replace File?", JOptionPane.YES_NO_CANCEL_OPTION);

                if (option == JOptionPane.NO_OPTION) {
                    chooser = new JFileChooser(lastSelectedFolder);
                    chooser.setFileFilter(new TxtFileFilter());
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setDialogTitle("Export Selected Spectrum");

                    returnVal = chooser.showSaveDialog(this);

                    if (returnVal == JFileChooser.CANCEL_OPTION) {
                        return;
                    } else {
                        selectedFile = chooser.getSelectedFile();

                        if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                            selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
                        }
                    }
                } else { // YES option
                    break;
                }
            }

            this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

            try {

                selectedFile = chooser.getSelectedFile();

                if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
                }

                if (selectedFile.exists()) {
                    selectedFile.delete();
                }

                selectedFile.createNewFile();

                FileWriter f = new FileWriter(selectedFile);

                // add the column headers
                for (int j = 0; j < spectrumJXTable.getColumnCount() - 1; j++) {
                    f.write(spectrumJXTable.getColumnName(j) + "\t");
                }

                f.write(spectrumJXTable.getColumnName(spectrumJXTable.getColumnCount() - 1) + "\n");

                // add the table contents
                for (int i = 0; i < spectrumJXTable.getRowCount(); i++) {
                    for (int j = 0; j < spectrumJXTable.getColumnCount() - 1; j++) {
                        f.write(spectrumJXTable.getValueAt(i, j) + "\t");
                    }

                    f.write(spectrumJXTable.getValueAt(i, spectrumJXTable.getColumnCount() - 1) + "\n");
                }

                f.close();

                lastSelectedFolder = selectedFile.getPath();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "An error occured when exporting the selected spectrum.\n" +
                        "See ../Properties/ErrorLog.txt for more details.",
                        "Error Exporting Selected Spectrum",
                        JOptionPane.ERROR_MESSAGE);
                Util.writeToErrorLog("Error when exporting selected spectrum: ");
                ex.printStackTrace();
            }

            this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        }
}//GEN-LAST:event_exportSelectedSpectrumJMenuItemActionPerformed

    /**
     * Export all identifications to a tab delimited text file.
     *
     * @param evt
     */
    private void exportAllIdentificationsJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportAllIdentificationsJMenuItemActionPerformed
        JFileChooser chooser = new JFileChooser(lastSelectedFolder);
        chooser.setFileFilter(new TxtFileFilter());
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle("Export All Identifications");

        File selectedFile;

        int returnVal = chooser.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            selectedFile = chooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
            }

            while (selectedFile.exists()) {
                int option = JOptionPane.showConfirmDialog(this,
                        "The  file " + chooser.getSelectedFile().getName() +
                        " already exists. Do you want to replace this file?",
                        "Replace File?", JOptionPane.YES_NO_CANCEL_OPTION);

                if (option == JOptionPane.NO_OPTION) {
                    chooser = new JFileChooser(lastSelectedFolder);
                    chooser.setFileFilter(new TxtFileFilter());
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setDialogTitle("Export All Identifications");

                    returnVal = chooser.showSaveDialog(this);

                    if (returnVal == JFileChooser.CANCEL_OPTION) {
                        return;
                    } else {
                        selectedFile = chooser.getSelectedFile();

                        if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                            selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
                        }
                    }
                } else { // YES option
                    break;
                }
            }

            this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

            try {

                selectedFile = chooser.getSelectedFile();

                if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
                }

                if (selectedFile.exists()) {
                    selectedFile.delete();
                }

                selectedFile.createNewFile();

                FileWriter f = new FileWriter(selectedFile);

                // add the column headers
                for (int j = 0; j < identificationsJXTable.getColumnCount() - 1; j++) {

                    if (j == 2) {
                        f.write("Modified Sequence" + "\t");
                        f.write("Ion Coverage" + "\t");
                    } else {
                        f.write(identificationsJXTable.getColumnName(j) + "\t");
                    }
                }

                f.write(identificationsJXTable.getColumnName(identificationsJXTable.getColumnCount() - 1) + "\n");


                // add the identification details

                // get the list of fixed modifications
                List<Integer> fixedModifications =
                        omssaOmxFile.getParserResult().MSSearch_request.MSRequest.get(0).MSRequest_settings.MSSearchSettings.MSSearchSettings_fixed.MSMod;


                // iterate all the identifications and print them to the file

                Iterator<MSSpectrum> iterator = spectrumHitSetMap.keySet().iterator();

                while (iterator.hasNext()) {

                    MSHitSet msHitSet = spectrumHitSetMap.get(iterator.next());

                    List<MSHits> allMSHits = msHitSet.MSHitSet_hits.MSHits;

                    Iterator<MSHits> msHitIterator = allMSHits.iterator();

                    while (msHitIterator.hasNext()) {

                        MSHits tempMSHit = msHitIterator.next();

                        String sequence = tempMSHit.MSHits_pepstring;
                        String[] modifications = new String[sequence.length()];

                        for (int i = 0; i < modifications.length; i++) {
                            modifications[i] = "";
                        }

                        String modifiedSequence = "";
                        String nTerminal = "NH2-";
                        String cTerminal = "-COOH";

                        // handle modifications
                        if (omssaOmxFile.getModifications().size() > 0) {

                            if (fixedModifications.size() > 0) {

                                for (int i = 0; i < fixedModifications.size(); i++) {

                                    Vector<String> modifiedResidues =
                                            omssaOmxFile.getModifications().get(fixedModifications.get(i)).getModResidues();

                                    for (int j = 0; j < modifiedResidues.size(); j++) {

                                        int index = sequence.indexOf(modifiedResidues.get(j));

                                        while (index != -1) {

                                            modifications[index] +=
                                                    "<" + omssaOmxFile.getModifications().get(fixedModifications.get(i)).getModNumber() + ">";

                                            index = sequence.indexOf(modifiedResidues.get(j), index + 1);
                                        }
                                    }
                                }
                            }

                            // variable modifications
                            Iterator<MSModHit> modsIterator = tempMSHit.MSHits_mods.MSModHit.iterator();

                            while (modsIterator.hasNext()) {

                                MSModHit currentMSModHit = modsIterator.next();

                                modifications[currentMSModHit.MSModHit_site] +=
                                        "<" + currentMSModHit.MSModHit_modtype.MSMod + ">";
                            }

                            String modificationDetails = "";

                            for (int i = 0; i < modifications.length; i++) {

                                if (!modifications[i].equalsIgnoreCase("")) {

                                    if (modificationDetails.lastIndexOf(modifications[i]) == -1) {

                                        OmssaModification tempOmssaModification = omssaOmxFile.getModifications().get(
                                                new Integer(modifications[i].substring(1, modifications[i].length() - 1)));

                                        if (tempOmssaModification != null) {

                                            modificationDetails += modifications[i] + " " + tempOmssaModification.getModName() +
                                                    " (" + tempOmssaModification.getModMonoMass() + "), ";


                                            if (tempOmssaModification.getModType() == OmssaModification.MODAA) {

                                                // "normal" modification
                                                modifiedSequence += sequence.substring(i, i + 1) + modifications[i];
                                            } else if (tempOmssaModification.getModType() == OmssaModification.MODN || tempOmssaModification.getModType() == OmssaModification.MODNAA || tempOmssaModification.getModType() == OmssaModification.MODNP || tempOmssaModification.getModType() == OmssaModification.MODNPAA) {

                                                // n-terminal modification
                                                nTerminal = modifications[i] + "-";
                                                modifiedSequence += sequence.substring(i, i + 1);
                                            } else if (tempOmssaModification.getModType() == OmssaModification.MODC || tempOmssaModification.getModType() == OmssaModification.MODCAA || tempOmssaModification.getModType() == OmssaModification.MODCP || tempOmssaModification.getModType() == OmssaModification.MODCPAA) {

                                                // c-terminal modification
                                                cTerminal = "-" + modifications[i];
                                                modifiedSequence += sequence.substring(i, i + 1);
                                            }
                                        } else {
                                            modifiedSequence += sequence.substring(i, i + 1) + modifications[i];
                                            modificationDetails += modifications[i] + " unknown, ";
                                        }
                                    } else {
                                        modifiedSequence += sequence.substring(i, i + 1) + modifications[i];
                                    }
                                } else {
                                    modifiedSequence += sequence.substring(i, i + 1) + modifications[i];
                                }
                            }

                            if (modificationDetails.endsWith(", ")) {
                                modificationDetails = modificationDetails.substring(0, modificationDetails.length() - 2);
                            }

                            if (modificationDetails.length() > 0) {
                                modificationDetailsJLabel.setText("Modifications: " + modificationDetails +
                                        "    /    " + ionCoverageLegend);
                            } else {
                                modificationDetailsJLabel.setText(ionCoverageLegend);
                            }
                        } else {
                            modificationDetailsJLabel.setText("Modifications: (Files with modification details were not provided. " +
                                    "No modifications are shown.)" + "    /    " + ionCoverageLegend);
                            modifiedSequence = sequence;
                        }

                        // add ion coverage to peptide sequence
                        Iterator<MSMZHit> mzHits = tempMSHit.MSHits_mzhits.MSMZHit.iterator();

                        int[][] ionCoverage = new int[sequence.length()][2];

                        while (mzHits.hasNext()) {
                            MSMZHit tempMzHit = mzHits.next();

                            int ionType = tempMzHit.MSMZHit_ion.MSIonType;
                            int charge = tempMzHit.MSMZHit_charge;
                            int ionNumber = tempMzHit.MSMZHit_number;
                            double mzValue = ((double) tempMzHit.MSMZHit_mz) / omssaResponseScale;

                            // Note: assumes that 0 is a, 1 is b, 2 is c, 3 is x, 4 is y and 5 is z
                            if (ionType == 0) {
                            } else if (ionType == 1) {
                                ionCoverage[ionNumber][0]++;
                            } else if (ionType == 3) {
                            } else if (ionType == 4) {
                                ionCoverage[ionNumber][1]++;
                            } else if (ionType == 5) {
                            }
                        }

                        // add the ion coverage to the modified sequence
                        int[][] ionCoverageProcessed = new int[sequence.length()][2];

                        for (int i = 1; i < ionCoverage.length; i++) {
                            if (ionCoverage[i][0] > 0 && ionCoverage[i - 1][0] > 0) {
                                ionCoverageProcessed[i][0] = 1;
                            } else {
                                ionCoverageProcessed[i][0] = 0;
                            }

                            if (ionCoverage[i][1] > 0 && ionCoverage[i - 1][1] > 0) {
                                ionCoverageProcessed[ionCoverage.length - 1 - i][1] = 1;
                            } else {
                                ionCoverageProcessed[ionCoverage.length - 1 - i][1] = 0;
                            }
                        }

                        String modifiedSequenceColorCoded = "<html>";

                        // add nTerminal
                        if (!nTerminal.startsWith("<")) {
                            modifiedSequenceColorCoded += nTerminal;
                        } else {
                            modifiedSequenceColorCoded += "&lt;";
                            modifiedSequenceColorCoded += nTerminal.substring(1, nTerminal.length() - 2);
                            modifiedSequenceColorCoded += "&gt;-";
                        }

                        int aminoAcidCounter = 0;

                        for (int i = 0; i < modifiedSequence.length(); i++) {

                            if (modifiedSequence.charAt(i) == '<') {

                                if (ionCoverageProcessed[aminoAcidCounter - 1][0] > 0) {
                                    // b ions
                                    modifiedSequenceColorCoded += "<u>";
                                }

                                if (ionCoverageProcessed[aminoAcidCounter - 1][1] > 0) {
                                    // y ions
                                    modifiedSequenceColorCoded += "<font color=\"red\">";
                                }

                                modifiedSequenceColorCoded += "&lt;";
                                i++;

                                while (modifiedSequence.charAt(i) != '>') {
                                    modifiedSequenceColorCoded += modifiedSequence.charAt(i++);
                                }

                                modifiedSequenceColorCoded += "&gt;";

                                if (ionCoverageProcessed[aminoAcidCounter - 1][0] > 0) {
                                    // b ions
                                    modifiedSequenceColorCoded += "</u>";
                                }
                                if (ionCoverageProcessed[aminoAcidCounter - 1][1] > 0) {
                                    // y ions
                                    modifiedSequenceColorCoded += "</font>";
                                }
                            } else {

                                if (ionCoverageProcessed[aminoAcidCounter][0] > 0) {
                                    // b ions
                                    modifiedSequenceColorCoded += "<u>";
                                }

                                if (ionCoverageProcessed[aminoAcidCounter][1] > 0) {
                                    // y ions
                                    modifiedSequenceColorCoded += "<font color=\"red\">";
                                }

                                modifiedSequenceColorCoded += modifiedSequence.charAt(i);

                                if (ionCoverageProcessed[aminoAcidCounter][0] > 0) {
                                    // b ions
                                    modifiedSequenceColorCoded += "</u>";
                                }
                                if (ionCoverageProcessed[aminoAcidCounter][1] > 0) {
                                    // y ions
                                    modifiedSequenceColorCoded += "</font>";
                                }

                                aminoAcidCounter++;
                            }

                            modifiedSequenceColorCoded += "<font color=\"black\">";
                        }

                        // add cTerminal
                        if (!cTerminal.startsWith("-<")) {
                            modifiedSequenceColorCoded += cTerminal;
                        } else {
                            modifiedSequenceColorCoded += "-&lt;";
                            modifiedSequenceColorCoded += cTerminal.substring(2, cTerminal.length() - 1);
                            modifiedSequenceColorCoded += "&gt;";
                        }

                        modifiedSequenceColorCoded += "</html>";

                        List<MSPepHit> pepHits = tempMSHit.MSHits_pephits.MSPepHit;

                        Iterator<MSPepHit> pepHitIterator = pepHits.iterator();

                        while (pepHitIterator.hasNext()) {

                            MSPepHit tempPepHit = pepHitIterator.next();

                            f.write(msHitSet.MSHitSet_number + "\t" +
                                    sequence + "\t" +
                                    modifiedSequence + "\t" +
                                    modifiedSequenceColorCoded + "\t" +
                                    tempPepHit.MSPepHit_start + "\t" +
                                    tempPepHit.MSPepHit_stop + "\t" +
                                    new Double(((double) tempMSHit.MSHits_mass) / omssaResponseScale) + "\t" +
                                    new Double(((double) tempMSHit.MSHits_theomass) / omssaResponseScale) + "\t" +
                                    tempMSHit.MSHits_evalue + "\t" +
                                    tempMSHit.MSHits_pvalue + "\t" +
                                    tempPepHit.MSPepHit_accession + "\t" +
                                    tempPepHit.MSPepHit_defline + "\n");
                        }
                    }
                }

                f.close();

                lastSelectedFolder = selectedFile.getPath();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "An error occured when exporting the identifications.\n" +
                        "See ../Properties/ErrorLog.txt for more details.",
                        "Error Exporting Identifications",
                        JOptionPane.ERROR_MESSAGE);
                Util.writeToErrorLog("Error when exporting identifications: ");
                ex.printStackTrace();
            }
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

}//GEN-LAST:event_exportAllIdentificationsJMenuItemActionPerformed

    /**
     * Opens a frame containing the help manual for OMSSA Viewer.
     *
     * @param evt
     */
    private void helpJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpJMenuItemActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpWindow(this, getClass().getResource("/de/proteinms/omxparser/helpfiles/OmssaViewer.html"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_helpJMenuItemActionPerformed

    /**
     * Opens a frame containing the About OMSSA Viewer information.
     *
     * @param evt
     */
    private void aboutJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutJMenuItemActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpWindow(this, getClass().getResource("/de/proteinms/omxparser/helpfiles/AboutOmssaViewer.html"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_aboutJMenuItemActionPerformed

    /**
     * Export the all the spectra as dat files.
     *
     * @param evt
     */
    private void exportAllSpectraJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportAllSpectraJMenuItemActionPerformed

        JFileChooser chooser = new JFileChooser(lastSelectedFolder);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Export All Spectra As DTA Files");

        File selectedFolder;

        int returnVal = chooser.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

            selectedFolder = chooser.getSelectedFile();

            for (int j = 0; j < spectraJXTable.getRowCount(); j++) {

                List<Double> mzValues = allMzValues.get((Integer) spectraJXTable.getValueAt(j, 0));
                List<Double> abundanceValues = allAbundanceValues.get((Integer) spectraJXTable.getValueAt(j, 0));

                File currentFile = new File(selectedFolder, "" + spectraJXTable.getValueAt(j, 1));

                FileWriter f;

                try {
                    f = new FileWriter(currentFile);

                    // write the precursor mass and charge
                    f.write(spectraJXTable.getValueAt(j, 2) + " " + spectraJXTable.getValueAt(j, 3) + "\n");

                    // write all the m/z abundance pairs
                    for (int i = 0; i < mzValues.size(); i++) {
                        f.write(mzValues.get(i) + " " + abundanceValues.get(i) + "\n");
                    }

                    f.close();

                    lastSelectedFolder = currentFile.getPath();

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "An error occured when exporting the spectra.\n" +
                            "See ../Properties/ErrorLog.txt for more details.",
                            "Error Exporting Spectra",
                            JOptionPane.ERROR_MESSAGE);
                    Util.writeToErrorLog("Error when exporting spectra: ");
                    ex.printStackTrace();
                }
            }

            this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        }
}//GEN-LAST:event_exportAllSpectraJMenuItemActionPerformed

    /**
     * Updates the ion coverage annotations
     *
     * @param evt
     */
    private void aIonsJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aIonsJCheckBoxActionPerformed
        if (identificationsJXTable.getRowCount() > 0) {

            int selectedRow = 0;

            if (identificationsJXTable.getRowCount() > 1 &&
                    identificationsJXTable.getSelectedRow() != -1) {
                selectedRow = identificationsJXTable.getSelectedRow();
            }

            Vector<DefaultSpectrumAnnotation> currentAnnotations = allAnnotations.get(
                    identificationsJXTable.getValueAt(selectedRow, 1) + "_" +
                    identificationsJXTable.getValueAt(selectedRow, 8));

            // update the ion coverage annotations
            spectrumPanel.setAnnotations(filterAnnotations(currentAnnotations));
            spectrumPanel.validate();
            spectrumPanel.repaint();
        }
    }//GEN-LAST:event_aIonsJCheckBoxActionPerformed

    /**
     * See aIonsJCheckBoxActionPerformed
     */
    private void bIonsJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bIonsJCheckBoxActionPerformed
        aIonsJCheckBoxActionPerformed(null);
    }//GEN-LAST:event_bIonsJCheckBoxActionPerformed

    /**
     * See aIonsJCheckBoxActionPerformed
     */
    private void cIonsJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cIonsJCheckBoxActionPerformed
        aIonsJCheckBoxActionPerformed(null);
    }//GEN-LAST:event_cIonsJCheckBoxActionPerformed

    /**
     * See aIonsJCheckBoxActionPerformed
     */
    private void xIonsJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xIonsJCheckBoxActionPerformed
        aIonsJCheckBoxActionPerformed(null);
    }//GEN-LAST:event_xIonsJCheckBoxActionPerformed

    /**
     * See aIonsJCheckBoxActionPerformed
     */
    private void yIonsJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yIonsJCheckBoxActionPerformed
        aIonsJCheckBoxActionPerformed(null);
    }//GEN-LAST:event_yIonsJCheckBoxActionPerformed

    /**
     * See aIonsJCheckBoxActionPerformed
     */
    private void zIonsJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zIonsJCheckBoxActionPerformed
        aIonsJCheckBoxActionPerformed(null);
    }//GEN-LAST:event_zIonsJCheckBoxActionPerformed

    /**
     * See aIonsJCheckBoxActionPerformed
     */
    private void chargeOneJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chargeOneJCheckBoxActionPerformed
        aIonsJCheckBoxActionPerformed(null);
    }//GEN-LAST:event_chargeOneJCheckBoxActionPerformed

    /**
     * See aIonsJCheckBoxActionPerformed
     */
    private void chargeTwoJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chargeTwoJCheckBoxActionPerformed
        aIonsJCheckBoxActionPerformed(null);
    }//GEN-LAST:event_chargeTwoJCheckBoxActionPerformed

    /**
     * See aIonsJCheckBoxActionPerformed
     */
    private void chargeOverTwoJCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chargeOverTwoJCheckBoxActionPerformed
        aIonsJCheckBoxActionPerformed(null);
    }//GEN-LAST:event_chargeOverTwoJCheckBoxActionPerformed

    /**
     * When the user selects a row in the spectra files table, the identifications (if any)
     * are inserted in the identification table at the bottom of the frame, and the spectrum
     * details are shown in the spectrum panel and table to the right.
     *
     * @param evt
     */
    private void spectraJXTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spectraJXTableMouseClicked
        
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        int row = spectraJXTable.getSelectedRow();

        if (row != -1) {
            List<Double> mzValues = allMzValues.get((Integer) spectraJXTable.getValueAt(row, 0));
            List<Double> abundanceValues = allAbundanceValues.get((Integer) spectraJXTable.getValueAt(row, 0));

            // empty the spectrum table
            while (spectrumJXTable.getRowCount() > 0) {
                ((DefaultTableModel) spectrumJXTable.getModel()).removeRow(0);
            }

            // scrolls the scrollbar to the top of the spectrum table
            spectrumJXTable.scrollRectToVisible(spectrumJXTable.getCellRect(0, 0, false));

            // epmty the spectrum panel
            while (spectrumJPanel.getComponents().length > 0) {
                spectrumJPanel.remove(0);
            }

            // needed as input to the spectrum panel
            double[] mzValuesAsDouble = new double[mzValues.size()];
            double[] abundanceValuesAsDouble = new double[mzValues.size()];

            // insert the spectrum details in the spectrum table
            for (int i = 0; i < mzValues.size(); i++) {

                ((DefaultTableModel) spectrumJXTable.getModel()).addRow(new Object[]{
                            new Integer(i + 1),
                            mzValues.get(i),
                            abundanceValues.get(i)
                        });

                mzValuesAsDouble[i] = mzValues.get(i);
                abundanceValuesAsDouble[i] = abundanceValues.get(i);
            }

            exportSelectedSpectrumJMenuItem.setEnabled(true);

            // updates the spectrum panel
            spectrumPanel = new SpectrumPanel(
                    mzValuesAsDouble,
                    abundanceValuesAsDouble,
                    ((Double) spectraJXTable.getValueAt(row, 2)),
                    "" + spectraJXTable.getValueAt(row, 3),
                    ((String) spectraJXTable.getValueAt(row, 1)),
                    60, true, false);

            spectrumJPanel.add(spectrumPanel);
            spectrumJPanel.validate();
            spectrumJPanel.repaint();

            // empty the identification table
            while (identificationsJXTable.getRowCount() > 0) {
                ((DefaultTableModel) identificationsJXTable.getModel()).removeRow(0);
            }

            // clear the modification details legend
            modificationDetailsJLabel.setText("");

            // get the list of fixed modifications
            List<Integer> fixedModifications =
                    omssaOmxFile.getParserResult().MSSearch_request.MSRequest.get(0).MSRequest_settings.MSSearchSettings.MSSearchSettings_fixed.MSMod;

            // iterate all the identifications and insert them into the identification table
            MSHitSet msHitSet = spectrumHitSetMap.get(spectra.get((Integer) spectraJXTable.getValueAt(row, 0)));

            allAnnotations = new HashMap();

            List<MSHits> allMSHits = msHitSet.MSHitSet_hits.MSHits;
            Iterator<MSHits> msHitIterator = allMSHits.iterator();

            while (msHitIterator.hasNext()) {

                MSHits tempMSHit = msHitIterator.next();

                String sequence = tempMSHit.MSHits_pepstring;
                String[] modifications = new String[sequence.length()];

                for (int i = 0; i < modifications.length; i++) {
                    modifications[i] = "";
                }

                String modifiedSequence = "";
                String nTerminal = "NH2-";
                String cTerminal = "-COOH";

                // handle modifications
                if (omssaOmxFile.getModifications().size() > 0) {

                    if (fixedModifications.size() > 0) {

                        for (int i = 0; i < fixedModifications.size(); i++) {

                            Vector<String> modifiedResidues =
                                    omssaOmxFile.getModifications().get(fixedModifications.get(i)).getModResidues();

                            for (int j = 0; j < modifiedResidues.size(); j++) {

                                int index = sequence.indexOf(modifiedResidues.get(j));

                                while (index != -1) {

                                    modifications[index] +=
                                            "<" + omssaOmxFile.getModifications().get(fixedModifications.get(i)).getModNumber() + ">";

                                    index = sequence.indexOf(modifiedResidues.get(j), index + 1);
                                }
                            }
                        }
                    }

                    // variable modifications
                    Iterator<MSModHit> modsIterator = tempMSHit.MSHits_mods.MSModHit.iterator();

                    while (modsIterator.hasNext()) {

                        MSModHit currentMSModHit = modsIterator.next();

                        modifications[currentMSModHit.MSModHit_site] +=
                                "<" + currentMSModHit.MSModHit_modtype.MSMod + ">";
                    }

                    String modificationDetails = "";

                    for (int i = 0; i < modifications.length; i++) {

                        if (!modifications[i].equalsIgnoreCase("")) {

                            if (modificationDetails.lastIndexOf(modifications[i]) == -1) {

                                OmssaModification tempOmssaModification = omssaOmxFile.getModifications().get(
                                        new Integer(modifications[i].substring(1, modifications[i].length() - 1)));

                                if (tempOmssaModification != null) {

                                    modificationDetails += modifications[i] + " " + tempOmssaModification.getModName() +
                                            " (" + tempOmssaModification.getModMonoMass() + "), ";

                                    if (tempOmssaModification.getModType() == OmssaModification.MODAA) {

                                        // "normal" modification
                                        modifiedSequence += sequence.substring(i, i + 1) + modifications[i];
                                    } else if (tempOmssaModification.getModType() == OmssaModification.MODN || tempOmssaModification.getModType() == OmssaModification.MODNAA || tempOmssaModification.getModType() == OmssaModification.MODNP || tempOmssaModification.getModType() == OmssaModification.MODNPAA) {

                                        // n-terminal modification
                                        nTerminal = modifications[i] + "-";
                                        modifiedSequence += sequence.substring(i, i + 1);
                                    } else if (tempOmssaModification.getModType() == OmssaModification.MODC || tempOmssaModification.getModType() == OmssaModification.MODCAA || tempOmssaModification.getModType() == OmssaModification.MODCP || tempOmssaModification.getModType() == OmssaModification.MODCPAA) {

                                        // c-terminal modification
                                        cTerminal = "-" + modifications[i];
                                        modifiedSequence += sequence.substring(i, i + 1);
                                    }
                                } else {
                                    modifiedSequence += sequence.substring(i, i + 1) + modifications[i];
                                    modificationDetails += modifications[i] + " unknown, ";
                                }
                            } else {
                                modifiedSequence += sequence.substring(i, i + 1) + modifications[i];
                            }
                        } else {
                            modifiedSequence += sequence.substring(i, i + 1) + modifications[i];
                        }
                    }

                    if (modificationDetails.endsWith(", ")) {
                        modificationDetails = modificationDetails.substring(0, modificationDetails.length() - 2);
                    }

                    if (modificationDetails.length() > 0) {
                        modificationDetailsJLabel.setText("Modifications: " + modificationDetails +
                                "    /    " + ionCoverageLegend);
                    } else {
                        modificationDetailsJLabel.setText(ionCoverageLegend);
                    }
                } else {
                    modificationDetailsJLabel.setText("Modifications: (Files with modification details were not provided. " +
                            "No modifications are shown.)" + "    /    " + ionCoverageLegend);
                    modifiedSequence = sequence;
                }

                // add ion coverage to peptide sequence
                Iterator<MSMZHit> mzHits = tempMSHit.MSHits_mzhits.MSMZHit.iterator();

                Vector<DefaultSpectrumAnnotation> currentAnnotations = new Vector();

                // annotate precursor ion
//                annotations.add(new DefaultSpectrumAnnotation(
//                            ((Double) spectraJXTable.getValueAt(spectraJXTable.getSelectedRow(), 2)).doubleValue(),
//                            ionCoverageErrorMargin, Color.GRAY, "pre"));

                int[][] ionCoverage = new int[sequence.length()][2];

                while (mzHits.hasNext()) {
                    MSMZHit tempMzHit = mzHits.next();

                    int ionType = tempMzHit.MSMZHit_ion.MSIonType;
                    int charge = tempMzHit.MSMZHit_charge;
                    int ionNumber = tempMzHit.MSMZHit_number;
                    double mzValue = ((double) tempMzHit.MSMZHit_mz) / omssaResponseScale;

                    String chargeAsString = "";

                    // add the charge to the label if higher than 1
                    if (charge > 1) {

                        for (int i = 0; i < charge; i++) {
                            chargeAsString += "+";
                        }
                    }

                    String unusedIon = "";

                    if (!usedIonTypes.contains(new Integer(ionType))) {
                        unusedIon = "#";
                    }

                    // assumes that 0 is a, 1 is b, 2 is c, 3 is x, 4 is y and 5 is z
                    //
                    // from OMSSA.mod.xsd:
                    // <xs:enumeration value="a" ncbi:intvalue="0" />
                    // <xs:enumeration value="b" ncbi:intvalue="1" />
                    // <xs:enumeration value="c" ncbi:intvalue="2" />
                    //  <xs:enumeration value="x" ncbi:intvalue="3" />
                    //  <xs:enumeration value="y" ncbi:intvalue="4" />
                    //  <xs:enumeration value="z" ncbi:intvalue="5" />
                    //  <xs:enumeration value="parent" ncbi:intvalue="6" />
                    //  <xs:enumeration value="internal" ncbi:intvalue="7" />
                    //  <xs:enumeration value="immonium" ncbi:intvalue="8" />
                    //  <xs:enumeration value="unknown" ncbi:intvalue="9" />
                    //  <xs:enumeration value="max" ncbi:intvalue="10" />

                    if (ionType == 0) {
                        currentAnnotations.add(new DefaultSpectrumAnnotation(
                                mzValue, ionCoverageErrorMargin, Color.BLUE,
                                unusedIon + "a" + (ionNumber + 1) + chargeAsString));
                    } else if (ionType == 1) {
                        currentAnnotations.add(new DefaultSpectrumAnnotation(
                                mzValue, ionCoverageErrorMargin, Color.BLUE,
                                unusedIon + "b" + (ionNumber + 1) + chargeAsString));
                        ionCoverage[ionNumber][0]++;
                    } else if (ionType == 2) {
                        currentAnnotations.add(new DefaultSpectrumAnnotation(
                                mzValue, ionCoverageErrorMargin, Color.BLUE,
                                unusedIon + "c" + (ionNumber + 1) + chargeAsString));
                    } else if (ionType == 3) {
                        currentAnnotations.add(new DefaultSpectrumAnnotation(
                                mzValue, ionCoverageErrorMargin, Color.BLACK,
                                unusedIon + "x" + (ionNumber + 1) + chargeAsString));
                    } else if (ionType == 4) {
                        currentAnnotations.add(new DefaultSpectrumAnnotation(
                                mzValue, ionCoverageErrorMargin, Color.BLACK,
                                unusedIon + "y" + (ionNumber + 1) + chargeAsString));
                        ionCoverage[ionNumber][1]++;
                    } else if (ionType == 5) {
                        currentAnnotations.add(new DefaultSpectrumAnnotation(
                                mzValue, ionCoverageErrorMargin, Color.BLACK,
                                unusedIon + "z" + (ionNumber + 1) + chargeAsString));
                    } else if (ionType == 6) {
                        currentAnnotations.add(new DefaultSpectrumAnnotation(
                                mzValue, ionCoverageErrorMargin, Color.GRAY,
                                unusedIon + "parent" + chargeAsString));
                    } else if (ionType == 7) {
                        currentAnnotations.add(new DefaultSpectrumAnnotation(
                                mzValue, ionCoverageErrorMargin, Color.GRAY,
                                unusedIon + "internal" + chargeAsString));
                    } else if (ionType == 8) {
                        currentAnnotations.add(new DefaultSpectrumAnnotation(
                                mzValue, ionCoverageErrorMargin, Color.GRAY,
                                unusedIon + "immonium" + chargeAsString));
                    } else if (ionType == 9) {
                        currentAnnotations.add(new DefaultSpectrumAnnotation(
                                mzValue, ionCoverageErrorMargin, Color.GRAY,
                                unusedIon + "unknown" + chargeAsString));
                    }

                    allAnnotations.put((sequence + "_" + new Float(tempMSHit.MSHits_pvalue)), currentAnnotations);

                    // only add the annotations for the first identification
                    if (allAnnotations.size() == 1) {
                        // add the ion coverage annotations to the spectrum panel
                        spectrumPanel.setAnnotations(filterAnnotations(currentAnnotations));
                        spectrumPanel.validate();
                        spectrumPanel.repaint();
                    }
                }

                // add the ion coverage to the modified sequence
                int[][] ionCoverageProcessed = new int[sequence.length()][2];

                for (int i = 1; i < ionCoverage.length; i++) {
                    if (ionCoverage[i][0] > 0 && ionCoverage[i - 1][0] > 0) {
                        ionCoverageProcessed[i][0] = 1;
                    } else {
                        ionCoverageProcessed[i][0] = 0;
                    }

                    if (ionCoverage[i][1] > 0 && ionCoverage[i - 1][1] > 0) {
                        ionCoverageProcessed[ionCoverage.length - 1 - i][1] = 1;
                    } else {
                        ionCoverageProcessed[ionCoverage.length - 1 - i][1] = 0;
                    }
                }

                String modifiedSequenceColorCoded = "<html>";

                // add nTerminal
                if (!nTerminal.startsWith("<")) {
                    modifiedSequenceColorCoded += nTerminal;
                } else {
                    modifiedSequenceColorCoded += "&lt;";
                    modifiedSequenceColorCoded += nTerminal.substring(1, nTerminal.length() - 2);
                    modifiedSequenceColorCoded += "&gt;-";
                }

                int aminoAcidCounter = 0;

                for (int i = 0; i < modifiedSequence.length(); i++) {

                    if (modifiedSequence.charAt(i) == '<') {

                        if (ionCoverageProcessed[aminoAcidCounter - 1][0] > 0) {
                            // b ions
                            modifiedSequenceColorCoded += "<u>";
                        }

                        if (ionCoverageProcessed[aminoAcidCounter - 1][1] > 0) {
                            // y ions
                            modifiedSequenceColorCoded += "<font color=\"red\">";
                        }

                        modifiedSequenceColorCoded += "&lt;";
                        i++;

                        while (modifiedSequence.charAt(i) != '>') {
                            modifiedSequenceColorCoded += modifiedSequence.charAt(i++);
                        }

                        modifiedSequenceColorCoded += "&gt;";

                        if (ionCoverageProcessed[aminoAcidCounter - 1][0] > 0) {
                            // b ions
                            modifiedSequenceColorCoded += "</u>";
                        }
                        if (ionCoverageProcessed[aminoAcidCounter - 1][1] > 0) {
                            // y ions
                            modifiedSequenceColorCoded += "</font>";
                        }
                    } else {

                        if (ionCoverageProcessed[aminoAcidCounter][0] > 0) {
                            // b ions
                            modifiedSequenceColorCoded += "<u>";
                        }

                        if (ionCoverageProcessed[aminoAcidCounter][1] > 0) {
                            // y ions
                            modifiedSequenceColorCoded += "<font color=\"red\">";
                        }

                        modifiedSequenceColorCoded += modifiedSequence.charAt(i);

                        if (ionCoverageProcessed[aminoAcidCounter][0] > 0) {
                            // b ions
                            modifiedSequenceColorCoded += "</u>";
                        }
                        if (ionCoverageProcessed[aminoAcidCounter][1] > 0) {
                            // y ions
                            modifiedSequenceColorCoded += "</font>";
                        }

                        aminoAcidCounter++;
                    }

                    modifiedSequenceColorCoded += "<font color=\"black\">";
                }

                // add cTerminal
                if (!cTerminal.startsWith("-<")) {
                    modifiedSequenceColorCoded += cTerminal;
                } else {
                    modifiedSequenceColorCoded += "-&lt;";
                    modifiedSequenceColorCoded += cTerminal.substring(2, cTerminal.length() - 1);
                    modifiedSequenceColorCoded += "&gt;";
                }

                modifiedSequenceColorCoded += "</html>";

                List<MSPepHit> pepHits = tempMSHit.MSHits_pephits.MSPepHit;

                Iterator<MSPepHit> pepHitIterator = pepHits.iterator();

                while (pepHitIterator.hasNext()) {

                    MSPepHit tempPepHit = pepHitIterator.next();

                    ((DefaultTableModel) identificationsJXTable.getModel()).addRow(new Object[]{
                                msHitSet.MSHitSet_number,
                                sequence,
                                modifiedSequenceColorCoded,
                                tempPepHit.MSPepHit_start,
                                tempPepHit.MSPepHit_stop,
                                new Double(((double) tempMSHit.MSHits_mass) / omssaResponseScale),
                                new Double(((double) tempMSHit.MSHits_theomass) / omssaResponseScale),
                                new Float(tempMSHit.MSHits_evalue),
                                new Float(tempMSHit.MSHits_pvalue),
                                tempPepHit.MSPepHit_accession,
                                tempPepHit.MSPepHit_defline
                            });
                }
            }

            if (evt != null && evt.getButton() == MouseEvent.BUTTON3) {
                copySpectraJPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }

        if (identificationsJXTable.getRowCount() > 1) {
            identificationsJXTable.setRowSelectionInterval(0, 0);
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_spectraJXTableMouseClicked

    /**
     * Opens a popup menu if the user right clicks in the spectrum table.
     *
     * @param evt
     */
    private void spectraJXTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_spectraJXTableKeyReleased
        spectraJXTableMouseClicked(null);
    }//GEN-LAST:event_spectraJXTableKeyReleased

    /**
     * Opens a popup menu if the user right clicks in the spectrum table.
     *
     * @param evt
     */
    private void spectrumJXTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spectrumJXTableMouseClicked
        if (spectrumJXTable.getSelectedRow() != -1 && evt.getButton() == MouseEvent.BUTTON3) {
            copySpectrumJPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_spectrumJXTableMouseClicked

    /**
     * Updates the ion coverage relative in the spectrum to the selected identification.
     *
     * @param evt
     */
    private void identificationsJXTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_identificationsJXTableKeyReleased
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        // update the ion coverage annotations
        spectrumPanel.setAnnotations(filterAnnotations(allAnnotations.get(
                identificationsJXTable.getValueAt(identificationsJXTable.getSelectedRow(), 1) + "_" +
                identificationsJXTable.getValueAt(identificationsJXTable.getSelectedRow(), 8))));
        spectrumPanel.validate();
        spectrumPanel.repaint();

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_identificationsJXTableKeyReleased

    /**
     * Updates the ion coverage relative in the spectrum to the selected identification.
     *
     * Right clicking opens a popup menu.
     *
     * @param evt
     */
    private void identificationsJXTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_identificationsJXTableMouseClicked
        if (identificationsJXTable.getSelectedRow() != -1) {

            if (evt.getButton() == MouseEvent.BUTTON3) {
                copyIdentificationsJPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            } else {

                if (identificationsJXTable.getRowCount() > 1) {

                    Vector<DefaultSpectrumAnnotation> currentAnnotations = allAnnotations.get(
                            identificationsJXTable.getValueAt(identificationsJXTable.getSelectedRow(), 1) + "_" +
                            identificationsJXTable.getValueAt(identificationsJXTable.getSelectedRow(), 8));

                    // update the ion coverage annotations
                    spectrumPanel.setAnnotations(filterAnnotations(currentAnnotations));
                    spectrumPanel.validate();
                    spectrumPanel.repaint();
                }
            }
        }
    }//GEN-LAST:event_identificationsJXTableMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox aIonsJCheckBox;
    private javax.swing.JMenuItem aboutJMenuItem;
    private javax.swing.JCheckBox bIonsJCheckBox;
    private javax.swing.JCheckBox cIonsJCheckBox;
    private javax.swing.JCheckBox chargeOneJCheckBox;
    private javax.swing.JCheckBox chargeOverTwoJCheckBox;
    private javax.swing.JCheckBox chargeTwoJCheckBox;
    private javax.swing.JMenuItem copyIdentificationsJMenuItem;
    private javax.swing.JPopupMenu copyIdentificationsJPopupMenu;
    private javax.swing.JMenuItem copySpectraJMenuItem;
    private javax.swing.JPopupMenu copySpectraJPopupMenu;
    private javax.swing.JMenuItem copySpectrumJMenuItem;
    private javax.swing.JPopupMenu copySpectrumJPopupMenu;
    private javax.swing.JMenuItem exitJMenuItem;
    private javax.swing.JMenuItem exportAllIdentificationsJMenuItem;
    private javax.swing.JMenuItem exportAllSpectraJMenuItem;
    private javax.swing.JMenu exportJMenu;
    private javax.swing.JMenuItem exportSelectedSpectrumJMenuItem;
    private javax.swing.JMenuItem exportSpectraFilesTableJMenuItem;
    private javax.swing.JMenu fileJMenu;
    private javax.swing.JMenu helpJMenu;
    private javax.swing.JMenuItem helpJMenuItem;
    private org.jdesktop.swingx.JXTable identificationsJXTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel modificationDetailsJLabel;
    private javax.swing.JMenuItem openJMenuItem;
    private org.jdesktop.swingx.JXTable spectraJXTable;
    private javax.swing.JPanel spectrumJPanel;
    private org.jdesktop.swingx.JXTable spectrumJXTable;
    private javax.swing.JCheckBox xIonsJCheckBox;
    private javax.swing.JCheckBox yIonsJCheckBox;
    private javax.swing.JCheckBox zIonsJCheckBox;
    // End of variables declaration//GEN-END:variables
}
