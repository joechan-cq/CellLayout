package celllayout.joe.com.celllayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private CellLayout cellLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cellLayout = (CellLayout) findViewById(R.id.layout_cell_main);
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.mipmap.ic_launcher);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            CellLayout.Cell cell = new CellLayout.Cell(i + "", imageView);
            cellLayout.addCell(cell);
        }
        for (int i = 0; i < 1; i++) {
            ImageView imageView = new ImageView(this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(300, 300);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.mipmap.ic_launcher);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            CellLayout.Cell cell = new CellLayout.Cell(i + "", imageView);
            cellLayout.addCell(cell);
        }
        TextView textTv = new TextView(this);
        textTv.setText("hello world");
        textTv.setGravity(Gravity.CENTER);
        CellLayout.Cell cell = new CellLayout.Cell("txt", textTv);
        cellLayout.addCell(cell);

        textTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setImageResource(R.mipmap.ic_launcher);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                CellLayout.Cell cell = new CellLayout.Cell("", imageView);
                cellLayout.addCell(cell);
            }
        });
    }
}
