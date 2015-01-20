package pl.agh.fis.ships;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Patryk on 20-Jan-15.
 *
 * Fragment polaczenia bluetooth.
 */
public class BluetoothFragment extends ListFragment {

    public static BluetoothFragment newInstance() {
        return new BluetoothFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ships, container);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
    }
}
