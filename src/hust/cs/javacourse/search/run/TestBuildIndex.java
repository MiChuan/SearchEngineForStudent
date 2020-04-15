package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.index.impl.DocumentBuilder;
import hust.cs.javacourse.search.index.impl.Index;
import hust.cs.javacourse.search.index.impl.IndexBuilder;
import hust.cs.javacourse.search.util.Config;

import java.io.File;
import java.util.Scanner;

/**
 * 测试索引构建
 */
public class TestBuildIndex {
    /**
     *  索引构建程序入口
     * @param args : 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("创建倒排索引，创建模式：");
        System.out.println("1. 从文本文档目录读取文档内容进行创建");
        System.out.println("2. 从已有的序列化索引文件反序列化进行创建");
        System.out.print("请输入数字：");
        Scanner scan = new Scanner(System.in);
        int opt = scan.nextInt();
        AbstractIndex index;
        switch (opt) {
            case 1:
                AbstractIndexBuilder indexBuilder = new IndexBuilder(new DocumentBuilder());
                index = indexBuilder.buildIndex(Config.DOC_DIR);
                if (index.getDictionary().isEmpty()){
                    System.out.println("Warning: 索引表为空！");
                }
                System.out.println("文档目录：");
                System.out.println(Config.DOC_DIR);
                System.out.println("倒排索引内容：");
                System.out.println(index.toString());
                break;
            case 2:
                index = new Index();
                index.load(new File(Config.INDEX_DIR + "index.dat"));
                System.out.println("倒排索引内容：");
                System.out.println(index.toString());
                break;
            default:
                System.out.println("输入格式错误!");
                break;
        }
    }
}
