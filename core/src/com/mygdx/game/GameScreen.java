package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
	
	final Drop game;
	
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	
	private OrthographicCamera camera;
	private Rectangle bucket;
	
	private Array<Raindrop> activeRaindrops;
	private Pool<Raindrop> raindropPool = new Pool<Raindrop>(){

		@Override
		protected Raindrop newObject() {
			//return new Raindrop(MathUtils.random(0, 800-64), 480, 64);
			return new Raindrop(MathUtils.random(0, 800-64), 480, 64);
		}
		
	};
	//An alternative way of creating Pool. This requires Raindrop to be static and for it to have a blank constructor.
	//private Pool<Raindrop> raindropPool = Pools.get(Raindrop.class);
	private long lastDropTime;
	private int dropsGathered;
	
	public GameScreen(Drop game) {
		this.game = game;
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		
		rainMusic.setLooping(true);
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		bucket = new Rectangle();
		bucket.setX(800/2 - 64/2);
		bucket.setY(20);
		bucket.setSize(64);
		activeRaindrops = new Array<Raindrop>();
	}
	public class Raindrop extends Rectangle implements Poolable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Raindrop(int x, int y, int size){
			super(x, y, size, size);
		}
		public Raindrop() {
			super();
		}
		public void init(int x, int y, int size){
			setPosition(x, y);
			setSize(size);
		}
		public void update(){
			setY(y-(200*Gdx.graphics.getDeltaTime()));
		}

		@Override
		public void reset() {
			setPosition(MathUtils.random(0, 800-64), 480);
		}
	}
	public void spawnRaindrop(){
		Raindrop raindrop = raindropPool.obtain();
		//raindrop.init(MathUtils.random(0, 800-64), 480, 64);
		activeRaindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
	@Override
	public void show() {
		rainMusic.play();

	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.getBatch().setProjectionMatrix(camera.combined);
		game.getBatch().begin();
		game.getFont().draw(game.getBatch(), "Tears Collected : " + dropsGathered, 0, 480);
		game.getBatch().draw(bucketImage, bucket.getX(), bucket.getY());
		for(Rectangle raindrop:activeRaindrops){
			game.getBatch().draw(dropImage, raindrop.getX(), raindrop.getY());
		}
		game.getBatch().end();
		
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
		
		
		Iterator<Raindrop> iter = activeRaindrops.iterator();
		while(iter.hasNext()){
			Raindrop raindrop = iter.next();
			raindrop.update();
			if(raindrop.getY() + 64 < 0){
				iter.remove();
				raindropPool.free(raindrop);
			}
			if(raindrop.overlaps(bucket)){
				dropsGathered++;
				dropSound.play();
				iter.remove();
				raindropPool.free(raindrop);
			}
		}
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}

}
