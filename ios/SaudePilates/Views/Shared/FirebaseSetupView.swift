import SwiftUI

struct FirebaseSetupView: View {
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Image(systemName: "flame.fill")
                        .font(.system(size: 48))
                        .foregroundStyle(.orange)

                    Text("Firebase iOS não configurado")
                        .font(.title2.bold())

                    Text("Para usar o app, registre o app iOS no Firebase Console e configure o projeto.")
                        .foregroundStyle(.secondary)

                    Group {
                        Text("Passos:")
                            .font(.headline)

                        Text("1. Abra o Firebase Console do projeto saudepilates-170df")
                        Text("2. Adicione um app iOS com bundle ID: com.saudepilates.app")
                        Text("3. Baixe o GoogleService-Info.plist")
                        Text("4. Substitua o arquivo em ios/SaudePilates/GoogleService-Info.plist")
                        Text("5. Ou cole o GOOGLE_APP_ID em FirebaseConfiguration.swift")
                    }
                    .font(.subheadline)

                    Link("Abrir Firebase Console", destination: URL(string: "https://console.firebase.google.com/project/saudepilates-170df/settings/general")!)
                        .buttonStyle(.borderedProminent)
                        .frame(maxWidth: .infinity)
                }
                .padding()
            }
            .navigationTitle("Configuração")
        }
    }
}
