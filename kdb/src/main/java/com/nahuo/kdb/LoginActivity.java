package com.nahuo.kdb;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nahuo.kdb.api.AccountAPI;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.kdb.model.PublicData;
import com.nahuo.library.controls.AutoCompleteTextViewEx;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.FunctionHelper;

public class LoginActivity extends Activity implements OnClickListener {

	private LoginActivity mContext = this;
	private AutoCompleteTextViewEx edtAccount;
	private EditText edtPassword;
	private Button btnLogin;

	private LoadingDialog loadingDialog;
	public static  String EXTA_ISSHOWERROR="EXTA_ISSHOWERROR";
	private boolean isShowError=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		initView();
	}


	/**
	 * 初始化视图
	 */
	private void initView() {

		Button btnLeft = (Button) findViewById(R.id.titlebar_btnLeft);
		btnLeft.setVisibility(View.GONE);
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText("登录");

		loadingDialog = new LoadingDialog(mContext);
		edtAccount = (AutoCompleteTextViewEx) mContext.findViewById(R.id.login_edtAccount);
		edtPassword = (EditText) mContext.findViewById(R.id.login_edtPassword);

		btnLogin = (Button) mContext.findViewById(R.id.login_btnLogin);

		edtAccount.setOnSearchLogDeleteListener(new AutoCompleteTextViewEx.OnSearchLogDeleteListener() {
			@Override
			public void onSearchLogDeleted(String text) {
				String newChar = SpManager.deleteLoginAccounts(mContext, text);
				Log.i(getClass().getSimpleName(), "deleteSearchItemHistories:" + newChar);
				edtAccount.populateData(newChar, ",");
				edtAccount.getFilter().filter(edtAccount.getText());
			}
		});


		String username = SpManager.getLoginAccounts(mContext);
		edtAccount.populateData(username, ",");

		btnLogin.setOnClickListener(this);
		findViewById(R.id.img_see_pwd).setOnClickListener(this) ;
		isShowError=getIntent().getBooleanExtra(EXTA_ISSHOWERROR,false);
		if (isShowError){
			ViewHub.showOkDialog(LoginActivity.this, "提示", "未授权登陆", "知道了", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.login_btnLogin:
				login();
				break;
			case R.id.img_see_pwd:
				int length = edtPassword.getText().length() ;
				if(length> 0){
					if(edtPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
						edtPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD) ;
						edtPassword.invalidate() ;
						edtPassword.setSelection(length) ;
					}
					else{
						edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD) ;
						edtPassword.invalidate() ;
						edtPassword.setSelection(edtPassword.getText().length()) ;
					}
				}
				break ;
		}
	}
	/**
	 * 登录
	 */
	private void login() {
		// 验证用户录入
		if (!validateInput())
			return;
		// 验证网络
		if (!FunctionHelper.CheckNetworkOnline(mContext))
			return;
		// 执行登录操作
		new Task().execute();
	}

	/**
	 * 验证用户输入
	 */
	private boolean validateInput() {
		String phoneNo = edtAccount.getText().toString().trim();
		String password = edtPassword.getText().toString().trim();
		// 验证手机号
		if (TextUtils.isEmpty(phoneNo)) {
			Toast.makeText(mContext, R.string.login_edtAccount_empty, Toast.LENGTH_SHORT).show();
			edtAccount.requestFocus();
			return false;
		}
		// 验证密码
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(mContext, R.string.login_edtPassword_empty, Toast.LENGTH_SHORT).show();
			edtPassword.requestFocus();
			return false;
		}
		return true;
	}

	private class Task extends AsyncTask<Object, Void, Object> {

		public Task() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
					loadingDialog.start(getString(R.string.login_doLogin_loading));

		}

		@Override
		protected Object doInBackground(Object... params) {
			try {
						String phoneNo = edtAccount.getText().toString().trim();
						String password = edtPassword.getText().toString().trim();
						AccountAPI.getInstance().userLogin(phoneNo, password);

			} catch (Exception e) {
				e.printStackTrace();
				return "error:" + e.getMessage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			if (loadingDialog.isShowing()) {
				loadingDialog.stop();
			}
			if (result instanceof String && ((String) result).startsWith("error:")) {
				ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
			} else {
						if (!TextUtils.isEmpty(PublicData.getCookie(mContext))) {
							SpManager.setCookie(mContext, PublicData.getCookie(mContext));
						}
						SpManager.setLoginAccount(mContext, edtAccount.getText().toString());

						Intent intent = new Intent(mContext, MainAcivity.class);
						startActivity(intent);
			}
		}

	}
}
