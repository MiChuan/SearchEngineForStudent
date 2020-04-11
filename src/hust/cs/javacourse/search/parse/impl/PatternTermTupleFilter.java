package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.Config;

public class PatternTermTupleFilter extends AbstractTermTupleFilter {
    private String pattern;

    /**
     * 构造函数
     * @param input : 输入流
     * */
    public PatternTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
        pattern = Config.TERM_FILTER_PATTERN;
    }

    /**
     * 获得下一个三元组
     * 基于正则表达式，过滤非英文字符
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        AbstractTermTuple patternFilter = input.next();
        if(patternFilter==null) return null;
        while (!patternFilter.term.getContent().matches(pattern)){//不匹配，含有非英文字符
            patternFilter = input.next();
            if(patternFilter==null) return null;
        }
        return patternFilter;
    }
}
