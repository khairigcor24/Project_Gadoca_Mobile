package com.example.project_farhangadoca_mobilee

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

// --- Palet Warna Gadoca ---
val GadocaBg = Color(0xFFF3F7F4)       // Background putih kehijauan sangat muda
val GadocaPrimary = Color(0xFF1F5E45)   // Hijau tua utama
val GadocaAccent = Color(0xFFE8B730)    // Kuning emas / aksen
val GadocaPointsCard = Color(0xFFDDEEE4)// Hijau muda pucat untuk kartu poin
val GadocaTextDark = Color(0xFF1B2B24)  // Hijau gelap hampir hitam
val GadocaTextSecondary = Color(0xFF5A6B63) // Abu-hijau teks sekunder

// --- Data Class Menu Item ---
data class MenuItem(
    val name: String,
    val description: String,
    val price: Double,
    val imageRes: Int
)

// Dummy data untuk menu favorit
val dummyMenuItems = listOf(
    MenuItem(
        name = "Es Kopi Gula Aren",
        description = "Kopi · Manis dan lembut",
        price = 28000.0,
        imageRes = android.R.drawable.ic_menu_gallery // Placeholder drawable
    ),
    MenuItem(
        name = "Butter Croissant",
        description = "Pastry · Renyah berlapis",
        price = 24000.0,
        imageRes = android.R.drawable.ic_menu_gallery // Placeholder drawable
    ),
    MenuItem(
        name = "Matcha Latte",
        description = "Tea · Creamy & earthy",
        price = 32000.0,
        imageRes = android.R.drawable.ic_menu_gallery
    )
)

// Format mata uang Rupiah Indonesia
fun formatRupiah(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount).replace("Rp", "Rp ")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GadocaTopBar() {
    // Top bar dengan ikon kopi dan lonceng notifikasi
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalCafe,
                    contentDescription = "Logo Cangkir Kopi",
                    tint = GadocaPrimary,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Gadoca",
                    fontWeight = FontWeight.SemiBold,
                    color = GadocaPrimary,
                    fontSize = 20.sp
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Aksi Notifikasi */ }) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifikasi",
                    tint = GadocaTextDark
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GadocaBg)
    )
}

@Composable
fun GreetingHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Teks tanggal kecil
        Text(
            text = "Minggu, 4 Oktober 2026",
            fontSize = 13.sp,
            color = GadocaTextSecondary,
            fontFamily = FontFamily.SansSerif
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        // Baris sapaan & avatar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pagi, Farhan.",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = GadocaTextDark,
                fontFamily = FontFamily.Serif
            )
            
            // Avatar lingkaran inisial NP dengan background hijau pucat
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(GadocaPointsCard),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NP",
                    fontWeight = FontWeight.Bold,
                    color = GadocaPrimary,
                    fontSize = 16.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Baris lokasi kafe
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Lokasi",
                    tint = GadocaPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Gadoca · Cabang Taman",
                    fontSize = 13.sp,
                    color = GadocaTextDark,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "Buka hingga 22.00",
                fontSize = 12.sp,
                color = GadocaTextSecondary
            )
        }
    }
}

@Composable
fun HeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(GadocaPrimary)
    ) {
        // Background gambar interior kafe (placeholder drawable dengan gradien overlay)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            GadocaPrimary.copy(alpha = 0.95f),
                            GadocaPrimary.copy(alpha = 0.6f)
                        )
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Label kecil kapital kuning
            Text(
                text = "SEDIKIT JEDA, BANYAK RASA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GadocaAccent,
                letterSpacing = 1.5.sp
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Judul serif putih besar
                Text(
                    text = "Favoritmu, siap menemani.",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.Serif
                )
                // Subteks putih kecil
                Text(
                    text = "Kopi diracik setelah kamu pesan.",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
            
            // Tombol teks "Jelajahi menu" + ikon panah kanan
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable { /* Aksi Jelajahi Menu */ }
            ) {
                Text(
                    text = "Jelajahi menu",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Panah Kanan",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun PointsCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GadocaPointsCard),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Kotak hijau dengan ikon sparkle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GadocaPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Poin Sparkle",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Column {
                    Text(
                        text = "240 poin untuk kamu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = GadocaTextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Member Hijau · Lihat hadiahmu",
                        fontSize = 12.sp,
                        color = GadocaTextSecondary
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Detail Poin",
                tint = GadocaPrimary
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = GadocaTextDark,
            fontFamily = FontFamily.Serif
        )
        Text(
            text = actionText,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = GadocaPrimary,
            modifier = Modifier.clickable(onClick = onActionClick)
        )
    }
}

@Composable
fun MenuCard(item: MenuItem, onAddClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(160.dp)
            .height(220.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Gambar produk di atas dengan sudut atas membulat (~120dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(GadocaPointsCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    tint = GadocaPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    // Nama menu tebal
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GadocaTextDark,
                        maxLines = 1
                    )
                    // Kategori kecil abu-abu
                    Text(
                        text = item.description,
                        fontSize = 11.sp,
                        color = GadocaTextSecondary,
                        maxLines = 1
                    )
                }
                
                // Baris bawah: Harga hijau tebal & tombol "+" bulat outline hijau
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatRupiah(item.price),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = GadocaPrimary
                    )
                    
                    IconButton(
                        onClick = onAddClick,
                        modifier = Modifier
                            .size(28.dp)
                            .border(1.dp, GadocaPrimary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah",
                            tint = GadocaPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReservationRow(onReservasiClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onReservasiClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = "Kalender",
                    tint = GadocaPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Butuh sudut untuk ngobrol?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = GadocaTextDark
                )
            }
            
            Text(
                text = "Reservasi →",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GadocaPrimary
            )
        }
    }
}

@Composable
fun GadocaBottomBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // 1. Beranda (aktif, hijau tua, dengan titik indikator kecil di bawah ikon)
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (selectedTab == 0) Icons.Default.Home else Icons.Outlined.Home,
                        contentDescription = "Beranda",
                        tint = if (selectedTab == 0) GadocaPrimary else GadocaTextSecondary
                    )
                    if (selectedTab == 0) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(GadocaPrimary)
                        )
                    }
                }
            },
            label = { Text("Beranda", fontSize = 11.sp, color = if (selectedTab == 0) GadocaPrimary else GadocaTextSecondary) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        
        // 2. Menu (ikon cangkir)
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == 1) Icons.Default.LocalCafe else Icons.Outlined.LocalCafe,
                    contentDescription = "Menu",
                    tint = if (selectedTab == 1) GadocaPrimary else GadocaTextSecondary
                )
            },
            label = { Text("Menu", fontSize = 11.sp, color = if (selectedTab == 1) GadocaPrimary else GadocaTextSecondary) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        
        // 3. Pesanan (ikon dokumen/receipt)
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == 2) Icons.AutoMirrored.Filled.ReceiptLong else Icons.AutoMirrored.Outlined.ReceiptLong,
                    contentDescription = "Pesanan",
                    tint = if (selectedTab == 2) GadocaPrimary else GadocaTextSecondary
                )
            },
            label = { Text("Pesanan", fontSize = 11.sp, color = if (selectedTab == 2) GadocaPrimary else GadocaTextSecondary) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        
        // 4. Akun (ikon orang)
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == 3) Icons.Default.Person else Icons.Outlined.Person,
                    contentDescription = "Akun",
                    tint = if (selectedTab == 3) GadocaPrimary else GadocaTextSecondary
                )
            },
            label = { Text("Akun", fontSize = 11.sp, color = if (selectedTab == 3) GadocaPrimary else GadocaTextSecondary) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BerandaScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { GadocaTopBar() },
        bottomBar = { GadocaBottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it }) },
        containerColor = GadocaBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 2. Header Sapaan
            GreetingHeader()

            // 3. Banner Hero
            HeroBanner()

            // 4. Kartu Poin
            PointsCard(onClick = { /* Aksi klik kartu poin */ })

            // 5. Section "Favorit di Gadoca"
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    title = "Favorit di Gadoca",
                    actionText = "Lihat semua",
                    onActionClick = { /* Aksi lihat semua menu */ }
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(dummyMenuItems) { item ->
                        MenuCard(
                            item = item,
                            onAddClick = { /* Aksi tambah ke keranjang */ }
                        )
                    }
                }
            }

            // 6. Banner Reservasi Kecil
            ReservationRow(onReservasiClick = { /* Aksi reservasi */ })
            
            // Spacer bawah agar konten tidak tertutup bottom bar
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// --- Preview BerandaScreen ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BerandaScreenPreview() {
    BerandaScreen()
}
