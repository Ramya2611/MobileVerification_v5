package com.example.kavi.mobileverification;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
import com.apollographql.apollo.exception.ApolloException;

import java.io.File;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;




/**
 * A simple {@link Fragment} subclass.
 */
public class Cashout extends Fragment {

   private TextView amount;

    public ApolloClient apolloClient;

   public Integer pwallet;


    public Cashout() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        SharedPreferences sp = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);

        String mobile = sp.getString("mobile",null);

        View rootView=inflater.inflate(R.layout.fragment_cashout, container, false);

         amount = rootView.findViewById(R.id.cash_amo);


        File file = new File(getActivity().getCacheDir().toURI());
        //Size in bytes of the cache
        int size = 1024*1024;

        //Create the http response cache store
        DiskLruHttpCacheStore cacheStore = new DiskLruHttpCacheStore(file, size);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        apolloClient = ApolloClient.builder()
                .serverUrl("https://digicashserver.herokuapp.com/graphql")
                .httpCache(new ApolloHttpCache(cacheStore))
                .okHttpClient(okHttpClient)
                .build();


        apolloClient
                .query(PersondetailsQuery.builder().mobileno(mobile).build())
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
                .enqueue(new ApolloCall.Callback<PersondetailsQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<PersondetailsQuery.Data> response) {


                        PersondetailsQuery.Data data = response.data();

                        if(data!=null){
                            Log.d("msg","cash out");
                        }


                            pwallet = Integer.parseInt(data.person().get(0).wallet.toString());

                            Log.d("datas",Integer.toString(pwallet));

//
//                        final boolean keepRunning1 = true;
//                        Thread thread_two = new Thread(){
//
//                            @Override
//                            public void run(){
//
//                                while(keepRunning1){
//
//                                    // Make the thread wait half a second. If you want...
//                                    try {
//                                        Thread.sleep(500);
//                                    } catch (InterruptedException e) {
//                                        Toast.makeText(getActivity().getApplicationContext(), "Default Signature                         Fail", Toast.LENGTH_LONG).show();
//                                        e.printStackTrace();
//                                    }
//
//                                    // here you check the value of getActivity() and break up if needed
//                                    if(getActivity() == null)
//                                        return;

                                    getActivity().runOnUiThread(new Runnable(){
                                        @Override
                                        public void run(){

                                            amount.setText(String.valueOf(pwallet));

                                        }
                                    });
                                }
//                            }
//                        };thread_two.start();
//
//                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                        Log.e("Fail", "onFailure: ",e );

                    }
                });


        return rootView;

    }





}
