package com.saudepilates.app

import com.saudepilates.app.data.repositories.AnamnesisRepository
import com.saudepilates.app.data.repositories.AttendanceRepository
import com.saudepilates.app.data.repositories.AuthRepository
import com.saudepilates.app.data.repositories.EvolutionRepository
import com.saudepilates.app.data.repositories.MessageRepository
import com.saudepilates.app.data.repositories.PaymentRepository
import com.saudepilates.app.data.repositories.PlanRepository
import com.saudepilates.app.data.repositories.ScheduleRepository
import com.saudepilates.app.data.repositories.SubscriptionRepository

object AppContainer {
    val authRepository = AuthRepository()
    val paymentRepository = PaymentRepository()
    val planRepository = PlanRepository()
    val subscriptionRepository = SubscriptionRepository()
    val scheduleRepository = ScheduleRepository()
    val attendanceRepository = AttendanceRepository()
    val evolutionRepository = EvolutionRepository()
    val anamnesisRepository = AnamnesisRepository()
    val messageRepository = MessageRepository()
}
