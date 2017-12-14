package com.yezhou.example.mycalendar;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.file.DirectoryIteratorException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    EditText titleEdit;
    EditText contentEdit;
    Button chooseDate;
    TextView showDate;
    CheckBox checkRemind;
    String remind = "0";
    MyDateItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        titleEdit = (EditText) findViewById(R.id.add_edit_title);
        contentEdit = (EditText) findViewById(R.id.add_edit_content);
        checkRemind = (CheckBox) findViewById(R.id.add_check_remind);
        showDate = (TextView) findViewById(R.id.add_txt_showdate);
        showDate.setOnClickListener(this);
        chooseDate = (Button) findViewById(R.id.add_btn_choosedate);
        chooseDate.setOnClickListener(this);

        if(savedInstanceState != null) {    // 重载销毁的数据
            String title = savedInstanceState.getString("itemTitle");
            String content = savedInstanceState.getString("itemContent");
            String date = savedInstanceState.getString("itemDate");
            showDate.setText(date);
            titleEdit.setText(title);
            contentEdit.setText(content);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_fab_submit);
        fab.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showAlertDialog();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.add_txt_showdate:
                showDialog();
                break;
            case R.id.add_btn_choosedate:
                showDialog();
                break;
            case R.id.add_fab_submit:
                String title = titleEdit.getText().toString();
                String content = contentEdit.getText().toString();
                String date = showDate.getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString("itemTitle", title);
                bundle.putString("itemContent", content);
                bundle.putString("itemDate", date);    // 传值给MainActivity

                if (isEmpty(title, date, content)) {
                    Toast.makeText(AddActivity.this, "有内容为空请填写", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(AddActivity.this, MainActivity.class);    // 返回MainActivity
                    intent.putExtras(bundle);
                    startActivity(intent);

                    save(title, content);
                    finish();    // 销毁活动
                }
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){    // 按下返回键时 提醒
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            showAlertDialog();
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("itemTitle", titleEdit.getText().toString());
        outState.putString("itemContent", contentEdit.getText().toString());
        outState.putString("itemDate", showDate.getText().toString());
    }

    private void save(String strTitle, String strContent) {    // item经由 (选择日期) 已被创建 现实现添加(title content)
        SharedPreferences.Editor editor = getSharedPreferences("item", MODE_PRIVATE).edit();
        editor.putString("title", strTitle);
        editor.putString("content", strContent);
        editor.putString("date",item.getDate());
        editor.apply();

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this, "MyCalendar.db", null, 1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        if (checkRemind.isChecked()) {
            remind  = "1";
        }
        dbHelper.insertData(strTitle, item.getDate(), strContent, dateString, remind);
        Toast.makeText(this, "添加成功" + dateString, Toast.LENGTH_SHORT);
    }

    private void showDialog() {    // 点击按钮、TextView显示Dialog 选择日期
        if (item != null) {
            final DatePickerDialog dialog = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {    // 用于获取dialog中选定的年月日
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    item.setDate(year, month, dayOfMonth);
                    showDate.setText(item.getDate());
                }
            }, item.getYear(), item.getMonth() - 1, item.getDay());
            dialog.setMessage("请选择日期");
            dialog.show();
        } else {
            Calendar calendar = Calendar.getInstance(Locale.CHINA);    // 创建一个日历引用，通过静态方法getInstance() 从指定时区 Locale.CHINA 获得一个日期实例
            Date date = new Date();
            calendar.setTime(date);
            final DatePickerDialog dialog = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {    // 用于获取dialog中选定的年月日
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    item = new MyDateItem();
                    item.setDate(year, month, dayOfMonth);
                    Log.d("admin", item.getDate());
                    showDate.setText(item.getDate());
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.setMessage("请选择日期");
            dialog.show();
        }
    }

    private boolean isEmpty(String t, String d, String c) {
        if (t.trim().isEmpty() || d.trim().isEmpty() || c.trim().isEmpty()) {
            return true;
        }
        return false;
    }

    private void showAlertDialog() {    // 点击返回按钮触发的事件
        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddActivity.this);    // 弹出一个警告框
        dialog.setTitle("提醒 :");
        dialog.setMessage("未提交，是否退出");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
