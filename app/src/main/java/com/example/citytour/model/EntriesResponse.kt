package com.example.citytour.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EntriesResponse(

	@Json(name="entries")
	val entries: List<EntriesItem?> = emptyList(),

	@Json(name="count")
	val count: Int? = null
)

@JsonClass(generateAdapter = true)
data class EntriesItem(

	@Json(name="Description")
	val description: String? = null,

	@Json(name="Category")
	val category: String? = null,

	@Json(name="HTTPS")
	val hTTPS: Boolean? = null,

	@Json(name="Auth")
	val auth: String? = null,

	@Json(name="API")
	val aPI: String? = null,

	@Json(name="Cors")
	val cors: String? = null,

	@Json(name="Link")
	val link: String? = null
)
