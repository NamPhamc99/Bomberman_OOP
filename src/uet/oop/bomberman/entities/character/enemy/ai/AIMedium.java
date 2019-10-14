package uet.oop.bomberman.entities.character.enemy.ai;

import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import java.util.*;

public class AIMedium extends AI {
    Bomber _bomber;
    Enemy _e;
    private SortedSet<Double> _neighbors;


    public AIMedium(Bomber bomber, Enemy e) {
        _bomber = bomber;
        _e = e;
        _neighbors = new TreeSet<Double>();
    }

    @Override
    public int calculateDirection() {
        double _bomberX = _bomber.getX();
        double _bomberY = _bomber.getY();
        double _enemyX = _e.getX();
        double _enemyY = _e.getY();
        //TODO: Xử lí va chạm và tính toán đường đi

        if(Math.abs(_bomberX - _enemyX) <= 60 || Math.abs(_bomberY - _enemyY) <= 60) {
            //System.out.println("In range");

            double goUP = distance(_bomberX, _bomberY, _enemyX, _enemyY-1); //3
            double goDown = distance(_bomberX, _bomberY, _enemyX, _enemyY+1); //0
            double goLeft = distance(_bomberX, _bomberY, _enemyX-1, _enemyY); //2
            double goRight = distance(_bomberX, _bomberY, _enemyX+1, _enemyY); //1

            if (!_neighbors.contains(goDown)) {
                //System.out.println("Getting new direction");
                _neighbors.clear();
                _neighbors.add(goDown);
                _neighbors.add(goLeft);
                _neighbors.add(goRight);
                _neighbors.add(goUP);
            }
            else {
                //System.out.println("Stuck! Choosing from the leftovers");
                _neighbors.remove(_neighbors.first());
            }

            if (_neighbors.first() == goUP) {
                //System.out.println("Going up");
                return 3;
            } else if (_neighbors.first() == goLeft) {
                //System.out.println("Going left");
                return 2;
            } else if (_neighbors.first() == goRight) {
                //System.out.println("Going right");
                return 1;
            } else if (_neighbors.first() == goDown) {
                //System.out.println("Going down");
                return 0;
            }
        }
        //System.out.println("Out of range");
        return this.random.nextInt(4);
    }

    //Tính khoảng cách giữa Enemy và Player
    private static double distance(double bomberX, double bomberY, double mobX, double mobY){
        return Math.sqrt((bomberX - mobX)*(bomberX - mobX) + (bomberY - mobY)*(bomberY - mobY));
    }

}