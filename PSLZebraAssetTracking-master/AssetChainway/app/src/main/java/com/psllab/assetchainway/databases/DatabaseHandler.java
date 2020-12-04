package com.psllab.assetchainway.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;


import com.psllab.assetchainway.modals.AssetMaster;
import com.psllab.assetchainway.modals.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ASSET.DB";


    //USER TABLE
    public static final String DB_USER_ID = "UserID";
    public static final String DB_USER_USERNAME = "UserName";
    public static final String DB_USER_PASSWORD = "Password";
    public static final String DB_USER_FIRST_NAME = "FirstName";
    public static final String DB_USER_LAST_NAME = "LastName";
    public static final String DB_USER_COMPANY_CODE = "CompanyCode";
    public static final String DB_USER_TRANSACTION_DATE_TIME = "TransactionDateTime";
    public static final String DB_USER_IS_ACTIVE = "IsActive";


    //ASSET MASTER TABLE

    public static final String DB_ASSET_CID = "CID";
    public static final String DB_ASSET_REF_ID = "RefID";
    public static final String DB_ASSET_NAME = "Name";
    public static final String DB_ASSET_DESCRIPTION = "Description";
    public static final String DB_ASSET_CATAGORY_ID = "CategoryID";
    public static final String DB_ASSET_CATAGORY_NAME = "CategoryName";
    public static final String DB_ASSET_DATAFIELD_1 = "DataField1";
    public static final String DB_ASSET_DATAFIELD_2 = "DataField2";
    public static final String DB_ASSET_DATAFIELD_3 = "DataField3";
    public static final String DB_ASSET_DATAFIELD_4 = "DataField4";
    public static final String DB_ASSET_TAG_ID = "TagID";
    public static final String DB_ASSET_SR_NO = "SerialNo";
    public static final String DB_ASSET_USER_ID = "UserID";
    public static final String DB_ASSET_TRANSACTION_DATE_TIME = "TransactionDateTime";

    //TABLES
    public static final String TABLE_USERS = "Users_Table";
    public static final String TABLE_ASSET_MASTER = "Asset_Master_Table";



    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_USER = "CREATE TABLE "
                + TABLE_USERS
                + "("
                + DB_USER_ID + " TEXT UNIQUE,"//0
                + DB_USER_USERNAME + " TEXT ,"//1
                + DB_USER_PASSWORD + " TEXT ,"//2
                + DB_USER_FIRST_NAME + " TEXT ,"//3
                + DB_USER_LAST_NAME + " TEXT ,"//4
                + DB_USER_COMPANY_CODE + " TEXT ,"//5
                + DB_USER_TRANSACTION_DATE_TIME + " TEXT ,"//6
                + DB_USER_IS_ACTIVE + " TEXT"//7
                + ")";

        String CREATE_TABLE_ASSET_MASTER = "CREATE TABLE "
                + TABLE_ASSET_MASTER
                + "("
                + DB_ASSET_CID + " TEXT UNIQUE,"//0
                + DB_ASSET_REF_ID + " TEXT ,"//1
                + DB_ASSET_NAME + " TEXT ,"//2
                + DB_ASSET_DESCRIPTION + " TEXT ,"//3
                + DB_ASSET_CATAGORY_ID + " TEXT ,"//4
                + DB_ASSET_CATAGORY_NAME + " TEXT ,"//5
                + DB_ASSET_DATAFIELD_1 + " TEXT ,"//6
                + DB_ASSET_DATAFIELD_2 + " TEXT ,"//7
                + DB_ASSET_DATAFIELD_3 + " TEXT ,"//8
                + DB_ASSET_DATAFIELD_4 + " TEXT ,"//9
                + DB_ASSET_TAG_ID + " TEXT ,"//10
                + DB_ASSET_SR_NO + " TEXT ,"//11
                + DB_ASSET_USER_ID + " TEXT ,"//12
                + DB_ASSET_TRANSACTION_DATE_TIME + " TEXT"//13
                + ")";

        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_ASSET_MASTER);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSET_MASTER);
        // Create tables again

        onCreate(db);
    }

    //store user

    public void storeUserMaster(List<User> lst) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO Users_Table (UserID,UserName,Password,FirstName,LastName,CompanyCode,TransactionDateTime,IsActive) VALUES (?, ?, ?, ?, ?, ? ,?, ?)";
        //db.beginTransaction();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(sql);
        try {
            for (int i = 0; i < lst.size(); i++) {

                // String UserID,UserName,Password,FirstName,LastName,CompanyCode,TransactionDateTime,IsActive;
                stmt.bindString(1, lst.get(i).getUserID());
                stmt.bindString(2, lst.get(i).getUserName());
                stmt.bindString(3, lst.get(i).getPassword());
                stmt.bindString(4, lst.get(i).getFirstName());
                stmt.bindString(5, lst.get(i).getLastName());
                stmt.bindString(6, lst.get(i).getCompanyCode());
                stmt.bindString(7, lst.get(i).getTransactionDateTime());
                stmt.bindString(8, lst.get(i).getIsActive());
                stmt.execute();
                stmt.clearBindings();
            }
            db.setTransactionSuccessful();
            // db.endTransaction();
        } catch (Exception e) {
            Log.e("in exc r", e.getMessage());
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }
    }


    //store ASSET MASTER

    public void storeAssetMaster(List<AssetMaster> lst) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO Asset_Master_Table (CID,RefID,Name,Description,CategoryID,CategoryName,DataField1,DataField2,DataField3,DataField4,TagID,SerialNo,UserID,TransactionDateTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?)";
        //db.beginTransaction();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(sql);
        try {
            for (int i = 0; i < lst.size(); i++) {

                stmt.bindString(1, lst.get(i).getCID());
                stmt.bindString(2, lst.get(i).getRefID());
                stmt.bindString(3, lst.get(i).getName());
                stmt.bindString(4, lst.get(i).getDescription());
                stmt.bindString(5, lst.get(i).getCategoryID());
                stmt.bindString(6, lst.get(i).getCategoryName());
                stmt.bindString(7, lst.get(i).getDataField1());
                stmt.bindString(8, lst.get(i).getDataField2());
                stmt.bindString(9, lst.get(i).getDataField3());
                stmt.bindString(10, lst.get(i).getDataField4());
                stmt.bindString(11, lst.get(i).getTagID());
                stmt.bindString(12, lst.get(i).getSerialNo());
                stmt.bindString(13, lst.get(i).getUserID());
                stmt.bindString(14, lst.get(i).getTransactionDateTime());
                stmt.execute();
                stmt.clearBindings();
            }
            db.setTransactionSuccessful();
            // db.endTransaction();
        } catch (Exception e) {
            Log.e("in exc r", e.getMessage());
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }
    }

    //USER COUNT

    public int getUserCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    //ASSET MASTER COUNT

    public int getAssetMasterCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ASSET_MASTER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    //GET USER IS ACTIVE
    public String getUserActive(String username, String password) {
        String task_number = "";
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  "+DB_USER_IS_ACTIVE +" FROM "+TABLE_USERS+" WHERE "+DB_USER_USERNAME +" = '"+username+"' AND "+DB_USER_PASSWORD +" = '"+password+"' " ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                task_number = cursor.getString(cursor.getColumnIndex(DB_USER_IS_ACTIVE));
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return task_number;
    }

    //GET USER IS ACTIVE
    public String getUserNameActive(String username, String password) {
        String task_number = "";
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  "+DB_USER_IS_ACTIVE +" FROM "+TABLE_USERS+" WHERE "+DB_USER_USERNAME +" = '"+username+"' " ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                task_number = cursor.getString(cursor.getColumnIndex(DB_USER_IS_ACTIVE));
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return task_number;
    }


    //GET USER IS ACTIVE
    public String getUserID(String username, String password) {
        String task_number = "";
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  "+DB_USER_ID +" FROM "+TABLE_USERS+" WHERE "+DB_USER_USERNAME +" = '"+username+"' " ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                task_number = cursor.getString(cursor.getColumnIndex(DB_USER_ID));
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return task_number;
    }

    //GET COMPANY CODE BY USER
    public String getUserCompanyCode(String username, String password) {
        String task_number = "";
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  "+DB_USER_COMPANY_CODE +" FROM User_Table WHERE "+DB_USER_USERNAME +" = '"+username+"' AND "+DB_USER_PASSWORD +" = '"+password+"' " ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                task_number = cursor.getString(cursor.getColumnIndex(DB_USER_COMPANY_CODE));
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return task_number;
    }

    //delete USER TABLE
    public void deleteUserMaster() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, null, null);
        db.close();
    }

    //delete ASSET MASTER TABLE
    public void deleteAssetMaster() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ASSET_MASTER, null, null);
        db.close();
    }


    public ArrayList<AssetMaster> getAllMasterList() {
        ArrayList<AssetMaster> barcodeList = new ArrayList<AssetMaster>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ASSET_MASTER;
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                AssetMaster assetMaster = new AssetMaster();

                assetMaster.setCID(cursor.getString(cursor.getColumnIndex(DB_ASSET_CID)));
                assetMaster.setRefID(cursor.getString(cursor.getColumnIndex(DB_ASSET_REF_ID)));
                assetMaster.setName(cursor.getString(cursor.getColumnIndex(DB_ASSET_NAME)));
                assetMaster.setDescription(cursor.getString(cursor.getColumnIndex(DB_ASSET_DESCRIPTION)));
                assetMaster.setCategoryID(cursor.getString(cursor.getColumnIndex(DB_ASSET_CATAGORY_ID)));
                assetMaster.setCategoryName(cursor.getString(cursor.getColumnIndex(DB_ASSET_CATAGORY_NAME)));
                assetMaster.setDataField1(cursor.getString(cursor.getColumnIndex(DB_ASSET_DATAFIELD_1)));
                assetMaster.setDataField2(cursor.getString(cursor.getColumnIndex(DB_ASSET_DATAFIELD_2)));
                assetMaster.setDataField3(cursor.getString(cursor.getColumnIndex(DB_ASSET_DATAFIELD_3)));
                assetMaster.setDataField4(cursor.getString(cursor.getColumnIndex(DB_ASSET_DATAFIELD_4)));
                assetMaster.setTagID(cursor.getString(cursor.getColumnIndex(DB_ASSET_TAG_ID)));
                assetMaster.setSerialNo(cursor.getString(cursor.getColumnIndex(DB_ASSET_SR_NO)));
                assetMaster.setUserID(cursor.getString(cursor.getColumnIndex(DB_ASSET_USER_ID)));
                assetMaster.setTransactionDateTime(cursor.getString(cursor.getColumnIndex(DB_ASSET_TRANSACTION_DATE_TIME)));

                // Adding unloadCylinderModals to list
                barcodeList.add(assetMaster);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return barcodeList;
    }


    //GET ASSET DETAILS BY TAGID
    public AssetMaster getAssetDetailsByTagID(String tag_id)   {
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            cursor = db.query(TABLE_ASSET_MASTER, new String[]{DB_ASSET_CID, DB_ASSET_REF_ID, DB_ASSET_NAME, DB_ASSET_DESCRIPTION, DB_ASSET_CATAGORY_ID, DB_ASSET_CATAGORY_NAME, DB_ASSET_DATAFIELD_1,DB_ASSET_DATAFIELD_2,DB_ASSET_DATAFIELD_3,DB_ASSET_DATAFIELD_4,DB_ASSET_TAG_ID,DB_ASSET_SR_NO,DB_ASSET_USER_ID,DB_ASSET_TRANSACTION_DATE_TIME}, DB_ASSET_TAG_ID + "=?",
                    new String[]{tag_id}, null, null, null);

            if (cursor == null || cursor.getCount() == 0) {
                return null;
            } else {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    AssetMaster assetMaster = new AssetMaster();

                    assetMaster.setCID(cursor.getString(cursor.getColumnIndex(DB_ASSET_CID)));
                    assetMaster.setRefID(cursor.getString(cursor.getColumnIndex(DB_ASSET_REF_ID)));
                    assetMaster.setName(cursor.getString(cursor.getColumnIndex(DB_ASSET_NAME)));
                    assetMaster.setDescription(cursor.getString(cursor.getColumnIndex(DB_ASSET_DESCRIPTION)));
                    assetMaster.setCategoryID(cursor.getString(cursor.getColumnIndex(DB_ASSET_CATAGORY_ID)));
                    assetMaster.setCategoryName(cursor.getString(cursor.getColumnIndex(DB_ASSET_CATAGORY_NAME)));
                    assetMaster.setDataField1(cursor.getString(cursor.getColumnIndex(DB_ASSET_DATAFIELD_1)));
                    assetMaster.setDataField2(cursor.getString(cursor.getColumnIndex(DB_ASSET_DATAFIELD_2)));
                    assetMaster.setDataField3(cursor.getString(cursor.getColumnIndex(DB_ASSET_DATAFIELD_3)));
                    assetMaster.setDataField4(cursor.getString(cursor.getColumnIndex(DB_ASSET_DATAFIELD_4)));
                    assetMaster.setTagID(cursor.getString(cursor.getColumnIndex(DB_ASSET_TAG_ID)));
                    assetMaster.setSerialNo(cursor.getString(cursor.getColumnIndex(DB_ASSET_SR_NO)));
                    assetMaster.setUserID(cursor.getString(cursor.getColumnIndex(DB_ASSET_USER_ID)));
                    assetMaster.setTransactionDateTime(cursor.getString(cursor.getColumnIndex(DB_ASSET_TRANSACTION_DATE_TIME)));

                    return assetMaster;
                } else {
                    return null;
                }
            }
        }finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null){
                cursor.close();
            }

        }
    }



}
