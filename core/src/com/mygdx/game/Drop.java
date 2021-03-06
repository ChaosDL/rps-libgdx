package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Drop extends Game {
	
	private SpriteBatch batch;
	private BitmapFont font;
	@Override
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont();
		this.setScreen(new MainMenuScreen(this));
	}
	public SpriteBatch getBatch(){
		return batch;
	}
	public BitmapFont getFont(){
		return font;
	}
	public void dispose(){
		super.dispose();
		screen.dispose();
		batch.dispose();
		font.dispose();
	}

}
