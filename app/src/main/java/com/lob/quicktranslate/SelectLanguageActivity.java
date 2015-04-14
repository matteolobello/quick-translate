package com.lob.quicktranslate;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.heinrichreimersoftware.materialdrawer.DrawerFrameLayout;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import com.melnykov.fab.FloatingActionButton;

import at.markushi.ui.RevealColorView;


public class SelectLanguageActivity extends ActionBarActivity
        implements AdapterView.OnItemSelectedListener {

    ImageView hamburger, flag;
    DrawerFrameLayout drawer;
    SharedPreferences settings;
    Spinner spinner;

    FloatingActionButton fab;

    private RevealColorView revealColorView;
    private int backgroundColor;
    boolean mustHide = false;
    boolean isRedApp = false;

    String translateFrom = "translate_from";

    int arPos = 0, buPos = 1, chPos = 2, czPos = 3, daPos = 4,
        duPos = 5, enPos = 6, esPos = 7, fiPos = 8, frPos = 9,
        gePos = 10, grPos = 11, hePos = 12, huPos = 13, inPos = 14,
        itPos = 15, jaPos = 16, laPos = 17, liPos = 18, maPos = 19,
        noPos = 20, pePos = 21, poPos = 22, porPos = 23, roPos = 24,
        ruPos = 25, slkPos = 26, slPos = 27, spPos = 28, swPos = 29,
        tupos = 30, ukPos = 31,viPos = 32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        configureDrawer();
        configureHamburger();
        configureSharedPref();
        configureFab();
        configureSpinner();
        configureFlag();
        configureReveal();
    }

    void configureReveal() {
        revealColorView = (RevealColorView) findViewById(R.id.reveal);
        backgroundColor = Color.parseColor("#303F9F");
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
                            .setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
                                @Override
                                public void onClick(DrawerProfile drawerProfile) {
                                }
                            })
            );
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(Color.parseColor("#283593"));
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
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(Color.parseColor("#b71c1c"));
            findViewById(R.id.header).setBackgroundColor(Color.parseColor("#D32F2F"));
            colorRecents();
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

    @Override
    protected void onPause() {
        super.onPause();
        settings = getSharedPreferences("QuickTranslate", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isRedApp", isRedApp).commit();
        setIcon();
    }


    void configureSharedPref() {
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    void configureFlag() {
        flag = (ImageView)findViewById(R.id.flag);
        setupFlagAndLang(-1);
    }

    void configureSpinner() {
        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lang_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private Point getLocationInView(View src, View target) {
        final int[] l0 = new int[2];
        src.getLocationOnScreen(l0);

        final int[] l1 = new int[2];
        target.getLocationOnScreen(l1);

        l1[0] = l1[0] - l0[0] + target.getWidth() / 2;
        l1[1] = l1[1] - l0[1] + target.getHeight() / 2;

        return new Point(l1[0], l1[1]);
    }

    void configureFab() {
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.startAnimation(
                        AnimationUtils.loadAnimation(SelectLanguageActivity.this, R.anim.rotate));

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
                                        public void onClick(DrawerProfile drawerProfile) {
                                        }
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
                                        public void onClick(DrawerProfile drawerProfile) {
                                        }
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
        });
        setIcon();
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
                new ComponentName("com.lob.quicktranslate", "com.lob.quicktranslate.MainActivity-Blue"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    void disableRedIcon() {
        getPackageManager().setComponentEnabledSetting(
                new ComponentName("com.lob.quicktranslate", "com.lob.quicktranslate.MainActivity-Red"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
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
                                Intent i = new Intent(getApplicationContext(), MainActivity.class)
                                        .putExtra("wasEnabled", getIntent().getExtras().getBoolean("wasEnabled"))
                                        .putExtra("isRedApp", isRedApp)
                                        .putExtra("mustAnimateTick", false);
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
                        .setTextPrimary("Switch Language")
                        .setTextSecondary("Select your language")
                        .setImage(getResources().getDrawable(R.drawable.switch_lang))
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
                        .setTextPrimary("Settings")
                        .setTextSecondary("Change the app settings")
                        .setImage(getResources().getDrawable(R.drawable.settings))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem drawerItem, int id, int position) {
                                drawer.closeDrawer();
                                Intent i = new Intent(getApplicationContext(), SettingsActivity.class)
                                        .putExtra("wasEnabled", getIntent().getExtras().getBoolean("wasEnabled"))
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
                                        .putExtra("wasEnabled", getIntent().getExtras().getBoolean("wasEnabled"))
                                        .putExtra("isRedApp", isRedApp);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("isRedApp", isRedApp).commit();
                                startActivity(i);
                                overridePendingTransition(0, 0);
                            }
                        })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isRedApp", isRedApp).commit();
        setIcon();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_language, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void setupFlagAndLang(int position) {
        SharedPreferences.Editor editor = settings.edit();
        if (position >= 0) {
            // Should use switch-case
            if (position == 0) {
                flag.setImageResource(R.drawable.sa);
                editor.putString("translate_from", "ara");
                editor.commit();
            } else if (position == 1) {
                flag.setImageResource(R.drawable.bg);
                editor.putString("translate_from", "bul");
                editor.commit();
            } else if (position == 2) {
                flag.setImageResource(R.drawable.cn);
                editor.putString("translate_from", "chi");
                editor.commit();
            } else if (position == 3) {
                flag.setImageResource(R.drawable.cz);
                editor.putString("translate_from", "cze");
                editor.commit();
            } else if (position == 4) {
                flag.setImageResource(R.drawable.dk);
                editor.putString("translate_from", "dan");
                editor.commit();
            } else if (position == 5) {
                flag.setImageResource(R.drawable.nl);
                editor.putString("translate_from", "dut");
                editor.commit();
            } else if (position == 6) {
                flag.setImageResource(R.drawable.gb);
                editor.putString("translate_from", "eng");
                editor.commit();
            } else if (position == 7) {
                flag.setImageResource(R.drawable.ee);
                editor.putString("translate_from", "est");
                editor.commit();
            } else if (position == 8) {
                flag.setImageResource(R.drawable.fi);
                editor.putString("translate_from", "fin");
                editor.commit();
            } else if (position == 9) {
                flag.setImageResource(R.drawable.fr);
                editor.putString("translate_from", "fra");
                editor.commit();
            } else if (position == 10) {
                flag.setImageResource(R.drawable.de);
                editor.putString("translate_from", "deu");
                editor.commit();
            } else if (position == 11) {
                flag.setImageResource(R.drawable.gr);
                editor.putString("translate_from", "ell");
                editor.commit();
            } else if (position == 12) {
                flag.setImageResource(R.drawable.il);
                editor.putString("translate_from", "heb");
                editor.commit();
            } else if (position == 13) {
                flag.setImageResource(R.drawable.hu);
                editor.putString("translate_from", "hun");
                editor.commit();
            } else if (position == 14) {
                flag.setImageResource(R.drawable.id);
                editor.putString("translate_from", "ind");
                editor.commit();
            } else if (position == 15) {
                flag.setImageResource(R.drawable.it);
                editor.putString("translate_from", "ita");
                editor.commit();
            } else if (position == 16) {
                flag.setImageResource(R.drawable.jp);
                editor.putString("translate_from", "jpn");
                editor.commit();
            } else if (position == 17) {
                flag.setImageResource(R.drawable.latin);
                editor.putString("translate_from", "lat");
                editor.commit();
            } else if (position == 18) {
                flag.setImageResource(R.drawable.lv);
                editor.putString("translate_from", "lit");
                editor.commit();
            } else if (position == 19) {
                flag.setImageResource(R.drawable.my);
                editor.putString("translate_from", "mal");
                editor.commit();
            } else if (position == 20) {
                flag.setImageResource(R.drawable.no);
                editor.putString("translate_from", "nor");
                editor.commit();
            } else if (position == 21) {
                flag.setImageResource(R.drawable.pe);
                editor.putString("translate_from", "per");
                editor.commit();
            } else if (position == 22) {
                flag.setImageResource(R.drawable.pl);
                editor.putString("translate_from", "pol");
                editor.commit();
            } else if (position == 23) {
                flag.setImageResource(R.drawable.pt);
                editor.putString("translate_from", "por");
                editor.commit();
            } else if (position == 24) {
                flag.setImageResource(R.drawable.ro);
                editor.putString("translate_from", "rom");
                editor.commit();
            } else if (position == 25) {
                flag.setImageResource(R.drawable.ru);
                editor.putString("translate_from", "rus");
                editor.commit();
            } else if (position == 26) {
                flag.setImageResource(R.drawable.sk);
                editor.putString("translate_from", "slk");
                editor.commit();
            } else if (position == 27) {
                flag.setImageResource(R.drawable.si);
                editor.putString("translate_from", "slo");
                editor.commit();
            } else if (position == 28) {
                flag.setImageResource(R.drawable.es);
                editor.putString("translate_from", "spa");
                editor.commit();
            } else if (position == 29) {
                flag.setImageResource(R.drawable.se);
                editor.putString("translate_from", "swe");
                editor.commit();
            } else if (position == 30) {
                flag.setImageResource(R.drawable.tr);
                editor.putString("translate_from", "tur");
                editor.commit();
            } else if (position == 31) {
                flag.setImageResource(R.drawable.ua);
                editor.putString("translate_from", "ukr");
                editor.commit();
            } else if (position == 32) {
                flag.setImageResource(R.drawable.vn);
                editor.putString("translate_from", "vie");
                editor.commit();
            }
        } else {
            if (settings.getString(translateFrom, "eng").equals("bul")) {
                flag.setImageResource(R.drawable.bg);
                spinner.setSelection(buPos);
            } else if (settings.getString(translateFrom, "eng").equals("chi")) {
                flag.setImageResource(R.drawable.cn);
                spinner.setSelection(chPos);
            } else if (settings.getString(translateFrom, "eng").equals("deu")) {
                flag.setImageResource(R.drawable.de);
                spinner.setSelection(gePos);
            } else if (settings.getString(translateFrom, "eng").equals("fin")) {
                flag.setImageResource(R.drawable.fi);
                spinner.setSelection(fiPos);
            } else if (settings.getString(translateFrom, "eng").equals("fra")) {
                flag.setImageResource(R.drawable.fr);
                spinner.setSelection(frPos);
            } else if (settings.getString(translateFrom, "eng").equals("eng")) {
                flag.setImageResource(R.drawable.gb);
                spinner.setSelection(enPos);
            } else if (settings.getString(translateFrom, "eng").equals("ell")) {
                flag.setImageResource(R.drawable.gr);
                spinner.setSelection(grPos);
            } else if (settings.getString(translateFrom, "eng").equals("hun")) {
                flag.setImageResource(R.drawable.hu);
                spinner.setSelection(huPos);
            } else if (settings.getString(translateFrom, "eng").equals("ita")) {
                flag.setImageResource(R.drawable.it);
                spinner.setSelection(itPos);
            } else if (settings.getString(translateFrom, "eng").equals("jpn")) {
                flag.setImageResource(R.drawable.jp);
                spinner.setSelection(jaPos);
            } else if (settings.getString(translateFrom, "eng").equals("lat")) {
                flag.setImageResource(R.drawable.latin);
                spinner.setSelection(laPos);
            } else if (settings.getString(translateFrom, "eng").equals("nor")) {
                flag.setImageResource(R.drawable.no);
                spinner.setSelection(noPos);
            } else if (settings.getString(translateFrom, "eng").equals("fil")) {
                flag.setImageResource(R.drawable.ph);
                spinner.setSelection(fiPos);
            } else if (settings.getString(translateFrom, "eng").equals("pol")) {
                flag.setImageResource(R.drawable.pl);
                spinner.setSelection(poPos);
            } else if (settings.getString(translateFrom, "eng").equals("por")) {
                flag.setImageResource(R.drawable.pt);
                spinner.setSelection(porPos);
            } else if (settings.getString(translateFrom, "eng").equals("rus")) {
                flag.setImageResource(R.drawable.ru);
                spinner.setSelection(ruPos);
            } else if (settings.getString(translateFrom, "eng").equals("spa")) {
                flag.setImageResource(R.drawable.es);
                spinner.setSelection(spPos);
            } else if (settings.getString(translateFrom, "eng").equals("swe")) {
                flag.setImageResource(R.drawable.se);
                spinner.setSelection(swPos);
            } else if (settings.getString(translateFrom, "eng").equals("ukr")) {
                flag.setImageResource(R.drawable.ua);
                spinner.setSelection(ukPos);
            } else if (settings.getString(translateFrom, "eng").equals("est")) {
                flag.setImageResource(R.drawable.ee);
                spinner.setSelection(esPos);
            } else if (settings.getString(translateFrom, "eng").equals("heb")) {
                flag.setImageResource(R.drawable.il);
                spinner.setSelection(hePos);
            } else if (settings.getString(translateFrom, "eng").equals("ind")) {
                flag.setImageResource(R.drawable.id);
                spinner.setSelection(inPos);
            } else if (settings.getString(translateFrom, "eng").equals("lit")) {
                flag.setImageResource(R.drawable.lv);
                spinner.setSelection(liPos);
            } else if (settings.getString(translateFrom, "eng").equals("mal")) {
                flag.setImageResource(R.drawable.my);
                spinner.setSelection(maPos);
            } else if (settings.getString(translateFrom, "eng").equals("per")) {
                flag.setImageResource(R.drawable.pe);
                spinner.setSelection(pePos);
            } else if (settings.getString(translateFrom, "eng").equals("rom")) {
                flag.setImageResource(R.drawable.ro);
                spinner.setSelection(roPos);
            } else if (settings.getString(translateFrom, "eng").equals("slk")) {
                flag.setImageResource(R.drawable.sk);
                spinner.setSelection(slkPos);
            } else if (settings.getString(translateFrom, "eng").equals("slo")) {
                flag.setImageResource(R.drawable.si);
                spinner.setSelection(slPos);
            } else if (settings.getString(translateFrom, "eng").equals("vie")) {
                flag.setImageResource(R.drawable.vn);
                spinner.setSelection(viPos);
            } else if (settings.getString(translateFrom, "eng").equals("tur")) {
                flag.setImageResource(R.drawable.tr);
                spinner.setSelection(tupos);
            } else if (settings.getString(translateFrom, "eng").equals("dan")) {
                flag.setImageResource(R.drawable.dk);
                spinner.setSelection(daPos);
            } else if (settings.getString(translateFrom, "eng").equals("dut")) {
                flag.setImageResource(R.drawable.nl);
                spinner.setSelection(duPos);
            } else if (settings.getString(translateFrom, "eng").equals("cze")) {
                flag.setImageResource(R.drawable.cz);
                spinner.setSelection(czPos);
            } else if (settings.getString(translateFrom, "eng").equals("ara")) {
                flag.setImageResource(R.drawable.sa);
                spinner.setSelection(arPos);
            }
        }
    }

    int i = -1;

    @Override
    protected void onStop() {
        super.onStop();
        setIcon();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
        i++;
        if (i != 0) {
            Animation fadeout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
            flag.setAnimation(fadeout);
            fadeout.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setupFlagAndLang(position);
                    flag.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
