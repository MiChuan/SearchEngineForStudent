package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;

public class SimpleScanner extends AbstractTermTupleScanner {
    private int curPos; //单词当前位置
    private LinkedList<String> stringBuff; //保存读取的一行字符的缓冲区
    StringSplitter split = new StringSplitter();
    /**
     * 缺省构造函数
     */
    public SimpleScanner() {
        this.curPos = 0;
        this.stringBuff = new LinkedList<String>();
        split.setSplitRegex(Config.STRING_SPLITTER_REGEX);
    }

    /**
     * 构造函数
     *
     * @param input ：指定输入流对象，应该关联到一个文本文件
     */
    public SimpleScanner(BufferedReader input) {
        super(input);
        this.curPos = 0;
        this.stringBuff = new LinkedList<String>();
        split.setSplitRegex(Config.STRING_SPLITTER_REGEX);
    }

    /**
     * 获得下一个三元组
     *
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        try{
            if(stringBuff.isEmpty()){
                String str = this.input.readLine();//读取一行字符
                while(str != null && str.equals("")){//略过空行,比较字符串是否相同要用equals
                    str = input.readLine();
                }
                if(str == null){//读到输入流结束
                    return null;
                }
                stringBuff = new LinkedList<String>(split.splitByRegex(str));
                if(stringBuff.isEmpty()){
                    return null;
                }
            }
            String elem = stringBuff.getFirst();
            stringBuff.removeFirst();
            return new TermTuple(elem, ++this.curPos);
        } catch (IOException ex){
            ex.printStackTrace();
        }
        return null;
    }
}
