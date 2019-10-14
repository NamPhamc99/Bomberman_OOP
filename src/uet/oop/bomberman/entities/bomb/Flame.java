package uet.oop.bomberman.entities.bomb;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.entities.tile.destroyable.DestroyableTile;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.level.Coordinates;

public class Flame extends Entity {

	protected Board _board;
	protected int _direction;
	private int _radius;
	protected int xOrigin, yOrigin;
	protected FlameSegment[] _flameSegments = new FlameSegment[0];

	/**
	 *
	 * @param x hoành độ bắt đầu của Flame
	 * @param y tung độ bắt đầu của Flame
	 * @param direction là hướng của Flame
	 * @param radius độ dài cực đại của Flame
	 */
	public Flame(int x, int y, int direction, int radius, Board board) {
		xOrigin = x;
		yOrigin = y;
		_x = x;
		_y = y;
		_direction = direction;
		_radius = radius;
		_board = board;
		createFlameSegments();
	}

	/**
	 * Tạo các FlameSegment, mỗi segment ứng một đơn vị độ dài
	 */
	private void createFlameSegments() {
		/**
		 * tính toán độ dài Flame, tương ứng với số lượng segment
		 */
		_flameSegments = new FlameSegment[calculatePermitedDistance()];

		/**
		 * biến last dùng để đánh dấu cho segment cuối cùng
		 */
		boolean last;

		// TODO: tạo các segment dưới đây
		for(int i=0; i<_flameSegments.length; i++){
			//Có 4 hướng flame, tuy vào hướng flame mà giảm/tăng tọa độ của flame segments
			if(_direction == 0) _y--; //VD: flame đi lên thì tọa độ _x giữ nguyên còn _y giảm dần
			if(_direction == 1) _x++;
			if(_direction == 2) _y++;
			if(_direction == 3) _x--;
			if(i<_flameSegments.length-1){
				last = false;
				_flameSegments[i] = new FlameSegment((int)this._x, (int)this._y , this._direction, last);
			}
			else{
				last = true;
				_flameSegments[i] = new FlameSegment((int)this._x, (int)this._y , this._direction, last);
			}
		}
	}

	/**
	 * Tính toán độ dài của Flame, nếu gặp vật cản là Brick/Wall, độ dài sẽ bị cắt ngắn
	 * @return
	 */
	private int calculatePermitedDistance() {
		// TODO: thực hiện tính toán độ dài của Flame
		int flame_length = 0;
		int x = (int) _x;
		int y = (int) _y;
		for(int i=0; i<this._radius; i++){
			if(_direction == 0) y--;
			if(_direction == 1) x++;
			if(_direction == 2) y++;
			if(_direction == 3) x--;

			//Các entity trên 1 đường flame
			Entity e = _board.getEntity(x, y, null);
			if(e instanceof Enemy) flame_length++;
			if(!e.collide(this)) break;
			flame_length++;
		}
		return flame_length;
	}
	
	public FlameSegment flameSegmentAt(int x, int y) {
		for (int i = 0; i < _flameSegments.length; i++) {
			if(_flameSegments[i].getX() == x && _flameSegments[i].getY() == y)
				return _flameSegments[i];
		}
		return null;
	}

	@Override
	public void update() {}
	
	@Override
	public void render(Screen screen) {
		for (int i = 0; i < _flameSegments.length; i++) {
			_flameSegments[i].render(screen);
		}
	}

	@Override
	public boolean collide(Entity e) {
		// TODO: xử lý va chạm với Bomber, Enemy. Chú ý đối tượng này có vị trí chính là vị trí của Bomb đã nổ
		if(e instanceof Wall) return true;
		//Collide với Enemy và Bomber trong FlameSegments
		return false;
	}
}
