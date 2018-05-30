package lecture.com.cashmanager.menu.cashtransaction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import lecture.com.cashmanager.model.Category;

public class AddTransactionActivity extends AppCompatActivity{

    Category category;
    private static final int MODE_ADD_INCOME = 1;
    private static final int MODE_ADD_EXPENSE = -1;
    private static final int MY_REQUEST_CODE = 1234;
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
        tsDate.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
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
            category = db.getCategory(categoryid);
            tsCategory.setText(category.getName());
        }
    }
}
