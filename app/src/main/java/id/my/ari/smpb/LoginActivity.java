/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package id.my.ari.smpb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import id.my.ari.smpb.library.DatabaseHandler;
import id.my.ari.smpb.library.UserFunctions;

import id.my.ari.smpb.R;

public class LoginActivity extends Activity {
	public LoginActivity(){}
	Button btnLogin;
	Button btnLinkToRegister;
	EditText inputEmail;
	EditText inputPassword;
	TextView loginErrorMsg;
	JSONParser jsonParser = new JSONParser();
	ProgressDialog pDialog;
	JSONArray string_json = null;
	String cek_login=null, email, password;
	


	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";
	private String url_get = "http://ari.my.id/smpb/get_detail.php";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// Importing all assets like buttons, text fields
		inputEmail = (EditText) findViewById(R.id.loginEmail);
		inputPassword = (EditText) findViewById(R.id.loginPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);
		
		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				email = inputEmail.getText().toString();
				password = inputPassword.getText().toString();
				
				new AmbilDetailBerita().execute();
				
				
			}
		});
		

		// Link to Register Screen
		btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivity(i);
				finish();
			}
		});
	}
	
	class AmbilDetailBerita extends AsyncTask<String, String, String> { 
		 
		@Override
		protected void onPreExecute() { 
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			//pDialog.setMessage("Mohon Tunggu ... !");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... params) {

					try {

						List<NameValuePair> params1 = new ArrayList<NameValuePair>();
						params1.add(new BasicNameValuePair("kirim",email));

						JSONObject json = jsonParser.makeHttpRequest(
								url_get, "GET", params1);
						string_json = json.getJSONArray("role_name");

							runOnUiThread(new Runnable() {
								public void run() {
									
							       
							try {
								UserFunctions userFunction = new UserFunctions();
								Log.d("Button", "Login");
								JSONObject json = userFunction.loginUser(email, password);
								// ambil objek member pertama dari JSON Array
								JSONObject ar = string_json.getJSONObject(0);
								String nama = ar.getString("role");
								
								cek_login=nama;
								
								if (json.getString(KEY_SUCCESS) != null) {
									loginErrorMsg.setText("");
									String res = json.getString(KEY_SUCCESS); 
									if(Integer.parseInt(res) == 1){
										// user successfully logged in
										// Store user details in SQLite Database
										DatabaseHandler db = new DatabaseHandler(getApplicationContext());
										JSONObject json_user = json.getJSONObject("user");
										
										// Clear all previous data in database
										userFunction.logoutUser(getApplicationContext());
										db.addUser2(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));						
										
										
										if(cek_login.equals("Admin")){
											Toast.makeText(
									 				getApplicationContext(),"Anda login sebagai "+cek_login ,
									 				Toast.LENGTH_LONG).show();
											// Launch Dashboard Screen
											Intent dashboard = new Intent(getApplicationContext(), MainAdmin.class);
										
											dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											dashboard.putExtra("namaLogin", email);
											startActivity(dashboard);
											} else if (cek_login.equals("Manager")){
												Intent dashboard = new Intent(getApplicationContext(), ManageArticleManager.class);
												
												dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												dashboard.putExtra("namaLogin", email);
												startActivity(dashboard);

											} else {
                                                Intent dashboard = new Intent(getApplicationContext(), ManageArticle.class);

                                                dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                dashboard.putExtra("namaLogin", email);
                                                startActivity(dashboard);
                                        }

										// Close Login Screen
										finish();
									}else{
										// Error in login
										loginErrorMsg.setText("Username dan password salah.");
									}
								}
				        
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								}
						}
					});
							
					} catch (JSONException e) {
						e.printStackTrace();
				}

			return null;
		}

		protected void onPostExecute(String file_url) {

			pDialog.dismiss();
		}
	}
}
