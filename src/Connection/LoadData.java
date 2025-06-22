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
public abstract class LoadData {
    protected Connection connect;
    
    public LoadData(Connection connect) {
        this.connect = connect;
    }
    
    public abstract int loadCountData();

// Subclass 1
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

// Subclass 2
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
}
