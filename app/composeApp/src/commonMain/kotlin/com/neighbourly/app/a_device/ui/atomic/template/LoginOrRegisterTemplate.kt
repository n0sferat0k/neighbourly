package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.neighbourly.app.a_device.ui.atomic.molecule.card.LoginOrRegisterCardFooter
import com.neighbourly.app.a_device.ui.atomic.organism.auth.OrganismLoginForm
import com.neighbourly.app.a_device.ui.atomic.organism.auth.OrganismRegisterForm
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismContentBubble
import com.neighbourly.app.b_adapt.viewmodel.auth.LoginViewModel
import com.neighbourly.app.b_adapt.viewmodel.auth.RegisterViewModel

@Composable
fun LoginOrRegisterTemplate(
    loginState: LoginViewModel.LoginViewState,
    registerState: RegisterViewModel.RegisterViewState,
    loginUsername: (user: String) -> Unit,
    loginPassword: (pass: String) -> Unit,
    onLogin: (remember: Boolean) -> Unit,
    registerUsername: (user: String) -> Unit,
    registerFullname: (name: String) -> Unit,
    registerEmail: (email: String) -> Unit,
    registerPhone: (phone: String) -> Unit,
    registerPassword: (pass: String, confPass: String) -> Unit,
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
    var index by remember { mutableStateOf(0) }

    OrganismContentBubble(
        scrollable = true,
        content = {
            when (index) {
                0 -> OrganismLoginForm(
                    state = loginState,
                    updateUsername = loginUsername,
                    updatePassword = loginPassword,
                    onLogin = onLogin,
                )

                1 -> OrganismRegisterForm(
                    state = registerState,
                    updateUsername = registerUsername,
                    updateFullname = registerFullname,
                    updateEmail = registerEmail,
                    updatePhone = registerPhone,
                    updatePassword = registerPassword,
                    onRegister = onRegister,
                )
            }
        },
        footerContent = {
            LoginOrRegisterCardFooter(onSelectLogin = {
                index = 0
            }, onSelectRegister = {
                index = 1
            })
        }
    )
}
