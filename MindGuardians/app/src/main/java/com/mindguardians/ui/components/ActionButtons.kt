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

// Devuelve la lista de ataques apropiada para el tipo de monstruo dado
fun attacksForMonster(
    monsterType: String,
    onWater:     () -> Unit,
    onStretch:   () -> Unit,
    onMind:      () -> Unit,
    onSleep:     () -> Unit,
    onBreath:    () -> Unit,
): List<AttackAction> {
    return when {
        // Monstruos de tipo físico/gravedad — el movimiento y el agua son muy efectivos
        monsterType.contains("Gravedad", ignoreCase = true) ||
                monsterType.contains("Peso",     ignoreCase = true) -> listOf(
            AttackAction("💧", "Hidratación Cósmica", "Beber Agua",        20, Color(0xFF0099CC), Color(0xFF006699), onClick = onWater),
            AttackAction("🌟", "Salto Galáctico",     "Estirarse",         25, Color(0xFF6B21A8), Color(0xFF581C87), onClick = onStretch),
            AttackAction("🌬️", "Aliento del Cosmos",  "Respirar profundo", 15, Color(0xFF0F766E), Color(0xFF0D5C55), onClick = onBreath),
        )
        // Monstruos de tipo mental/caos — la meditación y el sueño son muy efectivos
        monsterType.contains("Caos",   ignoreCase = true) ||
                monsterType.contains("Mental", ignoreCase = true) ||
                monsterType.contains("Mente",  ignoreCase = true) -> listOf(
            AttackAction("✨", "Zen Cósmico",         "Meditar",           30, Color(0xFFF59E0B), Color(0xFFD97706), textColor = Color.Black, onClick = onMind),
            AttackAction("🌙", "Sueño Estelar",       "Descansar",         25, Color(0xFF4338CA), Color(0xFF3730A3), onClick = onSleep),
            AttackAction("🌬️", "Aliento del Cosmos",  "Respirar profundo", 20, Color(0xFF0F766E), Color(0xFF0D5C55), onClick = onBreath),
        )
        // Monstruos de tipo vacío/energía — balance de todos los hábitos
        monsterType.contains("Vacío",   ignoreCase = true) ||
                monsterType.contains("Estelar", ignoreCase = true) ||
                monsterType.contains("Energía", ignoreCase = true) -> listOf(
            AttackAction("💧", "Hidratación Cósmica", "Beber Agua",  20, Color(0xFF0099CC), Color(0xFF006699), onClick = onWater),
            AttackAction("✨", "Zen Cósmico",         "Meditar",     20, Color(0xFFF59E0B), Color(0xFFD97706), textColor = Color.Black, onClick = onMind),
            AttackAction("🌙", "Sueño Estelar",       "Descansar",   20, Color(0xFF4338CA), Color(0xFF3730A3), onClick = onSleep),
        )
        // Monstruos oscuros/fuerza — el estiramiento y la meditación rompen su poder
        monsterType.contains("Oscura", ignoreCase = true) ||
                monsterType.contains("Fuerza", ignoreCase = true) -> listOf(
            AttackAction("🌟", "Salto Galáctico",     "Estirarse",   25, Color(0xFF6B21A8), Color(0xFF581C87), onClick = onStretch),
            AttackAction("✨", "Zen Cósmico",         "Meditar",     25, Color(0xFFF59E0B), Color(0xFFD97706), textColor = Color.Black, onClick = onMind),
            AttackAction("💧", "Hidratación Cósmica", "Beber Agua",  15, Color(0xFF0099CC), Color(0xFF006699), onClick = onWater),
        )
        // Default — los 3 ataques clásicos para cualquier tipo no reconocido
        else -> listOf(
            AttackAction("💧", "Elixir Estelar",  "Beber Agua",  15, Color(0xFF0099CC), Color(0xFF006699), onClick = onWater),
            AttackAction("🌟", "Salto Galáctico", "Estirarse",   20, Color(0xFF6B21A8), Color(0xFF581C87), onClick = onStretch),
            AttackAction("✨", "Zen Cósmico",     "Meditar",     25, Color(0xFFF59E0B), Color(0xFFD97706), textColor = Color.Black, onClick = onMind),
        )
    }
}

@Composable
fun ActionButtons(
    actions:  List<AttackAction>,
    disabled: Boolean,
) {
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
