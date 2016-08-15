package com.smartbuilders.synchronizer.ids;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.jasgcorp.ids.sqlresult.SQLResultAdapter;
import com.jasgcorp.ids.sqlresult.SQLResultDataRow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class SQLConsoleActivity extends Activity implements OnClickListener{

	private static final String TAG = SQLConsoleActivity.class.getSimpleName();
	public static final String KEY_CURRENT_USER 			= "com.jasgcorp.ids.SQLConsoleActivity.KEY_CURRENT_USER";
	private static final String STATE_CURRENT_USER 			= "state_current_user";
	private static final String STATE_COLUMNS_WIDTHS 		= "state_columns_widths";
	private static final String STATE_HEADER_QUERY_RESULTS 	= "state_header_query_results";	
	private static final String STATE_QUERY_RESULTS 		= "state_query_results";
	private static final String STATE_SQL_SENTENCE_EDITTEXT = "state_sql_sentence_edittext";
	
	private User mCurrentUser;
	private ListView queryResultListView;
	private EditText sqlSentenceEditText;
	private ProgressDialog waitPleaseDialog;
	private ArrayList<Integer> columnsWidths;
	private ArrayList<SQLResultDataRow> queryResults;
	private SQLResultDataRow headerQueryResults;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sqlconsole);

		Typeface impactLabelReversed = Typeface.createFromAsset(getAssets(), "fonts/impact_label_reversed.ttf");
		((TextView)findViewById(R.id.sqlSentenceLabel)).setTypeface(impactLabelReversed);
		
		//initialization of onClickListener to the buttons
		findViewById(R.id.executeQueryButton).setOnClickListener(this);
		
		queryResultListView = (ListView) findViewById(R.id.queryResultListView);
		sqlSentenceEditText = (EditText) findViewById(R.id.sqlSentenceEditText);
		
		columnsWidths = new ArrayList<Integer>();
		queryResults = new ArrayList<SQLResultDataRow>();
		headerQueryResults = new SQLResultDataRow();

		Bundle extras = getIntent().getExtras();
		if(extras!=null && extras.containsKey(KEY_CURRENT_USER)){
			mCurrentUser = extras.getParcelable(KEY_CURRENT_USER);
			queryResultListView.setAdapter(new SQLResultAdapter(SQLConsoleActivity.this));
		}else if(savedInstanceState != null){
			restoreInterface(savedInstanceState);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sqlconsole, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.executeQueryButton:
				executeQuery(sqlSentenceEditText.getText().toString().trim());
				break;
	        default:
		}
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
   		outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
   		outState.putString(STATE_SQL_SENTENCE_EDITTEXT, sqlSentenceEditText.getText().toString());
   		outState.putParcelable(STATE_HEADER_QUERY_RESULTS, headerQueryResults);
   		outState.putParcelableArrayList(STATE_QUERY_RESULTS, queryResults);
   		outState.putIntegerArrayList(STATE_COLUMNS_WIDTHS, columnsWidths);
    	super.onSaveInstanceState(outState);
    }
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		restoreInterface(savedInstanceState);
	}
	
	private void executeQuery(final String sentence){
		waitPleaseDialog = ProgressDialog.show(this, getString(R.string.executing_query), getString(R.string.wait_please), true, false);
		try {
			String errorMessage = new AsyncTask<Object, String, String>() {
								@Override
								protected String doInBackground(Object... params) {
									columnsWidths = new ArrayList<Integer>();
									queryResults = new ArrayList<SQLResultDataRow>();
									headerQueryResults = new SQLResultDataRow();
									Context ctx = (Context) params[0];
									if(sentence!=null && !sentence.isEmpty()){
										Cursor c = null;
										try{
											c = getContentResolver().query(mCurrentUser!=null 
																				? DataBaseContentProvider
																						.INTERNAL_DB_URI
																						.buildUpon()
																						.appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId())
																						.build() 
																				: DataBaseContentProvider.INTERNAL_DB_URI, 
													 						null, 
													 						sentence,
																			null, 
																			null);
											if(c!=null){
												SQLResultDataRow row = new SQLResultDataRow();
												//header data
												for(int i =0 ; i<c.getColumnCount(); i++){
													columnsWidths.add(c.getColumnName(i).length());
													headerQueryResults.addColumn(c.getColumnName(i));
												}
												
												while(c.moveToNext()){
													row = new SQLResultDataRow();
													for(int i =0 ; i<c.getColumnCount(); i++){
														if(c.getString(i)!=null && 
																columnsWidths.get(i) < c.getString(i).length()){
															columnsWidths.set(i, c.getString(i).length());
														}
														row.addColumn(c.getString(i));
													}
													queryResults.add(row);
												}
												return null;
											}
											return ctx.getString(R.string.sentence_result_null);
										}catch(Exception e){
											e.printStackTrace();
											return e.getMessage();
										}finally{
											if(c!=null){
												c.close();
											}
										}
									}else{
										return ctx.getString(R.string.sql_sentence_empty);
									}
								}
								
								@Override
								protected void onPostExecute(String errorMessage) {
									super.onPostExecute(errorMessage);
									if(waitPleaseDialog!=null && waitPleaseDialog.isShowing()){
										waitPleaseDialog.dismiss();
									}
								}
							}.execute(this).get();
			if(errorMessage != null){
				new AlertDialog.Builder(this)
								.setMessage(errorMessage)
								.setNeutralButton(R.string.accept, null)
								.show();
			}else{
				insertHeader(queryResults, columnsWidths, headerQueryResults);
				queryResultListView.setAdapter(new SQLResultAdapter(SQLConsoleActivity.this, queryResults, columnsWidths));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param savedInstanceState
	 */
	private void restoreInterface(Bundle savedInstanceState){
		if(savedInstanceState != null){
			//recuperar el usuario
			if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
				mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
			}
			//recuperar la consulta que esta escrita
			if(savedInstanceState.containsKey(STATE_SQL_SENTENCE_EDITTEXT)){
				sqlSentenceEditText.setText(savedInstanceState.getString(STATE_SQL_SENTENCE_EDITTEXT));
			}
			//recuperar la tabla de los resultados de la consulta
			if(savedInstanceState.containsKey(STATE_QUERY_RESULTS) 
					&& savedInstanceState.containsKey(STATE_COLUMNS_WIDTHS)
					&& savedInstanceState.containsKey(STATE_HEADER_QUERY_RESULTS)){
				headerQueryResults 	= savedInstanceState.getParcelable(STATE_HEADER_QUERY_RESULTS);
				queryResults 		= savedInstanceState.getParcelableArrayList(STATE_QUERY_RESULTS);
				columnsWidths 		= savedInstanceState.getIntegerArrayList(STATE_COLUMNS_WIDTHS);
				insertHeader(queryResults, columnsWidths, headerQueryResults);
				queryResultListView.setAdapter(new SQLResultAdapter(SQLConsoleActivity.this, queryResults, columnsWidths));
			}
		}
	}

	/**
	 * 
	 * @param queryResults
	 * @param columnsWidths
	 * @param headerQueryResults
	 */
	private void insertHeader(ArrayList<SQLResultDataRow> queryResults, 
			ArrayList<Integer> columnsWidths, SQLResultDataRow headerQueryResults){
		LinearLayout header = (LinearLayout)findViewById(R.id.headerLinearLayout);
		header.removeAllViews();
		int n = queryResults.size(), cifras=0;
        while(n!=0){ n/=10; cifras++; }
        Typeface consoleFont = Typeface.createFromAsset(getAssets(), "fonts/console.ttf");
        
		TextView column = new TextView(this);
		LayoutParams layoutParams = new LayoutParams((int)(cifras * SQLResultAdapter.TEXT_SIZE_IN_SP * 1.5), LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(SQLResultAdapter.TV_MARGIN_LEFT, 
				    			SQLResultAdapter.TV_MARGIN_TOP, 
				    			SQLResultAdapter.TV_MARGIN_RIGHT, 
				    			SQLResultAdapter.TV_MARGIN_BOTTOM);
		column.setLayoutParams(layoutParams);
		column.setTextSize(TypedValue.COMPLEX_UNIT_SP, SQLResultAdapter.TEXT_SIZE_IN_SP);
    	column.setTypeface(consoleFont);
    	column.setText("");
		header.addView(column);

        for(int i=0; i<headerQueryResults.getColumnsQty(); i++){
        	column = new TextView(this);
        	layoutParams = new LayoutParams((columnsWidths!=null && columnsWidths.size()>i) 
												? (int) (columnsWidths.get(i) * SQLResultAdapter.TEXT_SIZE_IN_SP * 1.39) 
												: LayoutParams.WRAP_CONTENT, 
											LayoutParams.WRAP_CONTENT);
        	layoutParams.setMargins(SQLResultAdapter.TV_MARGIN_LEFT, 
				        			SQLResultAdapter.TV_MARGIN_TOP, 
				        			SQLResultAdapter.TV_MARGIN_RIGHT, 
				        			SQLResultAdapter.TV_MARGIN_BOTTOM);
        	column.setLayoutParams(layoutParams);
        	column.setTextSize(TypedValue.COMPLEX_UNIT_SP, SQLResultAdapter.TEXT_SIZE_IN_SP);
        	column.setTypeface(consoleFont);
        	column.setText(headerQueryResults.getColumn(i));
        	header.addView(column);
        }
	}

}
