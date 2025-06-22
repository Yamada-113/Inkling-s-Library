/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Connection;

import java.util.Date;

/**
 *
 * @author HELLO
 */
public interface IServicePeminjaman{
    String generateIDPeminjaman();
    int ambilStokDariDatabase(String idBuku);
    boolean prosesPeminjaman(String idBuku, String idAnggota, int jumlahPinjam, Date tglPinjam, Date tglKembali);
    void updateStokBuku(String idBuku, int jumlahPinjam);
}
