package com.andytenholder.booklistingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Andy Tenholder on 3/3/2017.
 */

public class BookAdapter extends ArrayAdapter <Book>{

    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        Book currentBook = getItem(position);

        String bookTitle = currentBook.getTitle();
        TextView bookTitleTextView = (TextView) convertView.findViewById(R.id.book_title);
        bookTitleTextView.setText(bookTitle);

        String bookAuthor = currentBook.getAuthors();
        TextView bookAuthorsTextView = (TextView) convertView.findViewById(R.id.book_author);
        bookAuthorsTextView.setText(bookAuthor);

        String bookDate = currentBook.getPublishedDate();
        TextView bookDateTextView = (TextView) convertView.findViewById(R.id.book_date);
        bookDateTextView.setText(bookDate);

        String bookCoverImage = currentBook.getSmallThumbNail();
        Context context = parent.getContext();
        ImageView bookCoverImageView = (ImageView) convertView.findViewById(R.id.cover_thumbnail);
        //Check if book had cover image.  If not then use stock image
        if (bookCoverImage == "no cover"){
            bookCoverImageView.setImageResource(R.drawable.no_book_cover);
        }else {
            Picasso.with(context).load(bookCoverImage).into(bookCoverImageView);
        }

        return convertView;
    }
}
