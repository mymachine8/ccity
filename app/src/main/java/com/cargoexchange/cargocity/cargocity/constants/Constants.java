package com.cargoexchange.cargocity.cargocity.constants;

import android.location.LocationManager;

public class Constants
{
    public static final String CARGO_API_BASE_URL  = "http://10.0.3.2:9000/api/";
    public static final String CARGO_API_LOCAL_URL = "http://10.0.3.2:9000/api/";
    public static final String CARGO_API_DEV = "http://api.cargocity.in/api/";
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
    public static final int PERMISSION_ACCESS_LOCATION=1;
    public static final int PERMISSION_EXTERNAL_STORAGE=2;
    public static final String LOCATION_PROVIDER= LocationManager.GPS_PROVIDER;
    public static final String NETWORK_LOCATION_PROVIDER=LocationManager.NETWORK_PROVIDER;
    public static final int TWO_MINUTES = 1000 * 60 * 2;
    public static final int LOCATION_SETTINGS_ACTION=2;
    public static final int SOCKET_TIMEOUT_MS = 5000;
}
