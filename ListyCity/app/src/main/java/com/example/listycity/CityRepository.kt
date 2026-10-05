package com.example.listycity

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CityRepository {
    private val db = Firebase.firestore
    private val citiesRef = db.collection("cities")

    private val _cities = mutableStateListOf(
        City("Edmonton", "AB"),
        City("Vancouver", "BC"),
        City("Toronto", "ON")
    )

    val cities: List<City>
        get() = _cities

    fun addCity(city: City) {
        citiesRef.document(city.name).set(city)
    }

    fun deleteCity(city: City) {
        citiesRef.document(city.name).delete()
    }

    fun updateCity(oldCity: City, updatedCity: City) {
        //Causes bugs with both addCity and deleteCity; database appears to store
        //old City attributes even after updateCity is called. In practice, this
        //causes bugs such as the following:
        //1. If you add Edmonton, AB, for instance, then update Edmonton, AB to
        //   Vancouver, BC, then add Edmonton, AB back, Vancouver, BC gets replaced
        //   with Edmonton, AB instead of storing both.
        //2. If you add Edmonton, AB, then update Edmonton, AB to Calgary, AB, trying
        //   to delete Calgary, AB will not remove anything. Instead, if you try to
        //   delete Edmonton, AB, Calgary, AB will be removed.
        //Leaving this here because I was not sure if I should change updateCity to
        //address these problems, since this was the implementation given in the lab
        //instructions.
        citiesRef.document(oldCity.name).set(updatedCity)
    }

    init {
        citiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _cities.clear()

            snapshot?.documents?.forEach { document ->
                val city = document.toObject(City::class.java)
                if (city != null) {
                    _cities.add(city)
                }
            }
        }
    }
}