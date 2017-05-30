package com.uni.pip.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.google.gson.reflect.TypeToken;
import com.uni.pip.R;
import com.uni.pip.adapter.TruckAdapter;
import com.uni.pip.config.Parameters;
import com.uni.pip.datalayer.models.Comment;
import com.uni.pip.datalayer.models.Destination;
import com.uni.pip.datalayer.models.Event;
import com.uni.pip.datalayer.models.Truck;
import com.uni.pip.datalayer.models.User;
import com.uni.pip.datalayer.server.GetRequestModel;
import com.uni.pip.datalayer.server.MyHttpClient;
import com.uni.pip.datalayer.server.RequestDataProvider;
import com.uni.pip.datalayer.server.ServerResponseHandler;
import com.uni.pip.security.SecurePreferences;
import com.uni.pip.ui.MainActivity;
import com.uni.pip.utilities.Methods;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik;

/**
 * Created by sammy on 2/28/2017.
 */

public class TruckEntryExitFragment extends Fragment implements View.OnClickListener {

    private MyHttpClient myHttpClient;

    private RecyclerView recyclerView;
    private Button add;
    private TextView header;
    private ArrayList<Integer> destinationNumbers = new ArrayList<>();
    private int entranceCount = 0;
    private int wadi = 0;
    private int akaidarWaste = 0;
    private int akaiderSludge = 0;
    private int zaatari = 0;

    public static TruckEntryExitFragment newInstance(Event event) {
        TruckEntryExitFragment fragment = new TruckEntryExitFragment();
        Bundle b = new Bundle();
        b.putParcelable(Parameters.TYPE_MEASUREMENT, event);
        fragment.setArguments(b);
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_truck_exit_entry, container, false);
        if (getActivity() != null)
            ((MainActivity) getActivity()).setTitleToolbar("نقطة التفتيش");
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        add = (Button) layout.findViewById(R.id.add);
        header = (TextView) layout.findViewById(R.id.header);

        add.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isNetworkAvailable() && !TextUtils.isEmpty(SecurePreferences.getInstance(getActivity()).getString(Parameters.GET)))
            try {
                getTrucksEntered();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        else getStored();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void getTrucksEntered() throws JSONException, UnsupportedEncodingException {
        if (getActivity() != null)
            ((MainActivity) getActivity()).progressBar.setVisibility(View.VISIBLE);
        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(getActivity());
        GetRequestModel requestModel = requestDataProvider.getRemainingTrucks(
                SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_NUMBER),
                SecurePreferences.getInstance(getActivity()).getString(Parameters.PASSWORD),
                Methods.getCurrentTimeStampApi()
        );
        Type type = new TypeToken<ArrayList<Event>>() {
        }.getType();

        myHttpClient.get(getActivity(), requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<ArrayList<Event>>(type) {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onConnectivityError(String message) {
                if (getActivity() != null)
                    ((MainActivity) getActivity()).progressBar.setVisibility(View.GONE);

                getStored();
            }

            @Override
            public void onDataError(String message) {
                if (getActivity() != null)
                    ((MainActivity) getActivity()).progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onServerFailure(String message) {
                if (getActivity() != null)
                    ((MainActivity) getActivity()).progressBar.setVisibility(View.GONE);

            }


            @Override
            public void onServerSuccess(ArrayList<Event> data) {
                if (data != null) {
                    insert(data);
                } else {
                    if (getActivity() != null)
                        ((MainActivity) getActivity()).progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void getStored() {
        ArrayList<Event> getAllEvents = (ArrayList<Event>) Event.getAll();
        ArrayList<Event> data = null;
        if (getAllEvents != null && !getAllEvents.isEmpty()) {
            data = getAllEvents;
        }


        ArrayList<User> myUsers = new ArrayList<>();
        if (data != null && data != null && !data.isEmpty())
            for (int i = 0; i < data.size(); i++) {
                User user = User.getUserById(data.get(i).getDriver_user_Id());
                myUsers.add(user);
            }

        ArrayList<Truck> myTrucks = new ArrayList<>();
        if (data != null && data != null && !data.isEmpty())
            for (int i = 0; i < data.size(); i++) {
                Truck truck = Truck.getTruckById(data.get(i).getTruck_Id());
                if (truck != null)
                    myTrucks.add(truck);
            }


        int size = 0;

        ArrayList<Destination> entranceDesti = new ArrayList<>();

        List<Destination> allDestinations = Destination.getall();
        if (data != null)
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < allDestinations.size(); j++) {
                    if (allDestinations.get(j).getDestination_Id().equals(data.get(i).getDestination_Id()))
                        entranceDesti.add(i, allDestinations.get(j));
                }

            }
        if (!entranceDesti.isEmpty()) {
            for (int i = 0; i < entranceDesti.size(); i++) {
                if (entranceDesti.get(i).getDestination_Id().equals("a7be1e26-2042-11e7-a0e7-002590d1d12a"))
                    wadi++;
                else if (entranceDesti.get(i).getDestination_Id().equals("af54487e-1b99-11e7-a0e7-002590d1d12a"))
                    akaidarWaste++;
                else if (entranceDesti.get(i).getDestination_Id().equals("d719b3be-1b99-11e7-a0e7-002590d1d12a"))
                    akaiderSludge++;
                else zaatari++;

            }
        }
        if (data != null && data != null && !data.isEmpty()) {
            header.setText(Html.fromHtml("<b>" + getString(R.string.wadi) + ":</b> " + wadi + " &#160;  " +
                    "" + "<b>" + getString(R.string.akaider_waste) + ":</b> " + akaidarWaste + " &#160;  " +
                    "" + "<b>" + getString(R.string.akaider_sludge) + ":</b> " + akaiderSludge + " &#160;  " +
                    "" + "<b>" + getString(R.string.zaatari) + ":</b> " + zaatari + " &#160;  "
            ));
        }

        if (data != null && !data.isEmpty()) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            TruckAdapter truckAdapter = new TruckAdapter(getActivity(), data, myUsers, myTrucks, entranceDesti);
            recyclerView.setAdapter(truckAdapter);
            recyclerView.setLayoutManager(linearLayoutManager);

            truckAdapter.setOnClick(new TruckAdapter.onClick() {
                @Override
                public void OnClick(Event event, Truck truck, User user) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().add(R.id.content_layout, AddTruckFragment.newInstance(
                            event
                            ), AddTruckFragment.class.getSimpleName()
                    ).addToBackStack(AddTruckFragment.class.getSimpleName()).commitAllowingStateLoss();
                }
            });
            truckAdapter.sort();
        }
        if (getActivity() != null)
            ((MainActivity) getActivity()).progressBar.setVisibility(View.GONE);

    }

    private void insert(ArrayList<Event> newData) {
        Truck t = null;
        String truckName = "";
        List<Destination> ad = Destination.getall();
        SecurePreferences.getInstance(getActivity()).put(Parameters.GET, "");
        String destinationName = "";
        ActiveAndroid.beginTransaction();


        List<Event> exits = Event.getAll();
        if(exits!=null && !exits.isEmpty())
        for(int i =0; i<exits.size(); i++){
            Event.delete(Event.class, exits.get(i).getId());
        }
        List<Comment> comments = Comment.getAll();
        if(comments!=null && !comments.isEmpty())
            for(int i =0; i<comments.size(); i++){
                Comment.delete(Comment.class, comments.get(i).getId());
            }

        List<Truck> getTrucks = Truck.getAll();
        List<User> getUser = User.getAll();
        User user = new User();
        try {
            if (newData != null && !newData.isEmpty())
                for (int i = 0; i < newData.size(); i++) {
                    for (int l = 0; l < getUser.size(); l++) {
                        if (getUser.get(l).getUser_Id().equals(newData.get(i).getDriver_user_Id()))
                            user = getUser.get(l);
                    }
                    for (int j = 0; j < getTrucks.size(); j++) {
                            if(getTrucks.get(j).getTruck_Id().equals(newData.get(i).getTruck_Id()))
                                t= getTrucks.get(j);

                    }
                    truckName = t.getTruck_number();

                    for (int m = 0; m < ad.size(); m++) {
                        if (ad.get(m).getDestination_Id().equals(newData.get(i).getDestination_Id()))
                            destinationName = ad.get(m).getDestination_ar();
                    }
                    String commentId = "";
                    if(newData.get(i).getComment_Id()==null)
                        commentId = "";
                    else commentId = newData.get(i).getComment_Id();

                    Event event = new Event(newData.get(i).getEvent_Id(),
                            newData.get(i).getTimestamp(), newData.get(i).getDriver_user_Id(),
                            newData.get(i).getTruck_Id(),
                            newData.get(i).getController_user_Id(),
                            newData.get(i).getDestination_Id(),
                            newData.get(i).getPayment_voucher_number(),
                            newData.get(i).getTruck_volume(),
                            commentId,
                            truckName, user.getFirst_name_ar() + " " + user.getLast_name_ar(),
                            "", user.getUsername(), "0",
                            destinationName, newData.get(i).getTime_user());
                    event.save();
                }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }


        if (getActivity() != null)
            ((MainActivity) getActivity()).progressBar.setVisibility(View.GONE);

        ArrayList<Event> getAllEvents = (ArrayList<Event>) Event.getAll();
        ArrayList<Event> data = null;
        if (getAllEvents != null && !getAllEvents.isEmpty()) {
            data = getAllEvents;
        }

        ArrayList<User> myUsers = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            User usera = User.getUserById(data.get(i).getDriver_user_Id());
            myUsers.add(usera);
        }

        ArrayList<Truck> entranceTrucks = new ArrayList<>();
        ArrayList<Truck> newTrucks = new ArrayList<>();
        List<Truck> allTrucks = Truck.getAll();
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < allTrucks.size(); j++) {
                if (allTrucks.get(j).getTruck_Id().equals(data.get(i).getTruck_Id()))
                    entranceTrucks.add(i, allTrucks.get(j));
            }

        }
        ArrayList<Destination> entranceDesti = new ArrayList<>();

        List<Destination> allDestinations = Destination.getall();
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < allDestinations.size(); j++) {
                if (allDestinations.get(j).getDestination_Id().equals(data.get(i).getDestination_Id()))
                    entranceDesti.add(i, allDestinations.get(j));
            }

        }
        ArrayList<Truck> myTrucks = new ArrayList<>();
        if (!data.isEmpty())
            for (int i = 0; i < data.size(); i++) {
                Truck truck = Truck.getTruckById(data.get(i).getTruck_Id());
                if (truck != null)
                    myTrucks.add(truck);
            }
        if (!entranceDesti.isEmpty()) {
            for (int i = 0; i < entranceDesti.size(); i++) {
                if (entranceDesti.get(i).getDestination_Id().equals("a7be1e26-2042-11e7-a0e7-002590d1d12a"))
                    wadi++;
                else if (entranceDesti.get(i).getDestination_Id().equals("af54487e-1b99-11e7-a0e7-002590d1d12a"))
                    akaidarWaste++;
                else if (entranceDesti.get(i).getDestination_Id().equals("d719b3be-1b99-11e7-a0e7-002590d1d12a"))
                    akaiderSludge++;
                else zaatari++;

            }
        }
        if (data != null && data != null && !data.isEmpty()) {
            header.setText(Html.fromHtml("<b>" + getString(R.string.wadi) + ":</b> " + wadi + " &#160;  " +
                    "" + "<b>" + getString(R.string.akaider_waste) + ":</b> " + akaidarWaste + " &#160;  " +
                    "" + "<b>" + getString(R.string.akaider_sludge) + ":</b> " + akaiderSludge + " &#160;  " +
                    "" + "<b>" + getString(R.string.zaatari) + ":</b> " + zaatari + " &#160;  "
            ));
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        TruckAdapter truckAdapter = new TruckAdapter(getActivity(), data, myUsers, myTrucks, entranceDesti);
        recyclerView.setAdapter(truckAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        truckAdapter.setOnClick(new TruckAdapter.onClick() {
            @Override
            public void OnClick(Event event, Truck truck, User user) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.content_layout, AddTruckFragment.newInstance(
                        event
                        ), AddTruckFragment.class.getSimpleName()
                ).addToBackStack(AddTruckFragment.class.getSimpleName()).commitAllowingStateLoss();
            }
        });

        truckAdapter.sort();
        if (getActivity() != null)
            ((MainActivity) getActivity()).progressBar.setVisibility(View.GONE);
    }

    private void setError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                if (getActivity() != null)
                    if (Methods.isAutomaticTimeEnabled(getActivity())) {
                        ((MainActivity) getActivity()).setTitle("سجل دخول");
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().add(R.id.content_layout, new AddTruckFragment(),
                                AddTruckFragment.class.getSimpleName()).addToBackStack(AddTruckFragment.class
                                .getSimpleName()).commitAllowingStateLoss();
                    } else ((MainActivity) getActivity()).displayAutomaticTimeEnable();
                break;
            default:
                break;
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if(getActivity()!=null && ((MainActivity)getActivity()).progressBar!=null)
//        ((MainActivity) getActivity()).progressBar.setVisibility(View.INVISIBLE);
//
//    }
}
