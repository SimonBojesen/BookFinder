package com.example.bookfinder.model.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VolumeInfo {

   private String title;
   private String subtitle;
   private List<String> authors;
   private String publisher;
   private String publishedDate;
   private String description;
   private Integer pageCount;
   private String printType;
   private List<String> categories;
   private List<IndustryIdentifiers> industryIdentifiers;
   private ImageLinks imageLinks;
   private Double averageRating;
   private Integer ratingsCount;
   private String language;


   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getSubtitle() {
      return subtitle;
   }

   public void setSubtitle(String subtitle) {
      this.subtitle = subtitle;
   }

   public List<String> getAuthors() {
      return authors;
   }

   public void setAuthors(List<String> authors) {
      this.authors = authors;
   }

   public String getPublisher() {
      return publisher;
   }

   public void setPublisher(String publisher) {
      this.publisher = publisher;
   }

   public String getPublishedDate() {
      return publishedDate;
   }

   public void setPublishedDate(String publishedDate) {
      this.publishedDate = publishedDate;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Integer getPageCount() {
      return pageCount;
   }

   public void setPageCount(Integer pageCount) {
      this.pageCount = pageCount;
   }

   public String getPrintType() {
      return printType;
   }

   public void setPrintType(String printType) {
      this.printType = printType;
   }

   public List<String> getCategories() {
      return categories;
   }

   public void setCategories(List<String> categories) {
      this.categories = categories;
   }

   public List<IndustryIdentifiers> getIndustryIdentifiers() {
      return industryIdentifiers;
   }

   public void setIndustryIdentifiers(List<IndustryIdentifiers> industryIdentifiers) {
      this.industryIdentifiers = industryIdentifiers;
   }

   public ImageLinks getImageLinks() {
      return imageLinks;
   }

   public void setImageLinks(ImageLinks imageLinks) {
      this.imageLinks = imageLinks;
   }

   public Double getAverageRating() {
      return averageRating;
   }

   public void setAverageRating(Double averageRating) {
      this.averageRating = averageRating;
   }

   public Integer getRatingsCount() {
      return ratingsCount;
   }

   public void setRatingsCount(Integer ratingsCount) {
      this.ratingsCount = ratingsCount;
   }

   public String getLanguage() {
      return language;
   }

   public void setLanguage(String language) {
      this.language = language;
   }
}
