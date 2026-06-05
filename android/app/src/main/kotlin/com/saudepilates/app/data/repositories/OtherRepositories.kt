package com.saudepilates.app.data.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.saudepilates.app.data.models.AnamnesisRecord
import com.saudepilates.app.data.models.AttendanceRecord
import com.saudepilates.app.data.models.EvolutionRecord
import com.saudepilates.app.data.models.Plan
import com.saudepilates.app.data.models.ScheduledClass
import com.saudepilates.app.data.models.StudentMessage
import com.saudepilates.app.data.models.SubscriptionInfo
import com.saudepilates.app.util.DateUtils
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

class PlanRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchPlans(companyId: String): List<Plan> {
        return db.collection("plans").whereEqualTo("companyId", companyId).get().await()
            .documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                Plan(
                    id = doc.id,
                    title = data["title"] as? String ?: "",
                    price = (data["price"] as? Number)?.toDouble() ?: 0.0,
                    companyId = data["companyId"] as? String,
                    description = data["description"] as? String
                )
            }.sortedBy { it.title.lowercase() }
    }

    suspend fun savePlan(plan: Plan, companyId: String): String {
        val payload = mapOf(
            "title" to plan.title,
            "price" to plan.price,
            "description" to (plan.description ?: ""),
            "companyId" to companyId,
            "createdAt" to java.time.Instant.now().toString()
        )
        return if (plan.id.isBlank()) {
            db.collection("plans").add(payload).await().id
        } else {
            db.collection("plans").document(plan.id).set(payload).await()
            plan.id
        }
    }
}

class SubscriptionRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchSubscription(companyId: String): SubscriptionInfo {
        val ref = db.collection("subscriptions").document(companyId)
        val snapshot = ref.get().await()
        if (snapshot.exists()) {
            val data = snapshot.data!!
            return SubscriptionInfo(
                isSubscribed = data["isSubscribed"] as? Boolean ?: false,
                expirationDate = DateUtils.toDate(data["expirationDate"]),
                plan = data["plan"] as? String
            )
        }
        val trial = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 30) }.time
        val payload = mapOf(
            "isSubscribed" to true,
            "expirationDate" to java.time.Instant.now().plusSeconds(30L * 24 * 3600).toString(),
            "plan" to "trial",
            "createdAt" to java.time.Instant.now().toString(),
            "companyId" to companyId
        )
        ref.set(payload).await()
        return SubscriptionInfo(isSubscribed = true, expirationDate = trial, plan = "trial")
    }
}

class ScheduleRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchProfessorSchedule(professorId: String, startDate: Date, endDate: Date): List<ScheduledClass> {
        return db.collection("scheduledClasses").whereEqualTo("professorId", professorId).get().await()
            .documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                val date = DateUtils.toDate(data["date"]) ?: return@mapNotNull null
                if (date.before(DateUtils.startOfDay(startDate)) || date.after(endDate)) return@mapNotNull null
                ScheduledClass(
                    id = doc.id,
                    professorId = data["professorId"] as? String ?: "",
                    studentId = data["studentId"] as? String,
                    date = date,
                    startTime = data["startTime"] as? String,
                    duration = (data["duration"] as? Number)?.toInt(),
                    experimental = data["experimental"] as? Boolean,
                    experimentalStudentName = data["experimentalStudentName"] as? String,
                    studentName = data["studentName"] as? String
                )
            }.sortedWith(compareBy({ it.date }, { it.startTime ?: "" }))
    }

    suspend fun fetchStudentSchedule(studentId: String): List<ScheduledClass> {
        return db.collection("scheduledClasses").whereEqualTo("studentId", studentId).get().await()
            .documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                ScheduledClass(
                    id = doc.id,
                    professorId = data["professorId"] as? String ?: "",
                    studentId = data["studentId"] as? String,
                    date = DateUtils.toDate(data["date"]) ?: Date(),
                    startTime = data["startTime"] as? String,
                    duration = (data["duration"] as? Number)?.toInt()
                )
            }.sortedBy { it.date }
    }

    suspend fun saveScheduledClass(data: Map<String, Any?>): String {
        val payload = data.filterValues { it != null }.mapValues { it.value!! }.toMutableMap()
        if (payload["date"] is Date) payload["date"] = Timestamp(payload["date"] as Date)
        return db.collection("scheduledClasses").add(payload).await().id
    }

    suspend fun deleteScheduledClass(id: String) {
        db.collection("scheduledClasses").document(id).delete().await()
    }
}

class AttendanceRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun saveAttendance(data: Map<String, Any?>) {
        val payload = data.filterValues { it != null }.mapValues { it.value!! }.toMutableMap()
        if (payload["date"] is Date) payload["date"] = Timestamp(payload["date"] as Date)
        db.collection("attendanceRecords").add(payload).await()
    }
}

class EvolutionRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchEvolutions(studentId: String): List<EvolutionRecord> {
        return db.collection("evolutions").whereEqualTo("studentId", studentId).get().await()
            .documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                EvolutionRecord(
                    id = doc.id,
                    studentId = data["studentId"] as? String ?: "",
                    professorId = data["professorId"] as? String,
                    date = DateUtils.toDate(data["date"]) ?: Date(),
                    notes = data["notes"] as? String ?: "",
                    exercises = data["exercises"] as? String
                )
            }.sortedByDescending { it.date }
    }

    suspend fun saveEvolution(data: Map<String, Any?>) {
        val payload = data.filterValues { it != null }.mapValues { it.value!! }.toMutableMap()
        if (payload["date"] is Date) payload["date"] = Timestamp(payload["date"] as Date)
        db.collection("evolutions").add(payload).await()
    }
}

class AnamnesisRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchByStudent(studentId: String): List<AnamnesisRecord> {
        val records = db.collection("anamnesis").whereEqualTo("studentId", studentId).get().await()
            .documents.map { mapAnamnesis(it.id, it.data) }.toMutableList()
        val legacy = db.collection("anamnesis").document(studentId).get().await()
        if (legacy.exists() && records.none { it.id == legacy.id }) {
            records.add(mapAnamnesis(legacy.id, legacy.data))
        }
        return records.sortedByDescending { it.performedAt ?: "" }
    }

    suspend fun save(studentId: String, companyId: String, data: Map<String, Any?>, id: String?): String {
        val payload = data.filterValues { it != null }.mapValues { it.value!! }.toMutableMap()
        payload["studentId"] = studentId
        payload["companyId"] = companyId
        payload["updatedAt"] = java.time.Instant.now().toString()
        if (!payload.containsKey("performedAt")) payload["performedAt"] = payload["updatedAt"]!!
        return if (!id.isNullOrBlank()) {
            db.collection("anamnesis").document(id).set(payload).await()
            id
        } else {
            payload["createdAt"] = payload["updatedAt"]!!
            db.collection("anamnesis").add(payload).await().id
        }
    }

    private fun mapAnamnesis(id: String, data: Map<String, Any>?): AnamnesisRecord {
        return AnamnesisRecord(
            id = id,
            studentId = data?.get("studentId") as? String ?: id,
            performedAt = data?.get("performedAt") as? String,
            mainComplaint = data?.get("mainComplaint") as? String,
            medicalHistory = data?.get("medicalHistory") as? String,
            surgeries = data?.get("surgeries") as? String,
            medications = data?.get("medications") as? String,
            physicalActivity = data?.get("physicalActivity") as? String,
            goals = data?.get("goals") as? String,
            observations = data?.get("observations") as? String
        )
    }
}

class MessageRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchMessages(professorId: String): List<StudentMessage> {
        return db.collection("messages").whereEqualTo("professorId", professorId).get().await()
            .documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                StudentMessage(
                    id = doc.id,
                    studentId = data["studentId"] as? String ?: "",
                    professorId = data["professorId"] as? String,
                    subject = data["subject"] as? String ?: "",
                    message = data["message"] as? String ?: "",
                    isRead = data["isRead"] as? Boolean,
                    createdAt = DateUtils.toDate(data["createdAt"]),
                    studentName = data["studentName"] as? String
                )
            }.sortedByDescending { it.createdAt }
    }

    suspend fun markAsRead(id: String) {
        db.collection("messages").document(id).update("isRead", true).await()
    }
}
