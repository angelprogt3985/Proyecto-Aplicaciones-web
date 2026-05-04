package com.mindguardians.data

data class BattleRecord(
    val id         : String = "",
    val habitType  : String = "",
    val result     : String = "",
    val goldEarned : Int    = 0,
    val xpEarned   : Int    = 0,
    val date       : String = "",
    val timestamp  : Long   = System.currentTimeMillis(),
)