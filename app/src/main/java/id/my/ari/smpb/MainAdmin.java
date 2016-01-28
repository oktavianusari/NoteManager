package id.my.ari.smpb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainAdmin extends Activity{
	
	Button btnViewRole,btnViewUser,btnViewKategori,btnLog, btnViewStatus, btnArtikelTest;
	Button btnNewProduct;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_admin);

		// Buttons
		btnViewRole = (Button) findViewById(R.id.btnRole);
		btnLog = (Button) findViewById(R.id.btnLogout);
		btnNewProduct = (Button) findViewById(R.id.btnCreateProduct);
		btnViewUser = (Button) findViewById(R.id.btnUser);
		btnViewKategori = (Button) findViewById(R.id.btnKategori);
		btnViewStatus = (Button) findViewById(R.id.btnStatus);
		btnArtikelTest = (Button) findViewById(R.id.btnArtikelTest);
		// view products click event
		btnViewRole.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching All products Activity
				Intent i = new Intent(getApplicationContext(), ManageRole.class);
				startActivity(i);
				
			}
		});
		btnLog.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching All products Activity
				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(i);
				finish();
			}
		});
		btnViewKategori.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching All products Activity
				Intent i = new Intent(getApplicationContext(), ManageKategori.class);
				startActivity(i);
				
			}
		});
		
		btnViewStatus.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching All products Activity
				Intent i = new Intent(getApplicationContext(), ManageStatus.class);
				startActivity(i);
				
			}
		});
		btnViewUser.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching All products Activity
				Intent i = new Intent(getApplicationContext(), ManageUser.class);
				startActivity(i);
				
			}
		});
		
		btnArtikelTest.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching All products Activity
				Intent i = new Intent(getApplicationContext(), ManageArticleManager.class);
				startActivity(i);

				
			}
		});
		
	
	}
}
