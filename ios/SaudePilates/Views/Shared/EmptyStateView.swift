import SwiftUI

enum EmptyIllustrationStyle {
    case scheduleSelectProfessor
    case scheduleNoClasses
    case studentsActive
    case studentsInactive
    case professorsActive
    case professorsInactive
    case plans
    case paymentSelectStudent
    case paymentVisualization
}

struct EmptyIllustration: View {
    let style: EmptyIllustrationStyle

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 32, style: .continuous)
                .fill(
                    LinearGradient(
                        colors: [
                            BrandColors.indigo.opacity(0.18),
                            BrandColors.indigo.opacity(0.06)
                        ],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
                .frame(width: 168, height: 168)

            VStack(spacing: 10) {
                Image(systemName: primarySymbol)
                    .font(.system(size: 56, weight: .light))
                    .foregroundStyle(BrandColors.indigo)
                    .symbolRenderingMode(.hierarchical)

                Image(systemName: secondarySymbol)
                    .font(.system(size: 22, weight: .medium))
                    .foregroundStyle(BrandColors.indigo.opacity(0.55))
            }
        }
        .accessibilityHidden(true)
    }

    private var primarySymbol: String {
        switch style {
        case .scheduleSelectProfessor: return "person.crop.circle.badge.plus"
        case .scheduleNoClasses: return "calendar.badge.clock"
        case .studentsActive: return "person.3.fill"
        case .studentsInactive: return "person.crop.circle.badge.xmark"
        case .professorsActive: return "person.crop.circle.badge.checkmark"
        case .professorsInactive: return "person.crop.circle.badge.minus"
        case .plans: return "list.bullet.rectangle.portrait.fill"
        case .paymentSelectStudent: return "person.crop.circle.badge.plus"
        case .paymentVisualization: return "chart.bar.fill"
        }
    }

    private var secondarySymbol: String {
        switch style {
        case .scheduleSelectProfessor: return "calendar"
        case .scheduleNoClasses: return "figure.mind.and.body"
        case .studentsActive: return "figure.walk"
        case .studentsInactive: return "archivebox"
        case .professorsActive: return "figure.mind.and.body"
        case .professorsInactive: return "archivebox"
        case .plans: return "creditcard"
        case .paymentSelectStudent: return "brazilianrealsign.circle"
        case .paymentVisualization: return "calendar"
        }
    }
}

struct EmptyStateView: View {
    let title: String
    var message: String? = nil
    var systemImage: String? = nil
    var illustrationStyle: EmptyIllustrationStyle? = nil
    var actionTitle: String? = nil
    var action: (() -> Void)? = nil

    var body: some View {
        VStack(spacing: 20) {
            if let illustrationStyle {
                EmptyIllustration(style: illustrationStyle)
            } else if let systemImage {
                Image(systemName: systemImage)
                    .font(.system(size: 44, weight: .light))
                    .foregroundStyle(BrandColors.indigo.opacity(0.7))
                    .symbolRenderingMode(.hierarchical)
                    .frame(width: 120, height: 120)
                    .background(BrandColors.indigo.opacity(0.08))
                    .clipShape(RoundedRectangle(cornerRadius: 28, style: .continuous))
            }

            VStack(spacing: 8) {
                Text(title)
                    .font(.title3.weight(.semibold))
                    .multilineTextAlignment(.center)

                if let message {
                    Text(message)
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 28)
                }
            }

            if let actionTitle, let action {
                Button(actionTitle, action: action)
                    .buttonStyle(.borderedProminent)
                    .tint(BrandColors.indigo)
                    .padding(.top, 4)
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.horizontal, 16)
        .padding(.vertical, 20)
    }
}
