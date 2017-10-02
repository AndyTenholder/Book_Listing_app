package com.andytenholder.booklistingapp;

/**
 * Created by Andy Tenholder on 3/1/2017.
 */

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving book data from GoogleBooks API.
 */
public final class QueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the GoogleBooks API and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<Book> books = extractFromJson(jsonResponse);

        // Return the list of {@link Book}s
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of items (or books).
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            // For each book in the bookArray, create an {@link Book} object
            for (int i = 0; i < bookArray.length(); i++) {

                // Get a single book at position i within the list of books
                JSONObject book_items = bookArray.getJSONObject(i);

                // create holder for bookId and then check JSON for object
                String bookId = "No Book ID Found";
                if (book_items.has("id")){
                // get book id
                bookId = book_items.optString("id");
                }

                // locate the valumeInfo object within the items object
                JSONObject volume_info = book_items.optJSONObject("volumeInfo");

                //Create variable for book title and check JSON for object
                String bookTitle ="No Title Found";
                if (volume_info.has("title")) {
                    // get book title
                    bookTitle = volume_info.getString("title");
                }

                // create empty string to hold author(s) name
                String bookAuthorsComplete = "";
                if(volume_info.has("authors")) {
                    // locate the authors array
                    JSONArray book_authors = volume_info.getJSONArray("authors");

                    // for loop to gather names of all authors
                    for (int j = 0; j < book_authors.length(); j++) {
                        // locate name of author at position j
                        String bookAuthor = book_authors.getString(j);
                        // checks if any authors have already been added
                        if (bookAuthorsComplete.isEmpty()) {
                            bookAuthorsComplete = bookAuthor;
                        } else {
                            // adds additional authors and seperates them by commas
                            bookAuthorsComplete = bookAuthorsComplete + ", " + bookAuthor;
                        }
                    }
                }else {
                    // If no author object is found set to "No Author Provided"
                    bookAuthorsComplete = "No Author Found";
                }

                // Create string to hold book publisher and check JSON for object
                String bookPublisher = "No Publisher Found";
                if(volume_info.has("publisher")){
                // get book publisher
                bookPublisher = volume_info.getString("publisher");
                }

                // Create string to hold book publish date and check JSON for object
                String publishedDate = "No Publish Date Found";
                if(volume_info.has("publishedDate")){
                // get date published
                publishedDate = volume_info.getString("publishedDate");}

                // Create string to hold book description and check JSON for object
                String bookDescription = "No Book Description Found";
                if(volume_info.has("description")){
                // get book description
                bookDescription = volume_info.getString("description");}

                // Create string to hold book description and check JSON for object
                String pageCount = "No Page Count Found";
                if(volume_info.has("pageCount")){
                // get page count
                pageCount = volume_info.getString("pageCount");}

                // Create string to hold average rating and check JSON for object
                String averageRating = "No Average Rating Found";
                if(volume_info.has("averageRating")){
                // get average rating
                averageRating = volume_info.getString("averageRating");}

                // Create string to hold rating count and check JSON for object
                String ratingsCount = "No Rating Count Found";
                if(volume_info.has("ratingsCount")){
                //get number of people who have rated the book
                 ratingsCount = volume_info.getString("ratingsCount");}

                //Create empty string and then check if the JSON has the needed object
                String smallThumbnailString = "";
                if (volume_info.has("imageLinks")) {
                    // locate the imageLinks object
                    JSONObject image_links = volume_info.getJSONObject("imageLinks");
                    // create a string for the book cover
                    smallThumbnailString = image_links.getString("smallThumbnail");
                }else{
                    //if JSON does not contain cover image set to "no cover" to be caught and replaced in adapter line 52
                    smallThumbnailString = "no cover";
                }

                // create string for link to book website location
                String infoLinkString = volume_info.optString("infoLink");
                // set base value of infoLinkURL to google books website to aviod URL being empty
                URL infoLinkURL = new URL("https://books.google.com/");
                // check that string is not empty and convert to URL
                if (infoLinkString != null && !infoLinkString.isEmpty()){
                    infoLinkURL = createUrl(infoLinkString);
                }
                //(String idTag, String title, String authors, String publisher, String publishedDate, String description, String pageCount, String averageRating,
                //String ratingCount, URL smallThumbNail, URL previewLink)
                books.add(new Book (bookId, bookTitle, bookAuthorsComplete, bookPublisher, publishedDate, bookDescription, pageCount, averageRating, ratingsCount,
                        smallThumbnailString, infoLinkURL));

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Return the list of books
        return books;
    }

}