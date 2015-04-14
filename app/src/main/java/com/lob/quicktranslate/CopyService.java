package com.lob.quicktranslate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class CopyService extends Service {

    ClipboardManager cm;
    SharedPreferences settings;
    NotificationManager notificationManager;
    Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();
        cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.addPrimaryClipChangedListener(new ClipboardListener());

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build());

        if (settings.getBoolean("mustShowNotification", false)) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            int icon = !settings.getBoolean("isRedApp", true) ? R.drawable.ic_launcher_blue : R.drawable.ic_launcher_red;
            CharSequence notiText = "Notification from service";
            long meow = System.currentTimeMillis();

            notification = new Notification(icon, notiText, meow);
            CharSequence contentTitle = "QuickTranslate is running...";
            CharSequence contentText = "Tap to edit settings";
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notificationManager.notify(1, notification);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cm.removePrimaryClipChangedListener(new ClipboardListener());
        try {
            notificationManager.cancel(1);
        } catch (Exception e) {
            // do nothing
        }
    }

    class ClipboardListener implements ClipboardManager.OnPrimaryClipChangedListener {

        int i = -1;
        public void onPrimaryClipChanged() {
            settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (settings.getBoolean("wasEnabled", false)) {
                i++;
                ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                String textToTranslate = String.valueOf(clipBoard.getText()).replace("\"", "");
                if (i != 0) /* Method is called twice */ {
                    try {
                        Translate.setClientId("INSERT_CLIENT_ID_HERE");
                        Translate.setClientSecret("INSERT_CLIENT_SECRET_HERE");

                        String translatedText = Translate.execute(textToTranslate, translateTo());
                        final Toast toast = Toast.makeText(getBaseContext(), translatedText,
                                Toast.LENGTH_SHORT);
                        toast.show();

                        new CountDownTimer(settings.getInt("toastDurationMs", 4000), 500) {
                            public void onTick(long millisUntilFinished) { toast.show(); }
                            public void onFinish() { toast.cancel(); }
                        }.start();

                        if (settings.getBoolean("mustCopy", false))
                            clipBoard.setText(translatedText);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        i = -1;
                    }
                }
            }
        }
    }

    Language translateTo() {
        String translateFrom = "translate_from";
        SharedPreferences settings = getSharedPreferences("QuickTranslate", 0);
        if (settings.getString(translateFrom, "eng").equals("ara")) {
            return Language.ARABIC;
        } else if (settings.getString(translateFrom, "eng").equals("bul")) {
            return Language.BULGARIAN;
        } else if (settings.getString(translateFrom, "eng").equals("chi")) {
            return Language.CHINESE_SIMPLIFIED;
        } else if (settings.getString(translateFrom, "eng").equals("cze")) {
            return Language.CZECH;
        } else if (settings.getString(translateFrom, "eng").equals("dan")) {
            return Language.DANISH;
        } else if (settings.getString(translateFrom, "eng").equals("dut")) {
            return Language.DUTCH;
        } else if (settings.getString(translateFrom, "eng").equals("est")) {
            return Language.ESTONIAN;
        } else if (settings.getString(translateFrom, "eng").equals("heb")) {
            return Language.HEBREW;
        } else if (settings.getString(translateFrom, "eng").equals("ind")) {
            return Language.INDONESIAN;
        } else if (settings.getString(translateFrom, "eng").equals("deu")) {
            return Language.GERMAN;
        } else if (settings.getString(translateFrom, "eng").equals("fin")) {
            return Language.FINNISH;
        } else if (settings.getString(translateFrom, "eng").equals("fra")) {
            return Language.FRENCH;
        } else if (settings.getString(translateFrom, "eng").equals("lat")) {
            return Language.LATVIAN;
        } else if (settings.getString(translateFrom, "eng").equals("lit")) {
            return Language.LITHUANIAN;
        } else if (settings.getString(translateFrom, "eng").equals("per")) {
            return Language.PERSIAN;
        } else if (settings.getString(translateFrom, "eng").equals("rom")) {
            return Language.ROMANIAN;
        } else if (settings.getString(translateFrom, "eng").equals("slo")) {
            return Language.SLOVENIAN;
        } else if (settings.getString(translateFrom, "eng").equals("slk")) {
            return Language.SLOVAK;
        } else if (settings.getString(translateFrom, "eng").equals("tur")) {
            return Language.TURKISH;
        } else if (settings.getString(translateFrom, "eng").equals("vie")) {
            return Language.VIETNAMESE;
        } else if (settings.getString(translateFrom, "eng").equals("mal")) {
            return Language.MALAY;
        } else if (settings.getString(translateFrom, "eng").equals("eng")) {
            return Language.ENGLISH;
        } else if (settings.getString(translateFrom, "eng").equals("ell")) {
            return Language.GREEK;
        } else if (settings.getString(translateFrom, "eng").equals("hun")) {
            return Language.HUNGARIAN;
        } else if (settings.getString(translateFrom, "eng").equals("ita")) {
            return Language.ITALIAN;
        } else if (settings.getString(translateFrom, "eng").equals("lat")) {
            return Language.LATVIAN;
        } else if (settings.getString(translateFrom, "eng").equals("nor")) {
            return Language.NORWEGIAN;
        } else if (settings.getString(translateFrom, "eng").equals("pol")) {
            return Language.POLISH;
        } else if (settings.getString(translateFrom, "eng").equals("por")) {
            return Language.PORTUGUESE;
        } else if (settings.getString(translateFrom, "eng").equals("rus")) {
            return Language.RUSSIAN;
        } else if (settings.getString(translateFrom, "eng").equals("spa")) {
            return Language.SPANISH;
        } else if (settings.getString(translateFrom, "eng").equals("swe")) {
            return Language.SWEDISH;
        } else if (settings.getString(translateFrom, "eng").equals("ukr")) {
            return Language.UKRAINIAN;
        } else {
            return Language.ENGLISH;
        }
    }
}
