package com.mindguardians.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguardians.ui.theme.*

@Composable
fun GuideScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(.4f))
                .border(1.dp, GoldNeon.copy(.4f), RoundedCornerShape(20.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📖", fontSize = 40.sp)
                Spacer(Modifier.height(8.dp))
                Row {
                    Text("Guía de ", color = TextWhite, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text("Winni", color = GoldNeon, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text("Knight", color = CyanNeon, fontWeight = FontWeight.Black, fontSize = 20.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Todo lo que necesitas saber para la app Android",
                    color = Color.White.copy(.4f),
                    fontSize = 11.sp,
                )
            }
        }

        // 1. Combate
        GuideSection(
            emoji    = "⚔️",
            title    = "Pantalla de Combate",
            subtitle = "Cómo derrotar a los Enemigos de Salud",
            borderColor = CyanNeon,
        ) {
            GuideCard {
                GuideText("Al iniciar sesión entrarás directamente al combate. Verás la barra de vida de tu héroe arriba y el monstruo actual en el centro.")
            }
            Spacer(Modifier.height(10.dp))
            GuideCard {
                GuideSectionLabel("Botones de Ataque", CyanNeon)
                Spacer(Modifier.height(8.dp))
                GuideStep(1, "Agua / Estiramiento / Meditación", "Cada botón representa una acción de salud real. Realiza la acción en la vida real y luego presiona el botón.")
                GuideStep(2, "Los ataques cambian según el enemigo", "Un Golem de Gravedad es débil al agua y al movimiento. Una Nebulosa de Caos es débil a la meditación. Los botones se adaptan automáticamente.")
                GuideStep(3, "Daño mostrado en el botón", "El número en el botón es el daño base. Si tienes equipo activo, el daño real será mayor.")
            }
            Spacer(Modifier.height(10.dp))
            GuideTip(CyanNeon, "💡", "El monstruo contraataca después de cada ataque tuyo. Si tu HP llega a 0 perderás el combate, pero podrás recuperarte sin perder datos.")
        }

        // 2. Narrador del Oráculo (en combate)
        GuideSection(
            emoji    = "🔮",
            title    = "Narrador del Oráculo",
            subtitle = "Panel de IA en la pantalla de combate",
            borderColor = GreenNeon,
        ) {
            GuideCard {
                GuideText("En la parte inferior de la pantalla de Combate encontrarás el panel del Narrador. Tiene dos funciones:")
                Spacer(Modifier.height(10.dp))
                GuideStep(1, "⚡ Hazaña", "Describe una acción saludable que hayas realizado hoy (\"Tomé 2 litros de agua\", \"Dormí 8 horas\"). La IA generará una frase épica y tu próximo ataque tendrá x1.5 de daño.")
                GuideStep(2, "☠ Debilidad", "Describe un problema de salud que tengas (\"Llevo 3 días sin dormir bien\"). La IA creará un jefe especial basado en esa debilidad que aparecerá en tu cola de combates.")
            }
            GuideTip(GreenNeon, "⚡", "El bonus x1.5 se consume en el siguiente ataque. Úsalo con el ataque de mayor daño disponible.")
        }

        // 3. Tienda
        GuideSection(
            emoji    = "🛍️",
            title    = "Tienda del Héroe",
            subtitle = "Gasta tu oro para mejorar tu personaje",
            borderColor = GoldNeon,
        ) {
            GuideCard {
                GuideStep(1, "Gana oro en combate", "Al derrotar enemigos recibes oro automáticamente. Los jefes dan más oro que los enemigos normales.")
                GuideStep(2, "Ve a la tab Tienda", "Verás los ítems disponibles. Los que no puedes comprar están bloqueados (sin suficiente oro).")
                GuideStep(3, "Toca el ítem para comprarlo", "Si tienes suficiente oro, la compra se realiza de inmediato y el ítem aparece en tu Inventario.")
            }
            Spacer(Modifier.height(10.dp))
            GuideCard(borderColor = GoldNeon.copy(.3f)) {
                GuideSectionLabel("Efectos reales del equipo", GoldNeon)
                Spacer(Modifier.height(8.dp))
                GuideItemEffect("🗡️ Espada del Amanecer",  "+10% a todo el daño",       CyanNeon)
                GuideItemEffect("🛡️ Escudo Estelar",       "+15 HP máximo",              Color(0xFF4ADE80))
                GuideItemEffect("🪖 Casco de Claridad",    "+20% daño de Meditación",    PurpleLight)
                GuideItemEffect("👟 Botas del Cosmos",     "+15% daño de Estiramiento",  GoldNeon)
                GuideItemEffect("💎 Amuleto Galáctico",    "+5% daño + 5 HP extra",      CyanNeon)
                GuideItemEffect("🔮 Orbe del Oráculo",     "+30% a todo el daño",        GreenNeon)
            }
            GuideTip(GoldNeon, "🏅", "El equipo se sincroniza automáticamente con la web. Lo que compres aquí aparece en tu panel web y viceversa.")
        }

        // 4. Inventario
        GuideSection(
            emoji    = "🎒",
            title    = "Inventario",
            subtitle = "Revisa todo tu equipo comprado",
            borderColor = PurpleLight,
        ) {
            GuideCard {
                GuideText("La tab Inventario muestra todos los objetos que has comprado, con fecha de compra, descripción y precio pagado.")
                Spacer(Modifier.height(10.dp))
                GuideText("Los stats de todo el equipo se aplican automáticamente a tu héroe en todo momento. No hace falta hacer nada para equiparlos.")
            }
        }

        // 5. Oráculo
        GuideSection(
            emoji    = "✨",
            title    = "Oráculo de Gemini",
            subtitle = "Chat de IA para consejos de salud",
            borderColor = GreenNeon,
        ) {
            GuideCard {
                GuideStep(1, "Escribe tu consulta", "Cualquier pregunta sobre salud, ejercicio, alimentación o bienestar.")
                GuideStep(2, "Presiona 🔮 Consultar", "La IA (Google Gemini) responderá con consejos personalizados en tono épico.")
                GuideStep(3, "Lee la respuesta del Oráculo", "Aparece en verde en el lado izquierdo. Tus mensajes en morado a la derecha.")
            }
            GuideTip(GreenNeon, "💡", "El historial del chat se borra al salir de la pantalla. Es una conversación fresca cada vez.")
        }

        // 6. Ranking
        GuideSection(
            emoji    = "🏆",
            title    = "Ranking Global",
            subtitle = "Compara tu progreso con otros jugadores",
            borderColor = Color(0xFFEF4444),
        ) {
            GuideCard {
                GuideText("Muestra los 10 mejores héroes del mundo ordenados por XP total. El top 3 tiene podio visual. Tu entrada aparece resaltada en cyan si estás en el ranking.")
                Spacer(Modifier.height(10.dp))
                GuideText("Para subir posiciones: gana más combates, sube de nivel y acumula XP.")
            }
        }

        // 7. Subir de nivel
        GuideSection(
            emoji    = "⭐",
            title    = "Sistema de Progresión",
            subtitle = "Cómo funciona el nivel y la experiencia",
            borderColor = GoldNeon,
        ) {
            GuideCard {
                GuideStep(1, "Gana XP al vencer enemigos", "Cada victoria otorga XP. Los jefes generados por IA dan más XP.")
                GuideStep(2, "Sube de nivel al llegar a 100 XP", "Al subir de nivel tu HP máximo base aumenta +10.")
                GuideStep(3, "Tu vida se restaura tras cada victoria", "Al terminar un combate tu HP vuelve al máximo para el siguiente.")
            }
            GuideTip(GoldNeon, "🏅", "El equipo de la tienda aumenta aún más tu HP máximo y daño, por encima del bonus de nivel.")
        }

        // 8. Sincronización web
        GuideSection(
            emoji    = "🌐",
            title    = "Sincronización con la Web",
            subtitle = "Android y la web comparten tus datos",
            borderColor = CyanNeon,
        ) {
            GuideCard {
                GuideText("Todo tu progreso (nivel, oro, XP, inventario, combates) se guarda en Firebase y es visible en tiempo real desde la web de WinniKnight.")
                Spacer(Modifier.height(10.dp))
                GuideText("Los combates que ganas en Android aparecen en el historial web. El equipo que compras en cualquier plataforma aparece en el inventario de ambas.")
            }
            GuideTip(CyanNeon, "💡", "Si no ves tus datos actualizados en la web, recarga la página. Los datos tardan unos segundos en sincronizarse.")
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ─── Componentes internos ────────────────────────────────────────────────────

@Composable
private fun GuideSection(
    emoji:       String,
    title:       String,
    subtitle:    String,
    borderColor: Color,
    content:     @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(borderColor.copy(.12f))
                    .border(1.dp, borderColor.copy(.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(emoji, fontSize = 22.sp)
            }
            Column {
                Text(title, color = borderColor, fontWeight = FontWeight.Black, fontSize = 15.sp)
                Text(subtitle, color = Color.White.copy(.35f), fontSize = 11.sp)
            }
        }
        content()
    }
}

@Composable
private fun GuideCard(
    borderColor: Color = Color.White.copy(.1f),
    content:     @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(.3f))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        content = content,
    )
}

@Composable
private fun GuideText(text: String) {
    Text(text, color = Color.White.copy(.6f), fontSize = 12.sp, lineHeight = 18.sp)
}

@Composable
private fun GuideSectionLabel(text: String, color: Color) {
    Text(text, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = .5.sp)
}

@Composable
private fun GuideStep(number: Int, title: String, desc: String) {
    Row(
        modifier = Modifier.padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(CyanNeon.copy(.15f))
                .border(1.dp, CyanNeon.copy(.5f), RoundedCornerShape(50.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(number.toString(), color = CyanNeon, fontWeight = FontWeight.Black, fontSize = 11.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.height(2.dp))
            Text(desc, color = Color.White.copy(.5f), fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun GuideItemEffect(name: String, effect: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name, color = TextWhite, fontSize = 11.sp, modifier = Modifier.weight(1f))
        Text(effect, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun GuideTip(borderColor: Color, emoji: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(borderColor.copy(.06f))
            .border(1.dp, borderColor.copy(.25f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(emoji, fontSize = 16.sp)
        Text(text, color = Color.White.copy(.55f), fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.weight(1f))
    }
}
