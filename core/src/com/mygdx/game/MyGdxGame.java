package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class MyGdxGame extends ApplicationAdapter {
	/*SpriteBatch batch;
	Texture img;*/
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;
	
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	@Override
	public void create () {
		/*batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		*/
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		
		rainMusic.setLooping(true);
		rainMusic.play();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		bucket = new Rectangle();
		bucket.setX(800/2 - 64/2);
		bucket.setY(20);
		bucket.setSize(64);
		raindrops = new Array<Rectangle>();
		spawnRaindrop();
	}
	public void spawnRaindrop(){
		Rectangle raindrop = new Rectangle();
		raindrop.setX(MathUtils.random(0, 800-64));
		raindrop.setY(480);
		raindrop.setSize(64);
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
	@Override
	public void render () {
		/*
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
		*/
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.getX(), bucket.getY());
		for(Rectangle raindrop:raindrops){
			batch.draw(dropImage, raindrop.getX(), raindrop.getY());
		}
		batch.end();
		
		if(Gdx.input.isTouched()){
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.setX(touchPos.x - 64/2);
		}
		if(Gdx.input.isKeyPressed(Keys.LEFT)) bucket.setX(bucket.getX()-(200*Gdx.graphics.getDeltaTime()));
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) bucket.setX(bucket.getX()+(200*Gdx.graphics.getDeltaTime()));
		if(bucket.getX()<0)bucket.setX(0);
		if(bucket.getX()>800-64)bucket.setX(800-64);
		if(TimeUtils.nanoTime()-lastDropTime > 1000000000) spawnRaindrop();
		Iterator<Rectangle> iter = raindrops.iterator();
		while(iter.hasNext()){
			Rectangle raindrop = iter.next();
			raindrop.setY(raindrop.getY()-(200*Gdx.graphics.getDeltaTime()));
			if(raindrop.getY() + 64 < 0) iter.remove();
			if(raindrop.overlaps(bucket)){
				dropSound.play();
				iter.remove();
			}
		}
	}
	
	@Override
	public void dispose () {
		/*
		batch.dispose();
		img.dispose();
		*/
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}
}
