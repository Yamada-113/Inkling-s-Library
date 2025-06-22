-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 22, 2025 at 05:29 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `inklings_library`
--

-- --------------------------------------------------------

--
-- Table structure for table `anggota`
--

CREATE TABLE `anggota` (
  `id_anggota` varchar(20) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `telp` varchar(15) NOT NULL,
  `jenis_kelamin` varchar(50) NOT NULL,
  `tanggal_bergabung` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `anggota`
--

INSERT INTO `anggota` (`id_anggota`, `nama`, `email`, `telp`, `jenis_kelamin`, `tanggal_bergabung`) VALUES
('AGT25060002', 'Budi Santoso', 'budi.santoso@example.com', '082233445566', 'Laki-laki', '2023-02-20'),
('AGT25060003', 'Citra Dewi', 'citra.dewi@example.com', '083344556677', 'Perempuan', '2023-03-10'),
('AGT25060004', 'Dian Pratama', 'dian.pratama@example.com', '084455667788', 'Laki-laki', '2023-04-05'),
('AGT25060005', 'Eka Wulandari', 'eka.wulandari@example.com', '085566778899', 'Perempuan', '2023-05-12'),
('AGT25060006', 'Fajar Nugroho', 'fajar.nugroho@example.com', '086677889900', 'Laki-laki', '2023-06-18'),
('AGT25060007', 'Gita Sari', 'gita.sari@example.com', '087788990011', 'Perempuan', '2023-07-22'),
('AGT25060008', 'Hadi Putra', 'hadi.putra@example.com', '088899001122', 'Laki-laki', '2023-08-30'),
('AGT25060009', 'Indah Permata', 'indah.permata@example.com', '089900112233', 'Perempuan', '2023-09-14'),
('AGT25060010', 'Joko Susilo', 'joko.susilo@example.com', '081112223344', 'Laki-laki', '2023-10-25'),
('AGT25060011', 'Kartika Ayu', 'kartika.ayu@example.com', '082223334455', 'Perempuan', '2023-11-03'),
('AGT25060012', 'Lukman Hakim', 'lukman.hakim@example.com', '083334445566', 'Laki-laki', '2023-12-08'),
('AGT25060013', 'Mira Rahayu', 'mira.rahayu@example.com', '084445556677', 'Perempuan', '2024-01-17'),
('AGT25060014', 'Nanda Pratama', 'nanda.pratama@example.com', '085556667788', 'Laki-laki', '2024-02-21'),
('AGT25060015', 'Oki Setiawan', 'oki.setiawan@example.com', '086667778899', 'Laki-laki', '2024-03-09'),
('AGT25060016', 'Putri Anggraeni', 'putri.anggraeni@example.com', '087778889900', 'Perempuan', '2024-04-11'),
('AGT25060017', 'Rudi Hermawan', 'rudi.hermawan@example.com', '088889990011', 'Laki-laki', '2024-05-19'),
('AGT25060018', 'Siti Aminah', 'siti.aminah@example.com', '089990001122', 'Perempuan', '2024-06-23'),
('AGT25060019', 'Teguh Wijaya', 'teguh.wijaya@example.com', '081001112233', 'Laki-laki', '2024-07-30'),
('AGT25060020', 'chimi', 'chimi@gmail.com', '089745162343', 'Perempuan', '2025-06-22'),
('AGT25060021', 'Chiro', 'chiroo@gmail.com', '089657453212', 'Laki - laki', '2025-06-22');

-- --------------------------------------------------------

--
-- Table structure for table `buku`
--

CREATE TABLE `buku` (
  `id_buku` varchar(20) NOT NULL,
  `kategori` varchar(100) NOT NULL,
  `judul` varchar(100) NOT NULL,
  `author` varchar(100) NOT NULL,
  `penerbit` varchar(100) NOT NULL,
  `stok_buku` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `buku`
--

INSERT INTO `buku` (`id_buku`, `kategori`, `judul`, `author`, `penerbit`, `stok_buku`) VALUES
('1LB25060001', 'Fiksi', 'No Longer Human', 'Osamu Dazai', 'Gramedia', '10'),
('1LB25060002', 'Fiksi', 'Bumi Manusia', 'Pramoedya Ananta Toer', 'Pustaka Jaya', '11'),
('1LB25060003', 'Non Fiksi', 'Cosmos', 'Carl Sagan', 'Gramedia', '6'),
('1LB25060004', 'Biografi', 'Steve Jobs', 'Walter Isaacson', 'Bentang Pustaka', '10'),
('1LB25060005', 'Fiksi', 'Laskar Pelangi', 'Andrea Hirata', 'Bentang Pustaka', '14'),
('1LB25060006', 'Sejarah', 'Sejarah Indonesia Modern', 'M.C. Ricklefs', 'Serambi', '7'),
('1LB25060007', 'Pendidikan', 'Pendidikan Karakter', 'Donni Juni Priansa', 'Pustaka Setia', '7'),
('1LB25060008', 'Comic', 'Naruto Vol. 1', 'Masashi Kishimoto', 'Elex Media', '5'),
('1LB25060009', 'Non Fiksi', 'Sapiens', 'Yuval Noah Harari', 'Kepustakaan Populer Gramedia', '7'),
('1LB25060010', 'Fiksi', 'Pulang', 'Tere Liye', 'Gramedia Pustaka Utama', '10'),
('1LB25060011', 'Biografi', 'Habibie & Ainun', 'B.J. Habibie', 'THC Mandiri', '6'),
('1LB25060012', 'Sejarah', 'Api Sejarah', 'Ahmad Mansur Suryanegara', 'Penerbit Salamadani', '6'),
('1LB25060013', 'Pendidikan', 'Teori Belajar dan Pembelajaran', 'Dr. Hamzah B. Uno', 'Bumi Aksara', '12'),
('1LB25060014', 'Comic', 'One Piece Vol. 1', 'Eiichiro Oda', 'Elex Media', '22'),
('1LB25060015', 'Non Fiksi', 'Atomic Habits', 'James Clear', 'Gramedia', '7');

-- --------------------------------------------------------

--
-- Table structure for table `detail_peminjaman`
--

CREATE TABLE `detail_peminjaman` (
  `id_peminjaman` varchar(100) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `id_buku` varchar(100) NOT NULL,
  `judul` varchar(100) NOT NULL,
  `jumlah` int(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `detail_peminjaman`
--

INSERT INTO `detail_peminjaman` (`id_peminjaman`, `nama`, `id_buku`, `judul`, `jumlah`) VALUES
('PMJ25060001', 'AGT25060007', '1LB25060003', 'Cosmos', 2),
('PMJ25060001', 'AGT25060007', '1LB25060004', 'Steve Jobs', 2),
('PMJ25060002', 'AGT25060011', '1LB25060012', 'Api Sejarah', 5),
('PMJ25060003', 'AGT25060020', '1LB25060007', 'Pendidikan Karakter', 2),
('PMJ25060004', 'AGT25060021', '1LB25060013', 'Teori Belajar dan Pembelajaran', 2),
('PMJ25060005', 'AGT25060011', '1LB25060012', 'Api Sejarah', 1);

-- --------------------------------------------------------

--
-- Table structure for table `peminjaman`
--

CREATE TABLE `peminjaman` (
  `id_peminjaman` varchar(50) NOT NULL,
  `id_anggota` varchar(100) NOT NULL,
  `total_pinjam` int(100) NOT NULL,
  `tanggal_pinjam` date NOT NULL,
  `tanggal_kembali` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `peminjaman`
--

INSERT INTO `peminjaman` (`id_peminjaman`, `id_anggota`, `total_pinjam`, `tanggal_pinjam`, `tanggal_kembali`) VALUES
('PMJ25060001', 'AGT25060007', 4, '2025-06-18', '2025-06-20'),
('PMJ25060002', 'AGT25060011', 5, '2025-06-21', '2025-06-25'),
('PMJ25060003', 'AGT25060020', 2, '2025-06-22', '2025-06-22'),
('PMJ25060004', 'AGT25060021', 2, '2025-06-22', '2025-06-23'),
('PMJ25060005', 'AGT25060011', 1, '2025-06-22', '2025-06-03');

-- --------------------------------------------------------

--
-- Table structure for table `pengembalian`
--

CREATE TABLE `pengembalian` (
  `id_pengembalian` varchar(100) NOT NULL,
  `id_peminjaman` varchar(100) NOT NULL,
  `id_buku` varchar(100) NOT NULL,
  `tgl_kembali` date NOT NULL,
  `denda` int(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pengembalian`
--

INSERT INTO `pengembalian` (`id_pengembalian`, `id_peminjaman`, `id_buku`, `tgl_kembali`, `denda`) VALUES
('PGB25060001', 'PMJ25060001', '1LB25060003', '2025-06-25', 10000),
('PGB25060002', 'PMJ25060001', '1LB25060004', '2025-06-22', 4000),
('PGB25060003', 'PMJ25060005', '1LB25060012', '2025-06-30', 54000);

-- --------------------------------------------------------

--
-- Table structure for table `register`
--

CREATE TABLE `register` (
  `id` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` varchar(50) NOT NULL DEFAULT 'User'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `register`
--

INSERT INTO `register` (`id`, `username`, `password`, `role`) VALUES
(15, 'keii', 'e33887a1d6e2a5cf5588bf2c40ebe626e9274659f35c47264603247833b13802', 'Admin'),
(23, 'Anggi', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'User'),
(26, 'Asep', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'User');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `anggota`
--
ALTER TABLE `anggota`
  ADD PRIMARY KEY (`id_anggota`);

--
-- Indexes for table `buku`
--
ALTER TABLE `buku`
  ADD PRIMARY KEY (`id_buku`);

--
-- Indexes for table `peminjaman`
--
ALTER TABLE `peminjaman`
  ADD PRIMARY KEY (`id_peminjaman`);

--
-- Indexes for table `register`
--
ALTER TABLE `register`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `register`
--
ALTER TABLE `register`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
