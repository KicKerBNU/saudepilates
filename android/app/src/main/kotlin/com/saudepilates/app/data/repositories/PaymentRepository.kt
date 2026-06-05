package com.saudepilates.app.data.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.saudepilates.app.data.models.ProfessorPayment
import com.saudepilates.app.data.models.ProfessorPayout
import com.saudepilates.app.data.models.StudentPayment
import com.saudepilates.app.util.DateUtils
import kotlinx.coroutines.tasks.await
import java.util.Date

class PaymentRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchStudentPayments(companyId: String, studentId: String? = null): List<StudentPayment> {
        var query = db.collection("studentPayments").whereEqualTo("companyId", companyId)
        if (studentId != null) query = query.whereEqualTo("studentId", studentId)
        return query.get().await().documents.mapNotNull { mapStudentPayment(it.id, it.data) }
            .sortedByDescending { it.paymentDate }
    }

    suspend fun fetchProfessorPayments(companyId: String, professorId: String? = null): List<ProfessorPayment> {
        var query = db.collection("professorPayments").whereEqualTo("companyId", companyId)
        if (professorId != null) query = query.whereEqualTo("professorId", professorId)
        val professorPayments = query.get().await().documents.mapNotNull { mapProfessorPayment(it.id, it.data) }
        val studentPayments = fetchStudentPayments(companyId)
        return filterActiveProfessorPayments(professorPayments, studentPayments)
    }

    suspend fun fetchProfessorPayouts(companyId: String, month: Int, year: Int): List<ProfessorPayout> {
        return db.collection("professorPayouts")
            .whereEqualTo("companyId", companyId)
            .whereEqualTo("month", month)
            .whereEqualTo("year", year)
            .get().await().documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                ProfessorPayout(
                    id = doc.id,
                    professorId = data["professorId"] as? String ?: "",
                    month = (data["month"] as? Long)?.toInt() ?: month,
                    year = (data["year"] as? Long)?.toInt() ?: year,
                    totalAmount = (data["totalAmount"] as? Number)?.toDouble() ?: 0.0
                )
            }
    }

    suspend fun addStudentPayment(data: Map<String, Any?>): String {
        val payload = data.filterValues { it != null }.mapValues { it.value!! }.toMutableMap()
        if (payload["paymentDate"] is Date) {
            payload["paymentDate"] = Timestamp(payload["paymentDate"] as Date)
        }
        payload["createdAt"] = Timestamp.now()
        return db.collection("studentPayments").add(payload).await().id
    }

    suspend fun addProfessorPayment(data: Map<String, Any?>) {
        val payload = data.filterValues { it != null }.mapValues { it.value!! }.toMutableMap()
        if (payload["paymentDate"] is Date) {
            payload["paymentDate"] = Timestamp(payload["paymentDate"] as Date)
        }
        payload["createdAt"] = Timestamp.now()
        db.collection("professorPayments").add(payload).await()
    }

    suspend fun deleteStudentPayment(paymentId: String, companyId: String) {
        val paymentRef = db.collection("studentPayments").document(paymentId)
        val snapshot = paymentRef.get().await()
        val data = snapshot.data ?: throw IllegalStateException("Pagamento não encontrado")

        deleteLinkedProfessorPayments(
            studentPaymentId = paymentId,
            studentId = data["studentId"] as? String ?: "",
            paymentDate = DateUtils.toDate(data["paymentDate"]) ?: Date(),
            amount = (data["finalAmount"] as? Number)?.toDouble() ?: (data["amount"] as? Number)?.toDouble() ?: 0.0,
            createdAt = DateUtils.toDate(data["createdAt"]),
            companyId = companyId
        )
        paymentRef.delete().await()
    }

    suspend fun markProfessorPaid(professorId: String, month: Int, year: Int, totalAmount: Double, companyId: String) {
        db.collection("professorPayouts").add(
            mapOf(
                "professorId" to professorId,
                "month" to month,
                "year" to year,
                "totalAmount" to totalAmount,
                "companyId" to companyId,
                "paidAt" to Timestamp.now()
            )
        ).await()
    }

    suspend fun unmarkProfessorPaid(payoutId: String) {
        db.collection("professorPayouts").document(payoutId).delete().await()
    }

    private suspend fun deleteLinkedProfessorPayments(
        studentPaymentId: String,
        studentId: String,
        paymentDate: Date,
        amount: Double,
        createdAt: Date?,
        companyId: String
    ) {
        val snapshot = db.collection("professorPayments")
            .whereEqualTo("companyId", companyId)
            .whereEqualTo("studentId", studentId)
            .get().await()

        var candidates = snapshot.documents
        val byId = candidates.filter { it.data?.get("studentPaymentId") == studentPaymentId }
        candidates = if (byId.isNotEmpty()) byId else candidates.filter { doc ->
            val data = doc.data ?: return@filter false
            data["studentPaymentId"] == null &&
                DateUtils.isSameDay(DateUtils.toDate(data["paymentDate"]) ?: Date(), paymentDate) &&
                (data["originalStudentPayment"] as? Number)?.toDouble() == amount
        }

        if (candidates.isEmpty()) return

        val target = createdAt?.time ?: 0L
        val best = candidates.minByOrNull { doc ->
            val created = DateUtils.toDate(doc.data?.get("createdAt"))?.time ?: 0L
            kotlin.math.abs(created - target)
        } ?: return

        db.collection("professorPayments").document(best.id).delete().await()
    }

    private fun filterActiveProfessorPayments(
        professorPayments: List<ProfessorPayment>,
        studentPayments: List<StudentPayment>
    ): List<ProfessorPayment> {
        val available = studentPayments.sortedBy { it.paymentDate }.toMutableList()
        val active = mutableListOf<ProfessorPayment>()

        professorPayments.sortedBy { it.paymentDate }.forEach { payment ->
            val index = if (payment.studentPaymentId != null) {
                available.indexOfFirst { it.id == payment.studentPaymentId }
            } else {
                available.indexOfFirst { student ->
                    student.studentId == payment.studentId &&
                        DateUtils.isSameDay(student.paymentDate, payment.paymentDate) &&
                        student.paidAmount == payment.originalStudentPayment
                }
            }
            if (index >= 0) {
                active.add(payment)
                available.removeAt(index)
            }
        }
        return active
    }

    private fun mapStudentPayment(id: String, data: Map<String, Any>?): StudentPayment? {
        if (data == null) return null
        val paymentDate = DateUtils.toDate(data["paymentDate"]) ?: return null
        return StudentPayment(
            id = id,
            studentId = data["studentId"] as? String ?: "",
            professorId = data["professorId"] as? String,
            planId = data["planId"] as? String,
            companyId = data["companyId"] as? String,
            amount = (data["amount"] as? Number)?.toDouble(),
            finalAmount = (data["finalAmount"] as? Number)?.toDouble(),
            originalAmount = (data["originalAmount"] as? Number)?.toDouble(),
            discountPercent = (data["discountPercent"] as? Number)?.toDouble(),
            discountAmount = (data["discountAmount"] as? Number)?.toDouble(),
            commissionPercent = (data["commissionPercent"] as? Number)?.toDouble(),
            commissionAmount = (data["commissionAmount"] as? Number)?.toDouble(),
            paymentDate = paymentDate,
            paymentPeriod = data["paymentPeriod"] as? String,
            periodMonths = (data["periodMonths"] as? Number)?.toInt()
        )
    }

    private fun mapProfessorPayment(id: String, data: Map<String, Any>?): ProfessorPayment? {
        if (data == null) return null
        val paymentDate = DateUtils.toDate(data["paymentDate"]) ?: return null
        return ProfessorPayment(
            id = id,
            professorId = data["professorId"] as? String ?: "",
            studentId = data["studentId"] as? String ?: "",
            studentPaymentId = data["studentPaymentId"] as? String,
            amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
            commissionPercent = (data["commissionPercent"] as? Number)?.toDouble(),
            originalStudentPayment = (data["originalStudentPayment"] as? Number)?.toDouble(),
            paymentDate = paymentDate,
            notes = data["notes"] as? String
        )
    }
}
