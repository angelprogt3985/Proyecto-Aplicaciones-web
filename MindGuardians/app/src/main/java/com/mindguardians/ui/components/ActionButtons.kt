package com.mindguardians.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguardians.ui.theme.*

data class AttackAction(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val damage: Int,
    val bgColor: Color,
    val borderColor: Color,
    val textColor: Color = TextWhite,
    val onClick: () -> Unit,
)

@Composable
fun ActionButtons(
    onWaterAttack: () -> Unit,
    onStretchAttack: () -> Unit,
    onMindAttack: () -> Unit,
    disabled: Boolean,
) {
    val actions = listOf(
        AttackAction("💧", "Elixir Estelar",  "Beber Agua",  15, Color(0xFF0099CC), Color(0xFF006699), onClick = onWaterAttack),
        AttackAction("🌟", "Salto Galáctico", "Estirarse",   20, Color(0xFF6B21A8), Color(0xFF581C87), onClick = onStretchAttack),
        AttackAction("✨", "Zen Cósmico",     "Meditar",     25, Color(0xFFF59E0B), Color(0xFFD97706), textColor = Color.Black, onClick = onMindAttack),
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        actions.forEach { action ->
            AttackButton(action = action, disabled = disabled)
        }
    }
}

@Composable
private fun AttackButton(action: AttackAction, disabled: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (disabled) 0.4f else 1f)
            .clip(RoundedCornerShape(16.dp))
            .background(action.bgColor)
            .border(
                width = 0.dp,
                color = Color.Transparent,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(enabled = !disabled) { action.onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Ícono
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = .1f))
                    .border(2.dp, Color.White.copy(alpha = .2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(action.emoji, fontSize = 20.sp)
            }

            // Texto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.title.uppercase(),
                    color = action.textColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = .5.sp,
                )
                Text(
                    text = action.subtitle,
                    color = action.textColor.copy(alpha = .8f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                )
            }

            // Daño
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = .3f))
                    .border(2.dp, action.borderColor.copy(alpha = .6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    "-${action.damage}",
                    color = TextWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                )
            }
        }
    }
}
