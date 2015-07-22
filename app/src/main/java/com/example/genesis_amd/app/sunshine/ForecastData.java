package com.example.genesis_amd.app.sunshine;

import java.util.ArrayList;

class ForecastDay
{
    private long m_date;
    private double m_tempMin;
    private double m_tempMax;
    private double m_tempNight;
    private double m_tempEvening;
    private double m_tempMorning;
    private double m_pressure;
    private long   m_humidity;
}

/**
 * Created by GENESIS-AMD on 3/29/2015.
 */
public class ForecastData
{
    public String location() { return m_location; }
    public String location(String location) { return m_location = location; }

    public String country() { return m_country; }
    public String country(String country) { return m_country = country; }

    public double latitude() { return m_latitude; }
    public double latitude(double latitude) { return m_latitude = latitude; }

    public long numDays() { return m_forecastDays.size(); }
    public void addDay(ForecastDay day) { m_forecastDays.add(day); }
    public ForecastDay day(int index) { return m_forecastDays.get(index); }

    private String m_location;
    private String m_country;

    private double m_latitude;
    private double m_longitude;
    private ArrayList<ForecastDay> m_forecastDays;
}
