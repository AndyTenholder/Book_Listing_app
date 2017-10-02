package com.andytenholder.booklistingapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>>{

    public static final String LOG_TAG = MainActivity.class.getName();


    /** URL for book data from the Google Book API */
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    private static final String API_KEY = "AIzaSyBCZXY0OOQosABa3sLgtt2x-apkg8pvBp0";


    /**
     * Constant value for the loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int LOADER_ID = 1;

    /** Adapter for the list of books */
    private BookAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.emptyView);
        bookListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);


        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Get a reference to the LoaderManager, in order to interact with loaders.
        final LoaderManager loaderManager = getLoaderManager();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }


        //Find the search button in the activity_main
        Button searchButton = (Button) findViewById(R.id.button_search);
        //Set an OnClickListener on the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When the search button is clicked restart the loader to preform the search
                loaderManager.restartLoader(LOADER_ID, null, MainActivity.this);
            }
        });

    }


    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {

        //call the createRequestURL method and pass that string into the BookLoader
        String completedURLString = createRequestURL();

        return new BookLoader(this, completedURLString);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No books found."
        mEmptyStateTextView.setText(R.string.no_books);

        // Clear the adapter of previous data
        mAdapter.clear();

        // If there is a valid list of {@link Books}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // BookLoader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    //Method to use the data for the search field to create a string URL that matches the google books API requirements
    public String createRequestURL (){

        //String to hold the completed URL string
        String completeURLString = "";

        //Locate EditText box and create a string of it's data
        EditText textBox = (EditText) findViewById(R.id.text_box);
        String textBoxString = textBox.getText().toString();

        //String to hold the search data after the spaces have been removed and replaced with %20
        String formattedTextBox = "";

        //Check if the search contained spaces
        if (textBoxString.contains(" ")){
            //Create an array of strings splitting at each space
            String[] splitStr = textBoxString.trim().split(" ");

            //for loop that will run for each string in the new array and put them together into a single string
            for(int b = 0; b < splitStr.length; b++){
                //check if the formattedTextBox string is empty
                if (formattedTextBox.isEmpty()) {
                    //set formattedTextBox string equal to the first string in the array
                    formattedTextBox = splitStr[b];
                }else{
                    //add %20 and next string in the array
                    formattedTextBox = formattedTextBox + "%20" + splitStr[b];
                }
            }
        }else{
            //if the search contained no spaces set formattedTextBox equal to textBoxString
            formattedTextBox = textBoxString;
        }


        // Locate title radio button and see if it is checked
        RadioButton titleButton = (RadioButton) findViewById(R.id.title_button);
        boolean titleButtonChecked = titleButton.isChecked();

        // Locate author radio button and see if it is checked
        RadioButton authorButton = (RadioButton) findViewById(R.id.author_button);
        boolean authorButtonChecked = authorButton.isChecked();

        //If title button is checked put together string URL for title
        if (titleButtonChecked){
            completeURLString = BASE_URL + "intitle:" + formattedTextBox + "&" + API_KEY;
        }
        //If author button is checked put together string URL for author
        else if (authorButtonChecked){
            completeURLString = BASE_URL + "inauthor:" + formattedTextBox + "&" + API_KEY;
        }
        //If neither title nor author buttons are checked put together string URL for subject
        else{
            completeURLString = BASE_URL + "subject:" + formattedTextBox + "&" + API_KEY;
        }

        return completeURLString;

    }

}
