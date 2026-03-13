package com.mindguardians.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mindguardians.ui.theme.*

@Composable
fun VictoryModal(
    isOpen: Boolean,
    onContinue: () -> Unit,
    goldEarned: Int,
    xpEarned: Int,
) {
    if (!isOpen) return

    // Animación de escala de entrada
    val scaleAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        )
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xEA000000)),
            contentAlignment = Alignment.Center,
        ) {
            // Tarjeta exterior (borde dorado)
            Box(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .scale(scaleAnim.value)
                    .clip(RoundedCornerShape(36.dp))
                    .background(
                        Brush.verticalGradient(listOf(GoldNeon, CyanNeon))
                    )
                    .padding(3.dp)
            ) {
                // Tarjeta interior
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(33.dp))
                        .background(Brush.verticalGradient(listOf(SpaceDeep, SpaceDark)))
                        .border(4.dp, BorderPurple, RoundedCornerShape(33.dp))
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("🏆", fontSize = 80.sp)
                    Spacer(Modifier.height(12.dp))

                    Text(
                        "¡VICTORIA!",
                        color = GoldNeon,
                        fontWeight = FontWeight.Black,
                        fontSize = 36.sp,
                        letterSpacing = 2.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "¡Has salvado la galaxia del bienestar!",
                        color = TextWhite.copy(alpha = .85f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(24.dp))

                    // Oro ganado
                    RewardRow(emoji = "💰", label = "Estrellas de Oro", value = "+$goldEarned", valueColor = GoldNeon, borderColor = GoldNeon.copy(.4f))
                    Spacer(Modifier.height(10.dp))
                    // XP ganado
                    RewardRow(emoji = "⚡", label = "Energía Cósmica",  value = "+$xpEarned",  valueColor = CyanNeon, borderColor = CyanNeon.copy(.4f))

                    Spacer(Modifier.height(24.dp))

                    // Botón continuar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(listOf(CyanNeon, PurpleNeon, GoldNeon))
                            )
                            .border(4.dp, Color.White.copy(.2f), RoundedCornerShape(16.dp))
                            .clickableNoRipple(onContinue)
                            .padding(vertical = 18.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "🚀 ¡SIGUIENTE AVENTURA!",
                            color = TextWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardRow(emoji: String, label: String, value: String, valueColor: Color, borderColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(.4f))
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(emoji, fontSize = 22.sp)
        Text(label, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
        Text(value, color = valueColor, fontWeight = FontWeight.Black, fontSize = 22.sp)
    }
}

// Helper: clickable sin efecto ripple
@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
