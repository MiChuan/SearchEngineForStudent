package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.AbstractTermTuple;

public class TermTuple extends AbstractTermTuple {
    public TermTuple() {
    }

    /**
     * 使用term和curPos来构造TermTuple
     * @param term ：单词
     * @param curPos ：位置
     */
    public TermTuple(AbstractTerm term, int curPos){
        this.term = term;
        this.curPos = curPos;
    }

    /**
     * 使用String类型代替Term，更便于使用
     * @param content ：单词内容
     * @param curPos ：位置
     */
    public TermTuple(String content, int curPos){
        this.term = new Term();
        this.term.setContent(content);
        this.curPos = curPos;
    }

    /**
     * 判断二个三元组内容是否相同
     *
     * @param obj ：要比较的另外一个三元组
     * @return 如果内容相等（三个属性内容都相等）返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TermTuple){
            return this.curPos == ((TermTuple)obj).curPos &&
                    this.freq == ((TermTuple)obj).freq &&
                    this.term == ((TermTuple)obj).term;
        }
        return false;
    }

    /**
     * 获得三元组的字符串表示
     *
     * @return ： 三元组的字符串表示
     */
    @Override
    public String toString() {
        return "(" + this.term.toString() + "freq: " + this.freq + "curPos: " + this.curPos + ")";
    }
}
