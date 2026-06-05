package com.saudepilates.app.ui.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saudepilates.app.AppContainer
import com.saudepilates.app.BuildConfig
import com.saudepilates.app.ui.theme.IosColors
import com.saudepilates.app.ui.components.IosFormScroll
import com.saudepilates.app.ui.components.IosGroup
import com.saudepilates.app.ui.components.IosLoadingOverlay
import com.saudepilates.app.ui.components.IosScreen
import com.saudepilates.app.ui.components.IosSectionHeader
import com.saudepilates.app.util.ToastManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showRegister by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val auth = AppContainer.authRepository

    if (showRegister) {
        RegisterScreen(onClose = { showRegister = false })
        return
    }

    BoxWithLoading(loading) {
        IosScreen(title = "Saúde Pilates", modifier = modifier) { padding ->
            IosFormScroll(Modifier.padding(padding)) {
                Text(
                    "v${BuildConfig.VERSION_NAME}",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    textAlign = TextAlign.Center,
                    color = IosColors.secondaryLabel
                )
                IosSectionHeader("Conta")
                IosGroup {
                    OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth().padding(12.dp), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                    OutlinedTextField(password, { password = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth().padding(12.dp), singleLine = true, visualTransformation = PasswordVisualTransformation())
                }
                IosGroup {
                    Button(
                        onClick = {
                            scope.launch {
                                loading = true
                                try { auth.login(email, password) } catch (e: Exception) { ToastManager.error(e.message ?: "Erro ao entrar") }
                                finally { loading = false }
                            }
                        },
                        enabled = email.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    ) { Text("Entrar") }
                }
                IosGroup {
                    TextButton(onClick = { showRegister = true }, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Text("Criar conta da empresa")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onClose: () -> Unit) {
    var companyName by remember { mutableStateOf("") }
    var adminName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val auth = AppContainer.authRepository

    BoxWithLoading(loading) {
        IosScreen(title = "Nova conta", onBack = onClose) { padding ->
            IosFormScroll(Modifier.padding(padding)) {
                IosSectionHeader("Empresa")
                IosGroup {
                    OutlinedTextField(companyName, { companyName = it }, label = { Text("Nome da empresa") }, modifier = Modifier.fillMaxWidth().padding(12.dp), singleLine = true)
                }
                IosSectionHeader("Administrador")
                IosGroup {
                    OutlinedTextField(adminName, { adminName = it }, label = { Text("Seu nome") }, modifier = Modifier.fillMaxWidth().padding(12.dp), singleLine = true)
                    OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth().padding(12.dp), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                    OutlinedTextField(password, { password = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth().padding(12.dp), singleLine = true, visualTransformation = PasswordVisualTransformation())
                }
                IosGroup {
                    Button(
                        onClick = {
                            scope.launch {
                                loading = true
                                try {
                                    auth.register(email, password, companyName, adminName)
                                    ToastManager.success("Conta criada com sucesso")
                                    onClose()
                                } catch (e: Exception) { ToastManager.error(e.message ?: "Erro") }
                                finally { loading = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    ) { Text("Criar conta") }
                }
            }
        }
    }
}

@Composable
private fun BoxWithLoading(loading: Boolean, content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Box {
        content()
        IosLoadingOverlay(loading)
    }
}
