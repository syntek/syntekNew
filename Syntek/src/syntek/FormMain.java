/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syntek;

import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.TableView;
import org.apache.log4j.PropertyConfigurator;
import org.docx4j.convert.out.flatOpcXml.FlatOpcXmlCreator;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.xmlPackage.XmlData;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import syntek.waitingForm.myThread;

/**
 *
 * @author ABC
 */
public class FormMain extends javax.swing.JFrame {

    /**
     * Creates new form formMain
     */
    public static int documentID = -1;
    public static myThread thread = new myThread();
    public FormMain() {
        initComponents();
        myInit();
        PropertyConfigurator.configure("log4j.properties");
    }
    
    private void setCenterPosition() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }
    
    private void myInit() {
        ConSQL conSQL = new ConSQL("localhost", "1433", "syntek", "sa", "1234$");
        loadListDocument();
        btnConvertDocxToPDF.setEnabled(false);
        btnDeleteFile.setEnabled(false);
        btnInsertNewFile.setEnabled(false);
        btnMergeFile.setEnabled(false);
        btnOpenFile.setEnabled(false);
        btnUpdateFile.setEnabled(false);
        btnMoveDown.setEnabled(false);
        btnMoveUp.setEnabled(false);
        
        setCenterPosition();
        
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                setTableOption(tableFileDetail);
            }
            
            @Override
            public void componentMoved(ComponentEvent e) {
            }
            
            @Override
            public void componentShown(ComponentEvent e) {
            }
            
            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
    }
    
    private void setTableOption(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(30);
        columnModel.getColumn(1).setPreferredWidth(60);
        columnModel.getColumn(2).setPreferredWidth(table.getBounds().width - 30 - 120);
        columnModel.getColumn(3).setPreferredWidth(60);
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);
        
        TableCellRenderer r = new HeaderRenderer(table.getTableHeader(), 0);
        table.getColumnModel().getColumn(0).setHeaderRenderer(r);
    }
    
    public static DefaultTableModel getDataFromTable(String tableName, Vector tableTitle) {
        
        tableTitle.add(0, "");
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int x, int y) {
                return false;
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
                list.add(table.getValueAt(i, 2).toString());
                //System.out.println(table.getValueAt(i, 3).toString());
            }
        }
        return list;
    }
    
    public void loadListDocument() {
        try {
            String sql = "SELECT title FROM Document";
            CallableStatement cs = ConSQL.CON.prepareCall(sql);
            ResultSet rs = cs.executeQuery();
            DefaultListModel defaultListModel = new DefaultListModel();
            while (rs.next()) {
                defaultListModel.addElement(rs.getString(1));
            }
            txtDocumentList.setModel(defaultListModel);
            btnEditDocument.setEnabled(false);
            btnDeleteDocument.setEnabled(false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public String getDocumentIDbyName(String title) {
        String id = "";
        try {
            String sql = "SELECT id from Document WHERE Title = ?";
            PreparedStatement ps = ConSQL.CON.prepareStatement(sql);
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
            String sql = "SELECT ROW_NUMBER()Over (Order by fileIndex) as STT,URL, PageCount FROM DocumentFile WHERE DocumentID =? order by FileIndex";
            PreparedStatement ps = ConSQL.CON.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            DefaultTableModel defaultTableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int x, int y) {
                    return false;
                }
                
                @Override
                public Class getColumnClass(int col) {
                    switch (col) {
                        case 0:
                            return Boolean.class;
                        case 1:
                            return Integer.class;
                        case 3:
                            return Integer.class;
                        default:
                            return String.class;
                    }
                }
            };
            
            Vector<String> title = new Vector<>();
            title.add("");
            title.add("STT");
            title.add("Đường dẫn");
            title.add("Số trang");
            defaultTableModel.setColumnIdentifiers(title);
            int pageTotal = 0;
            while (rs.next()) {
                Vector data = new Vector();
                data.add(false);
                data.add(rs.getString("STT"));
                data.add(rs.getString("URL"));
                data.add(rs.getString("PageCount"));
                pageTotal += Integer.parseInt(rs.getString("PageCount"));
                defaultTableModel.addRow(data);
            }
            tableFileDetail.setModel(defaultTableModel);
            txtPageNumber.setText(pageTotal + "");
            setTableOption(tableFileDetail);
        } catch (SQLException ex) {
            //Logger.getLogger(formMain.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }
    
    public int getPagesNumber(String pathFile) {
        int pageNumber = 0;
        try {
            WordprocessingMLPackage mLPackage = WordprocessingMLPackage.load(new File(pathFile));
            FlatOpcXmlCreator worker = new FlatOpcXmlCreator(mLPackage);
            org.docx4j.xmlPackage.Package pkg = worker.get();
            List<org.docx4j.xmlPackage.Part> parts = pkg.getPart();
            
            for (int i = 0; i < parts.size(); i++) {
                if (parts.get(i).getName().equalsIgnoreCase("/docProps/app.xml")) {
                    XmlData xmlData = parts.get(i).getXmlData();
                    Element any = xmlData.getAny();
                    NodeList list = any.getChildNodes();
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
            Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pageNumber;
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
        txtPageNumber = new javax.swing.JLabel();
        lbDocumentID = new javax.swing.JTextField();
        pnManageFiles = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableFileDetail = new javax.swing.JTable();
        btnOpenFile = new javax.swing.JButton();
        btnMergeFile = new javax.swing.JButton();
        btnConvertDocxToPDF = new javax.swing.JButton();
        btnInsertNewFile = new javax.swing.JButton();
        btnUpdateFile = new javax.swing.JButton();
        btnDeleteFile = new javax.swing.JButton();
        btnMoveUp = new javax.swing.JButton();
        btnMoveDown = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtDocumentList = new javax.swing.JList();
        btnExport = new javax.swing.JButton();

        jButton4.setText("jButton4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(897, 440));
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });
        getContentPane().setLayout(new java.awt.CardLayout());

        pnManageDocument.setBorder(javax.swing.BorderFactory.createTitledBorder("Quản lý văn bản"));

        lableDocID.setText("Văn bản: ");

        btnInsertDocument.setText("Thêm văn bản");
        btnInsertDocument.setMaximumSize(new java.awt.Dimension(113, 23));
        btnInsertDocument.setMinimumSize(new java.awt.Dimension(113, 23));
        btnInsertDocument.setPreferredSize(new java.awt.Dimension(113, 23));
        btnInsertDocument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertDocumentActionPerformed(evt);
            }
        });

        btnEditDocument.setText("Sửa tên văn bản");
        btnEditDocument.setMaximumSize(new java.awt.Dimension(105, 23));
        btnEditDocument.setMinimumSize(new java.awt.Dimension(105, 23));
        btnEditDocument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditDocumentActionPerformed(evt);
            }
        });

        btnDeleteDocument.setText("Xóa văn bản");
        btnDeleteDocument.setMaximumSize(new java.awt.Dimension(113, 23));
        btnDeleteDocument.setMinimumSize(new java.awt.Dimension(113, 23));
        btnDeleteDocument.setPreferredSize(new java.awt.Dimension(113, 23));
        btnDeleteDocument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteDocumentActionPerformed(evt);
            }
        });

        lbDocumentID.setEditable(false);
        lbDocumentID.setBorder(null);

        javax.swing.GroupLayout pnManageDocumentLayout = new javax.swing.GroupLayout(pnManageDocument);
        pnManageDocument.setLayout(pnManageDocumentLayout);
        pnManageDocumentLayout.setHorizontalGroup(
            pnManageDocumentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnManageDocumentLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lableDocID)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbDocumentID)
                .addGap(18, 18, 18)
                .addComponent(txtPageNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnInsertDocument, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEditDocument, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDeleteDocument, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnManageDocumentLayout.setVerticalGroup(
            pnManageDocumentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnManageDocumentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnManageDocumentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDocumentID)
                    .addGroup(pnManageDocumentLayout.createSequentialGroup()
                        .addGroup(pnManageDocumentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(lableDocID)
                            .addComponent(btnInsertDocument, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEditDocument, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnDeleteDocument, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPageNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pnManageFiles.setBorder(javax.swing.BorderFactory.createTitledBorder("Quản lý File"));

        tableFileDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Số thứ tự", "Đường dẫn", "Số trang"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableFileDetail.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableFileDetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableFileDetailMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableFileDetail);

        btnOpenFile.setText("Mở file");
        btnOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenFileActionPerformed(evt);
            }
        });

        btnMergeFile.setText("Nối file");
        btnMergeFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMergeFileActionPerformed(evt);
            }
        });

        btnConvertDocxToPDF.setText("Chuyển sang pdf");
        btnConvertDocxToPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConvertDocxToPDFActionPerformed(evt);
            }
        });

        btnInsertNewFile.setText("Thêm file mới");
        btnInsertNewFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertNewFileActionPerformed(evt);
            }
        });

        btnUpdateFile.setText("Cập nhật số trang");
        btnUpdateFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateFileActionPerformed(evt);
            }
        });

        btnDeleteFile.setText("Xóa file");
        btnDeleteFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteFileActionPerformed(evt);
            }
        });

        btnMoveUp.setText("Chuyển lên");
        btnMoveUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveUpActionPerformed(evt);
            }
        });

        btnMoveDown.setText("Chuyển xuống");
        btnMoveDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveDownActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnManageFilesLayout = new javax.swing.GroupLayout(pnManageFiles);
        pnManageFiles.setLayout(pnManageFilesLayout);
        pnManageFilesLayout.setHorizontalGroup(
            pnManageFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnManageFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(pnManageFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnOpenFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnInsertNewFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUpdateFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDeleteFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMoveUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMoveDown, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConvertDocxToPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMergeFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnManageFilesLayout.setVerticalGroup(
            pnManageFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnManageFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnManageFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnManageFilesLayout.createSequentialGroup()
                        .addComponent(btnOpenFile)
                        .addGap(18, 18, 18)
                        .addComponent(btnInsertNewFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdateFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteFile)
                        .addGap(18, 18, 18)
                        .addComponent(btnMoveUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMoveDown)
                        .addGap(18, 18, 18)
                        .addComponent(btnConvertDocxToPDF)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMergeFile)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(45, 45, 45))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Danh sách văn bản"));

        txtDocumentList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDocumentListMouseClicked(evt);
            }
        });
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
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(pnManageDocument, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnManageFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                //Process p = Runtime.getRuntime().exec("cmd /c " + list.get(i).toString());
                Process p = Runtime.getRuntime().exec("cmd /c " + "\"" + list.get(i).toString() + "\"");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy file: " + list.get(i).toString(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                //Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnOpenFileActionPerformed
    
    private void btnInsertNewFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertNewFileActionPerformed
        
        DialogInsertNewFile dialogInsertNewFile = new DialogInsertNewFile(this, true, txtDocumentList.getSelectedValue().toString());
        dialogInsertNewFile.setVisible(true);
        String id = getDocumentIDbyName(txtDocumentList.getSelectedValue().toString());
        updateDataTable(id);
        
    }//GEN-LAST:event_btnInsertNewFileActionPerformed
    
    private void txtDocumentListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_txtDocumentListValueChanged
        if (txtDocumentList.getSelectedIndex() > -1) {
            documentID = Integer.parseInt(getDocumentIDbyName(txtDocumentList.getSelectedValue().toString()));
            updateDataTable(String.valueOf(documentID));
            lbDocumentID.setText(txtDocumentList.getSelectedValue().toString());
            btnEditDocument.setEnabled(true);
            btnDeleteDocument.setEnabled(true);
        }
    }//GEN-LAST:event_txtDocumentListValueChanged
    
    private void txtDocumentListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDocumentListMouseClicked
        if (txtDocumentList.getSelectedIndex() >= 0) {
            btnDeleteFile.setEnabled(false);
            btnInsertNewFile.setEnabled(true);
            btnUpdateFile.setEnabled(false);
            btnMergeFile.setEnabled(false);
            btnConvertDocxToPDF.setEnabled(false);
            btnOpenFile.setEnabled(false);
            if (_getRowSelectedCount(tableFileDetail) > 0) {
                btnOpenFile.setEnabled(true);
                btnConvertDocxToPDF.setEnabled(true);
                btnUpdateFile.setEnabled(true);
                btnDeleteFile.setEnabled(true);
            }
        }
    }//GEN-LAST:event_txtDocumentListMouseClicked
    
    private void btnInsertDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertDocumentActionPerformed
        
        String temp = JOptionPane.showInputDialog(null, "Mời bạn nhập tên văn bản (Tên văn bản phải là duy nhất): ", "Nhập", JOptionPane.PLAIN_MESSAGE);
        
        if (null != temp) {
            String sql = "SELECT id FROM Document WHERE Title=?";
            try {
                PreparedStatement preparedStatement = ConSQL.CON.prepareStatement(sql);
                preparedStatement.setString(1, temp);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Tên văn bản đã tồn tại! Xin hãy chọn tên khác", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } else {
                    preparedStatement = ConSQL.CON.prepareStatement("INSERT INTO Document VALUES(?)");
                    preparedStatement.setString(1, temp);
                    if (preparedStatement.executeUpdate() > 0) {
                        /*
                         * Tao thu muc luu document
                         */
                        HandleFolder.createFolderDocument(getDocumentIDbyName(temp));
                        
                        loadListDocument();
                    } else {
                        JOptionPane.showMessageDialog(null, "Thêm mới văn bản không thành công", "Thông báo", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }//GEN-LAST:event_btnInsertDocumentActionPerformed
    
    private int _getRowSelectedCount(JTable table) {
        int count = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, 0).toString().equals("true")) {
                count++;
            }
        }
        return count;
    }
    private void tableFileDetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableFileDetailMouseClicked
        boolean check = Boolean.valueOf(tableFileDetail.getValueAt(tableFileDetail.getSelectedRow(), 0).toString());
        tableFileDetail.setValueAt(!check, tableFileDetail.getSelectedRow(), 0);
        int a = _getRowSelectedCount(tableFileDetail);
        System.out.println(a);
        if (a == 0) {
            tableFileDetail.getColumnModel().getColumn(0).setHeaderValue(Boolean.FALSE);
            tableFileDetail.getTableHeader().repaint();
        } else if (a > 0) {
            if (a == tableFileDetail.getRowCount()) {
                tableFileDetail.getColumnModel().getColumn(0).setHeaderValue(Boolean.TRUE);
                tableFileDetail.getTableHeader().repaint();
            }
            btnOpenFile.setEnabled(true);
            btnConvertDocxToPDF.setEnabled(true);
            btnUpdateFile.setEnabled(true);
            btnDeleteFile.setEnabled(true);
            btnMoveUp.setEnabled(true);
            btnMoveDown.setEnabled(true);
        } else {
            btnOpenFile.setEnabled(false);
            btnConvertDocxToPDF.setEnabled(false);
            btnUpdateFile.setEnabled(false);
            btnDeleteFile.setEnabled(false);
            btnMoveUp.setEnabled(false);
            btnMoveDown.setEnabled(false);
        }
        
        if (a > 1) {
            btnMergeFile.setEnabled(true);
        } else {
            btnMergeFile.setEnabled(false);
        }
        
    }//GEN-LAST:event_tableFileDetailMouseClicked
    public int getIndexbyURL(String url) {
        int index = 0;
        try {
            String sql = "SELECT FileIndex FROM DocumentFile WHERE URL=?";
            PreparedStatement ps = ConSQL.CON.prepareStatement(sql);
            ps.setString(1, url);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                index = Integer.parseInt(rs.getString("FileIndex"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return index;
    }
    
    public void UpdateFileIndex(String url, int t) {
        try {
            int indexNew = getIndexbyURL(url) + t;
            String sql = "UPDATE DocumentFile SET FileIndex=? WHERE URL=?";
            PreparedStatement ps = ConSQL.CON.prepareStatement(sql);
            ps.setInt(1, indexNew);
            ps.setString(2, url);
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void btnUpdateFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateFileActionPerformed
        boolean check = true;
        ArrayList<String> list = getPathChooseFromTable(tableFileDetail);
        int DocumentID = documentID;
        for (int i = 0; i < list.size(); i++) {
            try {
                String url = list.get(i);
                int pageCount = getPagesNumber(url);
                String sql = "UPDATE DocumentFile SET pageCount =? WHERE DocumentID =? and URL =?";
                PreparedStatement ps = ConSQL.CON.prepareStatement(sql);
                ps.setInt(1, pageCount);
                ps.setInt(2, DocumentID);
                ps.setString(3, url);
                
                ps.executeUpdate();
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Lỗi cập nhật file: " + list.get(i));
                check = false;
                Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                
            }
        }
        if (check) {
            
            JOptionPane.showMessageDialog(null, "Cập nhật thành công");
            
            updateDataTable(String.valueOf(DocumentID));
        }
    }//GEN-LAST:event_btnUpdateFileActionPerformed
    
    private void btnDeleteFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteFileActionPerformed
        if (JOptionPane.showConfirmDialog(rootPane, "Bạn thực sự muốn xóa file này?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            boolean check = true;
            ArrayList<String> list = getPathChooseFromTable(tableFileDetail);
            
            for (int i = 0; i < list.size(); i++) {
                try {
                    String url = list.get(i);
                    String sql = "DELETE DocumentFile WHERE DocumentID =? and URL =?";
                    PreparedStatement ps = ConSQL.CON.prepareStatement(sql);
                    ps.setInt(1, documentID);
                    ps.setString(2, url);
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    check = false;
                    JOptionPane.showMessageDialog(null, "Lỗi xóa file");
                    Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (check) {
                updateDataTable(String.valueOf(documentID));
            }
        }
    }//GEN-LAST:event_btnDeleteFileActionPerformed
    
    private void btnEditDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditDocumentActionPerformed
        String temp = null;
        try {
            temp = JOptionPane.showInputDialog(null, "Mời bạn nhập tên văn bản mới(Tên văn bản phải là duy nhất): ", "Nhập", JOptionPane.PLAIN_MESSAGE, null, null, txtDocumentList.getSelectedValue().toString()).toString();
        } catch (NullPointerException ex) {
        }
        if (null != temp && temp.length() > 0) {
            String sql = "SELECT id FROM Document WHERE Title=?";
            try {
                PreparedStatement preparedStatement = ConSQL.CON.prepareStatement(sql);
                preparedStatement.setString(1, temp);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Tên văn bản đã tồn tại! Xin hãy chọn tên khác", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } else {
                    preparedStatement = ConSQL.CON.prepareStatement("update Document set Title=? where ID=?");
                    preparedStatement.setString(1, temp);
                    preparedStatement.setInt(2, FormMain.documentID);
                    if (preparedStatement.executeUpdate() > 0) {
                        loadListDocument();
                    } else {
                        JOptionPane.showMessageDialog(null, "Xảy ra lỗi khi sửa", "Thông báo", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnEditDocumentActionPerformed
    
    private void btnDeleteDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteDocumentActionPerformed
        if (JOptionPane.showConfirmDialog(null, "Bạn thực sự muốn xóa văn bản này?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement pstm = ConSQL.CON.prepareStatement("DELETE Document WHERE ID=?");
                pstm.setInt(1, FormMain.documentID);
                if (pstm.executeUpdate() > 0) {
                    HandleFolder.deleteFolderDocument(String.valueOf(FormMain.documentID));
                    loadListDocument();
                    updateDataTable(null);
                } else {
                    JOptionPane.showMessageDialog(null, "Xóa thất bại", "Thông báo", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnDeleteDocumentActionPerformed
    
    private void btnMergeFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMergeFileActionPerformed
        // TODO add your handling code here:
        String filename = HandleFolder.generateFileName("MergedDocx.docx");
        String pathsave = HandleFolder.PATH_TEMP + filename;
        ArrayList<String> listPath = getPathChooseFromTable(tableFileDetail);
        List<InputStream> listInputStream = new ArrayList<>();
        
        for (int i = 0; i < listPath.size(); i++) {
            try {
                System.out.println(listPath.get(i).toString());
                InputStream is = new FileInputStream(new File(listPath.get(i).toString()));
                listInputStream.add(is);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            
            MergeDocx.mergeDocx(listInputStream, pathsave);
        } catch (Docx4JException ex) {
            Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        DialogSaveFile dialogSaveFile = new DialogSaveFile(this, true);
        dialogSaveFile.setVisible(true);
        String path_save = DialogSaveFile.PATH_SAVE;
        HandleFolder.exportFile(pathsave, path_save);
    }//GEN-LAST:event_btnMergeFileActionPerformed
    
    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
        setTableOption(tableFileDetail);
        System.out.println(tableFileDetail.size().width);
    }//GEN-LAST:event_formWindowStateChanged
    
    private void btnConvertDocxToPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConvertDocxToPDFActionPerformed
        DialogSaveFile dialogSaveFile = new DialogSaveFile(this, true);
        dialogSaveFile.setVisible(true);
        String path_save = DialogSaveFile.PATH_SAVE;
        ConvertDocxToPDF convertDocxToPDF = new ConvertDocxToPDF();
        for (int i = 0; i < tableFileDetail.getRowCount(); i++) {
            if (tableFileDetail.getValueAt(i, 0).toString().equals("true")) {
                String inputPath = tableFileDetail.getValueAt(i, 2).toString();
                convertDocxToPDF.convert(inputPath, path_save);
            }
        }
        JOptionPane.showMessageDialog(rootPane, "Xong");
    }//GEN-LAST:event_btnConvertDocxToPDFActionPerformed
    
    private void btnMoveUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveUpActionPerformed
        
        int rowCount = tableFileDetail.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            boolean check = (boolean) tableFileDetail.getValueAt(i, 0);
            if (check) {
                if (i > 0) {
                    boolean upcheck = (boolean) tableFileDetail.getValueAt(i - 1, 0);
                    if (upcheck == false) {
                        String urlChoose = tableFileDetail.getValueAt(i, 2).toString();
                        String url1 = tableFileDetail.getValueAt(i - 1, 2).toString();
                        UpdateFileIndex(urlChoose, -1);
                        UpdateFileIndex(url1, +1);
                        
                        DefaultTableModel model = (DefaultTableModel) tableFileDetail.getModel();
                        for (int q = 0; q < model.getColumnCount(); q++) {
                            Object o1 = model.getValueAt(i, q);
                            Object o2 = model.getValueAt(i - 1, q);
                            model.setValueAt(o1, i - 1, q);
                            model.setValueAt(o2, i, q);
                        }
                        tableFileDetail.setRowSelectionInterval(i-1, i-1);
                    }
                }
            }
        }
//        if (checkChoose > 1) {
//            JOptionPane.showMessageDialog(null, "Bạn chỉ được chọn 1");
//        } else {
//            int vt = 0;
//            String urlChoose = null;
//            for (int i = 0; i < rowCount; i++) {
//                boolean check = (boolean) tableFileDetail.getValueAt(i, 0);
//                if (check) {
//                    urlChoose = tableFileDetail.getValueAt(i, 2).toString();
//                    vt = i;
//                    System.out.println(urlChoose);
//                    break;
//                }
//            }
//            if (vt != 0) {
//                String url1 = tableFileDetail.getValueAt(vt - 1, 2).toString();
//                UpdateFileIndex(urlChoose, -1);
//                UpdateFileIndex(url1, +1);
//            }
//        }
//        updateDataTable(String.valueOf(FormMain.documentID));

    }//GEN-LAST:event_btnMoveUpActionPerformed
    
    private void btnMoveDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveDownActionPerformed
        
        int rowCount = tableFileDetail.getRowCount();
        for (int i = rowCount-1; i >= 0; i--) {
            boolean check = (boolean) tableFileDetail.getValueAt(i, 0);
            if (check) {
                if (i <rowCount-1) {
                    boolean downcheck = (boolean) tableFileDetail.getValueAt(i + 1, 0);
                    if (downcheck == false) {
                        String urlChoose = tableFileDetail.getValueAt(i, 2).toString();
                        String url1 = tableFileDetail.getValueAt(i +1, 2).toString();
                        UpdateFileIndex(urlChoose, +1);
                        UpdateFileIndex(url1, -1);
                        
                        DefaultTableModel model = (DefaultTableModel) tableFileDetail.getModel();
                        for (int q = 0; q < model.getColumnCount(); q++) {
                            Object o1 = model.getValueAt(i, q);
                            Object o2 = model.getValueAt(i +1, q);
                            model.setValueAt(o1, i + 1, q);
                            model.setValueAt(o2, i, q);
                        }
                        tableFileDetail.setRowSelectionInterval(i+1, i+1);
                    }
                }
            }
        }
//        int rowCount = tableFileDetail.getRowCount();
//        int checkChoose = 0;
//        for (int i = 0; i < rowCount; i++) {
//            boolean check = (boolean) tableFileDetail.getValueAt(i, 0);
//            if (check) {
//                checkChoose++;
//            }
//        }
//        if (checkChoose > 1) {
//            JOptionPane.showMessageDialog(null, "Bạn chỉ được chọn 1");
//        } else {
//            int vt = 0;
//            String urlChoose = null;
//            for (int i = 0; i < rowCount; i++) {
//                boolean check = (boolean) tableFileDetail.getValueAt(i, 0);
//                if (check) {
//                    urlChoose = tableFileDetail.getValueAt(i, 2).toString();
//                    vt = i;
//                    System.out.println(urlChoose);
//                    break;
//                }
//            }
//            if (vt != rowCount - 1) {
//                String url1 = tableFileDetail.getValueAt(vt + 1, 2).toString();
//                UpdateFileIndex(urlChoose, +1);
//                UpdateFileIndex(url1, -1);
//            }
//        }
//        updateDataTable(String.valueOf(FormMain.documentID));
    }//GEN-LAST:event_btnMoveDownActionPerformed
    
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
    private javax.swing.JButton btnConvertDocxToPDF;
    private javax.swing.JButton btnDeleteDocument;
    private javax.swing.JButton btnDeleteFile;
    private javax.swing.JButton btnEditDocument;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnInsertDocument;
    private javax.swing.JButton btnInsertNewFile;
    private javax.swing.JButton btnMergeFile;
    private javax.swing.JButton btnMoveDown;
    private javax.swing.JButton btnMoveUp;
    private javax.swing.JButton btnOpenFile;
    private javax.swing.JButton btnUpdateFile;
    private javax.swing.JButton jButton4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lableDocID;
    private javax.swing.JTextField lbDocumentID;
    private javax.swing.JPanel pnManageDocument;
    private javax.swing.JPanel pnManageFiles;
    private javax.swing.JTable tableFileDetail;
    private javax.swing.JList txtDocumentList;
    private javax.swing.JLabel txtPageNumber;
    // End of variables declaration//GEN-END:variables
}
