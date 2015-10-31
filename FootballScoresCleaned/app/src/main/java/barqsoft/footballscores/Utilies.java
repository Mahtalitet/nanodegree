package barqsoft.footballscores;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies
{
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;

    // --- Version 6.0 --->

    public static final int BUNDESLIGA1_15 = 394;
    public static final int BUNDESLIGA2_15 = 395;
    public static final int LIGUE1_15 = 396;
    public static final int LIGUE2_15 = 397;
    public static final int PREMIERLEAGUE_15 = 398;
    public static final int PRIMERADIVISION_15 = 399;
    public static final int SEGUNDADIVISION_15 = 400;
    public static final int SERIEA_15 = 401;
    public static final int PRIMEIRALIGA_15 = 402;
    public static final int BUNDESLIGA3_15 = 403;
    public static final int EREDIVISIE_15 = 404;
    public static final int CHAMPIONS_15 = 405;

    // <--- Version 6.0 ---



    public static String getLeague(int league_num)
    {
        switch (league_num)
        {
            case SERIE_A : return "Seria A";
            case PREMIER_LEGAUE : return "Premier League";
            case CHAMPIONS_LEAGUE : return "UEFA Champions League";
            case PRIMERA_DIVISION : return "Primera Division";
            case BUNDESLIGA : return "Bundesliga";

            // TODO : import via API league names and store in the database

            // --- Version 6.0 --->
            case BUNDESLIGA1_15: return "1. Bundesliga 2015/16";
            case BUNDESLIGA2_15: return "2. Bundesliga 2015/16";
            case LIGUE1_15: return "Ligue 1 2015/16";
            case LIGUE2_15: return "Ligue 2 2015/16";
            case PREMIERLEAGUE_15: return "Premier League 2015/16";
            case PRIMERADIVISION_15: return "Primera Division 2015/16";
            case SEGUNDADIVISION_15: return "Segunda Division 2015/16";
            case SERIEA_15: return "Serie A 2015/16";
            case PRIMEIRALIGA_15: return "Primeira Liga 2015/16";
            case BUNDESLIGA3_15: return "3. Bundesliga 2015/16";
            case EREDIVISIE_15: return "Eredivisie 2015/16";
            case CHAMPIONS_15: return "Champions League 2015/16";

            default: return "Unknown league. Please report.";
            // default: return "Not known League Please report";
            // <--- Version 6.0 ---

        }
    }
    public static String getMatchDay(int match_day,int league_num)
    {
        if(league_num == CHAMPIONS_LEAGUE || league_num == CHAMPIONS_15)
        {
            if (match_day <= 6)
            {
                return "Group Stages, Matchday : 6";
            }
            else if(match_day == 7 || match_day == 8)
            {
                return "First Knockout round";
            }
            else if(match_day == 9 || match_day == 10)
            {
                return "QuarterFinal";
            }
            else if(match_day == 11 || match_day == 12)
            {
                return "SemiFinal";
            }
            else
            {
                return "Final";
            }
        }
        else
        {
            return "Matchday : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return " - ";
        }
        else
        {
            // --- Version 6.0 --->
            // return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
            return String.valueOf(home_goals) + " : " + String.valueOf(awaygoals);
            // <--- Version 6.0 ---

        }
    }

    public static int getTeamCrestByTeamName (String teamname)
    {

        return R.drawable.no_crest_192;

       /* if (teamname==null){return R.drawable.no_crest_192;}
        switch (teamname)
        {
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.no_crest_192;
        } */
    }
}
