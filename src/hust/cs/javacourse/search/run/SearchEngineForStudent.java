package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.index.impl.DocumentBuilder;
import hust.cs.javacourse.search.index.impl.Index;
import hust.cs.javacourse.search.index.impl.IndexBuilder;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.impl.FreqSorter;
import hust.cs.javacourse.search.query.impl.IndexSearcher;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StopWords;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SearchEngineForStudent {
    public static void main(String[] args) throws IOException{
        System.out.println("基于英文文档的搜索引擎");
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
        IndexSearcher searcher = new IndexSearcher();
        searcher.open(Config.INDEX_DIR + "index.dat");
        FreqSorter freqSorter = new FreqSorter();
        // 查询一个单词
        String req;
        System.out.println("倒排索引查询，输入格式：");
        System.out.println("1. oneWord");
        System.out.println("2. word combine word");
        System.out.println("combine :\n\tor: +,|\n\tand: &,*");
        System.out.println("3. firstWord secondWord");
        System.out.println("4. 输入quitSearch退出查询");
        System.out.print("请输入需要查询的单词: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while((req = br.readLine()) != null && !req.equals("quitSearch")){
            String[] reqs = req.split("[\\s]+");   // 用空白符切分输入的这行
            List<String> stopWords = new ArrayList<String>(Arrays.asList(StopWords.STOP_WORDS));
            for(String s : reqs){
                if(stopWords.contains(s))
                    System.out.println("\033[31mWarning: 停用词: " + s + "\033[0m");
            }
            if(reqs.length < 1){
                System.out.println("请至少输入一个单词");
            } else if(reqs.length == 1){

                AbstractHit[] hits = searcher.search(new Term(req), freqSorter);
                if(hits == null) System.out.println("未搜索到任何结果");
                else for(AbstractHit h: hits)
                    System.out.println(h.toString());
                System.out.print("请输入需要查询的单词: ");

            } else if(reqs.length == 2){        // 查询两个在文中相邻的单词

                AbstractHit[] hits = searcher.search(new Term(reqs[0]), new Term(reqs[1]), freqSorter);
                if(hits == null) System.out.println("未搜索到任何结果");
                else for(AbstractHit h: hits)
                    System.out.println(h.toString());
                System.out.print("请输入需要查询的单词: ");

            } else if(reqs.length == 3){        // 查询两个单词，中间用&表示“与”，|表示或

                if(reqs[1].equals("&") || reqs[1].equals("*")){     // 与
                    AbstractHit[] hits = searcher.search(new Term(reqs[0]), new Term(reqs[2]),
                            freqSorter, AbstractIndexSearcher.LogicalCombination.ADN);
                    if(hits == null || hits.length < 1) System.out.println("未搜索到任何结果");
                    else for(AbstractHit h: hits)
                        System.out.println(h.toString());

                } else if(reqs[1].equals("|") || reqs[1].equals("+")){  // 或
                    AbstractHit[] hits = searcher.search(new Term(reqs[0]), new Term(reqs[2]),
                            freqSorter, AbstractIndexSearcher.LogicalCombination.OR);
                    if(hits == null || hits.length < 1) System.out.println("未搜索到任何结果");
                    else for(AbstractHit h: hits)
                        System.out.println(h.toString());

                } else {
                    System.out.println("\033[31m逻辑关系解析失败\033[0m");
                    System.out.println("输入格式： word combine word");
                    System.out.println("combine :   or: +,|  and: &,*");
                }
                System.out.print("请输入需要查询的单词: ");

            } else {
                System.out.println("输入单词数过多");
                System.out.print("请输入需要查询的单词: ");
            }
        }
    }
}
