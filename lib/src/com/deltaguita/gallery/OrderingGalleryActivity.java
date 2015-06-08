package com.deltaguita.gallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.luminous.pick.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderingGalleryActivity extends Activity {
    private GridView gridGallery;
    //    private Handler handler;
    private GalleryAdapter adapter;
    //    private TextView message;
    private ImageView imgNoMedia;
    //    private int maxImageNumber;
//    private String action;
    private ArrayList<PhotoItem> temp;
    private ImageLoader imageLoader;

    //    static volatile Uri sImageUri;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gallery);

        initImageLoader();
        init();
        adapter.addAll(getGalleryPhotos());

        checkImageStatus();
    }

    private void initImageLoader() {

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

//        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);

        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .resetViewBeforeLoading(true).considerExifParams(false)
//                .showImageForEmptyUri(R.drawable.image_errors)
//                .showImageOnLoading(R.drawable.image_loading)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)

                .memoryCacheExtraOptions(800, 800)
                        // default = device screen dimensions
                .diskCacheExtraOptions(800, 800, null)
                .threadPoolSize(5)
                        // default Thread.NORM_PRIORITY - 1
                .threadPriority(Thread.NORM_PRIORITY)
                        // default FIFO
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        // default
//                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                        // default
                .diskCache(
                        new UnlimitedDiscCache(StorageUtils.getCacheDirectory(
                                this, true)))
                        // default
                .diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                        // default
                .imageDecoder(new BaseImageDecoder(false))
                        // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                        // default
                .defaultDisplayImageOptions(imageOptions).build();

        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //先把已選擇的存下來
        temp = adapter.getSelected();

    }

    private void init() {

//        handler = new Handler();
        gridGallery = (GridView) findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(true);
        adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
        findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                setResult(RESULT_CANCELED, data);
                finish();
            }
        });

        gridGallery.setOnItemClickListener(mItemMulClickListener);

        gridGallery.setAdapter(adapter);
        imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);
        findViewById(R.id.btnGalleryOk).setOnClickListener(mOkClickListener);

    }

    private void checkImageStatus() {
        if (adapter.isEmpty()) {
            imgNoMedia.setVisibility(View.VISIBLE);
        } else {
            imgNoMedia.setVisibility(View.GONE);
        }
    }

    View.OnClickListener mOkClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {


            final ArrayList<PhotoItem> selected = adapter.getSelected();
            if (selected.size() == 0) {
                new AlertDialog.Builder(OrderingGalleryActivity.this).setTitle("相片膠卷").setMessage("您還未選擇照片")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent data = new Intent();
                                setResult(RESULT_CANCELED, data);
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        }).setCancelable(true).show();

            } else {
                String[] allPath = new String[selected.size()];
                for (int i = 0; i < allPath.length; i++) {
                    allPath[i] = selected.get(i).sdcardPath;
                }
                Intent data = new Intent().putExtra("all_path", allPath);
                setResult(RESULT_OK, data);
                finish();
            }


        }
    };
    AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {

            adapter.changeSelection(v, position);

        }
    };


    private ArrayList<PhotoItem> getGalleryPhotos() {
        ArrayList<PhotoItem> galleryList = new ArrayList<PhotoItem>();

        try {
            final String[] columns = {MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;

            Cursor imagecursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    PhotoItem item = new PhotoItem();

                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);
                    item.sdcardPath = imagecursor.getString(dataColumnIndex);
                    if (temp != null)
                        for (PhotoItem gallery : temp) {
                            if (gallery.sdcardPath.equals(item.sdcardPath))
                                item.isSeleted = true;
                        }
                    if (new File(item.sdcardPath).exists()) galleryList.add(item);
                }
            }
            imagecursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }

    boolean dupicate() {
        List<String> devices = new ArrayList<String>();
        devices.add("htc");
        return devices.contains(android.os.Build.BRAND.toLowerCase());
    }

    @Override
    protected void onDestroy() {
//        ImageLoader.getInstance().clearMemoryCache();
        super.onDestroy();
    }
}
