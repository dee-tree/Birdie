package com.codemitry.birdie;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Arrays;


public class ShopActivity extends AppCompatActivity {
    int money;

    int[] prices = {0, 35};
    int[] names = {R.string.first_texture, R.string.second_texture};
    int[] pictures = {R.drawable.shop_texture0, R.drawable.shop_texture1};

    boolean[] bought;
    int selected;

    ConstraintLayout dialog;

    ImageView[] locks;
    TextView moneyTextView;
    TextView[] texturePrices;
    View[] texturesLocks;
    ImageView[] texturesImages;
    TextView[] texturesNames;
    Button[] texturesButtons;
    View[] textures;
    int textureCount = Config.TEXTURES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        money = Config.loadMoney(this);
        moneyTextView = findViewById(R.id.money);
        updateMoneyText();

        dialog = findViewById(R.id.buy_dialog);
//
        Config.saveMoney(this, 36);
        Config.saveBoughtTexture(this, 1, false);

        loadSelected();
        loadBought();

        textures = new View[textureCount];
        textures[0] = findViewById(R.id.first_texture);
        textures[1] = findViewById(R.id.second_texture);

        loadNames();
        updateNames();

        loadPrices();

        loadImages();
        updateImages();

        loadLocks();
        updateLocks();

        loadButtons();

        updateButtons();

    }

    void loadPrices() {
        texturePrices = new TextView[textureCount];
        for (int i = 0; i < textureCount; i++) {
            texturePrices[i] = textures[i].findViewById(R.id.price);
            texturePrices[i].setText(String.valueOf(prices[i]));
        }
    }

    void loadNames() {
        texturesNames = new TextView[textureCount];
        for (int i = 0; i < textureCount; i++)
            texturesNames[i] = textures[i].findViewById(R.id.name);
    }

    void updateNames() {
        for (int i = 0; i < textureCount; i++)
            texturesNames[i].setText(names[i]);
    }

    void loadButtons() {
        texturesButtons = new Button[textureCount];
        for (int i = 0; i < textureCount; i++) {
            texturesButtons[i] = textures[i].findViewById(R.id.button);
            setOnClick(texturesButtons[i], i);
        }
    }

    void updateButtons() {
        for (int i = 0; i < textureCount; i++) {
            if (bought[i]) {
                texturesButtons[i].setEnabled(true);
                texturesButtons[i].setText(R.string.select);
            } else {
                texturesButtons[i].setEnabled(true);
                texturesButtons[i].setText(R.string.buy);
            }
        }
        texturesButtons[selected].setEnabled(false);
        texturesButtons[selected].setText(R.string.selected);
    }

    void loadImages() {
        texturesImages = new ImageView[textureCount];
        for (int i = 0; i < textureCount; i++) {
            texturesImages[i] = textures[i].findViewById(R.id.picture);
            texturesImages[i].setImageResource(pictures[i]);
        }
    }

    void loadLocks() {
        texturesLocks = new View[textureCount];
        locks = new ImageView[textureCount];

        for (int i = 0; i < textureCount; i++) {
            texturesLocks[i] = textures[i].findViewById(R.id.lock_layout);
            locks[i] = texturesLocks[i].findViewById(R.id.lock);
        }

    }

    void updateLocks() {
        System.out.println(Arrays.toString(bought));
        for (int i = 0; i < textureCount; i++)
            texturesLocks[i].setVisibility(bought[i] ? View.GONE : View.VISIBLE);
    }

    void updateImages() {
        for (int i = 0; i < textureCount; i++) {

        }
    }

    void loadBought() {
        bought = Config.loadBoughtTextures(this);
    }

    void loadSelected() {
        selected = Config.loadSelectedTexture(this);
    }

    void updateMoneyText() {
        moneyTextView.setText(String.valueOf(money));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void onBackClick(View v) {
        if (dialog.getVisibility() == View.VISIBLE)
            hideBuyDialog();
        else
            super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (dialog.getVisibility() == View.VISIBLE)
            hideBuyDialog();
        else
            super.onBackPressed();
    }


    private void setOnClick(final Button btn, final int texture) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBought();
                loadSelected();


                // Смысла проверять на selected нет, поскольку кнопка с selected - Disabled и не нажимается
                if (bought[texture]) {
                    Config.saveSelectedTexture(ShopActivity.this, texture);
                    selected = texture;
                    updateButtons();
                } else {
                    // Достаточно денег на покупку текстуры
                    if (money >= prices[texture]) {
                        showBuyDialog(texture);
                        setOnYesClick(findViewById(R.id.yes), texture);
                    } else {
                        Toast.makeText(ShopActivity.this, getString(R.string.no_money) + " " + (prices[texture] - money) + " BCoins", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    void showBuyDialog(int texture) {
        for (int i = 0; i < textureCount; i++)
            texturesButtons[i].setEnabled(false);

        ((TextView) dialog.findViewById(R.id.texture_name)).setText(names[texture]);
        ((TextView) dialog.findViewById(R.id.cost_text)).setText(R.string.will_cost);
        ((TextView) dialog.findViewById(R.id.cost_text)).append("\n" + prices[texture] + " BCoins");

        dialog.setVisibility(View.VISIBLE);
    }


    void hideBuyDialog() {
        updateButtons();
        dialog.setVisibility(View.GONE);
    }


    public void setOnYesClick(View v, final int texture) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBuyDialog();
                if (money >= prices[texture]) {
                    Config.saveBoughtTexture(ShopActivity.this, texture, true);
                    Config.saveSelectedTexture(ShopActivity.this, texture);

                    Config.saveMoney(ShopActivity.this, money - prices[texture]);
                    money -= prices[texture];
                    updateMoneyText();

                    selected = texture;
                    bought[texture] = true;

                    locks[texture].animate().scaleX(0.3f).scaleY(0.3f).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            locks[texture].animate().scaleX(2f).scaleY(2f).setDuration(650).alpha(0).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    updateLocks();
                                }
                            });
                        }
                    });


                    texturesButtons[texture].animate().scaleX(0.3f).scaleY(0.3f).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            texturesButtons[texture].animate().scaleX(1f).scaleY(1f).setDuration(650).start();
                            updateButtons();
                        }
                    });
                }
            }
        });
    }

    public void onLockClick(View v) {
        final ImageView lock = v.findViewById(R.id.lock);

        lock.animate().scaleY(1.5f).scaleX(1.5f).setDuration(400).withEndAction(new Runnable() {
            @Override
            public void run() {
                lock.animate().scaleY(1f).scaleX(1f).setDuration(400).start();
            }
        });
    }


    public void onNoClick(View v) {
        hideBuyDialog();
    }
}
