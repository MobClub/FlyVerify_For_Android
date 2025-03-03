package cn.fly.verify.demo.login;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.fly.FlySDK;
import cn.fly.verify.datatype.VerifyResult;
import cn.fly.verify.demo.Const;
import cn.fly.verify.demo.ResultListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class LoginTask {
	private static LoginTask instance;
	private static final String URL_LOGIN = "https://demo.accuratead.cn/demo/sdkLogin";

	private LoginTask() {}

	public static LoginTask getInstance() {
		if (instance == null) {
			synchronized (LoginTask.class) {
				if (instance == null) {
					instance = new LoginTask();
				}
			}
		}
		return instance;
	}

	public String getSignMD5(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
			Signature[] signs = packageInfo.signatures;
			Signature sign = signs[0];
			String signStr = encryptionMD5(sign.toByteArray());
			return signStr;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}


	/**
	 * MD5加密
	 * @param byteStr 需要加密的内容
	 * @return 返回 byteStr的md5值
	 */
	public static String encryptionMD5(byte[] byteStr) {
		MessageDigest messageDigest = null;
		StringBuffer md5StrBuff = new StringBuffer();
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(byteStr);
			byte[] byteArray = messageDigest.digest();
			for (int i = 0; i < byteArray.length; i++) {
				if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
					md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
				} else {
					md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
				}
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md5StrBuff.toString();
	}

	public void login(VerifyResult verifyResult, ResultListener<String> resultListener) {
		HashMap<String, Object> values = new HashMap<String, Object>();
		if (verifyResult != null) {
			values.put("opToken", verifyResult.getOpToken());
			values.put("operator", verifyResult.getOperator());
			values.put("phoneOperator", verifyResult.getOperator());
			values.put("token", verifyResult.getToken());
			values.put("md5", getSignMD5(FlySDK.getContext()));
			values.put("appkey", Const.APP_KEY);
		}
		OkHttpClient client = new OkHttpClient().newBuilder()
				.connectTimeout(8, TimeUnit.SECONDS)
				.writeTimeout(8, TimeUnit.SECONDS)
				.readTimeout(16, TimeUnit.SECONDS)
				.retryOnConnectionFailure(true)
				.hostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				})
				.build();
		String json = toJson(values);
		RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
		Request request = new Request.Builder()
				.url(URL_LOGIN)
				.post(body)
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, final IOException e) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						resultListener.onFailure(e);
					}
				});
			}

			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				final String result = response.body().string();
				Log.d("JIYAN","result : "+result);
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {

						String res = "";
						if (response.code() == 200) {
							try {
								JSONObject jsonObject = new JSONObject(result);
								JSONObject resJson = jsonObject.getJSONObject("res");
								res = resJson.toString();
							} catch (Throwable t) {
								Log.e("JIYAN", t.getMessage(), t);
							}
							Log.d("JIYAN","res : "+res);
							//res = "{\"phone\":\"18217534845\"}";
							try {
								JSONObject rsu = new JSONObject(res);
								String data = rsu.getString("phone");
								resultListener.onComplete(data);
							} catch (JSONException e) {
							}

						} else {
							try {
								JSONObject jsonObject = new JSONObject(result);
								JSONObject resJson = jsonObject.getJSONObject("error");
								res = resJson.toString();
							} catch (Throwable t) {
								Log.e("HttpManager", t.getMessage(), t);
							}
							resultListener.onFailure(new Throwable(result));
						}
					}
				});
			}
		});
	}
	private String toJson(HashMap<String, Object> params){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		Object[] keys = params.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			if(i >0){
				stringBuilder.append(",");
			}
			stringBuilder.append("\"").append(keys[i]).append("\"").append(":");
			stringBuilder.append("\"").append(params.get(keys[i])).append("\"");
		}
		stringBuilder.append("}");

		return stringBuilder.toString();
	}
}
