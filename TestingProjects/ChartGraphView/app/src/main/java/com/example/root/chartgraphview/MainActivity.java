package com.example.root.chartgraphview;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RealtimeUpdatesFragment.OnFragmentInteractionListener {

    Graph graphLogic = new Graph(this);
    GraphView graph;
    public static final String ARG_SECTION_NUMBER = "";

    public void onSectionAttached(int number) {
        ArrayList<String> items = new ArrayList<String>();
        switch (number) {
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graph = (GraphView) findViewById(R.id.graph);
        graphLogic.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        graphLogic.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        graphLogic.onPause();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
