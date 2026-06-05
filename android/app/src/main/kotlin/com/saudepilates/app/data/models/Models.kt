package com.saudepilates.app.data.models

import java.util.Date

enum class UserRole { ADMIN, PROFESSOR, STUDENT }

data class UserProfile(
    val id: String,
    val email: String,
    val role: UserRole,
    val companyId: String,
    val name: String,
    val phone: String? = null,
    val planId: String? = null,
    val professorId: String? = null,
    val isActive: Boolean? = true,
    val deactivatedAt: String? = null,
    val createdAt: String? = null
) {
    val displayName: String get() = name.ifBlank { email }
    val isUserActive: Boolean get() = isActive != false
}

data class Company(
    val id: String,
    val name: String,
    val language: String? = "pt",
    val ownerId: String? = null
)

data class Plan(
    val id: String,
    val title: String,
    val price: Double,
    val companyId: String? = null,
    val description: String? = null
)

data class StudentPayment(
    val id: String,
    val studentId: String,
    val professorId: String? = null,
    val planId: String? = null,
    val companyId: String? = null,
    val amount: Double? = null,
    val finalAmount: Double? = null,
    val originalAmount: Double? = null,
    val discountPercent: Double? = null,
    val discountAmount: Double? = null,
    val commissionPercent: Double? = null,
    val commissionAmount: Double? = null,
    val paymentDate: Date,
    val paymentPeriod: String? = null,
    val periodMonths: Int? = null
) {
    val paidAmount: Double get() = finalAmount ?: amount ?: 0.0
}

data class ProfessorPayment(
    val id: String,
    val professorId: String,
    val studentId: String,
    val studentPaymentId: String? = null,
    val amount: Double,
    val commissionPercent: Double? = null,
    val originalStudentPayment: Double? = null,
    val paymentDate: Date,
    val studentName: String? = null,
    val notes: String? = null
)

data class ProfessorPayout(
    val id: String,
    val professorId: String,
    val month: Int,
    val year: Int,
    val totalAmount: Double
)

data class ScheduledClass(
    val id: String,
    val professorId: String,
    val studentId: String? = null,
    val date: Date,
    val startTime: String? = null,
    val duration: Int? = null,
    val experimental: Boolean? = false,
    val experimentalStudentName: String? = null,
    val studentName: String? = null
)

data class AttendanceRecord(
    val id: String,
    val studentId: String,
    val professorId: String,
    val date: Date,
    val present: Boolean,
    val notes: String? = null
)

data class EvolutionRecord(
    val id: String,
    val studentId: String,
    val professorId: String? = null,
    val date: Date,
    val notes: String,
    val exercises: String? = null
)

data class AnamnesisRecord(
    val id: String,
    val studentId: String,
    val performedAt: String? = null,
    val mainComplaint: String? = null,
    val medicalHistory: String? = null,
    val surgeries: String? = null,
    val medications: String? = null,
    val physicalActivity: String? = null,
    val goals: String? = null,
    val observations: String? = null
)

data class StudentMessage(
    val id: String,
    val studentId: String,
    val professorId: String? = null,
    val subject: String,
    val message: String,
    val isRead: Boolean? = false,
    val createdAt: Date? = null,
    val studentName: String? = null
)

data class SubscriptionInfo(
    val isSubscribed: Boolean,
    val expirationDate: Date? = null,
    val plan: String? = null
) {
    val isValid: Boolean
        get() = isSubscribed && (expirationDate?.after(Date()) == true)
}

data class DashboardStats(
    val totalStudents: Int = 0,
    val totalProfessors: Int = 0,
    val monthlyRevenue: Double = 0.0,
    val totalPlans: Int = 0
)

object RotationValue {
    const val ROTATION = "rotation"
}
