import Foundation

enum CurrencyFormatter {
    static func symbol(for language: String?) -> String {
        switch language ?? "pt" {
        case "en": return "$"
        case "es", "fr": return "€"
        default: return "R$"
        }
    }

    static func locale(for language: String?) -> Locale {
        switch language ?? "pt" {
        case "en": return Locale(identifier: "en_US")
        case "es": return Locale(identifier: "es_ES")
        case "fr": return Locale(identifier: "fr_FR")
        default: return Locale(identifier: "pt_BR")
        }
    }

    static func format(_ value: Double, language: String?) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        formatter.locale = locale(for: language)
        return formatter.string(from: NSNumber(value: value)) ?? String(format: "%.2f", value)
    }

    static func formatWithSymbol(_ value: Double, language: String?) -> String {
        "\(symbol(for: language)) \(format(value, language: language))"
    }
}
