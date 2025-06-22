/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package main;

import Connection.*;
import Connection.LoadData.AnggotaDataLoader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author HELLO
 */
public class DashboardMenu extends javax.swing.JPanel {
private Connection connect;
private LoadData[] loaders;
    /**
     * Creates new form DashboardMenu
     */
    public DashboardMenu() {
        this(new DatabaseCon().getConnection());
    }
    
    public DashboardMenu(Connection connect) {
        this.connect = connect;
        initComponents();
        initDataLoaders();
        loadData();
        getRiwayatPeminjaman();
    }
    
     public abstract class LoadData {
        protected Connection connect;
        
        public LoadData(Connection connect) {
            this.connect = connect;
        }
        
        public abstract int loadCountData();
    }
    
    public class AnggotaDataLoader extends LoadData {
        public AnggotaDataLoader(Connection connect) {
            super(connect);
        }
        
        @Override
        public int loadCountData() {
            try {
                String sql = "SELECT COUNT(*) FROM anggota";
                try (PreparedStatement stmt = connect.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
    
    public class BukuDataLoader extends LoadData {
        public BukuDataLoader(Connection connect) {
            super(connect);
        }
        
        @Override
        public int loadCountData() {
            try {
                String sql = "SELECT COUNT(*) FROM buku";
                try (PreparedStatement stmt = connect.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
    
    public class PeminjamanDataLoader extends LoadData {
        public PeminjamanDataLoader(Connection connect) {
            super(connect);
        }

        @Override
        public int loadCountData() {
            try {
                String sql = "SELECT COUNT(*) FROM peminjaman";
                try (PreparedStatement stmt = connect.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
    
    public class PengembalianDataLoader extends LoadData {
        public PengembalianDataLoader(Connection connect) {
            super(connect);
        }

        @Override
        public int loadCountData() {
            try {
                String sql = "SELECT COUNT(*) FROM pengembalian";
                try (PreparedStatement stmt = connect.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
    
    private void initDataLoaders() {
        this.loaders = new LoadData[] {
            new AnggotaDataLoader(connect),
            new BukuDataLoader(connect),
            new PeminjamanDataLoader(connect),
            new PengembalianDataLoader(connect)
        };
    }
    
    // Overloading method
    private void loadData() {
        loadData(false);
    }
    
    private void loadData(boolean forceReload) {
        totalAnggota.setText(String.valueOf(loaders[0].loadCountData()));
        totalBuku.setText(String.valueOf(loaders[1].loadCountData()));
        totalPinjam.setText(String.valueOf(loaders[2].loadCountData()));
        totalPengembalian.setText(String.valueOf(loaders[3].loadCountData()));
    }
    
    private void getRiwayatPeminjaman() {
        DefaultTableModel model = (DefaultTableModel) tableRiwayat.getModel();
        model.setRowCount(0);

    try {
        String sqlSelect = """
            SELECT 
            p.id_peminjaman,
            a.nama AS nama_anggota,
            b.judul,
            dp.jumlah AS total_pinjam,
            p.tanggal_pinjam,
            p.tanggal_kembali,
            CASE 
                WHEN pg.id_pengembalian IS NOT NULL THEN 'Dikembalikan'
                WHEN p.tanggal_kembali < CURDATE() THEN 'Terlambat'
                ELSE 'Dipinjam'
            END AS status
        FROM peminjaman p
        JOIN anggota a ON p.id_anggota = a.id_anggota
        JOIN detail_peminjaman dp ON p.id_peminjaman = dp.id_peminjaman
        JOIN buku b ON dp.id_buku = b.id_buku
        LEFT JOIN pengembalian pg ON p.id_peminjaman = pg.id_peminjaman
        ORDER BY p.tanggal_pinjam DESC;
            """;
            
        PreparedStatement stmtSelect = connect.prepareStatement(sqlSelect);
        ResultSet rs = stmtSelect.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("id_peminjaman"),
                rs.getString("nama_anggota"),
                rs.getString("judul"),
                rs.getInt("total_pinjam"),
                rs.getDate("tanggal_pinjam"),
                rs.getDate("tanggal_kembali"),
                rs.getString("status")
            });
        }

        tableRiwayat.setModel(model);
        
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal mengambil data riwayat!", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        totalAnggota = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        totalPinjam = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        totalPengembalian = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        totalBuku = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableRiwayat = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(1133, 690));
        setLayout(new java.awt.CardLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(173, 173, 248));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("ANGGOTA");

        totalAnggota.setBackground(new java.awt.Color(204, 204, 204));
        totalAnggota.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        totalAnggota.setText("999");

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Anggota.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 45, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(totalAnggota)
                        .addGap(18, 18, 18)))
                .addComponent(jLabel11)
                .addGap(12, 12, 12))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalAnggota)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addGap(19, 19, 19))
        );

        jPanel2.setBackground(new java.awt.Color(173, 173, 248));
        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setPreferredSize(new java.awt.Dimension(175, 81));

        jLabel2.setText("PEMINJAMAN");

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Borrow Book.png"))); // NOI18N

        totalPinjam.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        totalPinjam.setText("999");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(totalPinjam)
                        .addGap(18, 18, 18)))
                .addComponent(jLabel9)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalPinjam)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(19, 19, 19))
        );

        jPanel4.setBackground(new java.awt.Color(173, 173, 248));
        jPanel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel4.setPreferredSize(new java.awt.Dimension(175, 81));

        jLabel3.setText("PENGEMBALIAN");

        totalPengembalian.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        totalPengembalian.setText("999");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Return Book.png"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(totalPengembalian)
                        .addGap(18, 18, 18)))
                .addComponent(jLabel5)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalPengembalian))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel5)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(173, 173, 248));
        jPanel5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel5.setPreferredSize(new java.awt.Dimension(175, 81));

        jLabel4.setText("BUKU");

        totalBuku.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        totalBuku.setText("999");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Book Stack.png"))); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap(62, Short.MAX_VALUE)
                        .addComponent(totalBuku)
                        .addGap(18, 18, 18)))
                .addComponent(jLabel7)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)
                        .addGap(5, 5, 5)
                        .addComponent(totalBuku))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel7)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel13.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(153, 153, 153));
        jLabel13.setText("Riwayat Peminjaman Buku");

        jLabel19.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(8, 37, 69));
        jLabel19.setText("Data > Dashboard");

        jLabel14.setIcon(new javax.swing.ImageIcon("C:\\Users\\HELLO\\OneDrive\\Gambar\\Elemen\\iMac.png")); // NOI18N

        tableRiwayat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Peminjaman", "Anggota", "ID Buku", "Jumlah Peminjaman", "Tanggal Peminjaman", "Tanggal Pengembalian", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableRiwayat.setPreferredSize(new java.awt.Dimension(525, 400));
        tableRiwayat.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tableRiwayat);
        if (tableRiwayat.getColumnModel().getColumnCount() > 0) {
            tableRiwayat.getColumnModel().getColumn(0).setResizable(false);
            tableRiwayat.getColumnModel().getColumn(1).setResizable(false);
            tableRiwayat.getColumnModel().getColumn(2).setResizable(false);
            tableRiwayat.getColumnModel().getColumn(3).setResizable(false);
            tableRiwayat.getColumnModel().getColumn(4).setResizable(false);
            tableRiwayat.getColumnModel().getColumn(5).setResizable(false);
            tableRiwayat.getColumnModel().getColumn(6).setResizable(false);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(0, 620, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel14))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
                .addGap(42, 42, 42)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );

        add(jPanel3, "card2");
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableRiwayat;
    private javax.swing.JLabel totalAnggota;
    private javax.swing.JLabel totalBuku;
    private javax.swing.JLabel totalPengembalian;
    private javax.swing.JLabel totalPinjam;
    // End of variables declaration//GEN-END:variables

    private int anggota(){
        int jumlahAnggota = 0;
            try{
                String count = "SELECT COUNT(*) AS total FROM anggota";
                PreparedStatement stmt = connect.prepareStatement(count);
                ResultSet rs = stmt.executeQuery();

                if(rs.next()){
                    jumlahAnggota = rs.getInt("total");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return jumlahAnggota;
    }
    
    private int buku(){
        int jumlahBuku = 0;
            try{
                String count = "SELECT COUNT(*) AS total FROM buku";
                PreparedStatement stmt = connect.prepareStatement(count);
                ResultSet rs = stmt.executeQuery();

                if(rs.next()){
                    jumlahBuku = rs.getInt("total");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return jumlahBuku;
    }
    
    private int peminjaman(){
        int jumlahPeminjaman = 0;
            try{
                String count = "SELECT COUNT(*) AS total FROM peminjaman";
                PreparedStatement stmt = connect.prepareStatement(count);
                ResultSet rs = stmt.executeQuery();

                if(rs.next()){
                    jumlahPeminjaman = rs.getInt("total");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return jumlahPeminjaman;
    }
    
    private int pengembalian(){
        int jumlahPengembalian = 0;
            try{
                String count = "SELECT COUNT(*) AS total FROM pengembalian";
                PreparedStatement stmt = connect.prepareStatement(count);
                ResultSet rs = stmt.executeQuery();

                if(rs.next()){
                    jumlahPengembalian = rs.getInt("total");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return jumlahPengembalian;
    }
}
