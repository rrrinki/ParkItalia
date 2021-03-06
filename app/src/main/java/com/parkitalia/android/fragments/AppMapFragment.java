package com.parkitalia.android.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parkitalia.android.R;
import com.parkitalia.android.activities.BaseActivity;
import com.parkitalia.android.activities.LandingScreen;
import com.parkitalia.android.activities.LatLongModel;
import com.parkitalia.android.activities.LatlongmodelGet;
import com.parkitalia.android.activities.MyAccount;
import com.parkitalia.android.activities.SessionManager;
import com.parkitalia.android.locationsearch.AppPlace;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class AppMapFragment extends BaseFragment implements OnMapReadyCallback {
    public static final String MyPREFERENCES = "LocationLAtLng" ;
    double lat1, long1, lat2, long2,saveLat,saveLng;
    int maptype = 0;
    ImageView maptypeimage;
    LinearLayout findMyCarButton,myAccount;
    Button saveButton;
    SharedPreferences sharedpreferences;
    List<Polyline> polylines = new ArrayList<Polyline>();
    Polyline polylineFinal;
    TextView textDistance, nametext, markerAddress;
    ArrayList<LatlongmodelGet> listmodel = new ArrayList<LatlongmodelGet>();
    List<LatLng> pointsdecode = new ArrayList<LatLng>();
    List<LatLongModel> objList = new ArrayList<LatLongModel>();
    Geocoder geocoder;
   // String googleMap;
    String imageId;
    Marker marker;
    GoogleMap googleMap1;
    double lati,longi;
    String showImage;
    Double latitude= -34.0, longitude=151.0;
    List<android.location.Address> addresses;
    String link = "https://maps.googleapis.com/maps/api/directions/json?origin=Honda%202w%20Zonal%20office&destination=LANDMARK%20Designer%20Studio&mode=car&key=AIzaSyDzzJDHFB6rZ-GT56iqEgqY-wQSiU8t_f4";
    String link2 = "https://maps.googleapis.com/maps/api/directions/json?origin=43.272910,11.990023&destination=43.272901,11.984020&mode=car&key=AIzaSyDzzJDHFB6rZ-GT56iqEgqY-wQSiU8t_f4";

    private static final String KEY_PLACE = "KEY_PLACE";
    Polyline polyline23;
    String email;

    public static Fragment getFragment(AppPlace appPlace) {
        Bundle bundle = new Bundle();
        AppMapFragment receiversFragment = new AppMapFragment();
        if (appPlace != null) {
            bundle.putSerializable(KEY_PLACE, appPlace);
        }
        receiversFragment.setArguments(bundle);
        return receiversFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BaseActivity) getActivity();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = new SupportMapFragment();
//        FloatingActionButton buttonfloat= (FloatingActionButton) view.findViewById(R.id.fab);
//        buttonfloat.setVisibility(View.INVISIBLE);
        getChildFragmentManager().beginTransaction().add(R.id.map_container, mapFragment, "MAPS").commit();
        mapFragment.getMapAsync(this);
        maptypeimage = (ImageView) view.findViewById(R.id.clickmaptype);
        nametext = (TextView) view.findViewById(R.id.fragment_map_tv_take_me);
        saveButton = (Button) view.findViewById(R.id.button_save);
        markerAddress = (TextView) view.findViewById(R.id.fragment_map_tv_distance);
        findMyCarButton = (LinearLayout) view.findViewById(R.id.find_my_car_layout);
        myAccount = (LinearLayout) view.findViewById(R.id.linearlayout_account);
       // textDistance = (TextView) view.findViewById(R.id.fragment_map_tv_distence);
        ImageView textView = (ImageView) view.findViewById(R.id.back_button);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LandingScreen.class);
                intent.putExtra("Key_Data", "0");
                intent.putExtra("Key_Data1", "1");

                startActivity(intent);
            }
        });

        findMyCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* getMyLocation();*/
               SharedPreferences sharedpreferences1 = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                String saveLati=sharedpreferences1.getString("Lat","");
                String saveLongi=sharedpreferences1.getString("Lng","");
                try{
                saveLat=Double.parseDouble(saveLati);
                saveLng=Double.parseDouble(saveLongi);
                }catch(NumberFormatException e){}

                if(saveLati!=null && saveLongi!=null) {

                    googleMap1.addMarker(new MarkerOptions().position(new LatLng(saveLat, saveLng)).title("Your parked car location").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_red)));

                    googleMap1.getUiSettings().setMapToolbarEnabled(true);
                    // Zoom in, animating the camera.
                    googleMap1.animateCamera(CameraUpdateFactory.zoomIn());
                    // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                    googleMap1.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "First save your location", Toast.LENGTH_SHORT).show();
                }

                //  myCarClick();
               //  Toast.makeText(getActivity().getApplicationContext(),"You are already here",Toast.LENGTH_SHORT).show();

                // googleMap.setMyLocation
                // Enabled(true);
                // Check if we were successful in obtaining the map.

//                SharedPreferences shared = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
//                String LatString = (shared.getString("Lat", ""));
//                String LndString = (shared.getString("Lng", ""));
//                Log.e("Data",LatString+LndString);
//
//                Intent intent = new Intent(getContext(), AccomodationMapActivity.class);
//                intent.putExtra("lat", Double.valueOf(LatString));
//                intent.putExtra("lng", Double.valueOf(LndString));
//                startActivity(intent);
            }
        });

        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyAccount.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap1 =googleMap;

  /*      maptypeimage.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
                if (maptype % 2 == 0) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    maptypeimage.setImageResource(R.drawable.mapnormal);
                } else {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    maptypeimage.setImageResource(R.drawable.mapsatellite);
                }
 maptype++;
            }
       });*/
       // googleMap.setMyLocationEnabled(true);
        // Check if we were successful in obtaining the map.
        if (googleMap!= null) {
                LocationManager locationManager = (LocationManager) getContext()
                        .getSystemService(getContext().LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                lati = location.getLatitude();
                longi = location.getLongitude();

                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location arg0) {
                        // TODO Auto-generated method stub

                        /*googleMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("You").icon(BitmapDescriptorFactory.fromResource(R.drawable.mine)));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati, longi), 15));
                        // Zoom in, animating the camera.
                        googleMap.animateCamera(CameraUpdateFactory.zoomIn());*//**//*
                        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);*/

                    }
                });
            }

       googleMap.addMarker(new MarkerOptions().position(new LatLng(lati, longi)).title("You").icon(BitmapDescriptorFactory.fromResource(R.drawable.mine)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati, longi), 15));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());/**/
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        String id = user.get(SessionManager.KEY_ID);
        String lname = user.get(SessionManager.Key_Lname);
        String fname = user.get(SessionManager.KeY_Fname);
        email = user.get(SessionManager.KEY_EMAIL);
        String link23 = "http://indotesting.com/parkme/webservices/serachby_userlocation/get_datas";
        Log.e("DataLink", link23);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest jsObjRequest = new StringRequest(Request.Method.GET, link23,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        Log.d("jsonobject gggggg", "response" + response);
                        try {
                            JSONObject json = new JSONObject(response);
                            String details = json.getString("Details");
                            Log.d("jsonobject details", "response" + details);
                            JSONArray jsonArray = new JSONArray(details);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String id = jsonObject.getString("id");
                                String lat = jsonObject.getString("lat");
                                String lng = jsonObject.getString("lng");
                                String name = jsonObject.getString("name");
                                String type = jsonObject.getString("type");
                                String image=jsonObject.getString("image");
                                Log.e("Daoub", type);
                                listmodel.add(new LatlongmodelGet(id,Double.parseDouble(lat), Double.parseDouble(lng), name, type,image));
                                if (type.contains("MC") == true) {
                                    Log.e("Daoub", "MC");

                                    googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))).title(name)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mcicon)));
                                }
                                if (type.contains("F") == true) {
                                    Log.e("Daoub", "F");

                                    googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))).title(name).icon(BitmapDescriptorFactory.fromResource(R.drawable.ficon)));
                                }
                                if (type.contains("P") == true) {
                                    Log.e("Daoub", "P");


                                    googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))).title(name).icon(BitmapDescriptorFactory.fromResource(R.drawable.ppico)));
                                }
                                if (type.contains("RV") == true) {
                                    Log.e("Daoub", "RV");

                                    googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))).title(name).icon(BitmapDescriptorFactory.fromResource(R.drawable.rv)));
                                }
                                if (type.contains("D") == true) {
                                    Log.e("Daoub", "D");

                                    googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))).title(name).icon(BitmapDescriptorFactory.fromResource(R.drawable.dicon)));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
            }
        }
        );

        queue.add(jsObjRequest);
        googleMap.setOnMarkerClickListener(
                new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                googleMap.clear();
                addresses = null;
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove("Lat");
                editor.remove("Lng");
                editor.commit();
                Double lat = marker.getPosition().latitude;
                Double lng = marker.getPosition().longitude;
             //   showImage=forImage(lat,lng,listmodel);
               // com.squareup.picasso.Picasso.with(getContext().getApplicationContext()).
                 //       load("http://indotesting.com/parkme/uploads/parking/"+imageId+"/"+showImage).placeholder(R.mipmap.ic_launcher).into(maptypeimage);
                //     Bitmap bitmap= StringToBitMap(showImage);

                getAddressLatLng(lat, lng);
          /*      maptypeimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog settingsDialog = new Dialog(getActivity());

                        View v = getActivity().getLayoutInflater().inflate(R.layout.image_layout, null);


                        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        settingsDialog.setContentView(v);

                        ImageView iv= (ImageView) v.findViewById(R.id.profile_img_popup);
                        Bitmap bm=((BitmapDrawable)maptypeimage.getDrawable()).getBitmap();
                        iv.setImageBitmap(bm);
                        settingsDialog.show();
                    }
                });
*/
                for (int i = 0; i < listmodel.size(); i++) {
                    Log.e("Type Model", listmodel.get(i).getType());
                    if (listmodel.get(i).getType().contains("MC")) {
                        nametext.setText("Distance :" + listmodel.get(i).getName());
                     //   Log.e("ImageChoose",listmodel.get(i).getName()+"////"+listmodel.get(i).getImage());
                       // Picasso.with(getActivity().getApplicationContext()).load(listmodel.get(i).getImage()).into(maptypeimage);

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(listmodel.get(i).getLat(), listmodel.get(i).getLng()))
                                .title(listmodel.get(i).getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.mcicon)));
                        // getAddressLatLng(listmodel.get(i).getLat(),listmodel.get(i).getLng());
                    }
                    if (listmodel.get(i).getType().contains("F")) {
                        nametext.setText("Distance :" + listmodel.get(i).getName());

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(listmodel.get(i).getLat(), listmodel.get(i).getLng()))
                                .title(listmodel.get(i).getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ficon)));
                        // getAddressLatLng(listmodel.get(i).getLat(),listmodel.get(i).getLng());
                    }
                    if (listmodel.get(i).getType().contains("P")) {
                        nametext.setText("Distance :" + listmodel.get(i).getName());

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(listmodel.get(i).getLat(), listmodel.get(i).getLng()))
                                .title(listmodel.get(i).getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ppico)));
                        //getAddressLatLng(listmodel.get(i).getLat(),listmodel.get(i).getLng());
                    }
                    if (listmodel.get(i).getType().contains("D")) {
                        nametext.setText("Distance :" + listmodel.get(i).getName());

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(listmodel.get(i).getLat(), listmodel.get(i).getLng()))
                                .title(listmodel.get(i).getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.dicon)));
                        //getAddressLatLng(listmodel.get(i).getLat(),listmodel.get(i).getLng());
                    }
                    if (listmodel.get(i).getType().contains("RV")) {
                        nametext.setText("Distance :" + listmodel.get(i).getName());

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(listmodel.get(i).getLat(), listmodel.get(i).getLng()))
                                .title(listmodel.get(i).getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.rv)));
                        // getAddressLatLng(listmodel.get(i).getLat(),listmodel.get(i).getLng());
                    }
                }

                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lati, longi))
                        .title("You")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mine)));
                Log.e("long", "" + marker.getPosition().longitude);
                String link6 = "https://maps.googleapis.com/maps/api/directions/json?origin="+lati+","+longi+"&destination="+marker.getPosition().latitude+","+marker.getPosition().longitude+"&mode=car&key=AIzaSyAtUvQTirIhpDUe5XbOHogeSgzFymxv0oc";
                Log.e("Link 67", link6);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, link6, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Json Object", "response:=" + response);
                        try {
                            String routes = response.getString("routes");

                            JSONArray jsonArray = new JSONArray(routes);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String bounds = jsonObject.getString("bounds");
                                String legspoint = jsonObject.getString("legs");
                                JSONArray legsArr = new JSONArray(legspoint);
                                for (int j = 0; j < legsArr.length(); j++) {

                                    JSONObject legsobj = legsArr.getJSONObject(j);
                                    String distanceObj = legsobj.getString("distance");
                                    JSONObject jstextDistance = new JSONObject(distanceObj);
                                    String distancefinal = jstextDistance.getString("text");
                                    Log.e("Distance Meter", distancefinal);
                                    nametext.setText("Distance " + distancefinal);
                                }

                                String overviewpoints = jsonObject.getString("overview_polyline");
                                Log.e("Overviewpoints", overviewpoints);
                                JSONObject jsonObjectpoints = new JSONObject(overviewpoints);
                                String points = jsonObjectpoints.getString("points");
                                Log.e("points", points);
                                pointsdecode = decodePolyLine(points);


                                JSONObject jsonObject1 = new JSONObject(bounds);
                                String northeast = jsonObject1.getString("northeast");
                                JSONObject jsonObject2 = new JSONObject(northeast);
                                lat1 = jsonObject2.getDouble("lat");
                                double long1 = jsonObject2.getDouble("lng");
                                LatLng sydney2 = new LatLng(lat1, long1);


                                String southwest = jsonObject1.getString("southwest");
                                JSONObject jsonObject3 = new JSONObject(southwest);

                            }

                            Log.e("Pointdecode", pointsdecode.toString() + pointsdecode.size());
                            PolylineOptions polylineOptions = new PolylineOptions().
                                    geodesic(true).
                                    color(Color.BLUE).
                                    width(10);
                            for (int j = 0; j < pointsdecode.size(); j++) {
                                PolylineOptions polylineOptions2 = polylineOptions.add(pointsdecode.get(j));
                                polyline23 = googleMap.addPolyline(polylineOptions);
                            }
//
//                    polylineOptions.visible(false);
//                    Polyline line = mMap.addPolyline(new PolylineOptions()
//                            .add(new LatLng(location.getLatitude(), location.getLongitude()),
//                                    new LatLng(this.destinationLatitude, this.destinationLongitude))
//                            .width(1)
//                            .color(Color.DKGRAY)
                            polyline23.remove();

                            Log.e("Pointdecode Clear", pointsdecode.toString() + pointsdecode.size());


                            googleMap.addPolyline(polylineOptions);

                            Log.e("routes", routes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


// TODO Auto-generated method stub

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
// TODO Auto-generated method stub
                    }
                });
                queue.add(jsObjRequest);

                return false;
            }
        });


    }



    private void getMyLocation() {

        LocationManager locationManager = (LocationManager) getContext()
                .getSystemService(getContext().LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lati = location.getLatitude();
        longi = location.getLongitude();
        LatLng latLng = new LatLng(lati,longi);
        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        googleMap1.animateCamera(CameraUpdateFactory.zoomIn());
        */
        googleMap1.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    public String forImage(Double lat1, Double lng1, ArrayList<LatlongmodelGet> latlongmodelGets){
        String image=null;
        for(int i=0; i <latlongmodelGets.size();i++){


            if(lat1.equals(latlongmodelGets.get(i).getLat()) && lng1.equals(latlongmodelGets.get(i).getLng())) {
                image=latlongmodelGets.get(i).getImage();
                imageId = latlongmodelGets.get(i).getId();
                return image;
            }
        }
        return image;
    }

    public void getAddressLatLng(final Double lat, final Double lng) {
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                String placeName = strReturnedAddress.toString();

                markerAddress.setText(placeName);
                saveButton.setVisibility(View.VISIBLE);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("Lat", String.valueOf(lat));
                        editor.putString("Lng", String.valueOf(lng));
                        editor.commit();
                        Toast.makeText(getActivity().getApplicationContext(),"Your location has been saved", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

}
