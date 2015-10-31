package it.jaschke.alexandria;

import android.app.Activity;
// import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
// import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
// import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
// import android.widget.Toast;


import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;

// import com.google.zxing.*;

public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    protected static EditText ean;
    private final int LOADER_ID = 1;
    private View rootView;
    private final String EAN_CONTENT = "eanContent";

    public AddBook(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(ean != null) {
            outState.putString(EAN_CONTENT, ean.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ean = (EditText) rootView.findViewById(R.id.ean);

        ean.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean = s.toString();
                //catch isbn10 numbers
                if(ean.length() == 10 && !ean.startsWith("978")){
                    ean = "978" + ean;
                }
                if(ean.length()<13){
                    clearFields();
                    return;
                }

                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBook.this.restartLoader();
            }
        });

        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Intent intent = new Intent(
                            "com.google.zxing.client.android.SCAN");
                    intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "PRODUCT_MODE");
                    startActivityForResult(intent, 0);

                } catch (Exception e) {

                    e.printStackTrace();

                }

            }
        });

        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ean.setText("");
            }
        });

        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                ean.setText("");
            }
        });

        if(savedInstanceState != null){
            ean.setText(savedInstanceState.getString(EAN_CONTENT));
            ean.setHint("");
        }

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {


        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                ean.setText(intent.getStringExtra("SCAN_RESULT"));
                onEANReady(intent.getStringExtra("SCAN_RESULT"));
            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }

    }


    public void onEANReady(String ean){

        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, ean);
        bookIntent.setAction(BookService.FETCH_BOOK);
        getActivity().startService(bookIntent);
        AddBook.this.restartLoader();

    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(ean.getText().length()==0){
            return null;
        }
        String eanStr= ean.getText().toString();
        if(eanStr.length() == 10 && !eanStr.startsWith("978")){
            eanStr = "978" + eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {

        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        if(bookTitle != null) {
            ((TextView) rootView.findViewById(R.id.bookTitle)).setText(bookTitle);
        }

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        if(bookSubTitle != null) {
            ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);
        }

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr;

        if(authors != null) {
            authorsArr = authors.split(",");
            ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
            ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",","\n"));
        }

        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            new DownloadImage((ImageView) rootView.findViewById(R.id.bookCover)).execute(imgUrl);
            rootView.findViewById(R.id.bookCover).setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        if(categories != null) {
            ((TextView) rootView.findViewById(R.id.categories)).setText(categories);
        }

        if(MainActivity.currentMenu != null) {
            MenuItem mi;
            mi = MainActivity.currentMenu.findItem(R.id.action_delete);
            mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            mi = MainActivity.currentMenu.findItem(R.id.action_next);
            mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields(){
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.authors)).setText("");
        ((TextView) rootView.findViewById(R.id.categories)).setText("");
        rootView.findViewById(R.id.bookCover).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

}
