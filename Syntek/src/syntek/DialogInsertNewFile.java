/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syntek;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author ABC
 */
public class DialogInsertNewFile extends javax.swing.JDialog {

    /**
     * Creates new form DialogInsertNewFile
     */
    String documentName = null;

    public DialogInsertNewFile(java.awt.Frame parent, boolean modal, String documentTitle) {
        super(parent, modal);

        this.documentName = documentTitle;
        initComponents();
        txtDocumentID.setText(documentTitle);
        txtPath.setEditable(false);
        txtDocumentID.setEditable(false);
        txtPageCount.setEditable(false);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnChooseFile = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtPath = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtPageCount = new javax.swing.JTextField();
        txtDocumentID = new javax.swing.JTextField();
        btnInsertFile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Thêm mới file");
        setResizable(false);
        getContentPane().setLayout(new java.awt.CardLayout());

        btnChooseFile.setText("...");
        btnChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseFileActionPerformed(evt);
            }
        });

        jLabel4.setText("Văn bản: ");

        jLabel5.setText("Đường dẫn:");

        jLabel6.setText("Số trang:");

        txtPageCount.setBorder(null);

        txtDocumentID.setBorder(null);

        btnInsertFile.setText("Thêm file");
        btnInsertFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(txtPageCount))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(txtPath))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(47, 47, 47)
                        .addComponent(txtDocumentID, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnChooseFile, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(53, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnInsertFile, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(137, 137, 137))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtDocumentID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnChooseFile))
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtPageCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(btnInsertFile, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        getContentPane().add(jPanel1, "card2");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChooseFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseFileActionPerformed
        // TODO add your handling code here:
        DialogChooseNewFile dialogChooseNewFile = new DialogChooseNewFile(null, true);
        dialogChooseNewFile.setDocumentID(documentName);
        dialogChooseNewFile.setVisible(true);
        txtPath.setText(DialogChooseNewFile.PATH_FILE);
        txtPageCount.setText(String.valueOf(DialogChooseNewFile.PAGE_COUNT));
    }//GEN-LAST:event_btnChooseFileActionPerformed

    private void btnInsertFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertFileActionPerformed
        /*
         * get new File index
         */
        FormMain.thread.run();
        int fileIndex = 1;
        try {
            PreparedStatement pstm = ConSQL.CON.prepareStatement("SELECT MAX(FileIndex)as newIndex FROM DocumentFile WHERE DocumentID = ?");
            pstm.setInt(1, FormMain.documentID);
            ResultSet myresult = pstm.executeQuery();
            myresult.next();
            if (!"null".equals(myresult.getString("newIndex"))) {
                fileIndex = myresult.getInt("newIndex") + 1;
            }
            HandleFolder.copyFile(txtPath.getText(), String.valueOf(FormMain.documentID));
            // fileIndex = myresult.getInt("newIndex")+1;
            System.out.println("====================================" + myresult.getString("newIndex"));
            /*
             * Insert new File into database
             */

            String sql = "INSERT INTO DocumentFile(DocumentID, FileIndex, URL, PageCount) VALUES(?, ?, ? ,?)";
            PreparedStatement ps = ConSQL.CON.prepareCall(sql);
            ps.setInt(1, FormMain.documentID);
            ps.setInt(2, fileIndex);
            ps.setString(3, HandleFolder.PATH_SAVED_FILE);
            ps.setInt(4, Integer.parseInt(txtPageCount.getText()));

            System.out.println(txtPath.getText());
            System.out.println(FormMain.documentID);



            int e = ps.executeUpdate();
            if (e > 0) {
                FormMain.thread.StopThread();
                this.dispose();
            }
        } catch (SQLException ex) {
            FormMain.thread.StopThread();
            Logger.getLogger(DialogInsertNewFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnInsertFileActionPerformed

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
            java.util.logging.Logger.getLogger(DialogInsertNewFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DialogInsertNewFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DialogInsertNewFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DialogInsertNewFile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogInsertNewFile dialog = new DialogInsertNewFile(new javax.swing.JFrame(), true, "");
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
    private javax.swing.JButton btnChooseFile;
    private javax.swing.JButton btnInsertFile;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtDocumentID;
    private javax.swing.JTextField txtPageCount;
    private javax.swing.JTextField txtPath;
    // End of variables declaration//GEN-END:variables
}
