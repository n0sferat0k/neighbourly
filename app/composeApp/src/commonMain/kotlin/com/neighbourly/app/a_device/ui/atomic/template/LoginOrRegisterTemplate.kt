package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.runtime.Composable
import com.neighbourly.app.a_device.ui.atomic.molecule.card.LoginOrRegisterCardFooter
import com.neighbourly.app.a_device.ui.atomic.organism.auth.OrganismForgotForm
import com.neighbourly.app.a_device.ui.atomic.organism.auth.OrganismLoginForm
import com.neighbourly.app.a_device.ui.atomic.organism.auth.OrganismRegisterForm
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismContentBubble
import com.neighbourly.app.b_adapt.viewmodel.auth.LoginRegisterViewModel

@Composable
fun LoginOrRegisterTemplate(
    state: LoginRegisterViewModel.LoginRegisterViewState,
    contentIndex: Int,
    onLogin: (username: String, password: String, remember: Boolean) -> Unit,
    onReset: (email: String) -> Unit,
    onGoToLogin: () -> Unit,
    onGoToRegister: () -> Unit,
    onGoToForgot: () -> Unit,
    onRegister: (
        username: String,
        password: String,
        confirmPassword: String,
        fullName: String,
        email: String,
        phoneNumber: String,
        profileFile: String?,
        remember: Boolean
    ) -> Unit
) {

    OrganismContentBubble(
        scrollable = true,
        content = {
            when (contentIndex) {
                0 -> OrganismLoginForm(
                    username = state.rememberedUsername,
                    password = state.rememberedPassword,
                    loading = state.loading,
                    error = state.error,
                    onLogin = onLogin,
                    onForgot = onGoToForgot,
                )

                1 -> OrganismRegisterForm(
                    loading = state.loading,
                    error = state.error,
                    onRegister = onRegister,
                )

                2 -> OrganismForgotForm(
                    onReset = onReset,
                    resetComplete = state.resetComplete,
                    loading = state.loading,
                    error = state.error,
                )
            }
        },
        footerContent = {
            LoginOrRegisterCardFooter(
                onSelectLogin = onGoToLogin,
                onSelectRegister = onGoToRegister
            )
        }
    )
}
