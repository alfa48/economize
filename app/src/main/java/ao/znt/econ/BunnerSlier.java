package ao.znt.econ;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import ss.com.bannerslider.ImageLoadingService;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.adapters.ISliderAdapter;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class BunnerSlier extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bunner_slier);

        Slider.init(new PicassoImageservice(this));
        Slider slider = findViewById(R.id.banner_slider);
        slider.setAdapter(new MainSliderAdapter());
    }
    public static class MainSliderAdapter extends SliderAdapter {
        @Override
        public int getItemCount() {
            return 3;
        }
        @Override
        public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
            switch (position){
                case 0:
                    imageSlideViewHolder.bindImageSlide(R.drawable.banner_1);
                    break;
                case 1:
                    imageSlideViewHolder.bindImageSlide(R.drawable.banner_3);
                    break;
                case 2:
                    imageSlideViewHolder.bindImageSlide(R.drawable.orcamento_scroll);
                    break;
            }
        }
    }
    public static class PicassoImageservice implements ImageLoadingService{
        private final Context context;

        public PicassoImageservice(Context context){
            this.context = context;
        }
        @Override
        public void loadImage(String url, ImageView imageView) { }
        @Override
        public void loadImage(int resource, ImageView imageView) { imageView.setImageResource(resource); }
        @Override
        public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView) { }
    }
}