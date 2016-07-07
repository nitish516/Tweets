package com.example.nitish.tweets;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private List<Tweet> tweetList;

    private HttpService httpService;

    private Map<String, Integer> frequencyMap;

    private RecyclerView recyclerView;

    private ProgressBar progressBar;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        tweetList = new ArrayList<Tweet>();
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(tweetList));

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        textView = (TextView) findViewById(R.id.fwords_text);

        httpService = new TwitterHttpService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNetwork();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(tweetList));
    }

    List<String> getMostFrequent(List<Tweet> tweets){
        Iterator<Tweet> itr = tweets.iterator();
        while(itr.hasNext())
        {
            String[] words = itr.next().getText().split(" ");
            addWordsToMap(words);
        }

        Set<String> ketSet = frequencyMap.keySet();
        Iterator<String> iterator = ketSet.iterator();
        int highest = 0;
        int second = 0;
        int third = 0;
        String highestWord = "";
        String secondWord = "";
        String thirdWord = "";
        while (iterator.hasNext()){
            String word = iterator.next();
            int freq = frequencyMap.get(word);
            if(freq <= third)
                continue;
            if(freq > highest){
                third = second;
                thirdWord = secondWord;
                second = highest;
                secondWord = highestWord;
                highest = freq;
                highestWord = word;
                continue;
            }
            if(freq > second){
                third = second;
                thirdWord = secondWord;
                second = freq;
                secondWord = word;
                continue;
            }
            if(freq>third){
                third=freq;
                thirdWord = word;
            }
        }
        return Arrays.asList(highestWord, secondWord, thirdWord);
    }

    private void addWordsToMap(String[] words) {
        for(int i=0;i<words.length;i++){
            if(frequencyMap.containsKey(words[i])) {
                frequencyMap.put(words[i], frequencyMap.get(words[i]) + 1);
            }
            else {
                frequencyMap.put(words[i], 1);
            }
        }
    }

    private void checkNetwork(){
        ConnectivityManager cm = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean network_connected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (!network_connected)
            onDetectNetworkState().show();
        else{
            AsyncTask task = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        return httpService.getData();
                    }catch (IOException ioe) {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    progressBar.setVisibility(View.GONE);
                    if (o != null){
                        tweetList = (List<Tweet>) o;
                        setupRecyclerView(recyclerView);
                        frequencyMap = new HashMap<>();
                        List<String> frequentWords = getMostFrequent(tweetList);
                        Log.i("info", frequentWords.get(0));
                        Log.i("info", frequentWords.get(1));
                        Log.i("info", frequentWords.get(2));
                        textView.setText("Most frequent words: " + frequentWords.get(0) + ", "
                                + frequentWords.get(1) + ", "
                                + frequentWords.get(2));
                    }
                    else{
                        textView.setText("Error fetching the tweets!");
                    }
                 }
            };
            task.execute();
        }
    }

    public AlertDialog onDetectNetworkState(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Network unavailable")
                .setTitle("Open Setting")
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        MainActivity.this.finish();
                    }
                })
                .setPositiveButton("Οκ",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
        return builder1.create();
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Tweet> mValues;

        public SimpleItemRecyclerViewAdapter(List<Tweet> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).getText());
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public Tweet mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

}
