package com.andytenholder.booklistingapp;

import java.net.URL;

/**
 * Created by Andy Tenholder on 3/1/2017.
 */

public class Book {
    private String mIdTag;
    public String getIdTag(){return mIdTag;}

    private String mTitle;
    public String getTitle() {return mTitle;}

    private String mAuthors;
    public String getAuthors(){return mAuthors;}


    private String mPublisher;
    public String getPublisher(){return mPublisher;}


    private String mPublishedDate;
    public String getPublishedDate(){return mPublishedDate;}


    private String mDescription;
    public String getDescription(){return mDescription;}


    private String mPageCount;
    public String getPageCount(){return mPageCount;}

    private String mAverageRating;
    public String getAverageRating(){return mAverageRating;}

    private String mRatingCount;
    public String getRatingCount(){return mRatingCount;}

    private String mSmallThumbNail;
    public String getSmallThumbNail(){return mSmallThumbNail;}

    private URL mPreviewLink;
    public URL getPreviewLink(){return mPreviewLink;}

    public Book (String idTag, String title, String authors, String publisher, String publishedDate, String description, String pageCount, String averageRating,
                String ratingCount, String smallThumbNail, URL previewLink){
        mIdTag = idTag;
        mTitle = title;
        mAuthors = authors;
        mPublisher = publisher;
        mDescription = description;
        mPageCount = pageCount;
        mAverageRating = averageRating;
        mRatingCount = ratingCount;
        mSmallThumbNail = smallThumbNail;
        mPreviewLink = previewLink;
        mPublishedDate = publishedDate;
    }

}
