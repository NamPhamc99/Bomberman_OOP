package uet.oop.bomberman.entities.tile.item;

import uet.oop.bomberman.PlaySound;
import uet.oop.bomberman.entities.tile.Tile;
import uet.oop.bomberman.graphics.Sprite;

import java.util.Random;

public abstract class Item extends Tile {

    private Random random;

	public Item(int x, int y, Sprite sprite) {
	    super(x, y, sprite);
	    sound_effect = new PlaySound();
        random = new Random();
	}

	public void play_item_sound() {
        sound_effect.playSound(".\\res\\SoundTrack\\Bomber\\Item_" + (random.nextInt(5) + 1) +".wav",false);
    }
}
