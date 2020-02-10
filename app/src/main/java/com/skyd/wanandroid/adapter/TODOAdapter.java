package com.skyd.wanandroid.adapter;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.skyd.wanandroid.R;
import com.skyd.wanandroid.activity.TODOActivity;
import com.skyd.wanandroid.item.TODOItem;
import com.skyd.wanandroid.tool.HttpCallbackListener;
import com.skyd.wanandroid.tool.HttpUtil;

import java.util.Calendar;
import java.util.List;

public class TODOAdapter extends RecyclerView.Adapter<TODOAdapter.ViewHolder> {
    private List<TODOItem> mTODOList;

    public void resetList(List<TODOItem> todoList) {
        mTODOList = todoList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View TODOView;
        TextView tvTitle;
        TextView tvContent;
        TextView tvTime;
        TextView tvCompleteTime;
        ImageView ivDelete;
        ImageView ivEdit;
        ImageView ivStatus;

        public ViewHolder(View view) {
            super(view);
            TODOView = view;
            tvTitle = view.findViewById(R.id.tv_todoTitle);
            tvContent = view.findViewById(R.id.tv_todoContent);
            tvTime = view.findViewById(R.id.tv_todoTime);
            tvCompleteTime = view.findViewById(R.id.tv_todoCompleteTime);
            ivDelete = view.findViewById(R.id.iv_todoDelete);
            ivEdit = view.findViewById(R.id.iv_todoEdit);
            ivStatus = view.findViewById(R.id.iv_todoStatus);
        }
    }

    public TODOAdapter(List<TODOItem> todoList) {
        mTODOList = todoList;
    }

    @Override
    public TODOAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        final TODOAdapter.ViewHolder holder = new TODOAdapter.ViewHolder(view);
        holder.TODOView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final TODOAdapter.ViewHolder holder, final int position) {
        final TODOItem todoItem = mTODOList.get(position);
        holder.tvTitle.setText(Html.fromHtml(todoItem.getTitle()));
        holder.tvContent.setText(Html.fromHtml(todoItem.getContent()));
        holder.tvTime.setText("预计完成时间: " + todoItem.getTime());
        holder.tvCompleteTime.setText("完成: " + todoItem.getCompleteTime());
        if (todoItem.getStatus() == 1) {
            holder.ivStatus.setImageResource(R.drawable.complete);
        } else {
            holder.ivStatus.setImageResource(R.drawable.uncomplete);
        }
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TODOActivity) todoItem.getActivity()).alertEdit(todoItem);
            }
        });
        holder.ivStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.setPostRequest("https://www.wanandroid.com/lg/todo/done/" +
                        todoItem.getId() + "/json", "status=" + (todoItem.getStatus() == 1 ? 0 : 1), new HttpCallbackListener() {
                    @Override
                    public void onComplete(String response) {
                        todoItem.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mTODOList.get(position).getStatus() == 1) {
                                    mTODOList.get(position).setStatus(0);
                                    holder.ivStatus.setImageResource(R.drawable.uncomplete);
                                    mTODOList.get(position).setCompleteTime("");
                                    mTODOList.get(position).setCompleteTimeStamp(0);
                                    holder.tvCompleteTime.setText("完成: " + todoItem.getCompleteTime());
                                    Toast.makeText(todoItem.getActivity(), "已修改为未完成", Toast.LENGTH_SHORT).show();
                                } else {
                                    mTODOList.get(position).setStatus(1);
                                    holder.ivStatus.setImageResource(R.drawable.complete);
                                    Calendar c = Calendar.getInstance();
                                    mTODOList.get(position).setCompleteTime(c.get(Calendar.YEAR) + "-"
                                            + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE));
                                    mTODOList.get(position).setCompleteTimeStamp(System.currentTimeMillis());
                                    holder.tvCompleteTime.setText("完成: " + todoItem.getCompleteTime());
                                    Toast.makeText(todoItem.getActivity(), "已修改为完成", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.setPostRequest("https://www.wanandroid.com/lg/todo/delete/"
                        + todoItem.getId() + "/json", null, new HttpCallbackListener() {
                    @Override
                    public void onComplete(String response) {
                        if (todoItem.getActivity() != null) {
                            todoItem.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TODOActivity.todoList.get(todoItem.getType()).remove(position);
                                    notifyDataSetChanged();
                                    Toast.makeText(todoItem.getActivity(), "已删除", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTODOList.size();
    }
}
