package com.example.daihugou;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
// プレイヤークラス
public class Player
{
    // メンバ変数定義
    // プレイヤーが持っているカード情報
    private Card card_info;
    public List<Card> cards;

    // コンストラクタ(初期化処理)
    public Player()
    {
        cards = new ArrayList<>();
        card_info = new Card(); // オブジェクトを生成

        // カード情報を初期化
        card_info.no = 0;   // 数字
        card_info.suit = 0; // マーク
    }

    // メソッド定義
    // カードを1枚ドロー
    public void Draw(Deck deck)
    {
        // ドローしたカード情報を取得
        card_info = deck.GetCard();
        cards.add(card_info);
    }

    public void Draw(Card card)
    {
        // ドローしたカード情報を取得
        card_info = card;
        cards.add(card_info);
    }

    // プレイヤーが持っているカードの数字を取得
    public int GetNo()
    {
        return card_info.no;
    }

    // プレイヤーが持っているカードのマークを取得
    public int GetSuit()
    {
        return card_info.suit;
    }

    public int Getsize()
    {
        return cards.size();
    }

    // プレイヤーが持っているカード達の数字を取得
    public int[] GetNos()
    {
        int size = cards.size();
        int[] nos = new int[size];
        for(int i=0;i<size;i++){
            nos[i] = cards.get(i).no;
        }
        return nos;
    }

    // プレイヤーが持っているカードのマーク達を取得
    public int[] GetSuits()
    {
        int size = cards.size();
        int[] suits = new int[size];
        for(int i=0;i<size;i++){
            suits[i] = cards.get(i).suit;
        }
        return suits;
    }

    // プレイヤーが持っているカードをソート
    // 役の強さではなく単純な数字順
    public void SortCards()
    {
        double[] c = new double[cards.size()];
        int s = cards.size();
        for(int i=0;i<s;i++){
            c[i] = cards.get(i).no + (cards.get(i).suit * 0.1);
        }
        Arrays.sort(c);
        cards = new ArrayList<>();
        for(int i=0;i<s;i++){
            Card sc = new Card();
            sc.no = (int)c[i];
            sc.suit = (int)(c[i]*10)%10;
            cards.add(sc);
        }
    }
}
