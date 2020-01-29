package com.laundry.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.security.SecureRandom;

/**
 * Created by rekha_p on 16-01-2018.
 */
public class CommonUtils {
    public static final String SHARED_PREF = "Pref";

    public static int generateRandomNumber() {
        int range = 9;  // to generate a single number with this range, by default its 0..9
        int length = 4; // by default length is 4
        int randomNumber;

        SecureRandom secureRandom = new SecureRandom();
        String s = "";
        for (int i = 0; i < length; i++) {
            int number = secureRandom.nextInt(range);
            if (number == 0 && i == 0) { // to prevent the Zero to be the first number as then it will reduce the length of
                                         // generated pin to three or even more if the second or third number came as zeros
                i = -1;
                continue;
            }
            s = s + number;
        }

        randomNumber = Integer.parseInt(s);

        return randomNumber;
    }

    public static void showDialog(Context context, String title, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert = dialog.create();
        alert.show();
    }
}
