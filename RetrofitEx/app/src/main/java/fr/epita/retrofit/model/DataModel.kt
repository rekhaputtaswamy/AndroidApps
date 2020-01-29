package fr.epita.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DataModel (
    @Expose
    @SerializedName("id")
    val id: Integer,

    @Expose
    @SerializedName("title")
    val title: String,

    @Expose
    @SerializedName("description")
    val description: String,

    @Expose
    @SerializedName("director")
    val director: String,

    @Expose
    @SerializedName("producer")
    val producer: String,

    @Expose
    @SerializedName("release_date")
    val release_date: String,

    @Expose
    @SerializedName("rt_score")
    val rt_score: String,

    @Expose
    @SerializedName("people")
    val people: List<String>,

    @Expose
    @SerializedName("species")
    val species: List<String>,

    @Expose
    @SerializedName("locations")
    val locations: List<String>,

    @Expose
    @SerializedName("vehicles")
    val vehicles: List<String>,

    @Expose
    @SerializedName("url")
    val url: String,

    @Expose
    @SerializedName("length")
    val length: Object

    /*@Expose
    @SerializedName("albumId")
    val albumId: Integer,

    @Expose
    @SerializedName("id")
    val id: Integer,

    @Expose
    @SerializedName("title")
    val title: String,

    @Expose
    @SerializedName("url")
    val url: String,

    @Expose
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String*/

)