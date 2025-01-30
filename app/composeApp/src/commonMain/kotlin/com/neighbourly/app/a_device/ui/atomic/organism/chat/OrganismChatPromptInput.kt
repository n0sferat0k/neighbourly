package com.neighbourly.app.a_device.ui.atomic.organism.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.AppColors
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.ai_prompt
import neighbourly.composeapp.generated.resources.send
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismChatPromptInput(
    onPrompt: (prompt: String) -> Unit
) {
    var prompt by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = prompt,
            onValueChange = {
                prompt = it
            },
            label = { Text(stringResource(Res.string.ai_prompt)) },
            modifier = Modifier.weight(1f),
        )

        Image(
            modifier = Modifier.size(48.dp).padding(4.dp).clickable {
                if(prompt.isNotEmpty())  {
                    onPrompt(prompt)
                    prompt = ""
                }
            },
            painter = painterResource(Res.drawable.send),
            contentDescription = "Send Button Image",
            colorFilter = ColorFilter.tint(AppColors.primary),
        )
    }
}