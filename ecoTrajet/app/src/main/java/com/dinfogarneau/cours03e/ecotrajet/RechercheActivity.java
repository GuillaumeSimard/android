package com.dinfogarneau.cours03e.ecotrajet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;


public class RechercheActivity extends Activity {
    /** Called when the activity is first created. */

    //déclaration des variables.
    CalendarView calendarFirst;
    CalendarView calendarSecond;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);
        Button btn = (Button)this.findViewById(R.id.btnRechercher);
        calendarFirst = (CalendarView) findViewById(R.id.calendar);
        calendarSecond = (CalendarView) findViewById(R.id.idCalendrier);


        //initialisation des calendriers
        initializeCalendar(calendarFirst);
        initializeCalendar(calendarSecond);
    }

    //Méthode permettant d'initiaiser le calendrier
    public void initializeCalendar(CalendarView cal) {

        //initialisation du calendrier
        cal.setShowWeekNumber(false);
        cal.setFirstDayOfWeek(2);
        cal.setSelectedWeekBackgroundColor(getResources().getColor(R.color.green));
        cal.setUnfocusedMonthDateColor(getResources().getColor(R.color.transparent));
        cal.setWeekSeparatorLineColor(getResources().getColor(R.color.transparent));
        cal.setSelectedDateVerticalBar(R.color.darkgreen);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {

                Toast.makeText(getApplicationContext(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
            }

        });


    }


    public void onClickRechercher(View v) {
        Intent intent = new Intent(this, ResultatRechercheActivity.class);
        this.startActivity(intent);
    }

}