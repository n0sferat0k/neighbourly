package com.neighbourly.app.d_entity.interf

import kotlinx.coroutines.flow.Flow

interface ConfigProvider {
    val wideScreenFlow: Flow<Boolean>
}