package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.BufferedReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TermTupleScanner extends AbstractTermTupleScanner {
    private Queue<AbstractTermTuple> tuples = new LinkedList<>();
    private int pos = 1;
    private StringSplitter splitter ;
    public TermTupleScanner(BufferedReader bufferedReader){
        super(bufferedReader);
        splitter = new StringSplitter();
        splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
    }

    @Override
    public AbstractTermTuple next() {

        //惰性解析，当容器里面没有的时候再解析
        if(tuples.size() == 0 ){
            try{
                String t = input.readLine();
                while(t != null && t.equals("")){//略过空行,比较字符串是否相同要用equals
                    t = input.readLine();
                }
                if(t != null){
                    List<String> words = splitter.splitByRegex(t);
                    for(String s:words){
                        AbstractTermTuple tuple = new TermTuple();
                        tuple.curPos = pos++;
                        tuple.term = new Term();
                        tuple.term.setContent(s);
                        tuples.add(tuple);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return tuples.poll();
    }
}
