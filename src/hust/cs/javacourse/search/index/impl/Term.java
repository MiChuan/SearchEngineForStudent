package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractTerm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Term extends AbstractTerm {
    /**
     * 缺省构造函数
     */
    public Term() {
    }

    /**
     * 构造函数
     *
     * @param content ：Term内容
     */
    public Term(String content) {
        super(content);
    }

    /**
     * 判断二个Term内容是否相同
     * @param obj ：要比较的另外一个Term
     * @return 如果内容相等返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Term){
            return this.content == ((Term)obj).content;
        }
        return false;
    }

    /**
     * 返回Term的字符串表示
     * @return 字符串
     */
    @Override
    public String toString() {
        return "Term: " + this.content + " ";
    }

    /**
     * 返回Term内容
     * @return Term内容
     */
    @Override
    public String getContent() {
        return this.content;
    }

    /**
     * 设置Term内容
     * @param content：Term的内容
     */
    @Override
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 比较二个Term大小（按字典序）
     * @param o： 要比较的Term对象
     * @return ： 返回二个Term对象的字典序差值
     */
    @Override
    public int compareTo(AbstractTerm o) {
        int len1 = this.content.length();
        int len2 = ((Term)o).content.length();
        //取数组长度里面最小的
        int lim = Math.min(len1,len2);
        // 获得两个数组，这两个数组就是string的属性

        int k = 0;
        while (k < lim) {
            //获取第K的字符，进行比较
            char c1 = this.content.charAt(k);
            char c2 = ((Term)o).content.charAt(k);
            if (c1 != c2) {
                //Java使用的是Unicode编码，因此返回这两个字符的Unicode差值。
                return c1 - c2;
            }
            k++;
        }

        //如果前lim个字符都相同，那么就返回长度差。
        return len1 - len2;
    }

    /**
     * 写到二进制文件
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) throws IOException {
        try{
            out.writeBytes(this.content);
        } catch (IOException ex){
            throw ex;
        }
    }

    /**
     * 从二进制文件读
     * @param in ：输入流对象
     */
    @Override
    public void readObject(ObjectInputStream in) throws IOException {
        try{
            this.content = in.readUTF();
        }
         catch (IOException ex){
            throw ex;
         }
    }
}
