package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.runtime.Composable
import com.neighbourly.app.a_device.ui.atomic.organism.web.WebGalleryView
import com.neighbourly.app.a_device.ui.atomic.page.WebMapPage
import com.neighbourly.app.a_device.ui.atomic.organism.web.WebPageView
import com.neighbourly.app.b_adapt.viewmodel.navigation.WebContent
import com.neighbourly.app.b_adapt.viewmodel.navigation.WebContent.WebGallery
import com.neighbourly.app.b_adapt.viewmodel.navigation.WebContent.WebMap

@Composable
fun WebContentTemplate(
    webContent: WebContent
) {
    when (webContent) {
        WebMap -> WebMapPage()

        is WebGallery -> WebGalleryView(
            itemId = webContent.itemId,
            householdId = webContent.householdId,
            imageId = webContent.imageId
        )

        is WebContent.WebPage -> WebPageView(webContent.url)
    }
}
