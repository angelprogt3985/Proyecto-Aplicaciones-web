package com.mindguardians.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import com.mindguardians.GameViewModel
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

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("142",              "Combates",   CyanNeon,    Modifier.weight(1f))
                StatCard(heroGold.toString(), "Oro total",  GoldNeon,    Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("18",               "Racha días", PurpleLight, Modifier.weight(1f))
                StatCard(heroLevel.toString(),"Nivel héroe",GreenNeon,  Modifier.weight(1f))
            }
        }

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
fun ShopScreen(vm: GameViewModel) {
    data class ShopItem(val id: String, val emoji: String, val name: String, val stat: String, val price: Int)

    val items = listOf(
        ShopItem("shop_01", "🗡️", "Espada del Amanecer", "+10% Daño Agua",  120),
        ShopItem("shop_02", "🛡️", "Escudo Estelar",      "+15 HP Máx.",     120),
        ShopItem("shop_03", "🪖", "Casco de Claridad",   "+20% Daño Mente", 180),
        ShopItem("shop_04", "👟", "Botas del Cosmos",    "+15% Postura",    150),
        ShopItem("shop_05", "💎", "Amuleto Galáctico",   "+5% Todo daño",   250),
        ShopItem("shop_06", "🔮", "Orbe del Oráculo",    "+2x bonif. IA",   300),
    )

    val available = items.filter { !vm.purchasedIds.contains(it.id) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenTitle(prefix = "Tienda ", prefixColor = GoldNeon, suffix = "del Héroe")

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
                Text("${vm.heroGold} Oro", color = GoldNeon, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("Disponible", color = Color.White.copy(.4f), fontSize = 11.sp)
            }
        }

        if (vm.isLoadingShop) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GoldNeon, modifier = Modifier.size(32.dp))
            }
        } else if (available.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(.3f))
                    .border(1.dp, GoldNeon.copy(.2f), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("¡Has comprado todo el equipo disponible! 🏆", color = GoldNeon, fontSize = 13.sp, textAlign = TextAlign.Center)
            }
        } else {
            val rows = available.chunked(2)
            rows.forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowItems.forEach { item ->
                        val canAfford = vm.heroGold >= item.price
                        val isBlocked = vm.isPurchasing

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(.35f))
                                .border(1.dp, if (canAfford) PurpleNeon.copy(.25f) else Color.White.copy(.1f), RoundedCornerShape(16.dp))
                                .then(if (canAfford && !isBlocked) Modifier.clickable {
                                    vm.purchaseItem(item.id, item.name, item.stat, item.emoji, item.price)
                                } else Modifier)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(item.emoji, fontSize = 36.sp)
                                Spacer(Modifier.height(8.dp))
                                Text(item.name, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                Text(item.stat, color = PurpleLight, fontSize = 11.sp, modifier = Modifier.padding(vertical = 4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(if (canAfford) GoldNeon.copy(.1f) else Color.White.copy(.05f))
                                        .border(1.dp, if (canAfford) GoldNeon.copy(.3f) else Color.White.copy(.1f), RoundedCornerShape(50.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        if (isBlocked && canAfford) "..." else "💰 ${item.price}",
                                        color = if (canAfford) GoldNeon else Color.White.copy(.3f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                    if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }

        if (vm.purchasedIds.isNotEmpty()) {
            DarkCard(borderColor = GreenNeon.copy(.3f)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("✓ EQUIPADO", color = GreenNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    items.filter { vm.purchasedIds.contains(it.id) }.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("${item.emoji} ${item.name}", color = TextWhite, fontSize = 12.sp)
                            Text(item.stat, color = GreenNeon, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
// ─── RANKING ─────────────────────────────────────────────────────────────────
@Composable
fun RankingScreen(vm: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenTitle(prefix = "Ranking ", prefixColor = Color(0xFFEF4444), suffix = "Global")

        if (vm.isLoadingRanking) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFEF4444), modifier = Modifier.size(32.dp))
            }
        } else if (vm.ranking.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(.3f))
                    .border(1.dp, PurpleNeon.copy(.2f), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("No hay héroes en el ranking aún.", color = TextMuted, fontSize = 13.sp)
            }
        } else {
            val emojis = listOf("🦁", "🧙", "🧝", "⚡", "🌿", "⚔️", "🔥", "✨", "🏹", "🌟")
            val podiumEntries = vm.ranking.take(3)
            val listEntries   = vm.ranking.drop(3)

            val podiumOrder = when (podiumEntries.size) {
                1 -> listOf(0)
                2 -> listOf(1, 0)
                else -> listOf(1, 0, 2)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                podiumOrder.forEach { idx ->
                    val entry    = podiumEntries[idx]
                    val name     = (entry["displayName"] as? String) ?: "Héroe"
                    val level    = (entry["heroLevel"]   as? Long)?.toInt() ?: 1
                    val pos      = idx + 1
                    val isFirst  = pos == 1
                    val podColor = when (pos) {
                        1    -> Color(0xFFCA8A04)
                        2    -> Color(0xFF475569)
                        else -> Color(0xFFC2410C)
                    }

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
                            Text(emojis.getOrElse(idx) { "⚔️" }, fontSize = if (isFirst) 26.sp else 22.sp)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Nv. $level", color = TextMuted, fontSize = 10.sp)
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (isFirst) 70.dp else if (pos == 2) 50.dp else 34.dp)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(podColor),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(pos.toString(), color = TextWhite, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listEntries.forEachIndexed { i, entry ->
                    val rank    = i + 4
                    val name    = (entry["displayName"] as? String) ?: "Héroe"
                    val level   = (entry["heroLevel"]   as? Long)?.toInt() ?: 1
                    val totalXp = (entry["totalXp"]     as? Long) ?: 0
                    val isMe    = entry["displayName"] == vm.heroName

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isMe) CyanNeon.copy(.06f) else Color.Black.copy(.25f))
                            .border(1.dp, if (isMe) CyanNeon.copy(.4f) else PurpleNeon.copy(.15f), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(rank.toString(), color = if (isMe) CyanNeon else Color.White.copy(.3f), fontWeight = FontWeight.Black, fontSize = 13.sp, modifier = Modifier.width(20.dp))
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(SpaceDeep).border(1.dp, PurpleNeon.copy(.3f), CircleShape), contentAlignment = Alignment.Center) {
                            Text(emojis.getOrElse(i + 3) { "⚔️" }, fontSize = 20.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(if (isMe) "$name (Tú)" else name, color = if (isMe) CyanNeon else Color(0xFFE2E8F0), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Nv. $level", color = TextMuted, fontSize = 10.sp)
                        }
                        Text("$totalXp XP", color = PurpleLight, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ─── ORACLE ──────────────────────────────────────────────────────────────────
@Composable
fun OracleScreen(vm: GameViewModel) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(vm.oracleMessages.size) {
        if (vm.oracleMessages.isNotEmpty()) {
            listState.animateScrollToItem(vm.oracleMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
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

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(.3f))
                .border(1.dp, GreenNeon.copy(.2f), RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(vm.oracleMessages) { (role, text) ->
                val isOracle = role == "oracle"
                Box(
                    modifier = Modifier
                        .fillMaxWidth(.85f)
                        .then(if (!isOracle) Modifier.align(Alignment.End) else Modifier)
                        .clip(
                            RoundedCornerShape(
                                topStart    = if (isOracle) 4.dp else 16.dp,
                                topEnd      = if (isOracle) 16.dp else 4.dp,
                                bottomStart = 16.dp,
                                bottomEnd   = 16.dp,
                            )
                        )
                        .background(if (isOracle) GreenNeon.copy(.1f) else PurpleNeon.copy(.15f))
                        .border(
                            1.dp,
                            if (isOracle) GreenNeon.copy(.25f) else PurpleNeon.copy(.3f),
                            RoundedCornerShape(
                                topStart    = if (isOracle) 4.dp else 16.dp,
                                topEnd      = if (isOracle) 16.dp else 4.dp,
                                bottomStart = 16.dp,
                                bottomEnd   = 16.dp,
                            )
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            if (isOracle) "🔮 Oráculo" else "⚔️ Tú",
                            color = if (isOracle) GreenNeon else PurpleLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = if (!isOracle) Modifier.fillMaxWidth() else Modifier,
                            textAlign = if (!isOracle) TextAlign.End else TextAlign.Start,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(text, color = TextWhite.copy(.8f), fontSize = 12.sp, lineHeight = 18.sp)
                    }
                }
            }

            if (vm.isOracleLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color    = GreenNeon,
                            strokeWidth = 2.dp,
                        )
                    }
                }
            }

            vm.oracleError?.let { error ->
                item {
                    Text(
                        error,
                        color    = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

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
                        .background(if (vm.isOracleLoading) Color.Gray else Color(0xFF166534))
                        .border(2.dp, GreenNeon.copy(.4f), RoundedCornerShape(12.dp))
                        .clickable(enabled = !vm.isOracleLoading) {
                            vm.consultOracle(inputText)
                            inputText = ""
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (vm.isOracleLoading) "Consultando..." else "🔮 Consultar al Oráculo",
                        color = TextWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                    )
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