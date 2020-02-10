package com.skyd.wanandroid.adapter;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skyd.wanandroid.R;
import com.skyd.wanandroid.activity.WebsiteActivity;
import com.skyd.wanandroid.item.BannerItem;
import com.skyd.wanandroid.tool.HttpUtil;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
    private ArrayList<BannerItem> bannerItemList;

    @Override
    public int getCount() {
        return bannerItemList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //getView
        final View view = View.inflate(container.getContext(), R.layout.banner_item, null);
        final ImageView imageView = view.findViewById(R.id.iv_banner);
        final int pos = position;
        TextView tv_title = view.findViewById(R.id.tv_banner);
        //if (bannerItemList.get(pos).getPicture() == null) {
        view.post(new Runnable() {
            @Override
            public void run() {
                RequestOptions options = new RequestOptions()
                        .placeholder(R.color.colorWhite)
                        .error(R.color.colorWhite)
                        .override(view.getWidth(), view.getWidth() / 9 * 5);
                Glide.with(imageView.getContext()).load(bannerItemList.get(pos).getImagePath()).apply(options).into(imageView);

                imageView.setDrawingCacheEnabled(true);
                bannerItemList.get(pos).setPicture(imageView.getDrawingCache());
                imageView.setDrawingCacheEnabled(false);
            }
        });
        /*} else {
            imageView.setImageBitmap(bannerItemList.get(pos).getPicture());
        }*/

        tv_title.setText(Html.fromHtml(bannerItemList.get(pos).getTitle()));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HttpUtil.isOnlineByPing("www.baidu.com")) {
                    Intent intent = new Intent(v.getContext(), WebsiteActivity.class);
                    intent.putExtra("surl", bannerItemList.get(pos).getSurl());
                    intent.putExtra("id", -1);
                    intent.putExtra("title", bannerItemList.get(pos).getTitle());
                    v.getContext().startActivity(intent);
                } else {
                    Toast.makeText(v.getContext(), "无法连接网络", Toast.LENGTH_LONG).show();
                }
            }
        });
        //添加到容器中
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public ViewPagerAdapter(ArrayList<BannerItem> bannerItemList) {
        this.bannerItemList = bannerItemList;
    }
}
