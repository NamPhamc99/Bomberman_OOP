package uet.oop.bomberman.entities.tile;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.PlaySound;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.level.FileLevelLoader;

public class Portal extends Tile {

	protected Board _board;

	public Portal(int x, int y, Sprite sprite,Board board) {
		super(x, y, sprite);
		_board = board;
		sound_effect = new PlaySound();
	}

	@Override
	public boolean collide(Entity e) {
		// TODO: xử lý khi Bomber đi vào
		if (e instanceof Bomber) {
			if (_board.detectNoEnemies()) {
			    sound_effect.playSound(".\\res\\SoundTrack\\Bomber\\LevelUp.wav",false);
				_board.nextLevel();

			}
		}
		return false;
	}

}
