package com.mindguardians.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguardians.ui.theme.*

@Composable
fun MonsterCard(
    name: String,
    hp: Int,
    maxHp: Int,
    type: String,
    isAttacking: Boolean,
) {
    val pct = if (maxHp > 0) hp.toFloat() / maxHp else 0f

    // Animación flotante
    val floatAnim = rememberInfiniteTransition(label = "float")
    val offsetY by floatAnim.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "floatY",
    )

    // Shake al recibir daño
    val shakeAnim = remember { Animatable(0f) }
    LaunchedEffect(isAttacking) {
        if (isAttacking) {
            shakeAnim.animateTo(0f, animationSpec = keyframes {
                durationMillis = 400
                -14f at 80
                14f  at 160
                -8f  at 240
                0f   at 320
            })
        }
    }

    val barColor = when {
        pct > 0.6f -> CyanNeon
        pct > 0.3f -> GoldNeon
        else       -> Color(0xFFEF4444)
    }

    // Tarjeta completa como columna vertical
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0x99000000))
            .border(2.dp, BorderPurple, RoundedCornerShape(32.dp))
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Badge tipo
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(CyanNeon)
                .border(2.dp, CyanNeon.copy(alpha = .4f), RoundedCornerShape(50.dp))
                .padding(horizontal = 20.dp, vertical = 6.dp),
        ) {
            Text(
                type,
                color = TextWhite,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
            )
        }

        Spacer(Modifier.height(16.dp))

        // Monstruo flotante
        Box(
            modifier = Modifier
                .offset(y = offsetY.dp)
                .rotate(shakeAnim.value)
                .size(120.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(PurpleNeon, SpaceDeep, SpaceDark)))
                .border(4.dp, CyanNeon.copy(alpha = .5f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("💀", fontSize = 60.sp)
        }

        Spacer(Modifier.height(16.dp))

        // Nombre del monstruo — siempre debajo del círculo
        Text(
            text = name,
            color = TextWhite,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))

        // Barra HP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("❤️ VIDA ENEMIGA", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("$hp/$maxHp",      color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(Color(0x4D000000))
                .border(2.dp, BorderPurple, RoundedCornerShape(50.dp))
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(pct)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50.dp))
                    .background(barColor)
            )
        }
    }
}