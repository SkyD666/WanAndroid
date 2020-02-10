package com.skyd.wanandroid.item;

import android.app.Activity;
import android.text.Html;

import com.skyd.wanandroid.activity.SquareUsersShareArticlesActivity;

public class ArticleItem {
    private String title;
    private String author;
    private String shareUser;
    private String link;
    private String niceDate;
    private String superChapterName;
    private String chapterName;
    private String pictureUrl;
    private Activity activity;
    private boolean isSquare;
    private boolean isMySquare;
    private int userId = -1;
    private int id = -1;
    private int isImaged = 0;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setMySquare(boolean mySquare) {
        isMySquare = mySquare;
    }

    public boolean isMySquare() {
        return isMySquare;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isSquare() {
        return isSquare;
    }

    public void setSquare(boolean square) {
        isSquare = square;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public int isImaged() {
        return isImaged;
    }

    public void setHasImage(int isImaged) {
        this.isImaged = isImaged;
    }

    private int isTop;      //0非置顶，1置顶

    public int getId() {
        return id;
    }

    public ArticleItem(String title, String author, String shareUser, String link, String niceDate
            , String superChapterName, String chapterName, int isTop, int id) {
        this.title = title;
        this.author = author;
        this.shareUser = shareUser;
        this.link = link;
        this.niceDate = niceDate;
        this.superChapterName = superChapterName;
        this.chapterName = chapterName;
        this.isTop = isTop;
        this.id = id;
    }

    public int getIsTop() {
        return isTop;
    }

    public String getLink() {
        return link;
    }

    public String getNiceDate() {
        return niceDate;
    }

    public String getFinalChapterName() {
        if(superChapterName.equals("") && chapterName.equals("")){
            return "";
        } else if(chapterName.equals("")){
            return "分类: " + superChapterName;
        } else if(superChapterName.equals("")){
            return "分类: " + chapterName;
        } else {
            return "分类: " + superChapterName + "/" + chapterName;
        }
    }

    public String getSuperChapterName() {
        return superChapterName;
    }

    public String getChapterName() {
        return chapterName;
    }

    public String getTitle() {
        //return Html.fromHtml(title).toString(); //将html格式的标题解析为普通(转到adapter里解析了)
        return title;
    }

    public String getShareUser() {
        return shareUser;
    }

    public String getFinalAuthor() {
        if (author.equals("")) {
            return shareUser;
        } else {
            return author;
        }
    }

    public String getAuthor() {
        return author;
    }
}
