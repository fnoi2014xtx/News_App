package com.java.xtxnews;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import android.os.Handler;
import android.os.Message;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLogin extends Fragment {

    private Button register;
    private Button login;
    private EditText username;
    private EditText password;
    private EditText ip_address;
    private String out;

    public FragmentLogin() {
        // Required empty public constructor
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){// if not then search
            if(msg.what == 1) {
                ((MainActivity) (getActivity())).flipLogFragment();
            }
            Toast.makeText(getActivity(),out,Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        username = view.findViewById(R.id.account_input);
        password = view.findViewById(R.id.password_input);
        ip_address = view.findViewById(R.id.ip);
        login = view.findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userName = username.getText().toString();
                final String passWord = password.getText().toString();
                final String ip = ip_address.getText().toString();

                try {
                    String path = "http://"+URLEncoder.encode(ip,"utf-8")+":8080/Web1/UserServlet?type="+ URLEncoder.encode("login", "utf-8")+"&user="+URLEncoder.encode(userName,"utf-8")+"&password="+URLEncoder.encode(passWord,"utf-8");
                    //Toast.makeText(getActivity(), path, Toast.LENGTH_SHORT).show();
                    out = null;
                    //while(out == null || out.equals("")) {
                    HttpsUtils.sendOkHttpRequest(path, new okhttp3.Callback() {

                        public void onResponse(Call call, Response response) throws IOException {
                            String output = response.body().string();
                            Log.w("out:", output);
                            String[] result = output.split("\n");
                            //Toast.makeText(getActivity(), output , Toast.LENGTH_SHORT).show();
                            if (result[0].equals("Success")) {
                                NewsIO.setLogged(true);
                                NewsIO.setUser(userName);
                                NewsIO.setPassWord(passWord);
                                NewsIO.setIp(ip);
                                String[] subscribedlst = result[2].split("\t");
                                int j = subscribedlst.length;
                                Map<String, String> map = NewsIO.getMap();
                                map.clear();
                                for (int k = 0; 2 * k + 1 < j; k++) {//subscribed
                                    map.put(subscribedlst[2 * k + 1], subscribedlst[2 * k + 2]);
                                }
                                out = "Login Successfully";
                                NewsIO.Generate();
                                Log.w("end","end");
                                //Toast.makeText(getActivity(), "Login Successfully", Toast.LENGTH_SHORT).show();
                                //((MainActivity)(getActivity())).flipLogFragment();
                            } else {
                                out = "Error: " + result[0];
                            }
                            Message msg = new Message();
                            if(out.equals("Login Successfully")){

                                msg.what = 1;

                            }
                            else{
                                msg.what = 0;
                            }
                            handler.sendMessage(msg);
                            //Toast.makeText(getActivity(), out, Toast.LENGTH_SHORT).show();
                            //((MainActivity)(getActivity())).flipLogFragment();
                        }

                        public void onFailure(Call call, IOException e) {
                            out = "Error: Internet Failure";
                            Message msg = new Message();
                            msg.what = 0;
                            handler.sendMessage(msg);
                            //Toast.makeText(getActivity(), out, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getActivity(), "Error: Internet Failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //}

                }catch(Exception e){
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        register = (Button) view.findViewById(R.id.register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = username.getText().toString();
                String passWord = password.getText().toString();
                String ip = ip_address.getText().toString();
                try {
                    String url = "http://" + URLEncoder.encode(ip, "utf-8") + ":8080/Web1/UserServlet?type=new" + "&user=" + URLEncoder.encode(userName, "utf-8") + "&password=" + URLEncoder.encode(passWord, "utf-8");
                    //Toast.makeText(getActivity(), url, Toast.LENGTH_SHORT).show();
                    out = null;
                    // while(out == null || out.equals("")) {
                    HttpsUtils.sendOkHttpRequest(url, new okhttp3.Callback() {
                        public void onResponse(Call call, Response response) throws IOException {
                            String output = response.body().string();
                            Log.w("out:", output);
                            if (output.equals("Success")) {
                                out = "Registered Successfully";
                            } else {
                                out = "Error: " + output;
                            }
                            Message msg = new Message();
                            msg.what = 0;
                            handler.sendMessage(msg);
                            //Toast.makeText(getActivity(), out, Toast.LENGTH_SHORT).show();
                        }

                        public void onFailure(Call call, IOException e) {
                            out = "Error: Internet Failure";
                            Message msg = new Message();
                            msg.what = 0;
                            handler.sendMessage(msg);
                            //Toast.makeText(getActivity(), out, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //}
                    //URL url = new URL("http://" + URLEncoder.encode(ip, "utf-8") + ":8080/Web1/UserServlet?type=new" + "&user=" + URLEncoder.encode(userName, "utf-8") + "&password=" + URLEncoder.encode(passWord, "utf-8"));

                }catch(Exception e){
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


}
