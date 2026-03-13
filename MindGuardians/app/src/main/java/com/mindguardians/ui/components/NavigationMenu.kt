package com.mindguardians.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider as Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguardians.Screen
import com.mindguardians.ui.theme.*
import kotlin.math.roundToInt

data class NavItem(
    val screen: Screen,
    val emoji: String,
    val label: String,
    val desc: String,
    val color: Color,
    val badge: String? = null,
)

val NAV_ITEMS = listOf(
    NavItem(Screen.COMBAT,    "⚔️",  "Combate",         "Pantalla de batalla",      CyanNeon),
    NavItem(Screen.DASHBOARD, "📊",  "Dashboard",       "Estadísticas y progreso",  PurpleNeon),
    NavItem(Screen.SHOP,      "🛍️", "Tienda",           "Equipa a tu héroe",        GoldNeon, badge = "3"),
    NavItem(Screen.RANKING,   "🏆",  "Ranking Global",  "Top guerreros del reino",  Color(0xFFEF4444)),
    NavItem(Screen.ORACLE,    "✨",  "Oráculo IA",      "Narrador Gemini",          GreenNeon, badge = "!"),
)

@Composable
fun NavigationMenu(
    isOpen: Boolean,
    onClose: () -> Unit,
    onNavigate: (Screen) -> Unit,
    currentScreen: Screen,
    heroLevel: Int,
    heroGold: Int,
) {
    val offsetAnim = animateFloatAsState(
        targetValue = if (isOpen) 0f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "menuSlide",
    )

    if (!isOpen && offsetAnim.value >= 0.99f) return   // evita recomposición innecesaria

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val menuWidthPx = (maxWidth * .78f).value

        // Overlay oscuro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = (1f - offsetAnim.value) * .65f))
                .clickable(onClick = onClose),
        )

        // Panel lateral
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(maxWidth * .78f)
                .align(Alignment.CenterEnd)
                .offset { IntOffset((menuWidthPx * offsetAnim.value).roundToInt().dp.roundToPx(), 0) }
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF0D0D1F), Color(0xFF10102A)))
                )
                .border(
                    width = 2.dp,
                    color = PurpleNeon.copy(alpha = .4f),
                    shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp),
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Header ──────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(SpaceDeep, SpaceBlue))
                        )
                        .border(
                            width = 1.dp,
                            color = PurpleNeon.copy(.3f),
                            shape = RoundedCornerShape(0.dp),
                        )
                        .padding(start = 24.dp, end = 48.dp, top = 48.dp, bottom = 20.dp)
                ) {
                    Column {
                        // Botón cerrar
                        Box(
                            modifier = Modifier
                                .align(Alignment.End)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(.05f))
                                .border(1.dp, Color.White.copy(.1f), CircleShape)
                                .clickable(onClick = onClose),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("✕", color = Color.White.copy(.6f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(12.dp))

                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Brush.radialGradient(listOf(PurpleNeon, CyanNeon)))
                                .border(2.dp, GoldNeon.copy(.5f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("⚔️", fontSize = 26.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("PaladinUrb_24", color = TextWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        Spacer(Modifier.height(2.dp))
                        Text("★ NIVEL $heroLevel · GUERRERO ESTELAR", color = CyanNeon, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }

                // ── Etiqueta sección ──────────────────────────────────────
                Text(
                    "NAVEGACIÓN",
                    color = PurpleNeon.copy(.8f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 4.dp),
                )

                // ── Items ─────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    NAV_ITEMS.forEach { item ->
                        val active = currentScreen == item.screen
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (active) CyanNeon.copy(.08f) else Color.Transparent)
                                .border(
                                    start = if (active) BorderStroke(3.dp, CyanNeon) else BorderStroke(3.dp, Color.Transparent)
                                )
                                .clickable { onNavigate(item.screen); onClose() }
                                .padding(horizontal = 24.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(item.color.copy(.13f))
                                    .border(1.dp, item.color.copy(.27f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(item.emoji, fontSize = 16.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.label, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(item.desc,  color = TextMuted, fontSize = 11.sp)
                            }
                            if (item.badge != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(item.badge, color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            Text("›", color = Color.White.copy(.2f), fontSize = 18.sp)
                        }
                    }

                    Divider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))

                    // Perfil
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                            .padding(horizontal = 24.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFA855F7).copy(.2f))
                                .border(1.dp, Color(0xFFA855F7).copy(.3f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("👤", fontSize = 16.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mi Perfil",               color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Configuración y avatar",  color = TextMuted, fontSize = 11.sp)
                        }
                        Text("›", color = Color.White.copy(.2f), fontSize = 18.sp)
                    }
                }

                // ── Footer ────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(topWidth = 1.dp, color = PurpleNeon.copy(.2f))
                        .padding(20.dp),
                ) {
                    // Mini stats
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            Triple("142", "Combates", GoldNeon),
                            Triple(heroLevel.toString(), "Nivel",  CyanNeon),
                            Triple(heroGold.toString(),  "Oro",    PurpleLight),
                        ).forEach { (value, label, color) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(.04f))
                                    .border(1.dp, PurpleNeon.copy(.2f), RoundedCornerShape(12.dp))
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                    Text(label, color = TextDim,  fontSize = 9.sp, letterSpacing = .5.sp)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Logout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEF4444).copy(.07f))
                            .border(1.dp, Color(0xFFEF4444).copy(.3f), RoundedCornerShape(12.dp))
                            .clickable { }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("🚪 Cerrar Sesión", color = Color(0xFFEF4444).copy(.8f), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// Helper para BorderStroke sin índice
private fun Modifier.border(start: BorderStroke): Modifier = this  // se usa la API estándar abajo

// Divider helper
@Composable
private fun Divider(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(PurpleNeon.copy(.2f))
    )
}

// Border start helper
private fun Modifier.border(topWidth: androidx.compose.ui.unit.Dp, color: Color): Modifier =
    this.border(
        width = topWidth,
        color = color,
        shape = RoundedCornerShape(0.dp),
    )
