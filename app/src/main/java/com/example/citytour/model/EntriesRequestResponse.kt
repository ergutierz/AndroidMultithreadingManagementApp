package com.example.citytour.model

import com.google.gson.annotations.SerializedName

data class EntriesRequestResponse(

	@field:SerializedName("entries")
	val entries: List<Entry?>? = null,

	@field:SerializedName("count")
	val count: Int? = null
)

data class Entry(

	@field:SerializedName("Description")
	val description: String? = null,

	@field:SerializedName("Category")
	val category: String? = null,

	@field:SerializedName("HTTPS")
	val hTTPS: Boolean? = null,

	@field:SerializedName("Auth")
	val auth: String? = null,

	@field:SerializedName("API")
	val aPI: String? = null,

	@field:SerializedName("Cors")
	val cors: String? = null,

	@field:SerializedName("Link")
	val link: String? = null
)
