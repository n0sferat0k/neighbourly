package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.neighbourly.app.a_device.ui.atomic.molecule.card.OkCardFooter
import com.neighbourly.app.a_device.ui.atomic.organism.info.OrganismAppDownloadLink
import com.neighbourly.app.a_device.ui.atomic.organism.info.OrganismAppInfo
import com.neighbourly.app.a_device.ui.atomic.organism.info.OrganismErrorLog
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismContentBubble
import com.neighbourly.app.b_adapt.viewmodel.BackendInfoViewModel.BackendInfoViewState


val backendInfoTemplate = TemplateNode(
    id = "OrganismContentBubble",
    params = mapOf("scrollable" to "true"),
    children = listOf(
        TemplateNode(
            id = "Column", children = listOf(
                TemplateNode(id = "OrganismAppInfo"),
                TemplateNode(id = "OrganismAppDownloadLink"),
                TemplateNode(id = "OrganismErrorLog")
            )
        ),
        TemplateNode(id = "CloseCardFooter")
    )
)

data class TemplateNode(
    val id: String,
    val params: Map<String, String?> = emptyMap(),
    val children: List<TemplateNode> = emptyList()
)

typealias TemplateCallback = (name: String, params: Map<String, String?>) -> Unit
typealias TemplateArguments = Map<String, String?>

@Composable
fun RenderTemplate(
    template: TemplateNode?,
    arguments: TemplateArguments,
    callback: TemplateCallback
) {
    template?.let {
        when (template.id) {
            "Column" -> Column {
                template.children.forEach { RenderTemplate(it, arguments, callback) }
            }

            "Row" -> Row {
                template.children.forEach { RenderTemplate(it, arguments, callback) }
            }

            "OrganismContentBubble" -> OrganismContentBubble(
                scrollable = template.params["scrollable"].toBoolean(),
                content = { RenderTemplate(template.children[0], arguments, callback) },
                footerContent = { RenderTemplate(template.children[1], arguments, callback) }
            )

            "OrganismAppInfo" -> OrganismAppInfo(
                arguments["appVersion"].orEmpty(),
                arguments["isDebug"].toBoolean()
            )

            "OrganismAppDownloadLink" -> OrganismAppDownloadLink(
                arguments["appVersion"].orEmpty(),
            )

            "OrganismErrorLog" -> OrganismErrorLog(arguments["lastError"])

            "CloseCardFooter" -> OkCardFooter(onOk = { callback("onClose", emptyMap()) })
        }
    }
}

@Composable
fun BackendInfoTemplate(
    state: BackendInfoViewState,
    onClose: () -> Unit
) {
    RenderTemplate(
        backendInfoTemplate,
        mapOf(
            "appVersion" to state.appVersion,
            "isDebug" to state.isDebug.toString(),
            "lastError" to state.lastError
        ),
        { command, params ->
            when (command) {
                "onClose" -> onClose()
            }
        }
    )
}

//@Composable
//fun BackendInfoTemplate(
//    state: BackendInfoViewState,
//    onClose: () -> Unit
//) {
//    OrganismContentBubble(
//        scrollable = false,
//        content = {
//            Column {
//                OrganismAppInfo(state.appVersion, state.isDebug)
//                OrganismAppDownloadLink(state.appVersion)
//                OrganismErrorLog(state.lastError)
//            }
//        },
//        footerContent = {
//            CloseCardFooter { onClose() }
//        }
//    )
//}