package com.example.mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.local.AppDatabase
import com.example.mobile.data.local.User
import com.example.mobile.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EcoLog(
    val id: Long,
    val title: String,
    val category: String,
    val carbonSavedKg: Double,
    val pointsEarned: Int,
    val timeLabel: String = "Just now"
)

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    val userState: StateFlow<User?>

    private val _ecoLogs = MutableStateFlow(
        listOf(
            EcoLog(
                id = 1L,
                title = "Recycled 12 PET bottles",
                category = "Waste",
                carbonSavedKg = 2.2,
                pointsEarned = 20,
                timeLabel = "2 hrs ago"
            )
        )
    )
    val ecoLogs: StateFlow<List<EcoLog>> = _ecoLogs.asStateFlow()

    private val _dailyChallengeCompleted = MutableStateFlow(false)
    val dailyChallengeCompleted: StateFlow<Boolean> = _dailyChallengeCompleted.asStateFlow()

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        userState = repository.userFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        viewModelScope.launch {
            repository.initializeUserIfEmpty("Eco User", 1250)
        }
    }

    fun updatePoints(newPoints: Int) {
        viewModelScope.launch {
            repository.updatePoints(newPoints.coerceAtLeast(0))
        }
    }

    fun addPoints(change: Int) {
        viewModelScope.launch {
            repository.changePoints(change)
        }
    }

    fun updateName(newName: String) {
        val cleanedName = newName.trim()
        if (cleanedName.isBlank()) return
        viewModelScope.launch {
            repository.updateName(cleanedName)
        }
    }

    fun completeDailyChallenge() {
        if (_dailyChallengeCompleted.value) return
        _dailyChallengeCompleted.value = true
        addPoints(5)
        addLog(
            title = "Avoided a plastic straw",
            category = "Waste",
            carbonSavedKg = 0.01,
            pointsEarned = 5
        )
    }

    fun logDisposal(itemName: String) {
        addPoints(5)
        addLog(
            title = "Disposed $itemName correctly",
            category = "Waste",
            carbonSavedKg = 0.35,
            pointsEarned = 5
        )
    }

    fun logActivity(title: String, category: String, carbonSavedKg: Double) {
        val points = (carbonSavedKg * 10).toInt().coerceAtLeast(1)
        addPoints(points)
        addLog(title, category, carbonSavedKg, points)
    }

    fun redeemReward(cost: Int): Boolean {
        val currentPoints = userState.value?.points ?: return false
        if (currentPoints < cost) return false
        addPoints(-cost)
        return true
    }

    private fun addLog(
        title: String,
        category: String,
        carbonSavedKg: Double,
        pointsEarned: Int
    ) {
        _ecoLogs.update { current ->
            listOf(
                EcoLog(
                    id = System.currentTimeMillis(),
                    title = title,
                    category = category,
                    carbonSavedKg = carbonSavedKg,
                    pointsEarned = pointsEarned
                )
            ) + current
        }
    }
}
