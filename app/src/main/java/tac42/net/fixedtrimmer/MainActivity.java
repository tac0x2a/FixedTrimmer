package tac42.net.fixedtrimmer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    EditText x1,y1,x2,y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String posString = sp.getString("pos", "0,0,0,0");
        String[] posArray = posString.split(",");

        x1 = (EditText)findViewById(R.id.editText_x1); x1.setText(posArray[0]);
        y1 = (EditText)findViewById(R.id.editText_y1); y1.setText(posArray[1]);
        x2 = (EditText)findViewById(R.id.editText_x2); x2.setText(posArray[2]);
        y2 = (EditText)findViewById(R.id.editText_y2); y2.setText(posArray[3]);

        Button saveButton = (Button)findViewById(R.id.button_save);
        saveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String saveString =
                                x1.getText().toString() + "," +
                                        y1.getText().toString() + "," +
                                        x2.getText().toString() + "," +
                                        y2.getText().toString();

                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString("pos", saveString);
                        edit.commit();

                        Toast.makeText(MainActivity.this, "Save:" + saveString, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Intent intent = getIntent();
        Log.d("Trimmer", "intent:" + intent);

        if(intent == null) return;
        if(intent.getType() == null) return;
        if(! intent.getType().startsWith("image/")) return;

        try{
            procImage(intent);
            finish();
        } catch(Exception ex){
            Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void procImage(Intent imageIntent) throws IOException {
        Uri imageUri = (Uri) imageIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri == null) return;

        //Toast.makeText(MainActivity.this, "" + imageUri, Toast.LENGTH_LONG).show();

        Bitmap image = getBitmapFromUri(imageUri);
        //Toast.makeText(MainActivity.this, "" + image.getWidth() + "x" + image.getHeight(), Toast.LENGTH_LONG).show();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String posString = sp.getString("pos", "0,0,0,0");
        String[] posArray = posString.split(",");
        int x1 = Integer.parseInt(posArray[0]);
        int y1 = Integer.parseInt(posArray[1]);
        int x2 = Integer.parseInt(posArray[2]);
        int y2 = Integer.parseInt(posArray[3]);
        int w = x2-x1;
        int h = y2-y1;

        image = Bitmap.createBitmap(image, x1, y1, w, h, null, true);

        String dirPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();


        File dir =new File(dirPath);
        if(! dir.exists()){
            dir.mkdirs();
        }

        // 日付でファイル名を作成　
        Date mDate = new Date();
        SimpleDateFormat fileName = new SimpleDateFormat("yyyyMMdd_HHmmss");

        // 保存処理開始
        FileOutputStream fos = null;
        fos = new FileOutputStream(new File(dirPath, fileName.format(mDate) + ".jpg"));

        // PNGで保存
        image.compress(Bitmap.CompressFormat.PNG, 100, fos);

        // 保存処理終了
        fos.close();

        //Toast.makeText(MainActivity.this, "Save:" + dirPath + "/" + fileName, Toast.LENGTH_LONG).show();

    }

    private Bitmap getBitmapFromUri(Uri uri)throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
