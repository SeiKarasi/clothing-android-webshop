package com.example.clothingwebshop;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ShopListActivity extends AppCompatActivity {

    private static final String LOG_TAG = ShopListActivity.class.getName();
    private FirebaseUser user;

    private RecyclerView mRecyclerView;
    private ArrayList<ShoppingItem> mItemsData;
    private ShoppingItemAdapter mAdapter;

    private int gridNumber = 1;
    private boolean viewRow = true;
    private int cartItems = 0;

    private FrameLayout redCircle;
    private TextView contentTextView;

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    private NotificationHandler mNotificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            Log.d(LOG_TAG, "Hitelesített felhasználó!");
        } else {
            Log.e(LOG_TAG, "NEM hitelesített felhasználó!");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemsData = new ArrayList<>();

        mAdapter = new ShoppingItemAdapter(this, mItemsData);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        queryItemData();

        mNotificationHandler = new NotificationHandler(this);

    }

    private void queryItemData(){
        mItemsData.clear();

        mItems.orderBy("add_to_cart_count", Query.Direction.DESCENDING).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                ShoppingItem item = documentSnapshot.toObject(ShoppingItem.class);
                item.setId(documentSnapshot.getId());
                mItemsData.add(item);
            }
            if(mItemsData.size() == 0){
                initializeItemData();
                queryItemData();
            }

            mAdapter.notifyDataSetChanged();
        });
    }

    public void deleteItem(ShoppingItem item){
        DocumentReference reference = mItems.document(item._getId());
        reference.delete().addOnSuccessListener(success -> {
            Toast.makeText(this, "A(z) " + item.getName() + " nevezetű termék törlése sikerült!", Toast.LENGTH_LONG).show();
        }).addOnFailureListener(failure ->{
            Toast.makeText(this, "Az alábbi termék törlése nem sikerült: " + item._getId(), Toast.LENGTH_LONG).show();
        });

        queryItemData();
        mNotificationHandler.cancel();
    }



    private void initializeItemData() {
        String[] itemsList = getResources().getStringArray(R.array.clothing_item_names);
        String[] itemsInfo = getResources().getStringArray(R.array.clothing_item_info);;
        String[] itemsPrice = getResources().getStringArray(R.array.clothing_item_price);;
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.clothing_item_images);
        TypedArray itemsRate = getResources().obtainTypedArray(R.array.clothing_item_rates);


        for(int i = 0; i < itemsList.length; i++){
            mItems.add(new ShoppingItem(
                    itemsList[i],
                    itemsInfo[i],
                    itemsPrice[i],
                    itemsRate.getFloat(i, 0),
                    itemsImageResource.getResourceId(i, 0),
                    0));

        }

        itemsImageResource.recycle();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.cloth_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.cart:
                return true;
            case R.id.view_type:
                if(viewRow) {
                    changeSpanCount(item, R.drawable.icon_view_2, 1);
                } else {
                    changeSpanCount(item, R.drawable.icon_view_1, 3);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int i) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(i);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();
        redCircle = rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(view -> onOptionsItemSelected(alertMenuItem));
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(ShoppingItem item){
        cartItems += 1;

        if(0 < cartItems){
            contentTextView.setText(String.valueOf(cartItems));
        } else {
            contentTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);

        mItems.document(item._getId()).update("add_to_cart_count", item.getAdd_to_cart_count() + 1)
                .addOnFailureListener(failure -> {
                    Toast.makeText(this, "Az alábbi termék frissítése nem sikerült: " + item._getId(), Toast.LENGTH_LONG).show();
                });


        mNotificationHandler.send(item.getName());
        queryItemData();
    }
}