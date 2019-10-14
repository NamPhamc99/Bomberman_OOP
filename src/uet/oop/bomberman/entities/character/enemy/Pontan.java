package uet.oop.bomberman.entities.character.enemy;


import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.character.enemy.ai.AILow;
import uet.oop.bomberman.entities.character.enemy.ai.AIMedium;
import uet.oop.bomberman.graphics.Sprite;

public class Pontan extends Enemy {

    public Pontan(int x, int y, Board board) {
        super(x, y, board, Sprite.pontan_dead_side1, Game.getBomberSpeed(), 200);
        _sprite = Sprite.pontan_left1_side1;

        _ai = new AIMedium(_board.getBomber(), this);
        _direction  = _ai.calculateDirection();
    }

    @Override
    protected void chooseSprite() {
        switch(_direction) {
            case 0:
            case 1:
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.pontan_right1_side1, Sprite.pontan_right2_side1, Sprite.pontan_right3_side1, _animate, 60);
                    _sprite = Sprite.movingSprite(Sprite.pontan_right1_side2, Sprite.pontan_right2_side2, Sprite.pontan_right3_side2, _animate, 60);
                } else {
                    _sprite = Sprite.movingSprite(Sprite.pontan_right1_side1, Sprite.pontan_right2_side1, Sprite.pontan_right3_side1, _animate, 60);
                    _sprite = Sprite.movingSprite(Sprite.pontan_right1_side2, Sprite.pontan_right2_side2, Sprite.pontan_right3_side2, _animate, 60);
                }
                break;
            case 2:
            case 3:
                if (_moving){
                    _sprite = Sprite.movingSprite(Sprite.pontan_left1_side1, Sprite.pontan_left2_side1, Sprite.pontan_left3_side1, _animate, 60);
                    _sprite = Sprite.movingSprite(Sprite.pontan_left1_side2, Sprite.pontan_left2_side2, Sprite.pontan_left3_side2, _animate, 60);
                }else{
                    _sprite = Sprite.movingSprite(Sprite.pontan_left1_side1, Sprite.pontan_left2_side1, Sprite.pontan_left3_side1, _animate, 60);
                    _sprite = Sprite.movingSprite(Sprite.pontan_left1_side2, Sprite.pontan_left2_side2, Sprite.pontan_left3_side2, _animate, 60);
                }
                break;
        }
    }
}
