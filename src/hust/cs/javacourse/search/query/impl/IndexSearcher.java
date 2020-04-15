package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.impl.Posting;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexSearcher extends AbstractIndexSearcher {
    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     *
     * @param indexFile ：指定索引文件
     */
    @Override
    public void open(String indexFile) {
        this.index.load(new File(indexFile));
        this.index.optimize();
    }

    /**
     * 根据单个检索词进行搜索
     *
     * @param queryTerm ：检索词
     * @param sorter    ：排序器
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {
        AbstractPostingList postList = index.search(queryTerm);
        if(postList == null) return null;       // 没搜到任何文档
        List<AbstractHit> hitArray = new ArrayList<AbstractHit>();
        for(int i=0;i<postList.size();i++){     // 遍历查询单词的倒排索引表
            AbstractPosting post = postList.get(i);
            String path = index.getDocName(post.getDocId());
            Map<AbstractTerm, AbstractPosting> map = new HashMap<AbstractTerm, AbstractPosting>();
            map.put(queryTerm, post);
            AbstractHit h = new Hit(post.getDocId(), path, map);
            h.setScore(sorter.score(h));        // 设置分数
            hitArray.add(h);
        }
        sorter.sort(hitArray);      // 对查询结果排序
        return (AbstractHit[]) hitArray.toArray(new AbstractHit[0]);
    }

    /**
     * 根据二个检索词进行搜索
     *
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter     ：    排序器
     * @param combine    ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, LogicalCombination combine) {
        AbstractPostingList postList1 = index.search(queryTerm1);
        AbstractPostingList postList2 = index.search(queryTerm2);
        List<AbstractHit> hitArray = new ArrayList<AbstractHit>();
        int i=0, j=0;
        int size1 = 0, size2 = 0;
        // 判断是否搜索到文档
        if(postList1 != null) size1 = postList1.size();
        if(postList2 != null) size2 = postList2.size();
        while(i < size1 && j < size2) {
            AbstractPosting post1 = postList1.get(i);
            AbstractPosting post2 = postList2.get(j);
            //两个搜索词的当前文档是同一份
            if(post1.getDocId() == post2.getDocId()) {
                String path = index.getDocName(post1.getDocId());
                Map<AbstractTerm, AbstractPosting> map =
                        new HashMap<AbstractTerm, AbstractPosting>();
                // 两个单词都放入映射表中
                map.put(queryTerm1, post1);
                map.put(queryTerm2, post2);
                AbstractHit h = new Hit(post1.getDocId(), path, map);
                h.setScore(sorter.score(h));        // 设置分数
                hitArray.add(h);
                i++;    j++;
            } else if(post1.getDocId() < post2.getDocId()) {
                if(combine == LogicalCombination.OR) {
                    String path = index.getDocName(post1.getDocId());
                    Map<AbstractTerm, AbstractPosting> map =
                            new HashMap<AbstractTerm, AbstractPosting>();
                    map.put(queryTerm1, post1);
                    AbstractHit h = new Hit(post1.getDocId(), path, map);
                    h.setScore(sorter.score(h));        // 设置分数
                    hitArray.add(h);
                }
                i++;
            } else {        // post1 > post2
                if(combine == LogicalCombination.OR) {
                    String path = index.getDocName(post2.getDocId());
                    Map<AbstractTerm, AbstractPosting> mp =
                            new HashMap<AbstractTerm, AbstractPosting>();
                    mp.put(queryTerm2, post2);
                    AbstractHit h = new Hit(post2.getDocId(), path, mp);
                    h.setScore(sorter.score(h));        // 设置分数
                    hitArray.add(h);
                }
                j++;
            }
        }
        if(combine == LogicalCombination.OR) {  // 读取剩余的一部分命中结果
            while(i < size1){
                AbstractPosting post1 = postList1.get(i);
                String path = index.getDocName(post1.getDocId());
                Map<AbstractTerm, AbstractPosting> mp =
                        new HashMap<AbstractTerm, AbstractPosting>();
                mp.put(queryTerm1, post1);
                AbstractHit h = new Hit(post1.getDocId(), path, mp);
                h.setScore(sorter.score(h));        // 设置分数
                hitArray.add(h);
                i++;
            }
            while(j < size2){
                AbstractPosting post2 = postList2.get(i);
                String path = index.getDocName(post2.getDocId());
                Map<AbstractTerm, AbstractPosting> mp =
                        new HashMap<AbstractTerm, AbstractPosting>();
                mp.put(queryTerm2, post2);
                AbstractHit h = new Hit(post2.getDocId(), path, mp);
                h.setScore(sorter.score(h));        // 设置分数
                hitArray.add(h);
                j++;
            }
        }
        new FreqSorter().sort(hitArray);
        return (AbstractHit[]) hitArray.toArray(new Hit[0]);
    }

    /**
     * 查询两个在文中相邻出现的单词（进阶功能）
     * @param queryTerm1 ：第一个单词
     * @param queryTerm2 ：第二个单词
     * @param sorter ：排序器
     * @return ：查询结果数组
     */
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter) {
        AbstractPostingList postList1 = index.search(queryTerm1);
        AbstractPostingList postList2 = index.search(queryTerm2);
        if(postList1 == null || postList2 == null) return null;
        List<AbstractHit> hitArray = new ArrayList<AbstractHit>();
        int i=0, j=0;
        while(i < postList1.size() && j < postList2.size()) {
            AbstractPosting post1 = postList1.get(i);
            AbstractPosting post2 = postList2.get(j);
            // 这里默认索引中的数据都是按文档ID从小到大排序了
            if (post1.getDocId() == post2.getDocId()) {
                List<Integer> pos1 = post1.getPositions();
                List<Integer> pos2 = post2.getPositions();
                int a = 0, b = 0;
                List<Integer> positions = new ArrayList<Integer>();     // 存放连续两个单词出现的位置
                while(a < pos1.size() && b < pos2.size()){
                    int p1 = pos1.get(a);
                    int p2 = pos2.get(b);
                    if(p1 == p2-1){
                        positions.add(p1);
                        a++;    b++;
                    } else if(p1 < p2-1){
                        a++;
                    } else {
                        b++;
                    }
                }
                if(positions.size() > 0) {      // 否则会出现score = 0.0的情况
                    String path = index.getDocName(post1.getDocId());
                    Map<AbstractTerm, AbstractPosting> mp =
                            new HashMap<AbstractTerm, AbstractPosting>();
                    // 把两个单词合并放入映射表中，中间用一个空格隔开，位置则使用两个单词中第一个单词所在的位置
                    // 这样在高亮显示的时候就可以了（要注意把空格替换成\\s，以避免原文中相邻单词直接有多个空格的情况）
                    mp.put(new Term(queryTerm1.getContent() + " " + queryTerm2.getContent()),
                            new Posting(post1.getDocId(), positions.size(), positions));
                    AbstractHit h = new Hit(post1.getDocId(), path, mp);
                    h.setScore(sorter.score(h));        // 先设置分数
                    hitArray.add(h);
                }
                i++;    j++;
            } else if (post1.getDocId() < post2.getDocId()) {
                i++;
            } else {        // post1 > post2
                j++;
            }
        }
        if(hitArray.size() < 1) return null;
        new FreqSorter().sort(hitArray);
        return (AbstractHit[]) hitArray.toArray(new Hit[0]);
    }
}

