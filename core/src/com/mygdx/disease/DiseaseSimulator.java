package com.mygdx.disease;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;

public class DiseaseSimulator extends ApplicationAdapter {
	public static World w;
	@Override
	public void create () {
		TextureManager.loadAll();
		w = new World();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		w.act();
		w.draw();
		System.out.println(w.humanList.size());
	}
	
	@Override
	public void dispose () {
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		w.resize(width,height);
	}
}
