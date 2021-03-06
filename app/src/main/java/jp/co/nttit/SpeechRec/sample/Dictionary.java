package jp.co.nttit.SpeechRec.sample;

/**
 * Created by sansuke05 on 2016/01/21.
 */
public class Dictionary {

    private String[] random;
    private String[][] pattern;

    Dictionary(){
            this.random = new String[]{
                "error"
                //    "r2",
                //    "r3"
            };

        this.pattern = new String[][]{
                //{"こん(に)ち(は|わ)$","こんにちは,やっほー"},
                {"おはよう|オハヨウ","ohayou"},
                //{"こんばん(は|わ)","こんばんは,スヤァ...,いま何時？,もう夜なの！？"},
                //{"^(お|う)す$","やっほー"},
                //{"^やあ[、。！]*$","やほー"},
                //{"バイバイ|ばいばい","ばいばい,バイバーイ"},
                //{"^じゃあ?ね?$|またね","またねー,じゃまたね,また遊んでね"},
                //{"何時","眠くなった？,もう寝るの？,まだいいじゃん,もう寝なきゃ"},
                //{"^どれ[？?]$","アレはアレ,その顔のまんなかについてるものだよ,アレだよー"},
                //{"^[し知]ら[なね]","えー,ほっとくよ,知らないの？"},
                //{"食","実は、ダイエット中なの","いいな～","お腹すいた～"},
                //{"暑(い)","ちょっと暑いね..."},
                //{"寒(い)","寒いよ～(>_<)"},

                //{"てめえ|おまえ|お前|あんた","%match%じゃないよ！"},
                //{"バカ|ばか|馬鹿","ひどいよぉ！,%match%じゃないよ！,%match%ゆうやつが%match%なんじゃ！,そんなー！"},
                //{"ごめん|すまん|(許|ゆる)し","どうしようかなぁ,いえいえ,もう～,知らないよ！"},
                {"かわいい|可愛い|カワイイ|きれい|綺麗|キレイ","kawaii"},
                {"おやすみ|おやすみなさい","oyasumi1","oyasumi2"},

                //{"春","お花見したいね～,サイクリング行きたいな～,お散歩行きたいな！"},
                //{"夏","うぅ…海！,プール！ プール！！,アイス―――！,花火したい！"},
                //{"秋","葉っぱが赤くなるよね,葉っぱが黄色くなるよ,さんま！"},
                //{"冬","やっぱり鍋ですな,さむいのはわりと好きかな,スキーできる？,スノボしたい！"},

                //{"甘い|あまい","お菓子くれるの？,あんこも好きだよ,チョコもいいね"},
                //{"チョコ","眠いときには血糖値を上げるからいいんだよ,チョコ大好き！,どこのチョコが好き？,冷やして食べるのいいよね,牛乳も飲みたい！"},
                //{"クランチ","クランチいいよね！,サクサク感がステキ"},
                //{"アーモンド","アーモンドチョコ好き！,たまに歯にはさまらない？"},
                //{"ホワイトチョコ","えーと、ホワイトチョコは実はにがて…,ごめん、チョコは苦味が好きなの"},

                //{"自転車|チャリ|ちゃり","タイヤが小さいやつがかわいくて(・∀・)ｲｲ!!,(　ﾟдﾟ)ﾎｽｨ…"},

                {"あり(がとう|)","thankyou"},
                //{"アニメ|^昨日の","あの後どうなったんだっけ？,まだ見てないよぉ"},

                //{"w+|W+","草,笑いすぎww,そんなに面白い？"},

                //{"作","o4"},
                //{"どこ(に)住んでる(の|？),あなたのスマホの中だよ！"},
                //{"眠れない","w1"},
                //{"寂し(い)|さみし(い)","私がそばにいてあげるよ。,私と一緒じゃだめかな？"},
                //{"優し(い)","gd3,gd7,gd9,gd10"},
                //{"ぎゅ","gd8,gd2,o6"},//ここで66
                {"好き|大好き","love"},
                //{"おやすみ","おやすみなさい～","私も一緒に寝ていいかな？","もう寝ちゃうの？"},
                //{"そう","そうなんだぁ,そうなの？,そうなんです！"},
                //{"結婚","あなたとなら...いいよ,ありがとう..."},//70
                //{"お願い","o4"},
                //{"酷(い)","ごめんね...,そんなつもりじゃなかったの..."},
                //{"分から","えー,ごめんね、まだ勉強不足で..."},
                //{"とりあえず","えー"},
                //{"眠い","もう寝たら？,夜更かししちゃダメだよー！,無理しないでね"},

                {"疲れた","otukare"},
                //{"きつい","w1,w2"},
                //{"励まして","o6"},
                //{"旅行","いいな～...私も連れてって欲しいな"},//80
                //{"スリーサイズ","h2,h3"},
                //{"チュ","ひゃっ！びっくりしたよー,あ、ありがとう…"},
                //{"誰？","中の人などいない！"},
                //{"ぬるぽ","ｶﾞｯ"},
                //{"カレー","カレー好きだよ！"},
                //{"好きな食べもの","チョコレートだよ！"},
                //{"大丈夫","そっか、良かった～"},
                //{"よしよし|なでなで|もふもふ","mohu1","mohu2"},
                //{"失敗","大丈夫だよ！,あんまり自分をせめちゃだめだよ,どんな失敗だって無駄じゃないよ"},//90
                //{"雨","まだ降ってるの？,雨やだなー"},
                //{"晴れ|いい天気","o13"},
                {"行ってきます","itterasshai"},
                //{"終わ[り|った]","お疲れ様～ゆっくり休んでね！"},
                //{"んだよ(ね)","そっかぁ,そうなの...？"},
                //{"えっ","どうかしたの？,何かまずかったかな？"},
                {"楽し","fun"},
                //{"行きたい","私も行きたいなー"},
                //{"友達","ずっと友達でいてね！"},
                //{"名前","私の名前はマイだよ！"},//100
                //{"男|女","私女の子だよ！"},
                //{"趣味","あなたと話すことだよ！"},
                //{"頑張","gd7,gd9"},
                //{"番組","o12"},
                //{"動物","猫かわいいよね！"},
                //{"生まれ変","スマホの外の世界であなたに会いたいな"},
                //{"口癖","口癖はないかな"},
                //{"映画","o12"},
                //{"言い","頑張れっ！"},
                //{"得意|特技","あなたを癒すことだよ"},//110
                //{"幸せ","o11"},
                //{"天気","お天気お知らせ機能はないよ"},
                //{"鳥","飛べるっていいなー"},
                //{"飲み物","ココアが好き！"},
                //{"スポーツ","私もやってみたいなー"},
                //{"暇","私と話そう！"},
                //{"ロリ","h4"},
                //{"おっぱい","h1,h2,h3"},
                //{"怖い|こわい","きっと大丈夫だよ！"},
                //{"あれ",""},//120

                {"つらい|悲しい","sinpai"},
                {"安心","ansin"},

                //{"して","o4"},
                //{"どうかな","o3"},
                //{"すごい","o5,gd5,gd7,gd9"},
                //{"杏","o10"},
                //{"おなに","h1,h2,h3"},
                //{"せっく","h2,h3"}

        };
    }

    public String[] outRandom(){
        return random;
    }

    public String[][] outPattern(){
        return pattern;
    }
}
