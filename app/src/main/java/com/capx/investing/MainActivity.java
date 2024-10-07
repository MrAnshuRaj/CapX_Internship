package com.capx.investing;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private TextView stockName, stockPrice, stockPercentChange, desc_main, desc_sub, loading_tv;
    private ProgressBar progressBar;
    private CardView detailsCV;
    private AutoCompleteTextView searchSymbol;
    private ImageView percentageIndicator;
    private ConstraintLayout mainLayout;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        stockName = findViewById(R.id.stock_name);
        stockPrice = findViewById(R.id.stock_price);
        stockPercentChange = findViewById(R.id.stock_percent_change);
        percentageIndicator = findViewById(R.id.percentage_indicator);
        searchButton = findViewById(R.id.search_button);
        searchSymbol = findViewById(R.id.search_symbol_input);
        desc_main = findViewById(R.id.DescriptionMain);
        desc_sub = findViewById(R.id.Desc_subtitle);
        mainLayout = findViewById(R.id.main);
        loading_tv = findViewById(R.id.loading_tv);
        progressBar = findViewById(R.id.progressBar);
        detailsCV = findViewById(R.id.detailsCard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupObservers();
        setupSearchButton();
        List<String> stockSymbols = readStockSymbolsFromRawFile();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, stockSymbols);
        searchSymbol.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getStockDetails().observe(this, stockDetails -> {
            if (stockDetails != null) {
                stockName.setText(stockDetails.getName());
                stockPrice.setText(stockDetails.getPrice());
                stockPercentChange.setText(String.format("%.2f%%", stockDetails.getPercentChange()));

                if (stockDetails.getPercentChange() == 0) {
                    percentageIndicator.setVisibility(View.GONE);
                } else if (stockDetails.getPercentChange() > 0) {
                    percentageIndicator.setVisibility(View.VISIBLE);
                    percentageIndicator.setImageResource(R.drawable.increase);
                } else {
                    percentageIndicator.setVisibility(View.VISIBLE);
                    percentageIndicator.setImageResource(R.drawable.decrease);
                }
                progressBar.setVisibility(View.GONE);
                loading_tv.setVisibility(View.GONE);
                detailsCV.setVisibility(View.VISIBLE);
            }
        });
        viewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                loading_tv.setVisibility(View.VISIBLE);
                detailsCV.setVisibility(View.GONE);
                desc_main.setVisibility(View.GONE);
                desc_sub.setVisibility(View.GONE);
                mainLayout.setBackgroundColor(Color.parseColor("#EDEDED"));
            } else {
                progressBar.setVisibility(View.GONE);
                loading_tv.setVisibility(View.GONE);
                mainLayout.setBackgroundColor(Color.WHITE);
            }
        });

        viewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                desc_main.setVisibility(View.VISIBLE);
                desc_sub.setVisibility(View.VISIBLE);
                mainLayout.setBackgroundColor(Color.WHITE);
            }
        });
    }

    private void setupSearchButton() {
        searchButton.setOnClickListener(v -> {
            hideKeyboard();
            detailsCV.setVisibility(View.GONE);
            String ticker = searchSymbol.getText().toString().toUpperCase();
            viewModel.loadStockData(ticker);
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
    }

    private List<String> readStockSymbolsFromRawFile() {
        List<String> stockSymbols = new ArrayList<>();
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.stock_symbols);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stockSymbols.add(line.trim());
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return stockSymbols;
    }
}
