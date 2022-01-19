package com.org.trophy.admin.Model;

import android.content.Context;

import com.org.trophy.admin.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SportParams {
    public static List<SportModel> SPORTS;
    public static Map<Integer, Integer> SPORT_ICONS;
    public static List<String> WEEKS;
    public static List<String> WEEKS_2;
    public static List<RankingModel> RANKING;

    static {
        SPORTS = new ArrayList<>();
        SPORT_ICONS = new HashMap<>();
        WEEKS = new ArrayList<>();
        WEEKS_2 = new ArrayList<>();
        RANKING = new ArrayList<>();
    }
    public static void initSports(Context context){
        if(SPORTS.isEmpty()){
            SPORTS.add(new SportModel(1, "Football")); SPORT_ICONS.put(1, R.raw.football);
            SPORTS.add(new SportModel(2, "Table Tennis")); SPORT_ICONS.put(2, R.raw.table_tennis);
            SPORTS.add(new SportModel(3, "Basketball")); SPORT_ICONS.put(3, R.raw.basketball);
            SPORTS.add(new SportModel(4, "Skiing")); SPORT_ICONS.put(4, R.raw.skiing);
            SPORTS.add(new SportModel(5, "Snowboarding")); SPORT_ICONS.put(5, R.raw.snowboarding);
            SPORTS.add(new SportModel(6, "American football")); SPORT_ICONS.put(6, R.raw.americanfootball);
            SPORTS.add(new SportModel(7, "Badminton")); SPORT_ICONS.put(7, R.raw.badminton);
            SPORTS.add(new SportModel(8, "Baseball")); SPORT_ICONS.put(8, R.raw.baseball);
            SPORTS.add(new SportModel(9, "Volleyball")); SPORT_ICONS.put(9, R.raw.volleyball);
            SPORTS.add(new SportModel(10, "Boxing")); SPORT_ICONS.put(10, R.raw.boxing);
            SPORTS.add(new SportModel(11, "Cricket")); SPORT_ICONS.put(11, R.raw.cricket);
            SPORTS.add(new SportModel(12, "Golf")); SPORT_ICONS.put(12, R.raw.golf);
            SPORTS.add(new SportModel(13, "Hockey"));  SPORT_ICONS.put(13, R.raw.hockey);
            SPORTS.add(new SportModel(14, "Ice hockey")); SPORT_ICONS.put(14, R.raw.hockey);
            SPORTS.add(new SportModel(15, "Tennis")); SPORT_ICONS.put(15, R.raw.tennis);
            SPORTS.add(new SportModel(16, "Esport")); SPORT_ICONS.put(16, R.raw.esports);
        }

        if(WEEKS.isEmpty()){
            WEEKS.add("Sunday");  WEEKS_2.add("SUN");
            WEEKS.add("Monday"); WEEKS_2.add("MON");
            WEEKS.add("Tuesday"); WEEKS_2.add("TUE");
            WEEKS.add("Wednesday"); WEEKS_2.add("WED");
            WEEKS.add("Thursday"); WEEKS_2.add("THU");
            WEEKS.add("Friday"); WEEKS_2.add("FRI");
            WEEKS.add("Saturday"); WEEKS_2.add("SAT");

        }
        //init weeks
        if(RANKING.isEmpty()){
            RANKING.add(new RankingModel(context.getString(R.string.not_ranking), R.drawable.ic_no_ranking));
            RANKING.add(new RankingModel(context.getString(R.string.bronze), R.drawable.bronze));
            RANKING.add(new RankingModel(context.getString(R.string.silver), R.drawable.silver));
            RANKING.add(new RankingModel(context.getString(R.string.gold), R.drawable.gold));
            RANKING.add(new RankingModel(context.getString(R.string.platinum), R.drawable.platinum));
            RANKING.add(new RankingModel(context.getString(R.string.diamond), R.drawable.diamond));
        }
    }
}
