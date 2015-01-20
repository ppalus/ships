package pl.agh.fis.ships;

import android.app.Activity;
import android.os.Bundle;

public class ShipsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ships);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.container, BluetoothFragment.newInstance()).commitAllowingStateLoss();
        }
    }
}
