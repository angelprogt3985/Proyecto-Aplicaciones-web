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
import androidx.compose.foundation.lazy.LazyColumn
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
import com.google.firebase.auth.FirebaseAuth
import com.mindguardians.ui.components.*
import com.mindguardians.ui.screens.*
import com.mindguardians.ui.theme.*
import com.mindguardians.ui.screens.InventoryScreen
import com.mindguardians.ui.components.attacksForMonster

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
    TabItem(Screen.INVENTORY, "🎒",  "Inventario"),
    TabItem(Screen.GUIDE,     "📖",  "Guía"),
    )

// ─── FLUJO DE PANTALLAS AUTH ─────────────────────────────────────────────────
private enum class AuthScreen { LOGIN, REGISTER }

// ─── APP ROOT ────────────────────────────────────────────────────────────────
@Composable
fun MindGuardiansApp() {

    var sessionKey by remember { mutableIntStateOf(0) }
    var isLoggedIn by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }
    var authScreen by remember { mutableStateOf(AuthScreen.LOGIN) }

    val doLogout = {
        FirebaseAuth.getInstance().signOut()
        isLoggedIn = false
        authScreen = AuthScreen.LOGIN  // ← siempre vuelve al login, no al registro
        sessionKey++                   // ← destruye y recrea el AuthViewModel limpio
        Unit
    }

    if (!isLoggedIn) {
        // key(sessionKey) hace que Compose destruya y recree el AuthViewModel
        // con cada logout, evitando que isSuccess=true del login anterior
        // navegue solo sin que el usuario haga nada.
        key(sessionKey) {
            when (authScreen) {
                AuthScreen.LOGIN -> LoginScreen(
                    onLoginSuccess       = { isLoggedIn = true },
                    onNavigateToRegister = { authScreen = AuthScreen.REGISTER }
                )
                AuthScreen.REGISTER -> RegisterScreen(
                    onRegisterSuccess = { isLoggedIn = true },
                    onNavigateToLogin = { authScreen = AuthScreen.LOGIN }
                )
            }
        }
    } else {
        key(sessionKey) {
            GameScreen(doLogout = doLogout)
        }
    }
}

@Composable
private fun GameScreen(
    doLogout: () -> Unit,
    vm: GameViewModel = viewModel(),
) {
    // ── APP PRINCIPAL ─────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SpaceDark, SpaceDeep, SpaceMid)))
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── HEADER ───────────────────────────────────────────────────
            AppHeader(
                heroName  = vm.heroName,
                heroLevel = vm.heroLevel,
                heroGold  = vm.heroGold,
                onMenuClick = { vm.menuOpen = true },
                onLogout    = doLogout
            )

            // ── TAB BAR ──────────────────────────────────────────────────
            TabBar(
                current  = vm.currentScreen,
                onChange = { vm.currentScreen = it },
            )

            // ── CONTENIDO ────────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                when (vm.currentScreen) {
                    Screen.DASHBOARD -> DashboardScreen(vm.heroGold, vm.heroLevel)
                    Screen.SHOP      -> ShopScreen(vm = vm)
                    Screen.RANKING   -> RankingScreen(vm = vm)
                    Screen.ORACLE    -> OracleScreen(vm)
                    Screen.COMBAT    -> CombatScreen(vm)
                    Screen.INVENTORY -> InventoryScreen(vm = vm)
                    Screen.GUIDE     -> GuideScreen()
                }
            }

            // ── FOOTER ───────────────────────────────────────────────────
            AppFooter()
        }

        // ── OVERLAY: menú lateral ────────────────────────────────────────
        NavigationMenu(
            isOpen        = vm.menuOpen,
            onClose       = { vm.menuOpen = false },
            onNavigate    = { vm.currentScreen = it; vm.menuOpen = false },
            onLogout      = doLogout,
            currentScreen = vm.currentScreen,
            heroName      = vm.heroName,
            heroLevel     = vm.heroLevel,
            heroGold      = vm.heroGold,
        )

        // ── OVERLAY: modal de victoria ───────────────────────────────────
        VictoryModal(
            isOpen      = vm.isVictory,
            onContinue  = { vm.continueAfterVictory() },
            goldEarned  = vm.goldReward(),
            xpEarned    = vm.xpReward(),
        )

        DefeatModal(
            isOpen    = vm.isDefeat,
            onRecover = { vm.recoverAfterDefeat() },
        )
    }
}

// ─── HEADER ──────────────────────────────────────────────────────────────────
@Composable
fun AppHeader(
    heroName:    String,
    heroLevel:   Int,
    heroGold:    Int,
    onMenuClick: () -> Unit,
    onLogout:    () -> Unit = {},
) {
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
            .border(width = 2.dp, color = PurpleNeon.copy(.3f), shape = RoundedCornerShape(0.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Logo
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .rotate(rotation)
                    .clip(CircleShape)
                    .background(GoldNeon)
                    .border(2.dp, GoldDark.copy(.5f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("⭐", fontSize = 16.sp)
            }
            Column {
                Text(
                    heroName,
                    color = CyanNeon,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    maxLines = 1,
                )
                Text(
                    "Nv.$heroLevel  ·  💰$heroGold",
                    color = GoldNeon,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        // Botones
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(.05f))
                    .border(2.dp, Color(0xFFFF6B6B).copy(.4f), RoundedCornerShape(10.dp))
                    .clickable(onClick = onLogout),
                contentAlignment = Alignment.Center,
            ) {
                Text("🚪", fontSize = 15.sp)
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(.05f))
                    .border(2.dp, PurpleNeon.copy(.4f), RoundedCornerShape(10.dp))
                    .clickable(onClick = onMenuClick),
                contentAlignment = Alignment.Center,
            ) {
                Text("☰", color = CyanNeon, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
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
    val actions = attacksForMonster(
        monsterType = vm.currentMonster.type,
        onWater   = { vm.attack(20, "Hidratación Cósmica") },
        onStretch = { vm.attack(25, "Salto Galáctico") },
        onMind    = { vm.attack(30, "Zen Cósmico") },
        onSleep   = { vm.attack(25, "Sueño Estelar") },
        onBreath  = { vm.attack(15, "Aliento del Cosmos") },
    )
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            HeroStats(
                hp = vm.heroHp, maxHp = vm.heroMaxHP,
                level = vm.heroLevel,
                xp = vm.heroXp, maxXp = 100,
                gold = vm.heroGold,
            )
        }
        item {
            if (vm.currentMonster.isBoss) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEF4444).copy(alpha = .15f))
                        .border(1.dp, Color(0xFFEF4444).copy(.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "BOSS BATTLE",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                    )
                }
            }
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

        if (vm.bonusActive) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoldNeon.copy(alpha = .15f))
                        .border(1.dp, GoldNeon.copy(.6f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "⚡ PODER x1.5 ACTIVO — Úsalo en tu próximo ataque",
                        color = GoldNeon,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = .5.sp,
                    )
                }
            }
        }
        item {
            ActionButtons(
                actions  = actions,
                disabled = vm.isBusy || vm.monsterHp <= 0 || vm.isDefeat,
                )
        }
        item {
            ReportPanel(vm = vm)
        }
    }

}


