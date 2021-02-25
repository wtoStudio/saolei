package personal.wt.saolei;

import java.util.Objects;

public class CellInfo {
    public boolean isBoom;
    public boolean isOpen;
    public boolean isMarked;
    public int value;
    public int row;
    public int col;

    public CellInfo(){ }

    public CellInfo(boolean isBoom){
        this.isBoom = isBoom;
    }

    public CellInfo(boolean isBoom, boolean isOpen){
        this.isBoom = isBoom;
        this.isOpen = isOpen;
    }

    public CellInfo(boolean isBoom, boolean isOpen, int row, int col){
        this(isBoom, isOpen);
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellInfo cellInfo = (CellInfo) o;
        return row == cellInfo.row &&
                col == cellInfo.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
