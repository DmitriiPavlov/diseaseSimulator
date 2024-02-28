package com.mygdx.disease;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.HashMap;

public class TextureManager {
    public static HashMap<String, Texture> textureHashMap;
    public static Skin mSkin;

    public static Texture get(String textureName){
        return textureHashMap.get(textureName);
    }



    public static void loadAll(){
        //we initiate the hashmap
        textureHashMap = loadFrom("textures");
        mSkin = new Skin(Gdx.files.internal("glassy/skin/glassy-ui.json"));
    }

    public static HashMap<String,Texture> loadFrom(String pathToTextureFolder){
        FileHandle[] fileHandles = Gdx.files.internal(pathToTextureFolder).list();
        HashMap<String, Texture> newHash = new HashMap<>();
        for (FileHandle f : fileHandles){
            newHash.put(f.name(),new Texture(pathToTextureFolder+"/"+f.name()));
        }

        return newHash;

    }
}
