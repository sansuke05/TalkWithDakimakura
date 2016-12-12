package jp.co.nttit.SpeechRec.sample;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Responder {
    private String name;
    Random rnd = new Random();
    Dictionary dictionary = new Dictionary();

    Responder(String name){
        this.name = name;
    }

    String response(String input){
        return "";
    }

    public String getName(){
        return this.name;
    }
}

class RandomResponder extends Responder{

    private String[] random = dictionary.outRandom();

    RandomResponder(String name){
        super(name);
    }

    @Override
    String response(String input){
        return random[rnd.nextInt(random.length)];
    }
}

class PatternResponder extends Responder{

    private int i;
    private String[][] pattern = dictionary.outPattern();
    private String[] random = dictionary.outRandom(),temp;
    private String resp;
    private Pattern p;
    private Matcher m;

    PatternResponder(String name){
        super(name);
    }

    @Override
    String response(String input){

        for (i=0;i<pattern.length;i++){
            p = Pattern.compile(pattern[i][0]);
            m = p.matcher(input);
            if(m.find()){
                temp = pattern[i][1].split(",",0);
                resp = temp[rnd.nextInt(temp.length)];
                return resp.replaceAll("%match%", m.group());
            }
        }

        return random[rnd.nextInt(random.length)];
    }
}
