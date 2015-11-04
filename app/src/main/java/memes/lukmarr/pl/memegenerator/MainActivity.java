package memes.lukmarr.pl.memegenerator;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int SELECT_PHOTO = 100;
    private RelativeLayout parent;
    private Button rotateButton, addTextButton;
    private int rotationValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parent = (RelativeLayout) findViewById(R.id.parent);
        rotateButton = (Button) findViewById(R.id.rotateButton);
        addTextButton = (Button) findViewById(R.id.addText);
        rotateButton.setVisibility(View.GONE);
        addTextButton.setVisibility(View.GONE);
        findViewById(R.id.photoTake).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    final Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);

                    Mateyko.with(MainActivity.this).load(yourSelectedImage).resize(320, 240)
                            .rotated(-90)
                            .into(parent);
                    rotateButton.setVisibility(View.VISIBLE);
                    addTextButton.setVisibility(View.VISIBLE);
                    rotateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rotationValue = (rotationValue + 90) % 360;
                            Mateyko.with(MainActivity.this).load(yourSelectedImage).resize(320, 240)
                                    .rotated(rotationValue)
                                    .into(parent);
                        }
                    });
                    addTextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AddTextAction(MainActivity.this, new AddTextAction.TextSendListener() {
                                @Override
                                public void onSend(String title, String subtitle) {
                                    setNewImageWithText(title, subtitle, yourSelectedImage);
                                }

                                @Override
                                public void onCancel() {
                                    Toast.makeText(MainActivity.this, "...", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
        }
    }

    private void setNewImageWithText(String title, String subtitle, Bitmap yourSelectedImage) {
        Matrix matrix = new Matrix();
        matrix.postRotate(270);//270
        Bitmap selectedImage = Bitmap.createBitmap(yourSelectedImage, 0, 0, yourSelectedImage.getWidth(),
                yourSelectedImage.getHeight(), matrix, true);

        Bitmap bitmap = selectedImage.copy(android.graphics.Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
//                            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint paint = new Paint();
        // text color - #3D3D3D
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.MONOSPACE);
        // text size in pixels
        paint.setTextSize((100));
        // text shadow
        paint.setShadowLayer(5f, 5f, 5f, Color.BLACK);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(title, 0, title.length(), bounds);
        int x = (parent.getWidth() / 5);// - bounds.width()) / 2;
        int y = (parent.getHeight() / 5);// + bounds.height()) / 2;
        canvas.drawText(title, x, y, paint);

        Rect bounds2 = new Rect();
        paint.getTextBounds(subtitle, 0, subtitle.length(), bounds2);
        int x2 = (4 * parent.getWidth() / 5);// - bounds.width()) / 2;
        int y2 = (4 * parent.getHeight() / 5);// + bounds.height()) / 2;

        canvas.drawText(subtitle, x2, y2, paint);
        Mateyko.with(MainActivity.this).load(bitmap).resize(320, 240)
                .into(parent);
    }
}
