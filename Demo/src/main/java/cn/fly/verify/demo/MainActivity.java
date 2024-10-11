package cn.fly.verify.demo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.fly.verify.CustomUIRegister;
import cn.fly.verify.CustomViewClickListener;
import cn.fly.verify.GetTokenCallback;
import cn.fly.verify.OAuthPageEventCallback;
import cn.fly.verify.PageCallback;
import cn.fly.verify.PreVerifyCallback;
import cn.fly.verify.FlyVerify;
import cn.fly.verify.UiLocationHelper;
import cn.fly.verify.VerifyCallback;
import cn.fly.verify.common.exception.VerifyException;
import cn.fly.verify.datatype.UiSettings;
import cn.fly.verify.datatype.VerifyResult;
import cn.fly.verify.demo.login.LoginTask;

import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.fly.verify.demo.util.CustomizeUtils;
import cn.fly.verify.demo.util.PrivacyDialogUtils;
import cn.fly.verify.ui.component.CommonProgressDialog;
import pl.droidsonroids.gif.GifImageView;



public class MainActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "MainActivity-Demo";
	private static final int REQUEST_CODE = 1001;
	private GifImageView logoIv;
	private Button verifyBtn;
	private Button verifyDialogBtn;
	private TextView versionTv;
	private TextView appNameTv;
	private TextView tvResult;
	private TextView uiTest;
	private View layoutTest;
	private CheckBox cbAutoFinish,cbOtherAutoFinish;
	private boolean devMode = true;
	private int defaultUi = 0;
	private boolean isPreVerifyDone = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//#if def{sdk.debugable}
		if (LeakCanary.isInAnalyzerProcess(this)) {
			// This process is dedicated to LeakCanary for heap analysis.
			// You should not init your app in this process.
			return;
		}
		LeakCanary.install(getApplication());
		//#endif
		CrashReport.initCrashReport(getApplicationContext(), "28fe0803ee", false);
		setContentView(R.layout.activity_main);
		if (Build.VERSION.SDK_INT >= 21){
			// 设置沉浸式状态栏
			View decorView = getWindow().getDecorView();
			int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
			decorView.setSystemUiVisibility(option);
//			 设置状态栏透明
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			getWindow().setStatusBarColor(Color.TRANSPARENT);
			if (Build.VERSION.SDK_INT >= 23){
				getWindow().getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
			}
		}

		initView();
		PrivacyDialogUtils privacyDialogUtils = new PrivacyDialogUtils();
		privacyDialogUtils.setDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				init();
			}
		});
		if(!privacyDialogUtils.showPrivacyDialogIfNeed(this, "秒验")) {
			init();
		}
	}

	private void init(){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if(checkSelfPermission("android.permission.READ_PHONE_STATE") != PackageManager.PERMISSION_GRANTED){
					requestPermissions(new String[]{"android.permission.READ_PHONE_STATE"},0);
				}
			}
//			preVerify();
			FlyVerify.setUiSettings(new UiSettings.Builder().setFinishActivityTransitionAnim(R.anim.fly_verify_zoom_in, R.anim.fly_verify_zoom_out).build());
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.fly_verify_demo_main_preverify:
				preVerify();
				break;
			case R.id.fly_verify_demo_main_verify: {
				// 添加自定义控件
				// 自定义UI
				addCustomView();
				verify();
				break;
			}
			case R.id.fly_verify_demo_main_verify2: {
				addCustomView();
				verify2();
				break;
			}
			case R.id.fly_verify_demo_main_verify_dialog: {
				if (!isPreVerifyDone) {
					Toast.makeText(MainActivity.this, "请等待预登录完成" , Toast.LENGTH_SHORT).show();
					return;
				}
				addCustomView4();
				customizeUi4();
				verify();
				break;
			}
			case R.id.fly_verify_demo_main_app_name: {
				switchDevMode();

				break;
			}
			case R.id.fly_verify_demo_main_version: {
				switchDevMode();
				break;
			}
			case R.id.btnOther:
				startActivity(new Intent(MainActivity.this, OtherActivity.class));
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initView() {
		findViewById(R.id.fly_verify_demo_main_verify2).setOnClickListener(this);
		findViewById(R.id.fly_verify_demo_main_preverify).setOnClickListener(this);
		layoutTest = findViewById(R.id.layoutTest);
		logoIv = findViewById(R.id.fly_verify_demo_main_logo);
		verifyBtn = findViewById(R.id.fly_verify_demo_main_verify_dialog);
		verifyDialogBtn = findViewById(R.id.fly_verify_demo_main_verify);
		tvResult = findViewById(R.id.tvResult);
		appNameTv = findViewById(R.id.fly_verify_demo_main_app_name);
		appNameTv.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				layoutTest.setVisibility(layoutTest.getVisibility() == View.GONE?View.VISIBLE:View.GONE);
				return false;
			}
		});
		versionTv = findViewById(R.id.fly_verify_demo_main_version);
		versionTv.setText(FlyVerify.getVersion());
		logoIv.setImageResource(R.drawable.fly_verify_demo_tradition);
		verifyBtn.setOnClickListener(this);
		verifyDialogBtn.setOnClickListener(this);
		logoIv.setOnClickListener(this);
		appNameTv.setOnClickListener(this);
		versionTv.setOnClickListener(this);

		cbAutoFinish = findViewById(R.id.cbAutoFinish);
		cbOtherAutoFinish = findViewById(R.id.cbOtherAutoFinish);
		findViewById(R.id.btnOther).setOnClickListener(this);
	}
	/**
	 * 预登录
	 * <p>
	 * 建议提前调用预登录接口，可以加快免密登录过程，提高用户体验
	 */
	private void preVerify() {
		isPreVerifyDone = false;
		//设置在1000-10000之内
		//FlyVerify.setTimeOut(5000);
		//移动的debug tag 是CMCC-SDK,电信是CT_ 联通是PriorityAsyncTask
		FlyVerify.setDebugMode(true);
//		FlyVerify.autoFinishOAuthPage(false);
		FlyVerify.preVerify(new PreVerifyCallback() {
			@Override
			public void onComplete(Void data) {
				// Nothing to do
				if (devMode) {
					Toast.makeText(MainActivity.this, "预登录成功", Toast.LENGTH_SHORT).show();
				}
				tvResult.setText("预取号成功");
				isPreVerifyDone = true;
			}

			@Override
			public void onFailure(VerifyException e) {
				isPreVerifyDone = true;
				// Nothing to do
				Throwable t = e.getCause();
				String errDetail = null;
				if (t != null){
					errDetail = t.getMessage();
				}

				if (devMode) {
					// 登录失败
					Log.e(TAG, "preVerify failed", e);
					// 错误码
					int errCode = e.getCode();
					// 错误信息
					String errMsg = e.getMessage();
					// 更详细的网络错误信息可以通过t查看，请注意：t有可能为null
					String msg = "错误码: " + errCode + "\n错误信息: " + errMsg;
					if (!TextUtils.isEmpty(errDetail)) {
						msg += "\n详细信息: " + errDetail;
					}
					tvResult.setText("预取号失败："+msg);
					Log.e(TAG,msg);
					Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 免密登录
	 */
	private void verify() {
		FlyVerify.autoFinishOAuthPage(cbAutoFinish.isChecked());
		FlyVerify.otherLoginAutoFinishOAuthPage(cbOtherAutoFinish.isChecked());
		CommonProgressDialog.showProgressDialog(this);
		//需要在verify之前设置
		FlyVerify.OtherOAuthPageCallBack(new OAuthPageEventCallback() {
			@Override
			public void initCallback(OAuthPageEventResultCallback cb) {
				cb.pageOpenCallback(new PageOpenedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " pageOpened");
					}
				});
				cb.loginBtnClickedCallback(new LoginBtnClickedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " loginBtnClicked");
					}
				});
				cb.agreementPageClosedCallback(new AgreementPageClosedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " agreementPageClosed");
					}
				});
				cb.agreementPageOpenedCallback(new AgreementClickedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " agreementPageOpened");
					}
				});
				cb.cusAgreement1ClickedCallback(new CusAgreement1ClickedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " cusAgreement1ClickedCallback");
					}
				});
				cb.cusAgreement2ClickedCallback(new CusAgreement2ClickedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " cusAgreement2ClickedCallback");
					}
				});
				cb.checkboxStatusChangedCallback(new CheckboxStatusChangedCallback() {
					@Override
					public void handle(boolean b) {
						Toast.makeText(MainActivity.this, "勾上"+b,Toast.LENGTH_SHORT).show();
						Log.i(TAG,System.currentTimeMillis() + " current status is " + b);
					}
				});
				cb.pageCloseCallback(new PageClosedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " pageClosed");
						HashMap<String, List<Integer>> map = UiLocationHelper.getInstance().getViewLocations();
						if (map == null) {
							return;
						}
						for (String key : map.keySet()) {
							List<Integer> locats = map.get(key);
							if (locats != null && locats.size() > 0) {
								for (int i : locats) {
									Log.i(TAG, i + " xywh");
								}
							}
						}
					}
				});
			}
		});
		FlyVerify.verify(new PageCallback() {
			@Override
			public void pageCallback(int code, String desc) {
				Log.i(TAG, "pageCallback code="+code+",desc="+desc);
				CommonProgressDialog.dismissProgressDialog();
				Toast.makeText(MainActivity.this, code + desc, Toast.LENGTH_SHORT).show();
			}
		}, new GetTokenCallback() {
			@Override
			public void onComplete(VerifyResult data) {
				tvResult.setText("取号成功");
				tokenToPhone(data);
			}

			@Override
			public void onFailure(VerifyException e) {
				showExceptionMsg(e);
			}
		});

	}


	/**
	 * 免密登录
	 */
	private void verify2() {
		FlyVerify.autoFinishOAuthPage(cbAutoFinish.isChecked());
		FlyVerify.otherLoginAutoFinishOAuthPage(cbOtherAutoFinish.isChecked());
		CommonProgressDialog.showProgressDialog(this);
		FlyVerify.verify(new VerifyCallback() {
			@Override
			public void onOtherLogin() {
				CommonProgressDialog.dismissProgressDialog();
				Toast.makeText(MainActivity.this, "其他方式登录", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onUserCanceled() {
				CommonProgressDialog.dismissProgressDialog();
				Toast.makeText(MainActivity.this, "取消授权页", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(VerifyResult data) {
				tvResult.setText("取号成功");
				tokenToPhone(data);
			}

			@Override
			public void onFailure(VerifyException e) {
				showExceptionMsg(e);
			}
		});
	}

	private void tokenToPhone(VerifyResult data) {
		CommonProgressDialog.dismissProgressDialog();
		if (data != null) {
			Log.d(TAG, data.toJSONString());
			// 获取授权码成功，将token信息传给应用服务端，再由应用服务端进行登录验证，此功能需由开发者自行实现
			CommonProgressDialog.showProgressDialog(MainActivity.this);
			Log.i("sss","返回"+data.toJSONString());

			LoginTask.getInstance().login(data, new ResultListener<String>() {
				@Override
				public void onComplete(String data) {
					CommonProgressDialog.dismissProgressDialog();
					Log.d(TAG, "Login success. data: " + data);
					// 服务端登录成功，跳转成功页
					goResultActivity(data);
				}

				@Override
				public void onFailure(Throwable e) {
					// 登录失败
					Log.e(TAG, "login failed", e);
					CommonProgressDialog.dismissProgressDialog();

					String msg = "获取授权码成功，应用服务器登录失败" +"\n错误信息: " + e.getMessage();
					Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
					goResultActivity(null);
				}
			});
		}
	}

	public void showExceptionMsg(VerifyException e) {
		// 登录失败
		if (defaultUi == 1){
			//失败之后不会自动关闭授权页面，需要手动关闭
			FlyVerify.finishOAuthPage();
		}
		CommonProgressDialog.dismissProgressDialog();
		// 错误码
		int errCode = e.getCode();
		// 错误信息
		String errMsg = e.getMessage();
		// 更详细的网络错误信息可以通过t查看，请注意：t有可能为null
		Throwable t = e.getCause();
		String errDetail = null;
		if (t != null) {
			errDetail = t.getMessage();
		}

		String msg = "错误码: " + errCode + "\n错误信息: " + errMsg;
		if (!TextUtils.isEmpty(errDetail)) {
			msg += "\n详细信息: " + errDetail;
		}else{
			msg = errMsg;
		}
		tvResult.setText("取号失败："+msg);
		Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
		goResultActivity(null);
	}

	private void customizeUi() {
		FlyVerify.setUiSettings(CustomizeUtils.customizeUi(MainActivity.this));
		FlyVerify.setLandUiSettings(CustomizeUtils.customizeLandUi());
	}


	private void customizeUi1() {
		FlyVerify.setUiSettings(CustomizeUtils.customizeUi1());
		FlyVerify.setLandUiSettings(CustomizeUtils.customizeUi5(this));
//		LandUiSettings uiSettings1  = new LandUiSettings.Builder()
//				.setTranslateAnim(true)
//				.setImmersiveTheme(true)
//				.setImmersiveStatusTextColorBlack(true)
//				.build();
//		FlyVerify.setLandUiSettings(uiSettings1);
	}


	private void customizeUi2() {
		FlyVerify.setUiSettings(CustomizeUtils.customizeUi2());
		FlyVerify.setLandUiSettings(null);
//		LandUiSettings uiSettings1  = new LandUiSettings.Builder()
//				.setDialogMaskBackgroundClickClose(true)
//				.setStartActivityTransitionAnim(R.anim.fly_verify_translate_bottom_in,R.anim.fly_verify_translate_bottom_out)
//				.setFinishActivityTransitionAnim(R.anim.fly_verify_translate_bottom_in,R.anim.fly_verify_translate_bottom_out)
//				.setDialogTheme(true)
//				.setDialogAlignBottom(true)
//				.build();
//		FlyVerify.setLandUiSettings(uiSettings1);
	}

	private void customizeUi3() {
		FlyVerify.setUiSettings(CustomizeUtils.customizeUi3());
		FlyVerify.setLandUiSettings(null);
	}

	private void customizeUi4() {
		FlyVerify.setUiSettings(CustomizeUtils.customizeUi4());
		FlyVerify.setLandUiSettings(CustomizeUtils.customizeLandUi4());
	}

	/**
	 * 添加自定义view
	 */
	private void addCustomView() {
		CustomUIRegister.addCustomizedUi(CustomizeUtils.buildCustomView(MainActivity.this),
				new CustomViewClickCallback(0));

		List<View> views = new ArrayList<View>();
		TextView textView = new TextView(MainActivity.this);
		textView.setText("返回");
		textView.setId(R.id.customized_view_id_div);
		textView.setTextColor(0xff000000);
		textView.setTextSize(16);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//		params.addRule(RelativeLayout.RIGHT_OF, R.id.fly_verify_title_bar_left);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());
		//params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		//params.bottomMargin = ResHelper.dipToPx(MainActivity.this,400);
		//params.bottomMargin = 800;//ResHelper.dipToPx(MainActivity.this,0);
		//params.leftMargin = ResHelper.dipToPx(MainActivity.this,35);

		textView.setLayoutParams(params);
		//textView.setGravity(Gravity.CENTER);
		views.add(textView);
		CustomUIRegister.addTitleBarCustomizedUi(views,new CustomViewClickCallback(1));
		//CustomUIRegister.addCustomizedUi(views,new CustomViewClickCallback(1));
	}

	private void addCustomView1() {
		CustomUIRegister.addCustomizedUi(CustomizeUtils.buildCustomView(this), new CustomViewClickListener() {
			@Override
			public void onClick(View view) {
				int id = view.getId();
				if (id == R.id.customized_btn_id_1) {
					customizeUi3();
					addCustomView3();
					FlyVerify.refreshOAuthPage();
				}
			}
		});
		View view = LayoutInflater.from(this).inflate(R.layout.fly_verify_demo_loading,null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		view.setLayoutParams(params);
		CustomUIRegister.setCustomizeLoadingView(view);

	}

	private void addCustomView2() {
		CustomUIRegister.addCustomizedUi(CustomizeUtils.buildCustomView2(this), new CustomViewClickListener() {
			@Override
			public void onClick(View view) {
				int id = view.getId();
				String msg = "";
				if (id == R.id.customized_btn_id_1) {
					msg = "用户取消登录";
					// 自定义控件点击时，SecVerify默认不关闭授权页面，若需关闭，可调用该方法
					FlyVerify.finishOAuthPage();
					CommonProgressDialog.dismissProgressDialog();
				} else if (id == R.id.customized_view_id) {
					return;
				}
				// 关闭加载框
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void addCustomView3() {

		CustomUIRegister.addCustomizedUi(CustomizeUtils.buildCustomView3(this), new CustomViewClickListener() {
			@Override
			public void onClick(View view) {
				int id = view.getId();
				String msg = "";
				if (id == R.id.customized_btn_id_1) {
					msg = "按钮1 clicked";
					// 自定义控件点击时，SecVerify默认不关闭授权页面，若需关闭，可调用该方法
					addCustomView1();
					customizeUi2();
					FlyVerify.refreshOAuthPage();
				} else if (id == R.id.customized_btn_id_0){
					msg = "关闭返回 ";
					FlyVerify.finishOAuthPage();
				} else if (id == R.id.customized_btn_id_3){
					msg = "登录返回";
					FlyVerify.finishOAuthPage();
				}
				// 关闭加载框
				CommonProgressDialog.dismissProgressDialog();
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void addCustomView4() {
		CustomUIRegister.addCustomizedUi(CustomizeUtils.buildCustomView4(MainActivity.this),
				null);
		CustomUIRegister.addTitleBarCustomizedUi(null,null);
	}

	private void goResultActivity(String data) {
		Intent i = new Intent(this, ResultActivity.class);
		if (data != null) {
			i.putExtra("fly_verify_demo_verify_success", true);
			i.putExtra("result", data);
		} else {
			i.putExtra("fly_verify_demo_verify_success", false);
		}
		if (data != null){
			startActivityForResult(i, REQUEST_CODE);
//			FlyVerify.finishOAuthPage();
		}
		cn.fly.verify.ui.component.CommonProgressDialog.dismissProgressDialog();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (defaultUi == 1 || defaultUi == 3) {
			//处理部分机型调起授权页面的Activity设置固定方向之后，授权页面无法横竖屏切换
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//根据需要设置为横屏或者竖屏
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (defaultUi == 1 || defaultUi == 3) {
			//处理部分机型调起授权页面的Activity设置固定方向之后，授权页面无法横竖屏切换
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
	}


	private void switchDevMode() {
		if (devMode) {
			devMode = false;
			Toast.makeText(this, "开发者模式：Off", Toast.LENGTH_SHORT).show();
		} else {
			devMode = true;
			Toast.makeText(this, "开发者模式：On", Toast.LENGTH_SHORT).show();
		}
	}

	private static class CustomViewClickCallback implements CustomViewClickListener {
		private int customUI;

		public CustomViewClickCallback(int customUI) {
			this.customUI = customUI;
		}

		@Override
		public void onClick(View view) {
			if (customUI == 0) {
				int id = view.getId();
				String msg = "";
				if (id == R.id.customized_btn_id_1) {
					msg = "微信 clicked";
					// 自定义控件点击时，SecVerify默认不关闭授权页面，若需关闭，可调用该方法
//					FlyVerify.finishOAuthPage();
					Intent intent = new Intent(view.getContext(), ResultActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					view.getContext().startActivity(intent);
					Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
				}
				// 关闭加载框
			} else if (customUI == 1) {
				// 关闭加载框
				FlyVerify.finishOAuthPage();
			} else if (customUI == 4){
				int id = view.getId();
				String msg = "";
				if (id == R.id.customized_btn_id_1) {
					// 自定义控件点击时，SecVerify默认不关闭授权页面，若需关闭，可调用该方法
				} else if (id == R.id.customized_view_id) {
					return;
				}
				// 关闭加载框
			}
			CommonProgressDialog.dismissProgressDialog();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		onCreate(null);
	}
}
