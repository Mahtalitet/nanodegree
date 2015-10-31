package barqsoft.footballscores;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.Source;

import barqsoft.footballscores.service.FootballScoresWidgetService;

/**
 * Created by Marcin Gruszecki on 2015-09-09.
 * Project Three @ Udacity
 */

public class FootballScoresWidget extends AppWidgetProvider {


    private AppWidgetManager theAppWidgetManager;

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {

        if(theAppWidgetManager != null) {
            this.onUpdate(context,theAppWidgetManager,theAppWidgetManager.getAppWidgetIds(new ComponentName(context, FootballScoresWidget.class)));
        }
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {

        if(theAppWidgetManager != null) {
            this.onUpdate(context,theAppWidgetManager,theAppWidgetManager.getAppWidgetIds(new ComponentName(context, FootballScoresWidget.class)));
        }
        super.onEnabled(context);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        theAppWidgetManager = appWidgetManager;

        ComponentName thisWidget = new ComponentName(context,
                FootballScoresWidget.class);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_main);

            // -- Collection Widget -->

            Intent srvIntent = new Intent(context, FootballScoresWidgetService.class);
            // remoteViews.setRemoteAdapter(widgetId, srvIntent);
            remoteViews.setRemoteAdapter(widgetId, R.id.stack_view, srvIntent);

            // <-- Collection Widget --

            /*

            Date today = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat(context.getResources().getString(R.string.YYYYMMDD_format));
            String[] whereArgs = {dateFormat.format(today)};

            //TODO Position on the next match

            Cursor c = context.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),null,null,whereArgs,null);
            if(c != null) {
                c.moveToFirst();
                String sHomeScore = c.getString(c.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL));
                if(sHomeScore.equals(context.getString(R.string.match_score_scheduled_db))){
                    remoteViews.setTextViewText(R.id.host_score,context.getString(R.string.match_score_scheduled));
                } else {
                    remoteViews.setTextViewText(R.id.host_score,sHomeScore);
                }
                String sAwayScore = c.getString(c.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL));
                if(sAwayScore.equals(context.getString(R.string.match_score_scheduled_db))){
                    remoteViews.setTextViewText(R.id.guest_score,context.getString(R.string.match_score_scheduled));
                } else {
                    remoteViews.setTextViewText(R.id.guest_score,sAwayScore);
                }
                remoteViews.setTextViewText(R.id.host_team, c.getString(c.getColumnIndex(DatabaseContract.scores_table.HOME_COL)));
                remoteViews.setTextViewText(R.id.guest_team, c.getString(c.getColumnIndex(DatabaseContract.scores_table.AWAY_COL)));
                String s = c.getString(c.getColumnIndex(DatabaseContract.scores_table.TIME_COL));
                if(s.equals(context.getString(R.string.match_status_finished_db)))
                {
                    remoteViews.setTextViewText(R.id.match_time,context.getString(R.string.match_status_finished));
                } else {
                    remoteViews.setTextViewText(R.id.match_time, s);
                }
            }

            */

            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }

/*
        Intent intent = new Intent(context, MyWidgetProvider.class);

        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
*/



        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
