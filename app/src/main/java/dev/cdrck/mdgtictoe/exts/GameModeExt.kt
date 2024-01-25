package dev.cdrck.mdgtictoe.exts

import dev.cdrck.mdgtictoe.datas.GameMod

val GameMod.modeName: String
	get() = when(this) {
		GameMod.Computer -> "Computer"
		GameMod.PvP -> "Two Players"
	}
