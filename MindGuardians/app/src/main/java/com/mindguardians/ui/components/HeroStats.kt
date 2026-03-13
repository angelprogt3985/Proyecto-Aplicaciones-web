package com.mindguardians.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguardians.ui.theme.*

@Composable
fun HeroStats(
    hp: Int, maxHp: Int,
    level: Int,
    xp: Int, maxXp: Int,
    gold: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x80000000))
            .border(2.dp, BorderPurple, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            // Nivel + Oro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Badge nivel
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(PurpleNeon)
                        .border(2.dp, PurpleLight.copy(alpha = .4f), RoundedCornerShape(50.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("⭐ NIVEL $level", color = TextWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
                // Badge oro
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(GoldNeon)
                        .border(2.dp, GoldDark.copy(alpha = .4f), RoundedCornerShape(50.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("💰 $gold", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Barra HP
            StatBar(
                emoji = "❤️", label = "VIDA",
                current = hp, max = maxHp,
                fillColor = Brush.horizontalGradient(listOf(CyanNeon, PurpleNeon)),
            )

            Spacer(Modifier.height(10.dp))

            // Barra XP
            StatBar(
                emoji = "⚡", label = "ENERGÍA",
                current = xp, max = maxXp,
                fillColor = Brush.horizontalGradient(listOf(GoldNeon, PurpleNeon)),
            )
        }
    }
}

@Composable
private fun StatBar(
    emoji: String,
    label: String,
    current: Int,
    max: Int,
    fillColor: Brush,
) {
    val pct = if (max > 0) current.toFloat() / max else 0f

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("$emoji $label", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Text("$current/$max", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
    Spacer(Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Color(0x4D000000))
            .border(1.dp, BorderPurple, RoundedCornerShape(50.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(pct)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50.dp))
                .background(fillColor)
        )
    }
}
