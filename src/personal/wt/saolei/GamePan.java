package personal.wt.saolei;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class GamePan extends JPanel {

    public int width;
    public int height;

    //行数
    public int row = 20;
    //列数
    public int col = 25;

    //每个小格子的宽度
    public int cellWidth = 30;

    //布雷的数量
    public int initBoomCount = 55;

    //格子之间的缝隙距离
    public int celllspadding = 1;
    public int cellOffset = celllspadding*2;

    //布雷信息二维数组
    public CellInfo[][] coor;

    //是否已经布雷，游戏开始后，点击第一个格子之后才布雷（即点开第一个格子永远不会是雷）
    public boolean hasGenerateBoom;

    //【雷图标】
    private Image bombImage = Util.getImage("bomb_16.png");

    //【已标记】 图标
    private Image markImage = Util.getImage("tick_octagon_16.png");

    public static Map<Integer, Color> colorMap = new HashMap(){{
        put(1, new Color(2, 131, 65));
        put(2, Color.BLUE);
        put(3, Color.CYAN);
        put(4, Color.CYAN);
        put(5, Color.ORANGE);
        put(6, Color.ORANGE);
        put(7, Color.RED);
        put(8, Color.RED);
    }};

    public GamePan(){

        specialSize();
        setClickHandler();
    }

    private void specialSize(){
        this.width = this.col * cellWidth;
        this.height = this.row * cellWidth;
        this.setPreferredSize(new Dimension(this.width, this.height));
    }

    //布雷
    private void generateCellInfo(int r, int c){
        this.coor = new CellInfo[row][col];
        for(int i=0;i<row;i++){
            for (int j=0;j<col;j++){
                coor[i][j] = new CellInfo(false, false, i, j);
            }
        }

        coor[r][c] = new CellInfo(false, true, r, c);//中心

        //System.out.println("布雷区域：");
        while(initBoomCount>0){
            int rx = Util.randomInt(row);
            int ry = Util.randomInt(col);
            if(rx==r && ry==c) continue;
            CellInfo cellInfo = coor[rx][ry];
            if(cellInfo.isBoom) continue;
            boolean isBoom = Util.randomBoolean();
            if(isBoom){
                coor[rx][ry].isBoom = true;
                coor[rx][ry].value = -1;
                //System.out.println("["+rx+","+ry+"]");
                initBoomCount--;
            }
        }

        for(int i=0;i<row;i++){
            for (int j=0;j<col;j++){
                if(coor[i][j].isBoom) continue;
                int n = 0;
                if(i-1>=0&&i-1<row&&j-1>=0&&j-1<col){
                    if(coor[i-1][j-1].isBoom) n++; //左上
                }
                if(i-1>=0&&i-1<row&&j>=0&&j<col){
                    if(coor[i-1][j].isBoom) n++; //上
                }
                if(i-1>=0&&i-1<row&&j+1>=0&&j+1<col){
                    if(coor[i-1][j+1].isBoom) n++; //右上
                }

                if(i>=0&&i<row&&j-1>=0&&j-1<col){
                    if(coor[i][j-1].isBoom) n++; //左
                }
                if(i>=0&&i<row&&j+1>=0&&j+1<col){
                    if(coor[i][j+1].isBoom) n++; //右
                }

                if(i+1>=0&&i+1<row&&j-1>=0&&j-1<col){
                    if(coor[i+1][j-1].isBoom) n++; //左下
                }
                if(i+1>=0&&i+1<row&&j>=0&&j<col){
                    if(coor[i+1][j].isBoom) n++; //下
                }
                if(i+1>=0&&i+1<row&&j+1>=0&&j+1<col){
                    if(coor[i+1][j+1].isBoom) n++; //右下
                }
                coor[i][j].value = n;
            }
        }
    }

    private void setClickHandler(){
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            int button = e.getButton();
            int currX = e.getY()/cellWidth;
            int currY = e.getX()/cellWidth;
            if(currX>=row||currY>=col) return;

            if(button == MouseEvent.BUTTON1){ //鼠标左键
                System.out.println("[" + currX + "," + currY + "]");
                if(!hasGenerateBoom){
                    generateCellInfo(currX, currY);
                    hasGenerateBoom = true;
                    repaint();
                }else{
                    CellInfo cellInfo = coor[currX][currY];
                    if(cellInfo.isOpen) return;
                    if(cellInfo.isBoom){ //点开了雷
                        openAll();
                        repaint();
                        JOptionPane.showMessageDialog(null, "Boooooom!", "提示", JOptionPane.ERROR_MESSAGE);
                    }else{
                        cellInfo.isOpen = true;
                        if(cellInfo.value>0){
                            repaint();
                        }else if(cellInfo.value==0){
                            Set<CellInfo> emptyArea = expandEmptyArea(currX, currY, new HashSet<CellInfo>());
                            findEmptyAreaBoundary(emptyArea);
                        }
                    }
                }
            }else if(button == MouseEvent.BUTTON3){ //鼠标右键
                CellInfo cellInfo = coor[currX][currY];
                if(!cellInfo.isOpen){
                    cellInfo.isMarked = !cellInfo.isMarked;
                    repaint();
                }
            }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintGrid(g);
    }

    private void paintGrid(Graphics _g){
        Graphics2D g = (Graphics2D)_g;
        //绘制网格
        g.setColor(new Color(197, 197, 197));
        for(int i=0;i<row;i++){
            for (int j=0;j<col;j++){
                g.fillRect(cellWidth*j+celllspadding, cellWidth*i+celllspadding, cellWidth-cellOffset, cellWidth-cellOffset);
            }
        }
        //绘制标记情况
        if(hasGenerateBoom){
            for(int i=0;i<row;i++){
                for (int j=0;j<col;j++){
                    CellInfo cellInfo = coor[i][j];
                    if(cellInfo.isOpen && cellInfo.isBoom){
                        //g.setColor(Color.RED);
                        //g.fillRect(cellWidth*j+celllspadding, cellWidth*i+celllspadding, cellWidth-cellOffset, cellWidth-cellOffset);
                        g.drawImage(bombImage, cellWidth*j + 7, cellWidth*i + 7, bombImage.getWidth(null), bombImage.getHeight(null), null);
                    }else if(cellInfo.isOpen && !cellInfo.isBoom){
                        g.setColor(Color.BLACK);
                        int value = cellInfo.value;
                        if(value==0){
                            g.setColor(Color.WHITE);
                            g.fillRect(cellWidth*j+celllspadding, cellWidth*i+celllspadding, cellWidth-cellOffset, cellWidth-cellOffset);
                        }else{
                            g.setStroke(new BasicStroke(4f));
                            g.setColor(colorMap.get(value));
                            g.drawString(value+"",cellWidth*j+10, cellWidth*i-10 + cellWidth);
                        }
                    }else if(cellInfo.isMarked){
                        //g.setColor(Color.YELLOW);
                        //g.fillRect(cellWidth*j, cellWidth*i, cellWidth, cellWidth);
                        g.drawImage(markImage, cellWidth*j + 7, cellWidth*i + 7, markImage.getWidth(null), markImage.getHeight(null), null);
                    }
                }
            }
        }
    }

    //点到雷时，自动点开全部，表示结束
    private void openAll(){
        for(int i=0;i<row;i++){
            for (int j=0;j<col;j++){
                coor[i][j].isOpen = true;
            }
        }
    }

    //点到空白格子时，自动展开邻接的空白格子
    private Set<CellInfo> expandEmptyArea(int row, int col, Set<CellInfo> tempSet){
        //System.out.println("expandEmptyArea：" + row + "," + col);
        List<CellInfo> cellList = new ArrayList<>();
        /*if(row-1>=0 && col-1>=0 && coor[row-1][col-1].value==0 && !tempSet.contains(coor[row-1][col-1])){
            cellList.add(coor[row-1][col-1]);
            coor[row-1][col-1].isOpen = true;
            tempSet.add(coor[row-1][col-1]);
        }*/
        if(row-1>=0 && coor[row-1][col].value==0 && !tempSet.contains(coor[row-1][col])){
            cellList.add(coor[row-1][col]);
            coor[row-1][col].isOpen = true;
            tempSet.add(coor[row-1][col]);
        }
        /*if(row-1>=0 && col+1<this.col && coor[row-1][col+1].value==0 && !tempSet.contains(coor[row-1][col+1])){
            cellList.add(coor[row-1][col+1]);
            coor[row-1][col+1].isOpen = true;
            tempSet.add(coor[row-1][col+1]);
        }*/
        if(col-1>=0 && coor[row][col-1].value==0 && !tempSet.contains(coor[row][col-1])){
            cellList.add(coor[row][col-1]);
            coor[row][col-1].isOpen = true;
            tempSet.add(coor[row][col-1]);
        }
        if(col+1<this.col && coor[row][col+1].value==0 && !tempSet.contains(coor[row][col+1])){
            cellList.add(coor[row][col+1]);
            coor[row][col+1].isOpen = true;
            tempSet.add(coor[row][col+1]);
        }
        /*if(row+1<this.row && col-1>=0 && coor[row+1][col-1].value==0 && !tempSet.contains(coor[row+1][col-1])){
            cellList.add(coor[row+1][col-1]);
            coor[row+1][col-1].isOpen = true;
            tempSet.add(coor[row+1][col-1]);
        }*/
        if(row+1<this.row && coor[row+1][col].value==0 && !tempSet.contains(coor[row+1][col])){
            cellList.add(coor[row+1][col]);
            coor[row+1][col].isOpen = true;
            tempSet.add(coor[row+1][col]);
        }
        /*if(row+1<this.row && col+1<this.col && coor[row+1][col+1].value==0 && !tempSet.contains(coor[row+1][col+1])){
            cellList.add(coor[row+1][col+1]);
            coor[row+1][col+1].isOpen = true;
            tempSet.add(coor[row+1][col+1]);
        }*/
        //repaint();
        //try {Thread.sleep(20);} catch (InterruptedException e) {}
        cellList.forEach(cell->{
            expandEmptyArea(cell.row, cell.col, tempSet);
        });
        return tempSet;
    }

    //确定空白区域的边界，并将边界的数字显示出来
    private void findEmptyAreaBoundary(Set<CellInfo> emptyArea){
        emptyArea.forEach(c->{
            int row = c.row, col = c.col;
            if(row-1>=0 && col-1>=0){
                coor[row-1][col-1].isOpen = true;
            }
            if(row-1>=0){
                coor[row-1][col].isOpen = true;
            }
            if(row-1>=0 && col+1<this.col){
                coor[row-1][col+1].isOpen = true;
            }
            if(col-1>=0){
                coor[row][col-1].isOpen = true;
            }
            if(col+1<this.col){
                coor[row][col+1].isOpen = true;
            }
            if(row+1<this.row && col-1>=0){
                coor[row+1][col-1].isOpen = true;
            }
            if(row+1<this.row){
                coor[row+1][col].isOpen = true;
            }
            if(row+1<this.row && col+1<this.col){
                coor[row+1][col+1].isOpen = true;
            }
        });
        repaint();
    }
}
