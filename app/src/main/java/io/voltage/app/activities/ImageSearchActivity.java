package io.voltage.app.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.Collection;
import java.util.Collections;

import io.pivotal.arca.adapters.Binding;
import io.pivotal.arca.fragments.ArcaFragment;
import io.pivotal.arca.fragments.ArcaFragmentBindings;
import io.pivotal.arca.fragments.ArcaSimpleRecyclerViewFragment;
import io.voltage.app.R;
import io.voltage.app.activities.ImageSearchActivity.ImageSearchFragment.OnImageSelectedListener;
import io.voltage.app.application.VoltageContentProvider.ImageSearchView;
import io.voltage.app.binders.ImageSearchViewBinder;
import io.voltage.app.helpers.SearchHelper;
import io.voltage.app.requests.ImageSearchQuery;

public class ImageSearchActivity extends ColorDefaultActivity {

    private interface Extras {
        String IMAGE_URL = "image_url";
    }

    public static void newInstance(final Activity activity, final int requestCode) {
        final Intent intent = new Intent(activity, ImageSearchActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public static String extractImageUrl(final Intent intent) {
        return intent.getStringExtra(Extras.IMAGE_URL);
    }

    private static Intent newResult(final String url) {
        final Intent intent = new Intent();
        intent.putExtra(Extras.IMAGE_URL, url);
        return intent;
    }

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_search);
		setTitle(R.string.title_image_search);

        final ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.powered_by_giphy).build();
        final SimpleDraweeView imageView = (SimpleDraweeView) findViewById(R.id.powered_by_giphy);
        imageView.setImageURI(imageRequest.getSourceUri());

        findImageFragment().setOnImageSelectedListener(new ImageListener());
    }

    private ImageSearchFragment findImageFragment() {
        final FragmentManager manager = getFragmentManager();
        return (ImageSearchFragment) manager.findFragmentById(R.id.fragment_image_search);
    }

    public class ImageListener implements OnImageSelectedListener {

        @Override
        public void onImageSelected(final String url) {
            setResult(RESULT_OK, newResult(url));
            finish();
        }
    }

    @ArcaFragment(
        fragmentLayout = R.layout.fragment_image_search,
        adapterItemLayout = R.layout.list_item_search_image,
        binder = ImageSearchViewBinder.class
    )
    public static class ImageSearchFragment extends ArcaSimpleRecyclerViewFragment implements SearchView.OnQueryTextListener {

        public interface OnImageSelectedListener {
            void onImageSelected(String url);
        }

        private OnImageSelectedListener mListener;

        @ArcaFragmentBindings
        private static final Collection<Binding> BINDINGS = Collections.singletonList(
            new Binding(R.id.search_image, ImageSearchView.Columns.IMAGE_URL)
        );

        @Override
        public void onViewCreated(final View view, final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            setHasOptionsMenu(true);

            onQueryTextChange("");
        }

        public void setOnImageSelectedListener(final OnImageSelectedListener listener) {
            mListener = listener;
        }

        @Override
        public void onItemClick(final RecyclerView recyclerView, final View view, final int position, final long id) {
            final Cursor cursor = (Cursor) getRecyclerViewAdapter().getItem(position);
            final String url = cursor.getString(cursor.getColumnIndex(ImageSearchView.Columns.IMAGE_URL));

            if (mListener != null) {
                mListener.onImageSelected(url);
            }
        }

        @Override
        public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
            inflater.inflate(R.menu.fragment_search, menu);

            new SearchHelper.Default().styleSearchView(menu, this);
        }

        @Override
        public boolean onQueryTextChange(final String text) {
            if (!TextUtils.isEmpty(text)) {
                execute(new ImageSearchQuery(text));

                getViewManager().showProgressView();
            } else {
                getViewManager().showEmptyView();
            }
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(final String query) {
            return false;
        }
    }
}