package com.mindguardians.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindguardians.GameViewModel
import com.mindguardians.ui.theme.*

private val HERO_CLASSES = listOf(
    "Guerrero", "Sanador", "Asesino", "Mago", "Caballero", "Arquero", "Druida", "Paladín"
)
private val CLASS_ICONS = mapOf(
    "Guerrero"  to "⚔️",
    "Sanador"   to "⚕️",
    "Asesino"   to "🥷",
    "Mago"      to "🔮",
    "Caballero" to "🛡️",
    "Arquero"   to "🏹",
    "Druida"    to "🌿",
    "Paladín"   to "✨",
)

@Composable
fun ProfileScreen(vm: GameViewModel) {
    var editingName  by remember { mutableStateOf(vm.heroName) }
    var editingClass by remember { mutableStateOf(vm.heroClass) }
    var saving       by remember { mutableStateOf(false) }
    var saved        by remember { mutableStateOf(false) }
    var isEditing    by remember { mutableStateOf(false) }

    // Sincronizar si el VM cambia externamente
    LaunchedEffect(vm.heroName, vm.heroClass) {
        if (!isEditing) {
            editingName  = vm.heroName
            editingClass = vm.heroClass
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Header con avatar y nombre
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(.4f))
                .border(1.dp, CyanNeon.copy(.3f), RoundedCornerShape(20.dp))
                .padding(24.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                // Icono de clase
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(GoldNeon.copy(.1f))
                        .border(2.dp, GoldNeon.copy(.4f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(CLASS_ICONS[vm.heroClass] ?: "⚔️", fontSize = 36.sp)
                }
                Spacer(Modifier.height(12.dp))

                // Nombre — editable o display
                if (isEditing) {
                    BasicTextField(
                        value = editingName,
                        onValueChange = { editingName = it },
                        textStyle = TextStyle(
                            color = TextWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                        ),
                        cursorBrush = SolidColor(CyanNeon),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(.07f))
                            .border(1.dp, CyanNeon.copy(.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .fillMaxWidth(),
                        singleLine = true,
                    )
                } else {
                    Text(vm.heroName, color = TextWhite, fontWeight = FontWeight.Black, fontSize = 22.sp)
                }

                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatChip("Nivel ${vm.heroLevel}", GoldNeon)
                    StatChip(vm.heroClass, CyanNeon)
                }
            }
        }

        // Stats del héroe
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(modifier = Modifier.weight(1f), emoji = "⭐", label = "Nivel",      value = vm.heroLevel.toString(), color = GoldNeon)
            StatCard(modifier = Modifier.weight(1f), emoji = "✨", label = "XP Total",   value = vm.totalXp.toString(),   color = CyanNeon)
            StatCard(modifier = Modifier.weight(1f), emoji = "💛", label = "Oro",         value = vm.heroGold.toString(),  color = GoldNeon)
            StatCard(modifier = Modifier.weight(1f), emoji = "❤️", label = "HP",          value = "${vm.heroHp}/${vm.heroMaxHP}", color = Color(0xFFEF4444))
        }

        // Selector de clase (solo en modo edición)
        if (isEditing) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(.3f))
                    .border(1.dp, Color.White.copy(.1f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("Elige tu clase:", color = Color.White.copy(.5f), fontSize = 12.sp)
                // Grid 4x2
                val rows = HERO_CLASSES.chunked(4)
                rows.forEach { rowClasses ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        rowClasses.forEach { cls ->
                            val selected = editingClass == cls
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) CyanNeon.copy(.15f) else Color.White.copy(.05f))
                                    .border(
                                        1.dp,
                                        if (selected) CyanNeon.copy(.7f) else Color.White.copy(.1f),
                                        RoundedCornerShape(12.dp),
                                    )
                                    .clickable { editingClass = cls }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(CLASS_ICONS[cls] ?: "⚔️", fontSize = 20.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        cls,
                                        color = if (selected) CyanNeon else Color.White.copy(.5f),
                                        fontSize = 9.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Botones de acción
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (isEditing) {
                // Cancelar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(.07f))
                        .border(1.dp, Color.White.copy(.15f), RoundedCornerShape(14.dp))
                        .clickable { isEditing = false; editingName = vm.heroName; editingClass = vm.heroClass }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Cancelar", color = Color.White.copy(.5f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                // Guardar
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (saving) CyanNeon.copy(.3f) else CyanNeon.copy(.15f))
                        .border(1.dp, CyanNeon.copy(if (saving) .3f else .6f), RoundedCornerShape(14.dp))
                        .clickable(enabled = !saving) {
                            saving = true
                            vm.saveProfile(editingName, editingClass)
                            // Pequeño delay visual antes de confirmar
                            saving = false
                            saved  = true
                            isEditing = false
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (saving) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = CyanNeon, strokeWidth = 2.dp)
                    } else {
                        Text("Guardar", color = CyanNeon, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CyanNeon.copy(.1f))
                        .border(1.dp, CyanNeon.copy(.4f), RoundedCornerShape(14.dp))
                        .clickable { isEditing = true; saved = false }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("✏️  Editar Perfil", color = CyanNeon, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }

        // Confirmación de guardado
        if (saved && !isEditing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyanNeon.copy(.08f))
                    .border(1.dp, CyanNeon.copy(.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text("✓ Perfil actualizado correctamente", color = CyanNeon, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun StatChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(color.copy(.1f))
            .border(1.dp, color.copy(.4f), RoundedCornerShape(50.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StatCard(modifier: Modifier, emoji: String, label: String, value: String, color: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.Black.copy(.3f))
            .border(1.dp, color.copy(.2f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(emoji, fontSize = 20.sp)
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 13.sp)
        Text(label, color = Color.White.copy(.35f), fontSize = 9.sp)
    }
}
