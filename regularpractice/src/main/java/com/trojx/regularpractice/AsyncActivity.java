package com.trojx.regularpractice;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**AsyncTack实践
 * Created by Administrator on 2016/2/21.
 */
public class AsyncActivity extends AppCompatActivity {
    private ProgressDialog dialog;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);
        iv = (ImageView) findViewById(R.id.iv_async);
    }
    public void load(View view){
        MyAsyncTask task=new MyAsyncTask();
        task.execute("http://7xla0x.com1.z0.glb.clouddn.com/picBayonetta1.png");
    }
    class MyAsyncTask extends AsyncTask<String, Integer,File>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AsyncActivity.this);
            dialog.setTitle("提示信息");
            dialog.setMessage("正在加载...");
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }

        @Override
        protected File doInBackground(String... params) {
            File file=new File("sdcard/AsyncPic.jpg");
            try {
                URL url=new URL(params[0]);
//                url.openConnection().getContentLength();
                InputStream is=url.openStream();
                FileOutputStream fos=new FileOutputStream(file);
                
                int len;
                byte[] buff=new byte[1024];
                while ((len=is.read(buff))!=-1){
                    fos.write(buff,0,len);
                }
                is.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return file;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
            iv.setImageBitmap(bitmap);
            dialog.dismiss();
        }
    }
}
