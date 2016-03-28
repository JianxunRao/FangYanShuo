package com.trojx.fangyan.Util.jxh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.trojx.fangyan.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**上传家乡话资源
 * Created by Trojx on 2016/3/20.
 */
public class JXH extends AppCompatActivity{

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jxh);


        AVQuery<AVObject> query=new AVQuery<>("jxh");
        query.setLimit(1000);
//        query.whereDoesNotExist("coverPic");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e==null){
                    for(AVObject item:list){
                        attachCover(item);
//                        attachVoice(item);
//                        item.put("isVoice", isVoice(item));
                        item.saveInBackground();
                    }
                    Log.e("list size",list.size()+"");
                }
            }
        });
    }

    /**
     * 解析每一行记录并上传cloud
     * @param record 一行record
     */
    private void parseAndUpload(String record){
        AVObject item=new AVObject("jxh");

        int id=Integer.parseInt(record.split(",")[0].split("=")[1]);
        String title=record.split("'")[1];
        String description=record.split("'")[3];
        String lang=record.split("'")[5];
        String provider_name=record.split("'")[7];

        if(!title.isEmpty()){
            item.put("jxhid",id);
            item.put("title",title);
            item.put("description",description);
            item.put("lang",lang);
            item.put("provider_name",provider_name);
            Log.i("item",item.toString());
            item.saveInBackground();
        }
    }

    /**
     * 上传item的封面（可能一对多）
     *
     */
    private void attachCover(final AVObject item){
        int id=item.getInt("jxhid");
        File file=new File("mnt/shared/jxh/coverPic/"+id+"/");
        if (file.exists()){
//            File[] pics=file.listFiles();
//            ArrayList<AVFile> avFiles=new ArrayList<>();
//            for(File pic:pics){
//                try {
//                    AVFile avFile=AVFile.withFile("jxh_"+id+pic.getName(),pic);
//                    avFile.saveInBackground();
//                    avFiles.add(avFile);
//                } catch (IOException e) {
//                    Log.e("attachCover",e.toString());
//                }
//            }
            File file1=new File("mnt/shared/jxh/coverPic/"+id+"/0.jpg");
            if(file1.exists()){
                try {
                    final AVFile avFile=AVFile.withFile("jxh_"+id+file1.getName(),file1);
                    avFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            item.put("cover",avFile);
                            item.saveInBackground();
                        }
                    });

                } catch (IOException e) {
                    Log.e("save file",e.toString());
                }
            }
        }else {
            Log.w("attachCover", "file missing" + id);
        }
    }

    /**
     * 上传item的音频（OOM）
     * @param item item
     */
    private void attachVoice(AVObject item){
        int id=item.getInt("jxhid");
        File file=new File("mnt/shared/jxh/voiceFile/"+id+".mp3");
        if(file.exists()){
            try {
                AVFile avFile=AVFile.withFile("jxh_"+id+".mp3",file);//大文件会OOM
                avFile.saveInBackground();
                item.put("voiceFile",avFile);
                item.saveInBackground();
            } catch (IOException e) {
                Log.e("attachVoice", e.toString());
            }
        }else {
            Log.e("attachVoice","voice file missing"+id);
        }
    }

    /**
     * 根据本地是否存在音频文件确定是否为音频item
     * @param item 要确认的item
     * @return 是为音频，否为视频
     */
    private boolean isVoice(AVObject item){
        int id=item.getInt("jxhid");
        File file=new File("mnt/shared/jxh/voiceFile/"+id+".mp3");
        return file.exists();
    }
}
