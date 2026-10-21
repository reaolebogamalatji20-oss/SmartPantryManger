package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.db.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.util.AppUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

/** Used for BOTH adding and editing. Edit mode is triggered by an ID passed in the Intent. */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private DatabaseHelper db;
    private TextInputLayout tilName, tilQty;
    private TextInputEditText etName, etQty, etExpiry;
    private Spinner spUnit;
    private long editingId = -1; // -1 means "add new"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        tilName = findViewById(R.id.tilName);
        tilQty = findViewById(R.id.tilQty);
        etName = findViewById(R.id.etName);
        etQty = findViewById(R.id.etQty);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);

        // Read the Intent extra: default -1 if no ID was passed
        editingId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (editingId != -1) {
            setTitle("Edit ingredient");
            loadExistingItem();
        } else {
            setTitle("Add ingredient");
        }

        etExpiry.setOnClickListener(v -> showDatePicker());
        etExpiry.setOnLongClickListener(v -> { etExpiry.setText(""); return true; });
        findViewById(R.id.btnSave).setOnClickListener(v -> save());
    }

    private void loadExistingItem() {
        PantryItem item = db.getPantryItem(editingId);
        if (item == null) { finish(); return; }
        etName.setText(item.getName());
        etQty.setText(AppUtils.formatQuantity(item.getQuantity()));
        if (item.getExpiryDate() != null) etExpiry.setText(item.getExpiryDate());

        // Select the saved unit in the spinner
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) spUnit.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(item.getUnit())) {
                spUnit.setSelection(i);
                break;
            }
        }
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) ->
                        etExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void save() {
        tilName.setError(null);
        tilQty.setError(null);

        String name = etName.getText().toString().trim();
        String qtyText = etQty.getText().toString().trim();
        boolean valid = true;

        // ---- Input validation
        if (name.isEmpty()) {
            tilName.setError("Please enter an ingredient name");
            valid = false;
        } else if (name.length() > 40) {
            tilName.setError("Name is too long (max 40 characters)");
            valid = false;
        }

        double qty = 0;
        if (qtyText.isEmpty()) {
            tilQty.setError("Please enter a quantity");
            valid = false;
        } else {
            try {
                qty = Double.parseDouble(qtyText);
                if (qty <= 0) {
                    tilQty.setError("Quantity must be greater than zero");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                tilQty.setError("Enter a valid number");
                valid = false;
            }
        }
        if (!valid) return; // stop: nothing is written to the database

        String unit = spUnit.getSelectedItem().toString();
        String expiry = etExpiry.getText().toString().trim();
        if (expiry.isEmpty()) expiry = null;

        PantryItem item = new PantryItem(editingId, name, qty, unit, expiry);
        if (editingId == -1) {
            db.insertPantryItem(item);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            db.updatePantryItem(item);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish(); // returns to the list, whose onResume() reloads the data
    }

    @Override
    public boolean onSupportNavigateUp() { // the ActionBar "back arrow"
        finish();
        return true;
    }
}