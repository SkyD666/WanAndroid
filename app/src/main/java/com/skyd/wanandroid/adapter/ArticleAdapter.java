package com.skyd.wanandroid.adapter;

import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skyd.wanandroid.GlobalData;
import com.skyd.wanandroid.R;
import com.skyd.wanandroid.activity.SquareUsersShareArticlesActivity;
import com.skyd.wanandroid.activity.WebsiteActivity;
import com.skyd.wanandroid.item.ArticleItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skyd.wanandroid.tool.ConvertStreamToString;
import com.skyd.wanandroid.tool.HttpUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter {
    private static final int ITEM_VIEW_TYPE_IMAGED = 1;
    private static final int ITEM_VIEW_TYPE_UNIMAGED = 0;

    private List<ArticleItem> mArticleList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View articleView;
        TextView tvtitle;
        TextView tvauthor;
        TextView tvtime;
        TextView tvtop;
        TextView tvdelete;
        TextView tvchapter;

        public ViewHolder(View view) {
            super(view);
            articleView = view;
            tvtitle = view.findViewById(R.id.tv_title);
            tvauthor = view.findViewById(R.id.tv_author);
            tvtime = view.findViewById(R.id.tv_time);
            tvchapter = view.findViewById(R.id.tv_chapter);
            tvtop = view.findViewById(R.id.tv_top);
            tvdelete = view.findViewById(R.id.tv_delete);
        }
    }

    static class ImagedViewHolder extends RecyclerView.ViewHolder {
        View articleView;
        TextView tvtitle;
        TextView tvauthor;
        TextView tvtime;
        TextView tvtop;
        TextView tvdelete;
        TextView tvchapter;
        ImageView ivpicture;

        public ImagedViewHolder(View view) {
            super(view);
            articleView = view;
            tvtitle = view.findViewById(R.id.tv_title);
            tvauthor = view.findViewById(R.id.tv_author);
            tvtime = view.findViewById(R.id.tv_time);
            tvchapter = view.findViewById(R.id.tv_chapter);
            tvtop = view.findViewById(R.id.tv_top);
            tvdelete = view.findViewById(R.id.tv_delete);
            ivpicture = view.findViewById(R.id.iv_pic);
        }
    }

    public ArticleAdapter(List<ArticleItem> articleList) {
        mArticleList = articleList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            holder.articleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HttpUtil.isOnlineByPing("www.baidu.com")) {
                        int position = holder.getAdapterPosition();
                        Intent intent = new Intent(v.getContext(), WebsiteActivity.class);
                        intent.putExtra("surl", mArticleList.get(position).getLink());
                        intent.putExtra("id", mArticleList.get(position).getId());
                        intent.putExtra("title", mArticleList.get(position).getTitle());
                        v.getContext().startActivity(intent);
                    } else {
                        Toast.makeText(v.getContext(), "无法连接网络", Toast.LENGTH_LONG).show();
                    }
                }
            });
            return holder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_image_item, parent, false);
            final ImagedViewHolder holder = new ImagedViewHolder(view);
            holder.articleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HttpUtil.isOnlineByPing("www.baidu.com")) {
                        int position = holder.getAdapterPosition();
                        Intent intent = new Intent(v.getContext(), WebsiteActivity.class);
                        intent.putExtra("surl", mArticleList.get(position).getLink());
                        intent.putExtra("id", mArticleList.get(position).getId());
                        intent.putExtra("title", mArticleList.get(position).getTitle());
                        v.getContext().startActivity(intent);
                    } else {
                        Toast.makeText(v.getContext(), "无法连接网络", Toast.LENGTH_LONG).show();
                    }
                }
            });
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ArticleItem articleItem = mArticleList.get(position);
        if (articleItem.isImaged() == 0) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.tvtitle.setText(Html.fromHtml(articleItem.getTitle()));      //将html格式的标题解析为普通
            if (articleItem.getFinalAuthor().equals("")) {
                viewHolder.tvauthor.setVisibility(View.INVISIBLE);
            }
            viewHolder.tvauthor.setText(articleItem.getFinalAuthor());
            viewHolder.tvchapter.setText(articleItem.getFinalChapterName());
            viewHolder.tvtime.setText(articleItem.getNiceDate());
            final int pos = position;
            if (articleItem.isSquare()) {
                viewHolder.tvauthor.setTextColor(viewHolder.tvauthor.getResources().getColor(R.color.colorBlue01));
                viewHolder.tvauthor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), SquareUsersShareArticlesActivity.class);
                        intent.putExtra("userId", mArticleList.get(pos).getUserId());
                        intent.putExtra("username", mArticleList.get(pos).getShareUser());
                        v.getContext().startActivity(intent);
                        //Log.d("---", mArticleList.get(pos).getShareUser() +mArticleList.get(pos).getUserId());
                    }
                });
            }
            if (articleItem.getIsTop() == 1) {
                viewHolder.tvtop.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvtop.setVisibility(View.INVISIBLE);
            }
            if (articleItem.isMySquare()) {
                viewHolder.tvdelete.setVisibility(View.VISIBLE);
                viewHolder.tvdelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendRequestWithHttpURLConnection("https://wanandroid.com/lg/user_article/delete/" + articleItem.getId() + "/json", articleItem);
                    }
                });
            } else {
                viewHolder.tvdelete.setVisibility(View.INVISIBLE);
            }
        } else {
            ImagedViewHolder viewHolder = (ImagedViewHolder) holder;
            viewHolder.tvtitle.setText(Html.fromHtml(articleItem.getTitle()));      //将html格式的标题解析为普通
            if (articleItem.getFinalAuthor().equals("")) {
                viewHolder.tvauthor.setVisibility(View.INVISIBLE);
            }
            viewHolder.tvauthor.setText(articleItem.getFinalAuthor());
            viewHolder.tvchapter.setText(articleItem.getFinalChapterName());
            viewHolder.tvtime.setText(articleItem.getNiceDate());

            int lwidth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int rheight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            viewHolder.ivpicture.measure(lwidth, rheight);
            if (viewHolder.ivpicture.getTag(R.id.tag_image_article) != null &&
                    (int) viewHolder.ivpicture.getTag(R.id.tag_image_article) != position) {
                //如果tag不是Null,并且同时tag不等于当前的position。
                //说明当前的viewHolder是复用来的
                //Cancel any pending loads Glide may have for the view
                //and free any resources that may have been loaded for the view.
                Log.d("===","===");
                Glide.with(viewHolder.ivpicture.getContext()).clear(((ImagedViewHolder) holder).ivpicture);
            }
            float scale = viewHolder.ivpicture.getContext().getResources().getDisplayMetrics().density;
            int px = (int)(77 * scale + 0.5f);
            RequestOptions options = new RequestOptions()
                    .placeholder(R.color.colorWhite)
                    .error(R.color.colorWhite)
                    .override(px, px);
            Glide.with(viewHolder.ivpicture.getContext()).load(articleItem.getPictureUrl())
                    .apply(options)
                    .into(viewHolder.ivpicture);
            viewHolder.ivpicture.setTag(R.id.tag_image_article, position);      //设置tag
            final int pos = position;
            if (articleItem.isSquare()) {
                viewHolder.tvauthor.setTextColor(viewHolder.tvauthor.getResources().getColor(R.color.colorBlue01));
                viewHolder.tvauthor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), SquareUsersShareArticlesActivity.class);
                        intent.putExtra("userId", mArticleList.get(pos).getUserId());
                        intent.putExtra("username", mArticleList.get(pos).getShareUser());
                        v.getContext().startActivity(intent);
                    }
                });
            }
            if (articleItem.getIsTop() == 1) {
                viewHolder.tvtop.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvtop.setVisibility(View.INVISIBLE);
            }
            if (articleItem.isMySquare()) {
                viewHolder.tvdelete.setVisibility(View.VISIBLE);
                viewHolder.tvdelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendRequestWithHttpURLConnection
                                ("https://wanandroid.com/lg/user_article/delete/" + articleItem.getId() + "/json", articleItem);
                    }
                });
            } else {
                viewHolder.tvdelete.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

    private void sendRequestWithHttpURLConnection(final String surl, final ArticleItem articleItem) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(surl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.addRequestProperty("Cookie", GlobalData.sessionId);
                    //Log.d("---", surl);
                    connection.connect();
                    //Log.d("----", surl);
                    InputStream in = connection.getInputStream();
                    //Log.d("----", articleItem.getActivity().toString());
                    ((SquareUsersShareArticlesActivity) articleItem.getActivity()).refreshList();
                    //Log.d("----", articleItem.getActivity().toString());
                    //Log.d("---", new ConvertStreamToString(in).getResult());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    @Override
    public int getItemViewType(int position) {
        if (0 == mArticleList.get(position).isImaged()) {
            return ITEM_VIEW_TYPE_UNIMAGED;
        } else {
            return ITEM_VIEW_TYPE_IMAGED;
        }
    }
}
