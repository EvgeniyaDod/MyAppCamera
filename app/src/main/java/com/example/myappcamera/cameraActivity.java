package com.example.myappcamera;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class cameraActivity extends AppCompatActivity implements View.OnClickListener {

    VideoView myVideo;
    ImageView myImg;
    Button pictureBtn;
    Button upBtn;
    Button leftBtn;
    Button rightBtn;
    Button downBtn;
    Button sendSignal;
    MediaController mc;
    String ip="";
    String log="";
    String pas="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        myVideo = (VideoView) findViewById(R.id.myVideo);
        pictureBtn = (Button) findViewById(R.id.picture);
        upBtn = (Button) findViewById(R.id.up);
        leftBtn = (Button) findViewById(R.id.left);
        rightBtn = (Button) findViewById(R.id.right);
        downBtn = (Button) findViewById(R.id.down);
        myImg=(ImageView)findViewById(R.id.myImg);

        sendSignal=(Button) findViewById(R.id.sendSignal);

        Intent intent = getIntent(); //принимает данные из намерения
        log = intent.getStringExtra("username");
        pas = intent.getStringExtra("password");
        ip = intent.getStringExtra("ipCam");

        String URL=null;
        if(log.equalsIgnoreCase("")&&pas.equalsIgnoreCase(""))
            URL="rtsp://"+ip+"/axis-media/media.amp";
        else
            URL="rtsp://"+log+":"+pas+"@"+ip+"/axis-media/media.amp";
        mc = new MediaController(this);
        myVideo.setVideoURI(Uri.parse(URL)); //169.254.14.71
        myVideo.setMediaController(mc);
        myVideo.requestFocus();
        myVideo.start();
        pictureBtn.setOnClickListener(this);
        sendSignal.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        String auth=null;
        if(log.equalsIgnoreCase("")&&pas.equalsIgnoreCase(""))
            auth=ip;
        else
            auth=log+":"+pas+"@"+ip;
        switch(view.getId()){
            case R.id.picture:
                new DownloadImageTask((ImageView) findViewById(R.id.myImg)).execute("http://"+auth+"/axis-cgi/bitmap/image.bmp");
                break;
            case R.id.sendSignal:
                new RequestTask().execute("http://"+auth+"/axis-cgi/io/output.cgi?action=1:/1000\\");
                break;
        }

    }

    class RequestTask extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... uri) {
            String urlString=uri[0];
            String responseString = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if(conn.getResponseCode() == conn.HTTP_OK){
                    responseString = "OK";
                }
                else {
                    responseString = "FAILED"; // See documentation for more info on response handling
                }
            } catch (IOException e) {
                responseString = "ERROR";
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView Image;

        public DownloadImageTask(ImageView Image) {
            this.Image = Image;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];

            Bitmap img = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                img = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return img;
        }

        protected void onPostExecute(Bitmap result) {
            Image.setImageBitmap(result);
        }
    }
}
