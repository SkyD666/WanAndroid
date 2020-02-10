package com.skyd.wanandroid.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.skyd.wanandroid.R;
import com.skyd.wanandroid.adapter.TODOAdapter;
import com.skyd.wanandroid.item.ArticleItem;
import com.skyd.wanandroid.item.TODOItem;
import com.skyd.wanandroid.tool.HttpCallbackListener;
import com.skyd.wanandroid.tool.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TODOActivity extends AppCompatActivity {
    private static final int TYPE_COUNT = 5;
    private int selectType = 0;       //0,1,2,3,4
    public static List<List<TODOItem>> todoList = new ArrayList<>();
    //private List<TODOItem> todoList = new ArrayList<>();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private int refreshCount = 1, isFresh = 0;
    private TODOAdapter adapter;

    private int year;
    private int month;
    private int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        for (int i = 0; i < TYPE_COUNT; i++) {
            //List<TODOItem> tempList = new ArrayList<>();
            todoList.add(new ArrayList<TODOItem>());
        }

        adapter = new TODOAdapter(todoList.get(selectType));

        toolbar = findViewById(R.id.tb_todo);
        recyclerView = findViewById(R.id.rv_todo);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        toolbar.inflateMenu(R.menu.toolbar_todo);       //加载一下菜单

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (HttpUtil.isOnlineByPing("www.baidu.com")) {
            sendRequestAndParseJSON("https://www.wanandroid.com/lg/todo/v2/list/" + (refreshCount++) + "/json", 0, 0);
        } else {
            Toast.makeText(TODOActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_todo, menu);

        menu.add(1, 0, 1, "只用这一个");
        menu.add(1, 1, 2, "工作");
        menu.add(1, 2, 3, "学习");
        menu.add(1, 3, 4, "生活");
        menu.add(1, 4, 5, "娱乐");

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        todoList.clear();       //由于定义的是static所以要及时清除
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.it_todoAdd:
                alertAdd();
                break;
            case 0:
                selectType = 0;
                adapter.resetList(todoList.get(selectType));
                adapter.notifyDataSetChanged();
                //adapter = new TODOAdapter(todoList.get(selectType));
                recyclerView.setAdapter(adapter);
                break;
            case 1:
                selectType = 1;
                adapter.resetList(todoList.get(selectType));
                adapter.notifyDataSetChanged();
                //adapter = new TODOAdapter(todoList.get(selectType));
                recyclerView.setAdapter(adapter);
                break;
            case 2:
                selectType = 2;
                adapter.resetList(todoList.get(selectType));
                adapter.notifyDataSetChanged();
                //adapter = new TODOAdapter(todoList.get(selectType));
                recyclerView.setAdapter(adapter);
                break;
            case 3:
                selectType = 3;
                adapter.resetList(todoList.get(selectType));
                adapter.notifyDataSetChanged();
                //adapter = new TODOAdapter(todoList.get(selectType));
                recyclerView.setAdapter(adapter);
                break;
            case 4:
                selectType = 4;
                adapter.resetList(todoList.get(selectType));
                adapter.notifyDataSetChanged();
                //adapter = new TODOAdapter(todoList.get(selectType));
                recyclerView.setAdapter(adapter);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendRequestAndParseJSON(String Surl, final int scrollToPos, final int addItemFrom) {
        HttpUtil.setGetRequest(Surl, new HttpCallbackListener() {
            @Override
            public void onComplete(String response) {
                int[] scrollToPosition = new int[TYPE_COUNT];
                int[] addItemFrom_2 = new int[TYPE_COUNT];
                int[] i_2 = new int[TYPE_COUNT];
                for (int i = 0; i < TYPE_COUNT; i++) {
                    scrollToPosition[i] = scrollToPos;
                    addItemFrom_2[i] = addItemFrom;
                    i_2[i] = 0;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject_1 = new JSONObject(jsonObject.getString("data"));
                    JSONArray jsonArray = new JSONArray(jsonObject_1.getString("datas"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject_2 = jsonArray.getJSONObject(i);
                        String[] result = new String[4];
                        long timeStamp = jsonObject_2.getLong("date");
                        long completeTimeStamp;
                        //Log.d("---", jsonObject_2.getString("completeDate"));
                        if (jsonObject_2.getString("completeDate") == "null") {
                            completeTimeStamp = 0;
                        } else {
                            completeTimeStamp = jsonObject_2.getLong("completeDate");
                        }
                        int id = jsonObject_2.getInt("id");
                        int type = jsonObject_2.getInt("type");
                        int status = jsonObject_2.getInt("status");
                        int priority = jsonObject_2.getInt("priority");
                        result[0] = jsonObject_2.getString("dateStr");
                        result[1] = jsonObject_2.getString("completeDateStr");
                        result[2] = jsonObject_2.getString("title");
                        result[3] = jsonObject_2.getString("content");

                        addData(timeStamp, result[0], completeTimeStamp, result[1], result[2]
                                , result[3], id, type, status, priority,
                                i_2[type] + addItemFrom_2[type], scrollToPosition[type],
                                i + 1 == jsonArray.length() ? 1 : 0);

                        i_2[type]++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(final Exception e) {
            }
        });
    }

    private void addData(final long timeStamp, final String time, final long completeTimeStamp,
                         final String completeTime, final String title, final String content, final int id,
                         final int type, final int status, final int priority, final int addPosition,
                         final int scrollToPos, final int finish) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Log.d("----", addPosition + "");
                todoList.get(type).add(addPosition, new TODOItem(timeStamp, time, completeTimeStamp,
                        completeTime, title, content, id, type, status, priority));

                adapter.notifyDataSetChanged();
                todoList.get(type).get(addPosition).setActivity(TODOActivity.this);
                adapter.notifyItemInserted(addPosition);
                recyclerView.scrollToPosition(scrollToPos);
                if (finish == 1) isFresh = 0;
            }
        });
    }

    //添加编辑
    private void sendPostRequest(String sUrl, String data) {
        HttpUtil.setPostRequest(sUrl, data, new HttpCallbackListener() {
            @Override
            public void onComplete(String response) {
                refreshCount = 1;
                selectType = 0;
                todoList.clear();
                for (int i = 0; i < TYPE_COUNT; i++) {
                    todoList.add(new ArrayList<TODOItem>());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.resetList(todoList.get(selectType));
                        adapter.notifyDataSetChanged();
                        Toast.makeText(TODOActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                    }
                });
                sendRequestAndParseJSON("https://www.wanandroid.com/lg/todo/v2/list/" + (refreshCount++) + "/json", 0, 0);
            }

            @Override
            public void onError(Exception e) {
                Log.d("---", e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TODOActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //添加
    public void alertAdd() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.alert_editor_todo_add, null);
        final EditText editText1 = view.findViewById(R.id.editText1);
        final EditText editText2 = view.findViewById(R.id.editText2);
        final Spinner spinner = view.findViewById(R.id.todo_spinner);
        final DatePicker datePicker = view.findViewById(R.id.todo_datePicker);
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DATE);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int y, int m, int d) {
                year = y;
                month = m + 1;
                day = d;
            }
        });
        AlertDialog.Builder ad1 = new AlertDialog.Builder(this);
        ad1.setTitle("添加TODO: ");
        ad1.setIcon(R.drawable.info);
        ad1.setView(view);
        ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                String text1, text2;
                text1 = editText1.getText().toString();
                text2 = editText2.getText().toString();
                if (!text1.equals("") && !text2.equals("") && !spinner.getSelectedItem().toString().equals("")) {
                    Log.d("---", "title=" + text1 + "&content=" + text2 + "&date=" + year + "-"
                            + month + "-" + day + "&type=" + spinner.getSelectedItemPosition());
                    sendPostRequest("https://www.wanandroid.com/lg/todo/add/json",
                            ("title=" + text1 + "&content=" + text2 + "&date=" + year + "-"
                                    + month + "-" + day + "&type=" + spinner.getSelectedItemPosition()));
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TODOActivity.this, "操作失败，标题和详情不能为空", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        ad1.show();// 显示对话框
    }

    //编辑
    public void alertEdit(final TODOItem todoItem) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.alert_editor_todo_add, null);
        final EditText editText1 = view.findViewById(R.id.editText1);
        final EditText editText2 = view.findViewById(R.id.editText2);
        editText1.setText(Html.fromHtml(todoItem.getTitle()));
        editText2.setText(Html.fromHtml(todoItem.getContent()));
        final Spinner spinner = view.findViewById(R.id.todo_spinner);
        spinner.setSelection(todoItem.getType(), true);
        final DatePicker datePicker = view.findViewById(R.id.todo_datePicker);
        String[] date = todoItem.getTime().split("-");
        year = Integer.parseInt(date[0]);
        month = Integer.parseInt(date[1]);
        day = Integer.parseInt(date[2]);
        Log.d("---0", year + " " + month + " " + day);
        datePicker.init(year, month - 1, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int y, int m, int d) {
                year = y;
                month = m + 1;
                day = d;
            }
        });
        AlertDialog.Builder ad1 = new AlertDialog.Builder(this);
        ad1.setTitle("编辑TODO: ");
        ad1.setIcon(R.drawable.info);
        ad1.setView(view);
        ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                String text1, text2;
                text1 = editText1.getText().toString();
                text2 = editText2.getText().toString();
                if (!text1.equals("") && !text2.equals("") && !spinner.getSelectedItem().toString().equals("")) {
                    Log.d("---", "title=" + text1 + "&content=" + text2 + "&date=" + year + "-"
                            + month + "-" + day + "&type=" + spinner.getSelectedItemPosition());
                    sendPostRequest("https://www.wanandroid.com/lg/todo/update/" + todoItem.getId() + "/json",
                            ("title=" + text1 + "&content=" + text2 + "&date=" + year + "-"
                                    + month + "-" + day + "&type=" + spinner.getSelectedItemPosition()));
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TODOActivity.this, "操作失败，标题和详情不能为空", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        ad1.show();// 显示对话框
    }
}