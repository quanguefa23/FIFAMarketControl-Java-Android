package com.nhq.fifamarketcontrol;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_CODE = 1000;
    private static final int REQUEST_CODE_ADD = 1111;
    private static final int REQUEST_CODE_EDIT = 1211;
    ListView listView;
    ArrayList<FootballPlayer> listPlayer;
    PlayerAdapter adapter;
    Database database;
    int selectedIdInListPlayer = 0;
    int selectedIdInDatabase = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        //create database object
        createDatabaseObject();

        //initial list of football player
        listPlayer = new ArrayList<>();
        //initialListFootballPlayer(listPlayer);
        initialListPlayerFromDatabase();

        //initial adapter and adapt to listView
        adapter = new PlayerAdapter(MainActivity.this, R.layout.list_player, listPlayer);
        listView.setAdapter(adapter);

        //show pop up menu if long click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.setShowed(true);
                showMenu(view, i);
                return false;
            }
        });
    }

    private void initialListPlayerFromDatabase() {
        Cursor data = database.GetData("SELECT * FROM Contract");
        while (data.moveToNext()) {
            int id = data.getInt(0);
            String name = data.getString(1);
            String position = data.getString(2);
            int ovr = data.getInt(3);
            boolean isBuy = (data.getInt(4) == 1);
            int price = data.getInt(5);
            String dateString = data.getString(6);
            Calendar date = getDateFromString(dateString);
            listPlayer.add(new FootballPlayer(id, name, position, ovr, isBuy, price, date));
        }
        sortPlayerByName(listPlayer);
    }

    private Calendar getDateFromString(String dateString) {
        Calendar res = Calendar.getInstance();
        String[] token = dateString.split("-");
        int date = Integer.parseInt(token[0]);
        int month = Integer.parseInt(token[1]);
        int year = Integer.parseInt(token[2]);
        res.set(year, month - 1, date);
        return res;
    }

    private void createDatabaseObject() {
        database = new Database(this, "dataContract.sqlite", null, 1);

        try {
            //create table
            database.QueryData("CREATE TABLE IF NOT EXISTS " +
                    "Contract(id INTEGER PRIMARY KEY, " +
                    "name VARCHAR(50), " +
                    "position VARCHAR(50), " +
                    "ovr INTEGER, " +
                    "isBuy INTEGER, " +
                    "price INTEGER, " +
                    "date VARCHAR(20))");

            //insert data (demo)
/*            database.QueryData("INSERT INTO Contract " +
                    "VALUES" +
                    "(3, 'Icardi', 'CM', 85, 1, 1050, '23/11/2019')");
            database.QueryData("INSERT INTO Contract " +
                    "VALUES" +
                    "(4, 'Ronaldo', 'ST', 88, 0, 5050, '20/11/2019')");*/
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ADD: {
                if (resultCode == RESULT_OK && data != null) {
                    //add contract to listPlayer
                    int isBuyInt = data.getIntExtra("isBuy", 1);
                    boolean isBuy = (isBuyInt == 1);
                    String name = data.getStringExtra("name");
                    String position = data.getStringExtra("position");
                    int ovr = data.getIntExtra("ovr", 100);
                    int price = data.getIntExtra("price", 0);
                    String dateString = data.getStringExtra("date");
                    Calendar date = getDateFromString(dateString);

                    //add new contract to top of list
                    int id = getNewId();
                    FootballPlayer newContract = new FootballPlayer(id, name, position, ovr, isBuy, price, date);
                    listPlayer.add(0, newContract);
                    adapter.notifyDataSetChanged();

                    //add contract to database
                    try {
                        String sql = "INSERT INTO Contract " +
                                "VALUES" +
                                "(" + id +
                                ", '" + name +
                                "', '" + position +
                                "', " + ovr +
                                ", " + isBuyInt +
                                ", " + price +
                                ", '" + dateString +
                                "')";
                        database.QueryData(sql);
                        Toast.makeText(this, "Thêm hợp đồng mới thành công", Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
            case REQUEST_CODE_EDIT: {
                if (resultCode == RESULT_OK && data != null) {
                    //update contract to listPlayer
                    int isBuyInt = data.getIntExtra("isBuy", 1);
                    boolean isBuy = (isBuyInt == 1);
                    String name = data.getStringExtra("name");
                    String position = data.getStringExtra("position");
                    int ovr = data.getIntExtra("ovr", 100);
                    int price = data.getIntExtra("price", 0);
                    String dateString = data.getStringExtra("date");
                    Calendar date = getDateFromString(dateString);

                    FootballPlayer newContract = new FootballPlayer(selectedIdInDatabase, name, position, ovr, isBuy, price, date);
                    listPlayer.set(selectedIdInListPlayer, newContract);
                    adapter.notifyDataSetChanged();

                    //update database
                    try {
                        String sql = "UPDATE Contract SET\n" +
                                "name = '" + name + "', " +
                                "position = '" + position + "', " +
                                "ovr = " + ovr + ", " +
                                "isBuy = " + isBuyInt + ", " +
                                "price = " + price + ", " +
                                "date = '" + dateString + "'\n" +
                                "WHERE id = " + selectedIdInDatabase;
                        database.QueryData(sql);
                        Toast.makeText(this, "Sửa hợp đồng thành công", Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private int getNewId() {
        if (listPlayer.isEmpty())
            return 0;

        int maxID = -1;
        for (FootballPlayer f : listPlayer) {
            if (f.getId() > maxID)
                maxID = f.getId();
        }

        for (int i = 0; i < maxID; i++) {
            if (!isExistInListPlayer(i))
                return i;
        }
        return maxID + 1;
    }

    private boolean isExistInListPlayer(int i) {
        for (FootballPlayer f : listPlayer) {
            if (i == f.getId())
                return true;
        }
        return false;
    }

    //config option item selection in options menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(MainActivity.this, AddContractActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD);
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                return true;
            case R.id.statistic:
                showDialogStatistic();
                return true;
            case R.id.export:
                confirmExportData();
                return true;
            case R.id.sortByName1:
                sortPlayerByName(listPlayer);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.sortByName2:
                sortPlayerByName2(listPlayer);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.sortByPrice1:
                sortPlayerByPrice1(listPlayer);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.sortByPrice2:
                sortPlayerByPrice2(listPlayer);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.sortByDate1:
                sortPlayerByDate1(listPlayer);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.sortByDate2:
                sortPlayerByDate2(listPlayer);
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void confirmExportData() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Xuất dữ liệu");
        alertDialog.setMessage("Chuyển dữ liệu mua bán cầu thủ thành file PDF?");

        alertDialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //we need to handle runtime permission for devices with Marshmallow and above
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    //system OS >= Marshmallow (M, 6.0), check if permission is enabled or not

                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        //permission was not granted, request it
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, STORAGE_CODE);
                    }
                    else {
                        //permission already granted, call save pdf method
                        savePdf();
                    }
                }
                else {
                    //system OS < M, no required to check runtime permission
                    savePdf();
                }
            }
        });

        alertDialog.setNegativeButton("Hủy", null);
        alertDialog.show();
    }

    private void savePdf() {
        //create object of Document class
        Document doc = new Document();
        //pdf file name
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String fileName = "FIFA-Market-Data-" + dateFormat.format(Calendar.getInstance().getTime());
        //pdf file path
        String filePath = Environment.getExternalStorageDirectory() + "/" + fileName + ".pdf";

        try {
            //create instance of PdfWriter class
            PdfWriter.getInstance(doc, new FileOutputStream(filePath));
            //open the document for writing
            doc.open();

            //add data of each contract in array to file
            for (FootballPlayer f : listPlayer) {
                doc.add(new Paragraph(f.toString()));
            }

            //add author of the document
            doc.addAuthor("quanguefa");
            //close the document
            doc.close();
            //show message that file is saved
            Toast.makeText(this, fileName + ".pdf is saved to " + filePath, Toast.LENGTH_LONG).show();

        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted from popup
                    savePdf();
                }
                else {
                    //permission was denied from popup, show error message
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void sortPlayerByDate1(ArrayList<FootballPlayer> listPlayer) {
        Collections.sort(listPlayer, new Comparator<FootballPlayer>(){

            public int compare(FootballPlayer f1, FootballPlayer f2)
            {
                return f1.getDate().compareTo(f2.getDate());
            }
        });
    }

    private void sortPlayerByDate2(ArrayList<FootballPlayer> listPlayer) {
        Collections.sort(listPlayer, new Comparator<FootballPlayer>(){

            public int compare(FootballPlayer f1, FootballPlayer f2)
            {
                return -f1.getDate().compareTo(f2.getDate());
            }
        });
    }

    private void sortPlayerByPrice1(ArrayList<FootballPlayer> listPlayer) {
        Collections.sort(listPlayer, new Comparator<FootballPlayer>(){

            public int compare(FootballPlayer f1, FootballPlayer f2)
            {
                Integer num1 = f1.getPrice();
                Integer num2 = f2.getPrice();
                return num1.compareTo(num2);
            }
        });
    }

    private void sortPlayerByPrice2(ArrayList<FootballPlayer> listPlayer) {
        Collections.sort(listPlayer, new Comparator<FootballPlayer>(){

            public int compare(FootballPlayer f1, FootballPlayer f2)
            {
                Integer num1 = f1.getPrice();
                Integer num2 = f2.getPrice();
                return -num1.compareTo(num2);
            }
        });
    }

    private void sortPlayerByName2(ArrayList<FootballPlayer> listPlayer) {
        Collections.sort(listPlayer, new Comparator<FootballPlayer>(){

            public int compare(FootballPlayer f1, FootballPlayer f2)
            {
                return -f1.getName().compareTo(f2.getName());
            }
        });
    }

    private void showDialogStatistic() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_statistic);
        dialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(dialog.getWindow()).
                setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView sumBuyTV = dialog.findViewById(R.id.sumBuy);
        TextView sumSellTV = dialog.findViewById(R.id.sumSell);
        TextView sumBuyCoinTV = dialog.findViewById(R.id.sumBuyCoin);
        TextView sumSellCoinTV = dialog.findViewById(R.id.sumSellCoin);
        TextView maxBuyTV = dialog.findViewById(R.id.maxBuy);
        TextView maxSellTV = dialog.findViewById(R.id.maxSell);

        int sumBuy, sumSell, sumBuyCoin, sumSellCoin, maxBuyID, maxSellID, maxBuyCoin, maxSellCoin;
        sumBuy = sumSell = sumBuyCoin = sumSellCoin = maxBuyID = maxSellID = maxBuyCoin = maxSellCoin = 0;
        for (int i = 0; i < listPlayer.size(); i++) {
            FootballPlayer t = listPlayer.get(i);
            if (t.isBuy()) {
                sumBuy++;
                sumBuyCoin += t.getPrice();
                if (t.getPrice() > maxBuyCoin) {
                    maxBuyID = i;
                    maxBuyCoin = t.getPrice();
                }

            }
            else {
                sumSell++;
                sumSellCoin += t.getPrice();
                if (t.getPrice() > maxSellCoin) {
                    maxSellID = i;
                    maxSellCoin = t.getPrice();
                }
            }
        }

        sumBuyTV.append(sumBuy + "");
        sumSellTV.append(sumSell + "");
        sumBuyCoinTV.append(sumBuyCoin + "");
        sumSellCoinTV.append(sumSellCoin + "");
        maxBuyTV.append(listPlayer.get(maxBuyID).getName() + " - " + listPlayer.get(maxBuyID).getPrice());
        maxSellTV.append(listPlayer.get(maxSellID).getName() + " - " + listPlayer.get(maxSellID).getPrice());

        Button bt_ok = dialog.findViewById(R.id.out_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

/*    private void initialListFootballPlayer(ArrayList<FootballPlayer> listPlayer) {
        Calendar today = Calendar.getInstance();

        Calendar sub = Calendar.getInstance();
        sub.set(2019, 10, 20);

        listPlayer.add(new FootballPlayer("Icardi", "CM", 85, true, 1050, sub));
        listPlayer.add(new FootballPlayer("Icardi", "CM", 85, false, 1550, sub));
        listPlayer.add(new FootballPlayer("Muller", "ST", 85, false, 5550, today));
        listPlayer.add(new FootballPlayer("Aguero", "ST", 85, false, 1550, today));
        listPlayer.add(new FootballPlayer("Bellarabi", "ST", 85, false, 5550, today));
        listPlayer.add(new FootballPlayer("Verratti", "ST", 85, false, 5550, today));
        listPlayer.add(new FootballPlayer("Icardi", "CM", 85, true, 1000, today));
        listPlayer.add(new FootballPlayer("Icardi", "CM", 85, false, 1550, today));
        listPlayer.add(new FootballPlayer("Muller", "ST", 85, false, 5450, today));
        listPlayer.add(new FootballPlayer("Ronaldo", "CM", 85, true, 10050, today));
        listPlayer.add(new FootballPlayer("Icardi", "CM", 85, false, 1550, today));
        listPlayer.add(new FootballPlayer("Messi", "ST", 85, false, 5550, today));
        listPlayer.add(new FootballPlayer("Verratti", "ST", 85, true, 5550, today));

        sortPlayerByName(listPlayer);
    }*/

    public void sortPlayerByName(ArrayList<FootballPlayer> listPlayer) {
        Collections.sort(listPlayer, new Comparator<FootballPlayer>(){

            public int compare(FootballPlayer f1, FootballPlayer f2)
            {
                return f1.getName().compareTo(f2.getName());
            }
        });
    }

    //function to create pop-up menu and config option item selection
    private void showMenu(View view,final int i) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_delete, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.movetop: {
                        adapter.moveTop(i);
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                    case R.id.movebottom: {
                        adapter.moveBottom(i);
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                    case R.id.edit: {
                        editContract(i);
                        return true;
                    }
                    case R.id.delete: {
                        confirmDelete(i);
                        return true;
                    }
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    private void editContract(int i) {
        Intent intent = new Intent(MainActivity.this, EditContractActivity.class);
        FootballPlayer temp = listPlayer.get(i);
        int isBuy = temp.isBuy() ? 1 : 0;
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(temp.getDate().getTime());
        intent.putExtra("isBuy", isBuy);
        intent.putExtra("name", temp.getName());
        intent.putExtra("position", temp.getPostion());
        intent.putExtra("ovr", temp.getOvr());
        intent.putExtra("price", temp.getPrice());
        intent.putExtra("date", date);
        selectedIdInListPlayer = i;
        selectedIdInDatabase = listPlayer.get(i).getId();

        startActivityForResult(intent, REQUEST_CODE_EDIT);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    private void confirmDelete(final int id) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Xác nhận");
        alertDialog.setMessage("Bạn có muốn xóa hợp đồng này?");

        alertDialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int idInDatabase = listPlayer.get(id).getId();
                removeContractInDatabase(idInDatabase);
                adapter.remove(id);
                adapter.notifyDataSetChanged();
            }
        });
        alertDialog.setNegativeButton("Hủy", null);

        alertDialog.show();
    }

    private void removeContractInDatabase(int idInDatabase) {
        try {
            String sql = "DELETE FROM Contract\n" +
                    "WHERE id = " + idInDatabase;
            database.QueryData(sql);
            Toast.makeText(this, "Xóa hợp đồng thành công", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
