package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.PlaySound;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.Message;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.bomb.RemoteBomb;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.enemy.ai.AI;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.item.Item;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.level.Coordinates;

import java.awt.*;

public abstract class Enemy extends Character {

	protected int _points;
	
	protected double _speed;
	protected AI _ai;

	protected final double MAX_STEPS;
	protected final double rest;
	protected double _steps;
	
	protected int _finalAnimation = 30;
	protected Sprite _deadSprite;
	
	public Enemy(int x, int y, Board board, Sprite dead, double speed, int points) {
		super(x, y, board);
		
		_points = points;
		_speed = speed;
		
		MAX_STEPS = Game.TILES_SIZE / _speed;
		rest = (MAX_STEPS - (int) MAX_STEPS) / MAX_STEPS;
		_steps = MAX_STEPS;
		
		_timeAfter = 20;
		_deadSprite = dead;
		sound_effect = new PlaySound();
	}
	
	@Override
	public void update() {
		animate();
		
		if(!_alive) {
			afterKill();
			return;
		}
		
		if(_alive)
			calculateMove();
	}
	
	@Override
	public void render(Screen screen) {
		
		if(_alive)
			chooseSprite();
		else {
			if(_timeAfter > 0) {
				_sprite = _deadSprite;
				_animate = 0;
			} else {
				_sprite = Sprite.movingSprite(Sprite.mob_dead1, Sprite.mob_dead2, Sprite.mob_dead3, _animate, 60);
			}
				
		}
			
		screen.renderEntity((int)_x, (int)_y - _sprite.SIZE, this);
	}

	@Override
	public void calculateMove() {
		// TODO: Tính toán hướng đi và di chuyển Enemy theo _ai và cập nhật giá trị cho _direction
		// TODO: sử dụng canMove() để kiểm tra xem có thể di chuyển tới điểm đã tính toán hay không
		// TODO: sử dụng move() để di chuyển
		// TODO: nhớ cập nhật lại giá trị cờ _moving khi thay đổi trạng thái di chuyển

		if (this._steps <= 0) { //Khi nào đi hết các step thì lấy move mới để đi tiếp
			this._direction = _ai.calculateDirection();
			this._steps = MAX_STEPS; //gán lại step để trừ
		}

		double change_x = 0,change_y = 0;
		if (this._direction == 0 /*xuống*/) change_y++;
		else if (this._direction == 1 /*phải*/) change_x++;
		else if (this._direction == 2 /*trái*/) change_x--;
		else if (this._direction == 3 /*lên*/) change_y--;

		if (canMove(change_x, change_y)) {
			this._steps -= (rest + 1); //trừ dần số steps còn lại
			move(change_x * this._speed , change_y * this._speed);
			this._moving = true;
		}
		else {
			this._moving = false;
			this._steps = 0;
		}
	}

	@Override
	public void move(double xa, double ya) {
		if(!_alive) return;
		_y += ya;
		_x += xa;
	}

	@Override
	public boolean canMove(double x, double y) {
		// TODO: kiểm tra có đối tượng tại vị trí chuẩn bị di chuyển đến và có thể di chuyển tới đó hay không

		//_x,_y lúc này đag là tọa độ pixel
		// pải chuyển sang tọa độ tile
		// r tính toán tọa độ điểm đi tới
		// tìm xem có collide vs entity nào ở tọa độ ms ko, có thì canMove = false

		//Vấn đề là tìm tọa độ tile tiếp theo ntn
		//Level 1: width = 13, 13 tile trên 1 hàng,
		// Nếu đag ở tile 1, đi xuống, check obstacle ở tile 14
		// 14 = x_tile + y_tile*13
		// x_tile = 16*x_pixel
		// y_tile = 16*y_pixel

		double x_pixel = this._x;
		double y_pixel = this._y - 16;

		if (_direction == 0) { // xuống thì y_tile pải bằg 1, x_tile chạy từ 0-width
			x_pixel += _sprite.getSize()/2;
			y_pixel += 1;
		}
		if (_direction == 1) { //phải
			y_pixel += _sprite.getSize()/2;
			x_pixel += 1;
		}
		if (_direction == 2) { //trái
			x_pixel += _sprite.getSize();
			y_pixel += _sprite.getSize()/2;
		}
		if (_direction == 3) { //lên
			y_pixel += _sprite.getSize();
			x_pixel += _sprite.getSize()/2;
		}

		//Chuyển sang tile
		int x_tile = Coordinates.pixelToTile(x_pixel) + (int) x;
		int y_tile = Coordinates.pixelToTile(y_pixel) + (int) y;

		//Check chướng ngại vật
		Entity obstacle = _board.getEntity(x_tile,y_tile,this);

		//Một số Enemy có thể đi xuyên Brick là Kondoria
		if((this instanceof Kondoria) && !obstacle.collide(this)){
			if(obstacle instanceof Wall) return false;
			else if(obstacle instanceof Bomb) return false;
			else if(obstacle instanceof RemoteBomb) return false;
		}

		else if((this instanceof Balloon || this instanceof Doll || this instanceof Minvo || this instanceof Pass) &&
                !obstacle.collide(this)) return false;
//
//		else if (this instanceof Doll && !obstacle.collide(this)) {
//		    if (obstacle instanceof Item) {
//		        System.out.print(1);
//		        obstacle.remove();
//		        return true;
//            }
//            return false;
//        }

		else if (this instanceof Oneal && !obstacle.collide(this)) {
		    if (obstacle instanceof Bomb) {
                //System.out.println("Eating bomb");
                ((Bomb)obstacle).remove(); // ăn bom
                return true;
		    }
            return false;
        }
		return true;

	}

	@Override
	public boolean collide(Entity e) {
		// TODO: xử lý va chạm với Flame
		// TODO: xử lý va chạm với Bomber
		if(e instanceof Flame) {
			kill();
			return false;
		}

		if(e instanceof Bomber && !((Bomber) e).immuneToEnemy) {
			((Bomber) e).kill();
			return false;
		}

		else if(e instanceof Bomber && ((Bomber) e).immuneToEnemy){
			return false;
		}
		return true;
	}


	@Override
	public void kill() {
		if(!_alive) return;
		_alive = false;

        this.sound_effect.playSound(".\\res\\SoundTrack\\Enemy\\Dead.wav",false);
		_board.addPoints(_points);

		Message msg = new Message("+" + _points, getXMessage(), getYMessage(), 2, Color.white, 14);
		_board.addMessage(msg);
	}
	
	
	@Override
	protected void afterKill() {
		if(_timeAfter > 0) --_timeAfter;
		else {
			if(_finalAnimation > 0) --_finalAnimation;
			else
				remove();
		}
	}
	
	protected abstract void chooseSprite();
}
