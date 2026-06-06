import SwiftUI

enum BrandColors {
    static let indigo = Color(red: 79 / 255, green: 70 / 255, blue: 229 / 255)
}

struct AuthBrandHeader: View {
    var compact: Bool = false

    var body: some View {
        VStack(spacing: compact ? 10 : 14) {
            Image("BrandLogo")
                .resizable()
                .scaledToFit()
                .frame(width: compact ? 64 : 88, height: compact ? 64 : 88)
                .clipShape(RoundedRectangle(cornerRadius: compact ? 14 : 20, style: .continuous))
                .shadow(color: .black.opacity(0.12), radius: 8, y: 4)

            VStack(spacing: 4) {
                Text("Saúde Pilates")
                    .font(compact ? .title3.weight(.bold) : .title.weight(.bold))
                    .foregroundStyle(.primary)

                if !compact {
                    Text("Gestão para estúdios de Pilates")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                        .multilineTextAlignment(.center)
                }
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.top, compact ? 8 : 16)
    }
}

struct AuthGroupedSection<Content: View>: View {
    let title: String
    @ViewBuilder var content: Content

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title.uppercased())
                .font(.footnote.weight(.semibold))
                .foregroundStyle(.secondary)
                .padding(.horizontal, 4)

            VStack(spacing: 0) {
                content
            }
            .background(Color(uiColor: .secondarySystemGroupedBackground))
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
        }
    }
}

struct AuthFieldDivider: View {
    var body: some View {
        Divider()
            .padding(.leading, 16)
    }
}
