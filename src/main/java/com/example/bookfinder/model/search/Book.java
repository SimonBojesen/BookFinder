package com.example.bookfinder.model.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {

   private String id;

   private VolumeInfo volumeInfo;

   private SearchInfo searchInfo;

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public VolumeInfo getVolumeInfo() {
      return volumeInfo;
   }

   public void setVolumeInfo(VolumeInfo volumeInfo) {
      this.volumeInfo = volumeInfo;
   }

   public SearchInfo getSearchInfo() {
      return searchInfo;
   }

   public void setSearchInfo(SearchInfo searchInfo) {
      this.searchInfo = searchInfo;
   }
}
