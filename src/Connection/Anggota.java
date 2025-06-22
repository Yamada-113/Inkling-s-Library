/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Connection;

import java.util.Date;

/**
 *
 * @author HELLO
 */
public class Anggota {
    // Field private untuk enkapsulasi
    private String id;
    private String nama;
    private String email;
    private String telp;
    private String jenisKelamin;
    private Date tanggalBergabung;

    // Constructor
    public Anggota(String id, String nama, String email, String telp, 
                 String jenisKelamin, Date tanggalBergabung) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.telp = telp;
        this.jenisKelamin = jenisKelamin;
        this.tanggalBergabung = tanggalBergabung;
    }

    // Getter methods
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getEmail() { return email; }
    public String getTelp() { return telp; }
    public String getJenisKelamin() { return jenisKelamin; }
    public Date getTanggalBergabung() { return tanggalBergabung; }

    // Setter methods
    public void setId(String id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setEmail(String email) { this.email = email; }
    public void setTelp(String telp) { this.telp = telp; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }
    public void setTanggalBergabung(Date tanggalBergabung) { this.tanggalBergabung = tanggalBergabung; }
}
