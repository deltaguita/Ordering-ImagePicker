package deltaguita.sample;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.deltaguita.gallery.OrderingGalleryActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_PHOTOS = 203;
    ArrayList<String> photoPathList = new ArrayList<String>();
    HorizontalScrollView horizontalScrollView;
    LinearLayout imageContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        horizontalScrollView = (HorizontalScrollView)findViewById(R.id.scrollView);
        imageContainer = (LinearLayout)findViewById(R.id.imageContainer);

    }


    public void pickImages(View view) {
        Intent intent = new Intent(this, OrderingGalleryActivity.class);
        startActivityForResult(intent, REQUEST_CODE_PHOTOS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PHOTOS)
        handlePhotos(resultCode,data);

    }

    private void handlePhotos(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        String[] all_path = data.getStringArrayExtra("all_path");
        for (final String path : all_path) {
            photoPathList.add(path);
            appendPhoto(path);
        }
        horizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                horizontalScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
            }
        });
    }

    /**
     * appendPhoto
     *
     * @param path 將照片添加到清單
     */
    private void appendPhoto(final String path) {
//
        final View view = LayoutInflater.from(this)
                .inflate(R.layout.adapter_append_file_photo, imageContainer, false);
        view.setTag(path);
        view.setOnClickListener(onImageClick);

        ImageLoader.getInstance().displayImage("file://" + path,(ImageView)view.findViewById(R.id.image));
        imageContainer.addView(view, imageContainer.getChildCount());
        horizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                horizontalScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }


    private View.OnClickListener onImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, BigImagesActivity.class);
            intent.putExtra("images", photoPathList.toArray(new String[photoPathList.size()]));
            intent.putExtra("currentImage", (String) v.getTag());
            startActivity(intent);
        }
    };

}
