# 2020红岩移动开发Android考核：WanAndroid

内有APP简要介绍、主要技术及知识点、心得体会三大部分内容

## APP简要介绍（仓库app/release/app-release.apk的安装包比发邮件时的更新了）

### 未使用第三方库：

```xml
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'androidx.appcompat:appcompat:1.0.2'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test.ext:junit:1.1.0'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.1.1'
    implementation 'com.github.bumptech.glide:glide:4.10.0'
    implementation 'androidx.recyclerview:recyclerview:1.1.0'
    implementation 'com.google.android.material:material:1.0.0'
}
```

### 1.部分界面

![image](https://github.com/SkyD666/WanAndroid/raw/master/ScreenShots/首页.gif)![image](https://github.com/SkyD666/WanAndroid/raw/master/ScreenShots/体系.jpg)![image](https://github.com/SkyD666/WanAndroid/blob/master/ScreenShots/项目.jpg)![image](https://github.com/SkyD666/WanAndroid/blob/master/ScreenShots/广场.jpg)![image](https://github.com/SkyD666/WanAndroid/blob/master/ScreenShots/我的.jpg)![image](https://github.com/SkyD666/WanAndroid/blob/master/ScreenShots/登录.jpg)![image](https://github.com/SkyD666/WanAndroid/blob/master/ScreenShots/注册.jpg)![image](https://github.com/SkyD666/WanAndroid/blob/master/ScreenShots/排行榜.jpg)![image](https://github.com/SkyD666/WanAndroid/blob/master/ScreenShots/TODO.jpg)

### 2.主要功能(实现API所提供功能)

#### 1) 首页：

​		实现轮播banner、显示首页文章、在Toolbar菜单里有“收起/显示轮播”、“搜索”、“公众号”、“常用网址”、“导航”、“问答”功能。

##### 				部分功能进一步解释：

###### 				轮播：

​				显示首页banner数据，用ViewPager、ImageView、TextView和ViewGroup实现，并实现了自动切换，		点击轮播即可进入此文章

###### 				搜索：

​				显示有搜索热词、用户可以在搜索框输入内容进行搜索

###### 				公众号：

​				在Toolbar菜单显示有公众号列表，进行选择便可查看该公众号的文章。还可以在选中某个公众号后对此		公众号的文章进行搜索

###### 				导航：

​				分为多个不同类别，每个类别有多个不同的文章，使用流式布局实现，点击即可进入文章

###### 				问答：

​				内为“每日一问”的文章

#### 2) 体系文章：

​		分为多个不同类别，每个类别有多个不同的子类别，使用流式布局实现，点击子类别即可进入文章列表。通过Toolbar上的搜索框可以按作者进行体系下文章的搜索

#### 3) 项目：

​		包括最新项目和项目分类文章，用TabLayout和ViewPager实现

#### 4) 广场：

​		其他用户分享的文章，自己也可以在这里分享文章，还可以查看指定用户分享的文章

##### 				部分功能进一步解释：

###### 				自己分享文章：

​				通过点击Toolbar上的按钮，输入“文章名称”和“网站”来分享

###### 				查看自己分享的文章：

​				通过点击Toolbar上的按钮来查看，内显示用户昵称、ID、等级、排名、积分和分享的文章信息

###### 				查看其他人分享的文章：

​				通过点击文章列表上蓝色的作者昵称来查看，内显示作者昵称、ID、等级、排名、积分和分享的文章信		息

#### 5) 我的：

​		可进行用户注册、登录、退出，可以查看个人TODO、用户积分、收藏的文章、网站

##### 						部分功能进一步解释：

###### 						登录：

​				可以自动保存登录状态，

###### 						个人TODO：

​				通过点击Toolbar上的菜单来查看不同类别下的TODO。可以点击Toolbar上的“+”，来添加TODO。通过		点击TODO上左侧第一个按钮可以实现完成与未完成状态的切换，点击第二个按钮可以删除此TODO，点击第		三个按钮可以对此TODO进行编辑

###### 						用户积分：

​				可以查看当前用户的积分，还显示用户昵称、ID、等级、排名和积分获取来源列表。点击Toolbar的“积		分排行榜“按钮可以显示所有用户的排行总榜

###### 						用户收藏文章：

​				显示用户收藏过的文章，通过Toolbar的“添加站外文章“按钮可以添加站外文章

###### 				用户收藏网站：

​				显示用户收藏的网站，点击可以进入此网站。通过Toolbar的“添加“按钮可以添加网站；”编辑“按钮用于		修改添加过的网站信息，点击后背景变为绿色，此时点击网站便可进行修改；”删除“按钮用于删除添加过的网		站，点击后背景变为红色，此时点击网站便可进行删除。以上”编辑“、”删除“操作必须再次点击”完成编辑操		作“”完成删除操作“才可退出”编辑“”删除“状态

### 3.使用步骤

​		若不进行登录则不能进行文章收藏、网站收藏、TODO等操作，也不能进行分享文章。建议进行登录后再使用

## 主要技术及知识点

### 1.对网络请求进行封装

​		对常用的<u>***GET和POST请求进行封装***</u>，并设置了<u>***HttpCallbackListener监听器***</u>，同时也封装了判断网络是否可用

### 2.自定义View

​		通过自定义View实现了<u>***流式布局***</u>

### 3.主页轮播（ViewPager的使用）

​		通过ViewPager实现了***<u>主页轮播</u>***的实现

### 4.AlertDialog上显示多个控件（使用xml布局）

​		使用xml文件，可以实现在***<u>AlertDialog显示多个控件</u>***，达到接收多个用户信息的效果

### 5.通过获取cookie来取得sessionId信息，以便进行<u>***自动登录***</u>

### 6.TabLayout和ViewPager的使用（多个Fragment之间切换）

### 7.通过一个类来实现真正的全局数据

### 8.Fragment的使用

​		通过Fragment与RadioGroup的组合使用实现不同选项卡的切换

### 9.重视了RecyclerView的复用问题（如网络加载图片混乱问题）

### 10.对屏幕旋转后界面显示混乱的问题进行修复

### 11..9图片（启动页图片）制作（9-Patch File）

### 12.APP内图标全为亲手制作

## 心得体会

​		通过一整个寒假每天的学习，让我收获许多。对AS的使用越来越熟练，增加了许多知识，使用RecyclerView更加顺手，还学习了ViewPager、Fragment、RadioGroup、TabLayout等等，学习了对工具进行封装，自定义View。同时，在开发此软件过程中也踩了不少坑（如RecyclerView复用问题，屏幕旋转后界面混乱问题、Toolbar上SearchView显示异常的问题、异步网络请求的问题、线程过多导致崩溃的问题、runOnUiThread的问题等等），有些坑甚至是一搞就是一天，但是，正是因为这些坑，让我对细节更加重视起来。在编写xml文件时，我也思考了如何让程序界面更加美观……总之，经过这一个寒假的学习，我收获很多。我很感激WanAndroid的开放API，感激红岩网校学长学姐对我的帮助与指导，也感激一直努力的自己！相信在接下来的学习中，我会努力下去，收获更多！
