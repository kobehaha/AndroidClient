package com.se.house;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beans.House;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.se.house.ActivitySellerInsertClientList.Adapter_house.ViewHold;
import com.special.ResideMenuDemo.R;

public class ActivitySellerInsertClientList extends BasicActivity {
	private PopupWindow popupWindow;
	private ImageView img_back, img_add;
	private ListView lv_add_house;
	private Button btn_cancel, btn_sure, btn_show_pop, btn_delete_sure,
			btn_delete_cancel;
	private static final int reques = 1234;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private List<House> listHousee = new ArrayList<House>();
	private LayoutInflater inflater;
	private TextView tv_number;
	private Adapter_house adapter;
	private int[] arry;
	private boolean is_delect = false;
	@SuppressLint("UseSparseArrays") public HashMap<Integer, Boolean> is_select = new HashMap<Integer, Boolean>();// 选择的保存hashmap
	private int number_delect;// 设置删除的数量

	@TargetApi(19)
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.seactivityinsertclient);
		init();

		config();
		set_list_listener();// 设置不同情况下的listview状态
	}

	private void show_pop(View view2) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.popupwindowdeletese, null);
		view.findViewById(R.id.pop_item_delect).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						is_delect = true;
						set_list_listener();// 改变监听器设置
						try {
							adapter.notifyDataSetChanged();// 适配器变化透明度
							RelativeLayout relativeLayout_choose = (RelativeLayout) findViewById(R.id.relative_bottom_sure);
							RelativeLayout relativeLayout_delect = (RelativeLayout) findViewById(R.id.relative_bottom_delect);
							relativeLayout_choose.setVisibility(View.GONE);
							relativeLayout_delect.setVisibility(View.VISIBLE);
							img_add.setVisibility(View.GONE);

						} catch (Exception e) {
							Toast.makeText(ActivitySellerInsertClientList.this,
									"请选择偏好房源", Toast.LENGTH_SHORT).show();
						}

					}
				});
		popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 设置背景
		popupWindow.showAsDropDown(view2, 0, 20);// 设置位置
		popupWindow.update();

	}

	public void set_list_height_based(ListView listView) {// 先获取对应的adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		} else {
			int total_height = 0;
			for (int i = 0; i < listAdapter.getCount(); i++) {

				View item = listAdapter.getView(i, null, listView);
				item.measure(0, 0);
				total_height += item.getMeasuredHeight();

			}
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = total_height
					+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			listView.setLayoutParams(params);
		}

	}

	private void config() {// 配置文件

		File cacheDir = StorageUtils.getOwnCacheDirectory(// 设置缓存
				ActivitySellerInsertClientList.this, "imageloader/Cache");
		@SuppressWarnings("deprecation")
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(// 配置文件
				ActivitySellerInsertClientList.this)
				.threadPoolSize(4)
				.// 线程池大小
				threadPriority(Thread.NORM_PRIORITY - 2)
				.// 线程优先级
				tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCache(new UnlimitedDiskCache(cacheDir))
				.denyCacheImageMultipleSizesInMemory().build();

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageOnFail(R.drawable.head_icon_user)
				.showImageForEmptyUri(R.drawable.ic_empty).cacheOnDisc(true)
				// 设置缓存到sd，内存中
				.displayer(new RoundedBitmapDisplayer(20)).cacheInMemory(true)
				.considerExifParams(true).build();// 设置不同形态的图片
		imageLoader.init(config);

	}

	private void set_list_listener() {
		if (is_delect) {
			lv_add_house.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ViewHold viewHold = (ViewHold) view.getTag();// 获取Viewhold
					viewHold.checkBox.toggle();// 可以改变当前checkbox的状态
					getIsSelected()
							.put(position, viewHold.checkBox.isChecked());
					if (viewHold.checkBox.isChecked() == true) {
						number_delect++;
						refresh_view();

					} else {
						number_delect--;
						refresh_view();
					}

				}

			});

		} else {
			lv_add_house.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

				}
			});
		}

	}

	protected void refresh_view() {// 刷新视图
		tv_number.setText("选中" + String.valueOf(number_delect) + "个");
		adapter.notifyDataSetChanged();

	}

	public void init2(HashMap<Integer, Boolean> isHashMap) {// 初始化hashmap
		if (listHousee.size() == 0) {

		} else {
			for (int i = 0; i < listHousee.size(); i++) {
				getIsSelected().put(i, false);

			}

		}

	}

	public HashMap<Integer, Boolean> getIsSelected() {// 获取selelcted
		return is_select;
	}

	public void setIsSelected(HashMap<Integer, Boolean> isSelected) {// 设置isselcted
		is_select = isSelected;
	}

	private void init() {
		inflater = getLayoutInflater();
		btn_delete_cancel = (Button) findViewById(R.id.btn_insert_client_cancel_delect);
		btn_delete_sure = (Button) findViewById(R.id.btn_insert_client_sure_delect);
		btn_show_pop = (Button) findViewById(R.id.btn_show_delect_choose_house);
		tv_number = (TextView) findViewById(R.id.tv_se_choose_number);
		img_add = (ImageView) findViewById(R.id.img_add_se_client);
		img_back = (ImageView) findViewById(R.id.back9);
		lv_add_house = (ListView) findViewById(R.id.lv_se_insert_client);
		btn_cancel = (Button) findViewById(R.id.btn_insert_client_sure);
		btn_sure = (Button) findViewById(R.id.btn_insert_client_cancel);
		btn_delete_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (int i = 0; i < is_select.size(); i++) {// 获取移除的视图
					Log.v("kobe", String.valueOf(i));
					if (is_select.get(i) == true) {
						try {
							is_select.put(i, false);

							listHousee.remove(listHousee.get(0));// 大小动态变化

						} catch (IndexOutOfBoundsException e) {
							Toast.makeText(ActivitySellerInsertClientList.this,
									"发生错误", Toast.LENGTH_SHORT).show();
						}

					}

				}
				RelativeLayout relativeLayout_choose = (RelativeLayout) findViewById(R.id.relative_bottom_sure);
				RelativeLayout relativeLayout_delect = (RelativeLayout) findViewById(R.id.relative_bottom_delect);
				relativeLayout_choose.setVisibility(View.VISIBLE);
				relativeLayout_delect.setVisibility(View.GONE);
				img_add.setVisibility(View.VISIBLE);

				is_delect = false;
				number_delect = 0;
				set_list_listener();// 改变监听器设置
				set_list_height_based(lv_add_house);// 设置listview的高度，解除scollview的视图压制
				adapter.notifyDataSetChanged();
				tv_number.setText("选中" + String.valueOf(listHousee.size())
						+ "个");

			}
		});
		btn_delete_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				init2(is_select);// 初始化所有选中数据
				number_delect = 0;// 重设置数据
				RelativeLayout relativeLayout_choose = (RelativeLayout) findViewById(R.id.relative_bottom_sure);
				RelativeLayout relativeLayout_delect = (RelativeLayout) findViewById(R.id.relative_bottom_delect);
				relativeLayout_choose.setVisibility(View.VISIBLE);
				relativeLayout_delect.setVisibility(View.GONE);

				img_add.setVisibility(View.VISIBLE);
				is_delect = false;
				set_list_listener();// 改变监听器设置
				refresh_view();

			}
		});
		btn_show_pop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				show_pop(v);

			}
		});
		btn_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listHousee.size() == 0) {
					Toast.makeText(ActivitySellerInsertClientList.this,
							"对不起，您未选择喜爱的偏好房源", Toast.LENGTH_SHORT).show();

				} else {
					arry = new int[listHousee.size()];
					for (int i = 0; i < listHousee.size(); i++) {
						arry[i] = listHousee.get(i).getId();///
						System.out.println("选中的id是"+String.valueOf(arry[i]));

					}
					Intent intent = new Intent(
							ActivitySellerInsertClientList.this,
							ActivitySellerInsertClientInfo.class);
					Bundle bundle = new Bundle();
					bundle.putIntArray("all_house_id", arry);
					intent.putExtras(bundle);
					System.out.println("选中的id是"+String.valueOf(arry));
					startActivity(intent);
				}

			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});
		img_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});
		img_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(ActivitySellerInsertClientList.this,
						ActivitySellerClientChooseHouse.class);
				Bundle bundle = new Bundle();
				if (listHousee.size() == 0) {
					bundle.putBoolean("save_sure", false);
					intent.putExtras(bundle);

				} else {
					ArrayList<CharSequence> now_string = new ArrayList<CharSequence>(// 将现在已经存在的ID传递过去
					);
					Log.v("test_list_house_size",
							String.valueOf(listHousee.size()));
					for (int i = 0; i < listHousee.size(); i++) {
						now_string.add(String
								.valueOf(listHousee.get(i).getId()));
						Log.v("test_list_house_size_i", String.valueOf(i));

					}
					bundle.putBoolean("save_sure", true);
					bundle.putCharSequenceArrayList("save_id", now_string);
					intent.putExtras(bundle);

				}
				startActivityForResult(intent, reques);

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == reques && resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			int a = bundle.getInt("size");
			tv_number.setText("选中" + String.valueOf(a) + "个");
			for (int i = 0; i < a; i++) {
				House house = (House) bundle.getSerializable(String.valueOf(i));
				listHousee.add(house);
			}
			init2(is_select);// 初始化hashmap
			set_list_listener();// 设置监听器
			Log.v("insert_client_list__a", String.valueOf(a));
			Log.v("insert_client_list", String.valueOf(listHousee.size()));
			adapter = new Adapter_house();
			lv_add_house.setAdapter(adapter);

			set_list_height_based(lv_add_house);// 设置listview的高度，解除scollview的视图压制
			adapter.notifyDataSetChanged();

		}
	}

	public class Adapter_house extends BaseAdapter {

		@Override
		public int getCount() {
			return listHousee.size();
		}

		@Override
		public Object getItem(int arg0) {

			return listHousee.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {

			ViewHold viewHold;
			if (arg1 == null) {
				viewHold = new ViewHold();
				arg1 = inflater.inflate(R.layout.listitemhousechoose, null);// inflater
				// convert
				viewHold.image = (ImageView) arg1
						.findViewById(R.id.img_choose_house);
				viewHold.tv_house_name = (TextView) arg1
						.findViewById(R.id.tv_choose_house_info_location);
				viewHold.tv_house_size = (TextView) arg1
						.findViewById(R.id.tv_choose_house_info_size);
				viewHold.tv_house_price = (TextView) arg1
						.findViewById(R.id.tv_choose_house_info_price);
				viewHold.checkBox = (CheckBox) arg1
						.findViewById(R.id.check_choose_house);
				arg1.setTag(viewHold);

			}
			viewHold = (ViewHold) arg1.getTag();
			imageLoader.displayImage(listHousee.get(arg0).getPicture_url()[0],
					viewHold.image, options);// 加载图片
			viewHold.tv_house_name.setText(listHousee.get(arg0).getLocation());
			viewHold.tv_house_price.setText(String.valueOf(listHousee.get(arg0)
					.getPrice()));
			viewHold.tv_house_size.setText(String.valueOf(listHousee.get(arg0)
					.getSize()));
			if (is_delect == false) {// 如果是未被选择
				viewHold.checkBox.setVisibility(View.GONE);
				arg1.setAlpha(1);// 设置透明度全
				// arg1.setBackgroundColor(new Color(#00000000));
				arg1.setBackgroundResource(0);// 设置回无背景色

			} else {// 如果是被选择
				viewHold.checkBox.setVisibility(View.VISIBLE);

				arg1.setBackgroundColor(R.color.black);
				arg1.setAlpha((float) 0.8);

			}

			return arg1;
		}

		public class ViewHold {
			ImageView image;
			TextView tv_house_name, tv_house_size, tv_house_price;
			CheckBox checkBox;

		}

	}
}
