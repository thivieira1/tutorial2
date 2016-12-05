package com.example.vitorhenrique.imagemdownload;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity
{
    Button button;
    ImageView imageView;

    String image_url = "http://www.popcidade.com.br/mussumalive/wp-content/uploads/sites/2/2015/12/Captura-de-Tela-2015-12-04-a%CC%80s-15.48.09-635x254.png";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)findViewById(R.id.button);
        imageView = (ImageView)findViewById(R.id.image_view);
        button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    DownloadTask downloadTask = new DownloadTask();
                        downloadTask.execute(image_url);
                }
            }
        );
    }

    class DownloadTask extends AsyncTask<String, Integer, String>
    {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Download sendo feito...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            String path = params[0];
            int file_length = 0;
            try {
                URL url = new URL(path);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                file_length = urlConnection.getContentLength();
                File new_folder = new File("sdcard/photoalbum");
                if (!new_folder.exists())
                {
                    new_folder.mkdir();
                }

                File input_file = new File(new_folder,"downloadead_image.jpg");
                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
                byte[] data = new byte[1024];
                int total = 0;
                int count = 0;
                OutputStream outputStream = new FileOutputStream(new_folder);
                while ((count=inputStream.read(data))!=1)
                {
                    total+=count;
                    outputStream.write(data,0,count);
                    int progress = (int) total*100/file_length;
                    publishProgress(progress);
                }
                inputStream.close();
                outputStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Download completado...";
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            progressDialog.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(String result)
        {
            progressDialog.hide();
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            String path = "sdcard/photoalbum/downloadead_image.jpg";
            imageView.setImageDrawable(Drawable.createFromPath(path));
        }
    }
}