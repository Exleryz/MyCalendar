package com.yezhou.example.mycalendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView showspdata;
    private DrawerLayout mDrawerLayout;
    MyDateItem item;
    private ArrayList<String> titles;
    private ArrayList<String> dates;
    private ArrayList<String> times;
    private MyDatabaseHelper dbHelper;
    private ItemAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        dbHelper = new MyDatabaseHelper(this, "MyCalendar.db", null, 1);
        dbHelper.getWritableDatabase();    // 创建数据库

        titles = new ArrayList<>();
        dates = new ArrayList<>();
        times = new ArrayList<>();
        dbHelper.queryDataTitle(titles, dates, times);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ItemAdapter(titles, dates, times);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new MyDecoration(this, MyDecoration.VERTICAL_LIST));

        showspdata = (TextView) findViewById(R.id.main_show_spdata);
        showspdata.setText(load());    // 加载 保存下的数据(最新的一个item)

        Button btnStopService = (Button) findViewById(R.id.main_btn_stop);
        btnStopService.setOnClickListener(this);

        Intent startService = new Intent(this, MyService.class);
        startService(startService);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLastData();
            }
        });
    }

    private String load(){
        SharedPreferences pref = getSharedPreferences("item", MODE_PRIVATE);
        if (!pref.getString("title", "").equals("")) {
            StringBuilder string = new StringBuilder();
            string.append(pref.getString("title", "") + "\n");
            string.append(pref.getString("content", "") + "\n");
            string.append(pref.getString("date", ""));
            showspdata.setVisibility(View.VISIBLE);
            return string.toString();
        } else {
            showspdata.setVisibility(View.INVISIBLE);
            return null;
        }
    }

    @Override
    protected void onRestart() {    // 加载显示刚添加的数据
        super.onRestart();
        showspdata.setText(load());
        dbHelper.queryDataTitle(titles, dates, times);
        adapter.notifyDataSetChanged();
    }

    private void deleteLastData() {
        File file= new File("/data/data/"+getPackageName().toString()+"/shared_prefs","item.xml");
        if(file.exists())
        {
            file.delete();
            SharedPreferences sp=getSharedPreferences("item",MODE_PRIVATE);
            if(sp!=null){
                sp.edit().clear().commit();
                Log.d("admin", "sp commit");
            }
            Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
            showspdata.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.toolbar_add:
                Intent intent = new Intent(MainActivity.this, AddActivity.class);    // 打开Activity
                startActivity(intent);
                break;
            case R.id.toolbar_call:
                Intent intentPhone = new Intent(Intent.ACTION_DIAL);
                intentPhone.setData(Uri.parse("tel:15868093553"));
                startActivity(intentPhone);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_stop:
                Intent stopService = new Intent(this, MyService.class);
                stopService(stopService);
                break;
            default:
                break;
        }
    }
}
