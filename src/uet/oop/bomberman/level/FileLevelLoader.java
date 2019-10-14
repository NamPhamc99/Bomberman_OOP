package uet.oop.bomberman.level;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.PlaySound;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.enemy.*;
import uet.oop.bomberman.entities.tile.Grass;
import uet.oop.bomberman.entities.tile.Portal;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.entities.tile.item.*;
import uet.oop.bomberman.exceptions.LoadLevelException;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.gui.Frame;

import java.io.*;
import java.util.*;

public class FileLevelLoader extends LevelLoader {

	/**
	 * Ma trận chứa thông tin bản đồ, mỗi phần tử lưu giá trị kí tự đọc được
	 * từ ma trận bản đồ trong tệp cấu hình
	 */
	private static char[][] _map = new char[0][0];

	
	public FileLevelLoader(Board board, int level) throws LoadLevelException {
		super(board, level);
	}

    @Override
    public void loadLevel(int level) {
        _level = level;
        // TODO: đọc dữ liệu từ tệp cấu hình /levels/Level{level}.txt
        // TODO: cập nhật các giá trị đọc được vào _width, _height, _level, _map
        ClassLoader classLoader = getClass().getClassLoader();
        File input_map = new File(classLoader.getResource("levels/Level" + _level + ".txt").getFile());
        try (Scanner sc = new Scanner(input_map)) {
            _level = sc.nextInt();
            _height = sc.nextInt();
            _width = sc.nextInt();
            _map = new char[_height][_width];
            sc.nextLine();
            while (sc.hasNext()) {
                for (int row = 0; row < _height; row++) {
                    String s = sc.nextLine();
                    for (int col = 0; col < _width; col++) {
                        _map[row][col] = s.charAt(col);
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createEntities() {
        // TODO: tạo các Entity của màn chơi
        // TODO: sau khi tạo xong, gọi _board.addEntity() để thêm Entity vào game

        // TODO: phần code mẫu ở dưới để hướng dẫn cách thêm các loại Entity vào game
        // TODO: hãy xóa nó khi hoàn thành chức năng load màn chơi từ tệp cấu hình
        for (int y=0; y < getHeight(); y++) {
            for (int x=0; x < getWidth(); x++) {
                int pos = x + y * getWidth();
                /////////////////////Tiles////////////////////////
                //Wall
                if (_map[y][x] == '#') {
                    _board.addEntity(pos, new Wall(x, y, Sprite.wall));
                }
                //Grass
                else if (_map[y][x] == ' ') {
                    _board.addEntity(pos, new Grass(x, y, Sprite.grass));
                }
                //Brick
                else if(_map[y][x] == '*'){
		            _board.addEntity(pos, new LayeredEntity(x, y,
                                          new Grass(x, y, Sprite.grass),
                                          new Brick(x, y, Sprite.brick)));
                }
                //Portal
                else if(_map[y][x] == 'x'){
                    _board.addEntity(pos, new LayeredEntity(x, y,
                            new Grass(x ,y, Sprite.grass),
                            new Portal(x ,y, Sprite.portal, _board),
                            new Brick(x ,y, Sprite.brick)));
                }

                ///////////////Items/////////////////////////
                //Speedup
                else if(_map[y][x] == 's'){
                    _board.addEntity(pos, new LayeredEntity(x, y,
                            new Grass(x ,y, Sprite.grass),
                            new SpeedItem(x, y, Sprite.powerup_speed),
                            new Brick(x, y, Sprite.brick)));
                }
                //Flameup
                else if(_map[y][x] == 'f'){
                    _board.addEntity(pos, new LayeredEntity(x, y,
                            new Grass(x ,y, Sprite.grass),
                            new FlameItem(x, y, Sprite.powerup_flames),
                            new Brick(x, y, Sprite.brick)));
                }
                //Bombup
                else if(_map[y][x] == 'b'){
                    _board.addEntity(pos, new LayeredEntity(x, y,
                            new Grass(x ,y, Sprite.grass),
                            new BombItem(x, y, Sprite.powerup_bombs),
                            new Brick(x, y, Sprite.brick)));
                }
                //BombPass
                else if(_map[y][x] == 'B'){
                    _board.addEntity(pos, new LayeredEntity(x, y,
                            new Grass(x ,y, Sprite.grass),
                            new BombPass(x, y, Sprite.powerup_bombpass),
                            new Brick(x, y, Sprite.brick)));
                }
                //Detonator
                else if(_map[y][x] == 'd'){
                    _board.addEntity(pos, new LayeredEntity(x, y,
                            new Grass(x ,y, Sprite.grass),
                            new Detonator(x, y, Sprite.powerup_detonator),
                            new Brick(x, y, Sprite.brick)));
                }
                //FlamePass
                else if(_map[y][x] == 'F'){
                    _board.addEntity(pos, new LayeredEntity(x, y,
                            new Grass(x ,y, Sprite.grass),
                            new FlamePass(x, y, Sprite.powerup_flamepass),
                            new Brick(x, y, Sprite.brick)));
                }
                //Invisibility
                else if(_map[y][x] == 'I'){
                    _board.addEntity(pos, new LayeredEntity(x, y,
                            new Grass(x ,y, Sprite.grass),
                            new InvincibilityItem(x, y, Sprite.powerup_invisibility),
                            new Brick(x, y, Sprite.brick)));
                }
                ///////////////Player/////////////////
                else if(_map[y][x] == 'p'){
                    _board.addCharacter( new Bomber(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board) );
		            Screen.setOffset(0, 0);
		            _board.addEntity(pos, new Grass(x, y, Sprite.grass));
                }
                /////////////Enemies////////////////
                //Balloon
                else if(_map[y][x] == '1'){
                    _board.addCharacter( new Balloon(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
		            _board.addEntity(pos, new Grass(x, y, Sprite.grass));
                }
                //Oneal
                else if(_map[y][x] == '2'){
                    _board.addCharacter( new Oneal(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
                    _board.addEntity(pos, new Grass(x, y, Sprite.grass));
                }
                //Doll
                else if(_map[y][x] == '3'){
                    _board.addCharacter( new Doll(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
                    _board.addEntity(pos, new Grass(x, y, Sprite.grass));
                }
                //Kondoria
                else if(_map[y][x] == '4'){
                    _board.addCharacter( new Kondoria(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
                    _board.addEntity(pos, new Grass(x, y, Sprite.grass));
                }
                //Minvo
                else if(_map[y][x] == '5'){
                    _board.addCharacter( new Minvo(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
                    _board.addEntity(pos, new Grass(x, y, Sprite.grass));
                }
                //Pass
                else if(_map[y][x] == '7'){
                    _board.addCharacter( new Pass(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
                    _board.addEntity(pos, new Grass(x, y, Sprite.grass));
                }
            }
        }
    }

}
