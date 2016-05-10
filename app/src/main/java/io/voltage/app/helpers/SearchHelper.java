package io.voltage.app.helpers;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import io.voltage.app.R;

public class SearchHelper {

    public void styleSearchView(final Menu menu, final SearchView.OnQueryTextListener listener) {
        final MenuItem item = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) item.getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(listener);
            searchView.setQuery("", true);
            styleSearchViewText(searchView);
        }
    }

    private void styleSearchViewText(final SearchView searchView) {
        final String name = "android:id/search_src_text";
        final Resources resources = searchView.getContext().getResources();
        final int searchPlateId = resources.getIdentifier(name, null, null);
        final EditText editText = (EditText) searchView.findViewById(searchPlateId);
        if (editText != null) {
            editText.setTextColor(Color.WHITE);
            editText.setHint("");
        }
    }
}
