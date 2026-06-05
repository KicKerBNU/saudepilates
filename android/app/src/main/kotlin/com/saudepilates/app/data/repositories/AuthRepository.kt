package com.saudepilates.app.data.repositories

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.saudepilates.app.data.models.Company
import com.saudepilates.app.data.models.UserProfile
import com.saudepilates.app.data.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AuthRepository {
    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _company = MutableStateFlow<Company?>(null)
    val company: StateFlow<Company?> = _company.asStateFlow()

    val isAuthenticated: Boolean get() = auth.currentUser != null
    val userId: String? get() = auth.currentUser?.uid
    val companyId: String? get() = _userProfile.value?.companyId

    val isAdmin get() = _userProfile.value?.role == UserRole.ADMIN
    val isProfessor get() = _userProfile.value?.role == UserRole.PROFESSOR
    val isStudent get() = _userProfile.value?.role == UserRole.STUDENT

    init {
        auth.addAuthStateListener { firebaseUser ->
            if (firebaseUser == null) {
                _userProfile.value = null
                _company.value = null
            }
        }
    }

    suspend fun loadCurrentUser() {
        val uid = auth.currentUser?.uid ?: return
        fetchUserProfile(uid)
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
        fetchUserProfile(auth.currentUser!!.uid)
    }

    suspend fun register(email: String, password: String, companyName: String, adminName: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user!!.uid

        val companyRef = db.collection("companies").add(
            mapOf(
                "name" to companyName,
                "language" to "pt",
                "createdAt" to java.time.Instant.now().toString(),
                "ownerId" to uid
            )
        ).await()

        db.collection("users").document(uid).set(
            mapOf(
                "email" to email,
                "role" to UserRole.ADMIN.name.lowercase(),
                "companyId" to companyRef.id,
                "name" to adminName,
                "isActive" to true,
                "createdAt" to java.time.Instant.now().toString()
            )
        ).await()

        fetchUserProfile(uid)
    }

    fun logout() {
        auth.signOut()
        _userProfile.value = null
        _company.value = null
    }

    suspend fun fetchUserProfile(userId: String) {
        val snapshot = db.collection("users").document(userId).get().await()
        if (!snapshot.exists()) return

        val data = snapshot.data ?: return
        val role = when ((data["role"] as? String)?.lowercase()) {
            "admin" -> UserRole.ADMIN
            "professor" -> UserRole.PROFESSOR
            else -> UserRole.STUDENT
        }
        val name = (data["name"] as? String)
            ?: listOf(data["firstName"], data["lastName"]).filterIsInstance<String>().joinToString(" ").trim()
            .ifBlank { data["email"] as? String ?: "Usuário" }

        _userProfile.value = UserProfile(
            id = snapshot.id,
            email = data["email"] as? String ?: "",
            role = role,
            companyId = data["companyId"] as? String ?: "",
            name = name,
            phone = data["phone"] as? String,
            planId = data["planId"] as? String,
            professorId = data["professorId"] as? String,
            isActive = data["isActive"] as? Boolean,
            deactivatedAt = data["deactivatedAt"] as? String,
            createdAt = data["createdAt"] as? String
        )

        _userProfile.value?.companyId?.let { fetchCompany(it) }
    }

    suspend fun fetchCompany(companyId: String) {
        val snapshot = db.collection("companies").document(companyId).get().await()
        if (!snapshot.exists()) return
        val data = snapshot.data ?: return
        _company.value = Company(
            id = snapshot.id,
            name = data["name"] as? String ?: "",
            language = data["language"] as? String,
            ownerId = data["ownerId"] as? String
        )
    }

    suspend fun getUsersByCompany(role: UserRole? = null): List<UserProfile> {
        val companyId = companyId ?: return emptyList()
        var query = db.collection("users").whereEqualTo("companyId", companyId)
        if (role != null) {
            query = query.whereEqualTo("role", role.name.lowercase())
        }
        return query.get().await().documents.mapNotNull { mapUser(it.id, it.data) }
    }

    suspend fun createUserForCompany(
        email: String,
        password: String,
        role: UserRole,
        userData: Map<String, Any?>
    ) {
        if (!isAdmin) throw IllegalStateException("Ação não autorizada")
        val currentCompanyId = companyId ?: throw IllegalStateException("Empresa não encontrada")

        val secondaryName = "secondary-${UUID.randomUUID()}"
        val options = FirebaseApp.getInstance().options
        val secondaryApp = try {
            FirebaseApp.getInstance(secondaryName)
        } catch (_: IllegalStateException) {
            FirebaseApp.initializeApp(
                FirebaseApp.getInstance().applicationContext,
                options,
                secondaryName
            )
        }
        val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)
        val created = secondaryAuth.createUserWithEmailAndPassword(email, password).await()

        val payload = userData.filterValues { it != null }.mapValues { it.value!! }.toMutableMap()
        payload["email"] = email
        payload["role"] = role.name.lowercase()
        payload["companyId"] = currentCompanyId
        payload["isActive"] = true
        payload["createdAt"] = java.time.Instant.now().toString()

        db.collection("users").document(created.user!!.uid).set(payload).await()
        secondaryAuth.signOut()
    }

    suspend fun updateUser(userId: String, data: Map<String, Any?>) {
        val payload = data.filterValues { it != null }.mapValues { it.value!! }
        db.collection("users").document(userId).update(payload).await()
    }

    suspend fun deactivateUser(userId: String) {
        db.collection("users").document(userId).update(
            mapOf(
                "isActive" to false,
                "deactivatedAt" to java.time.Instant.now().toString()
            )
        ).await()
    }

    suspend fun reactivateUser(userId: String) {
        db.collection("users").document(userId).update(
            mapOf(
                "isActive" to true,
                "deactivatedAt" to FieldValue.delete()
            )
        ).await()
    }

    suspend fun updateCompany(companyId: String, name: String, language: String) {
        db.collection("companies").document(companyId).update(
            mapOf("name" to name, "language" to language)
        ).await()
        fetchCompany(companyId)
    }

    private fun mapUser(id: String, data: Map<String, Any>?): UserProfile? {
        if (data == null) return null
        val role = when ((data["role"] as? String)?.lowercase()) {
            "admin" -> UserRole.ADMIN
            "professor" -> UserRole.PROFESSOR
            else -> UserRole.STUDENT
        }
        val name = (data["name"] as? String)
            ?: listOf(data["firstName"], data["lastName"]).filterIsInstance<String>().joinToString(" ").trim()
            .ifBlank { data["email"] as? String ?: "Usuário" }
        return UserProfile(
            id = id,
            email = data["email"] as? String ?: "",
            role = role,
            companyId = data["companyId"] as? String ?: "",
            name = name,
            phone = data["phone"] as? String,
            planId = data["planId"] as? String,
            professorId = data["professorId"] as? String,
            isActive = data["isActive"] as? Boolean,
            deactivatedAt = data["deactivatedAt"] as? String
        )
    }
}
