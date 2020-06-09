package com.example.myapplication.location;

import com.example.myapplication.Beacons;
import com.example.myapplication.model.Position;
import com.example.myapplication.util.FilteredSignals;
import com.example.myapplication.util.Util;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

public class Multilateration {

    public static Position calcutateLocation(FilteredSignals filteredSignal) {

        synchronized (Util.beaconsMap){
            double[][] positions = new double[Util.beaconsMap.size()][2];
            double[] distances = new double[Util.beaconsMap.size()];

            int index = 0;

            for (Beacons beacons : Util.beaconsMap.values()) {

                positions[index][0] = beacons.getLat();
                positions[index][1] = beacons.getLng();

                switch (filteredSignal){
                    case UNFILTERED:
                        distances[index] = beacons.getRssiDist();
                        break;
                    case MEAN:
                        distances[index] = beacons.getMeanDist();
                        break;
                    case KALMAN:
                        distances[index] = beacons.getKalmanDist();
                        break;
                    case ARMA:
                        distances[index] = beacons.getArmaDist();
                        break;
                }
                index++;
            }

            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
            LeastSquaresOptimizer.Optimum optimum = solver.solve();

            // the answer
            double[] calculatedPosition = optimum.getPoint().toArray();

            // error and geometry information
//        RealVector standardDeviation = optimum.getSigma(0);
//        RealMatrix covarianceMatrix = optimum.getCovariances(0);

            // Log.i("MEAN_POSITION", "x: " + calculatedPosition[0] + " " + "y: " + calculatedPosition[1]);
            return new Position(calculatedPosition[0], calculatedPosition[1]);
        }
    }


}
