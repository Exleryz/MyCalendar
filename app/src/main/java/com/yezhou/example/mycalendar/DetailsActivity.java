package com.yezhou.example.mycalendar;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    String[] details = new String[4];
    MyDatabaseHelper dbHelper;
    MyDateItem item;
    TextView txtDate;
    CheckBox checkRemind;
    String remind;
    EditText editTitle;
    EditText editContent;
    TextView txtShowDay;
    AlertDialog.Builder dialog;

    String strDate;
    String strTime;
    String newDate;
    int code;    // 用于判断 1 删除 2返回保存
    boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new MyDatabaseHelper(this, "MyCalendar.db", null, 1);
        Intent intent = getIntent();
        strDate = intent.getStringExtra("date");
        strTime = intent.getStringExtra("time");

        dbHelper.queryData(strDate, strTime, details);    // 利用点击的position来判断id来进行删除

        editTitle = (EditText) findViewById(R.id.details_edit_title);
        txtDate = (TextView) findViewById(R.id.details_txt_showdate);
        editContent = (EditText) findViewById(R.id.details_edit_content);
        txtShowDay = (TextView) findViewById(R.id.details_txt_showday);
        checkRemind = (CheckBox) findViewById(R.id.details_check_remind);

        Button btnsSure = (Button) findViewById(R.id.details_btn_sure);
        Button btnUpdate = (Button) findViewById(R.id.details_btn_update);
        Button btnDelete = (Button) findViewById(R.id.details_btn_delete);    // 控件初始化

        isEdit = false;
        setEdit(editTitle, isEdit);
        setEdit(editContent, isEdit);
        checkRemind.setClickable(false);

        btnsSure.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        initialization(details);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isEdit) {
                    code = 2;
                    showAlertDialog("是否保存修改？", code);    // 处于可编辑状态 是否保存更改 是 保存退出 否 直接退出
                } else {
                    finish();
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.details_btn_sure:
                if (isEdit) {
                    code = 2;
                    showAlertDialog("是否保存修改？", code);    // 处于可编辑状态 是否保存更改 是 保存退出 否 直接退出
                } else {
                    finish();
                }
                break;
            case R.id.details_btn_update:
                isEdit = true;
                checkRemind.setClickable(true);
                setEdit(editTitle, isEdit);
                setEdit(editContent, isEdit);
                item = new MyDateItem();
                item.setDate(txtDate.getText().toString());
                item.decomposeDate();
                txtDate.setOnClickListener(this);
                Toast.makeText(this, "Now you can set content", Toast.LENGTH_SHORT).show();
                break;
            case R.id.details_btn_delete:
                code = 1;
                showAlertDialog("确定删除此记录?", code);
                break;
            case R.id.details_txt_showdate:
                showDateDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){    // 按下返回键时 提醒
        if (keyCode==KeyEvent.KEYCODE_BACK ) {
            if (isEdit)
                showAlertDialog("未提交,是否退出", 3);
            else
                finish();
        }
        return false;
    }

    private void initialization(String[] strs) {    // 初始化 向控件内填充数据
        editTitle.setText(strs[0]);
        txtDate.setText(strs[1]);
        editContent.setText(strs[2]);
        if (strs[3].equals("1")) {
            checkRemind.setChecked(true);
            remind = "1";
        } else {
            checkRemind.setChecked(false);
            remind =  "0";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
        Date date = new Date();
        txtShowDay.setText(calculatingDateDifference(sdf.format(date), strDate));    // 当天 设定日期
    }

    private void setEdit(View v, boolean is) {    // 设置不可编辑 当点击更新时 解放
        EditText temp = (EditText) v;
        if (is) {
            temp.setFocusableInTouchMode(true);
            temp.setFocusable(true);
        } else {
            temp.setFocusable(false);
            temp.setFocusableInTouchMode(false);
        }
    }

    private void saveUpdata() {
        if (isEdit) {    // 更新成功
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date currentTime = new Date();
            String newTime = formatter.format(currentTime);
            Log.d("admin_details_newTime", newTime);
            if (checkRemind.isChecked()) {
                remind = "1";
            } else {
                remind = "0";
            }
            if (newDate != null) {
                dbHelper.updateData(strDate, strTime, newDate, newTime, editTitle.getText().toString(), editContent.getText().toString(), remind);    // 利用strDate strTime搜索 更改数据库各项内容
            } else {    // 更新数据时data没有更新
                dbHelper.updateData(strDate, strTime, strDate, newTime, editTitle.getText().toString(), editContent.getText().toString(), remind);
            }
        }
    }

    private void showAlertDialog(final String message, final int code) {    // 点击返回按钮触发的事件
        final AlertDialog.Builder dialog = new AlertDialog.Builder(DetailsActivity.this);    // 弹出一个警告框
        dialog.setTitle("警告 :");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (code) {
                    case 1:    // 删除
                        dbHelper.deleteData(strDate, strTime);
                        finish();
                        break;
                    case 2:
                        saveUpdata();
                        finish();
                        break;
                    case 3:    // back键
                        finish();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
                if (code == 2) {
                    finish();    // show 之外不能用finish 感觉dialog是交由子线程去处理的，而主线程调用了finish所以闪退
                }
            }
        });
        dialog.show();    // show() 后面不能跟finish() WindowManager: android.view.WindowLeaked: Activity com.yezhou.example.mycalendar.DetailsActivity has leaked window DecorView@fb5316d[] that was originally added here
        // 有View未关闭 dialog会闪退
    }

    private void showDateDialog() {
        final DatePickerDialog dialog = new DatePickerDialog(DetailsActivity.this, new DatePickerDialog.OnDateSetListener() {    // 用于获取dialog中选定的年月日
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                item.setDate(year, month, dayOfMonth);
                txtDate.setText(item.getDate());

                newDate = item.getDate();    // 修改倒计时  日期
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
                Date date = new Date();
                txtShowDay.setText(calculatingDateDifference(sdf.format(date), newDate));    // 当天 新设定日期
            }
        }, item.getYear(), item.getMonth() - 1, item.getDay());
        dialog.setMessage("请选择日期");
        dialog.show();
    }


    public String calculatingDateDifference(String date1, String date2) {    // date1 当前时间 date2 设定时间

        String[] a = date1.split("-");
        String[] b = date2.split("-");
        int month1 = Integer.parseInt(a[1].trim());
        int month2 = Integer.parseInt(b[1].trim());
        int dofm1 = Integer.parseInt(a[2].trim());
        int dofm2 = Integer.parseInt(b[2].trim());
        int year1 = Integer.parseInt(a[0]);
        int year2 = Integer.parseInt(b[0]);

        int day1 = calculatingDays(year1, month1, dofm1);
        int day2 = calculatingDays(year2, month2, dofm2);

        if (year1 != year2)   //同一年
        {
            if (year1 > year2) {
                if (month1 == month2 && dofm1 == dofm2) {
                    return year1 - year2 + "年前的今天";
                } else {
                    int timeDistance = 0;
                    for (int i = year2; i < year1; i++) {
                        if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                        {
                            timeDistance += 366;
                        } else    //不是闰年
                        {
                            timeDistance += 365;
                        }
                    }
                    return "距今已过去" + (timeDistance + (day1 - day2)) + "天";    // 把年加到相同(计算一年天数*年数) + 一年中两天的差值
                }
            } else if (year2 > year1) {
                int timeDistance = 0;
                for (int i = year1; i < year2; i++) {
                    if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                    {
                        timeDistance += 366;
                    } else {    // 不是闰年
                        timeDistance += 365;
                    }
                }
                if (month1 == month2 && dofm1 == dofm2) {
                    return year2 - year1 + "年后的今天,还有" + (timeDistance + day2 - day1) + "天";
                }
                return "距离设定时间还有" + (timeDistance + day2 - day1) + "天";
            }


        } else {    //同一年
            if (day1 > day2) {
                return "已经过去" + (day1 - day2) + "天";
            } else if (day1 == day2) {
                return "就是今天";
            } else {
                return "还有" + (day2 - day1) + "天";
            }
        }
        return "我已经被判断日期绕晕了";
    }

    public int calculatingDays(int y, int m, int d) {    // 返回dayofyear
        m = m-1;
        int dayOfYear = 0;
        switch (m) {    // 利用case选择 累加
            case 11:
                dayOfYear = dayOfYear + 30;
            case 10:
                dayOfYear += 31;
            case 9:
                dayOfYear += 30;
            case 8:
                dayOfYear += 31;
            case 7:
                dayOfYear += 31;
            case 6:
                dayOfYear += 30;
            case 5:
                dayOfYear += 31;
            case 4:
                dayOfYear += 30;
            case 3:
                dayOfYear += 31;
            case 2:
                if (y % 4 == 0 && y % 100 != 0 || y % 400 == 0)    //闰年
                {
                    dayOfYear += 29;
                } else {    // 不是闰年
                    dayOfYear += 28;
                }
            case 1:
                dayOfYear += 31;
            case 0:
                dayOfYear += 0;
        }
        return dayOfYear + d;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("admin_details", "onDestroy");
    }
}
