/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Connection;

import Connection.DatabaseCon;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author HELLO
 */
public class DataPeminjaman implements IServicePeminjaman {
 private Connection connect;
    
    public DataPeminjaman() {
        DatabaseCon data = new DatabaseCon();
        connect = data.getConnection();
    }
    
    @Override
    public String generateIDPeminjaman() {
        try {
            String code = "PMJ";
        String yyMM = new SimpleDateFormat("yyMM").format(new Date());
        String sql = "SELECT id_peminjaman FROM peminjaman WHERE id_peminjaman LIKE ? ORDER BY id_peminjaman DESC LIMIT 1";
        PreparedStatement stmt = connect.prepareStatement(sql);
        stmt.setString(1, code + yyMM + "%");
        ResultSet rs = stmt.executeQuery();

        int nomor = 1;
        if (rs.next()) {
            String lastID = rs.getString("id_Peminjaman");
            String lastNumber = lastID.substring(9); 
            nomor = Integer.parseInt(lastNumber) + 1;
        }

        String newID = code + yyMM + String.format("%04d", nomor);
      
        rs.close();
        stmt.close();
        return newID;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public int ambilStokDariDatabase(String idBuku) {
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
   
    @Override
    public boolean prosesPeminjaman(String idBuku, String idAnggota, int jumlahPinjam, Date tglPinjam, Date tglKembali) {
        try {
            String idPinjam = generateIDPeminjaman();
            java.sql.Date tglPinjamSql = new java.sql.Date(tglPinjam.getTime());
            // Ebook otomatis perpanjang 30 hari
            java.sql.Date tglKembaliSql = new java.sql.Date(tglPinjam.getTime() + (30 * 24 * 60 * 60 * 1000));
            
            String sqlInsert = "INSERT INTO peminjaman (id_peminjaman, id_anggota, total_pinjam, tanggal_pinjam, tanggal_kembali) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtInsert = connect.prepareStatement(sqlInsert);
            stmtInsert.setString(1, idPinjam);
            stmtInsert.setString(2, idAnggota);
            stmtInsert.setInt(3, jumlahPinjam);
            stmtInsert.setDate(4, tglPinjamSql);
            stmtInsert.setDate(5, tglKembaliSql);
            stmtInsert.executeUpdate();
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void updateStokBuku(String idBuku, int jumlahPinjam) {
        try {
            String sqlUpdate = "UPDATE buku SET stok_buku = stok_buku - ? WHERE id_buku = ?";
            PreparedStatement stmtUpdate = connect.prepareStatement(sqlUpdate);
            stmtUpdate.setInt(1, jumlahPinjam);
            stmtUpdate.setString(2, idBuku);
            stmtUpdate.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
