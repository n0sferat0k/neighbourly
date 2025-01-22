package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.neighbourly.app.a_device.ui.atomic.molecule.card.OkCardFooter
import com.neighbourly.app.a_device.ui.atomic.organism.info.OrganismAppDownloadLink
import com.neighbourly.app.a_device.ui.atomic.organism.info.OrganismAppInfo
import com.neighbourly.app.a_device.ui.atomic.organism.info.OrganismErrorLog
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismContentBubble
import com.neighbourly.app.b_adapt.viewmodel.AppStateInfoViewModel.AppStateInfoViewState
import kotlinx.serialization.Serializable


val backendInfoTemplate =
    """
    {
      "id": "OrganismContentBubble",
      "params": {
        "scrollable": "true"
      },
      "children": {
        "content": {
          "id": "Column",
          "params": {},
          "children": {
            "1": {
              "id": "OrganismAppInfo",
              "params": {
                "appVersion": "1.0.4",
                "isDebug": "true"
              },
              "children": {}
            },
            "2": {
              "id": "OrganismAppDownloadLink",
              "params": {
                "appVersion": "1.0.4"
              },
              "children": {}
            },
            "3": {
              "id": "OrganismErrorLog",
              "params": {
                "lastError": "This is a fake error message."
              },
              "children": {}
            }
          }
        },
        "footerContent": {
          "id": "OkCardFooter",
          "params": {},
          "children": {}
        }
      }
    }    
    """.trimIndent()

@Serializable
data class TemplateNode(
    val id: String,
    val params: Map<String, String?> = emptyMap(),
    val children: Map<String, TemplateNode> = emptyMap()
)

typealias TemplateCallback = (name: String, params: Map<String, String?>) -> Unit

@Composable
fun RenderTemplate(
    template: TemplateNode?,
    callback: TemplateCallback
) {
    template?.let {
        when (template.id) {
            "Column" -> Column {
                template.children.forEach { RenderTemplate(it.value, callback) }
            }

            "Row" -> Row {
                template.children.forEach { RenderTemplate(it.value, callback) }
            }

            "OrganismContentBubble" -> OrganismContentBubble(
                scrollable = template.params["scrollable"].toBoolean(),
                content = { RenderTemplate(template.children["content"], callback) },
                footerContent = { RenderTemplate(template.children["footerContent"], callback) }
            )

            "OrganismAppInfo" -> OrganismAppInfo(
                template.params["appVersion"].orEmpty(),
                template.params["isDebug"].toBoolean()
            )

            "OrganismAppDownloadLink" -> OrganismAppDownloadLink(
                template.params["appVersion"].orEmpty(),
            )

            "OrganismErrorLog" -> OrganismErrorLog(template.params["lastError"])

            "OkCardFooter" -> OkCardFooter(onOk = { callback("onClose", emptyMap()) })
        }
    }
}

//@Composable
//fun CusomPageTemplate() {
//    RenderTemplate(
//        Json.decodeFromString<TemplateNode>(customPageTemplateJson),
//        { command, params ->
//            when (command) {
//                "onDealSelected" -> //send to backend for handling
//                "onFilterSelected" -> //send to backend for handling
//            }
//        }
//    )
//}

@Composable
fun BackendInfoTemplate(
    state: AppStateInfoViewState,
    onClose: () -> Unit
) {
    OrganismContentBubble(
        scrollable = true,
        content = {
            Column {
                OrganismAppInfo(state.appVersion, state.isDebug)
                OrganismAppDownloadLink(state.appVersion)
                OrganismErrorLog(state.lastError)
            }
        },
        footerContent = {
            OkCardFooter { onClose() }
        }
    )
}
