package celllayout.joe.com.celllayout;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Description
 * Created by chenqiao on 2016/10/26.
 */
public class CellLayout extends ViewGroup {

    private int columns = 5;
    private int rows = 5;
    private int per_cell_width;
    private int per_cell_height;
    private ArrayList<Cell> cells;
    private boolean[][] cellHolds;

    public CellLayout(Context context) {
        this(context, null);
    }

    public CellLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CellLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cells = new ArrayList<>();
        cellHolds = new boolean[rows][columns];
    }

    public void addCell(Cell cell) {
        cells.add(cell);
        if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
            //如果界面已经显示了，那么立刻进行一次位置计算
            initCell(cell);
            Point p = findLeftAndTop(cell);
            cell.setExpectXIndex(p.x);
            cell.setExpectYIndex(p.y);
            postInvalidate();
        }
    }

    int childWidthSpec, childHeightSpec, childExpectCellWidthNum, childExpectCellHeightNum;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        removeAllViews();
        per_cell_width = getMeasuredWidth() / columns;
        per_cell_height = getMeasuredHeight() / rows;
        // 获取到getMeasuredWidth后，进行一次cell的测量
        for (Cell cell : cells) {
            initCell(cell);
        }
    }

    private void initCell(Cell cell) {
        View child = cell.getContentView();
        addView(child);
        childWidthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.AT_MOST);
        childHeightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.AT_MOST);
        measureChild(child, childWidthSpec, childHeightSpec);
        //计算出cell要占据几格
        childExpectCellWidthNum = (int) Math.ceil(child.getMeasuredWidth() / (per_cell_width * 1.0));
        childExpectCellHeightNum = (int) Math.ceil(child.getMeasuredHeight() / (per_cell_height * 1.0));
        cell.setWidthNum(childExpectCellWidthNum);
        cell.setHeightNum(childExpectCellHeightNum);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int nowL, nowT, locateX, locateY;
        int cellWidth, cellHeight;
        for (Cell cell : cells) {
            if (cell.getExpectXIndex() >= 0 && cell.getExpectYIndex() >= 0) {
                locateX = cell.getExpectXIndex();
                locateY = cell.getExpectYIndex();
            } else {
                Point p = findLeftAndTop(cell);
                cell.setExpectXIndex(p.x);
                cell.setExpectYIndex(p.y);
                locateX = p.x;
                locateY = p.y;
                if (p.x == -1 || p.y == -1) {
                    Log.e("CellLayout", "onLayout: child is to large or to much children");
                    continue;
                }
            }
            nowL = locateY * per_cell_width;
            nowT = locateX * per_cell_height;
            cellWidth = cell.getWidthNum() * per_cell_width;
            cellHeight = cell.getHeightNum() * per_cell_height;
            //修改cell的layoutparam的大小，不然会导致cell的view中的gravity失效
            cell.getContentView().getLayoutParams().width = cellWidth;
            cell.getContentView().getLayoutParams().height = cellHeight;
            cell.getContentView().layout(nowL, nowT, nowL + cellWidth, nowT + cellHeight);
        }
    }

    //查找足够空间放置cell
    private Point findLeftAndTop(Cell cell) {
        Point result = new Point(-1, -1);
        boolean isEnough;
        for (int i = 0; i <= rows - cell.getHeightNum(); i++) {
            for (int j = 0; j <= columns - cell.getWidthNum(); j++) {
                isEnough = true;
                for (int k = j; k < j + cell.getWidthNum(); k++) {
                    if (!isEnough) {
                        break;
                    }
                    for (int l = i; l < i + cell.getHeightNum(); l++) {
                        if (cellHolds[k][l]) {
                            isEnough = false;
                            break;
                        }
                    }
                }
                if (isEnough) {
                    for (int k = j; k < j + cell.getWidthNum(); k++) {
                        for (int l = i; l < i + cell.getHeightNum(); l++) {
                            cellHolds[k][l] = true;
                        }
                    }
                    result.set(i, j);
                    return result;
                }
            }
        }
        return result;
    }

    public static class Cell {
        private String tag;
        private View contentView;
        private int widthNum;//横向占据的格数
        private int heightNum;//纵向占据的格数
        private int expectXIndex = -1, expectYIndex = -1;//计算出的可摆放的位置

        public Cell(String tag, View view) {
            this.tag = tag;
            this.contentView = view;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public View getContentView() {
            return contentView;
        }

        public void setContentView(View contentView) {
            this.contentView = contentView;
        }

        public int getWidthNum() {
            return widthNum;
        }

        public void setWidthNum(int widthNum) {
            this.widthNum = widthNum;
        }

        public void setHeightNum(int heightNum) {
            this.heightNum = heightNum;
        }

        public int getHeightNum() {
            return heightNum;
        }

        public int getExpectXIndex() {
            return expectXIndex;
        }

        public void setExpectXIndex(int expectXIndex) {
            this.expectXIndex = expectXIndex;
        }

        public int getExpectYIndex() {
            return expectYIndex;
        }

        public void setExpectYIndex(int expectYIndex) {
            this.expectYIndex = expectYIndex;
        }
    }
}