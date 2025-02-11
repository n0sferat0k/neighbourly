package com.neighbourly.app.b_adapt.viewmodel.bean

import kotlinx.datetime.Instant

data class ReminderVS(val id: Int?, val name: String, val next: Instant? = null)