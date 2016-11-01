package celllayout.joe.com.celllayout;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Description
 * Created by chenqiao on 2016/10/26.
 */
public class CellLayout extends ViewGroup implements View.OnDragListener {

    private int columns = 6;
    private int rows = 4;
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

        setOnDragListener(this);
    }

    public void addCell(Cell cell) {
        cells.add(cell);
        if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
            //如果界面已经显示了，那么立刻进行一次位置计算
            initCell(cell);
            Point p = findLeftAndTop(cell);
            cell.setExpectRowIndex(p.x);
            cell.setExpectColumnIndex(p.y);
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
        childExpectCellWidthNum = (int) Math.ceil(child.getMeasuredWidth() / (per_cell_width * 1.0f));
        childExpectCellHeightNum = (int) Math.ceil(child.getMeasuredHeight() / (per_cell_height * 1.0f));
        cell.setWidthNum(childExpectCellWidthNum);
        cell.setHeightNum(childExpectCellHeightNum);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int nowL, nowT, locateX, locateY;
        int cellWidth, cellHeight;
        for (Cell cell : cells) {
            if (cell.getExpectColumnIndex() >= 0 && cell.getExpectRowIndex() >= 0) {
                locateX = cell.getExpectColumnIndex();
                locateY = cell.getExpectRowIndex();
            } else {
                Point p = findLeftAndTop(cell);
                cell.setExpectRowIndex(p.x);
                cell.setExpectColumnIndex(p.y);
                locateX = cell.getExpectColumnIndex();
                locateY = cell.getExpectRowIndex();
                if (p.x == -1 || p.y == -1) {
                    Log.e("CellLayout", "onLayout: child is to large or to much children");
                    continue;
                }
            }
            nowL = locateX * per_cell_width;
            nowT = locateY * per_cell_height;
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
        for (int row = 0; row <= rows - cell.getHeightNum(); row++) {
            for (int column = 0; column <= columns - cell.getWidthNum(); column++) {
                isEnough = checkIsEnough(cellHolds, column, row, cell.getWidthNum(), cell.getHeightNum());
                if (isEnough) {
                    fillCellLayout(column, row, cell.getWidthNum(), cell.getHeightNum());
                    result.set(row, column);
                    return result;
                }
            }
        }
        return result;
    }

    //判断是否可以放置cell
    private boolean checkIsEnough(boolean[][] myCellHolds, int startX, int startY, int width, int height) {
        if (startX + width > columns || startY + height > rows) {
            return false;
        }
        for (int i = startX; i < startX + width; i++) {
            for (int j = startY; j < startY + height; j++) {
                if (myCellHolds[j][i]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void fillCellLayout(int startX, int startY, int width, int height) {
        if (startX + width > columns || startY + height > rows) {
            return;
        }
        for (int i = startX; i < startX + width; i++) {
            for (int j = startY; j < startY + height; j++) {
                cellHolds[j][i] = true;
            }
        }
    }

    private boolean[][] tempCellHolds;

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Cell cell = (Cell) event.getLocalState();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                Log.d("CellLayout", "onDrag: DRAG_START");
                tempCellHolds = cellHolds.clone();
                for (int i = cell.getExpectColumnIndex(); i < cell.getExpectColumnIndex() + cell.getWidthNum(); i++) {
                    for (int j = cell.getExpectRowIndex(); j < cell.getExpectRowIndex() + cell.getHeightNum(); j++) {
                        tempCellHolds[j][i] = false;
                    }
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                Log.d("CellLayout", "onDrag: DRAG_ENTERED");
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                Log.d("CellLayout", "onDrag: LOCATION");
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                Log.d("CellLayout", "onDrag: DRAG_EXITED");
                break;
            case DragEvent.ACTION_DROP:
                Log.d("CellLayout", "onDrag: DROP=" + event.getX() + " " + event.getY());
                int tempColumnIndex = (int) (event.getX() / per_cell_width);
                int tempRowIndex = (int) (event.getY() / per_cell_height);
                if (checkIsEnough(tempCellHolds, tempColumnIndex, tempRowIndex, cell.getWidthNum(), cell.getHeightNum())) {
                    fillCellLayout(tempColumnIndex, tempRowIndex, cell.getWidthNum(), cell.getHeightNum());
                    cell.setExpectColumnIndex(tempColumnIndex);
                    cell.setExpectRowIndex(tempRowIndex);
                    Log.d("CellLayout", "change Position:" + tempRowIndex + " " + tempColumnIndex);
//                    postInvalidate();
                    requestLayout();
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                Log.d("CellLayout", "onDrag: DRAG_ENDED");
                break;
        }
        return true;
    }

    public static class Cell {
        private String tag;
        private View contentView;
        private int widthNum;//横向占据的格数
        private int heightNum;//纵向占据的格数
        private int expectColumnIndex = -1, expectRowIndex = -1;//计算出的可摆放的位置

        public Cell(String tag, View view) {
            this.tag = tag;
            this.contentView = view;
            this.contentView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DragShadowBuilder builder = new DragShadowBuilder(v);
                    v.startDrag(null, builder, Cell.this, 0);
                    return true;
                }
            });
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

        public int getExpectColumnIndex() {
            return expectColumnIndex;
        }

        public void setExpectColumnIndex(int expectColumnIndex) {
            this.expectColumnIndex = expectColumnIndex;
        }

        public int getExpectRowIndex() {
            return expectRowIndex;
        }

        public void setExpectRowIndex(int expectRowIndex) {
            this.expectRowIndex = expectRowIndex;
        }
    }
}