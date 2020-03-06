package com.example.db;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MyDbHelper myHelper;
    SQLiteDatabase db;
    EditText edt_name;
    EditText edt_email;
    EditText edt_mobile;
    TextView show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myHelper=new MyDbHelper(this);
        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edt_mobile = findViewById(R.id.edt_mobile);
        show=findViewById(R.id.records);
    }
    public List getUser() {
        try{
            List userlist = new ArrayList();
            String name = edt_name.getText().toString();
            String email = edt_email.getText().toString();
            String email_reg = "\\w+@\\w+(\\.\\w+)+";
            String phone = edt_mobile.getText().toString();
            if (name.equals("") || email.equals("") || phone.equals("")) {
                Toast.makeText(MainActivity.this, "Please fill the blanks first", Toast.LENGTH_SHORT).show();
                return null;
            } else if (!email.matches(email_reg)) {
                Log.d("email","email not right");
                Toast.makeText(MainActivity.this, "Please enter right email", Toast.LENGTH_SHORT).show();
                return null;
            }else {
                userlist.add(name);
                userlist.add(email);
                userlist.add(phone);
                Log.d("userlist",userlist.toString());
                return userlist;
            }
        }catch (Exception e){
            Log.d("error",e.getMessage());
            return null;
        }
    }

    public void onWrite(View v){
        List userlist=getUser();
        if(userlist!=null){
            try{
                db=myHelper.getWritableDatabase();
                Cursor cursor=db.query(myHelper.table,new String[]{"phone"},"phone=?",new String[]{userlist.get(2).toString()},null,null,null);
                if(cursor.getCount()!=0){
                    Toast.makeText(MainActivity.this,"Already existed!",Toast.LENGTH_SHORT).show();
                }else{
                    ContentValues values=new ContentValues();
                    values.put("name",userlist.get(0).toString());
                    values.put("email",userlist.get(1).toString());
                    values.put("phone",userlist.get(2).toString());
                    db.insert(myHelper.table,null,values);
                    Toast.makeText(MainActivity.this,"Saved!",Toast.LENGTH_SHORT).show();
                }
                db.close();
            }catch (Exception e){
                Log.d("write error",e.getMessage());
                Toast.makeText(MainActivity.this,"something wrong",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onRead(View v){
        try{
            db=myHelper.getReadableDatabase();
            Cursor cursor=db.query(myHelper.table,null,null,null,null,null,null);
            if(cursor.getCount()==0){
                show.setText(R.string.records);
                Toast.makeText(MainActivity.this,"no data",Toast.LENGTH_SHORT).show();
            }else{
                cursor.moveToFirst();
                Log.d("content ",cursor.getString(1));
                Log.d("content ",cursor.getString(1));
                show.setText("NAME : "+cursor.getString(0)+"\n"+"EMAIL : "+cursor.getString(1)+"\n"+"PHONE : "+cursor.getString(2));
                while(cursor.moveToNext()){
                    show.append("\n\nNAME : "+cursor.getString(0)+"\n"+"EMAIL : "+cursor.getString(1)+"\n"+"PHONE : "+cursor.getString(2));
                }
            }
            db.close();
        }catch (Exception e){
            Log.d("read error",e.getMessage());
            Toast.makeText(MainActivity.this,"something wrong",Toast.LENGTH_SHORT).show();
        }

    }

    public void onUpdate(View v){
        List userlist=getUser();
        if(userlist!=null){
            try{
                db=myHelper.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("name",userlist.get(0).toString());
                values.put("email",userlist.get(1).toString());
                values.put("phone",userlist.get(2).toString());
                db.update(myHelper.table,values,"phone=?",new String[]{userlist.get(2).toString()});
                Toast.makeText(MainActivity.this,"updated!",Toast.LENGTH_SHORT).show();
                db.close();
            }catch(Exception e){
                Log.d("update error",e.getMessage());
                Toast.makeText(MainActivity.this,"something wrong",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onRemove(View v){
        try{
            db=myHelper.getWritableDatabase();
            db.delete(myHelper.table,null,null);
            Toast.makeText(MainActivity.this,"removed!",Toast.LENGTH_SHORT).show();
            db.close();
        }catch(Exception e){
            Log.d("remove error",e.getMessage());
            Toast.makeText(MainActivity.this,"something wrong",Toast.LENGTH_SHORT).show();
        }
    }

}
