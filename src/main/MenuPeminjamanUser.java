/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package main;

import Connection.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author HELLO
 */
public class MenuPeminjamanUser extends javax.swing.JPanel {
private Connection connect;
private String adaPeminjaman = null;
private boolean check = false;
private IServicePeminjaman servicePeminjaman;
    /**
     * Creates new form MenuPeminjaman
     */
    public MenuPeminjamanUser() {
        initComponents();
        DatabaseCon data = new DatabaseCon();
        connect = data.getConnection();
        
        servicePeminjaman = new DataPeminjaman();
        generateIDAnggota();
        generateIDPinjam();
        getData();
    }
    
    private String generateIDPinjam() {
        String id = servicePeminjaman.generateIDPeminjaman();
        this.id.setText(id);
        return id;
    }
    
    private String generateIDAnggota() {
    try {
        String code = "AGT";
        String yyMM = new SimpleDateFormat("yyMM").format(new Date());
        String sql = "SELECT id_anggota FROM anggota WHERE id_anggota LIKE ? ORDER BY id_anggota DESC LIMIT 1";
        PreparedStatement stmt = connect.prepareStatement(sql);
        stmt.setString(1, code + yyMM + "%");
        ResultSet rs = stmt.executeQuery();

        int nomor = 1;
        if (rs.next()) {
            String lastID = rs.getString("id_anggota");
            String lastNumber = lastID.substring(9); // Ambil 4 digit terakhir
            nomor = Integer.parseInt(lastNumber) + 1;
        }

        String newID = code + yyMM + String.format("%04d", nomor);
        anggotaID.setText(newID);

        rs.close();
        stmt.close();
        return newID;

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
      
    private void prosesPeminjaman() {  
        if (adaPeminjaman == null) {
            adaPeminjaman = generateIDPinjam();
        }
        String idPinjam = adaPeminjaman;
        String idBuku = IDBuku.getText();
        String idNamaAnggota = idAnggota.getText();
        int jumlahPinjam = Integer.parseInt(totalPinjam.getText());

        java.util.Date tglPinjamUtil = tglPinjam.getDate(); 
        java.util.Date tglKembaliUtil = tglKembali.getDate(); 

        if (tglPinjamUtil == null || tglKembaliUtil == null) {
            JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong!");
            return;
        }

        java.sql.Date tglPinjamSql = new java.sql.Date(tglPinjamUtil.getTime());
        java.sql.Date tglKembaliSql = new java.sql.Date(tglKembaliUtil.getTime());

        int stok = ambilStokDariDatabase(idBuku);

        if (jumlahPinjam > stok) {
            JOptionPane.showMessageDialog(this, "Jumlah pinjam melebihi stok!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            //Jika ID pinjam kosong/blm ada transaksi maka akan masuk ke dalam table peminjaman SEKALI
            //agar ID pinjam tetap sama jika anggota ingin menambah buku yang mau dipinjam
           if(!check){
            // Insert ke peminjaman
            String sqlInsert = "INSERT INTO peminjaman (id_peminjaman, id_anggota, total_pinjam, tanggal_pinjam, tanggal_kembali) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtInsert = connect.prepareStatement(sqlInsert);
            stmtInsert.setString(1, idPinjam);
            stmtInsert.setString(2, idNamaAnggota);
            stmtInsert.setInt(3, jumlahPinjam); 
            stmtInsert.setDate(4, tglPinjamSql);
            stmtInsert.setDate(5, tglKembaliSql);
            stmtInsert.executeUpdate();
            check = true;
           }
            
            //Insert ke Detail_Peminjaman
            String sqlInsertDetail = "INSERT INTO detail_peminjaman (id_peminjaman, nama, id_buku, judul, jumlah) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtInsertDetail = connect.prepareStatement(sqlInsertDetail);
            stmtInsertDetail.setString(1, idPinjam);
            stmtInsertDetail.setString(2, idNamaAnggota);
            stmtInsertDetail.setString(3, idBuku);
            stmtInsertDetail.setString(4, ambilJudulBuku(idBuku));
            stmtInsertDetail.setInt(5, jumlahPinjam); 
            stmtInsertDetail.executeUpdate();
            
            //Update total bnyknya buku yang di pinjam 
            String sqlUpdateTotal = "UPDATE peminjaman SET total_pinjam = (SELECT SUM(jumlah) FROM detail_peminjaman WHERE id_peminjaman = ?) WHERE id_peminjaman = ?";
            PreparedStatement stmtUpdateTotal = connect.prepareStatement(sqlUpdateTotal);
            stmtUpdateTotal.setString(1, idPinjam);
            stmtUpdateTotal.setString(2, idPinjam);
            stmtUpdateTotal.executeUpdate();
            
            int pilihan = JOptionPane.showConfirmDialog(this, "Mau tambah buku?", "Tambah Buku", JOptionPane.YES_NO_OPTION);
                if (pilihan == JOptionPane.NO_OPTION) {
                    adaPeminjaman = null;
                    check = false;
                    clearForm();
                    getData();
                    JOptionPane.showMessageDialog(this, "Peminjaman selesai!");
                }

            // Update stok buku
            String sqlUpdate = "UPDATE buku SET stok_buku = stok_buku - ? WHERE id_buku = ?";
            PreparedStatement stmtUpdate = connect.prepareStatement(sqlUpdate);
            stmtUpdate.setInt(1, jumlahPinjam);
            stmtUpdate.setString(2, idBuku);
            stmtUpdate.executeUpdate();

            // Gabungan data untuk tampilan table peminjaman rawwwwrrrrrrr
            String sqlSelect = "SELECT p.id_peminjaman, a.nama, p.total_pinjam, p.tanggal_pinjam, p.tanggal_kembali " +
                               "FROM peminjaman p " +
                               "JOIN anggota a ON p.id_anggota = a.id_anggota " +
                               "WHERE p.id_peminjaman = ?";
            PreparedStatement stmtSelect = connect.prepareStatement(sqlSelect);
            stmtSelect.setString(1, idPinjam);
            ResultSet rs = stmtSelect.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tablePinjam.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_peminjaman"),
                    rs.getString("nama"),
//                    rs.getString("judul"),
                    rs.getDate("tanggal_pinjam"),
                    rs.getDate("tanggal_kembali")
                });
            }
            tablePinjam.setModel(model);

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
    private String ambilJudulBuku(String idBuku) {
        try {
            Connection conn = new DatabaseCon().getConnection();
            String sql = "SELECT judul FROM buku WHERE id_buku = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, idBuku);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("judul");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
}
    
    private int ambilStokDariDatabase(String idBuku) {
        int stok = 0;
        try {
            Connection conn = new DatabaseCon().getConnection();
            String sql = "SELECT stok_buku FROM buku WHERE id_buku = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, idBuku);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stok = rs.getInt("stok_buku");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stok;
}
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablePinjam = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        add = new javax.swing.JButton();
        panelAdd = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        addPinjam = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        id = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        idAnggota = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        IDBuku = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        tglPinjam = new com.toedter.calendar.JDateChooser();
        jLabel28 = new javax.swing.JLabel();
        totalPinjam = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        tglKembali = new com.toedter.calendar.JDateChooser();
        pilihBuku = new javax.swing.JButton();
        pilihAnggota = new javax.swing.JButton();
        daftarAnggota = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        anggotaID = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        nama = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        telp = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        laki = new javax.swing.JRadioButton();
        perempuan = new javax.swing.JRadioButton();
        jLabel15 = new javax.swing.JLabel();
        tanggal = new com.toedter.calendar.JDateChooser();
        jLabel29 = new javax.swing.JLabel();
        email = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();

        setLayout(new java.awt.CardLayout());

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(new java.awt.CardLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Berlin Sans FB Demi", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(8, 37, 69));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Borrow Book.png"))); // NOI18N
        jLabel1.setText("Data Peminjaman Buku Inkling's Library");

        jLabel4.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(8, 37, 69));
        jLabel4.setText("Transaksi > Peminjaman");

        jLabel13.setIcon(new javax.swing.ImageIcon("C:\\Users\\HELLO\\OneDrive\\Gambar\\Elemen\\Borrow Book.png")); // NOI18N

        tablePinjam.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID Peminjaman", "ID Anggota", "Total Peminjaman", "Tanggal Pinjam", "Tanggal Kembali"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablePinjam.setPreferredSize(new java.awt.Dimension(525, 400));
        tablePinjam.setRequestFocusEnabled(false);
        tablePinjam.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tablePinjam);
        if (tablePinjam.getColumnModel().getColumnCount() > 0) {
            tablePinjam.getColumnModel().getColumn(0).setResizable(false);
            tablePinjam.getColumnModel().getColumn(1).setResizable(false);
            tablePinjam.getColumnModel().getColumn(2).setResizable(false);
            tablePinjam.getColumnModel().getColumn(3).setResizable(false);
            tablePinjam.getColumnModel().getColumn(4).setResizable(false);
        }

        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Add.png"))); // NOI18N

        add.setText("Add");
        add.setPreferredSize(new java.awt.Dimension(33, 23));
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel13))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel1)))
                .addGap(71, 71, 71)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(add, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        mainPanel.add(jPanel1, "card2");

        panelAdd.setBackground(new java.awt.Color(255, 255, 255));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Anggota.png"))); // NOI18N

        jLabel10.setFont(new java.awt.Font("Berlin Sans FB", 0, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 102));
        jLabel10.setText("Tambah Data Peminjaman");

        addPinjam.setText("Add");
        addPinjam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPinjamActionPerformed(evt);
            }
        });

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel11.setForeground(new java.awt.Color(102, 102, 102));
        jLabel11.setText("ID Peminjaman");

        id.setEditable(false);
        id.setBackground(new java.awt.Color(255, 255, 255));
        id.setForeground(new java.awt.Color(102, 102, 102));
        id.setToolTipText("");
        id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idActionPerformed(evt);
            }
        });

        jLabel12.setForeground(new java.awt.Color(102, 102, 102));
        jLabel12.setText("ID Anggota");

        idAnggota.setEditable(false);
        idAnggota.setBackground(new java.awt.Color(255, 255, 255));
        idAnggota.setForeground(new java.awt.Color(0, 0, 0));

        jLabel14.setForeground(new java.awt.Color(102, 102, 102));
        jLabel14.setText("ID Buku");

        IDBuku.setEditable(false);
        IDBuku.setBackground(new java.awt.Color(255, 255, 255));
        IDBuku.setForeground(new java.awt.Color(0, 0, 0));

        jLabel16.setForeground(new java.awt.Color(102, 102, 102));
        jLabel16.setText("Tanggal Peminjaman");

        tglPinjam.setBackground(new java.awt.Color(255, 255, 255));
        tglPinjam.setMinimumSize(new java.awt.Dimension(72, 12));
        tglPinjam.setPreferredSize(new java.awt.Dimension(72, 12));

        jLabel28.setForeground(new java.awt.Color(102, 102, 102));
        jLabel28.setText("Total Peminjaman");

        totalPinjam.setBackground(new java.awt.Color(255, 255, 255));
        totalPinjam.setForeground(new java.awt.Color(0, 0, 0));

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Cancel.png"))); // NOI18N

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Add2.png"))); // NOI18N

        jLabel19.setIcon(new javax.swing.ImageIcon("C:\\Users\\HELLO\\OneDrive\\Gambar\\Elemen\\User.png")); // NOI18N
        jLabel19.setText("jLabel9");

        jLabel20.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(8, 37, 69));
        jLabel20.setText("Data > Anggota");

        jLabel21.setForeground(new java.awt.Color(102, 102, 102));
        jLabel21.setText("Tanggal Pengembalian");

        tglKembali.setBackground(new java.awt.Color(255, 255, 255));
        tglKembali.setMinimumSize(new java.awt.Dimension(72, 12));
        tglKembali.setPreferredSize(new java.awt.Dimension(72, 12));

        pilihBuku.setText("...");
        pilihBuku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihBukuActionPerformed(evt);
            }
        });

        pilihAnggota.setText("...");
        pilihAnggota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihAnggotaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelAddLayout = new javax.swing.GroupLayout(panelAdd);
        panelAdd.setLayout(panelAddLayout);
        panelAddLayout.setHorizontalGroup(
            panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAddLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAddLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelAddLayout.createSequentialGroup()
                        .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelAddLayout.createSequentialGroup()
                                .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelAddLayout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(addPinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(29, 29, 29)
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton2))
                                    .addGroup(panelAddLayout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel10)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20))
                            .addGroup(panelAddLayout.createSequentialGroup()
                                .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelAddLayout.createSequentialGroup()
                                        .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(id, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(totalPinjam, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelAddLayout.createSequentialGroup()
                                                .addComponent(idAnggota, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(pilihAnggota, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(18, 18, 18)
                                        .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(tglPinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel16)
                                            .addComponent(jLabel21)
                                            .addComponent(tglKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(panelAddLayout.createSequentialGroup()
                                        .addComponent(IDBuku, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(pilihBuku, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        panelAddLayout.setVerticalGroup(
            panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAddLayout.createSequentialGroup()
                .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAddLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(panelAddLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(jLabel19))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17))
                    .addComponent(addPinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelAddLayout.createSequentialGroup()
                        .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelAddLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(id, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelAddLayout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tglPinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(idAnggota, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pilihAnggota, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(tglKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalPinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(IDBuku, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pilihBuku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(250, Short.MAX_VALUE))
        );

        mainPanel.add(panelAdd, "card3");

        daftarAnggota.setBackground(new java.awt.Color(255, 255, 255));

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Anggota.png"))); // NOI18N

        jLabel23.setFont(new java.awt.Font("Berlin Sans FB", 0, 24)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(0, 0, 102));
        jLabel23.setText("Daftar Data Keanggotaan");

        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setText("Cancel");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel24.setForeground(new java.awt.Color(102, 102, 102));
        jLabel24.setText("ID");

        anggotaID.setEditable(false);
        anggotaID.setBackground(new java.awt.Color(255, 255, 255));
        anggotaID.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        anggotaID.setForeground(new java.awt.Color(102, 102, 102));
        anggotaID.setToolTipText("");
        anggotaID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                anggotaIDActionPerformed(evt);
            }
        });

        jLabel25.setForeground(new java.awt.Color(102, 102, 102));
        jLabel25.setText("Nama");

        nama.setBackground(new java.awt.Color(255, 255, 255));
        nama.setForeground(new java.awt.Color(0, 0, 0));

        jLabel26.setForeground(new java.awt.Color(102, 102, 102));
        jLabel26.setText("No. Telepon");

        telp.setBackground(new java.awt.Color(255, 255, 255));
        telp.setForeground(new java.awt.Color(0, 0, 0));

        jLabel27.setForeground(new java.awt.Color(102, 102, 102));
        jLabel27.setText("Jenis Kelamin");

        laki.setBackground(new java.awt.Color(255, 255, 255));
        laki.setForeground(new java.awt.Color(102, 102, 102));
        laki.setText("Laki - Laki");
        laki.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lakiActionPerformed(evt);
            }
        });

        perempuan.setBackground(new java.awt.Color(255, 255, 255));
        perempuan.setForeground(new java.awt.Color(102, 102, 102));
        perempuan.setText("Perempuan");

        jLabel15.setForeground(new java.awt.Color(102, 102, 102));
        jLabel15.setText("Tanggal Bergabung");

        tanggal.setBackground(new java.awt.Color(255, 255, 255));
        tanggal.setMinimumSize(new java.awt.Dimension(72, 12));
        tanggal.setPreferredSize(new java.awt.Dimension(72, 12));

        jLabel29.setForeground(new java.awt.Color(102, 102, 102));
        jLabel29.setText("Email");

        email.setBackground(new java.awt.Color(255, 255, 255));
        email.setForeground(new java.awt.Color(0, 0, 0));

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Cancel.png"))); // NOI18N

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Add2.png"))); // NOI18N

        jLabel34.setIcon(new javax.swing.ImageIcon("C:\\Users\\HELLO\\OneDrive\\Gambar\\Elemen\\User.png")); // NOI18N
        jLabel34.setText("jLabel9");

        jLabel35.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(8, 37, 69));
        jLabel35.setText("Data > Anggota");

        javax.swing.GroupLayout daftarAnggotaLayout = new javax.swing.GroupLayout(daftarAnggota);
        daftarAnggota.setLayout(daftarAnggotaLayout);
        daftarAnggotaLayout.setHorizontalGroup(
            daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(daftarAnggotaLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(daftarAnggotaLayout.createSequentialGroup()
                        .addGroup(daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(daftarAnggotaLayout.createSequentialGroup()
                                .addComponent(laki)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(perempuan))
                            .addComponent(jLabel27)
                            .addComponent(jLabel26))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(daftarAnggotaLayout.createSequentialGroup()
                        .addGroup(daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(daftarAnggotaLayout.createSequentialGroup()
                                .addGroup(daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, daftarAnggotaLayout.createSequentialGroup()
                                        .addComponent(jLabel33)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(29, 29, 29)
                                        .addComponent(jLabel32)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton3))
                                    .addGroup(daftarAnggotaLayout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel23)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 388, Short.MAX_VALUE)
                                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel35))
                            .addGroup(daftarAnggotaLayout.createSequentialGroup()
                                .addGroup(daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(telp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(anggotaID, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nama, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(email, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(18, 18, 18)
                                .addGroup(daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        daftarAnggotaLayout.setVerticalGroup(
            daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(daftarAnggotaLayout.createSequentialGroup()
                .addGroup(daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(daftarAnggotaLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(daftarAnggotaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35)
                            .addComponent(jLabel34))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel32))
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(daftarAnggotaLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(anggotaID, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(daftarAnggotaLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nama, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(telp, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(daftarAnggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laki)
                    .addComponent(perempuan))
                .addContainerGap(190, Short.MAX_VALUE))
        );

        mainPanel.add(daftarAnggota, "card3");

        add(mainPanel, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void addPinjamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPinjamActionPerformed
        // TODO add your handling code here:
        prosesPeminjaman();
    }//GEN-LAST:event_addPinjamActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        mainPanel.removeAll();
        mainPanel.add(jPanel1);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idActionPerformed

    private void pilihAnggotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihAnggotaActionPerformed
        // TODO add your handling code here:
        new PilihAnggota(this).setVisible(true);
    }//GEN-LAST:event_pilihAnggotaActionPerformed

    private void pilihBukuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihBukuActionPerformed
        // TODO add your handling code here:
        new PilihBuku(this).setVisible(true);
    }//GEN-LAST:event_pilihBukuActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        // TODO add your handling code here:
        String email = JOptionPane.showInputDialog(this, "Masukkan Email kamu terlebih dahulu: ");
        
    try {
        String sql = "SELECT * FROM anggota WHERE email = ?";
        PreparedStatement stmt;
        stmt = connect.prepareStatement(sql);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Email belum terdaftar sebagai anggota! Silakan daftar terlebih dahulu.");
                mainPanel.removeAll();
                mainPanel.add(daftarAnggota);
                mainPanel.repaint();
                mainPanel.revalidate();
            } else {
                // Lanjut ke menu peminjaman
                mainPanel.removeAll();
                mainPanel.add(panelAdd);
                mainPanel.repaint();
                mainPanel.revalidate();
            }
    } catch (SQLException ex) {
        Logger.getLogger(MenuPeminjamanUser.class.getName()).log(Level.SEVERE, null, ex);
    }
    }//GEN-LAST:event_addActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        java.util.Date tanggalInput = tanggal.getDate();

        if(nama.getText().isEmpty() || telp.getText().isEmpty() || (!laki.isSelected() && !perempuan.isSelected()) || tanggalInput == null){
            JOptionPane.showMessageDialog(this, "Data Tidak Boleh Ada yang Kosong!", "Try Again", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if(!isNumber(telp.getText())){
            JOptionPane.showMessageDialog(this, "No. Telepon Harus Berupa Angka!", "Try Again", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(tanggalInput.getTime());
        String newID = generateIDAnggota();
        String isSelected = "";

        if (laki.isSelected()) {
            isSelected = "Laki - laki";
        } else if (perempuan.isSelected()) {
            isSelected = "Perempuan";
        }

        try {
            String sql = "INSERT INTO anggota (id_anggota, nama, email, telp, jenis_kelamin, tanggal_bergabung) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connect.prepareStatement(sql);
            stmt.setString(1, newID);
            stmt.setString(2, nama.getText());
            stmt.setString(3, email.getText());
            stmt.setString(4, telp.getText());
            stmt.setString(5, isSelected);
            stmt.setDate(6, sqlDate);
            stmt.executeUpdate();
            stmt.close();

            jButton2.setText("Back");
            clearForm();

            JOptionPane.showMessageDialog(this, "Data Berhasil Ditambahkan!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data Gagal Ditambahkan! X-X", "Failed", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        mainPanel.removeAll();
        mainPanel.add(jPanel1);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void anggotaIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_anggotaIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_anggotaIDActionPerformed

    private void lakiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lakiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lakiActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField IDBuku;
    private javax.swing.JButton add;
    private javax.swing.JButton addPinjam;
    private javax.swing.JTextField anggotaID;
    private javax.swing.JPanel daftarAnggota;
    private javax.swing.JTextField email;
    private javax.swing.JTextField id;
    private javax.swing.JTextField idAnggota;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton laki;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField nama;
    private javax.swing.JPanel panelAdd;
    private javax.swing.JRadioButton perempuan;
    private javax.swing.JButton pilihAnggota;
    private javax.swing.JButton pilihBuku;
    private javax.swing.JTable tablePinjam;
    private com.toedter.calendar.JDateChooser tanggal;
    private javax.swing.JTextField telp;
    private com.toedter.calendar.JDateChooser tglKembali;
    private com.toedter.calendar.JDateChooser tglPinjam;
    private javax.swing.JTextField totalPinjam;
    // End of variables declaration//GEN-END:variables

    public void setDataBuku(String idBuku) {
        IDBuku.setText(idBuku);
    }
    public void setDataAnggota (String anggota) {
        idAnggota.setText(anggota);
    }
    
    public static boolean isNumber(String e){
        return e.matches("\\d+");
    }

    private void getData() {
        DefaultTableModel model = (DefaultTableModel) tablePinjam.getModel();
        model.setRowCount(0);

        try {
            String sqlSelect = "SELECT id_peminjaman, id_anggota, total_pinjam, tanggal_pinjam, tanggal_kembali FROM peminjaman";
            PreparedStatement stmtSelect = connect.prepareStatement(sqlSelect);
            ResultSet rs = stmtSelect.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_peminjaman"),
                    rs.getString("id_anggota"),
//                    rs.getString("id_buku"),
                    rs.getInt("total_pinjam"),
                    rs.getDate("tanggal_pinjam"),
                    rs.getDate("tanggal_kembali")
                });
            }

            tablePinjam.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
}
    
    private void clearForm() {
        idAnggota.setText("");
        totalPinjam.setText("");
        IDBuku.setText("");
        tglPinjam.setDate(null);
        tglKembali.setDate(null);
        tablePinjam.clearSelection();
    }
}

