package uet.oop.bomberman.entities.character.enemy;


import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.character.enemy.ai.AILow;
import uet.oop.bomberman.entities.character.enemy.ai.AIMedium;
import uet.oop.bomberman.graphics.Sprite;

public class Pass extends Enemy {

    public Pass(int x, int y, Board board) {
        super(x, y, board, Sprite.pass_dead, Game.getBomberSpeed(), 200);

        _sprite = Sprite.pass_left1;

        _ai = new AIMedium(_board.getBomber(), this);
        _direction  = _ai.calculateDirection();
    }

    @Override
    protected void chooseSprite() {
        switch(_direction) {
            case 0:
            case 1:
                if(_moving)
                    _sprite = Sprite.movingSprite(Sprite.pass_right1, Sprite.pass_right2, Sprite.pass_right3, _animate, 60);
                else
                    _sprite = Sprite.pass_left1;
                break;
            case 2:
            case 3:
                if(_moving)
                    _sprite = Sprite.movingSprite(Sprite.pass_left1, Sprite.pass_left2, Sprite.pass_left3, _animate, 60);
                else
                    _sprite = Sprite.pass_left1;
                break;
        }
    }
}