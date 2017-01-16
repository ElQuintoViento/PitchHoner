package com.thorson.adam.pitchhoner;

import android.util.Log;

/**
 * Created by tor on 1/15/17.
 */
public class Helpers {
    private static final String TAG = Helpers.class.getSimpleName();

    // Uses continued fractions
    private static int[] computeReducedFraction(double number, int level){
        int[] tuple = new int[]{0, 0};
        int integerPart = (int)number;
        double decimalPart = number - ((double)integerPart);

        if (decimalPart == 0.0){
            tuple = new int[]{integerPart, 1};
        }
        else if(((1.0 - decimalPart) <= 0.000001) || ((decimalPart <= 0.000001) && (decimalPart > 0))){
            tuple = new int[]{1, 1};
        }
        // Limit depth (i.e. irrational numbers)
        else if(level > 20){
            tuple = new int[]{1, integerPart};
        }
        else{
            double decimalPartInverse = 1.0 / decimalPart;
            int fractionIntegerPart = (int)decimalPartInverse;
            double fractionDecimalPart = decimalPartInverse - ((double)fractionIntegerPart);

            if ((1.0 - fractionDecimalPart) <= 0.000001){
                ++fractionIntegerPart;
                fractionDecimalPart = 0;
            }
            else if ((fractionDecimalPart <= 0.000001) && (fractionDecimalPart > 0)){
                fractionDecimalPart = 0;
            }
            if(fractionDecimalPart == 0.0){
                tuple = new int[]{1, fractionIntegerPart};
            }
            else{
                tuple = computeReducedFraction(decimalPartInverse, level + 1);
                tuple = new int[]{tuple[1], tuple[0] + tuple[1] * fractionIntegerPart};
            }
        }
        // Log.d(TAG, String.format("Reduced fraction tuple %d/%d", tuple[0], tuple[1]));
        return tuple;
    }

    public static int[] computeReducedFraction(double number){
        return computeReducedFraction(number, 0);
    }
}
