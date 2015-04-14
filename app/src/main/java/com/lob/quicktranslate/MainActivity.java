package com.lob.quicktranslate;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.heinrichreimersoftware.materialdrawer.DrawerFrameLayout;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import com.melnykov.fab.FloatingActionButton;
import com.memetix.mst.translate.Translate;

import at.markushi.ui.RevealColorView;

public class MainActivity extends ActionBarActivity {

    ClipboardManager clipBoard;

    // Views
    ImageView enabledOrDisabled;
    ImageView hamburger;
    DrawerFrameLayout drawer;
    Switch switchState;
    FloatingActionButton fab;
    RevealColorView revealColorView;

    // SharedPreferences
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    int backgroundColor;
    boolean mustHide;
    boolean isRedApp;
    boolean serviceEnabled;
//    String clientId, clientIdSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureThreadPolicy();
//      new GetClientsId().execute(null, null, null);
        configureSharedPrefs();
        configureDrawer();
        configureSwitch();
        configureClipBoard();
        configureHamburger();
        configureFab();
        configureReveal();
        setIcon();
        checkIfFirstTime();
    }

    void checkIfFirstTime() {
        if (settings.getBoolean("runFirstTime", true)) {
            startActivity(new Intent(getApplicationContext(), TutorialActivity.class));
            overridePendingTransition(0, 0);
            settings.edit().putBoolean("runFirstTime", false).commit();
        }
    }

    boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    void configureReveal() {
        revealColorView = (RevealColorView) findViewById(R.id.reveal);
        backgroundColor = Color.parseColor("#303F9F");
        try {
            if (!getIntent().getExtras().getBoolean("isRedApp")) {
                mustHide = false;
                isRedApp = false;
                fab.setColorNormal(Color.parseColor("#448AFF"));
                fab.setColorPressed(Color.parseColor("#448AFF"));
                fab.setColorRipple(Color.parseColor("#FF5252"));
                drawer.setProfile(
                        new DrawerProfile()
                                .setAvatar(getResources().getDrawable(R.drawable.ic_launcher_blue))
                                .setBackground(getResources().getDrawable(R.drawable.back))
                                .setName("QuickTranslate")
                                .setDescription("Translate faster!")
                );
                try {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        getWindow().setStatusBarColor(Color.parseColor("#283593"));
                } catch (Exception e) { /* Not on Lollipop */ }
                findViewById(R.id.header).setBackgroundColor(Color.parseColor("#303F9F"));
                colorRecents();
            } else {
                mustHide = true;
                isRedApp = true;
                drawer.setProfile(
                        new DrawerProfile()
                                .setAvatar(getResources().getDrawable(R.drawable.ic_launcher_red))
                                .setBackground(getResources().getDrawable(R.drawable.back_red))
                                .setName("QuickTranslate")
                                .setDescription("Translate faster!")
                                .setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
                                    @Override
                                    public void onClick(DrawerProfile drawerProfile) {
                                    }
                                })
                );
                fab.setColorNormal(Color.parseColor("#FF5252"));
                fab.setColorPressed(Color.parseColor("#FF5252"));
                fab.setColorRipple(Color.parseColor("#448AFF"));
                try {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        getWindow().setStatusBarColor(Color.parseColor("#b71c1c"));
                } catch (Exception e) { /* Not on Lollipop */ }
                findViewById(R.id.header).setBackgroundColor(Color.parseColor("#D32F2F"));
                colorRecents();
            }
        } catch (Exception e) {
            if (!settings.getBoolean("isRedApp", false)
                    || getComponentName() != new ComponentName("com.lob.quicktranslate", "com.lob.quicktranslate.MainActivity-Red")) {
                mustHide = false;
                isRedApp = false;
                fab.setColorNormal(Color.parseColor("#448AFF"));
                fab.setColorPressed(Color.parseColor("#448AFF"));
                fab.setColorRipple(Color.parseColor("#FF5252"));
                drawer.setProfile(
                        new DrawerProfile()
                                .setAvatar(getResources().getDrawable(R.drawable.ic_launcher_blue))
                                .setBackground(getResources().getDrawable(R.drawable.back))
                                .setName("QuickTranslate")
                                .setDescription("Translate faster!")
                );
                try {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        getWindow().setStatusBarColor(Color.parseColor("#283593"));
                } catch (Exception exception) { /* Not on Lollipop */ }
                findViewById(R.id.header).setBackgroundColor(Color.parseColor("#303F9F"));
                colorRecents();
            } else {
                mustHide = true;
                isRedApp = true;
                drawer.setProfile(
                        new DrawerProfile()
                                .setAvatar(getResources().getDrawable(R.drawable.ic_launcher_red))
                                .setBackground(getResources().getDrawable(R.drawable.back_red))
                                .setName("QuickTranslate")
                                .setDescription("Translate faster!")
                                .setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
                                    @Override
                                    public void onClick(DrawerProfile drawerProfile) {
                                    }
                                })
                );
                fab.setColorNormal(Color.parseColor("#FF5252"));
                fab.setColorPressed(Color.parseColor("#FF5252"));
                fab.setColorRipple(Color.parseColor("#448AFF"));
                try {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        getWindow().setStatusBarColor(Color.parseColor("#b71c1c"));
                } catch (Exception ex) { /* Not on Lollipop */ }
                findViewById(R.id.header).setBackgroundColor(Color.parseColor("#D32F2F"));
                colorRecents();
            }

        }
    }

    void colorRecents() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isRedApp) {
                try {
                    ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription("",
                            BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                    R.drawable.hamburger_recents),
                            Color.parseColor("#D32F2F"));
                    ((Activity) this).setTaskDescription(taskDescription);
                } catch (Exception e) { /* Not on Lollipop */
                    e.printStackTrace();
                }
            } else {
                try {
                    ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription("",
                            BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                    R.drawable.hamburger_recents),
                            Color.parseColor("#303F9F"));
                    ((Activity) this).setTaskDescription(taskDescription);
                } catch (Exception e) { /* Not on Lollipop */
                    e.printStackTrace();
                }
            }
        }
    }

    void configureThreadPolicy() {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build());
    }

    void configureSharedPrefs() {
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = settings.edit();
    }

    Point getLocationInView(View src, View target) {
        final int[] l0 = new int[2];
        src.getLocationOnScreen(l0);
        final int[] l1 = new int[2];
        target.getLocationOnScreen(l1);
        l1[0] = l1[0] - l0[0] + target.getWidth() / 2;
        l1[1] = l1[1] - l0[1] + target.getHeight() / 2;
        return new Point(l1[0], l1[1]);
    }

    void configureFab() {
        revealColorView = (RevealColorView) findViewById(R.id.reveal);
        backgroundColor = Color.parseColor("#303F9F");
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.startAnimation(
                        AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate));

                final int color = Color.parseColor("#D32F2F");
                final Point p = getLocationInView(revealColorView, v);

                if (mustHide) {
                    mustHide = false;
                    isRedApp = false;
                    fab.setColorNormal(Color.parseColor("#448AFF"));
                    fab.setColorPressed(Color.parseColor("#448AFF"));
                    fab.setColorRipple(Color.parseColor("#FF5252"));
                    drawer.setProfile(
                            new DrawerProfile()
                                    .setAvatar(getResources().getDrawable(R.drawable.ic_launcher_blue))
                                    .setBackground(getResources().getDrawable(R.drawable.back))
                                    .setName("QuickTranslate")
                                    .setDescription("Translate faster!")
                                    .setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
                                        @Override
                                        public void onClick(DrawerProfile drawerProfile) {}
                                    })
                    );
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        getWindow().setStatusBarColor(Color.parseColor("#283593"));
                    revealColorView.hide(p.x, p.y, backgroundColor, 0, 300, null);
                    colorRecents();
                } else {
                    mustHide = true;
                    isRedApp = true;
                    drawer.setProfile(
                            new DrawerProfile()
                                    .setAvatar(getResources().getDrawable(R.drawable.ic_launcher_red))
                                    .setBackground(getResources().getDrawable(R.drawable.back_red))
                                    .setName("QuickTranslate")
                                    .setDescription("Translate faster!")
                                    .setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
                                        @Override
                                        public void onClick(DrawerProfile drawerProfile) {}
                                    })
                    );
                    fab.setColorNormal(Color.parseColor("#FF5252"));
                    fab.setColorPressed(Color.parseColor("#FF5252"));
                    fab.setColorRipple(Color.parseColor("#448AFF"));
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        getWindow().setStatusBarColor(Color.parseColor("#b71c1c"));
                    revealColorView.reveal(p.x, p.y, color, v.getHeight() / 2, 340, null);
                    colorRecents();
                }
            }
        }); setIcon();
    }

    void configureHamburger() {
        hamburger = (ImageView)findViewById(R.id.imageView2);
        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer();
            }
        });
    }

    void configureImageView() {
        try {
            if (getIntent().getExtras().getBoolean("wasEnabled")
                    || settings.getBoolean("wasEnabled", false))
                serviceEnabled = true;
             else
                serviceEnabled = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void configureClipBoard() {
        clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
    }

    int i = -1;
    void configureSwitch() {
        configureImageView();
        enabledOrDisabled = (ImageView)findViewById(R.id.imageView);
        switchState = (Switch) findViewById(R.id.switch1);
        if (serviceEnabled || isServiceRunning(CopyService.class)) {
            enabledOrDisabled.setImageResource(R.drawable.tick);
            switchState.setChecked(true);
            startService(new Intent(getApplicationContext(), CopyService.class));
        } else {
            enabledOrDisabled.setImageResource(R.drawable.x);
            switchState.setChecked(false);
            stopService(new Intent(getApplicationContext(), CopyService.class));
        }
        enabledOrDisabled = (ImageView)findViewById(R.id.imageView);
        switchState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                i++;
                if (isChecked && i == 0 && getCallingActivity() != null) {
                    // do nothing
                } else {
                    Animation fadeout = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fadeout);
                    switchState.setAnimation(fadeout);
                    fadeout.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            switchState.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fadein));
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });

                    Animation slideout = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out);
                    enabledOrDisabled.startAnimation(slideout);

                    slideout.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            if (isChecked) {
                                serviceEnabled = true;
                                editor.putBoolean("wasEnabled", serviceEnabled);
                                switchState.setText("Service enabled");
                            } else {
                                serviceEnabled = false;
                                editor.putBoolean("wasEnabled", serviceEnabled);
                                switchState.setText("Service disabled");
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (isChecked)
                                enabledOrDisabled.setImageResource(R.drawable.tick);
                            else
                                enabledOrDisabled.setImageResource(R.drawable.x);

                            Animation slidein = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in);
                            enabledOrDisabled.startAnimation(slidein);
                            slidein.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {}

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                                    enabledOrDisabled.startAnimation(rotate);
                                    rotate.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {}

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            editor.putBoolean("wasEnabled", serviceEnabled).commit();
                                            if (serviceEnabled) {
                                                Intent i = new Intent(getApplicationContext(), CopyService.class);
                                                startService(i);
                                            } else {
                                                Intent i = new Intent(getApplicationContext(), CopyService.class);
                                                stopService(i);
                                            }
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {}
                                    });
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {}
                            });
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }
            }
        });

    }

    void configureDrawer() {
        drawer = (DrawerFrameLayout) findViewById(R.id.drawer);
        drawer.setProfile(
                new DrawerProfile()
                        .setAvatar(getResources().getDrawable(R.drawable.ic_launcher_blue))
                        .setBackground(getResources().getDrawable(R.drawable.back))
                        .setName("QuickTranslate")
                        .setDescription("Translate faster!")
                        .setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
                            @Override
                            public void onClick(DrawerProfile drawerProfile) {
                            }
                        })
        );
        drawer.addItem(
                new DrawerItem()
                        .setTextPrimary("Start/Stop Service")
                        .setTextSecondary("Go to the main activity")
                        .setImage(getResources().getDrawable(R.drawable.check))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, int id, int position) {
                                drawer.closeDrawer();
                            }
                        })
        );
        drawer.addDivider();
        drawer.addItem(
                new DrawerItem()
                        .setTextPrimary("Switch Language")
                        .setTextSecondary("Select your language")
                        .setImage(getResources().getDrawable(R.drawable.switch_lang))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, int id, int position) {
                                drawer.closeDrawer();
                                Intent i = new Intent(getApplicationContext(), SelectLanguageActivity.class)
                                        .putExtra("wasEnabled", serviceEnabled)
                                        .putExtra("isRedApp", isRedApp);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("isRedApp", isRedApp).commit();
                                startActivity(i);
                                overridePendingTransition(0, 0);
                            }
                        })
        );
        drawer.addDivider();
        drawer.addItem(
                new DrawerItem()
                        .setTextPrimary("Settings")
                        .setTextSecondary("Change the app settings")
                        .setImage(getResources().getDrawable(R.drawable.settings))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, int id, int position) {
                                drawer.closeDrawer();
                                Intent i = new Intent(getApplicationContext(), SettingsActivity.class)
                                        .putExtra("wasEnabled", serviceEnabled)
                                        .putExtra("isRedApp", isRedApp);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("isRedApp", isRedApp).commit();
                                startActivity(i);
                                overridePendingTransition(0, 0);
                            }
                        })
        );
        drawer.addDivider();
        drawer.addItem(
                new DrawerItem()
                        .setTextPrimary("About")
                        .setTextSecondary("Learn more about the dev")
                        .setImage(getResources().getDrawable(R.drawable.information))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, int id, int position) {
                                drawer.closeDrawer();
                                Intent i = new Intent(getApplicationContext(), AboutActivity.class)
                                        .putExtra("wasEnabled", serviceEnabled)
                                        .putExtra("isRedApp", isRedApp);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("isRedApp", isRedApp).commit();
                                startActivity(i);
                                overridePendingTransition(0, 0);
                            }
                        })
        );
    }

    void setRedIcon() {
        getPackageManager().setComponentEnabledSetting(
                new ComponentName("com.lob.quicktranslate", "com.lob.quicktranslate.MainActivity-Red"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    void setBlueIcon() {
        getPackageManager().setComponentEnabledSetting(
                new ComponentName("com.lob.quicktranslate", "com.lob.quicktranslate.MainActivity-Blue"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    void disableBlueIcon() {
        getPackageManager().setComponentEnabledSetting(
                new ComponentName("com.lob.quicktranslate", "com.lob.quicktranslate.MainActivity-Blue"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    void disableRedIcon() {
        getPackageManager().setComponentEnabledSetting(
                new ComponentName("com.lob.quicktranslate", "com.lob.quicktranslate.MainActivity-Red"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    void restartLauncher() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String currentHomePackage = resolveInfo.activityInfo.packageName;
        ActivityManager mActivityManager = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        mActivityManager.killBackgroundProcesses(currentHomePackage);
    }

    void setIcon() {
        if (isRedApp) {
            disableBlueIcon();
            setRedIcon();
        } else {
            disableRedIcon();
            setBlueIcon();
        }
        restartLauncher();
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putBoolean("wasEnabled", serviceEnabled).commit();
        editor.putBoolean("isRedApp", isRedApp).commit();
        setIcon();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putBoolean("wasEnabled", serviceEnabled).commit();
        editor.putBoolean("isRedApp", isRedApp).commit();
        setIcon();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setIcon();
    }
}

/**
 * This code is for future use
 */
//    void editDrawer(Drawable avatar, Drawable background, String name, String description,
//                    int normal, int pressed, int ripple, int revealColor, int statusBar,
//                    int int1, int int2, Point p, boolean mustReveal, boolean mustColorHeader, int headerColor) {
//        drawer.setProfile(
//                new DrawerProfile()
//                        .setAvatar(avatar)
//                        .setBackground(background)
//                        .setName(name)
//                        .setDescription(description)
//                        .setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
//                            @Override
//                            public void onClick(DrawerProfile drawerProfile) {
//                            }
//                        })
//        );
//        fab.setColorNormal(normal);
//        fab.setColorPressed(pressed);
//        fab.setColorRipple(ripple);
//
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            getWindow().setStatusBarColor(statusBar);
//
//        if (mustReveal)
//            revealColorView.reveal(p.x, p.y, revealColor, int1, int2, null);
//
//        if (mustColorHeader)
//            findViewById(R.id.header).setBackgroundColor(headerColor);
//        colorRecents();
//    }
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    class GetClientsId extends AsyncTask<String, String, String> {
//
//        BufferedReader br;
//
//        String readTextFile(String name) throws IOException {
//            StringBuilder text = new StringBuilder();
//
//            try {
//                File sdcard = Environment.getExternalStorageDirectory();
//                File file = new File(sdcard, name);
//
//                br = new BufferedReader(new FileReader(file));
//                String line;
//                while ((line = br.readLine()) != null) {
//                    text.append(line);
//                    Log.i("Test", "text : " + text);
//                    text.append('\n');
//                } }
//            catch (IOException e) {
//                e.printStackTrace();
//
//            } finally {
//                br.close();
//            }
//            return text.toString();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//                File clientIdFile = new File("/sdcard/.client-id");
//                File secretClientIdFile = new File("/sdcard/.secret-id");
//
//                if (clientIdFile.exists()) clientIdFile.delete();
//                if (secretClientIdFile.exists()) secretClientIdFile.delete();
//
//                new DefaultHttpClient().execute(new HttpGet("URL_GOES_HERE"))
//                        .getEntity().writeTo(
//                        new FileOutputStream(new File("/sdcard/.secret-id")));
//
//                new DefaultHttpClient().execute(new HttpGet("URL_GOES_HERE"))
//                        .getEntity().writeTo(
//                        new FileOutputStream(new File("/sdcard/.client-id")));
//
//                clientIdSecret = readTextFile(".secret-id");
//                clientId = readTextFile(".client-id");
//
//                secretClientIdFile.delete();
//                clientIdFile.delete();
//
//            } catch (Exception e) { e.printStackTrace(); }
//            return null;
//        }
//    }
