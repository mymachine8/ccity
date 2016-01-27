package com.cargoexchange.cargocity.cargocity.constants;

/**
 * Created by thespidy on 18/01/16.
 */
public class Constants
{
    public static final String CARGO_API_BASE_URL  = "http://10.0.3.2:9000/api/";
    public static final String GOOGLE_MAP_MATRIX_API_BASE_URL="https://maps.googleapis.com/maps/api/distancematrix/json?";
    public static final String GOOGLE_MAP_DIRECTIONS_API_BASE_URL="https://maps.googleapis.com/maps/api/directions/json?";
    public static final String GOOGLE_MAP_SERVER_KEY="AIzaSyAa_HArC674Ruuyw91n2jNvntSoaIPyZ64";
    public static final String MAP_TRAFFICMODEL_PESSIMISTIC = "pessimistic";
    public static final String MAP_TRAFFICMODEL_OPTIMISTIC = "optimistic";
    public static final String MAP_TRAFFICMODEL_BESTGUESS = "best_guess";
    public static final String MAP_DEPARTURETIME = "now";
    public static final String MAP_TRANSTMODE = "bus";
    public static final int LOCATION_MAX_TIME=50000;
    public static final int LOCATION_MAX_DISTANCE=500;
}
