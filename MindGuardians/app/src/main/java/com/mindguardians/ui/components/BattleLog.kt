package com.mindguardians.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguardians.BattleMessage
import com.mindguardians.MessageType
import com.mindguardians.ui.theme.*

@Composable
fun BattleLog(messages: List<BattleMessage>) {
    val listState = rememberLazyListState()
    val recent = messages.takeLast(5)

    LaunchedEffect(messages.size) {
        if (recent.isNotEmpty()) listState.animateScrollToItem(recent.size - 1)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x4D000000))
            .border(2.dp, BorderPurple, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Column {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoldNeon)
                        .padding(6.dp)
                ) {
                    Text("⚔️", fontSize = 13.sp)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "BITÁCORA ESPACIAL",
                    color = TextWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                )
            }

            Spacer(Modifier.height(8.dp))

            if (recent.isEmpty()) {
                Text("⭐ ¡Listo para la aventura!", color = CyanNeon, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontSize = 13.sp)
            } else {
                LazyColumn(state = listState) {
                    items(recent) { msg ->
                        val (emoji, color) = when (msg.type) {
                            MessageType.DAMAGE -> "💥" to CyanNeon
                            MessageType.REWARD -> "⭐" to GoldNeon
                            MessageType.INFO   -> "✨" to PurpleLight
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp),
                        ) {
                            Text(emoji, fontSize = 14.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(msg.text, color = color, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
