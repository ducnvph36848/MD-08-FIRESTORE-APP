package com.example.md_08_ungdungfivestore;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.md_08_ungdungfivestore.adapters.ChatAdapter;
import com.example.md_08_ungdungfivestore.models.ChatMessage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ManChat extends AppCompatActivity {

    EditText edtMessage;
    ImageButton btnSend;
    ListView listView;

    ArrayList<ChatMessage> chatList;
    ChatAdapter adapter;

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        listView = findViewById(R.id.listViewChat);

        chatList = new ArrayList<>();
        adapter = new ChatAdapter(this, chatList);
        listView.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userMsg = edtMessage.getText().toString().trim();
                if (userMsg.isEmpty()) return;

                // Hiển thị message người dùng
                chatList.add(new ChatMessage(userMsg, true));
                adapter.notifyDataSetChanged();
                edtMessage.setText("");
                listView.smoothScrollToPosition(chatList.size() - 1);

                // Gửi tới OpenAI
                sendMessageToAI(userMsg);
            }
        });
    }

    private void sendMessageToAI(String message) {
        Gson gson = new Gson();
        // JSON gửi lên API
        JsonObject json = new JsonObject();
        json.addProperty("model", "gpt-5-nano");
        JsonArray messages = new JsonArray();
        JsonObject system = new JsonObject();
        system.addProperty("role", "system");
        //hướng dẫn, ngữ cảnh, hoặc vai trò cho AI
        system.addProperty("content", "Bạn là tư vấn viên bán quần áo thời trang, trả lời ngắn gọn.");
        messages.add(system);

        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("content", message);
        messages.add(user);
        json.add("messages", messages);
        RequestBody body = RequestBody.create(
                gson.toJson(json),
                MediaType.parse("application/json")
        );

        String apiKey = BuildConfig.OPENAI_API_KEY;   // lấy API KEY từ build.gradle

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        // Hiển thị "Đang trả lời..."
        chatList.add(new ChatMessage("Đang trả lời...", false));
        runOnUiThread(() -> adapter.notifyDataSetChanged());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    chatList.remove(chatList.size() - 1);
                    chatList.add(new ChatMessage("Lỗi mạng: " + e.getMessage(), false));
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String bodyStr = response.body().string();

                Log.e("AI_RESPONSE", bodyStr);   // GHI LOG ĐỂ XEM SERVER TRẢ VỀ GÌ

                String reply = parseReplyFromJson(bodyStr);

                runOnUiThread(() -> {
                    chatList.remove(chatList.size() - 1);
                    chatList.add(new ChatMessage(reply, false));
                    adapter.notifyDataSetChanged();
                });
            }

        });
    }


    private String parseReplyFromJson(String json) {
        try {
            JSONObject obj = new JSONObject(json);

            // Nếu server trả về lỗi
            if (obj.has("error")) {
                return "Lỗi API: " + obj.getJSONObject("error").getString("message");
            }

            // Nếu đúng format của ChatGPT
            JSONArray choices = obj.getJSONArray("choices");
            JSONObject first = choices.getJSONObject(0);
            JSONObject msg = first.getJSONObject("message");

            return msg.getString("content");

        } catch (Exception e) {
            return "Lỗi phân tích JSON: " + e.getMessage();
        }
    }

}
