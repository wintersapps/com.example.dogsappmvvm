package com.examples.dogsappmvvm.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class DogBreed {

    @ColumnInfo(name = "breed_id")
    @SerializedName("id")
    private String breedId;

    @ColumnInfo(name = "dog_name")
    @SerializedName("name")
    private String dogBreed;

    @ColumnInfo(name = "life_span")
    @SerializedName("life_span")
    private String lifespan;

    @ColumnInfo(name = "breed_group")
    @SerializedName("breed_group")
    private String breedGroup;

    @ColumnInfo(name = "bred_for")
    @SerializedName("bred_for")
    private String breedFor;

    @ColumnInfo(name = "temperament")
    @SerializedName("temperament")
    private String temperament;

    @ColumnInfo(name = "dog_url")
    @SerializedName("url")
    private String imageUrl;

    @PrimaryKey(autoGenerate = true)
    private int uid;

    public DogBreed() {
    }

    public DogBreed(String breedId, String dogBreed, String lifespan, String breedGroup, String breedFor, String temperament, String imageUrl) {
        this.breedId = breedId;
        this.dogBreed = dogBreed;
        this.lifespan = lifespan;
        this.breedGroup = breedGroup;
        this.breedFor = breedFor;
        this.temperament = temperament;
        this.imageUrl = imageUrl;
    }

    public String getBreedId() {
        return breedId;
    }

    public void setBreedId(String breedId) {
        this.breedId = breedId;
    }

    public String getDogBreed() {
        return dogBreed;
    }

    public void setDogBreed(String dogBreed) {
        this.dogBreed = dogBreed;
    }

    public String getLifespan() {
        return lifespan;
    }

    public void setLifespan(String lifespan) {
        this.lifespan = lifespan;
    }

    public String getBreedGroup() {
        return breedGroup;
    }

    public void setBreedGroup(String breedGroup) {
        this.breedGroup = breedGroup;
    }

    public String getBreedFor() {
        return breedFor;
    }

    public void setBreedFor(String breedFor) {
        this.breedFor = breedFor;
    }

    public String getTemperament() {
        return temperament;
    }

    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
