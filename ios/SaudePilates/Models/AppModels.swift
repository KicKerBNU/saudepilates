import Foundation
import FirebaseFirestore

enum UserRole: String, Codable, CaseIterable {
    case admin
    case professor
    case student
}

struct UserProfile: Identifiable, Codable, Hashable {
    var id: String
    var email: String
    var role: UserRole
    var companyId: String
    var name: String
    var phone: String?
    var planId: String?
    var professorId: String?
    var isActive: Bool?
    var deactivatedAt: String?
    var createdAt: String?

    var displayName: String {
        name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? email : name
    }

    var isUserActive: Bool { isActive != false }
}

struct Company: Identifiable, Codable, Hashable {
    var id: String
    var name: String
    var language: String?
    var ownerId: String?
    var createdAt: String?
}

struct Plan: Identifiable, Codable, Hashable {
    var id: String
    var title: String
    var price: Double
    var companyId: String?
    var description: String?
    var createdAt: String?
}

struct StudentPayment: Identifiable, Codable, Hashable {
    var id: String
    var studentId: String
    var professorId: String?
    var planId: String?
    var companyId: String?
    var amount: Double?
    var finalAmount: Double?
    var originalAmount: Double?
    var discountPercent: Double?
    var discountAmount: Double?
    var commissionPercent: Double?
    var commissionAmount: Double?
    var paymentDate: Date
    var paymentPeriod: String?
    var period: String?
    var periodMonths: Int?
    var notes: String?
    var createdAt: Date?

    var paidAmount: Double { finalAmount ?? amount ?? 0 }
}

struct ProfessorPayment: Identifiable, Codable, Hashable {
    var id: String
    var professorId: String
    var studentId: String
    var studentPaymentId: String?
    var planId: String?
    var companyId: String?
    var amount: Double
    var commissionPercent: Double?
    var originalStudentPayment: Double?
    var paymentDate: Date
    var notes: String?
    var createdAt: Date?
    var studentName: String?
}

struct ProfessorPayout: Identifiable, Codable, Hashable {
    var id: String
    var professorId: String
    var companyId: String?
    var month: Int
    var year: Int
    var totalAmount: Double
    var paidAt: Date?
}

struct ScheduledClass: Identifiable, Codable, Hashable {
    var id: String
    var professorId: String
    var studentId: String?
    var companyId: String?
    var date: Date
    var startTime: String?
    var duration: Int?
    var experimental: Bool?
    var experimentalStudentName: String?
    var studentName: String?
    var notes: String?
}

struct AttendanceRecord: Identifiable, Codable, Hashable {
    var id: String
    var studentId: String
    var professorId: String
    var companyId: String?
    var date: Date
    var present: Bool
    var notes: String?
}

struct EvolutionRecord: Identifiable, Codable, Hashable {
    var id: String
    var studentId: String
    var professorId: String?
    var companyId: String?
    var date: Date
    var notes: String
    var exercises: String?
}

struct AnamnesisRecord: Identifiable, Codable, Hashable {
    var id: String
    var studentId: String
    var companyId: String?
    var performedAt: String?
    var updatedAt: String?
    var createdAt: String?
    var studentName: String?

    // Clinical fields (mirrors web form)
    var mainComplaint: String?
    var medicalHistory: String?
    var surgeries: String?
    var medications: String?
    var physicalActivity: String?
    var goals: String?
    var observations: String?
}

struct StudentMessage: Identifiable, Codable, Hashable {
    var id: String
    var studentId: String
    var professorId: String?
    var companyId: String?
    var subject: String
    var message: String
    var isRead: Bool?
    var createdAt: Date?
    var studentName: String?
}

struct SubscriptionInfo: Codable {
    var isSubscribed: Bool
    var expirationDate: Date?
    var plan: String?

    var isValid: Bool {
        guard isSubscribed, let expirationDate else { return false }
        return expirationDate > Date()
    }
}

struct DashboardStats {
    var totalStudents: Int = 0
    var totalProfessors: Int = 0
    var monthlyRevenue: Double = 0
    var totalPlans: Int = 0
}

enum AppRoute: Hashable {
    case students
    case professors
    case plans
    case paymentsHub
    case paymentRegistration
    case monthlyPayments
    case paymentVisualization
    case professorPayments
    case schedule
    case anamnesis
    case settings
    case subscription
    case studentPaymentHistory(UserProfile)
    case studentForm(UserProfile?)
    case professorForm(UserProfile?)
    case planForm(Plan?)
    case professorStudents
    case professorEarnings
    case professorAttendance
    case professorEvolution
    case professorSchedule
    case professorMessages
    case professorAnamnesis
}

extension DocumentSnapshot {
    func dateValue(_ key: String) -> Date? {
        FirestoreDate.decode(data()?[key])
    }
}

enum FirestoreDate {
    static func decode(_ value: Any?) -> Date? {
        if let timestamp = value as? Timestamp { return timestamp.dateValue() }
        if let date = value as? Date { return date }
        if let string = value as? String {
            let formatter = ISO8601DateFormatter()
            formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
            if let date = formatter.date(from: string) { return date }
            formatter.formatOptions = [.withInternetDateTime]
            return formatter.date(from: string)
        }
        return nil
    }

    static func encode(_ date: Date) -> Timestamp {
        Timestamp(date: date)
    }
}

enum RotationValue {
    static let rotation = "rotation"
}
