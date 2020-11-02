package algorithm.doublepointer;

import java.util.Arrays;

/**
 * @Description:归并两个有序数组 Leetcode 88 E
 * @Author: GuoChangYu
 * @Date: Created in 12:38 2020/11/2
 **/
public class MergeSortedArray {
    public static void main(String[] args) {
        int[] nums1 = new int[]{1, 2, 3, 0, 0, 0};
        int[] nums2 = new int[]{2, 5, 6};
        int m = 3;
        int n = 3;

        System.out.println("result:"+ Arrays.toString(merge(nums1,m,nums2,n)));

    }

    /**
     * 从
     * @param nums1
     * @param m
     * @param nums2
     * @param n
     * @return
     */
    public static int[] merge(int[] nums1, int m, int[] nums2, int n) {
        int index1 = m - 1;
        int index2 = n - 1;
        int mergeIndex = m + n - 1;
        while (index1 >= 0 || index2 >= 0) {
            if (index1 < 0) {
                nums1[mergeIndex--] = nums2[index2--];
            } else if (index2 < 0) {
                nums1[mergeIndex--] = nums1[index1--];
            } else if (nums1[index1] > nums2[index2]) {
                nums1[mergeIndex--] = nums1[index1--];
            }else {
                nums1[mergeIndex--] = nums2[index2--];
            }
        }
        return nums1;
    }


}
