/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syntek;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.docx4j.convert.out.flatOpcXml.FlatOpcXmlCreator;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.xmlPackage.XmlData;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import sun.swing.FilePane;

/**
 *
 * @author ABC
 */
public class DialogChooseNewFile extends javax.swing.JDialog {

    /**
     * Creates new form FormInsertNewFile
     */
    public static String PATH_FILE = null;
    public static int PAGE_COUNT;
    public static String DESCRIPTION = ".docx";
    public static String EXTENSIONS = "docx";
    private String documentName = null;
    private int documentID = -1;

    public DialogChooseNewFile(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter(DESCRIPTION, EXTENSIONS);
        fileChoose.setFileFilter(extensionFilter);
        setLocation(200, 200);
    }

    public String getDocumentID() {
        return this.documentName;
    }

    public void setDocumentID(String DocumentName) {
        this.documentName = DocumentName;
    }

    public int getPagesNumber(String pathFile) {
        int pageNumber = 0;
        try {
            WordprocessingMLPackage mLPackage = WordprocessingMLPackage.load(new File(pathFile));
            MainDocumentPart mainDocumentPart = mLPackage.getMainDocumentPart();
            FlatOpcXmlCreator worker = new FlatOpcXmlCreator(mLPackage);
            org.docx4j.xmlPackage.Package pkg = worker.get();
            List<org.docx4j.xmlPackage.Part> parts = pkg.getPart();

            System.out.println("part: " + parts.size());
            for (int i = 0; i < parts.size(); i++) {
                if (parts.get(i).getName().equalsIgnoreCase("/docProps/app.xml")) {
                    XmlData xmlData = parts.get(i).getXmlData();
                    Element any = xmlData.getAny();
                    NodeList list = any.getChildNodes();
                    System.out.println("List: " + list.getLength());
                    for (int j = 0; j < list.getLength(); j++) {
                        org.w3c.dom.Node node = list.item(j);
                        if (node.getNodeName().equalsIgnoreCase("properties:Pages")) {
                            pageNumber = Integer.parseInt(node.getFirstChild().getNodeValue());
                            break;
                        }
                    }
                    break;
                }
            }
        } catch (Docx4JException ex) {
            JOptionPane.showMessageDialog(null, "Lỗi lấy số trang");
            //Logger.getLogger(Fake.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pageNumber;
    }

    public void getFilePaths() {
        File file = fileChoose.getSelectedFile();
        PATH_FILE = file.getPath();
        PAGE_COUNT = getPagesNumber(PATH_FILE);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        fileChoose = new javax.swing.JFileChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.CardLayout());

        fileChoose.setCurrentDirectory(null);
        fileChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileChooseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(fileChoose, javax.swing.GroupLayout.PREFERRED_SIZE, 562, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fileChoose, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1, "card2");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fileChooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileChooseActionPerformed
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File file = fileChoose.getSelectedFile();
            System.out.println(fileChoose.getTypeDescription(file));
            PATH_FILE = file.getPath();
            System.out.println(file.getName());
            try {
                boolean check = false;

                String sql = "SELECT id FROM DocumentFile WHERE URL = ? and DocumentID = ?";
                PreparedStatement ps = ConSQL.CON.prepareCall(sql);
                ps.setString(1, PATH_FILE);
                ps.setInt(2, FormMain.documentID);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    check = true;
                    JOptionPane.showMessageDialog(null, "File văn bản đã tồn tại");
                    break;
                }
                String fileName = file.getName();
                if (!fileName.substring(fileName.lastIndexOf(".")).equals(".docx")) {
                    check = true;
                    JOptionPane.showMessageDialog(rootPane, "Chỉ chọn được file có phần mở rộng .docx","Thông báo",JOptionPane.ERROR_MESSAGE);
                }
                if (!check) {
                    getFilePaths();
                    //getPagesNumber(PATH_FILE);
                    System.out.println(PATH_FILE);

                    setVisible(false);
                }
            } catch (SQLException ex) {
                Logger.getLogger(DialogChooseNewFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            this.dispose();
        }
    }//GEN-LAST:event_fileChooseActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DialogChooseNewFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DialogChooseNewFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DialogChooseNewFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DialogChooseNewFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogChooseNewFile dialog = new DialogChooseNewFile(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser fileChoose;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
