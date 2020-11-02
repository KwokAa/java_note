package algorithm.doublepointer;

/**
 * @Description: 最长子序列 Leetcode 524 M
 * @Author: GuoChangYu
 * @Date: Created in 14:18 2020/11/2
 **/
public class LongestWord {
    public static void main(String[] args) {
        String s = "abpcplea";
        String[] d = new String[]{"ale", "apple", "monkey", "plea","abpcp"};
        System.out.println(findLongestWord(d, s));

//        System.out.println("a".compareTo("b"));  //-1
//        System.out.println("a".compareTo("c"));  //-2
//        System.out.println("b".compareTo("b"));   //0
//        System.out.println("b".compareTo("a"));   //1
//        System.out.println("ab".compareTo("abc")); //-1
//        System.out.println("abcd".compareTo("abc"));  //1
//        System.out.println("abcdefd".compareTo("abc"));  //4
//        System.out.println("abcdeFd".compareTo("abc"));  //4
//        System.out.println("abC".compareTo("abc"));      //-32
//        System.out.println("ab".compareTo("abcdeerrw")); //-7
//        System.out.println("A".compareTo("a")); // -32
    }


    /**
     * 双指针 判断target是否为s的子串
     * @param s
     * @param target
     * @return
     */
    public static boolean isSubStr(String s,String target){
        int i = 0;
        int j = 0;
        while (i < s.length() && j < target.length()) {
            if (s.charAt(i) == target.charAt(j)) {
                j++;
            }
            i++;
        }
        return j == target.length();
    }

    /**
     * 找出最长子序列
     * @param d
     * @param s
     * @return
     */
    public static String findLongestWord(String[] d,String s) {
        String longestWord = "";
        for (String target : d) {
            int l1 = longestWord.length();
            int l2 = target.length();
            //l1 == l2 && longestWord.compareTo(target) < 0 如果长度相同就选择字典序最小的
            if (l1 > l2 || (l1 == l2 && longestWord.compareTo(target) < 0)) {
                continue;
            }
            if (isSubStr(s, target)) {
                longestWord = target;
            }
        }
        return longestWord;
    }



}
