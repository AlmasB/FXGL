public class DungeonCell extends Cell {
    private int cellType;

    public DungeonCell(int x, int y) {
        super(x, y);
        cellType = 1;
    }

    public void SetType(int type){
      cellType = type;
    }

    public int GetType(){
      return cellType;
    }
}