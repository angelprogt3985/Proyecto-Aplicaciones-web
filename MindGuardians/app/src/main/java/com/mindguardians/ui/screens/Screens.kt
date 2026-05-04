package com.mindguardians.ui.screens


import androidx.compose.foundation.horizontalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.text.SimpleDateFormat
import java.util.Locale

// ─── DASHBOARD ───────────────────────────────────────────────────────────────
@Composable
fun DashboardScreen(vm: GameViewModel) {
    val allBattles = vm.battles
    val filters    = listOf("Todos", "Gravedad Pesada", "Vacío Estelar", "Caos Cósmico")
    var activeFilter by remember { mutableStateOf("Todos") }

    // Filtrado por tipo de hábito
    val filtered = remember(allBattles.toList(), activeFilter) {
        if (activeFilter == "Todos") allBattles
        else allBattles.filter { it.habitType == activeFilter }
    }

    // Conteos para las tarjetas de resumen
    val totalBattles  = filtered.size
    val victories     = filtered.count { it.result == "Victoria" }
    val totalGold     = filtered.sumOf { it.goldEarned }
    val totalXp       = filtered.sumOf { it.xpEarned }

    // Barras del gráfico — combates por día en los últimos 7 días
    val today    = java.util.Calendar.getInstance()
    val weekDays = (6 downTo 0).map { offset ->
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, -offset)
        val dateStr = "%04d-%02d-%02d".format(
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.DAY_OF_MONTH),
        )
        val label = when (cal.get(java.util.Calendar.DAY_OF_WEEK)) {
            java.util.Calendar.MONDAY    -> "Lu"
            java.util.Calendar.TUESDAY   -> "Ma"
            java.util.Calendar.WEDNESDAY -> "Mi"
            java.util.Calendar.THURSDAY  -> "Ju"
            java.util.Calendar.FRIDAY    -> "Vi"
            java.util.Calendar.SATURDAY  -> "Sa"
            java.util.Calendar.SUNDAY    -> "Do"
            else -> "?"
        }
        val count = allBattles.count { it.date == dateStr }
        Triple(label, count, offset == 0)
    }
    val maxCount = weekDays.maxOf { it.second }.coerceAtLeast(1)

    // Formateo de fecha legible
    fun formatDate(dateStr: String): String {
        return try {
            val parts = dateStr.split("-")
            val months = listOf("","Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic")
            "${parts[2].toInt()} ${months[parts[1].toInt()]} ${parts[0]}"
        } catch (e: Exception) { dateStr }
    }

    // Color por tipo de combate
    fun habitColor(type: String): Color = when (type) {
        "Gravedad Pesada" -> GreenNeon
        "Vacío Estelar"   -> CyanNeon
        "Caos Cósmico"    -> Color(0xFFEF4444)
        else              -> PurpleNeon
    }
    fun habitEmoji(type: String): String = when (type) {
        "Gravedad Pesada" -> "🌿"
        "Vacío Estelar"   -> "⚡"
        "Caos Cósmico"    -> "🔥"
        else              -> "⚔️"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenTitle(prefix = "Wellness ", prefixColor = CyanNeon, suffix = "Dashboard")

        // Filtros por tipo
        Row(
            modifier            = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            filters.forEach { f ->
                val active = activeFilter == f
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (active) PurpleNeon.copy(.2f) else Color.Transparent)
                        .border(1.dp, PurpleNeon.copy(if (active) .8f else .4f), RoundedCornerShape(50.dp))
                        .clickable { activeFilter = f }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                ) {
                    Text(
                        f,
                        color      = if (active) PurpleNeon else PurpleNeon.copy(.6f),
                        fontSize   = 12.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }

        // Tarjetas de resumen — mismas 4 métricas que la web
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(totalBattles.toString(),   "Combates",   CyanNeon,  Modifier.weight(1f))
                StatCard(victories.toString(),      "Victorias",  GreenNeon, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(totalGold.toString(),      "Oro Total",  GoldNeon,  Modifier.weight(1f))
                StatCard(totalXp.toString(),        "XP Total",   PurpleLight, Modifier.weight(1f))
            }
        }

        // Gráfico semanal con datos reales
        DarkCard {
            Column {
                Text(
                    "⚔️ COMBATES ESTA SEMANA",
                    color        = Color.White.copy(.5f),
                    fontSize     = 11.sp,
                    fontWeight   = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth().height(90.dp),
                    verticalAlignment     = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    weekDays.forEach { (label, count, isToday) ->
                        val heightFraction = count.toFloat() / maxCount
                        Column(
                            modifier              = Modifier.weight(1f),
                            horizontalAlignment   = Alignment.CenterHorizontally,
                            verticalArrangement   = Arrangement.Bottom,
                        ) {
                            if (count > 0) {
                                Text(
                                    count.toString(),
                                    color    = if (isToday) CyanNeon else PurpleNeon.copy(.7f),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(Modifier.height(2.dp))
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    // Mínimo 4dp para que se vea aunque count sea 0
                                    .height((heightFraction * 70f).coerceAtLeast(4f).dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(
                                        if (count == 0) Color.White.copy(.06f)
                                        else if (isToday) CyanNeon
                                        else PurpleNeon
                                    ),
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                label,
                                color    = if (isToday) CyanNeon else Color.White.copy(.3f),
                                fontSize = 9.sp,
                            )
                        }
                    }
                }
            }
        }

        // Lista de combates
        DarkCard {
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                Text(
                    "HISTORIAL DE COMBATES",
                    color         = Color.White.copy(.5f),
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(12.dp))

                if (vm.isLoadingBattles) {
                    Box(
                        modifier            = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        contentAlignment    = Alignment.Center,
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier    = Modifier.size(24.dp),
                            color       = CyanNeon,
                            strokeWidth = 2.dp,
                        )
                    }
                } else if (filtered.isEmpty()) {
                    Box(
                        modifier            = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        contentAlignment    = Alignment.Center,
                    ) {
                        Text(
                            "No hay combates registrados aún.",
                            color    = Color.White.copy(.3f),
                            fontSize = 13.sp,
                        )
                    }
                } else {
                    filtered.forEach { battle ->
                        val color = habitColor(battle.habitType)
                        val emoji = habitEmoji(battle.habitType)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .border(
                                    width  = 0.dp,
                                    color  = Color.Transparent,
                                    shape  = RoundedCornerShape(0.dp),
                                ),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            // Emoji del tipo
                            Box(
                                modifier         = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(color.copy(.12f))
                                    .border(1.dp, color.copy(.3f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(emoji, fontSize = 16.sp)
                            }
                            // Tipo y fecha
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    battle.habitType,
                                    color      = color,
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    formatDate(battle.date),
                                    color    = Color.White.copy(.3f),
                                    fontSize = 10.sp,
                                )
                            }
                            // Resultado
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(
                                        if (battle.result == "Victoria") CyanNeon.copy(.12f)
                                        else Color(0xFFEF4444).copy(.12f)
                                    )
                                    .border(
                                        1.dp,
                                        if (battle.result == "Victoria") CyanNeon.copy(.4f)
                                        else Color(0xFFEF4444).copy(.4f),
                                        RoundedCornerShape(50.dp),
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    battle.result,
                                    color      = if (battle.result == "Victoria") CyanNeon else Color(0xFFEF4444),
                                    fontSize   = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            // Recompensas
                            Column(horizontalAlignment = Alignment.End) {
                                Text("🪙 +${battle.goldEarned}", color = GoldNeon,   fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("+${battle.xpEarned} XP",   color = PurpleLight, fontSize = 10.sp)
                            }
                        }
                        // Separador entre filas (excepto la última)
                        if (battle != filtered.last()) {
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(.05f)))
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
    // Catálogo canónico del repositorio — misma fuente que la web ✅
    val items     = vm.shopCatalog
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
                                    vm.purchaseItem(item.id, item.name, item.stat, item.emoji, item.price, item.bonusHp, item.bonusPower)
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


// ─── INVENTORY ───────────────────────────────────────────────────────────────
@Composable
fun InventoryScreen(vm: GameViewModel) {
    LaunchedEffect(Unit) { vm.refreshFullInventory() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenTitle(prefix = "Inventario ", prefixColor = PurpleLight, suffix = "del Héroe")

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
                Text("Disponible · ${vm.purchasedIds.size} objetos comprados", color = Color.White.copy(.4f), fontSize = 11.sp)
            }
        }

        if (vm.isLoadingInventory) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PurpleLight, modifier = Modifier.size(32.dp))
            }
        } else if (vm.inventoryItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(.3f))
                    .border(1.dp, PurpleNeon.copy(.2f), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Aún no tienes objetos. ¡Ve a la tienda! 🛒", color = TextMuted, fontSize = 13.sp, textAlign = TextAlign.Center)
            }
        } else {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            vm.inventoryItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(.3f))
                        .border(1.dp, PurpleNeon.copy(.2f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PurpleNeon.copy(.12f))
                            .border(1.dp, PurpleNeon.copy(.3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(item.emoji, fontSize = 28.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(item.stat, color = PurpleLight, fontSize = 11.sp)
                        Text(
                            "Comprado: ${item.purchasedAt?.toDate()?.let { sdf.format(it) } ?: "—"}",
                            color = Color.White.copy(.3f),
                            fontSize = 10.sp,
                        )
                    }
                    Text("💰 ${item.price}", color = GoldNeon.copy(.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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