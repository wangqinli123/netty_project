package com.itheima.netty.buffer;

import java.nio.IntBuffer;

public class TestIntBuffer {

    public static void main(String[] args) {

        //分配了8个长度的int数组
        IntBuffer buffer = IntBuffer.allocate(80);

        //buffer.capacity():获取buffer的容量
        for (int i=0;i<buffer.capacity();i++){
            int j = 2 * (i + 1);
            buffer.put(j);
        }

        // 重设此缓冲区，将限制设置为当前位置，然后将当前位置设置为0
        //固定缓冲区中的某些值，告诉缓冲区，我要开始操作了，如果你再往缓冲区写数据的话不要再覆盖我固定状态以前的数据了
        buffer.flip();

        // 查看在当前位置和限制位置之间是否有元素
        while (buffer.hasRemaining()){
            int i = buffer.get();
            System.out.println(i + " ");
        }
    }
}
