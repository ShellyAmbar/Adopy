package com.example.adopy;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.adopy.Models.PetModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FileSystemMemory {


    private FileSystemMemory()
    {

    }
    public static void SaveToFile(List<PetModel> petModelList,Context context)
    {
        FileOutputStream out = null;
        try {
            out = context.openFileOutput("list",MODE_PRIVATE);
            ObjectOutputStream ob = new ObjectOutputStream(out);
            ob.writeObject(petModelList);
            ob.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static List<PetModel> LoadFromFile(Context context)
    {
        List<PetModel> petModelList = new ArrayList<>();
        FileInputStream fi = null;
        try {
            fi = context.openFileInput("list");
            ObjectInputStream ob = new ObjectInputStream(fi);
            petModelList = (ArrayList<PetModel>)ob.readObject();
            ob.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        return  petModelList;
    }
}
