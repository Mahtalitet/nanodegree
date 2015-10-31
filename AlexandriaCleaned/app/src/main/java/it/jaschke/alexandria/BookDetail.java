package it.jaschke.alexandria;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class BookDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EAN_KEY = "EAN";
    private final int LOADER_ID = 10;
    private View rootView;
    protected static String ean;
    private String bookTitle;
    private ShareActionProvider shareActionProvider;

    public BookDetail(){
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            ean = arguments.getString(BookDetail.EAN_KEY);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }

        Menu m = ((MainActivity)getActivity()).currentMenu;
        if (m != null){
            MenuItem mi = m.findItem(R.id.action_search);
            if(mi != null) {
                mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }
        }

        rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
        return rootView;

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.book_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");

        Bundle arguments = getArguments();
        ean = arguments.getString(BookDetail.EAN_KEY);
        Cursor c = getActivity().getContentResolver().query(
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
                new String[]{AlexandriaContract.BookEntry.TITLE}, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        c.moveToFirst();
        bookTitle = c.getString(c.getColumnIndex(AlexandriaContract.BookEntry.TITLE));

        if(MainActivity.currentActionBar != null){
            MainActivity.currentActionBar.setSubtitle(bookTitle);
        }

        String sMessage = getString(R.string.share_text) + bookTitle;
        shareIntent.putExtra(Intent.EXTRA_TEXT, sMessage);
        shareActionProvider.setShareIntent(shareIntent);


        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                return true;
            }
        });


        MenuItem mi = menu.findItem(R.id.action_delete);
        mi.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            }
        });

        mi = menu.findItem(R.id.action_search);
        mi.setVisible(false);
        mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
                null,
                null,
                null,
                null
        );

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_delete){

            Intent bookIntent = new Intent(getActivity(), BookService.class);
            bookIntent.putExtra(BookService.EAN, BookDetail.ean);
            bookIntent.setAction(BookService.DELETE_BOOK);
            getActivity().startService(bookIntent);
            getActivity().getSupportFragmentManager().popBackStack();
            return true;
        }

        if(item.getItemId() == R.id.action_share){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {

        if (!data.moveToFirst()) {
            return;
        }

        // Error: check if null

        bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        if(bookTitle != null) {
            ((TextView) rootView.findViewById(R.id.fullBookTitle)).setText(bookTitle);
        }

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        if (bookSubTitle != null) {
            ((TextView) rootView.findViewById(R.id.fullBookSubTitle)).setText(bookSubTitle);
        }

        String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
        if (desc != null) {
            ((TextView) rootView.findViewById(R.id.fullBookDesc)).setText(desc);
        }

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        if (authors != null) {
            String[] authorsArr = authors.split(",");
            ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
            ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));

            String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
            if (Patterns.WEB_URL.matcher(imgUrl).matches()) {

                ImageView iv = (ImageView)rootView.findViewById(R.id.fullBookCover);

                new DownloadImage(iv).execute(imgUrl);
                iv.setVisibility(View.VISIBLE);
            }
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        if(categories != null) {
            ((TextView) rootView.findViewById(R.id.categories)).setText(categories);
        }



    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onPause();
    }




}