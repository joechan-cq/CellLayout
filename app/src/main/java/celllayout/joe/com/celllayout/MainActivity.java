package celllayout.joe.com.celllayout;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private AppWidgetHost mAppWidgetHost;
    private AppWidgetManager mAppWidgetManager;
    private CellLayout cellLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAppWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        mAppWidgetHost = new AppWidgetHost(getApplicationContext(), 0xfffff);
        //开始监听widget的变化
        mAppWidgetHost.startListening();

        cellLayout = (CellLayout) findViewById(R.id.layout_cell_main);
//        for (int i = 0; i < 3; i++) {
//            ImageView imageView = new ImageView(this);
//            imageView.setImageResource(R.mipmap.ic_launcher);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            CellLayout.Cell cell = new CellLayout.Cell(i + "", imageView);
//            cellLayout.addCell(cell);
//        }
//        for (int i = 0; i < 1; i++) {
//            ImageView imageView = new ImageView(this);
//            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(300, 300);
//            imageView.setLayoutParams(params);
//            imageView.setImageResource(R.mipmap.ic_launcher);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            CellLayout.Cell cell = new CellLayout.Cell(i + "", imageView);
//            cellLayout.addCell(cell);
//        }
        TextView textTv = new TextView(this);
        textTv.setText("hello world");
        textTv.setGravity(Gravity.CENTER);
        CellLayout.Cell cell = new CellLayout.Cell("txt", textTv);
        cellLayout.addCell(cell);

        textTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWidgetChooser();
            }
        });
    }

    private void showWidgetChooser() {
        int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        startActivityForResult(pickIntent, 0xaa);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0xaa:
                    addAppWidget(data);
                    break;
                case 0xbb:
                    completeAddAppWidget(data);
                    break;
            }
        } else if (requestCode == 0xaa &&
                resultCode == RESULT_CANCELED && data != null) {
            // Clean up the appWidgetId if we canceled
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    private void addAppWidget(Intent data) {
        int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

        String customWidget = data.getStringExtra("custom_widget");
        if ("search_widget".equals(customWidget)) {
            //这里直接将search_widget删掉了
            mAppWidgetHost.deleteAppWidgetId(appWidgetId);
        } else {
            AppWidgetProviderInfo appWidget = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

            if (appWidget.configure != null) {
                //有配置，弹出配置
                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
                intent.setComponent(appWidget.configure);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

                startActivityForResult(intent, 0xbb);
            } else {
                //没有配置，直接添加
                completeAddAppWidget(data);
            }
        }
    }

    /**
     * 添加widget
     *
     * @param data
     */
    private void completeAddAppWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        View hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(appWidgetInfo.minWidth,appWidgetInfo.minHeight);
        hostView.setLayoutParams(params);
        CellLayout.Cell cell = new CellLayout.Cell("Widget", hostView);
        cellLayout.addCell(cell);
    }
}
