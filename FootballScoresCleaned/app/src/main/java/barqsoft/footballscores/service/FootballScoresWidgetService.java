package barqsoft.footballscores.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

/**
 * Created by Marcin Gruszecki
 * for Udacity Program
 */
public class FootballScoresWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements FootballScoresWidgetService.RemoteViewsFactory {



    private Context theContext;
    private int appWidgetId;

    public StackRemoteViewsFactory(Context pContext, Intent pIntent){

        this.theContext = pContext;
        this.appWidgetId = pIntent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

    }


    @Override
    public void onCreate() {
        // ... do nothing ...
    }

    @Override
    public void onDataSetChanged() {
        // ... do nothing ...
    }

    @Override
    public void onDestroy() {
        // ... do nothing ...
    }

    @Override
    public int getCount() {

        Date today = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(theContext.getResources().getString(R.string.YYYYMMDD_format));
        String[] whereArgs = {dateFormat.format(today)};
        Cursor c = theContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),null,null,whereArgs,null);
        return c.getCount();

    }

    @Override
    public RemoteViews getViewAt(int position) {


        RemoteViews theFrame = new RemoteViews(theContext.getPackageName(),
                R.layout.widget);

        Date today = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(theContext.getResources().getString(R.string.YYYYMMDD_format));
        String[] whereArgs = {dateFormat.format(today)};

        //TODO Position on the next match

        Cursor c = theContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),null,null,whereArgs,null);
        if(c != null) {
            c.moveToFirst();
            c.move(position);
            String sHomeScore = c.getString(c.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL));
            if(sHomeScore.equals(theContext.getString(R.string.match_score_scheduled_db))){
                theFrame.setTextViewText(R.id.host_score,theContext.getString(R.string.match_score_scheduled));
            } else {
                theFrame.setTextViewText(R.id.host_score,sHomeScore);
            }
            String sAwayScore = c.getString(c.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL));
            if(sAwayScore.equals(theContext.getString(R.string.match_score_scheduled_db))){
                theFrame.setTextViewText(R.id.guest_score,theContext.getString(R.string.match_score_scheduled));
            } else {
                theFrame.setTextViewText(R.id.guest_score,sAwayScore);
            }
            theFrame.setTextViewText(R.id.host_team, c.getString(c.getColumnIndex(DatabaseContract.scores_table.HOME_COL)));
            theFrame.setTextViewText(R.id.guest_team, c.getString(c.getColumnIndex(DatabaseContract.scores_table.AWAY_COL)));
            String s = c.getString(c.getColumnIndex(DatabaseContract.scores_table.TIME_COL));
            if(s.equals(theContext.getString(R.string.match_status_finished_db)))
            {
                theFrame.setTextViewText(R.id.match_time,theContext.getString(R.string.match_status_finished));
            } else {
                theFrame.setTextViewText(R.id.match_time, s);
            }
        }

        // appWidgetManager.updateAppWidget(widgetId, theFrame);



        return theFrame;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}