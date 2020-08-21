import java.util.*;
 class Main {
     private static final int P = 1000000000+7;
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        long a,b,c,d;
        a = in.nextLong();
        b = in.nextLong();
        c = in.nextLong();
        d = in.nextLong();
        if(find(find(a,b),find(d,c))!=-1){
            System.out.println(find(find(a,b),find(c,d))*4);
        }else if(find(find(a,c),find(b,d))!=-1){
            System.out.println(find(find(a,c),find(b,d))*4);
        }else if(find(find(a,d),find(b,c))!=-1){
            System.out.println(find(find(a,d),find(b,c))*4);
        }else {
            System.out.println(-1);
        }
    }
    public static long find(long a,long b){
        if(a==-1 || b==-1){
            return -1;
        }
        if(a>b){
            long x=a;
            a=b;
            b=x;
        }

        if((b-a)%3==0){
            a=a+(b-a)/3;
            return a;
        }else if((b-a)%3==1){
            a=a+(b-a)/3;
            return  a;
        }else{
            return -1;
        }
        Collections.sort();
    }
}