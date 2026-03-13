package com.mindguardians.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguardians.ui.theme.*

// ─── DASHBOARD ───────────────────────────────────────────────────────────────
@Composable
fun DashboardScreen(heroGold: Int, heroLevel: Int) {
    val filters = listOf("Todos", "💧 Agua", "🌟 Postura", "✨ Mente")
    val bars    = listOf(40, 70, 55, 85, 45, 30, 60)
    val days    = listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Hoy")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenTitle(prefix = "Wellness ", prefixColor = CyanNeon, suffix = "Dashboard")

        // Filtros
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEach { f ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .border(1.dp, PurpleNeon.copy(.4f), RoundedCornerShape(50.dp))
                        .clickable { }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(f, color = PurpleNeon, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Stat cards 2x2
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("142",             "Combates",   CyanNeon,    Modifier.weight(1f))
                StatCard(heroGold.toString(),"Oro total",  GoldNeon,    Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("18",              "Racha días", PurpleLight, Modifier.weight(1f))
                StatCard(heroLevel.toString(),"Nivel héroe",GreenNeon,  Modifier.weight(1f))
            }
        }

        // Gráfica semanal
        DarkCard {
            Column {
                Text("⚔️ COMBATES ESTA SEMANA", color = Color.White.copy(.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    bars.forEachIndexed { i, h ->
                        val isToday = i == 6
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((h * .7f).dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(if (isToday) CyanNeon else PurpleNeon)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(days[i], color = if (isToday) CyanNeon else Color.White.copy(.3f), fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}

// ─── SHOP ────────────────────────────────────────────────────────────────────
@Composable
fun ShopScreen(heroGold: Int) {
    data class ShopItem(val emoji: String, val name: String, val stat: String, val price: Int, val owned: Boolean)
    val items = listOf(
        ShopItem("🗡️", "Espada del Amanecer", "+10% Daño Agua",  0,   true),
        ShopItem("🛡️", "Escudo Estelar",      "+15 HP Máx.",     120, false),
        ShopItem("🪖", "Casco de Claridad",   "+20% Daño Mente", 180, false),
        ShopItem("👟", "Botas del Cosmos",    "+15% Postura",    150, false),
        ShopItem("💎", "Amuleto Galáctico",   "+5% Todo daño",   250, false),
        ShopItem("🔮", "Orbe del Oráculo",    "+2x bonif. IA",   300, false),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenTitle(prefix = "Tienda ", prefixColor = GoldNeon, suffix = "del Héroe")

        // Banner de oro
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GoldNeon.copy(.1f))
                .border(1.dp, GoldNeon.copy(.35f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("💰", fontSize = 28.sp)
            Column {
                Text("$heroGold Oro", color = GoldNeon, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("Disponible", color = Color.White.copy(.4f), fontSize = 11.sp)
            }
        }

        // Grid 2 columnas
        val rows = items.chunked(2)
        rows.forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(.35f))
                            .border(1.dp, if (item.owned) GreenNeon.copy(.4f) else PurpleNeon.copy(.25f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(item.emoji, fontSize = 36.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(item.name, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Text(item.stat, color = PurpleLight, fontSize = 11.sp, modifier = Modifier.padding(vertical = 4.dp))
                            if (item.owned) {
                                Box(modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(GreenNeon.copy(.1f)).border(1.dp, GreenNeon.copy(.3f), RoundedCornerShape(50.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                                    Text("✓ Equipado", color = GreenNeon, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Box(modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(GoldNeon.copy(.1f)).border(1.dp, GoldNeon.copy(.3f), RoundedCornerShape(50.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                                    Text("💰 ${item.price}", color = GoldNeon, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

// ─── RANKING ─────────────────────────────────────────────────────────────────
@Composable
fun RankingScreen(heroLevel: Int) {
    data class PodiumEntry(val pos: Int, val emoji: String, val name: String, val level: Int, val color: Color)
    data class RankEntry(val rank: Int, val emoji: String, val name: String, val level: Int, val xp: String, val me: Boolean)

    val podium = listOf(
        PodiumEntry(2, "🧙", "Vortex_X",  12, Color(0xFF475569)),
        PodiumEntry(1, "🦁", "LionHeart", 18, Color(0xFFCA8A04)),
        PodiumEntry(3, "🧝", "StellaR",   10, Color(0xFFC2410C)),
    )
    val rankList = listOf(
        RankEntry(4, "⚡", "ThunderK",          9,         "2,840", false),
        RankEntry(5, "🌿", "NatureG",           8,         "2,510", false),
        RankEntry(6, "⚔️", "PaladinUrb (Tú)",  heroLevel, "2,140", true),
        RankEntry(7, "🔥", "FireMind",          6,         "1,980", false),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenTitle(prefix = "Ranking ", prefixColor = Color(0xFFEF4444), suffix = "Global")

        // Podio
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            podium.forEach { p ->
                val isFirst = p.pos == 1
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isFirst) 52.dp else 44.dp)
                            .clip(CircleShape)
                            .background(SpaceDeep)
                            .border(2.dp, if (isFirst) GoldNeon else Color.White.copy(.2f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(p.emoji, fontSize = if (isFirst) 26.sp else 22.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(p.name,          color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("Nv. ${p.level}", color = TextMuted, fontSize = 10.sp)
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isFirst) 70.dp else if (p.pos == 2) 50.dp else 34.dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(p.color),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(p.pos.toString(), color = TextWhite, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                }
            }
        }

        // Lista
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rankList.forEach { r ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (r.me) CyanNeon.copy(.06f) else Color.Black.copy(.25f))
                        .border(1.dp, if (r.me) CyanNeon.copy(.4f) else PurpleNeon.copy(.15f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(r.rank.toString(), color = if (r.me) CyanNeon else Color.White.copy(.3f), fontWeight = FontWeight.Black, fontSize = 13.sp, modifier = Modifier.width(20.dp))
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(SpaceDeep).border(1.dp, PurpleNeon.copy(.3f), CircleShape), contentAlignment = Alignment.Center) {
                        Text(r.emoji, fontSize = 20.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(r.name, color = if (r.me) CyanNeon else Color(0xFFE2E8F0), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Nv. ${r.level}", color = TextMuted, fontSize = 10.sp)
                    }
                    Text("${r.xp} XP", color = PurpleLight, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
            }
        }
    }
}

// ─── ORACLE ──────────────────────────────────────────────────────────────────
@Composable
fun OracleScreen() {
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4C1D95))
                    .border(2.dp, GreenNeon.copy(.5f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("🔮", fontSize = 36.sp)
            }
            Spacer(Modifier.height(10.dp))
            Text("ORÁCULO DE GEMINI", color = GreenNeon, fontWeight = FontWeight.Black, fontSize = 13.sp, letterSpacing = 2.sp)
            Text("Cuéntale tus hazañas al Narrador", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }

        // Chat
        DarkCard(borderColor = GreenNeon.copy(.2f)) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Mensaje oráculo
                Box(
                    modifier = Modifier
                        .fillMaxWidth(.85f)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                        .background(GreenNeon.copy(.1f))
                        .border(1.dp, GreenNeon.copy(.25f), RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("🔮 Oráculo", color = GreenNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("¡Salve, Guerrero Estelar! El cosmos observa tu jornada. ¿Qué hazañas de salud has realizado hoy?", color = TextWhite.copy(.8f), fontSize = 12.sp, lineHeight = 18.sp)
                    }
                }
                // Mensaje usuario
                Box(
                    modifier = Modifier
                        .fillMaxWidth(.85f)
                        .align(Alignment.End)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                        .background(PurpleNeon.copy(.15f))
                        .border(1.dp, PurpleNeon.copy(.3f), RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("⚔️ Tú", color = PurpleLight, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                        Spacer(Modifier.height(4.dp))
                        Text("Bebí 2 litros de agua y caminé 20 minutos.", color = TextWhite.copy(.8f), fontSize = 12.sp)
                    }
                }
            }
        }

        // Respuesta épica
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(GoldNeon.copy(.08f), PurpleNeon.copy(.08f))))
                .border(1.dp, GoldNeon.copy(.3f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("✨ RESPUESTA ÉPICA DEL ORÁCULO", color = GoldNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "\"¡Magnífico, Paladín! Has bebido el Elixir de la Vida y recorrido los senderos del cosmos. ¡Tus golpes serán el doble de certeros!\"",
                    color = TextWhite.copy(.8f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontStyle = FontStyle.Italic,
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(GoldNeon.copy(.2f))
                        .border(1.dp, GoldNeon.copy(.4f), RoundedCornerShape(50.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("⚡ Bonificador: +2x Daño por 3 ataques", color = GoldNeon, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Input
        DarkCard(borderColor = GreenNeon.copy(.3f)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("¿QUÉ HAZAÑA REALIZASTE HOY?", color = Color.White.copy(.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ej: Dormí 8 horas, tomé agua, medité...", color = Color.White.copy(.25f), fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(.3f))
                        .border(1.dp, GreenNeon.copy(.2f), RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor   = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor        = TextWhite.copy(.8f),
                        unfocusedTextColor      = TextWhite.copy(.8f),
                        focusedIndicatorColor   = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    minLines = 3,
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF166534))
                        .border(2.dp, GreenNeon.copy(.4f), RoundedCornerShape(12.dp))
                        .clickable { }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🔮 Consultar al Oráculo", color = TextWhite, fontWeight = FontWeight.Black, fontSize = 13.sp, letterSpacing = 1.sp)
                }
            }
        }
    }
}

// ─── HELPERS ─────────────────────────────────────────────────────────────────

@Composable
fun ScreenTitle(prefix: String, prefixColor: Color, suffix: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(prefix, color = prefixColor, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Text(suffix, color = TextWhite,   fontWeight = FontWeight.Black, fontSize = 20.sp)
    }
}

@Composable
fun StatCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(.3f))
            .border(1.dp, PurpleNeon.copy(.25f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 26.sp)
            Text(label, color = Color.White.copy(.4f), fontSize = 10.sp, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun DarkCard(borderColor: Color = BorderPurple, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(.3f))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        content()
    }
}
