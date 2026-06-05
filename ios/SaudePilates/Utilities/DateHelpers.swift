import Foundation

enum DateHelpers {
    static let monthNames = [
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    ]

    static func monthYear(_ date: Date) -> String {
        let calendar = Calendar.current
        let month = calendar.component(.month, from: date) - 1
        let year = calendar.component(.year, from: date)
        return "\(monthNames[month]) \(year)"
    }

    static func shortDate(_ date: Date, locale: Locale = Locale(identifier: "pt_BR")) -> String {
        date.formatted(.dateTime.day().month(.twoDigits).year().locale(locale))
    }

    static func isSameDay(_ lhs: Date, _ rhs: Date) -> Bool {
        Calendar.current.isDate(lhs, inSameDayAs: rhs)
    }

    static func startOfMonth(_ date: Date) -> Date {
        let calendar = Calendar.current
        let components = calendar.dateComponents([.year, .month], from: date)
        return calendar.date(from: components) ?? date
    }

    static func endOfMonth(_ date: Date) -> Date {
        let calendar = Calendar.current
        guard let start = calendar.date(from: calendar.dateComponents([.year, .month], from: date)),
              let next = calendar.date(byAdding: .month, value: 1, to: start),
              let end = calendar.date(byAdding: .day, value: -1, to: next) else {
            return date
        }
        return end
    }
}
