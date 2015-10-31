package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity
{
    public static int selected_match_id;

    // current_fragment must be greater or equal to 2:
    // if 2 than pager covers 5 days
    // if 3 than pager covers 7 days etc.

    public static int current_fragment = 2;
    public static Context theContext;

/*    private final String save_tag = "Save Test";*/
    public static PagerFragment mainPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Version 6.0 --->

        theContext = getApplicationContext();

        // <--- Version 6.0 ---


        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            mainPagerFragment = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mainPagerFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
/*        Log.v(save_tag,"will save");
        Log.v(save_tag,"fragment: "+String.valueOf(mainPagerFragment.mPagerHandler.getCurrentItem()));
        Log.v(save_tag,"selected id: "+selected_match_id);*/
        outState.putInt(this.getString(R.string.current_fragment_tag),mainPagerFragment.mPagerHandler.getCurrentItem());
        outState.putInt(this.getString(R.string.current_match_id),selected_match_id);
        getSupportFragmentManager().putFragment(outState,this.getString(R.string.main_pager_fragment_tag),mainPagerFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
/*        Log.v(save_tag,"will retrive");
        Log.v(save_tag,"fragment: "+String.valueOf(savedInstanceState.getInt(this.getString(R.string.current_fragment_tag))));
        Log.v(save_tag,"selected id: "+savedInstanceState.getInt(this.getString(R.string.current_match_id)));*/
        current_fragment = savedInstanceState.getInt(this.getString(R.string.current_fragment_tag));
        selected_match_id = savedInstanceState.getInt(this.getString(R.string.current_match_id));
        mainPagerFragment = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState,this.getString(R.string.main_pager_fragment_tag));

        super.onRestoreInstanceState(savedInstanceState);
    }
}
