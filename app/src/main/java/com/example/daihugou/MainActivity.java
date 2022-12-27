package com.example.daihugou;

import static android.widget.Toast.makeText;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static Boolean kakumei = false; // 革命中
    static Boolean yakiri = false;  // 8切り

    RecyclerView recyclerView;
    Button btnChoose;
    Button btnPass;
    MyImageAdapter adapter;

    String[] s = {"s","h","d","c"};

    private static final Integer[] photos = {
            R.drawable.s1, R.drawable.s2, R.drawable.s3,
            R.drawable.s4, R.drawable.s5, R.drawable.s6,
            R.drawable.s7, R.drawable.s8, R.drawable.s9,
            R.drawable.s10, R.drawable.s11, R.drawable.s12, R.drawable.s13,
            R.drawable.h1, R.drawable.h2, R.drawable.h3,
            R.drawable.h4, R.drawable.h5, R.drawable.h6,
            R.drawable.h7, R.drawable.h8, R.drawable.h9,
            R.drawable.h10, R.drawable.h11, R.drawable.h12, R.drawable.h13,
            R.drawable.d1, R.drawable.d2, R.drawable.d3,
            R.drawable.d4, R.drawable.d5, R.drawable.d6,
            R.drawable.d7, R.drawable.d8, R.drawable.d9,
            R.drawable.d10, R.drawable.d11, R.drawable.d12, R.drawable.d13,
            R.drawable.c1, R.drawable.c2, R.drawable.c3,
            R.drawable.c4, R.drawable.c5, R.drawable.c6,
            R.drawable.c7, R.drawable.c8, R.drawable.c9,
            R.drawable.c10, R.drawable.c11, R.drawable.c12, R.drawable.c13
    };

    volatile Boolean selectCheck = false;
    Boolean passCheck = false;
    private List<Integer> selectData;
    List<Integer> setData;

    boolean playFlag = true; // trueは継続可,Falseはゲーム終了

    int turn = 0;
    int pass = 0;
    int firstNum = 0; // 最初の人がカードを出した枚数

    Player desk;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // findViewId
        recyclerView = findViewById(R.id.myCards);
        btnChoose = findViewById(R.id.btnChoose);
        btnPass = findViewById(R.id.btnpass);
        TextView numCPU1 = findViewById(R.id.numCPU1);
        TextView numCPU2 = findViewById(R.id.numCPU2);
        TextView numCPU3 = findViewById(R.id.numCPU3);
        TextView cardTable = findViewById(R.id.cardTable);
        TextView revolution = findViewById(R.id.revolution);

        // recyclerViewSetting
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager rlayoutManager = new LinearLayoutManager(this);
        rlayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(rlayoutManager);

        //画像がランダムで入れ替わっている

        // ゲーム準備
        // 0~3 0がプレイヤー
        List<Player> Players = new ArrayList<>();
        for(int i=0;i<4;i++){
            Players.add(new Player());
        }

        // カード置き場
        desk = new Player();

        // 山札を生成
        Deck deck = new Deck();
        for(int i=0;i<52;i++) {
            Players.get(i % 4).Draw(deck);
        }
        Players.get(0).SortCards();

        //表示初期化
        setData = new ArrayList<>();
        adapter = new MyImageAdapter(setData);
        recyclerView.setAdapter(adapter);
        setViewPlayerCards(Players.get(0));

        btnChoose.setOnClickListener( v -> {
            selectData = adapter.getSelectData();
            selectCheck = true;
        });

        btnPass.setOnClickListener( v -> {
            selectData = adapter.getSelectData();
            adapter.ClearSelectData();
            passCheck = true;
            selectCheck = true;
        });

        new Thread(() -> {
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            // ゲームスタート
            while(playFlag){

                // プレイヤーの手札ソート
                Players.get(turn).SortCards();

                // Player turn
                if(turn == 0){
                    // プレイヤーの選択
                    while(true){

                        // 入力求ム
                        while (!selectCheck) ;
                        selectCheck = false;

                        int[] num = new int[selectData.size()];

                        for (int i = 0; i < selectData.size(); i++) {
                            num[i] = selectData.get(i);
                        }
                        adapter.ClearSelectData();
                        if(passCheck){
                            // passするとき
                            pass++;
                            passCheck = false;
                            break;
                        }else if(num.length >= 1 && checkers(desk,Players.get(0).cards,num,Players.get(0).Getsize())){
                            if(firstNum == 0){
                                firstNum = num.length;
                            }
                            if(firstNum == num.length){
                                // 机に置く
                                Arrays.sort(num);
                                for(int i=num.length-1;i>=0;i--){
                                    desk.Draw(Players.get(0).cards.get(num[i]));
                                    Players.get(0).cards.subList(num[i], num[i]+1).clear();
                                }
                                pass = 0;
                                break;
                            }
                        }
                        mainHandler.post(() -> makeText(this , "違うのを選択してください", Toast.LENGTH_LONG).show());
                    }
                    // 手札表示
                    mainHandler.post(() -> setViewPlayerCards(Players.get(0)));
                }else{
                    // 敵の番

                    // 思考時間風
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 出せる手札確認
                    List<Card> Pcards = new ArrayList<>();
                    for (Card c : Players.get(turn).cards) {
                        if(check(desk, c))Pcards.add(c);
                    }
                    if(Pcards.size()==0){
                        // 出せるの無ければpass
                        pass++;
                    }else{

                        int[] Pcn = new int[Pcards.size()]; // 同じ数字があった分だけカウントを増やす。
                        int max = 0;
                        int maxp = 0;
                        // 複数枚出せるのがあるか確認。
                        for(int i=0;i<Pcards.size();i++){
                            Pcn[i]++;
                            for(int j=i+1;j<Pcards.size();j++){
                                if(Pcards.get(i).no==Pcards.get(j).no){
                                    Pcn[i]++;
                                }
                            }
                            if(Pcn[i] > max){
                                max = Pcn[i];
                                maxp = i;
                            }
                        }

                        // 一番多いカードの位置を覚えます。
                        int[] pos = new int[max];
                        int j = 0;
                        for(int i=0;i<Pcards.size();i++){
                            if(Pcards.get(maxp).no == Pcards.get(i).no){
                                pos[j] = i;
                                j++;
                            }
                        }

                        if(firstNum == 0){
                            // 最初に場に出す人なら一番多いカードを出す。

                            // max分だけ場に出します。
                            for(int i=max-1;i>=0;i--){
                                desk.Draw(Pcards.get(pos[i]));
                                Players.get(turn).cards.remove(Pcards.get(pos[i]));
                            }
                            pass = 0;
                            firstNum = max;
                        }else{
                            // firstNum分だけカードを出したい
                            // 出せるカードはあるので1枚だけならランダムに出す
                            if(firstNum == 1){
                                int r = (int)Math.ceil(Math.random() * Pcards.size()-1);
                                desk.Draw(Pcards.get(r));
                                Players.get(turn).cards.remove(Pcards.get(r));
                                pass = 0;
                            }else{
                                // maxがfirstNum以上の枚数あれば出せるので
                                if(firstNum > max){
                                    // 足りなければpass
                                    pass++;
                                }else{
                                    // 足りてたらmaxのカードをfirstNum分だけだす。
                                    for(int i=firstNum-1;i>=0;i--){
                                        desk.Draw(Pcards.get(pos[i]));
                                        Players.get(turn).cards.remove(Pcards.get(pos[i]));
                                    }
                                    pass = 0;
                                }
                            }
                        }
                    }
                    int mai = Players.get(turn).cards.size();
                    if(turn==1){
                        mainHandler.post(() -> numCPU1.setText(Integer.valueOf(mai).toString()));
                    }else if(turn==2){
                        mainHandler.post(() -> numCPU2.setText(Integer.valueOf(mai).toString()));
                    }else{
                        mainHandler.post(() -> numCPU3.setText(Integer.valueOf(mai).toString()));
                    }
                }

                // ゲーム終了
                if (Players.get(turn).cards.size() <= 0){
                    mainHandler.post(() -> makeText(this , "finish", Toast.LENGTH_LONG).show());
                    int finalTurn = turn;
                    mainHandler.post(() -> cardTable.setText("winner:Player"+Integer.valueOf(finalTurn).toString()));
                    playFlag = false;
                    break;
                }

                // 8切りチェック
                if(desk.GetNo() == 8){
                    yakiri = true;
                }
                // 革命チェック
                if(firstNum == 4 && pass == 0){
                    kakumei = !kakumei;
                }
                if(kakumei){
                    mainHandler.post(() -> revolution.setText("革命中！"));
                }

                // turn 進行
                turn = (turn+1)%4;
                if(pass>=3){
                    // passしすぎで流れ
                    desk = new Player();
                    pass = 0;
                    firstNum = 0;
                    mainHandler.post(() -> cardTable.setText("流れ"));
                }else if(yakiri){
                    // 8切りによる流れ
                    desk = new Player();
                    turn = (turn+3)%4;
                    pass = 0;
                    firstNum = 0;
                    yakiri = false;
                    mainHandler.post(() -> cardTable.setText("8切り"));
                }else{
                    // firstNumの枚数分表示
                    StringBuilder deskViewText = new StringBuilder();
                    int[] ds = desk.GetSuits();
                    int[] dn = desk.GetNos();
                    for(int i=desk.cards.size()-1;i>=desk.cards.size()-firstNum;i--){
                        deskViewText.append(" ").append(s[ds[i]]);
                        deskViewText.append(dn[i]);
                    }
                    mainHandler.post(() -> cardTable.setText(deskViewText));
                }
            }
        }).start();

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setViewPlayerCards(Player player){
        setData.clear();
        int[] nos = player.GetNos();
        int[] suits = player.GetSuits();
        for (int i = 0; i < player.Getsize(); i++) {
            setData.add(photos[suits[i]*13 + nos[i]-1]);
        }
        adapter.notifyDataSetChanged();
    }

    // デスクに出せるかチェック　セーフならtrue ダメだったらfalse
    public static Boolean check(Player desk,Card card){
        int d = desk.GetNo();
        int c = card.no;
        if(d == 2 || d == 1)d += 13;
        if(c == 2 || c == 1)c += 13;
        if(kakumei){
            // 革命中は強さが入れ替わる
            if(d == 0){
                return true;
            }
            return d>c;
        }
        return d<c;
    }

    // まとめて確認できるように
    public static Boolean checkers(Player desk,List<Card> cards,int[] num,int length){
        // 出そうとしてるやつが大丈夫かチェック
        int no = -1;
        for(int n:num){
            // 手持ちカードから選択されているか
            if(n>=length || n < 0){
                return false;
            }
            // そのカードがdeskと比べて使えるカードか
            if(!check(desk, cards.get(n))){
                return false;
            }
            // そのカードは同じ数字か
            // 階段ルール追加するときはここ変更
            if(no == -1){
                no = cards.get(n).no;
            }else{
                if(no != cards.get(n).no){
                    return false;
                }
            }
        }

        return true;
    }
}