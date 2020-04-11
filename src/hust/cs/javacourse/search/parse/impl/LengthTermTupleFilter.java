package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.Config;

public class LengthTermTupleFilter extends AbstractTermTupleFilter {
    /**
     * 构造函数
     * @param input : 输入文件流
     * */
    public LengthTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
    }

    /**
     * 获得下一个三元组
     * 过滤长度小于3或大于20的单词
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        AbstractTermTuple lengthFilter = input.next();
        if(lengthFilter == null) return null;
        //长度小于3或大于20的被过滤
        while (lengthFilter.term.getContent().length()< Config.TERM_FILTER_MINLENGTH ||
                lengthFilter.term.getContent().length()>Config.TERM_FILTER_MAXLENGTH){
            lengthFilter = input.next();
            if(lengthFilter==null) return null;
        }
        return lengthFilter;
    }
}
