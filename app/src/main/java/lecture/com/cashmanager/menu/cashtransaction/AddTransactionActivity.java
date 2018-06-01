package lecture.com.cashmanager.menu.cashtransaction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import lecture.com.cashmanager.R;
import lecture.com.cashmanager.db.DBHelper;
import lecture.com.cashmanager.menu.AddCategoryActivity;
import lecture.com.cashmanager.model.CashTransaction;
import lecture.com.cashmanager.model.Category;

public class AddTransactionActivity extends AppCompatActivity{

    private static final int MODE_ADD_INCOME = 1;
    private static final int MODE_ADD_EXPENSE = -1;
    private static final int MY_REQUEST_CODE = 1234;

    Category category;
    private int type = 1;
    private int categoryid;

    EditText tsAmount;
    EditText tsCategory;
    EditText tsNote;
    EditText tsDate;
    TableRow trCategory;
    TableRow trDate;
    Button btnSave;
    Button btnCancel;

    private Calendar calendar;
    private int year, month, day;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(lecture.com.cashmanager.R.layout.activity_add_transaction);

        tsAmount = (EditText)findViewById(R.id.txt_add_transaction_value);
        tsCategory = (EditText)findViewById(R.id.txt_add_transaction_category);
        tsNote = (EditText)findViewById(R.id.txt_add_transaction_note);
        tsDate = (EditText)findViewById(R.id.txt_add_transaction_date);
        trCategory = (TableRow)findViewById(R.id.txt_add_transaction_category_wrap);
        trDate = (TableRow)findViewById(R.id.txt_add_transaction_date_wrap);
        btnSave = (Button)findViewById(R.id.btn_add_transaction_save);
        btnCancel = (Button)findViewById(R.id.btn_add_transaction_cancel);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        showDate(year, month, day);

        Intent intent = this.getIntent();
        type = intent.getIntExtra("type", 1) == 1 ? MODE_ADD_INCOME: MODE_ADD_EXPENSE;

        if(type == MODE_ADD_INCOME){
            tsAmount.setTextColor(getResources().getColor(R.color.green));
            tsAmount.setHintTextColor(getResources().getColor(R.color.green));
        }
        else{
            tsAmount.setTextColor(getResources().getColor(R.color.red));
            tsAmount.setHintTextColor(getResources().getColor(R.color.red));
        }

        tsCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategorySelector(type);
            }
        });

        tsDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category != null && !tsAmount.getText().toString().equals("")){
                    DBHelper db = new DBHelper(getApplicationContext());
                    SharedPreferences preferences = getSharedPreferences("user", Activity.MODE_PRIVATE);
                    int userid = preferences.getInt("id", 1);
                    db.addCT(new CashTransaction(userid, category.getId(), Integer.parseInt(tsAmount.getText().toString()), tsNote.getText().toString(), date, date));
                    Intent data = getIntent();
                    setResult(RESULT_OK, data);
                    finish();
                    Toast.makeText(getApplicationContext(), R.string.txt_add_ct_success, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.txt_error_add_ct_failed, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        String strDay = day < 10 ? "0" + day: day+"";
        String strMonth = month < 10 ? "0" + month: month+"";
        date = year + "-" + strMonth + "-" + strDay;

        tsDate.setText(new StringBuilder().append(strDay).append("/")
                .append(strMonth).append("/").append(year));

    }

    private void showCategorySelector(int type){
        Intent categoryIntent = new Intent(this, CategorySelectorActivity.class);
        categoryIntent.putExtra("categoryType", type);
        startActivityForResult(categoryIntent,MY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == MY_REQUEST_CODE ){
            categoryid = data.getIntExtra("categoryid", 1);

            DBHelper db = new DBHelper(this);

            //Load locale
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String lang = preferences.getString("lang_list","vi");
            category = db.getCategory(categoryid, lang);
            tsCategory.setText(category.getName());
        }
    }
}
