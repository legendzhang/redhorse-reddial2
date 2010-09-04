package com.redhorse.reddial2;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.redhorse.reddial2.reddial2;
import com.redhorse.reddial2.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.Contacts.Phones;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class reddial2 extends Activity implements OnItemClickListener {

	private GridView mGrid;
	private dbDialConfigAdapter dbDial = null;
	private List<String> mContactURI;
	private List<String> mContactID;
	private List<String> mContact;
	private List<Bitmap> mContactPhoto;
	private static final int STARTCONFIG_REQUEST = 1;
	private static final int STARTWEIBO_REQUEST = 2;
	private static final int STARTPICK_REQUEST = 3;

	private int itempos;
	private SharedPreferences share;

	private Uri uri = null;
	private String columnName = null;
	private String columnID = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = new Intent();
		intent.setClass(this, ServiceRed.class);
		startService(intent);

		dbDial = new dbDialConfigAdapter(this);
		dbDial.open();

		share = this.getPreferences(MODE_PRIVATE);

		int os_version = Integer.parseInt(Build.VERSION.SDK.toString());
		if (os_version > 4) {// 2.x，sdk版本
			uri = Uri.parse("content://com.android.contacts/contacts");// new
			columnName = ContactsContract.Contacts.DISPLAY_NAME;
			columnID = ContactsContract.Contacts._ID;
		} else {// 1.6以下SDK
			uri = Contacts.People.CONTENT_URI;
			columnName = Contacts.People.NAME;
			columnID = Contacts.People._ID;
		}

		loadApps();

		setContentView(R.layout.main);
		mGrid = (GridView) findViewById(R.id.myGrid);
		mGrid.setAdapter(new AppsAdapter());
		mGrid.setOnItemClickListener(this);
		Button button = (Button) findViewById(R.id.Button01);
		button.setOnClickListener(Button01Listener);
		button = (Button) findViewById(R.id.Button03);
		button.setOnClickListener(Button03Listener);
		button = (Button) findViewById(R.id.weibogrid);
		button.setOnClickListener(weibogridListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		new Intent();
		switch (requestCode) {
		case STARTCONFIG_REQUEST:
			switch (resultCode) {
			case RESULT_OK:
				break;
			default:
				break;
			}
			break;
		case STARTPICK_REQUEST:
			switch (resultCode) {
			case RESULT_OK:
				Bundle b = data.getExtras();
				String uid = b.getString("uid");

				mContactID.set(itempos, uid);
				Uri auri = Uri.parse(uri.toString() + "/" + uid);
				Editor editor = share.edit();
				editor.putString("dial" + Integer.toString(itempos),
						auri.toString());
				editor.commit();// 提交刷新数据
				mContactURI.set(itempos, auri.toString());
				Cursor ac = managedQuery(auri, null, null, null, null);
				ac.moveToFirst();
				String name = ac.getString(ac
						.getColumnIndexOrThrow(columnName));// People.NAME
				String userid = ac.getString(ac
						.getColumnIndexOrThrow(columnID));// People._ID
				//取头像
				ContentResolver cr = getContentResolver();
				Uri photouri = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI,
						Long.parseLong(userid));
				InputStream input = ContactsContract.Contacts
						.openContactPhotoInputStream(cr, photouri);
				Bitmap mBitmap = BitmapFactory.decodeStream(input);
				mContact.set(itempos, name);
				mContactPhoto.set(itempos, mBitmap);
				mGrid.setAdapter(new AppsAdapter());
				break;
			default:
				break;
			}
			break;
		case STARTWEIBO_REQUEST:
			break;
		default:
			finish();
			break;
		}
		Log.e("reddial2", "back");
	}

	private OnClickListener Button01Listener = new OnClickListener() {
		public void onClick(View v) {
			Intent i = new Intent(Intent.ACTION_VIEW, uri);
			startActivityForResult(i, 1);
		}
	};

	private OnClickListener Button03Listener = new OnClickListener() {
		public void onClick(View v) {
			Intent i = getIntent();
			Bundle b = new Bundle();
			b.putString("msg", "quit");
			i.putExtras(b);
			reddial2.this.setResult(RESULT_OK, i);
			dbDial.close();
			reddial2.this.finish();
		}
	};

	private OnClickListener weibogridListener = new OnClickListener() {
		public void onClick(View v) {
			Intent setting = new Intent();
			setting.setClass(reddial2.this, weibo.class);
			startActivityForResult(setting, STARTWEIBO_REQUEST);
		}
	};

	private void loadApps() {
		mContactID = new ArrayList<String>();
		mContactURI = new ArrayList<String>();
		mContact = new ArrayList<String>();
		mContactPhoto = new ArrayList<Bitmap>();

		mContactURI.add(share.getString("dial0", ""));
		mContactURI.add(share.getString("dial1", ""));
		mContactURI.add(share.getString("dial2", ""));
		mContactURI.add(share.getString("dial3", ""));
		mContactURI.add(share.getString("dial4", ""));
		mContactURI.add(share.getString("dial5", ""));
		mContactURI.add(share.getString("dial6", ""));
		mContactURI.add(share.getString("dial7", ""));
		mContactURI.add(share.getString("dial8", ""));
		mContactURI.add(share.getString("dial9", ""));
		mContactURI.add(share.getString("dial10", ""));
		mContactURI.add(share.getString("dial11", ""));

		for (int i = 0; i < mContactURI.size(); i++) {
			if (mContactURI.get(i).toString().equalsIgnoreCase("")) {
				mContactID.add("");
				mContact.add("");
				mContactPhoto.add(null);
				// Resources res = this.getResources();
				// Bitmap mBitmap = ((BitmapDrawable)
				// res.getDrawable(R.drawable.contact)).getBitmap();
				// mContactPhoto.add(mBitmap);
			} else {
				Uri auri = Uri.parse(mContactURI.get(i).toString());
				Cursor ac = managedQuery(auri, null, null, null, null);
				ac.moveToFirst();
				String name = ac.getString(ac
						.getColumnIndexOrThrow(columnName));// People.NAME
				String userid = ac.getString(ac
						.getColumnIndexOrThrow(columnID));// People._ID
				//取头像
				ContentResolver cr = getContentResolver();
				Uri photouri = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI,
						Long.parseLong(userid));
				InputStream input = ContactsContract.Contacts
						.openContactPhotoInputStream(cr, photouri);
				Bitmap mBitmap = BitmapFactory.decodeStream(input);
				mContactID.add(userid);
				mContact.add(name);
				mContactPhoto.add(mBitmap);
			}
		}
	}

	// 重点在这里面
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		// TODO Auto-generated method stub
		itempos = position;
		final List<String> num = new ArrayList<String>();
		if (mContactURI.get(position).toString().equalsIgnoreCase("")) {
			CharSequence[] cs = { "设置联系人" };
			AlertDialog opDialog = new AlertDialog.Builder(reddial2.this)
					.setTitle("选项")
					.setItems(cs, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent setting = new Intent();
							setting.setClass(reddial2.this, peoplelist.class);
							startActivityForResult(setting, STARTPICK_REQUEST);
						}
					}).create();
			opDialog.show();
		} else {
			Cursor phones = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ mContactID.get(position).toString(), null, null);
			;
			while (phones.moveToNext()) {
				String phoneNumber = phones.getString(phones
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				String phoneNumbertype = phones.getString(phones
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				// 多个号码如何处理
				num.add(ContactsContract.CommonDataKinds.Phone.getTypeLabel(this.getResources(),
						Integer.parseInt(phoneNumbertype), "")
						+ ":" + phoneNumber);
			}
			phones.close();
			num.add("设置联系人");

			CharSequence[] cs = num.toArray(new CharSequence[num.size()]);
			AlertDialog opDialog = new AlertDialog.Builder(reddial2.this)
					.setTitle("选项")
					.setItems(cs, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if ((((AlertDialog) dialog).getListView()
									.getCount() - 1) != which) {
								Uri uri = Uri.parse("tel:"
										+ num.get(which).toString());
								Intent it = new Intent(Intent.ACTION_CALL, uri);
								startActivity(it);
							} else {
								Intent setting = new Intent();
								setting.setClass(reddial2.this,
										peoplelist.class);
								startActivityForResult(setting,
										STARTPICK_REQUEST);
							}
						}
					}).create();
			opDialog.show();
		}
	}

	public class AppsAdapter extends BaseAdapter {
		public AppsAdapter() {
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			// 从layout文件生成list里面的内容
			convertView = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.listitem, null);

			TextView mTextView = (TextView) convertView
					.findViewById(R.id.imageTitle);
			mTextView.setText(mContact.get(position));
			ImageView mImageView = (ImageView) convertView
					.findViewById(R.id.imageView);
			mImageView.setImageBitmap(mContactPhoto.get(position));
			GradientDrawable grad = new GradientDrawable(
					Orientation.TOP_BOTTOM, new int[] { Color.DKGRAY,
							Color.BLACK });
			convertView.setBackgroundDrawable(grad);
			return convertView;

		}

		public final int getCount() {
			return mContact.size();
		}

		public final Object getItem(int position) {
			return mContact.get(position);
		}

		public final long getItemId(int position) {
			return position;
		}

		public View addTitleView(Bitmap image, String title) {
			LinearLayout layout = new LinearLayout(reddial2.this);
			layout.setOrientation(LinearLayout.VERTICAL);

			ImageView iv = new ImageView(reddial2.this);
			iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
			iv.setLayoutParams(new GridView.LayoutParams(60, 60));
			iv.setImageBitmap(image);

			layout.addView(iv);

			TextView tv = new TextView(reddial2.this);
			// tv.setTransformationMethod(SingleLineTransformationMethod.getInstance());
			tv.setSingleLine(true);
			tv.setText(title);
			tv.setTextSize(18);

			layout.addView(tv, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.FILL_PARENT));

			layout.setGravity(Gravity.CENTER);
			return layout;
		}

	}

	// 创建菜单
	private final static int ITEM_ID_SETTING = 11;
	private final static int ITEM_ID_ABOUT = 12;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		// menu.add(1, ITEM_ID_SETTING, 0, R.string.setting).setIcon(
		// R.drawable.menu_syssettings);
		menu.add(1, ITEM_ID_ABOUT, 0, R.string.about).setIcon(
				R.drawable.menu_help);
		return true;
	}

	// 给菜单加事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case ITEM_ID_SETTING:
			// Intent setting = new Intent();
			// setting.setClass(redhorse.this, reddial2.class);
			// startActivity(setting);
			break;
		case ITEM_ID_ABOUT:
			Intent setting = new Intent();
			setting.setClass(reddial2.this, Feedback.class);
			startActivity(setting);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}