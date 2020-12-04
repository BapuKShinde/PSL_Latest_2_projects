package com.zebra.pslsdksample.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.zebra.pslsdksample.modals.CastDetail;
import com.zebra.pslsdksample.modals.DestinationLocations;
import com.zebra.pslsdksample.modals.DestinationTouchPoints;
import com.zebra.pslsdksample.modals.SourcePlant;
import com.zebra.pslsdksample.modals.TasksModal;
import com.zebra.pslsdksample.modals.TransactionTypes;
import com.zebra.pslsdksample.modals.User;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Admin on 03/Nov/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "RFIDZ.DB";

    //GET LOCATIONS
    public static final String DB_LOC_ID = "LocationID";
    public static final String DB_LOC_CODE = "LocationCode";
    public static final String DB_LOC_DESC = "LocationDesc";
    public static final String DB_LOC_TYPE = "LocationType";
    public static final String DB_LOC_IS_WEIGHBRIDGE_REQUIRED = "IsWeighbridgeRequired";
    public static final String DB_LOC_AREA = "Area";
    public static final String DB_LOC_NAME = "LocationName";
    public static final String DB_LOC_MODIFIED_DATE_TIME = "ModifiedDateTime";
    public static final String DB_LOC_IS_ACTIVE = "IsActive";

    //GET TOUCH POINTS

    public static final String DB_TP_ID = "Id";
    public static final String DB_TP_TPID = "TouchPointId";
    public static final String DB_TP_NAME = "TouchpointName";
    public static final String DB_TP_LOCATION_ID = "LocationId";
    public static final String DB_TP_IS_DESTINATION_POINT = "IsDistinationPoint";
    public static final String DB_TP_MODIFIED_DATE_TIME = "ModifiedDateTime";
    public static final String DB_TP_CREATED_DATE_TIME = "CreatedDateTime";
    public static final String DB_TP_IS_ACTIVE = "IsActive";
    //GET SOURCE PLANTS
    public static final String DB_SP_ID = "ID";
    public static final String DB_SP_MAIN_PLANT = "MainPlant";
    public static final String DB_SP_SUB_PLANT = "SubPlant";
    public static final String DB_SP_MODIFIED_DATE_TIME = "ModifiedDateTime";
    public static final String DB_SP_CREATED_DATE_TIME = "CreatedDateTime";
    public static final String DB_SP_IS_ACTIVE = "IsActive";


    //GET TRANSACTION TYPES
    public static final String DB_TRANSACTION_ID = "ID";
    public static final String DB_TRANSACTION_DESCRIPTION = "Description";
    public static final String DB_TRANSACTION_LOCATION_TYPE = "LocationType";
    public static final String DB_TRANSACTION_MODIFIED_DATE_TIME = "ModifiedDateTime";
    public static final String DB_TRANSACTION_IS_ACTIVE = "IsActive";

    //GET TASKS
    public static final String DB_TASK_ID = "ID";
    public static final String DB_TASK_NUMBER = "TaskNo";
    public static final String DB_TASK_DATE = "TaskDate";
    public static final String DB_TASK_GATE_NUMBER = "GateNo";
    public static final String DB_TASK_START_DATE = "StartDate";
    public static final String DB_TASK_END_DATE = "EndDate";
    public static final String DB_TASK_PO_STO_NUMBER = "PO_STONo";
    public static final String DB_TASK_SR_NUMBER = "SrNo";
    public static final String DB_TASK_STO_PO_NUMBER = "STOsPONo";
    public static final String DB_TASK_STO_PO_SR_NUMBER = "STOPoSrNo";
    public static final String DB_TASK_PERMIT_NUMBER = "PermitNo";
    public static final String DB_TASK_PRODUCT = "Product";
    public static final String DB_TASK_DESCRIPTION = "Description";
    public static final String DB_TASK_TRANSACTION_CODE = "TransCode";
    public static final String DB_TASK_TRANSPORTER = "Transporter";
    public static final String DB_TASK_VENDOR = "Vendor";
    public static final String DB_TASK_ROUTE_CODE = "RouteCode";
    public static final String DB_TASK_SOURCE_PLANT = "SourcePlant";
    public static final String DB_TASK_SOURCE_LOCATION = "SourceLoc";
    public static final String DB_TASK_SOURCE_BATCH = "SourceBatch";


    public static final String DB_TASK_DESTINATION_PLANT = "DestinationPlant";
    public static final String DB_TASK_DESTINATION_LOCATION = "DestinationLoc";
    public static final String DB_TASK_DESTINATION_BATCH = "DestinationBatch";

    public static final String DB_TASK_OVERLOAD = "Overload";
    public static final String DB_TASK_BARGE_MANDATORY = "BargeMandatory";
    public static final String DB_TASK_CREATED_BY = "CreatedBy";
    public static final String DB_TASK_CREATED_DATE_TIME = "CreatedDateTime";
    public static final String DB_TASK_MODIFIED_BY = "ModifiedBy";
    public static final String DB_TASK_TRANSACTION_TYPE = "TransType";
    public static final String DB_TASK_MODIFIED_DATE_TIME = "ModifiedDateTime";
    public static final String DB_TASK_IS_ACTIVE = "IsActive";


    //USER
    public static final String DB_USER_ID = "ID";
    public static final String DB_USER_USERNAME = "UserName";
    public static final String DB_USER_PASSWORD = "Password";
    public static final String DB_USER_FIRST_NAME = "FirstName";
    public static final String DB_USER_LAST_NAME = "LastName";
    public static final String DB_USER_ROLE_ID = "RoleID";
    public static final String DB_USER_MODIFIED_DATE_TIME = "ModifiedDateTime";
    public static final String DB_USER_IS_ACTIVE = "IsActive";

    public static final String DB_CAST_ID = "ID";
    public static final String DB_CAST_PREFIX = "Prefix";
    public static final String DB_CAST_NO = "CastNo";
    public static final String DB_CAST_LOCATION = "Location";
    public static final String DB_CAST_TASK_ID = "TaskID";
    public static final String DB_CAST_TASK_NO = "TaskNo";

    public static final String TABLE_TRANSACTIONS = "Transactions_Table";
    public static final String TABLE_TASKS = "Tasks_Table";
    public static final String TABLE_USER = "User_Table";
    public static final String TABLE_CAST = "Cast_Table";
    public static final String TABLE_SOURCE_PLANT = "Source_Plant_Table";
    public static final String TABLE_DESTINATION_LOCATIONS = "Destination_Locations_Table";
    public static final String TABLE_DESTINATION_TOUCH_POINTS = "Destination_Touch_Points_Table";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }
    @Override
    public void onCreate(SQLiteDatabase db) {



        String CREATE_TABLE_DESTINATION_TOUCH_POINTS = "CREATE TABLE "
                + TABLE_DESTINATION_TOUCH_POINTS
                + "("
                + DB_TP_ID + " TEXT ,"//0
                + DB_TP_TPID + " TEXT ,"//1
                + DB_TP_NAME + " TEXT ,"//2
                + DB_TP_LOCATION_ID + " TEXT ,"//3
                + DB_TP_IS_DESTINATION_POINT + " TEXT ,"//3
                + DB_TP_MODIFIED_DATE_TIME + " TEXT ,"//3
                + DB_TP_CREATED_DATE_TIME + " TEXT ,"//3
                + DB_TP_IS_ACTIVE + " TEXT"//4
                + ")";

        String CREATE_TABLE_DESTINATION_LOCATIONS = "CREATE TABLE "
                + TABLE_DESTINATION_LOCATIONS
                + "("
                + DB_LOC_ID + " TEXT ,"//0
                + DB_LOC_CODE + " TEXT ,"//1
                + DB_LOC_DESC + " TEXT ,"//2
                + DB_LOC_TYPE + " TEXT ,"//3
                + DB_LOC_IS_WEIGHBRIDGE_REQUIRED + " TEXT ,"//3
                + DB_LOC_AREA + " TEXT ,"//3
                + DB_LOC_NAME + " TEXT ,"//3
                + DB_LOC_MODIFIED_DATE_TIME + " TEXT ,"//3
                + DB_LOC_IS_ACTIVE + " TEXT"//4
                + ")";

        String CREATE_TABLE_CAST = "CREATE TABLE "
                + TABLE_CAST
                + "("
                + DB_CAST_ID + " TEXT UNIQUE,"//0
                + DB_CAST_PREFIX + " TEXT ,"//1
                + DB_CAST_NO + " TEXT ,"//2
                + DB_CAST_LOCATION + " TEXT ,"//3
                + DB_CAST_TASK_ID + " TEXT ,"//4
                + DB_CAST_TASK_NO + " TEXT"//5
                + ")";

        String CREATE_TABLE_USER = "CREATE TABLE "
                + TABLE_USER
                + "("
                + DB_USER_ID + " TEXT UNIQUE,"//0
                + DB_USER_USERNAME + " TEXT ,"//1
                + DB_USER_PASSWORD + " TEXT ,"//2
                + DB_USER_FIRST_NAME + " TEXT ,"//3
                + DB_USER_LAST_NAME + " TEXT ,"//4
                + DB_USER_ROLE_ID + " TEXT ,"//5
                + DB_USER_MODIFIED_DATE_TIME + " TEXT ,"//6
                + DB_USER_IS_ACTIVE + " TEXT"//7
                + ")";

        String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE "
                + TABLE_TRANSACTIONS
                + "("
                + DB_TRANSACTION_ID + " TEXT UNIQUE,"//0
                + DB_TRANSACTION_DESCRIPTION + " TEXT ,"//1
                + DB_TRANSACTION_LOCATION_TYPE + " TEXT ,"//2
                + DB_TRANSACTION_MODIFIED_DATE_TIME + " TEXT ,"//3
                + DB_TRANSACTION_IS_ACTIVE + " TEXT"//4
                + ")";



        String CREATE_TABLE_SOURCE_PLANTS = "CREATE TABLE "
                + TABLE_SOURCE_PLANT
                + "("
                + DB_SP_ID + " TEXT ,"//0
                + DB_SP_MAIN_PLANT + " TEXT ,"//1
                + DB_SP_SUB_PLANT + " TEXT ,"//2
                + DB_SP_MODIFIED_DATE_TIME + " TEXT ,"//3
                + DB_SP_CREATED_DATE_TIME + " TEXT ,"//3
                + DB_SP_IS_ACTIVE + " TEXT"//4
                + ")";

        String CREATE_TABLE_TASKS = "CREATE TABLE "
                + TABLE_TASKS
                + "("
                + DB_TASK_ID + " TEXT UNIQUE,"//0
                + DB_TASK_NUMBER + " TEXT ,"//1
                + DB_TASK_DATE + " TEXT ,"//2
                + DB_TASK_GATE_NUMBER + " TEXT ,"//3

                + DB_TASK_START_DATE + " TEXT ,"//4
                + DB_TASK_END_DATE + " TEXT ,"//5
                + DB_TASK_PO_STO_NUMBER + " TEXT ,"//6
                + DB_TASK_SR_NUMBER + " TEXT ,"//7

                + DB_TASK_STO_PO_NUMBER + " TEXT ,"//8
                + DB_TASK_STO_PO_SR_NUMBER + " TEXT ,"//9
                + DB_TASK_PERMIT_NUMBER + " TEXT ,"//10
                + DB_TASK_PRODUCT + " TEXT ,"//11
                + DB_TASK_DESCRIPTION + " TEXT ,"//12
                + DB_TASK_TRANSACTION_CODE + " TEXT ,"//13
                + DB_TASK_TRANSPORTER + " TEXT ,"//14
                + DB_TASK_VENDOR + " TEXT ,"//15
                + DB_TASK_ROUTE_CODE + " TEXT ,"//16
                + DB_TASK_SOURCE_PLANT + " TEXT ,"//17
                + DB_TASK_SOURCE_LOCATION + " TEXT ,"//18
                + DB_TASK_SOURCE_BATCH + " TEXT ,"//19
                + DB_TASK_DESTINATION_PLANT + " TEXT ,"//20
                + DB_TASK_DESTINATION_LOCATION + " TEXT ,"//21
                + DB_TASK_DESTINATION_BATCH + " TEXT ,"//22
                + DB_TASK_OVERLOAD + " TEXT ,"//23
                + DB_TASK_BARGE_MANDATORY + " TEXT ,"//24
                + DB_TASK_CREATED_BY + " TEXT ,"//25
                + DB_TASK_CREATED_DATE_TIME + " TEXT ,"//26
                + DB_TASK_MODIFIED_BY + " TEXT ,"//27
                + DB_TASK_TRANSACTION_TYPE + " TEXT ,"//28
                + DB_TASK_MODIFIED_DATE_TIME + " TEXT ,"//29
                + DB_TASK_IS_ACTIVE + " TEXT "//30
                + ")";


        db.execSQL(CREATE_TABLE_TASKS);
        db.execSQL(CREATE_TABLE_DESTINATION_LOCATIONS);
        db.execSQL(CREATE_TABLE_DESTINATION_TOUCH_POINTS);
        db.execSQL(CREATE_TABLE_SOURCE_PLANTS);
        db.execSQL(CREATE_TABLE_TRANSACTIONS);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_CAST);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESTINATION_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESTINATION_TOUCH_POINTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOURCE_PLANT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        // Create tables again
        onCreate(db);
    }



    public void storeDestinationTouchPointMaster(List<DestinationTouchPoints> lst) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO Destination_Touch_Points_Table (Id,TouchPointId,TouchpointName,LocationId,IsDistinationPoint,ModifiedDateTime,CreatedDateTime,IsActive) VALUES (?, ?, ? ,?, ?, ?, ?, ?)";
        //db.beginTransaction();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(sql);
        try {
            for (int i = 0; i < lst.size(); i++) {
                stmt.bindString(1, lst.get(i).getDest_id());
                stmt.bindString(2, lst.get(i).getDest_touch_point_id());
                stmt.bindString(3, lst.get(i).getDest_touch_point_name());
                stmt.bindString(4, lst.get(i).getDest_location_id());
                stmt.bindString(5, lst.get(i).getDest_is_destination_point());
                stmt.bindString(6, lst.get(i).getDest_modified_date_time());
                stmt.bindString(7, lst.get(i).getDest_created_date_time());
                stmt.bindString(8, lst.get(i).getDest_is_active());
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


    public void storeDestinationLocationMaster(List<DestinationLocations> lst) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO Destination_Locations_Table (LocationID,LocationCode,LocationDesc,LocationType,IsWeighbridgeRequired,Area,LocationName,ModifiedDateTime,IsActive) VALUES (?, ?, ?, ? ,?, ?, ?, ?, ?)";
        //db.beginTransaction();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(sql);
        try {
            for (int i = 0; i < lst.size(); i++) {
                stmt.bindString(1, lst.get(i).getLocation_id());
                stmt.bindString(2, lst.get(i).getLocation_code());
                stmt.bindString(3, lst.get(i).getLocation_desc());
                stmt.bindString(4, lst.get(i).getLocation_type());
                stmt.bindString(5, lst.get(i).getLocation_is_weighbridge_required());
                stmt.bindString(6, lst.get(i).getLocation_area());
                stmt.bindString(7, lst.get(i).getLocation_name());
                stmt.bindString(8, lst.get(i).getLocation_modified_date());
                stmt.bindString(9, lst.get(i).getLocation_is_active());
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

    public int getDestinationLoocationsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_DESTINATION_LOCATIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }


    public int getDestinationTouchPointsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_DESTINATION_TOUCH_POINTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public ArrayList<DestinationLocations> getAllDistinctDestinationLocations() {
        ArrayList<DestinationLocations> transactionTypesArrayList = new ArrayList<DestinationLocations>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT *  FROM " + TABLE_DESTINATION_LOCATIONS +" GROUP BY "+DB_LOC_NAME ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DestinationLocations transactionTypes = new DestinationLocations();
                // transactionTypes.setSp_id(cursor.getString(cursor.getColumnIndex(DB_SP_ID)));
                transactionTypes.setLocation_id(cursor.getString(cursor.getColumnIndex(DB_LOC_ID)));
                transactionTypes.setLocation_code(cursor.getString(cursor.getColumnIndex(DB_LOC_CODE)));
                transactionTypes.setLocation_desc(cursor.getString(cursor.getColumnIndex(DB_LOC_DESC)));
                transactionTypes.setLocation_type(cursor.getString(cursor.getColumnIndex(DB_LOC_TYPE)));
                transactionTypes.setLocation_is_weighbridge_required(cursor.getString(cursor.getColumnIndex(DB_LOC_IS_WEIGHBRIDGE_REQUIRED)));
                transactionTypes.setLocation_type(cursor.getString(cursor.getColumnIndex(DB_LOC_AREA)));
                transactionTypes.setLocation_name(cursor.getString(cursor.getColumnIndex(DB_LOC_NAME)));
                transactionTypes.setLocation_modified_date(cursor.getString(cursor.getColumnIndex(DB_LOC_MODIFIED_DATE_TIME)));
                transactionTypes.setLocation_is_active(cursor.getString(cursor.getColumnIndex(DB_LOC_IS_ACTIVE)));
                // Adding unloadCylinderModals to list
                transactionTypesArrayList.add(transactionTypes);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return transactionTypesArrayList;
    }

    public ArrayList<DestinationTouchPoints> getAllDistinctDestinationTouchPointsForLocation(String loc_id) {
        ArrayList<DestinationTouchPoints> transactionTypesArrayList = new ArrayList<DestinationTouchPoints>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT *  FROM " + TABLE_DESTINATION_TOUCH_POINTS +" WHERE LocationID = '"+loc_id+"'" ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DestinationTouchPoints transactionTypes = new DestinationTouchPoints();
                // transactionTypes.setSp_id(cursor.getString(cursor.getColumnIndex(DB_SP_ID)));
                transactionTypes.setDest_id(cursor.getString(cursor.getColumnIndex(DB_TP_ID)));
                transactionTypes.setDest_touch_point_id(cursor.getString(cursor.getColumnIndex(DB_TP_TPID)));
                transactionTypes.setDest_touch_point_name(cursor.getString(cursor.getColumnIndex(DB_TP_NAME)));
                transactionTypes.setDest_location_id(cursor.getString(cursor.getColumnIndex(DB_TP_LOCATION_ID)));
                transactionTypes.setDest_is_destination_point(cursor.getString(cursor.getColumnIndex(DB_TP_IS_DESTINATION_POINT)));
                transactionTypes.setDest_modified_date_time(cursor.getString(cursor.getColumnIndex(DB_TP_MODIFIED_DATE_TIME)));
                transactionTypes.setDest_created_date_time(cursor.getString(cursor.getColumnIndex(DB_TP_CREATED_DATE_TIME)));
                transactionTypes.setDest_is_active(cursor.getString(cursor.getColumnIndex(DB_TP_IS_ACTIVE)));
                // Adding unloadCylinderModals to list

                transactionTypesArrayList.add(transactionTypes);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return transactionTypesArrayList;
    }


    public void storeCastMaster(List<CastDetail> lst) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO Cast_Table (ID,Prefix,CastNo,Location,TaskID,TaskNo) VALUES (?, ?, ?, ? ,?, ?)";
        //db.beginTransaction();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(sql);
        try {
            for (int i = 0; i < lst.size(); i++) {
                stmt.bindString(1, lst.get(i).getCast_id());
                stmt.bindString(2, lst.get(i).getCast_Prefix());
                stmt.bindString(3, lst.get(i).getCast_no());
                stmt.bindString(4, lst.get(i).getCast_location());
                stmt.bindString(5, lst.get(i).getCast_task_id());
                stmt.bindString(6, lst.get(i).getCast_task_no());
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
    public void storeSourcePlantMaster(List<SourcePlant> lst) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO Source_Plant_Table (ID,MainPlant,SubPlant,ModifiedDateTime,CreatedDateTime,IsActive) VALUES (?, ?, ?, ? ,?, ?)";
        //db.beginTransaction();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(sql);
        try {
            for (int i = 0; i < lst.size(); i++) {
                stmt.bindString(1, lst.get(i).getSp_id());
                stmt.bindString(2, lst.get(i).getSp_main_plant());
                stmt.bindString(3, lst.get(i).getSp_sub_plant());
                stmt.bindString(4, lst.get(i).getSp_modified_date_time());
                stmt.bindString(5, lst.get(i).getSp_created_date_time());
                stmt.bindString(6, lst.get(i).getSp_is_active());
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

    public void storeUserMaster(List<User> lst) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO User_Table (ID,UserName,Password,FirstName,LastName,RoleID,ModifiedDateTime,IsActive) VALUES (?, ?, ?, ?, ?, ? ,?, ?)";
        //db.beginTransaction();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(sql);
        try {
            for (int i = 0; i < lst.size(); i++) {
                stmt.bindString(1, lst.get(i).getUser_id());
                stmt.bindString(2, lst.get(i).getUser_name());
                stmt.bindString(3, lst.get(i).getUser_password());
                stmt.bindString(4, lst.get(i).getUser_firstname());
                stmt.bindString(5, lst.get(i).getUser_lastname());
                stmt.bindString(6, lst.get(i).getUser_roleid());
                stmt.bindString(7, lst.get(i).getUser_modified_date());
                stmt.bindString(8, lst.get(i).getUser_is_active());
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

    public void storeTransactionMaster(List<TransactionTypes> lst) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO Transactions_Table (ID,Description,LocationType,ModifiedDateTime,IsActive) VALUES (?, ?, ? ,?, ?)";
        //db.beginTransaction();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(sql);
        try {
            for (int i = 0; i < lst.size(); i++) {
                stmt.bindString(1, lst.get(i).getTransaction_id());
                stmt.bindString(2, lst.get(i).getTransaction_description());
                stmt.bindString(3, lst.get(i).getTransaction_location_type());
                stmt.bindString(4, lst.get(i).getTransaction_modified_date());
                stmt.bindString(5, lst.get(i).getTransaction_is_active());
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

    public void storeTaskMaster(List<TasksModal> lst) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO Tasks_Table (ID,TaskNo,TaskDate,GateNo,StartDate,EndDate,PO_STONo,SrNo,STOsPONo,STOPoSrNo,PermitNo,Product,Description,TransCode,Transporter,Vendor,RouteCode,SourcePlant,SourceLoc,SourceBatch,DestinationPlant,DestinationLoc,DestinationBatch,Overload,BargeMandatory,CreatedBy,CreatedDateTime,ModifiedBy,TransType,ModifiedDateTime,IsActive) VALUES (?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        //db.beginTransaction();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(sql);
        try {
            for (int i = 0; i < lst.size(); i++) {
                stmt.bindString(1, lst.get(i).getTasks_id());
                stmt.bindString(2, lst.get(i).getTask_number());
                stmt.bindString(3, lst.get(i).getTask_date());
                stmt.bindString(4, lst.get(i).getTask_gate_number());
                stmt.bindString(5, lst.get(i).getTask_start_date());
                stmt.bindString(6, lst.get(i).getTask_end_date());
                stmt.bindString(7, lst.get(i).getTask_po_sto_number());
                stmt.bindString(8, lst.get(i).getTask_sr_number());
                stmt.bindString(9, lst.get(i).getTask_sto_po_number());
                stmt.bindString(10, lst.get(i).getTask_sto_po_sr_number());
                stmt.bindString(11, lst.get(i).getTask_permit_number());
                stmt.bindString(12, lst.get(i).getTask_product());
                stmt.bindString(13, lst.get(i).getTask_description());
                stmt.bindString(14, lst.get(i).getTask_trans_code());
                stmt.bindString(15, lst.get(i).getTask_transporter());
                stmt.bindString(16, lst.get(i).getTask_vendor());
                stmt.bindString(17, lst.get(i).getTask_route_code());
                stmt.bindString(18, lst.get(i).getTask_source_plant());
                stmt.bindString(19, lst.get(i).getTask_source_location());
                stmt.bindString(20, lst.get(i).getTask_source_batch());
                stmt.bindString(21, lst.get(i).getTask_destination_plant());
                stmt.bindString(22, lst.get(i).getTask_destination_location());
                stmt.bindString(23, lst.get(i).getTask_destination_batch());
                stmt.bindString(24, lst.get(i).getTask_overload());
                stmt.bindString(25, lst.get(i).getTask_barge_mandatory());
                stmt.bindString(26, lst.get(i).getTask_created_by());
                stmt.bindString(27, lst.get(i).getTask_created_date_time());
                stmt.bindString(28, lst.get(i).getTask_modified_by());
                stmt.bindString(29, lst.get(i).getTask_transaction_id());
                stmt.bindString(30, lst.get(i).getTask_modified_date_time());
                stmt.bindString(31, lst.get(i).getTask_is_active());
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

    public ArrayList<TransactionTypes> getAllTransactionsTypes(String inquery) {
        ArrayList<TransactionTypes> transactionTypesArrayList = new ArrayList<TransactionTypes>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  "+DB_TRANSACTION_ID + ","+DB_TRANSACTION_DESCRIPTION + ","+DB_TRANSACTION_LOCATION_TYPE + ","+ DB_TRANSACTION_MODIFIED_DATE_TIME + ","+ DB_TRANSACTION_IS_ACTIVE +" FROM " + TABLE_TRANSACTIONS +" WHERE ID IN "+inquery+" AND IsActive = 'true' ORDER BY "+DB_TRANSACTION_MODIFIED_DATE_TIME + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TransactionTypes transactionTypes = new TransactionTypes();
                transactionTypes.setTransaction_id(cursor.getString(cursor.getColumnIndex(DB_TRANSACTION_ID)));
                transactionTypes.setTransaction_description(cursor.getString(cursor.getColumnIndex(DB_TRANSACTION_DESCRIPTION)));
                transactionTypes.setTransaction_location_type(cursor.getString(cursor.getColumnIndex(DB_TRANSACTION_LOCATION_TYPE)));
                transactionTypes.setTransaction_modified_date(cursor.getString(cursor.getColumnIndex(DB_TRANSACTION_MODIFIED_DATE_TIME)));
                transactionTypes.setTransaction_is_active(cursor.getString(cursor.getColumnIndex(DB_TRANSACTION_IS_ACTIVE)));
                // Adding unloadCylinderModals to list
                transactionTypesArrayList.add(transactionTypes);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return transactionTypesArrayList;
    }


    public ArrayList<SourcePlant> getAllDistinctMainSourcePlant() {
        ArrayList<SourcePlant> transactionTypesArrayList = new ArrayList<SourcePlant>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT DISTINCT "+DB_SP_MAIN_PLANT + ","+DB_SP_CREATED_DATE_TIME + ","+ DB_SP_MODIFIED_DATE_TIME + ","+ DB_SP_IS_ACTIVE +" FROM " + TABLE_SOURCE_PLANT +" ORDER BY "+DB_SP_MAIN_PLANT + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SourcePlant transactionTypes = new SourcePlant();
               // transactionTypes.setSp_id(cursor.getString(cursor.getColumnIndex(DB_SP_ID)));
                transactionTypes.setSp_main_plant(cursor.getString(cursor.getColumnIndex(DB_SP_MAIN_PLANT)));
                transactionTypes.setSp_created_date_time(cursor.getString(cursor.getColumnIndex(DB_SP_CREATED_DATE_TIME)));
                transactionTypes.setSp_modified_date_time(cursor.getString(cursor.getColumnIndex(DB_SP_MODIFIED_DATE_TIME)));
                transactionTypes.setSp_is_active(cursor.getString(cursor.getColumnIndex(DB_SP_IS_ACTIVE)));
                // Adding unloadCylinderModals to list
                transactionTypesArrayList.add(transactionTypes);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return transactionTypesArrayList;
    }

    public int getSourcePlantCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SOURCE_PLANT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public ArrayList<SourcePlant> getAllSubPlantForMainPlant(String mainplant) {
        ArrayList<SourcePlant> transactionTypesArrayList = new ArrayList<SourcePlant>();
        SQLiteDatabase db = this.getWritableDatabase();

        // String selectQuery = "SELECT  DISTINCT "+DB_TASK_PRODUCT + ","+DB_TASK_ID +","+DB_TASK_TRANSACTION_TYPE +" FROM " + TABLE_TASKS +" WHERE "+DB_TASK_TRANSACTION_TYPE +" = '"+transaction_id+"'";
        String selectQuery = "SELECT  DISTINCT "+DB_SP_SUB_PLANT  +" FROM " + TABLE_SOURCE_PLANT +" WHERE "+DB_SP_MAIN_PLANT +" = '"+mainplant+"'"+" AND "+DB_SP_MAIN_PLANT +"!= ''";
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.e("QUERY",selectQuery);
        if (cursor.moveToFirst()) {
            do {
                SourcePlant tasksModal = new SourcePlant();
                // tasksModal.setTasks_id(cursor.getString(cursor.getColumnIndex(DB_TASK_ID)));
                tasksModal.setSp_sub_plant(cursor.getString(cursor.getColumnIndex(DB_SP_SUB_PLANT)));
               // tasksModal.setTask_transaction_id(cursor.getString(cursor.getColumnIndex(DB_TASK_TRANSACTION_TYPE)));
                // Adding unloadCylinderModals to list
                transactionTypesArrayList.add(tasksModal);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return transactionTypesArrayList;
    }


    public ArrayList<TasksModal> getAllTransactionTypesBymainplant(String transaction_id) {
        ArrayList<TasksModal> transactionTypesArrayList = new ArrayList<TasksModal>();
        SQLiteDatabase db = this.getWritableDatabase();

        // String selectQuery = "SELECT  DISTINCT "+DB_TASK_PRODUCT + ","+DB_TASK_ID +","+DB_TASK_TRANSACTION_TYPE +" FROM " + TABLE_TASKS +" WHERE "+DB_TASK_TRANSACTION_TYPE +" = '"+transaction_id+"'";
        String selectQuery = "SELECT  DISTINCT "+DB_TASK_TRANSACTION_TYPE +" FROM " + TABLE_TASKS +" WHERE "+DB_TASK_SOURCE_PLANT +" IN "+transaction_id+" AND IsActive = 'true'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.e("INTRANSTYPESQUERY",selectQuery);
        if (cursor.moveToFirst()) {
            do {
                TasksModal tasksModal = new TasksModal();
                // tasksModal.setTasks_id(cursor.getString(cursor.getColumnIndex(DB_TASK_ID)));
               // tasksModal.setTask_product(cursor.getString(cursor.getColumnIndex(DB_TASK_PRODUCT)));
                tasksModal.setTask_transaction_id(cursor.getString(cursor.getColumnIndex(DB_TASK_TRANSACTION_TYPE)));
                // Adding unloadCylinderModals to list
                transactionTypesArrayList.add(tasksModal);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return transactionTypesArrayList;
    }

    public ArrayList<CastDetail> getAllCastList() {
        ArrayList<CastDetail> transactionTypesArrayList = new ArrayList<CastDetail>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  "+DB_CAST_ID + ","+DB_CAST_PREFIX + ","+DB_CAST_NO + ","+ DB_CAST_LOCATION + ","+ DB_CAST_TASK_ID+","+ DB_CAST_TASK_NO +" FROM " + TABLE_CAST;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CastDetail transactionTypes = new CastDetail();
                transactionTypes.setCast_id(cursor.getString(cursor.getColumnIndex(DB_CAST_ID)));
                transactionTypes.setCast_Prefix(cursor.getString(cursor.getColumnIndex(DB_CAST_PREFIX)));
                transactionTypes.setCast_no(cursor.getString(cursor.getColumnIndex(DB_CAST_NO)));
                transactionTypes.setCast_location(cursor.getString(cursor.getColumnIndex(DB_CAST_LOCATION)));
                transactionTypes.setCast_task_id(cursor.getString(cursor.getColumnIndex(DB_CAST_TASK_ID)));
                transactionTypes.setCast_task_no(cursor.getString(cursor.getColumnIndex(DB_CAST_TASK_NO)));
                // Adding unloadCylinderModals to list
                transactionTypesArrayList.add(transactionTypes);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return transactionTypesArrayList;
    }

    public int getUserCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public int getTransactionsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TRANSACTIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    //select max(datetime) from tableName;
    public int getTasksCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public ArrayList<TasksModal> getAllMaterialsByTransactionTypes(String transaction_id) {
        ArrayList<TasksModal> transactionTypesArrayList = new ArrayList<TasksModal>();
        SQLiteDatabase db = this.getWritableDatabase();

       // String selectQuery = "SELECT  DISTINCT "+DB_TASK_PRODUCT + ","+DB_TASK_ID +","+DB_TASK_TRANSACTION_TYPE +" FROM " + TABLE_TASKS +" WHERE "+DB_TASK_TRANSACTION_TYPE +" = '"+transaction_id+"'";
       // String selectQuery = "SELECT  DISTINCT "+DB_TASK_PRODUCT + ","+DB_TASK_TRANSACTION_TYPE +" FROM " + TABLE_TASKS +" WHERE "+DB_TASK_TRANSACTION_TYPE +" = '"+transaction_id+"'"+" AND "+DB_TASK_PRODUCT +"!= ''"+ " AND IsActive = 'true' ORDER BY "+DB_TASK_PRODUCT;
        String selectQuery = "SELECT  DISTINCT "+DB_TASK_PRODUCT + ","+DB_TASK_DESCRIPTION + ","+DB_TASK_TRANSACTION_TYPE +" FROM " + TABLE_TASKS +" WHERE "+DB_TASK_TRANSACTION_TYPE +" = '"+transaction_id+"'"+" AND "+DB_TASK_PRODUCT +"!= ''"+ " AND IsActive = 'true' ORDER BY "+DB_TASK_PRODUCT;
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.e("QUERY",selectQuery);
        if (cursor.moveToFirst()) {
            do {
                TasksModal tasksModal = new TasksModal();
               // tasksModal.setTasks_id(cursor.getString(cursor.getColumnIndex(DB_TASK_ID)));
                tasksModal.setTask_product(cursor.getString(cursor.getColumnIndex(DB_TASK_PRODUCT)));
                tasksModal.setTask_description(cursor.getString(cursor.getColumnIndex(DB_TASK_DESCRIPTION)));
                tasksModal.setTask_transaction_id(cursor.getString(cursor.getColumnIndex(DB_TASK_TRANSACTION_TYPE)));
                // Adding unloadCylinderModals to list
                transactionTypesArrayList.add(tasksModal);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return transactionTypesArrayList;
    }

    public ArrayList<TasksModal> getAllSourceByTransactionTypesAndMaterial(String transaction_id,String material) {
        ArrayList<TasksModal> transactionTypesArrayList = new ArrayList<TasksModal>();
        SQLiteDatabase db = this.getWritableDatabase();

        //String selectQuery = "SELECT  DISTINCT "+DB_TASK_SOURCE_LOCATION + ","+DB_TASK_ID +" FROM " + TABLE_TASKS +" WHERE "+DB_TASK_TRANSACTION_TYPE +" = '"+transaction_id+"' AND "+DB_TASK_PRODUCT +" = '"+material+"'" ;
      //Change  String selectQuery = "SELECT  DISTINCT "+DB_TASK_SOURCE_LOCATION +" FROM " + TABLE_TASKS +" WHERE "+DB_TASK_TRANSACTION_TYPE +" = '"+transaction_id+"' AND "+DB_TASK_PRODUCT +" = '"+material+"' AND "+DB_TASK_SOURCE_LOCATION +" != ''  AND IsActive = 'true'" ;
        String selectQuery = "SELECT  DISTINCT "+DB_TASK_SOURCE_LOCATION +","+DB_TASK_SOURCE_PLANT+","+DB_TASK_SOURCE_BATCH+" FROM " + TABLE_TASKS +" WHERE "+DB_TASK_TRANSACTION_TYPE +" = '"+transaction_id+"' AND "+DB_TASK_PRODUCT +" = '"+material+"' AND "+DB_TASK_SOURCE_LOCATION +" != ''  AND IsActive = 'true'" ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.e("SOURCE QUERY",selectQuery);
        if (cursor.moveToFirst()) {
            do {
                TasksModal tasksModal = new TasksModal();
               // tasksModal.setTasks_id(cursor.getString(cursor.getColumnIndex(DB_TASK_ID)));
                String allinone = cursor.getString(cursor.getColumnIndex(DB_TASK_SOURCE_PLANT))+"/"+cursor.getString(cursor.getColumnIndex(DB_TASK_SOURCE_LOCATION))+"/"+cursor.getString(cursor.getColumnIndex(DB_TASK_SOURCE_BATCH));
                tasksModal.setTask_source_location(allinone);
                //tasksModal.setTask_source_location(cursor.getString(cursor.getColumnIndex(DB_TASK_SOURCE_LOCATION)));
               // tasksModal.setTask_source_plant(cursor.getString(cursor.getColumnIndex(DB_TASK_SOURCE_PLANT)));
              //  tasksModal.setTask_source_batch(cursor.getString(cursor.getColumnIndex(DB_TASK_SOURCE_BATCH)));
                // Adding unloadCylinderModals to list
                transactionTypesArrayList.add(tasksModal);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return transactionTypesArrayList;
    }

    public ArrayList<TasksModal> getAllDestinationByTransactionTypesAndMaterial(String transaction_id,String material) {
        ArrayList<TasksModal> transactionTypesArrayList = new ArrayList<TasksModal>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  "+DB_TASK_ID + ","+DB_TASK_DESTINATION_LOCATION +" FROM " + TABLE_TASKS +" WHERE "+DB_TASK_TRANSACTION_TYPE +" = '"+transaction_id+"' AND "+DB_TASK_PRODUCT +" = '"+material+"'" ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TasksModal tasksModal = new TasksModal();
                tasksModal.setTasks_id(cursor.getString(cursor.getColumnIndex(DB_TASK_ID)));
                tasksModal.setTask_destination_location(cursor.getString(cursor.getColumnIndex(DB_TASK_DESTINATION_LOCATION)));
                // Adding unloadCylinderModals to list
                transactionTypesArrayList.add(tasksModal);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return transactionTypesArrayList;
    }

    public ArrayList<TasksModal> getAllDestinationByTransactionTypesAndMaterialAndSource(String transaction_id,String material,String source) {
        ArrayList<TasksModal> transactionTypesArrayList = new ArrayList<TasksModal>();
        SQLiteDatabase db = this.getWritableDatabase();

       // String selectQuery = "SELECT  DISTINCT "+DB_TASK_DESTINATION_LOCATION + ","+DB_TASK_ID +" FROM " + TABLE_TASKS +" WHERE "+DB_TASK_TRANSACTION_TYPE +" = '"+transaction_id+"' AND "+DB_TASK_PRODUCT +" = '"+material+"' AND "+DB_TASK_SOURCE_LOCATION +" = '"+source+"'" ;
        String selectQuery = "SELECT  DISTINCT "+DB_TASK_DESTINATION_LOCATION +","+DB_TASK_DESTINATION_PLANT +","+DB_TASK_DESTINATION_BATCH + " FROM " + TABLE_TASKS +" WHERE "+DB_TASK_TRANSACTION_TYPE +" = '"+transaction_id+"' AND "+DB_TASK_PRODUCT +" = '"+material+"' AND "+DB_TASK_SOURCE_LOCATION +" = '"+source+"' AND "+DB_TASK_DESTINATION_LOCATION+" !=''  AND IsActive = 'true'" ;
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.e("DESTINATION QUERY",selectQuery);

        if (cursor.moveToFirst()) {
            do {
                TasksModal tasksModal = new TasksModal();
               // tasksModal.setTasks_id(cursor.getString(cursor.getColumnIndex(DB_TASK_ID)));
                String allinone = cursor.getString(cursor.getColumnIndex(DB_TASK_DESTINATION_PLANT))+"/"+cursor.getString(cursor.getColumnIndex(DB_TASK_DESTINATION_LOCATION))+"/"+cursor.getString(cursor.getColumnIndex(DB_TASK_DESTINATION_BATCH));
                tasksModal.setTask_destination_location(allinone);
               // tasksModal.setTask_destination_location(cursor.getString(cursor.getColumnIndex(DB_TASK_DESTINATION_LOCATION)));
                // Adding unloadCylinderModals to list
                transactionTypesArrayList.add(tasksModal);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return transactionTypesArrayList;
    }



    public String getTaskNumber(String transaction_id,String material,String source,String destination,String s_plant,String s_batch,String d_plant,String d_batch) {
        String task_number = "";
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  "+DB_TASK_NUMBER +" FROM Tasks_Table WHERE "+DB_TASK_TRANSACTION_TYPE +" = '"+transaction_id+"' AND "+DB_TASK_PRODUCT +" = '"+material+"' AND "+DB_TASK_SOURCE_LOCATION +" = '"+source+"' AND "+DB_TASK_SOURCE_PLANT +" = '"+s_plant+"' AND "+DB_TASK_SOURCE_BATCH +" = '"+s_batch+"' AND "+DB_TASK_DESTINATION_PLANT +" = '"+d_plant+"' AND "+DB_TASK_DESTINATION_BATCH +" = '"+d_batch+"' AND "+DB_TASK_DESTINATION_LOCATION +" = '"+destination+"'" ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.e("TASK- QUERY",selectQuery);

        if (cursor.moveToFirst()) {
            //10000881 - 732
            do {
                Log.e("TASK- COUNT","abc");
                TasksModal tasksModal = new TasksModal();

               task_number = cursor.getString(cursor.getColumnIndex(DB_TASK_NUMBER));
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return task_number;
    }



    public String getIDFromTaskNumber(String task_number) {
        String task_id = "";
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  "+DB_TASK_ID +" FROM Tasks_Table WHERE "+DB_TASK_NUMBER +" = '"+task_number+"'" ;
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.e("TASK_ID QUERY",selectQuery);

        if (cursor.moveToFirst()) {
            do {
                TasksModal tasksModal = new TasksModal();

                task_id = cursor.getString(cursor.getColumnIndex(DB_TASK_ID));
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return task_id;
    }

    public String getUserActive(String username,String password) {
        String task_number = "";
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  "+DB_TASK_IS_ACTIVE +" FROM User_Table WHERE "+DB_USER_USERNAME +" = '"+username+"' AND "+DB_USER_PASSWORD +" = '"+password+"' " ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TasksModal tasksModal = new TasksModal();

                task_number = cursor.getString(cursor.getColumnIndex(DB_TASK_IS_ACTIVE));
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return task_number;
    }

    public String getUserFirstName(String username,String password) {
        String task_number = "";
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  "+DB_USER_FIRST_NAME +" FROM User_Table WHERE "+DB_USER_USERNAME +" = '"+username+"' AND "+DB_USER_PASSWORD +" = '"+password+"' " ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TasksModal tasksModal = new TasksModal();

                task_number = cursor.getString(cursor.getColumnIndex(DB_USER_FIRST_NAME));
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return task_number;
    }

    public String getUserMaxDate() {
        String task_number = "";
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "select max(ModifiedDateTime) from User_Table" ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TasksModal tasksModal = new TasksModal();

                task_number = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return task_number;
    }
    public String getTasksMaxDate() {
        String task_number = "";
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "select max(ModifiedDateTime) from Tasks_Table" ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TasksModal tasksModal = new TasksModal();

                task_number = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return task_number;
    }

    public String getTreansMaxDate() {
        String task_number = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "select max(ModifiedDateTime) from Transactions_Table" ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.e("COUNT",String.valueOf(cursor.getCount()));
        if (cursor.moveToFirst()) {
            do {
                TasksModal tasksModal = new TasksModal();

                task_number = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return task_number;
    }



    public String getTransactiontypeByTransactionID(String transaction_id) {
        String task_number = "";
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  "+DB_TRANSACTION_DESCRIPTION +" FROM "+TABLE_TRANSACTIONS+" WHERE "+DB_TRANSACTION_ID +" = '"+transaction_id+"' AND IsActive = 'true'" ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                //TasksModal tasksModal = new TasksModal();

                task_number = cursor.getString(cursor.getColumnIndex(DB_TRANSACTION_DESCRIPTION));
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return task_number;
    }

    public TasksModal getTaskDetailsByTaskunumber(String tasknumber) {
        SQLiteDatabase db = this.getWritableDatabase();
       /* Cursor cursor = db.query(TABLE_TASKS, new String[]{K_HU_NUMBER, K_TRIP_NUMBER, K_DELIVERY_NUMBER, K_PACKAGE_QUANTITY, K_SENDING_SITE, K_RECEIVING_SITE, K_TOTAL_HU, K_FOUNF_HU, K_IS_FOUND}, DB_TASK_NUMBER + "=?",
                new String[]{tasknumber}, null, null, null);*/


        String selectQuery = "SELECT  * FROM Tasks_Table WHERE "+DB_TASK_NUMBER +" = '"+tasknumber+"' AND IsActive = 'true'" ;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        } else {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                TasksModal stockCountDataModel = new TasksModal();




                stockCountDataModel.setTasks_id((cursor.getString(cursor.getColumnIndex(DB_TASK_ID))));
                stockCountDataModel.setTask_number(cursor.getString(cursor.getColumnIndex(DB_TASK_NUMBER)));
                stockCountDataModel.setTask_date(cursor.getString(cursor.getColumnIndex(DB_TASK_DATE)));
                stockCountDataModel.setTask_gate_number(cursor.getString(cursor.getColumnIndex(DB_TASK_GATE_NUMBER)));
                stockCountDataModel.setTask_start_date(cursor.getString(cursor.getColumnIndex(DB_TASK_START_DATE)));
                stockCountDataModel.setTask_end_date(cursor.getString(cursor.getColumnIndex(DB_TASK_END_DATE)));
                stockCountDataModel.setTask_po_sto_number(cursor.getString(cursor.getColumnIndex(DB_TASK_PO_STO_NUMBER)));
                stockCountDataModel.setTask_sr_number(cursor.getString(cursor.getColumnIndex(DB_TASK_SR_NUMBER)));
                stockCountDataModel.setTask_sto_po_number(cursor.getString(cursor.getColumnIndex(DB_TASK_STO_PO_NUMBER)));
                stockCountDataModel.setTask_sto_po_sr_number(cursor.getString(cursor.getColumnIndex(DB_TASK_STO_PO_SR_NUMBER)));
                stockCountDataModel.setTask_permit_number(cursor.getString(cursor.getColumnIndex(DB_TASK_PERMIT_NUMBER)));
                stockCountDataModel.setTask_product(cursor.getString(cursor.getColumnIndex(DB_TASK_PRODUCT)));
                stockCountDataModel.setTask_description(cursor.getString(cursor.getColumnIndex(DB_TASK_DESCRIPTION)));
                stockCountDataModel.setTask_trans_code(cursor.getString(cursor.getColumnIndex(DB_TASK_TRANSACTION_CODE)));
                stockCountDataModel.setTask_transporter(cursor.getString(cursor.getColumnIndex(DB_TASK_TRANSPORTER)));
                stockCountDataModel.setTask_vendor(cursor.getString(cursor.getColumnIndex(DB_TASK_VENDOR)));
                stockCountDataModel.setTask_route_code(cursor.getString(cursor.getColumnIndex(DB_TASK_ROUTE_CODE)));
                stockCountDataModel.setTask_source_plant(cursor.getString(cursor.getColumnIndex(DB_TASK_SOURCE_PLANT)));
                stockCountDataModel.setTask_source_location(cursor.getString(cursor.getColumnIndex(DB_TASK_SOURCE_LOCATION)));
                stockCountDataModel.setTask_source_batch(cursor.getString(cursor.getColumnIndex(DB_TASK_SOURCE_BATCH)));
                stockCountDataModel.setTask_destination_plant(cursor.getString(cursor.getColumnIndex(DB_TASK_DESTINATION_PLANT)));
                stockCountDataModel.setTask_destination_location(cursor.getString(cursor.getColumnIndex(DB_TASK_DESTINATION_LOCATION)));
                stockCountDataModel.setTask_destination_batch(cursor.getString(cursor.getColumnIndex(DB_TASK_DESTINATION_BATCH)));
                stockCountDataModel.setTask_overload(cursor.getString(cursor.getColumnIndex(DB_TASK_OVERLOAD)));
                stockCountDataModel.setTask_barge_mandatory(cursor.getString(cursor.getColumnIndex(DB_TASK_BARGE_MANDATORY)));
                stockCountDataModel.setTask_created_by(cursor.getString(cursor.getColumnIndex(DB_TASK_CREATED_BY)));
                stockCountDataModel.setTask_created_date_time(cursor.getString(cursor.getColumnIndex(DB_TASK_CREATED_DATE_TIME)));
                stockCountDataModel.setTask_modified_by(cursor.getString(cursor.getColumnIndex(DB_TASK_MODIFIED_BY)));
                stockCountDataModel.setTask_transaction_id(cursor.getString(cursor.getColumnIndex(DB_TASK_TRANSACTION_TYPE)));
                stockCountDataModel.setTask_modified_date_time(cursor.getString(cursor.getColumnIndex(DB_TASK_MODIFIED_DATE_TIME)));
                stockCountDataModel.setTask_is_active(cursor.getString(cursor.getColumnIndex(DB_TASK_IS_ACTIVE)));

                 /*
                + DB_TASK_TRANSACTION_TYPE + " TEXT ,"  setTask_transaction_id */


                return stockCountDataModel;
            } else {
                return null;
            }
        }
    }

    public String getTaskNumberByTaskId(String transaction_id) {
        String task_number = "";
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  "+DB_TASK_NUMBER +" FROM Tasks_Table WHERE "+DB_TASK_ID +" = '"+transaction_id+"'" ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TasksModal tasksModal = new TasksModal();

                task_number = cursor.getString(cursor.getColumnIndex(DB_TASK_NUMBER));
            } while (cursor.moveToNext());
        }
        // return cylinderList list
        return task_number;
    }


    public void deleteTransactionMaster() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, null, null);
        db.close();
    }


    public void deleteTaskMaster() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, null, null);
        db.close();
    }

    public void deleteSourcePlant() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SOURCE_PLANT, null, null);
        db.close();
    }

    public void deleteCastMaster() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CAST, null, null);
        db.close();
    }

    public void deleteDestinationLocation() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DESTINATION_LOCATIONS, null, null);
        db.close();
    }

    public void deleteDestinationTouchPoint() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DESTINATION_TOUCH_POINTS, null, null);
        db.close();
    }


}

