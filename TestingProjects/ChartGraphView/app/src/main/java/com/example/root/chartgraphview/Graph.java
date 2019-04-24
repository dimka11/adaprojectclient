package com.example.root.chartgraphview;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static android.content.Context.SENSOR_SERVICE;

public class Graph implements SensorEventListener {
    private MainActivity mainActivity;
    GraphView graph;

    private SensorManager sensorManager;
    private ArrayList<LineGraphSeries<DataPoint>> series = new ArrayList<>();
    private static ArrayList<Double> currentX = new ArrayList<>();
    private LinkedBlockingQueue<ArrayList<Double>> accelerationQueue;
    private ArrayList<Double> accelerationDataArray = new ArrayList<>();

    Graph(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        accelerationQueue = new LinkedBlockingQueue<>(10);
    }

    void onCreate() {
        sensorManager = (SensorManager) mainActivity.getSystemService(SENSOR_SERVICE);
        graph = mainActivity.graph;

        SetGraph();

        currentX.addAll(Arrays.asList(0.0, 0.0, 0.0, 0.0));

        // Start chart thread
        ThreadPoolExecutor liveChartExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        if (liveChartExecutor != null)
            liveChartExecutor.execute(new AccelerationChart(new AccelerationChartHandler()));
    }

    private void SetGraph() {
        series.add((new LineGraphSeries<DataPoint>()));
        series.add((new LineGraphSeries<DataPoint>()));
        series.add((new LineGraphSeries<DataPoint>()));
        series.add((new LineGraphSeries<DataPoint>()));

        series.get(0).setColor(Color.YELLOW);
        series.get(1).setColor(Color.RED);
        series.get(2).setColor(Color.BLUE);
        series.get(3).setColor(Color.GREEN);
        graph.addSeries(series.get(0));
        graph.addSeries(series.get(1));
        graph.addSeries(series.get(2));
        graph.addSeries(series.get(3));

        // activate horizontal zooming and scrolling
        graph.getViewport().setScalable(true);

        // activate horizontal scrolling
        graph.getViewport().setScrollable(true);

        // activate horizontal and vertical zooming and scrolling
        graph.getViewport().setScalableY(true);

        // activate vertical scrolling
        graph.getViewport().setScrollableY(true);
        // To set a fixed manual viewport use this:
        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.5);
        graph.getViewport().setMaxX(6.5);

        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-10);
        graph.getViewport().setMaxY(10);

        graph.setBackgroundColor(Color.rgb(252, 216, 189));

        graph.getGridLabelRenderer().setNumVerticalLabels(10);
    }

    void onResume() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(sensorEvent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    void onPause() {
        sensorManager.unregisterListener(this);
        resetDataCharts();
    }

    private void resetDataCharts() {
        series.get(0).resetData(new DataPoint[]{});
        series.get(1).resetData(new DataPoint[]{});
        series.get(2).resetData(new DataPoint[]{});
        series.get(3).resetData(new DataPoint[]{});
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        double x = values[0];
        double y = values[1];
        double z = values[2];

        double accelerationSquareRoot = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        double acceleration = Math.sqrt(accelerationSquareRoot);
        accelerationDataArray.clear();
        accelerationDataArray.addAll(Arrays.asList(acceleration, x, y, z));
        accelerationQueue.offer(accelerationDataArray);
    }

    private class AccelerationChartHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            ArrayList<Double> accelerationY = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0));

            accelerationY.set(0, (msg.getData().getDouble("ACCELERATION_VALUE")));
            accelerationY.set(1, msg.getData().getDouble("ACCELERATION_X"));
            accelerationY.set(2, msg.getData().getDouble("ACCELERATION_Y"));
            accelerationY.set(3, msg.getData().getDouble("ACCELERATION_Z"));


            series.get(0).appendData(new DataPoint(currentX.get(0), accelerationY.get(0)), true, 10);
            series.get(1).appendData(new DataPoint(currentX.get(1), accelerationY.get(1)), true, 10);
            series.get(2).appendData(new DataPoint(currentX.get(2), accelerationY.get(2)), true, 10);
            series.get(3).appendData(new DataPoint(currentX.get(3), accelerationY.get(3)), true, 10);
            currentX.set(0, currentX.get(0) + 1);
            currentX.set(1, currentX.get(1) + 1);
            currentX.set(2, currentX.get(2) + 1);
            currentX.set(3, currentX.get(3) + 1);
        }
    }

    private class AccelerationChart implements Runnable {
        private boolean drawChart = true;
        private Handler handler;

        AccelerationChart(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            while (drawChart) {
                Double accelerationY;
                Double accelerationY0;
                Double accelerationY1;
                Double accelerationY2;
                try {
                    Thread.sleep(300); // Speed up the X axis
                    accelerationY = accelerationQueue.poll().get(0);
                    accelerationY0 = accelerationQueue.poll().get(1);
                    accelerationY1 = accelerationQueue.poll().get(2);
                    accelerationY2 = accelerationQueue.poll().get(3);
                } catch (InterruptedException | java.lang.NullPointerException | java.lang.IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    continue;
                }

                // currentX value will be excced the limit of double type range
                // To overcome this problem comment of this line
                // currentX = (System.currentTimeMillis() / 1000) * 8 + 0.6;

                Message msgObj = handler.obtainMessage();
                Bundle b = new Bundle();
                b.putDouble("ACCELERATION_VALUE", accelerationY);
                b.putDouble("ACCELERATION_X", accelerationY0);
                b.putDouble("ACCELERATION_Y", accelerationY1);
                b.putDouble("ACCELERATION_Z", accelerationY2);
                msgObj.setData(b);
                handler.sendMessage(msgObj);
            }
        }

    }

}