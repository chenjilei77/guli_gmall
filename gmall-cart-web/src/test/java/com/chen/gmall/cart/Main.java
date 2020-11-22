package com.chen.gmall.cart;

import java.util.ArrayList;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int m = in.nextInt();
        int[] result=new int[n+1];
        long temp=0;
        ArrayList<ArrayList<Integer>> data = new ArrayList<>();
        int u,v,a,b;
        while(temp<=m){
            ArrayList list = new ArrayList();
            u = in.nextInt();
            v = in.nextInt();
            a = in.nextInt();
            b = in.nextInt();
            int num=help(a,b);
            list.add(num);
            list.add(u);
            list.add(v);
            data.add(list);
        }
        sort(data,0,data.size()-1);
        for(int i=m-1;i>=0;i--){
            if(result[data.get(i).get(1)]==0 || result[data.get(i).get(2)]==0){
                result[data.get(i).get(1)]+=data.get(i).get(0);
                result[data.get(i).get(2)]+=data.get(i).get(0);
            }
        }
        int sum=0;
        for(int i=0;i<m;i++){
            sum+=1;
        }
    }

    private static int help(int a, int b) {
        int result=1;
        for(int i=b+1;i<=a;i++){
            result*=i;
        }
        return result;
    }

    public static void sort(ArrayList<ArrayList<Integer>> data,int left,int right){
        if(left>=right)
            return;

        int i=left;
        int j=right;
        ArrayList<Integer> temp=data.get(left);
        while(i<j){
            while(i<j && data.get(j).get(0)<=temp.get(0))
                j--;
            while(i<j && data.get(i).get(0)>=temp.get(0))
                i++;
            ArrayList<Integer> x=data.get(i);
            data.set(i,data.get(j));
            data.set(j,x);

        }
        data.set(left,data.get(i));
        data.set(i,temp);
        sort(data,left,i-1);
        sort(data,left+1,right);
    }
}
//import java.util.*;
//public class Main {
//    public static void main(String[] args) {
//        Scanner in = new Scanner(System.in);
//        int t=in.nextInt();
//        int temp=0;
//        while(temp<t){
//            int n=in.nextInt();
//            int m=in.nextInt();
//            if(n%2==0 || m%2==0){
//                System.out.println(2);
//            }else {
//                for(int i=3;i<n*m;i++){
//                    if(n%i==0 || m%i==0){
//                        System.out.println(i);
//                        break;
//                    }
//                }
//            }
//            temp++;
//        }
//    }
//}