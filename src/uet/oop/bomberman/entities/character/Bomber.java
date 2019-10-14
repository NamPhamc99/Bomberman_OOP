

package uet.oop.bomberman.entities.character;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.PlaySound;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.entities.bomb.RemoteBomb;
import uet.oop.bomberman.entities.character.enemy.Balloon;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.tile.Grass;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.entities.tile.destroyable.DestroyableTile;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.level.Coordinates;

import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class Bomber extends Character {

    private List<Bomb> _bombs;
    private Queue<RemoteBomb> _remotebombs;
    //Đếm ngược cho các item
    private int _countdown_invincible = 500;
    private int _countdown_flamepass = 500;
    private int _countdown_bombpass = 500;

    protected Keyboard _input;

    public boolean bompass_absorb = false;
    //TODO: Xét trong cả Bomber và FlameSegment
    public boolean flamepass_absorb = false;
    //TODO: Xét trong cả Bomber và Enemy
    public boolean immuneToEnemy = false;
    public boolean detonator_absorb = false;
    /**
     * nếu giá trị này < 0 thì cho phép đặt đối tượng Bomb tiếp theo,
     * cứ mỗi lần đặt 1 Bomb mới, giá trị này sẽ được reset về 0 và giảm dần trong mỗi lần update()
     */
    protected int _timeBetweenPutBombs = 0;

    public Bomber(int x, int y, Board board) {
        super(x, y, board);
        _bombs = _board.getBombs();
        _remotebombs = _board.getRemoteBombs();
        _input = _board.getInput();
        _sprite = Sprite.player_right;
        sound_effect = new PlaySound();
    }

    @Override
    public void update() {
        clearBombs();
        clearRemoteBombs();
        if (!_alive) {
            afterKill();
            return;
        }

        //Count down items

        //Invincibility
        if (immuneToEnemy) {
            _countdown_invincible--;
            //System.out.println("Counting down invincibility " + _countdown_invincible);
        }

        if (_countdown_invincible <= 0) {
            //System.out.println("Reset invincibility");
            _countdown_invincible = 500;
            immuneToEnemy = false;
        }

        //FlamePass
        if(flamepass_absorb) {
            _countdown_flamepass--;
            //System.out.println("Counting down flame pass " + _countdown_flamepass);
        }

        if (_countdown_flamepass <= 0) {
            //System.out.println("Reset flame pass");
            _countdown_flamepass = 500;
            flamepass_absorb = false;
        }

        //BombPass
        if (bompass_absorb) {
            _countdown_bombpass--;
            //System.out.println("Counting down bomb pass " + _countdown_bombpass);
        }

        if (_countdown_bombpass <= 0) {
            //System.out.println("Reset bomb pass");
            _countdown_bombpass = 500;
            bompass_absorb = false;
        }


        if (_timeBetweenPutBombs < -7500) _timeBetweenPutBombs = 0;
        else _timeBetweenPutBombs--;

        animate();

        calculateMove();

        detectPlaceBomb();

        detonate();
    }
    private void detonate(){
        if(!_remotebombs.isEmpty() && _input.detonate){
            RemoteBomb rb = _remotebombs.remove();
            rb.explode();
            rb.updateFlames();
            _remotebombs.add(rb);
        }
    }
    @Override
    public void render(Screen screen) {
        calculateXOffset();

        if (_alive)
            chooseSprite();
        else
            _sprite = Sprite.player_dead1;

        screen.renderEntity((int) _x, (int) _y - _sprite.SIZE, this);
    }

    protected void calculateXOffset() {
        int xScroll = Screen.calculateXOffset(_board, this);
        Screen.setOffset(xScroll, 0);
    }

    /**
     * Kiểm tra xem có đặt được bom hay không? nếu có thì đặt bom tại vị trí hiện tại của Bomber
     */


    private void detectPlaceBomb() {
        // TODO: kiểm tra xem phím điều khiển đặt bom có được gõ và giá trị _timeBetweenPutBombs, Game.getBombRate() có thỏa mãn hay không
        // TODO:  Game.getBombRate() sẽ trả về số lượng bom có thể đặt liên tiếp tại thời điểm hiện tại
        // TODO: _timeBetweenPutBombs dùng để ngăn chặn Bomber đặt 2 Bomb cùng tại 1 vị trí trong 1 khoảng thời gian quá ngắn
        // TODO: nếu 3 điều kiện trên thỏa mãn thì thực hiện đặt bom bằng placeBomb()
        // TODO: sau khi đặt, nhớ giảm số lượng Bomb Rate và reset _timeBetweenPutBombs về 0
        if(detonator_absorb == true && _input.space && Game.getBombRate() > 0 && _timeBetweenPutBombs < 0){
            int Rbomb_X_coordinate = Coordinates.pixelToTile(this._x + Game.TILES_SIZE/2); //tránh lệch bom
            int Rbomb_Y_coordinate = Coordinates.pixelToTile(this._y - Game.TILES_SIZE/2); //tránh lệch bom
            placeRemoteBombs(Rbomb_X_coordinate, Rbomb_Y_coordinate);
            Game.addBombRate(-1);
            _timeBetweenPutBombs = 30;
        }
        if(detonator_absorb == false &&_input.space && Game.getBombRate() > 0 && _timeBetweenPutBombs < 0){
            int bomb_X_coordinate = Coordinates.pixelToTile(this._x + Game.TILES_SIZE/2); //tránh lệch bom
            int bomb_Y_coordinate = Coordinates.pixelToTile(this._y - Game.TILES_SIZE/2); //tránh lệch bom
            placeBomb(bomb_X_coordinate, bomb_Y_coordinate);
            Game.addBombRate(-1);
            _timeBetweenPutBombs = 30;
        }

    }


    protected void placeRemoteBombs(int x, int y){
        // TODO: thực hiện tạo đối tượng bom, đặt vào vị trí (x, y)
        this.sound_effect.playSound(".\\res\\SoundTrack\\Bomber\\PlantBomb.wav",false);
        _board.addRemoteBombs(new RemoteBomb(x, y ,_board));
    }
    protected void placeBomb(int x, int y) {
        // TODO: thực hiện tạo đối tượng bom, đặt vào vị trí (x, y)
        this.sound_effect.playSound(".\\res\\SoundTrack\\Bomber\\PlantBomb.wav",false);
        Entity e = _board.getEntity(x, y, this);
        if(e instanceof Brick){
            return;
        }
        _board.addBomb(new Bomb(x, y, _board));
    }

    private void clearBombs() {
        Iterator<Bomb> bs = _bombs.iterator();

        Bomb b;
        while (bs.hasNext()) {
            b = bs.next();
            if (b.isRemoved()) {
                bs.remove();
                Game.addBombRate(1);
            }
        }
    }
    private void clearRemoteBombs() {
        Iterator<RemoteBomb> bs = _remotebombs.iterator();

        RemoteBomb rb;
        while (bs.hasNext()) {
            rb = bs.next();
            if (rb.isRemoved()) {
                bs.remove();
                Game.addBombRate(1);
            }
        }
    }


    @Override
    public void kill() {
        this.sound_effect.playSound(".\\res\\SoundTrack\\Bomber\\Dead.wav",false);
        if (!_alive) return;
        _alive = false;
    }

    @Override
    protected void afterKill() {
        if (_timeAfter > 0) --_timeAfter;
        else {
            _board.endGame();
        }
    }

    @Override
    protected void calculateMove() {
        // TODO: xử lý nhận tín hiệu điều khiển hướng đi từ _input và gọi move() để thực hiện di chuyển
        // TODO: nhớ cập nhật lại giá trị cờ _moving khi thay đổi trạng thái di chuyển
        int x_steps = 0;
        int y_steps = 0;
        if(_input.up) y_steps--;
        if(_input.down) y_steps++;
        if(_input.right) x_steps++;
        if(_input.left) x_steps--;

        if(x_steps!=0 || y_steps!=0){
            move(x_steps * Game.getBomberSpeed(), y_steps * Game.getBomberSpeed());
            _moving = true;
        }
        else{
            _moving = false;
        }
    }

    @Override
    public boolean canMove(double x_steps, double y_steps) {
        // TODO: kiểm tra có đối tượng tại vị trí chuẩn bị di chuyển đến và có thể di chuyển tới đó hay không
        for (int c = 0; c < 4; c++) { //colision detection for each corner of the player
            double xt = ((this._x + x_steps) + c % 2 * 11) / Game.TILES_SIZE; //divide with tiles size to pass to tile coordinate
            double yt = ((this._y + y_steps) + c / 2 * 12 - 13) / Game.TILES_SIZE; //these values are the best from multiple tests
            Entity a = _board.getEntity(xt, yt, this);

            if (!a.collide(this))
                return false;
            //Invincibility
            if (immuneToEnemy == true && !a.collide(this)) {
                return true;
            }
            //Flame Pass (thực sự các kiểm tra va chạm lửa nằm trong collide, cái này chỉ để ko bị kẹt trong tia lửa)
            if (flamepass_absorb == true && !a.collide(this)) {
                return true;
            }
        }
        return true;
    }

    @Override
    public void move(double x_steps, double y_steps) {
        // TODO: sử dụng canMove() để kiểm tra xem có thể di chuyển tới điểm đã tính toán hay không và thực hiện thay đổi tọa độ _x, _y
        // TODO: nhớ cập nhật giá trị _direction sau khi di chuyển
        if(canMove(0, y_steps)){
            _y += y_steps;
        }
        if(canMove(x_steps, 0)){
            _x += x_steps;
        }

        if(x_steps > 0) _direction = 1;
        if(x_steps < 0) _direction = 3;
        if(y_steps > 0) _direction = 2;
        if(y_steps < 0) _direction = 0;
    }

    @Override
    public boolean collide(Entity e) {
        // TODO: xử lý va chạm với Flame
        // TODO: xử lý va chạm với Enemy
        if(e instanceof Flame && flamepass_absorb == false){
            kill();
            return false;
        }
        //Flame Pass
        if(e instanceof Flame && flamepass_absorb == true){
            return false;
        }
        if(immuneToEnemy == false && e instanceof Enemy){
            kill();
            return false;
        }
        //Immune to Enemy
        else if(immuneToEnemy == true && e instanceof Enemy){
            return false;
        }
        return true;
    }

    private void chooseSprite() {
        switch (_direction) {
            case 0:
                _sprite = Sprite.player_up;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_up_1, Sprite.player_up_2, _animate, 20);
                }
                break;
            case 1:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
            case 2:
                _sprite = Sprite.player_down;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_down_1, Sprite.player_down_2, _animate, 20);
                }
                break;
            case 3:
                _sprite = Sprite.player_left;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_left_1, Sprite.player_left_2, _animate, 20);
                }
                break;
            default:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
        }
    }
}