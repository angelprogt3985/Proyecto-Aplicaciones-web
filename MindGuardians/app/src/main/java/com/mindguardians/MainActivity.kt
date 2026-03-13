package com.mindguardians

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindguardians.ui.components.*
import com.mindguardians.ui.screens.*
import com.mindguardians.ui.theme.*
import androidx.compose.foundation.lazy.LazyColumn

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindGuardiansTheme {
                MindGuardiansApp()
            }
        }
    }
}

// ─── TABS ────────────────────────────────────────────────────────────────────
private data class TabItem(val screen: Screen, val emoji: String, val label: String)

private val TABS = listOf(
    TabItem(Screen.COMBAT,    "⚔️",  "Combate"),
    TabItem(Screen.DASHBOARD, "📊",  "Stats"),
    TabItem(Screen.SHOP,      "🛍️", "Tienda"),
    TabItem(Screen.RANKING,   "🏆",  "Ranking"),
    TabItem(Screen.ORACLE,    "✨",  "Oráculo"),
)

// ─── APP ROOT ────────────────────────────────────────────────────────────────
@Composable
fun MindGuardiansApp(vm: GameViewModel = viewModel()) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(SpaceDark, SpaceDeep, SpaceMid))
            )
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── HEADER ──────────────────────────────────────────────────────
            AppHeader(onMenuClick = { vm.menuOpen = true })

            // ── TAB BAR ─────────────────────────────────────────────────────
            TabBar(
                current  = vm.currentScreen,
                onChange = { vm.currentScreen = it },
            )

            // ── CONTENIDO ───────────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                when (vm.currentScreen) {
                    Screen.DASHBOARD -> DashboardScreen(vm.heroGold, vm.heroLevel)
                    Screen.SHOP      -> ShopScreen(vm.heroGold)
                    Screen.RANKING   -> RankingScreen(vm.heroLevel)
                    Screen.ORACLE    -> OracleScreen()
                    Screen.COMBAT    -> CombatScreen(vm)
                }
            }

            // ── FOOTER ──────────────────────────────────────────────────────
            AppFooter()
        }

        // ── OVERLAY: menú lateral ────────────────────────────────────────────
        NavigationMenu(
            isOpen        = vm.menuOpen,
            onClose       = { vm.menuOpen = false },
            onNavigate    = { vm.currentScreen = it; vm.menuOpen = false },
            currentScreen = vm.currentScreen,
            heroLevel     = vm.heroLevel,
            heroGold      = vm.heroGold,
        )

        // ── OVERLAY: modal de victoria ───────────────────────────────────────
        VictoryModal(
            isOpen      = vm.isVictory,
            onContinue  = { vm.continueAfterVictory() },
            goldEarned  = vm.goldReward(),
            xpEarned    = vm.xpReward(),
        )
    }
}

// ─── HEADER ──────────────────────────────────────────────────────────────────
@Composable
fun AppHeader(onMenuClick: () -> Unit) {
    val rotateAnim = rememberInfiniteTransition(label = "star")
    val rotation by rotateAnim.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "rotation",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(SpaceDeep, SpaceMid, SpaceBlue)))
            .border(
                width = 2.dp,
                color = PurpleNeon.copy(.3f),
                shape = RoundedCornerShape(0.dp),
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .rotate(rotation)
                    .clip(CircleShape)
                    .background(GoldNeon)
                    .border(2.dp, GoldDark.copy(.5f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("⭐", fontSize = 20.sp)
            }
            Row {
                Text("Winni",  color = GoldNeon,   fontWeight = FontWeight.Black, fontSize = 24.sp)
                Text("Knight", color = TextWhite,   fontWeight = FontWeight.Black, fontSize = 24.sp)
            }
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(.05f))
                .border(2.dp, PurpleNeon.copy(.4f), RoundedCornerShape(12.dp))
                .clickable(onClick = onMenuClick),
            contentAlignment = Alignment.Center,
        ) {
            Text("☰", color = CyanNeon, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── TAB BAR ─────────────────────────────────────────────────────────────────
@Composable
fun TabBar(current: Screen, onChange: (Screen) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(.3f))
            .border(
                width = 2.dp,
                color = PurpleNeon.copy(.2f),
                shape = RoundedCornerShape(0.dp),
            ),
    ) {
        TABS.forEach { tab ->
            val active = current == tab.screen
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(if (active) CyanNeon.copy(.08f) else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = if (active) CyanNeon else Color.Transparent,
                        shape = RoundedCornerShape(0.dp),
                    )
                    .clickable { onChange(tab.screen) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(tab.emoji, fontSize = 16.sp)
                Spacer(Modifier.height(2.dp))
                Text(
                    tab.label,
                    color = if (active) CyanNeon else Color.White.copy(.3f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = .5.sp,
                )
            }
        }
    }
}

// ─── FOOTER ──────────────────────────────────────────────────────────────────
@Composable
fun AppFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(SpaceDeep.copy(.9f), SpaceMid.copy(.9f), SpaceBlue.copy(.9f))))
            .border(2.dp, PurpleNeon.copy(.3f), RoundedCornerShape(0.dp))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("✨ ¡Protege la galaxia del bienestar! 🌟", color = CyanNeon, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

// ─── COMBAT SCREEN ───────────────────────────────────────────────────────────
@Composable
fun CombatScreen(vm: GameViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            HeroStats(
                hp = vm.heroHp, maxHp = 100,
                level = vm.heroLevel,
                xp = vm.heroXp, maxXp = 100,
                gold = vm.heroGold,
            )
        }
        item {
            MonsterCard(
                name        = vm.currentMonster.name,
                hp          = vm.monsterHp,
                maxHp       = vm.currentMonster.maxHp,
                type        = vm.currentMonster.type,
                isAttacking = vm.isAttacking,
            )
        }
        item {
            BattleLog(messages = vm.battleLog)
        }
        item {
            ActionButtons(
                onWaterAttack   = { vm.attack(15, "Supernova de Agua") },
                onStretchAttack = { vm.attack(20, "Salto Galáctico") },
                onMindAttack    = { vm.attack(25, "Zen Cósmico") },
                disabled        = vm.monsterHp <= 0,
            )
        }
    }
}
