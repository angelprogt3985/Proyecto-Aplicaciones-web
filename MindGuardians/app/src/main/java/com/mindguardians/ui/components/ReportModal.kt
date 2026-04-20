package com.mindguardians.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguardians.GameViewModel

@Composable
fun ReportPanel(vm: GameViewModel) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .background(Color.Black.copy(.3f))
            .border(1.dp, com.mindguardians.ui.theme.PurpleNeon.copy(.3f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            "NARRADOR DEL ORÁCULO",
            color = Color.White.copy(.4f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        androidx.compose.material3.TextField(
            value = vm.reportText,
            onValueChange = { vm.reportText = it },
            placeholder = {
                Text(
                    "Describe una hazaña que hayas realizado o una debilidad que" +
                            "tengas actualmente",
                    color = Color.White.copy(.25f),
                    fontSize = 12.sp,
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                .background(Color.Black.copy(.3f))
                .border(1.dp, com.mindguardians.ui.theme.PurpleNeon.copy(.2f), androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedContainerColor   = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor        = com.mindguardians.ui.theme.TextWhite.copy(.8f),
                unfocusedTextColor      = com.mindguardians.ui.theme.TextWhite.copy(.8f),
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            minLines = 2,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Botón Hazaña
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                    .background(if (vm.isReporting) Color.Gray else Color(0xFF166534))
                    .border(2.dp, com.mindguardians.ui.theme.GreenNeon.copy(.4f), androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                    .clickable(enabled = !vm.isReporting) { vm.reportDeed(vm.reportText) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (vm.isReporting) "..." else "⚡ Hazaña",
                    color = com.mindguardians.ui.theme.TextWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                )
            }
            // Botón Debilidad
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                    .background(if (vm.isReporting) Color.Gray else Color(0xFF7C1D1D))
                    .border(2.dp, Color(0xFFEF4444).copy(.4f), androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                    .clickable(enabled = !vm.isReporting) { vm.reportWeakness(vm.reportText) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (vm.isReporting) "..." else "☠ Debilidad",
                    color = com.mindguardians.ui.theme.TextWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                )
            }
        }
    }
}