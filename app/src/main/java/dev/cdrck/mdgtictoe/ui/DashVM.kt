package dev.cdrck.mdgtictoe.ui

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cdrck.mdgtictoe.datas.GameMod
import javax.inject.Inject

@HiltViewModel
class DashVM @Inject constructor(): ViewModel() {

    var selectedGameMode by mutableStateOf(GameMod.Computer)
        private set

    fun updateGameMode(mode: GameMod) {
        selectedGameMode = mode
    }

}