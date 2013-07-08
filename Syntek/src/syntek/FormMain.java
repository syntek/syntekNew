/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syntek;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ABC
 */
public class FormMain extends javax.swing.JFrame {

    /**
     * Creates new form formMain
     */
    public FormMain() {
        initComponents();
        ConSQL conSQL = new ConSQL("localhost", "1433", "syntek", "sa", "123456");
        loadListFile();

        Vector vt = new Vector();
        vt.add("ID");
        vt.add("DocumentID");
        vt.add("FileIndex");
        vt.add("URL");
        vt.add("PageCount");
        tableFileDetail.setModel(getDataFromTable("DocumentFile", vt));
    }

    public static DefaultTableModel getDataFromTable(String tableName, Vector tableTitle) {

        tableTitle.add(0, "");
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int x, int y) {
                if (y > 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public Class getColumnClass(int col) {
                switch (col) {
                    case 0:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
        };
        model.setColumnIdentifiers(tableTitle);

        try {

            Statement stm = ConSQL.CON.createStatement();
            ResultSet rs = stm.executeQuery("select * from " + tableName);
            ResultSetMetaData rsmt = rs.getMetaData();
            while (rs.next()) {

                Vector k = new Vector();
                k.add(false);
                for (int i = 1; i <= rsmt.getColumnCount(); i++) {
                    k.add(rs.getString(i));
                }
                model.addRow(k);
            }
        } catch (SQLException ex) {
            // Logger.getLogger(StaticMethod.class.getName()).log(Level.SEVERE, null, ex);
        }
        return model;
    }

    public ArrayList<String> getPathChooseFromTable(JTable table) {
        ArrayList<String> list = new ArrayList();
        int rowCount = table.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            boolean check = (boolean) table.getValueAt(i, 0);
            if (check) {
                list.add(table.getValueAt(i, 4).toString());
            }
        }
        return list;
    }

    public void loadListFile() {
        try {
            String sql = "SELECT title from Document";
            CallableStatement cs = ConSQL.CON.prepareCall(sql);
            ResultSet rs = cs.executeQuery();
            DefaultListModel defaultListModel = new DefaultListModel();
            while (rs.next()) {
                defaultListModel.addElement(rs.getString(1));
            }
            txtDocumentList.setModel(defaultListModel);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String getDocumentIDbyName(String title) {
        String id = "";
        try {
            String sql = "SELECT id from Document WHERE Title = ?";
            PreparedStatement ps = ConSQL.CON.prepareCall(sql);
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getString("id");
            }
        } catch (SQLException ex) {
            //Logger.getLogger(formMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(id);
        return id;
    }

    public void updateDataTable(String id) {
        try {
            String sql = "SELECT ID, FileIndex, URL, PageCount from DocumentFile WHERE DocumentID =?";
            PreparedStatement ps = ConSQL.CON.prepareCall(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            DefaultTableModel defaultTableModel = new DefaultTableModel();

            Vector<String> title = new Vector<>();
            title.add("ID");
            title.add("Số thứ tự");
            title.add("URL");
            title.add("Page Number");
            defaultTableModel.setColumnIdentifiers(title);

            while (rs.next()) {
                Vector data = new Vector();
                data.add(rs.getString("ID"));
                data.add(rs.getString("FileIndex"));
                data.add(rs.getString("URL"));
                data.add(rs.getString("PageCount"));

                defaultTableModel.addRow(data);
            }
            tableFileDetail.setModel(defaultTableModel);
        } catch (SQLException ex) {
            //Logger.getLogger(formMain.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton4 = new javax.swing.JButton();
        jScrollBar1 = new javax.swing.JScrollBar();
        jPanel1 = new javax.swing.JPanel();
        pnManageDocument = new javax.swing.JPanel();
        lableDocID = new javax.swing.JLabel();
        btnInsertDocument = new javax.swing.JButton();
        btnEditDocument = new javax.swing.JButton();
        btnDeleteDocument = new javax.swing.JButton();
        lbDocumentID = new javax.swing.JLabel();
        pnManageFiles = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableFileDetail = new javax.swing.JTable();
        btnOpenFile = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        btnInsertNewFile = new javax.swing.JButton();
        btnUpdateFile = new javax.swing.JButton();
        btnDeleteFile = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtDocumentList = new javax.swing.JList();
        btnExport = new javax.swing.JButton();

        jButton4.setText("jButton4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.CardLayout());

        pnManageDocument.setBorder(javax.swing.BorderFactory.createTitledBorder("Quản lý văn bản"));

        lableDocID.setText("Mã văn bản:");

        btnInsertDocument.setText("Thêm văn bản");

        btnEditDocument.setText("Sủa văn bản");

        btnDeleteDocument.setText("Xóa văn bản");

        lbDocumentID.setText("1");

        javax.swing.GroupLayout pnManageDocumentLayout = new javax.swing.GroupLayout(pnManageDocument);
        pnManageDocument.setLayout(pnManageDocumentLayout);
        pnManageDocumentLayout.setHorizontalGroup(
            pnManageDocumentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnManageDocumentLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lableDocID)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbDocumentID, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(189, 189, 189)
                .addComponent(btnInsertDocument, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnEditDocument, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDeleteDocument, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnManageDocumentLayout.setVerticalGroup(
            pnManageDocumentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnManageDocumentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnManageDocumentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lableDocID)
                    .addComponent(btnInsertDocument)
                    .addComponent(btnEditDocument)
                    .addComponent(btnDeleteDocument)
                    .addComponent(lbDocumentID))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnManageFiles.setBorder(javax.swing.BorderFactory.createTitledBorder("Quản lý File"));

        tableFileDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tableFileDetail);

        btnOpenFile.setText("Mở file");
        btnOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenFileActionPerformed(evt);
            }
        });

        jButton2.setText("Nối file");

        jButton3.setText("Chuyển sang pdf");

        btnInsertNewFile.setText("Thêm file mới");
        btnInsertNewFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertNewFileActionPerformed(evt);
            }
        });

        btnUpdateFile.setText("Cập nhật File");

        btnDeleteFile.setText("Xóa File");

        javax.swing.GroupLayout pnManageFilesLayout = new javax.swing.GroupLayout(pnManageFiles);
        pnManageFiles.setLayout(pnManageFilesLayout);
        pnManageFilesLayout.setHorizontalGroup(
            pnManageFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnManageFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(pnManageFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnManageFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnOpenFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnInsertNewFile, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                        .addComponent(btnUpdateFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDeleteFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        pnManageFilesLayout.setVerticalGroup(
            pnManageFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnManageFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnManageFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnManageFilesLayout.createSequentialGroup()
                        .addComponent(btnOpenFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                        .addComponent(btnInsertNewFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdateFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteFile)
                        .addGap(36, 36, 36)
                        .addComponent(jButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3)
                        .addGap(35, 35, 35)))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Danh sách văn bản"));

        txtDocumentList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                txtDocumentListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(txtDocumentList);

        btnExport.setText("Kết xuất");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnExport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane3)
                .addGap(18, 18, 18)
                .addComponent(btnExport)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnManageFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnManageDocument, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(pnManageDocument, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnManageFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, "card3");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenFileActionPerformed
        // TODO add your handling code here:
        ArrayList<String> list = getPathChooseFromTable(tableFileDetail);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toString());
            try {
                Process p = Runtime.getRuntime().exec("cmd /c " + list.get(i).toString());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy file: " + list.get(i).toString());
                //Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnOpenFileActionPerformed

    private void btnInsertNewFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertNewFileActionPerformed
        // TODO add your handling code here:
        DialogInsertNewFile dialogInsertNewFile = new DialogInsertNewFile(this, true, lbDocumentID.getText());
        dialogInsertNewFile.setVisible(true);


    }//GEN-LAST:event_btnInsertNewFileActionPerformed

    private void txtDocumentListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_txtDocumentListValueChanged
        // TODO add your handling code here:
        String id = getDocumentIDbyName(txtDocumentList.getSelectedValue().toString());
        updateDataTable(id);
        lbDocumentID.setText(id);
    }//GEN-LAST:event_txtDocumentListValueChanged

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
            java.util.logging.Logger.getLogger(FormMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                }
                new FormMain().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteDocument;
    private javax.swing.JButton btnDeleteFile;
    private javax.swing.JButton btnEditDocument;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnInsertDocument;
    private javax.swing.JButton btnInsertNewFile;
    private javax.swing.JButton btnOpenFile;
    private javax.swing.JButton btnUpdateFile;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lableDocID;
    private javax.swing.JLabel lbDocumentID;
    private javax.swing.JPanel pnManageDocument;
    private javax.swing.JPanel pnManageFiles;
    private javax.swing.JTable tableFileDetail;
    private javax.swing.JList txtDocumentList;
    // End of variables declaration//GEN-END:variables
}
