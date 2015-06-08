package deltaguita.sample;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wells on 2015/4/26.
 */


public class BigImagesActivity extends Activity{

    ViewPager viewPager;

    private static List<View> viewListBigImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_images);
        viewPager = (ViewPager) findViewById(R.id.viewPagerBigImage);

        String[] images = getIntent().getStringArrayExtra("images");
        String currentImage = getIntent().getStringExtra("currentImage");
        initImageView(images, currentImage);

    }
    public void initImageView(String[] images, String currentImage) {

        int currentPosition = 0;

        viewListBigImage = new ArrayList<>();
        int imageCount = images.length;
        for(int i = 0; i < imageCount; i++) {

            if(currentImage.equals(images[i])) {
                currentPosition = i;
            }

            View big_image_content = LayoutInflater.from(this).inflate(R.layout.adapter_big_image, viewPager, false);
            viewListBigImage.add(big_image_content);

            ImageView iv = (ImageView) big_image_content.findViewById(R.id.imgItemDetailBigImage);
            ImageLoader.getInstance().displayImage("file://" +images[i], iv);
            TextView tv = (TextView) big_image_content.findViewById(R.id.imgItemDetailBigImageText);
            String imageNum = String.format("%1$s/%2$s", (i + 1), imageCount);
            tv.setText(imageNum);

        }

        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return viewListBigImage.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewListBigImage.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                try {
                    container.addView(viewListBigImage.get(position));
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                return viewListBigImage.get(position);
            }
        };
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentPosition);

    }


}
