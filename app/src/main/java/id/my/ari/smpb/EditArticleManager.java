package id.my.ari.smpb;

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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class EditArticleManager extends Activity implements OnItemSelectedListener {

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
    int posisiKategori, posisiStatus;
	int cek_spinner2;
    String pid, namaLogin;
    Button btnSave;
    Button btnDelete;

	// url to create new product
	private static String url_create_product = "http://ari.my.id/smpb/android_connect/update_article.php";
	private String url_get = "http://ari.my.id/smpb/api-spinner/get_artikel.php";
	private String url_status_artikel = "http://ari.my.id/smpb/api-spinner/get_status.php";

    // single product url
    private static final String url_product_detials = "http://ari.my.id/smpb/android_connect/get_article_details.php";

    // url to update product
    private static final String url_update_product = "http://ari.my.id/smpb/android_connect/update_article.php";

    // url to delete product
    private static final String url_delete_product = "http://ari.my.id/smpb/android_connect/delete_article.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCT = "product";
	private static final String TAG_ID = "pid";
	private static final String TAG_NAME = "name";
	private static final String TAG_DES = "description";
	private static final String TAG_STAT = "status";
	private static final String TAG_PENGGUNA = "pengguna";
	private static final String TAG_KATEGORI = "kategori";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_article_manager);

        // getting product details from intent
        Intent i = getIntent();

        // save button
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        // getting product id (pid) from intent
        pid = i.getStringExtra(TAG_ID);
        namaLogin = i.getStringExtra("namaLoginEdit");
        Log.d("nama login manager", namaLogin);
        // Getting complete product details in background thread
        new GetProductDetails().execute();

		// Edit Text
		list_role = (Spinner) findViewById(R.id.role_kat);
		list_role.setOnItemSelectedListener(this);

		list_stat = (Spinner) findViewById(R.id.role_stat);
		list_stat.setOnItemSelectedListener(this);

		categoriesList = new ArrayList<Category>();
		statusArtikelList = new ArrayList<Statusnya>();

		txtJudul = (EditText) findViewById(R.id.inputJudul);
		txtDes = (EditText) findViewById(R.id.inputDeskripsi);

        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update product
                new SaveProductDetails().execute();
            }
        });

        // Delete button click event
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // deleting product in background thread
                new DeleteProduct().execute();
            }
        });

		//new GetCategories().execute();
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
        list_role.setSelection(posisiKategori-1);


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
        list_stat.setSelection(posisiStatus-1);

	}

	class GetProductDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditArticleManager.this);
            pDialog.setMessage("Memuat artikel...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... params) {

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("pid", pid));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_product_detials, "GET", params);

                        // check your log for json response
                        Log.d("Single Product Details", json.toString());

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json
                                    .getJSONArray("kategori"); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);

                            // product with this pid found
                            // Edit Text
                            txtJudul = (EditText) findViewById(R.id.inputJudul);
                            txtDes = (EditText) findViewById(R.id.inputDeskripsi);
                            //txtPengguna = (TextView) findViewById(R.id.inputPengguna);

                            // display product data in EditText
                            txtJudul.setText(product.getString(TAG_NAME));
                            txtDes.setText(product.getString(TAG_DES));
                            posisiKategori = Integer.parseInt(product.getString(TAG_KATEGORI));
                            posisiStatus = Integer.parseInt(product.getString(TAG_STAT));
                            Log.d("posisiKategori =", String.valueOf(posisiKategori));
                            //txtPengguna.setText(namaLogin);

                        } else {
                            // product with pid not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            //mulai tarik data untuk spinner
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


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
            pDialog.dismiss();
            populateSpinner();
		}


	}
    /**
     * Background Async Task to  Save product Details
     * */
    class SaveProductDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditArticleManager.this);
            pDialog.setMessage("Menyimpan artikel...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Saving product
         * */
        protected String doInBackground(String... args) {

            // getting updated data from EditTexts
            String judul = txtJudul.getText().toString();
            String des = txtDes.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_ID, pid));
            params.add(new BasicNameValuePair(TAG_NAME, judul));
            params.add(new BasicNameValuePair(TAG_KATEGORI, String.valueOf(cek_spinner)));
            params.add(new BasicNameValuePair(TAG_PENGGUNA, namaLogin));
            params.add(new BasicNameValuePair(TAG_DES, des));
            params.add(new BasicNameValuePair(TAG_STAT, String.valueOf(cek_spinner2)));


            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_product,
                    "POST", params);

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about product update
                    setResult(100, i);
                    finish();
                } else {
                    // failed to update product
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
            // dismiss the dialog once product uupdated
            pDialog.dismiss();
        }
    }

    /*****************************************************************
     * Background Async Task to Delete Product
     * */
    class DeleteProduct extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditArticleManager.this);
            pDialog.setMessage("Menghapus artikel...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting product
         * */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("pid", pid));

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_product, "POST", params);

                // check your log for json response
                Log.d("Delete Product", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // product successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about product deletion
                    setResult(100, i);
                    finish();
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
            // dismiss the dialog once product deleted
            pDialog.dismiss();

        }

    }

    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
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