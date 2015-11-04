package memes.lukmarr.pl.memegenerator;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Lukasz Marczak on 2015-11-04.
 * Show pop-Up Dialog
 */
public class AddTextAction {


    public interface TextSendListener {
        void onSend(String title, String subtitle);

        void onCancel();
    }

    public AddTextAction(final Context context, final TextSendListener listener) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_view);
        dialog.setTitle("Set headers");
        final EditText title = (EditText) dialog.findViewById(R.id.title);
        final EditText subtitle = (EditText) dialog.findViewById(R.id.subtitle);
        Button send = (Button) dialog.findViewById(R.id.send);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String titleName = title.getText().toString();
                String subtitleName = subtitle.getText().toString();

                if (titleName.length() == 0 && subtitleName.length() == 0) {
                    Toast.makeText(context, "Enter at least one title!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (listener != null)
                    listener.onSend(titleName, subtitleName);
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onCancel();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
