package tac42.net.fixedtrimmer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by tac on 2015/12/06.
 */
public class Trimmer extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        Toast.makeText(Trimmer.this, "onCreate", Toast.LENGTH_LONG).show();

        Log.d("Trimmer", "onCreate");
        //super.onCreate(savedInstanceState, persistentState);

        Intent intent = getIntent();
        Log.d("Trimmer", "intent:" + intent);
        if (intent.getType().startsWith("image/")) {
            Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (imageUri != null) {
                procImage(imageUri);
            }
        }
    }

    private void procImage(Uri imageUri){
        Toast.makeText(Trimmer.this, "" + imageUri, Toast.LENGTH_LONG).show();
    }
}
