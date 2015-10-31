package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.services.BookService;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, Callback {


    protected static ActionBar currentActionBar;
    protected static Menu currentMenu;
    protected static String currentTitle;
    protected static String currentBookTitle;
    String currentFragmentTag;
    String currentSubtitle;
    public static Fragment currentFragment;
    protected static boolean isSearchViewIconified = true;
    protected static boolean isSearchViewAvailable = false;
    protected static String currentQuery;

    final static String KEY_TITLE = "kTitle";
    final static String KEY_SUBTITLE = "kSubtitle";
    final static String KEY_SHOW_AS_ACTION = "kShowAsAction";
    final static String KEY_IS_SEARCH_VIEW_ICONIFIED = "kIsIconified";
    final static String KEY_SEARCH_VIEW_QUERY_TEXT = "kQueryText";

    public static Context appContext;

    private NavigationDrawerFragment navigationDrawerFragment;

    private CharSequence title;
    public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReciever;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appContext = getApplicationContext();
        super.onCreate(savedInstanceState);

        IS_TABLET = isTablet();
        if(IS_TABLET){
            setContentView(R.layout.activity_main_tablet);
        }else {
            setContentView(R.layout.activity_main);
        }

        messageReciever = new MessageReciever();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever,filter);

        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();
        currentActionBar = getSupportActionBar();

        navigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        currentActionBar.setTitle(currentTitle);
        currentActionBar.setSubtitle(currentSubtitle);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        currentFragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.books));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment;
        currentFragment = null;

        switch (position){
            default:
            case 0:
                nextFragment = new ListOfBooks();
                currentTitle = getResources().getString(R.string.app_name);
                currentSubtitle = getResources().getString(R.string.books);
                isSearchViewAvailable = true;
                currentFragment = nextFragment;
                break;
            case 1:
                nextFragment = new AddBook();
                currentTitle = getResources().getString(R.string.app_name);
                currentSubtitle = getResources().getString(R.string.scan);
                isSearchViewAvailable = false;
                break;
            case 2:
                nextFragment = new About();
                currentTitle = getResources().getString(R.string.app_name);
                currentSubtitle = getResources().getString(R.string.about);
                isSearchViewAvailable = false;
                break;
        }

        currentFragmentTag = currentSubtitle;
        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment, currentFragmentTag)
                .addToBackStack(null)
                .commit();


        if(currentActionBar != null){
            currentActionBar.setTitle(currentTitle);
            currentActionBar.setSubtitle(currentSubtitle);
        }

        setSearchView();

    }



    public void setTitle(int titleId) {
        title = getString(titleId);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null) {
            currentTitle = savedInstanceState.getString(KEY_TITLE);
            currentSubtitle = savedInstanceState.getString(KEY_SUBTITLE);
            isSearchViewAvailable = savedInstanceState.getBoolean(KEY_SHOW_AS_ACTION);
            isSearchViewIconified = savedInstanceState.getBoolean(KEY_IS_SEARCH_VIEW_ICONIFIED);
            currentQuery = savedInstanceState.getString(KEY_SEARCH_VIEW_QUERY_TEXT);
        }

        if(currentActionBar != null){
            currentActionBar.setTitle(currentTitle);
            currentActionBar.setSubtitle(currentSubtitle);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString(KEY_TITLE, currentTitle);
        outState.putString(KEY_SUBTITLE, currentSubtitle);
        outState.putBoolean(KEY_SHOW_AS_ACTION, isSearchViewAvailable);

        if(isSearchViewAvailable && currentMenu != null){
            SearchView sv = (SearchView)currentMenu.findItem(R.id.action_search).getActionView();
            if (sv != null) {

                    outState.putBoolean(KEY_IS_SEARCH_VIEW_ICONIFIED, sv.isIconified());
                    outState.putString(KEY_SEARCH_VIEW_QUERY_TEXT, sv.getQuery().toString());
            }
        } else {
            outState.putBoolean(KEY_IS_SEARCH_VIEW_ICONIFIED, true);
            outState.putString(KEY_SEARCH_VIEW_QUERY_TEXT, "");
        }

        super.onSaveInstanceState(outState);
    }

    protected void setSearchView(){

        Menu m = currentMenu;
        MenuItem mi;

        if(isSearchViewAvailable && currentFragment != null && currentFragment.getTag() != null) {

            if (currentFragment.getTag().equals(getResources().getString(R.string.books))) {

                if (m != null) {
                    mi = m.findItem(R.id.action_search);
                    if (mi != null) {
                        mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
                        mi.setVisible(true);
                        SearchView sv = (SearchView) mi.getActionView();
                        sv.setIconifiedByDefault(false);
                        sv.setOnQueryTextListener(
                                new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String s) {
                                        ListOfBooks.searchString = s;
                                        if (currentFragment != null) {
                                            ((ListOfBooks) currentFragment).restartLoader();
                                        }
                                        return true;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(String s) {
                                        ListOfBooks.searchString = s;

                                        if (currentFragment != null) {

                                            ((ListOfBooks) currentFragment).restartLoader();
                                        }
                                        return true;
                                    }
                                }
                        );

                        sv.setQuery(currentQuery, false);
                        sv.setIconified(isSearchViewIconified);
                    }
                }
            }
        } else {

            if(m != null){
                mi = m.findItem(R.id.action_search);
                if(mi != null){
                    mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                    mi.setVisible(false);
                }
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {

            getMenuInflater().inflate(R.menu.main, menu);
            currentMenu = menu;
            if(currentMenu != null) {
                MenuItem mi = currentMenu.findItem(R.id.action_search);
                if(mi != null) {
                    setSearchView();

                }
            }

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if(id == R.id.action_next){
            AddBook.ean.setText("");
            return true;
        } else if(id == R.id.action_delete ){
            if(currentFragmentTag != null && currentFragmentTag.equals(getString(R.string.scan))) {
                Intent bookIntent = new Intent(this, BookService.class);
                bookIntent.putExtra(BookService.EAN, AddBook.ean.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                startService(bookIntent);
                AddBook.ean.setText("");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStop() {

        Bundle b = new Bundle();
        onSaveInstanceState(b);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {
        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        currentFragment = fragment;

        int id = R.id.container;
        if(findViewById(R.id.right_container) != null){
            id = R.id.right_container;
        }

        if(currentMenu != null)
        {
            MenuItem mi = currentMenu.findItem(R.id.action_search);
            mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            mi.setVisible(false);
        }

        if(currentActionBar != null){
            currentActionBar.setSubtitle(currentSubtitle);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment, getResources().getString(R.string.book_details))
                .addToBackStack("Book Detail")
                .commit();

    }

    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(MESSAGE_KEY)!=null){
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goBack(View view){
        getSupportFragmentManager().popBackStack();
    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed() {

        if(currentFragment != null && currentFragment.getTag() != null && currentFragment.getTag().equals(getResources().getString(R.string.book_details))){
            getSupportFragmentManager().popBackStack();
            currentFragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.books));
            currentActionBar.setSubtitle(R.string.books);
            setSearchView();
        } else {
            super.onBackPressed();
            ArrayList<Fragment> fragmentList = new ArrayList(getSupportFragmentManager().getFragments());
            for(Fragment f:fragmentList)
                  {
                      if(f != null && f.isVisible()){
                         currentActionBar.setSubtitle(f.getTag());
                          break;
                      }
                  }
        }

    }




}