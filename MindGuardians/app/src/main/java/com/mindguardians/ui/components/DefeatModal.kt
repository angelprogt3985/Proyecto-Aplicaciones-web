package com.mindguardians.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.mindguardians.ui.theme.BorderPurple
import com.mindguardians.ui.theme.PurpleNeon
import com.mindguardians.ui.theme.SpaceDark
import com.mindguardians.ui.theme.SpaceDeep
import com.mindguardians.ui.theme.TextWhite

@Composable
fun DefeatModal(isOpen: Boolean, onRecover: () -> Unit) {
    if (!isOpen) return

    val scaleAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue   = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        )
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = {},
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xEA000000)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .scale(scaleAnim.value)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFFEF4444), PurpleNeon)))
                    .padding(3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(33.dp))
                        .background(Brush.verticalGradient(listOf(SpaceDeep, SpaceDark)))
                        .border(4.dp, BorderPurple, RoundedCornerShape(33.dp))
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("💀", fontSize = 80.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "DERROTA",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Black,
                        fontSize = 36.sp,
                        letterSpacing = 2.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "El cosmos te reclama. Recupera fuerzas y vuelve más fuerte.",
                        color = TextWhite.copy(alpha = .75f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFFEF4444), PurpleNeon)))
                            .border(4.dp, Color.White.copy(.15f), RoundedCornerShape(16.dp))
                            .clickable { onRecover() }
                            .padding(vertical = 18.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "🔄 RECUPERARSE Y CONTINUAR",
                            color = TextWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp,
                        )
                    }
                }
            }
        }
    }
}