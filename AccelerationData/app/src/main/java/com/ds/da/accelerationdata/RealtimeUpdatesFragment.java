package com.ds.da.accelerationdata;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

import static android.content.Context.SENSOR_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RealtimeUpdatesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RealtimeUpdatesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RealtimeUpdatesFragment extends Fragment implements SensorEventListener {
    private SensorManager sensorManager;
    private double magnitude;
    private double x;
    private double y;
    private double z;

    private final Handler mHandler = new Handler();
    private Runnable mTimer2;
    private LineGraphSeries<DataPoint> mSeries2;
    private LineGraphSeries<DataPoint> mSeriesX;
    private LineGraphSeries<DataPoint> mSeriesY;
    private LineGraphSeries<DataPoint> mSeriesZ;
    private double graph2LastXValue = 5d;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RealtimeUpdatesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RealtimeUpdatesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RealtimeUpdatesFragment newInstance(String param1, String param2) {
        RealtimeUpdatesFragment fragment = new RealtimeUpdatesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Context context = getActivity();
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_realtime_updates, container, false);


        GraphView graph2 = (GraphView) rootView.findViewById(R.id.FragmentGraph2);
        mSeries2 = new LineGraphSeries<>();
        mSeriesX = new LineGraphSeries<>();
        mSeriesY = new LineGraphSeries<>();
        mSeriesZ = new LineGraphSeries<>();
        mSeries2.setColor(Color.rgb(237, 75, 41));
        mSeriesX.setColor(Color.rgb(70, 230, 25));
        mSeriesY.setColor(Color.rgb(29, 79, 185));
        mSeriesZ.setColor(Color.rgb(214, 52, 126));

        mSeries2.setThickness(2);
        mSeriesX.setThickness(2);
        mSeriesY.setThickness(2);
        mSeriesZ.setThickness(2);

        graph2.setBackgroundColor(Color.rgb(255, 255, 255));

        graph2.addSeries(mSeries2);
        graph2.addSeries(mSeriesX);
        graph2.addSeries(mSeriesY);
        graph2.addSeries(mSeriesZ);
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(-100);
        graph2.getViewport().setMaxX(100);

        graph2.getViewport().setYAxisBoundsManual(true);
        graph2.getViewport().setMinY(-30);
        graph2.getViewport().setMaxY(30);

        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(4);
        graph2.getViewport().setMaxX(80);

        graph2.getViewport().setScalable(true);
        graph2.getViewport().setScrollable(true);
        graph2.getViewport().setScalableY(true);
        graph2.getViewport().setScrollableY(true);

        return rootView;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //magnitude = Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);


            // In this example, alpha is calculated as t / (t + dT),
            // where t is the low-pass filter's time-constant and
            // dT is the event delivery rate.

            final float alpha = 0.015f;

            // Isolate the force of gravity with the low-pass filter.
            float[] gravity = new float[3];
            float[] linear_acceleration = new float[3];
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            magnitude = Math.sqrt(linear_acceleration[0] * linear_acceleration[0] + linear_acceleration[1] * linear_acceleration[1] + linear_acceleration[2] * linear_acceleration[2]);
            //magnitude = Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);


        mTimer2 = new Runnable() {
            @Override
            public void run() {
                graph2LastXValue += 1d;
                mSeries2.appendData(new DataPoint(graph2LastXValue, magnitude), true, 40);
                mSeriesX.appendData(new DataPoint(graph2LastXValue, x), true, 40);
                mSeriesY.appendData(new DataPoint(graph2LastXValue, y), true, 40);
                mSeriesZ.appendData(new DataPoint(graph2LastXValue, z), true, 40);
                mHandler.postDelayed(this, 200);
            }
        };
        mHandler.postDelayed(mTimer2, 1000);
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer2);
        super.onPause();
    }
}