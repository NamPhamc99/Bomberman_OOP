package uet.oop.bomberman.entities.bomb;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.PlaySound;
import uet.oop.bomberman.entities.AnimatedEntitiy;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.level.Coordinates;

public class RemoteBomb extends AnimatedEntitiy {

    protected Board _board;
    protected Flame[] _flames;
    public boolean _exploded = false;
    public boolean _allowedToPassThru = true;
    public int _timeAfter = 20;

    public RemoteBomb(int x, int y, Board board) {
        _x = x;
        _y = y;
        _board = board;
        _sprite = Sprite.bomb;
        sound_effect = new PlaySound();
    }

    @Override
    public void update() {
        if (_exploded){
            if (_timeAfter > 0)
                _timeAfter--;
            else
                remove();
        }
        animate();
    }

    @Override
    public void render(Screen screen) {
        if (_exploded) {
            _sprite = Sprite.bomb_exploded2;
            renderFlames(screen);
        } else
            _sprite = Sprite.bomb;

        int xt = (int)_x << 4;
        int yt = (int)_y << 4;

        screen.renderEntity(xt, yt , this);
    }

    public void renderFlames(Screen screen) {
        for (int i = 0; i < _flames.length; i++) {
            _flames[i].render(screen);
        }
    }

    public void updateFlames() {
        for (int i = 0; i < _flames.length; i++) {
            _flames[i].update();
        }
    }

    /**
     * Xử lý Bomb nổ
     */
    public void explode() {
        _exploded = true;
        sound_effect.playSound(".\\res\\SoundTrack\\Bomb\\Explosion.wav", false);
        // TODO: xử lý khi Character đứng tại vị trí Bomb
        Character ch = _board.getCharacterAtExcluding((int) this._x, (int) this._y, null);
        if (ch != null) {
            ch.kill();
        }
        // TODO: tạo các Flame
        _flames = new Flame[4];
        for (int i = 0; i < _flames.length; i++) {
            _flames[i] = new Flame((int) _x, (int) _y, i, Game.getBombRadius(), _board);
        }
    }

    public FlameSegment flameAt(int x, int y) {
        if(!_exploded) return null;

        for (int i = 0; i < _flames.length; i++) {
            if(_flames[i] == null) return null;
            FlameSegment e = _flames[i].flameSegmentAt(x, y);
            if(e != null) return e;
        }

        return null;
    }

    @Override
    public boolean collide(Entity e) {
        // TODO: xử lý khi Bomber đi ra sau khi vừa đặt bom (_allowedToPassThru)
        // TODO: xử lý va chạm với Flame của Bomb khác
        // TODO: xử lí BombPass powerup
        if(e instanceof Bomber) {
            if(((Bomber) e).bompass_absorb){
                _allowedToPassThru = true;
            }
            else {
                double diffX = e.getX() - Coordinates.tileToPixel(getX());
                double diffY = e.getY() - Coordinates.tileToPixel(getY());

                if (!(diffX >= -10 && diffX < 16 && diffY >= 1 && diffY <= 28)) { // differences to see if the player has moved out of the bomb, tested values
                    _allowedToPassThru = false;
                }
            }

            return _allowedToPassThru;
        }
//        if(e instanceof Flame){
//            _exploded = true;
//            return true;
//        }
        return false;
    }
}
