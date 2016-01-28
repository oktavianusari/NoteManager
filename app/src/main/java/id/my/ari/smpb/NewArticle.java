package id.my.ari.smpb;

import id.my.ari.smpb.R;

import java.util.ArrayList;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class NewArticle extends Activity implements OnItemSelectedListener {

	// Progress Dialog
	private ProgressDialog pDialog;
	

	private ArrayList<Category> categoriesList;
	private ArrayList<Statusnya> statusArtikelList;
	JSONParser jsonParser = new JSONParser();
	JSONArray string_json = null;
	
	EditText txtJudul, txtDes;
	EditText inputPrice;
	EditText inputDesc;
	private Spinner list_role;
	private Spinner list_stat;
	int cek_spinner;
	int cek_spinner2;

	String namaLogin;

	// url to create new product
	private static String url_create_product = "http://ari.my.id/smpb/android_connect/create_article.php";
	private String url_get = "http://ari.my.id/smpb/api-spinner/get_artikel.php";
	private String url_status_artikel = "http://ari.my.id/smpb/api-spinner/get_status.php";
	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_article);

		// Edit Text
		list_role = (Spinner) findViewById(R.id.role_kat);
		list_role.setOnItemSelectedListener(this);
		
		list_stat = (Spinner) findViewById(R.id.role_stat);
		list_stat.setOnItemSelectedListener(this);

		categoriesList = new ArrayList<Category>();
		statusArtikelList = new ArrayList<Statusnya>();
		
		txtJudul = (EditText) findViewById(R.id.inputJudul);
		txtDes = (EditText) findViewById(R.id.inputDeskripsi);
	

		// Create button
		Button btnCreateProduct = (Button) findViewById(R.id.btnCreateProduct);

		// button click event
		btnCreateProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                new CreateNewProduct().execute();
			}
		});
		
		new GetCategories().execute();

        Intent i = getIntent();
        namaLogin = i.getStringExtra("namaLoginEdit");

	}

	private void populateSpinner() {
		List<String> lables = new ArrayList<String>();
		
		for (int i = 0; i < categoriesList.size(); i++) {
			lables.add(categoriesList.get(i).getName());
		}

		// Creating adapter for spinner
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, lables);

		// Drop down layout style - list view with radio button
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		list_role.setAdapter(spinnerAdapter);

		
		
		//================================status===================================
		List<String> lablesStatus = new ArrayList<String>();
		
		for (int i = 0; i < statusArtikelList.size(); i++) {
			lablesStatus.add(statusArtikelList.get(i).getName());
		}

		// Creating adapter for spinner
		ArrayAdapter<String> spinnerAdapterStatus = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, lablesStatus);

		// Drop down layout style - list view with radio button
		spinnerAdapterStatus
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		list_stat.setAdapter(spinnerAdapterStatus);

	}
	
	private class GetCategories extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NewArticle.this);
			pDialog.setMessage("Mohon tunggu...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			ServiceHandler jsonParser = new ServiceHandler();
			String json = jsonParser.makeServiceCall(url_get, ServiceHandler.GET);
			String jsonStat = jsonParser.makeServiceCall(url_status_artikel, ServiceHandler.GET);
			
			Log.e("Response: ", "> " + json);
			Log.e("Response: ", "> " + jsonStat);
			
			if (json != null) {
				try {
					JSONObject jsonObj = new JSONObject(json);
					if (jsonObj != null) {
						JSONArray categories = jsonObj
								.getJSONArray("kategori");						

						for (int i = 0; i < categories.length(); i++) {
							JSONObject catObj = (JSONObject) categories.get(i);
							Category cat = new Category(catObj.getInt("id"),
									catObj.getString("nama"));
							categoriesList.add(cat);
						}
					}
					

				}
				catch (JSONException e) {
					e.printStackTrace();
				}

			} 
			
			else {
				Log.e("JSON Data", "Didn't receive any data from server!");
			}
			
			
			if (jsonStat != null) {
				try {
					JSONObject jsonObjStat = new JSONObject(jsonStat);
					if (jsonObjStat != null) {
						JSONArray statuses = jsonObjStat
								.getJSONArray("status");						

						for (int i = 0; i < statuses.length(); i++) {
							JSONObject statObj = (JSONObject) statuses.get(i);
							Statusnya statusnya = new Statusnya(statObj.getInt("id"),
									statObj.getString("nama"));
							statusArtikelList.add(statusnya);
						}
					}
					

				}
				catch (JSONException e) {
					e.printStackTrace();
				}

			} 
			
			else {
				Log.e("JSON Data", "Didn't receive any data from server!");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (pDialog.isShowing())
				pDialog.dismiss();
			populateSpinner();
		}

	}
	
	
	/**
	 * Background Async Task to Create new product
	 * */
	class CreateNewProduct extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NewArticle.this);
			pDialog.setMessage("Menyimpan artikel...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			String name = txtJudul.getText().toString();
			String des = txtDes.getText().toString();


			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("description", des));
			params.add(new BasicNameValuePair("kategori", String.valueOf(cek_spinner)));
            params.add(new BasicNameValuePair("pengguna", namaLogin));
			params.add(new BasicNameValuePair("status", String.valueOf(cek_spinner2)));
			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_create_product,
					"POST", params);
			
			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created product
					Intent i = new Intent(getApplicationContext(), ManageArticle.class);
					startActivity(i);
					
					// closing this screen
					finish();
				} else {
					// failed to create product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}
		
	
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
		}

	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(
				getApplicationContext(),
						parent.getItemAtPosition(position).toString() + " dipilih" ,
				Toast.LENGTH_LONG).show();
		
		Spinner spinner = (Spinner) parent;
	    if(spinner.getId() == R.id.role_kat)
	    {
	    	//cek_spinner = list_role.getItemAtPosition(position).toString();
	    	cek_spinner = list_role.getSelectedItemPosition()+1;
	    }
	    else if(spinner.getId() == R.id.role_stat)
	    {
	    	//cek_spinner2 = list_stat.getItemAtPosition(position).toString();
	    	cek_spinner2 = list_stat.getSelectedItemPosition()+1;
	    }
	}
	

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
}
