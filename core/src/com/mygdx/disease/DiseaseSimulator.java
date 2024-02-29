package com.mygdx.disease;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.utils.ScreenUtils;

import javax.swing.*;
import javax.tools.Tool;

public class DiseaseSimulator extends ApplicationAdapter {
	public static World w;
	public static WelcomeScreen settings;
	public static TooltipManager tooltipManager;
	public static boolean simulating = false;
	@Override
	public void create () {
		TextureManager.loadAll();
		tooltipManager = new TooltipManager();
		tooltipManager.instant();
		tooltipManager.resetTime = 0;
		tooltipManager.initialTime = 0;
		tooltipManager.subsequentTime = 0.8f;
		settings = new WelcomeScreen();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		TooltipManager tool = new TooltipManager();
		tool.instant();
		if (simulating) {
			w.act();
			w.draw();
		}
		else{
			Gdx.input.setInputProcessor(settings);
			settings.act();
			settings.draw();
		}
	}
	
	@Override
	public void dispose () {
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		if (simulating) {
			w.resize(width, height);
		}
		settings.resize(width,height);
	}

}
