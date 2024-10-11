package cn.fly.verify.demo.login;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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

	public void login(VerifyResult verifyResult, ResultListener<String> resultListener) {
		HashMap<String, Object> values = new HashMap<String, Object>();
		if (verifyResult != null) {
			values.put("opToken", verifyResult.getOpToken());
			values.put("operator", verifyResult.getOperator());
			values.put("phoneOperator", verifyResult.getOperator());
			values.put("token", verifyResult.getToken());
			values.put("md5", "应用md5");
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
