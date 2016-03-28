package com.trojx.fangyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.avos.avoscloud.AVUser;
import com.gc.materialdesign.widgets.Dialog;
import com.trojx.fangyan.R;
import com.trojx.fangyan.fragment.JxhFragment;
import com.trojx.fangyan.fragment.MeFragment;
import com.trojx.fangyan.fragment.StoryFragment;
import com.trojx.fangyan.fragment.WordFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**主界面
 * Created by Administrator on 2016/1/18.
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Date lastClickBack;
    private static final String[] tabTitles={"词典","故事","家乡话","我的"};
    private MyAdapter adapter;
    private TabLayout tabLayout;
    private ImageButton ib_main_action;
    private static final int REQUEST_NEW_STORY=5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ib_main_action = (ImageButton) findViewById(R.id.ib_main_action);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        if(viewPager !=null){
            setupViewpager(viewPager);
        }
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        for(int i=0;i< tabLayout.getTabCount();i++){
            TabLayout.Tab tab= tabLayout.getTabAt(i);
            if(tab!=null){
                switch (i){
                    case 0:
                        tab.setIcon(getResources().getDrawable(R.drawable.iconfont_dict_selected));
                        break;
                    case 1:
                        tab.setIcon(getResources().getDrawable(R.drawable.iconfont_story_normal));
                        break;
                    case 2:
                        tab.setIcon(getResources().getDrawable(R.drawable.iconfont_media_normal));
                        break;
                    case 3:
                        tab.setIcon(getResources().getDrawable(R.drawable.iconfont_me_normal));
                    default:
                        break;
                }
            }
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabSelected(TabLayout.Tab tab) {
              viewPager.setCurrentItem(tabLayout.getSelectedTabPosition(), false);
              switch (tabLayout.getSelectedTabPosition()) {
                  case 0:
                      tab.setIcon(R.drawable.iconfont_dict_selected);
                      break;
                  case 1:
                      tab.setIcon(R.drawable.iconfont_story_selected);
                      break;
                  case 2:
                      tab.setIcon(R.drawable.iconfont_media_selected);
                      break;
                  case 3:
                      tab.setIcon(R.drawable.iconfont_me_selected);
                      break;
                  default:
                      break;
              }
          }

          @Override
          public void onTabUnselected(TabLayout.Tab tab) {
              switch (tabLayout.getSelectedTabPosition()) {
                  case 0:
                      tab.setIcon(R.drawable.iconfont_dict_normal);
                      break;
                  case 1:
                      tab.setIcon(R.drawable.iconfont_story_normal);
                      break;
                  case 2:
                      tab.setIcon(R.drawable.iconfont_media_normal);
                      break;
                  case 3:
                      tab.setIcon(R.drawable.iconfont_me_normal);
                      break;
                  default:
                      break;
              }
          }

          @Override
          public void onTabReselected(TabLayout.Tab tab) {
          }
      });

        ib_main_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = viewPager.getCurrentItem();
                switch (i) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, NewWordActivity.class);
                        intent.putExtra("word", "");
                        startActivity(intent);
                        break;
                    case 1:
                        if(AVUser.getCurrentUser()!=null){
                            Intent intent1 = new Intent(MainActivity.this, NewStoryActivity.class);
                            startActivityForResult(intent1, REQUEST_NEW_STORY);
                            break;
                        }else {
                            final Dialog dialog=new Dialog(MainActivity.this,"未登录","发布新的故事需要先登录");
                            dialog.show();
                            dialog.getButtonAccept().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            dialog.getButtonAccept().setText("好的");
                            dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                            break;
                        }
                    case 3:
                        Intent intent2 = new Intent(MainActivity.this, SettingActivity.class);
                        startActivityForResult(intent2,MeFragment.REQUEST_LOGIN);
                        break;
                    default:
                        break;
                }
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        ib_main_action.setVisibility(View.VISIBLE);
                        ib_main_action.setImageResource(R.drawable.ic_add_white_48dp);
                        break;
                    case 1:
                        ib_main_action.setVisibility(View.VISIBLE);
                        ib_main_action.setImageResource(R.drawable.ic_add_white_48dp);
                        break;
                    case 2:
                        ib_main_action.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        ib_main_action.setVisibility(View.VISIBLE);
                        ib_main_action.setImageResource(R.drawable.ic_settings_white_48dp);
                    break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    private void setupViewpager(ViewPager viewPager){
        adapter = new MyAdapter(getSupportFragmentManager());
        adapter.addFragment(new WordFragment(), tabTitles[0]);
        adapter.addFragment(new StoryFragment(), tabTitles[1]);
//        adapter.addFragment(new DiscoverFragment(),"发现");
        adapter.addFragment(new JxhFragment(),tabTitles[2]);
        adapter.addFragment(new MeFragment(), tabTitles[3]);
        viewPager.setAdapter(adapter);
    }
    class MyAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments=new ArrayList<>();
        private List<String> fragmentsTitles=new ArrayList<>();

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            fragmentsTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitles.get(position);
        }
    }




//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.it_add_new:
//                int i=viewPager.getCurrentItem();
//                if(i==0){
//                    Intent intent=new Intent(MainActivity.this,NewWordActivity.class);
//                    intent.putExtra("word","");
//                    startActivity(intent);
//                }else if(i==1){
//                    Intent intent=new Intent(MainActivity.this,NewStoryActivity.class);
//                    startActivity(intent);
//                }
//                break;
//            case R.id.it_setting:
//                Intent intent=new Intent(MainActivity.this,AboutActivity.class);
//                startActivity(intent);
//                break;
//            default:
//                break;
//        }
//        return true;
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_NEW_STORY&&resultCode==RESULT_OK){
            StoryFragment storyFragment= (StoryFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":1");
            if(storyFragment!=null){
                storyFragment.skip=0;
                storyFragment.storyList.clear();
                storyFragment.getStory();
            }
            MeFragment meFragment= (MeFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":3");
            if(meFragment!=null){
                meFragment.refresh();
            }
        }

    }

    /**
     * 两次返回退出
     */
    @Override
    public void onBackPressed() {
        if(lastClickBack==null){
            lastClickBack = new Date();
        }else {
            Date currentClickBack=new Date();
            if((currentClickBack.getTime()-lastClickBack.getTime())<2000){
                finish();
            }else {
                lastClickBack=currentClickBack;
            }
        }

    }

}
