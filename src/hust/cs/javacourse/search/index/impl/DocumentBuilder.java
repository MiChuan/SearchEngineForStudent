package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocument;
import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.parse.impl.*;

import java.io.*;

public class DocumentBuilder extends AbstractDocumentBuilder {
    /**
     * <pre>
     * 由解析文本文档得到的TermTupleStream,构造Document对象.
     * @param docId             : 文档id
     * @param docPath           : 文档绝对路径
     * @param termTupleStream   : 文档对应的TermTupleStream
     * @return ：Document对象
     * </pre>
     */
    @Override
    public AbstractDocument build(int docId, String docPath, AbstractTermTupleStream termTupleStream) {
        AbstractDocument document = new Document(docId,docPath);
        AbstractTermTuple termTuple = termTupleStream.next();
        while (termTuple!=null){
            document.addTuple(termTuple);
            termTuple = termTupleStream.next();
        }
        return document;
    }

    /**
     * <pre>
     * 由给定的File,构造Document对象.
     * 该方法利用输入参数file构造出AbstractTermTupleStream子类对象后,内部调用
     *      AbstractDocument build(int docId, String docPath, AbstractTermTupleStream termTupleStream)
     * @param docId     : 文档id
     * @param docPath   : 文档绝对路径
     * @param file      : 文档对应File对象
     * @return          : Document对象
     * </pre>
     */
    @Override
    public AbstractDocument build(int docId, String docPath, File file) {
        AbstractTermTupleStream termTupleStream = null;
        try{
            termTupleStream =
                    new LengthTermTupleFilter(new PatternTermTupleFilter(new StopWordTermTupleFilter(
                    new SimpleScanner(new BufferedReader(new InputStreamReader(new FileInputStream(file)))))));
        } catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
        return this.build(docId,docPath,termTupleStream);
    }
}
