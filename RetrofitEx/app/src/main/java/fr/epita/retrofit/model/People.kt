package fr.epita.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class People (

    @Expose
    @SerializedName("id")
    val id: String,

    @Expose
    @SerializedName("name")
    val name: String,

    @Expose
    @SerializedName("gender")
    val gender: String,

    @Expose
    @SerializedName("age")
    val age: String,

    @Expose
    @SerializedName("eye_color")
    val eyeColor: String,

    @Expose
    @SerializedName("hair_color")
    val hairColor: String,

    @Expose
    @SerializedName("films")
    val films: List<String>,

    @Expose
    @SerializedName("species")
    val species: String,

    @Expose
    @SerializedName("url")
    val url: String
)
