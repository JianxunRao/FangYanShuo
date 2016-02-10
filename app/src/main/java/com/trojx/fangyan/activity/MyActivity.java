package com.trojx.fangyan.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.trojx.fangyan.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

/**
 * Created by Administrator on 2016/1/15.
 */
public class MyActivity extends AppCompatActivity {

    private TextView tv;
    private File[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);

//        tv = (TextView) findViewById(R.id.tv);
//        File file =new File("mnt/shared/voiceFile/zh");
//        files = file.listFiles();
//        Log.e("files size",files.length+"");
//        uploadFiles();

//        AVQuery<AVObject> query=new AVQuery<>("story");
//        query.getFirstInBackground(new GetCallback<AVObject>() {
//            @Override
//            public void done(AVObject avObject, AVException e) {
//                AVObject item = new AVObject("storycomment");
//                item.put("user", AVUser.getCurrentUser());
//                item.put("story", avObject);
//                item.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(AVException e) {
//                        if(e!=null){
//                            Log.e("e",e.toString());
//                        }
//                    }
//                });
//            }
//        });
//        AVUser avUser=AVUser.getCurrentUser();
//        try {
//            AVFile file=AVFile.withAbsoluteLocalPath("xxxxx.jpg","sdcard/bitmapThumbReceiverSmall.jpg");
//            avUser.put("logo",file);
//            avUser.saveInBackground();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        renameUser();


    }
    private void uploadFile(){
        AVQuery<AVObject> query=new AVQuery<>("word");
        query.whereEqualTo("lang", 1 + "");
        query.whereDoesNotExist("voiceFile");
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                tv.setText(i + "");
            }
        });
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                try {
                    Log.e("avobject", avObject.get("voice").toString());
                    AVFile avFile = AVFile.withFile(findFile(avObject.getString("voice")).getName(), findFile(avObject.getString("voice")));
                    avObject.put("voiceFile", avFile);
                    avObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            uploadFile();
                        }
                    });
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
    private void uploadFiles(){
        AVQuery<AVObject> query=new AVQuery<>("word");
        query.setLimit(50);
        query.whereEqualTo("lang", 5 + "");
        query.whereDoesNotExist("voiceFile");
//        query.countInBackground(new CountCallback() {
//            @Override
//            public void done(int i, AVException e) {
//                tv.setText(i+"");
//            }
//        });
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(final List<AVObject> list, AVException e) {
                try {
                    final int[] a = new int[1];
                    a[0] = 0;
                    for (AVObject item : list) {
                        AVFile avFile = AVFile.withFile(findFile(item.getString("voice")).getName(), findFile(item.getString("voice")));
                        item.put("voiceFile", avFile);
                        item.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                a[0]++;
                                if (a[0] == list.size())
                                    uploadFiles();
                            }
                        });
                    }
                } catch (Exception ex) {
                }
            }
        });
    }
    private  File findFile(String s){
        for(int i=0;i<files.length;i++){
            if(files[i].getName().equals(s+".mp3"))
                return files[i];
        }
        return new File("sdcard/Download/httpclient-4.3.1.jar");
    }

    /**
     * 上传story
     */
    private void uploadStorys(){
        try{
            File file=new File("sdcard/story.txt");
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
            String line;
            while((line=br.readLine())!=null){
                if(line.length()>10){
                    String url=line.split("\\|")[0];
                    String name=line.split("\\|")[1];
                    String title=line.split("\\|")[2];
                    double lat=Double.parseDouble(line.split("\\|")[3]);
                    double lng=Double.parseDouble(line.split("\\|")[4]);
                    Log.e("url id",url);
                    uploadStory(name, url, lat, lng, title);
                }
            }
            br.close();
        }catch (Exception e){
            Log.e("upload story error",e.toString());
        }
    }

    /**
     * 上传指定信息的story
     * @param username
     * @param url
     * @param lat
     * @param lng
     * @param title
     */
    private void uploadStory(String username, final String url, final double lat, final double lng, final String title){

        final AVUser avUser=new AVUser();
        avUser.setUsername(username + title.hashCode());
        avUser.setPassword("123456");
        avUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    AVGeoPoint geoPoint = new AVGeoPoint(lat, lng);
                    AVObject story = new AVObject("story");
                    story.put("provider", avUser);
                    story.put("geoPoint", geoPoint);
                    story.put("content", title);
                    story.put("url", url);
                    story.saveInBackground();
                } else {
                    Log.e("upload story error", e.toString());
                }
            }
        });
    }

    /**
     * 为story添加file
     */
    private void uploadStoryFile(){
        AVQuery<AVObject> query=new AVQuery<>("story");
        query.whereExists("url");
        query.whereDoesNotExist("voiceFile");
        query.setLimit(1000);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (AVObject item : list) {
                        String url = item.getString("url");
                        try {
                            AVFile avFile = AVFile.withAbsoluteLocalPath(item.getString("content") + ".mp3", "sdcard/storyFile/" + url + ".mp3");
                            item.put("voiceFile", avFile);
                            item.saveInBackground();
                        } catch (IOException e1) {
                            Log.e("upload file error", e1.toString());
                        }
                    }
                }
            }
        });
    }

    private void getAddress(final AVObject item,final double lat, final double lng){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    StringBuilder url=new StringBuilder();
                    url.append("http://api.map.baidu.com/geocoder/v2/?ak=hRPA3mCvMq00f57wndGUVQ42" +
                            "&mcode=AF:91:42:AA:61:C1:95:22:E6:0C:14:93:0A:DA:FC:45:D6:DC:3B:27;com.trojx.fangyan&callback=renderReverse&location=");
                    url.append(lat+","+lng);
                    url.append("&output=json&pois=0");
                    URL urls=new URL(url.toString());
                    InputStream ins=urls.openStream();
                    BufferedReader br=new BufferedReader(new InputStreamReader(ins));
                    String line;
                    StringBuilder response=new StringBuilder();
                    while ((line=br.readLine())!=null){
                        response.append(line);
                    }
                    JSONObject jsonObject=new JSONObject(response.toString().substring(29));
                    JSONObject result=jsonObject.getJSONObject("result");
                    JSONObject addressComponent=result.getJSONObject("addressComponent");
                    String LocationMsg=new String(addressComponent.getString("province")+addressComponent.getString("city"));
//                    Log.e("city",addressComponent.getString("city"));
//                    Log.e("province",addressComponent.getString("province"));
                    Log.e(lat+","+lng,LocationMsg);
                    item.put("location", LocationMsg);
                    item.saveInBackground();
                }catch (Exception e){
                    Log.e("get location error",e.toString());
                }
            }
        }).start();
    }

    /**
     * 为story添加timelong
     */
    private void addTimeLong(){
        AVQuery<AVObject> query=new AVQuery<>("story");
        query.setLimit(1000);
        query.whereExists("voiceFile");
        query.whereExists("url");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (AVObject item : list) {
                        int timeLong = getTimeLong(item.getString("url"));
                        item.put("timelong", timeLong);
                        item.saveInBackground();
                    }
                }
            }
        });
    }

    /**
     * 获取指定文件的timelong
     * @param url
     * @return
     */
    private int getTimeLong(String url){
        MediaPlayer mp=MediaPlayer.create(this, Uri.parse("sdcard/storyFile/"+url+".mp3"));
//        mp.start();
        return  mp.getDuration();
    }

    /**
     * 重命名user   {"code":1,"error":"Forbidden to find by class permissions."}  增加头像
     */
    private void renameUser(){
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream("sdcard/story.txt"),"GBK"));
            String line;
            File file=new File("sdcard/其他");
            final File[] files=file.listFiles();
            final int lenth=files.length;
            while((line=br.readLine())!=null){
                String name=line.split("\\|")[1];
                String title=line.split("\\|")[2];
                name=name+title.hashCode();
                AVUser user=new AVUser();
                user.logInInBackground(name, "123456", new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        if(e==null){
                            try {
                                AVFile avFile=AVFile.withAbsoluteLocalPath(avUser.getUsername(),files[(int)(Math.random()*lenth)].getAbsolutePath());
                                avUser.put("logo",avFile);
                                avUser.saveInBackground();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }else {
                            Log.e("get user error",e.toString());
                        }
                    }
                });
                AVUser.getCurrentUser().logOut();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
