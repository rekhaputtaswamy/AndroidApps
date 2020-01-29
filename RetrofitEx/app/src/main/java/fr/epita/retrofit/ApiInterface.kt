package fr.epita.retrofit

import fr.epita.retrofit.model.DataModel
import fr.epita.retrofit.model.People
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("films")
    fun getFilms(): Call<List<DataModel>>

    @GET("people")
    fun getPeople(): Call<List<People>>

    @GET("photos")
    fun getPhotos(): Call<List<DataModel>>
}